package BaiTap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private Connection conn;

    public LoginForm() {
        setTitle("Login Form");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        panel.add(registerButton);

        add(panel);
        setVisible(true);

        // Connect to the database
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/Security";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            PreparedStatement statement = conn.prepareStatement("SELECT password FROM users WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String hashedPassword = result.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    JOptionPane.showMessageDialog(this, "Welcom!!");
                    // Open welcome interface or perform any other actions
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Hash the password before storing it
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm();
            }
        });
    }
}
