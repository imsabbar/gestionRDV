package rdv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dao.UserDAO;
import model.User;
import ui.MainFrameAdmin;

public class AdminLoginFrame extends JFrame {

    private final Color LIGHT_BLUE = new Color(220, 235, 245);
    private final Color MEDIUM_BLUE = new Color(180, 210, 230);
    private final Color DARK_BLUE = new Color(70, 110, 130);
    private final Color ACCENT_BLUE = new Color(100, 160, 200);
    private final Color BG_COLOR = new Color(240, 248, 255);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public AdminLoginFrame() {
        userDAO = new UserDAO();
        setTitle("Connexion Administrateur");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setOpacity(1f);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Panel gauche pour l'image
        JPanel imagePanel = createImagePanel();
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel droit pour le formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 60));

        buildLoginForm(formPanel);

        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(350, 0));
        imagePanel.setBackground(BG_COLOR);

        try {
            InputStream imgStream = getClass().getResourceAsStream("/admin_avatar.png");
            if (imgStream != null) {
                BufferedImage image = ImageIO.read(imgStream);

                JLabel imageLabel = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (image != null) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                            int width = getWidth();
                            int height = getHeight();

                            int imgWidth = image.getWidth();
                            int imgHeight = image.getHeight();
                            float ratio = Math.min(
                                    (float) width / imgWidth,
                                    (float) height / imgHeight
                            );

                            int newWidth = (int) (imgWidth * ratio);
                            int newHeight = (int) (imgHeight * ratio);

                            int x = (width - newWidth) / 2;
                            int y = (height - newHeight) / 2;

                            g2.drawImage(image, x, y, newWidth, newHeight, this);
                            g2.dispose();
                        }
                    }
                };

                imagePanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                JLabel placeholder = new JLabel("Image Admin", SwingConstants.CENTER);
                placeholder.setFont(new Font("Sans-serif", Font.PLAIN, 24));
                placeholder.setForeground(Color.GRAY);
                imagePanel.add(placeholder, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Erreur de chargement de l'image", SwingConstants.CENTER);
            imagePanel.add(errorLabel, BorderLayout.CENTER);
        }

        return imagePanel;
    }

    private void buildLoginForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Connexion Administrateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 22));
        titleLabel.setForeground(DARK_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(new Font("Sans-serif", Font.PLAIN, 16));
        panel.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        panel.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Sans-serif", Font.PLAIN, 16));
        panel.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        JButton loginButton = new JButton("Se connecter");
        styleButton(loginButton);
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton, gbc);

        // Ajout du lien pour retourner à la connexion standard
        gbc.gridy++;
        JLabel standardLoginLabel = new JLabel("Retour à la connexion standard");
        standardLoginLabel.setForeground(ACCENT_BLUE);
        standardLoginLabel.setFont(new Font("Sans-serif", Font.PLAIN, 16));
        standardLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        standardLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new StylishLoginFrame().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                standardLoginLabel.setText("<html><u>Retour à la connexion standard</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                standardLoginLabel.setText("Retour à la connexion standard");
            }
        });
        panel.add(standardLoginLabel, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                JOptionPane.showMessageDialog(this,
                        "Accès refusé: utilisateur non administrateur",
                        "Erreur d'authentification",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.dispose();
            EventQueue.invokeLater(() -> {
                MainFrameAdmin mainFrameAdmin = new MainFrameAdmin(user);
                mainFrameAdmin.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect", "Erreur d'authentification", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
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
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(DARK_BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_BLUE);
            }
        });
    }
}
