package crts;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(UIHelper.title("ADMIN DASHBOARD"), BorderLayout.NORTH);

        JPanel grid = UIHelper.panel(new GridLayout(2, 2, 20, 20));

        JButton manageBtn = UIHelper.button("Manage Courses");
        JButton timetableBtn = UIHelper.button("Generate Timetable");
        JButton reportBtn = UIHelper.button("Reports");
        JButton logoutBtn = UIHelper.button("Logout");

        grid.add(manageBtn);
        grid.add(timetableBtn);
        grid.add(reportBtn);
        grid.add(logoutBtn);

        add(grid, BorderLayout.CENTER);

        // -------- Actions --------
        manageBtn.addActionListener(e -> new CourseManagerFrame());

        reportBtn.addActionListener(e -> new ReportFrame());

        timetableBtn.addActionListener(e -> new GenerateTimetableFrame());

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
