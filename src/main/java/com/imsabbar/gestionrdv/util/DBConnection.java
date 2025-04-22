package com.imsabbar.gestionrdv.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static String url;
    private static String user;
    private static String pass;

    static {
        try (InputStream is = DBConnection.class.getResourceAsStream("/db.properties")) {
            Properties props = new Properties();
            props.load(is);
            url  = props.getProperty("db.url");
            user = props.getProperty("db.user");
            pass = props.getProperty("db.pass");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger db.properties", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, pass);
    }
}
