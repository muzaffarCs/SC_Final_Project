package crts;

import javax.swing.*;
import java.sql.*;

public class DropCourseFrame extends JFrame {

    int userId;
    JTextField courseIdField = new JTextField(10);

    public DropCourseFrame(int userId) {
        this.userId = userId;

        setTitle("Drop Course");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Enter Course ID to Drop:"));
        add(courseIdField);

        JButton dropBtn = new JButton("Drop Course");
        add(dropBtn);

        dropBtn.addActionListener(e -> dropCourse());

        setSize(300,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void dropCourse() {
        try (Connection con = DBConnection.getConnection()) {
            int courseId = Integer.parseInt(courseIdField.getText());

            // Check registration exists
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM registrations WHERE user_id=? AND course_id=?"
            );
            check.setInt(1, userId);
            check.setInt(2, courseId);

            if (!check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Not registered in this course");
                return;
            }

            // Remove registration
            PreparedStatement delete = con.prepareStatement(
                "DELETE FROM registrations WHERE user_id=? AND course_id=?"
            );
            delete.setInt(1, userId);
            delete.setInt(2, courseId);
            delete.executeUpdate();

            // Increase seat count
            PreparedStatement update = con.prepareStatement(
                "UPDATE courses SET seats = seats + 1 WHERE course_id=?"
            );
            update.setInt(1, courseId);
            update.executeUpdate();

            JOptionPane.showMessageDialog(this, "Course Dropped Successfully");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Drop failed");
            e.printStackTrace();
        }
    }
}

