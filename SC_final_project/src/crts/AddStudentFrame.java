package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddStudentFrame extends JFrame {

    private JTextField usernameField = new JTextField(18);
    private JTextField emailField = new JTextField(18);
    private JPasswordField passwordField = new JPasswordField(18);
    private JTextField programField = new JTextField(18);
    private JTextField semesterField = new JTextField(18);

    public AddStudentFrame() {

        setTitle("Add New Student");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIHelper.title("ADD NEW STUDENT"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        Dimension fieldSize = new Dimension(220, 28);

        // Username
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Username"), c);
        c.gridx = 1;
        usernameField.setPreferredSize(fieldSize);
        form.add(usernameField, c);

        // Email
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Email"), c);
        c.gridx = 1;
        emailField.setPreferredSize(fieldSize);
        form.add(emailField, c);

        // Password
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Password"), c);
        c.gridx = 1;
        passwordField.setPreferredSize(fieldSize);
        form.add(passwordField, c);

        // Program
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Program"), c);
        c.gridx = 1;
        programField.setPreferredSize(fieldSize);
        form.add(programField, c);

        // Semester
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Semester"), c);
        c.gridx = 1;
        semesterField.setPreferredSize(fieldSize);
        form.add(semesterField, c);

        // Button
        JButton addBtn = UIHelper.button("Add Student");
        addBtn.setPreferredSize(new Dimension(160, 35));

        c.gridx = 0; c.gridy++;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(addBtn, c);

        add(form, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addStudent());

        setVisible(true);
    }

    private void addStudent() {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String program = programField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                || program.isEmpty() || semesterText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        int semester;
        try {
            semester = Integer.parseInt(semesterText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Semester must be a number");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Transaction start
            con.setAutoCommit(false);

            // Insert into users
            PreparedStatement userPS = con.prepareStatement(
                "INSERT INTO users(username, email, password_hash, role) VALUES (?, ?, ?, 'STUDENT')",
                Statement.RETURN_GENERATED_KEYS
            );
            userPS.setString(1, username);
            userPS.setString(2, email);
            userPS.setString(3, PasswordUtil.hash(password));
            userPS.executeUpdate();

            ResultSet keys = userPS.getGeneratedKeys();
            if (!keys.next()) {
                con.rollback();
                throw new SQLException("User ID not generated");
            }

            int userId = keys.getInt(1);

            // Insert into students
            PreparedStatement studentPS = con.prepareStatement(
                "INSERT INTO students(registration_id, program, semester) VALUES (?, ?, ?)"
            );
            studentPS.setInt(1, userId);
            studentPS.setString(2, program);
            studentPS.setInt(3, semester);
            studentPS.executeUpdate();

            // Commit
            con.commit();

            JOptionPane.showMessageDialog(this, "Student added successfully");
            dispose();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username or Email already exists");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
