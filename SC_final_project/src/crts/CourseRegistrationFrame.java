package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CourseRegistrationFrame extends JFrame {

    private int registrationId;
    private JTextField courseCodeField = new JTextField(18);

    public CourseRegistrationFrame(int registrationId) {
        this.registrationId = registrationId;

        setTitle("Register Course");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- Title ----------
        add(UIHelper.title("REGISTER COURSE"), BorderLayout.NORTH);

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
        JButton registerBtn = UIHelper.button("Register Course");
        registerBtn.setPreferredSize(new Dimension(160, 35));

        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(registerBtn, c);

        add(form, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> registerCourse());

        setVisible(true);
    }

    private void registerCourse() {
        String courseCode = courseCodeField.getText().trim();

        if (courseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter course code");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement getCourse = con.prepareStatement(
                "SELECT course_id, available_seats FROM courses WHERE course_code=?"
            );
            getCourse.setString(1, courseCode);
            ResultSet rs = getCourse.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Course not found");
                return;
            }

            int courseId = rs.getInt("course_id");
            int seats = rs.getInt("available_seats");

            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, "No seats available");
                return;
            }

            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO registrations(registration_id, course_id) VALUES (?, ?)"
            );
            insert.setInt(1, registrationId);
            insert.setInt(2, courseId);
            insert.executeUpdate();

            PreparedStatement update = con.prepareStatement(
                "UPDATE courses SET available_seats = available_seats - 1 WHERE course_id=?"
            );
            update.setInt(1, courseId);
            update.executeUpdate();

            JOptionPane.showMessageDialog(this, "Course Registered Successfully");
            dispose();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Already registered in this course");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }
    }
}
