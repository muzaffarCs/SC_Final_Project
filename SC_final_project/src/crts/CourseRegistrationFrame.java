package crts;

import javax.swing.*;
import java.sql.*;

public class CourseRegistrationFrame extends JFrame {

    int userId;
    JTextField courseIdField = new JTextField(10);

    public CourseRegistrationFrame(int userId) {
        this.userId = userId;

        setTitle("Course Registration");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Enter Course ID:"));
        add(courseIdField);

        JButton registerBtn = new JButton("Register");
        add(registerBtn);

        registerBtn.addActionListener(e -> registerCourse());

        setSize(300,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void registerCourse() {
        int courseId;

        try {
            courseId = Integer.parseInt(courseIdField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            /* -------------------------------
               1️⃣ CHECK COURSE EXISTS + SEATS
            -------------------------------- */
            PreparedStatement psCourse = con.prepareStatement(
                "SELECT day, time_slot, seats FROM courses WHERE course_id=?"
            );
            psCourse.setInt(1, courseId);
            ResultSet courseRS = psCourse.executeQuery();

            if (!courseRS.next()) {
                JOptionPane.showMessageDialog(this, "Course not found");
                return;
            }

            String day = courseRS.getString("day");
            String time = courseRS.getString("time_slot");
            int seats = courseRS.getInt("seats");

            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, "Class Full");
                return;
            }

            /* -------------------------------
               2️⃣ CHECK ALREADY REGISTERED
            -------------------------------- */
            PreparedStatement already = con.prepareStatement(
                "SELECT * FROM registrations WHERE user_id=? AND course_id=?"
            );
            already.setInt(1, userId);
            already.setInt(2, courseId);

            if (already.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Already registered");
                return;
            }

            /* -------------------------------
               3️⃣ CLASH DETECTION (UC4)
            -------------------------------- */
            PreparedStatement clash = con.prepareStatement("""
                SELECT * FROM courses c
                JOIN registrations r ON c.course_id = r.course_id
                WHERE r.user_id=? AND c.day=? AND c.time_slot=?
            """);
            clash.setInt(1, userId);
            clash.setString(2, day);
            clash.setString(3, time);

            if (clash.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Time clash detected");
                return;
            }

            /* -------------------------------
               4️⃣ REGISTER COURSE
            -------------------------------- */
            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO registrations(user_id, course_id) VALUES (?,?)"
            );
            insert.setInt(1, userId);
            insert.setInt(2, courseId);
            insert.executeUpdate();

            /* -------------------------------
               5️⃣ UPDATE SEATS
            -------------------------------- */
            PreparedStatement updateSeats = con.prepareStatement(
                "UPDATE courses SET seats = seats - 1 WHERE course_id=?"
            );
            updateSeats.setInt(1, courseId);
            updateSeats.executeUpdate();

            JOptionPane.showMessageDialog(this, "Course Registered Successfully");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed");
        }
    }
}
