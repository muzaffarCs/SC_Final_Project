package crts;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    int userId;

    public StudentDashboard(int id) {
        userId = id;
        setTitle("Student Dashboard");
        setLayout(new GridLayout(2,2,15,15));

        JButton register = new JButton("Register Courses");
        JButton drop = new JButton("Drop Course");
        JButton view = new JButton("View Timetable");
        JButton logout = new JButton("Logout");

        add(register); add(drop);
        add(view); add(logout);

        register.addActionListener(e -> new CourseRegistrationFrame(userId));
        view.addActionListener(e -> new TimetableFrame(userId));
        drop.addActionListener(e -> new DropCourseFrame(userId));
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });

        setSize(500,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
