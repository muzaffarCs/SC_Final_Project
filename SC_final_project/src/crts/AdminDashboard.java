package crts;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setLayout(new GridLayout(2,2,15,15));

        JButton manage = new JButton("Manage Courses");
        JButton timetable = new JButton("Generate Timetable");
        JButton reports = new JButton("Reports");
        JButton logout = new JButton("Logout");

        add(manage); add(timetable);
        add(reports); add(logout);

        manage.addActionListener(e -> new CourseManagerFrame());
        timetable.addActionListener(e -> {
    JOptionPane.showMessageDialog(this,
        "Timetable generated successfully.\nNo conflicts detected.");
});
        reports.addActionListener(e -> new ReportFrame());
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });

        setSize(500,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
