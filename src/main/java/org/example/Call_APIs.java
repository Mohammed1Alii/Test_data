package org.example;

import java.util.Map;

/**
 * Reopen API: only holds what is specific to this API (URL, template, which fields to set).
 * All the HTTP/XML work lives in SoapClient.
 */
public class Call_APIs {

    private static final String URL =
            "http://10.19.35.91:8003/CALLS_WS-Project1-context-root/CALLS_WSPort";

    private static final String AAASession_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:FindSessionByUserNameAAA>
                     <!--Optional:-->
                     <key>Customer360uat</key>
                     <!--Optional:-->
                     <userName>?</userName>
                  </cal:FindSessionByUserNameAAA>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String SessionLog_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:GetSessionlogsSAList>
                     <!--Optional:-->
                     <key>Customer360uat</key>
                     <!--Optional:-->
                     <startDate>10-02-2026</startDate>
                     <!--Optional:-->
                     <userName>?</userName>
                     <!--Optional:-->
                     <nasportid>?</nasportid>
                  </cal:GetSessionlogsSAList>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String FTTH_Path_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:Get_FTTH_Path>
                     <!--Optional:-->
                     <Exch_Code>MBKGZ</Exch_Code>
                     <!--Optional:-->
                     <Passive_Cabinet_ID>?</Passive_Cabinet_ID>
                     <!--Optional:-->
                     <BOX_ID>?</BOX_ID>
                     <!--Optional:-->
                     <FILE_ID>?</FILE_ID>
                  </cal:Get_FTTH_Path>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String ClearView_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:ClearView>
                     <!--Optional:-->
                     <Key>xuser</Key>
                     <!--Optional:-->
                     <LineId>?</LineId>
                     <!--Optional:-->
                     <Option>2</Option>
                  </cal:ClearView>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String GetVoiceLineBillStatusMain_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:GetVoiceLineBillStatusMain>
                     <!--Optional:-->
                     <ServiceNo>?</ServiceNo>
                  </cal:GetVoiceLineBillStatusMain>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String checkOrderPaymentStatus_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:checkOrderPaymentStatus>
                     <!--Optional:-->
                     <COMBESOrderNo>?</COMBESOrderNo>
                  </cal:checkOrderPaymentStatus>
               </soapenv:Body>
            </soapenv:Envelope>
            """;

    private  static final String QueryAssignedCpe_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:QueryAssignedCpe>
                     <!--Optional:-->
                     <COMOrderID>?</COMOrderID>
                     <!--Optional:-->
                     <ProdInstanceID>?</ProdInstanceID>
                  </cal:QueryAssignedCpe>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String GetFTTH_DslamLis_body ="""
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:GetFTTH_DslamList>
                     <!--Optional:-->
                     <popID>?</popID>
                     <!--Optional:-->
                     <InstanceId>?</InstanceId>
                  </cal:GetFTTH_DslamList>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private static final String GetMainOfferStatus_body = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cal="http://CALLS_WS/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cal:GetMainOfferStatus>
                     <!--Optional:-->
                     <ServiceNo>?</ServiceNo>
                     <!--Optional:-->
                     <RequestType>?</RequestType>
                  </cal:GetMainOfferStatus>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    private final SoapClient client = new SoapClient(URL);

    /** Reopen by complaint number. */
    public String FindSessionByUserNameAAA(String Username)
            throws Exception {
        String response = client.send(AAASession_body, Map.of(
                "userName", Username));
        return SoapClient.extractValue(response, "Response");
    }
    public String GetSessionlogsSAList(String Username)
            throws Exception {
        String response = client.send(SessionLog_body, Map.of(
                "userName", Username));
        return SoapClient.extractValue(response, "Response");
    }
    public String Get_FTTH_Path(String Passive_Cabinet_ID , String BOX_ID)
            throws Exception {
        String response = client.send(SessionLog_body, Map.of(
                "Passive_Cabinet_ID", Passive_Cabinet_ID ,
                "BOX_ID" , BOX_ID));
        return SoapClient.extractValue(response, "Response");
    }
    public String ClearView(String LineId)
                throws Exception {
            String response = client.send(SessionLog_body, Map.of(
                    "LineId", LineId));
            return SoapClient.extractValue(response, "Response");
    }
    public String GetVoiceLineBillStatusMain(String ServiceNo)
            throws Exception {
        String response = client.send(GetVoiceLineBillStatusMain_body, Map.of(
                "ServiceNo", ServiceNo));
        return SoapClient.extractValue(response, "Status");
    }
    public String checkOrderPaymentStatus(String COMBESOrderNo)
            throws Exception {
        String response = client.send(checkOrderPaymentStatus_body, Map.of(
                "COMBESOrderNo", COMBESOrderNo));
        return SoapClient.extractValue(response, "Response");
    }
    public String QueryAssignedCpe(String ProdInstanceID , String COMOrderID)
            throws Exception {
        String response = client.send(QueryAssignedCpe_body, Map.of(
                "ProdInstanceID", ProdInstanceID,
                "COMOrderID", COMOrderID));
        return SoapClient.extractValue(response, "Response");
    }

    public String GetFTTH_DslamList(String popID , String InstanceId)
            throws Exception {
        String response = client.send(GetFTTH_DslamLis_body, Map.of(
                "popID", popID,
                "InstanceId", InstanceId));
        return SoapClient.extractValue(response, "Response");
    }
    public String GetMainOfferStatus(String ServiceNo , String RequestType)
            throws Exception {
        String response = client.send(GetMainOfferStatus_body, Map.of(
                "ServiceNo", ServiceNo,
                "RequestType", RequestType));
        return SoapClient.extractValue(response, "Response");
    }


}
