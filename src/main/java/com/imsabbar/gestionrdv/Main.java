package com.imsabbar.gestionrdv;

import com.imsabbar.gestionrdv.util.DBConnection;

public class Main {
    public static void main(String[] args) {
        try (var conn = DBConnection.getConnection()) {
            System.out.println("✅ Connecté : " + conn.getMetaData().getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
