package panel;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BilanPanel extends JDialog {
    private int patientIndex;
    private int age;
    private String patientNom;
    private String couverture;
    private String dateHeure;
    private String medecin;
    private String specialite;
    private String batiment;
    private String observations;

    public BilanPanel(int patientIndex, int age, String patientNom, String couverture,
                      String dateHeure, String medecin, String specialite, String batiment,
                      String observations) {
        this.patientIndex = patientIndex;
        this.age = age;
        this.patientNom = patientNom;
        this.couverture = couverture;
        this.dateHeure = dateHeure;
        this.medecin = medecin;
        this.specialite = specialite;
        this.batiment = batiment;
        this.observations = observations;

        setTitle("Bilan - Patient " + patientNom);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Index patient:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(patientIndex)), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Âge:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(age)), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Nom patient:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(patientNom), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Couverture Sociale:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(couverture), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Rendez-vous:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(dateHeure), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Médecin:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(medecin), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Spécialité:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(specialite), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Bâtiment:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(batiment), gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panel.add(new JLabel("Observations:"), gbc);
        gbc.gridx = 1;
        JTextArea observationsArea = new JTextArea(observations, 3, 25);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setEditable(false);
        panel.add(new JScrollPane(observationsArea), gbc);

        // Export Button
        gbc.gridx = 0; gbc.gridy = ++y; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton exportButton = new JButton("Export Bilan as PNG");
        exportButton.addActionListener(e -> exportAsImage(panel));
        panel.add(exportButton, gbc);

        add(panel);
    }

    private void exportAsImage(Component component) {
        try {
            int w = component.getWidth();
            int h = component.getHeight();
            if (w == 0 || h == 0) {
                JOptionPane.showMessageDialog(this, "Please resize or wait for the window to fully display.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            component.paint(g2);
            g2.dispose();

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Bilan as Image");
            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".png")) {
                    path += ".png";
                    file = new File(path);
                }
                ImageIO.write(img, "png", file);
                JOptionPane.showMessageDialog(this, "Bilan exported successfully to:\n" + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting image:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
