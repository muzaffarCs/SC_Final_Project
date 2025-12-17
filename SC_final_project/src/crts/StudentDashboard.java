package crts;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final int registrationId;

    public StudentDashboard(int registrationId) {
        this.registrationId = registrationId;

        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(UIHelper.title("STUDENT DASHBOARD"), BorderLayout.NORTH);

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

        // ✅ FIELD IS USED HERE
        registerBtn.addActionListener(e -> new CourseRegistrationFrame(this.registrationId));

        dropBtn.addActionListener(e -> new DropCourseFrame(this.registrationId));

        viewBtn.addActionListener(e -> new TimetableFrame(this.registrationId));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
