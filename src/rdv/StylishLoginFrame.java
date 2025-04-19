package rdv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import dao.UserDAO;
import model.User;
import ui.MainFrame;

public class StylishLoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    private final Color LIGHT_BLUE = new Color(220, 235, 245);
    private final Color MEDIUM_BLUE = new Color(180, 210, 230);
    private final Color DARK_BLUE = new Color(70, 110, 130);
    private final Color ACCENT_BLUE = new Color(100, 160, 200);
    private final Color BG_COLOR = new Color(240, 248, 255);

    public StylishLoginFrame() {
        userDAO = new UserDAO();

        setTitle("Hopital - Connexion");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setOpacity(1f);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        titleBar.setOpaque(false);

        mainPanel.add(titleBar, BorderLayout.PAGE_START);
        mainPanel.setBackground(BG_COLOR);

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 40));

        buildLoginForm(leftPanel);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(450, 0));
        rightPanel.setBackground(Color.WHITE);

        try {
            InputStream imgStream = getClass().getResourceAsStream("/sec_avatar.png");
            if (imgStream != null) {
                BufferedImage hospitalImage = ImageIO.read(imgStream);

                JLabel imageLabel = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (hospitalImage != null) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                            int panelWidth = getWidth();
                            int panelHeight = getHeight();

                            int imgWidth = hospitalImage.getWidth();
                            int imgHeight = hospitalImage.getHeight();

                            double scaleX = (double) panelWidth / imgWidth;
                            double scaleY = (double) panelHeight / imgHeight;
                            double scale = Math.min(scaleX, scaleY);

                            int newWidth = (int) (imgWidth * scale);
                            int newHeight = (int) (imgHeight * scale);

                            int x = (panelWidth - newWidth) / 2;
                            int y = (panelHeight - newHeight) / 2;

                            g2.drawImage(hospitalImage, x, y, newWidth, newHeight, this);
                            g2.dispose();
                        }
                    }
                };

                imageLabel.setOpaque(true);
                imageLabel.setBackground(Color.WHITE);
                rightPanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                throw new Exception("Image non trouvée");
            }
        } catch (Exception e) {
            JLabel placeholder = new JLabel("Image Hospitalière", SwingConstants.CENTER);
            placeholder.setFont(new Font("Sans-serif", Font.PLAIN, 24));
            placeholder.setForeground(Color.GRAY);
            rightPanel.add(placeholder, BorderLayout.CENTER);
        }

        return rightPanel;
    }

    private void buildLoginForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Connectez-vous");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Accédez au système de gestion hospitalière");
        subtitleLabel.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 130, 150));
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(30), gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 3;
        JLabel userLabel = new JLabel("Identifiant");
        userLabel.setFont(new Font("Sans-serif", Font.PLAIN, 24));
        panel.add(userLabel, gbc);

        gbc.gridy = 4;
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        panel.add(usernameField, gbc);

        gbc.gridy = 5;
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Sans-serif", Font.PLAIN, 24));
        panel.add(passLabel, gbc);

        gbc.gridy = 6;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        panel.add(passwordField, gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Connexion");
        styleButton(loginButton);
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton, gbc);

        gbc.gridy = 8;
        JLabel adminLabel = new JLabel("Connecter en tant qu'Admin");
        adminLabel.setForeground(ACCENT_BLUE);
        adminLabel.setFont(new Font("Sans-serif", Font.PLAIN, 16));
        adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fadeOutAndOpenAdmin();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                adminLabel.setText("<html><u>Connecter en tant qu'Admin</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                adminLabel.setText("Connecter en tant qu'Admin");
            }
        });
        panel.add(adminLabel, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, MEDIUM_BLUE),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        field.setBackground(Color.WHITE);
        field.setFont(new Font("Sans-serif", Font.PLAIN, 16));
    }

    private void styleButton(JButton button) {
        button.setBackground(ACCENT_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Sans-serif", Font.BOLD, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 40, 12, 40),
                BorderFactory.createLineBorder(DARK_BLUE, 1)
        ));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(DARK_BLUE);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(ACCENT_BLUE);
            }
        });
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            if (!"RECEPTIONIST".equalsIgnoreCase(user.getRole())) {
                JOptionPane.showMessageDialog(this,
                        "Accès refusé: utilisateur non secrétaire",
                        "Erreur d'authentification",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.dispose();
            EventQueue.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect", "Erreur d'authentification", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void fadeOutAndOpenAdmin() {
        this.dispose(); // Ferme simplement la fenêtre actuelle
        new AdminLoginFrame().setVisible(true); // Ouvre directement l'admin frame
    }

    private void fadeInAdminLogin() {
        AdminLoginFrame adminFrame = new AdminLoginFrame();
        adminFrame.setOpacity(0f);
        adminFrame.setVisible(true);

        Timer timer = new Timer(20, null);
        final float[] opacity = {0f};
        timer.addActionListener(e -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1f) {
                adminFrame.setOpacity(1f);
                timer.stop();
            } else {
                adminFrame.setOpacity(Math.min(1f, opacity[0]));
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StylishLoginFrame frame = new StylishLoginFrame();
            frame.setVisible(true);
        });
    }
}
