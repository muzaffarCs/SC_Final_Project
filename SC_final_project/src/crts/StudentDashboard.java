package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {

    private final int registrationId;
    private JLabel usernameLabel = new JLabel("Loading...");

    public StudentDashboard(int registrationId) {
        this.registrationId = registrationId;

        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        add(UIHelper.title("STUDENT DASHBOARD"), BorderLayout.NORTH);

        // Student Info Panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel userText = new JLabel("Logged in as:");
        userText.setFont(new Font("Segoe UI", Font.BOLD, 14));

        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(0, 70, 140));

        infoPanel.add(userText);
        infoPanel.add(usernameLabel);

        add(infoPanel, BorderLayout.SOUTH);

        // Buttons Grid
        JPanel grid = UIHelper.panel(new GridLayout(2, 2, 20, 20));

        JButton registerBtn = UIHelper.button("Register Course");
        JButton dropBtn = UIHelper.button("Drop Course");
        JButton viewBtn = UIHelper.button("View Timetable");
        JButton logoutBtn = UIHelper.button("Logout");

        grid.add(registerBtn);
        grid.add(dropBtn);
        grid.add(viewBtn);
        grid.add(logoutBtn);

        add(grid, BorderLayout.CENTER);

        // Actions
        registerBtn.addActionListener(e -> new CourseRegistrationFrame(this.registrationId));

        dropBtn.addActionListener(e -> new DropCourseFrame(this.registrationId));

        viewBtn.addActionListener(e -> new TimetableFrame(this.registrationId));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        // Load Student Info
        loadUsername();

        setSize(600, 420);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadUsername() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT username FROM users WHERE user_id = ?");
            ps.setInt(1, registrationId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usernameLabel.setText(rs.getString("username"));
            } else {
                usernameLabel.setText("Unknown");
            }

        } catch (Exception e) {
            usernameLabel.setText("Error");
        }
    }
}
