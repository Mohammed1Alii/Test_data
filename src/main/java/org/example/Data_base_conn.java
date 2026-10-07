package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Data_base_conn
{
    private Connection connection;
    public Data_base_conn() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            props.load(in);
        }

        String dbUrl    = props.getProperty("DB_URL");
        String user     = props.getProperty("DB_USER");
        String password = props.getProperty("DB_PASSWORD");
        //System.out.println(user);
        try {
            connection = DriverManager.getConnection(dbUrl, user, password);
            System.out.println("Connected to Oracle Database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> GetAllTeleNu() throws SQLException {
        String query =
                "SELECT SERVICE_ID FROM ( " +
                        "SELECT SERVICE_ID, initate_date FROM wf_work_order " +
                        "WHERE LENGTH(TEL_NO) = 8 " +
                        "AND request_type IN ('FTTHNewSubHV','FTTHOntRep','FTTHNewSubV','FTTHMigrationHV','FTTHMigrationNewSubV') " +
                        "UNION ALL " +
                        "SELECT SERVICE_ID, initate_date FROM wf_work_order_ar " +
                        "WHERE LENGTH(TEL_NO) = 8 " +
                        "AND request_type IN ('FTTHNewSubHV','FTTHOntRep','FTTHNewSubV','FTTHMigrationHV','FTTHMigrationNewSubV') " +
                        ") " +
                        "GROUP BY SERVICE_ID " +
                        "ORDER BY MAX(initate_date) DESC";

        List<String> ids = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("SERVICE_ID"));
            }
            System.out.println("Query Completed Successfully");
            System.out.println(ids.size());
        }
        return ids;
    }
    public List<String> GetAllComOrderID() throws SQLException {
        String query =
                "SELECT COM_ORDER_ID FROM ( " +
                        "SELECT COM_ORDER_ID, initate_date FROM wf_work_order " +
                        "WHERE request_type IN ('FTTHNewSubHV','FTTHOntRep','FTTHNewSubV','FTTHMigrationHV','FTTHMigrationNewSubV') " +
                        "UNION ALL " +
                        "SELECT COM_ORDER_ID, initate_date FROM wf_work_order_ar " +
                        "WHERE request_type IN ('FTTHNewSubHV','FTTHOntRep','FTTHNewSubV','FTTHMigrationHV','FTTHMigrationNewSubV') " +
                        ") " +
                        "GROUP BY COM_ORDER_ID " +
                        "ORDER BY MAX(initate_date) DESC";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("COM_ORDER_ID"));
            }
            System.out.println("Query Completed Successfully..!!");
            System.out.println(ids.size());
        }
        return ids;
    }
    public List<String> DSLAM() throws SQLException {
        String query = "select DISTINCT (NW_ELEMENT_VALUE) from lc_place_demographic where NW_ELEMENT_TYPE = 'POP' and org_role_id = 'MBKGZ'";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("NW_ELEMENT_VALUE"));
            }
            System.out.println("Query Completed Successfully..!!");
            System.out.println(ids.size());
        }
        return ids;
    }


}

