package main;

import rdv.StylishLoginFrame;
import util.DatabaseInitializer;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set the look and feel to FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
            ex.printStackTrace();
        }
        
        // Initialize database
        DatabaseInitializer.initialize();
        
        // Run the application on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            StylishLoginFrame stylishLoginFrame = new StylishLoginFrame();
            stylishLoginFrame.setVisible(true);
        });
    }
}