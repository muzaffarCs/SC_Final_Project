package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class DropCourseFrame extends JFrame {

    private int registrationId;
    private JTextField courseCodeField = new JTextField(18);

    public DropCourseFrame(int registrationId) {

        this.registrationId = registrationId;

        setTitle("Drop Course");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIHelper.title("DROP COURSE"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Course Code"), c);

        c.gridx = 1;
        courseCodeField.setPreferredSize(new Dimension(220, 28));
        form.add(courseCodeField, c);

        JButton dropBtn = UIHelper.button("Drop Course");

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

            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(
                "SELECT c.course_id, c.drop_deadline " +
                "FROM registrations r JOIN courses c " +
                "ON r.course_id=c.course_id " +
                "WHERE r.registration_id=? AND c.course_code=?");

            ps.setInt(1, registrationId);
            ps.setString(2, courseCode);

            ResultSet rs = ps.executeQuery();

            boolean found = false;
            LocalDate today = LocalDate.now();

            while (rs.next()) {

                found = true;

                LocalDate deadline = rs.getDate("drop_deadline").toLocalDate();
                if (today.isAfter(deadline)) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this,
                        "Drop deadline passed: " + deadline);
                    return;
                }

                int courseId = rs.getInt("course_id");

                PreparedStatement del = con.prepareStatement(
                    "DELETE FROM registrations WHERE registration_id=? AND course_id=?");
                del.setInt(1, registrationId);
                del.setInt(2, courseId);
                del.executeUpdate();

                PreparedStatement upd = con.prepareStatement(
                    "UPDATE courses SET available_seats=available_seats+1 WHERE course_id=?");
                upd.setInt(1, courseId);
                upd.executeUpdate();
            }

            if (!found) {
                con.rollback();
                JOptionPane.showMessageDialog(this,
                    "You are not registered in this course");
                return;
            }

            con.commit();
            JOptionPane.showMessageDialog(this,
                "Course dropped successfully");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Drop failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
