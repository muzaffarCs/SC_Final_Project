package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

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

        add(UIHelper.title("REGISTER COURSE"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        Dimension fieldSize = new Dimension(220, 28);

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Course Code"), c);

        c.gridx = 1;
        courseCodeField.setPreferredSize(fieldSize);
        form.add(courseCodeField, c);

        JButton registerBtn = UIHelper.button("Register Course");
        registerBtn.setPreferredSize(new Dimension(160, 35));

        c.gridx = 0;
        c.gridy = 1;
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

            // 1️⃣ Select all slots for this course code
            PreparedStatement ps = con.prepareStatement(
                    "SELECT course_id, available_seats FROM courses WHERE course_code=?");
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            ArrayList<Integer> slotsToRegister = new ArrayList<>();

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                int seats = rs.getInt("available_seats");

                if (seats > 0) {
                    slotsToRegister.add(courseId);
                }
            }

            if (slotsToRegister.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available slots for this course");
                return;
            }

            // 2️⃣ Insert all slots
            PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO registrations(registration_id, course_id) VALUES (?, ?)");
            PreparedStatement updateSeats = con.prepareStatement(
                    "UPDATE courses SET available_seats = available_seats - 1 WHERE course_id=?");

            int registeredCount = 0;

            for (int courseId : slotsToRegister) {
                try {
                    insert.setInt(1, registrationId);
                    insert.setInt(2, courseId);
                    insert.executeUpdate();

                    updateSeats.setInt(1, courseId);
                    updateSeats.executeUpdate();

                    registeredCount++;
                } catch (SQLIntegrityConstraintViolationException e) {
                    // Already registered for this slot, skip
                }
            }

            if (registeredCount > 0) {
                JOptionPane.showMessageDialog(this, "Successfully registered for " + registeredCount + " slot(s)");
            } else {
                JOptionPane.showMessageDialog(this, "You are already registered for all slots of this course");
            }

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }
    }
}
