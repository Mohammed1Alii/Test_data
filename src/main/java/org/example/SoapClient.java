package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic SOAP-over-HTTP client. Knows nothing about any specific API:
 * you give it an endpoint, a request XML (or a template + values), and it returns the response XML.
 */
public class SoapClient {

    private final String endpointUrl;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private int connectTimeoutMs = 15_000;
    private int readTimeoutMs = 120_000;

    public SoapClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
        this.headers.put("Content-Type", "text/xml; charset=utf-8");
    }

    // ---------- Configuration (chainable) ----------

    public SoapClient header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public SoapClient soapAction(String action) {
        return header("SOAPAction", "\"" + action + "\"");
    }

    public SoapClient timeouts(int connectMs, int readMs) {
        this.connectTimeoutMs = connectMs;
        this.readTimeoutMs = readMs;
        return this;
    }

    // ---------- Sending ----------

    /** Sends a complete SOAP request and returns the raw response XML. */
    public String send(String requestXml) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpointUrl).openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(connectTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            for (Map.Entry<String, String> h : headers.entrySet()) {
                conn.setRequestProperty(h.getKey(), h.getValue());
            }

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestXml.getBytes(StandardCharsets.UTF_8)); // UTF-8 safe (Arabic etc.)
            }

            int status = conn.getResponseCode();
            InputStream in = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
            String body = (in == null) ? "" : readFully(in);

            if (status >= 400) {
                // SOAP faults normally arrive as HTTP 500 with the fault in the body
                throw new SoapCallException(status, body);
            }
            return body;
        } finally {
            conn.disconnect();
        }
    }

    /** Parses a template, sets element values by tag name, sends it, returns the response XML. */
    public String send(String templateXml, Map<String, String> values) throws Exception {
        Document doc = parse(templateXml);
        setValues(doc, values);
        return send(toXml(doc));
    }

    // ---------- XML helpers (static, reusable anywhere) ----------

    public static Document parse(String xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // blocks XXE
        DocumentBuilder b = f.newDocumentBuilder();
        String clean = xml.replace("\uFEFF", "").trim();
        return b.parse(new InputSource(new StringReader(clean)));
    }

    /** Sets the text of the first element with this local name (any namespace). */
    public static void setValue(Document doc, String tagName, String value) {
        Node node = firstElement(doc, tagName);
        if (node == null) {
            throw new IllegalArgumentException("Element not found in request: " + tagName);
        }
        node.setTextContent(value);
    }

    public static void setValues(Document doc, Map<String, String> values) {
        for (Map.Entry<String, String> e : values.entrySet()) {
            setValue(doc, e.getKey(), e.getValue());
        }
    }

    /** Replaces the first element named tagName with the given XML fragment (e.g. a whole <WorkOrderInfo>...). */
    public static void replaceElement(Document doc, String tagName, String fragmentXml) throws Exception {
        Node old = firstElement(doc, tagName);
        if (old == null) {
            throw new IllegalArgumentException("Element not found in request: " + tagName);
        }
        Document fragDoc = parse(fragmentXml);
        Node imported = doc.importNode(fragDoc.getDocumentElement(), true);
        old.getParentNode().replaceChild(imported, old);
    }

    /** Returns the text of the first element with this name in a response, or null if absent. */
    public static String extractValue(String xml, String tagName) throws Exception {
        Node node = firstElement(parse(xml), tagName);
        return node == null ? null : node.getTextContent();
    }

    /** Returns the text of every element with this name (useful for repeated results). */
    public static List<String> extractValues(String xml, String tagName) throws Exception {
        NodeList nodes = parse(xml).getElementsByTagNameNS("*", tagName);
        List<String> out = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            out.add(nodes.item(i).getTextContent());
        }
        return out;
    }

    /** Returns the first element with this name, serialized as an XML string (no declaration). */
    public static String extractElement(String xml, String tagName) throws Exception {
        Node node = firstElement(parse(xml), tagName);
        if (node == null) {
            throw new IllegalArgumentException("Element not found: " + tagName);
        }
        return toXml(node, false, true);
    }

    public static String toXml(Node node) throws Exception {
        return toXml(node, true, false);
    }

    public static String toXml(Node node, boolean indent, boolean omitDeclaration) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitDeclaration ? "yes" : "no");
        StringWriter w = new StringWriter();
        t.transform(new DOMSource(node), new StreamResult(w));
        return w.toString();
    }

    // ---------- Internals ----------

    private static Element firstElement(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagNameNS("*", tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }

    private static String readFully(InputStream in) throws IOException {
        try (InputStream is = in; ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) {
                buf.write(chunk, 0, n);
            }
            return new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    /** Thrown when the server answers with HTTP 4xx/5xx. The body usually contains the SOAP fault. */
    public static class SoapCallException extends IOException {
        private final int status;
        private final String responseBody;

        public SoapCallException(int status, String responseBody) {
            super("HTTP " + status + ": " + responseBody);
            this.status = status;
            this.responseBody = responseBody;
        }

        public int getStatus() { return status; }
        public String getResponseBody() { return responseBody; }
    }
}

