package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DropCourseFrame extends JFrame {

    private int registrationId;
    private JTextField courseCodeField = new JTextField(18);

    public DropCourseFrame(int registrationId) {
        this.registrationId = registrationId;

        setTitle("Drop Course");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- Title ----------
        add(UIHelper.title("DROP COURSE"), BorderLayout.NORTH);

        // ---------- Form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        Dimension fieldSize = new Dimension(220, 28);

        // ---------- Course Code ----------
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Course Code"), c);

        c.gridx = 1;
        courseCodeField.setPreferredSize(fieldSize);
        form.add(courseCodeField, c);

        // ---------- Button ----------
        JButton dropBtn = UIHelper.button("Drop Course");
        dropBtn.setPreferredSize(new Dimension(150, 35));

        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(dropBtn, c);

        add(form, BorderLayout.CENTER);

        dropBtn.addActionListener(e -> dropCourse());

        setVisible(true);
    }

    private void dropCourse() {
        String courseCode = courseCodeField.getText().trim();

        if (courseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter course code");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Get course_id
            PreparedStatement ps = con.prepareStatement(
                "SELECT course_id FROM courses WHERE course_code=?"
            );
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Course not found");
                return;
            }

            int courseId = rs.getInt(1);

            PreparedStatement del = con.prepareStatement(
                "DELETE FROM registrations WHERE registration_id=? AND course_id=?"
            );
            del.setInt(1, registrationId);
            del.setInt(2, courseId);

            if (del.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(this, "You are not registered in this course");
                return;
            }

            PreparedStatement update = con.prepareStatement(
                "UPDATE courses SET available_seats = available_seats + 1 WHERE course_id=?"
            );
            update.setInt(1, courseId);
            update.executeUpdate();

            JOptionPane.showMessageDialog(this, "Course Dropped Successfully");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
