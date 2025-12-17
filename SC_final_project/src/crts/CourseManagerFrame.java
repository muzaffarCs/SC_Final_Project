package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CourseManagerFrame extends JFrame {

    private JTextField codeField = new JTextField(18);
    private JTextField titleField = new JTextField(18);
    private JTextField seatsField = new JTextField(18);

    private JComboBox<String> dayBox = new JComboBox<>(
            new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" });

    private JComboBox<String> timeBox = new JComboBox<>(
            new String[] {
                    "09:05-10:35",
                    "10:45-12:15",
                    "12:25-13:55",
                    "14:15-15:45",
                    "16:00-17:30"
            });

    public CourseManagerFrame() {

        setTitle("Manage Courses");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ---------- Title ----------
        add(UIHelper.title("MANAGE COURSES"), BorderLayout.NORTH);

        // ---------- Center Panel ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        // ---------- Helper ----------
        Dimension fieldSize = new Dimension(220, 28);

        // ---------- Row 1 ----------
        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Course Code"), c);

        c.gridx = 1;
        codeField.setPreferredSize(fieldSize);
        form.add(codeField, c);

        // ---------- Row 2 ----------
        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Course Title"), c);

        c.gridx = 1;
        titleField.setPreferredSize(fieldSize);
        form.add(titleField, c);

        // ---------- Row 3 ----------
        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("Day"), c);

        c.gridx = 1;
        dayBox.setPreferredSize(fieldSize);
        form.add(dayBox, c);

        // ---------- Row 4 ----------
        c.gridx = 0;
        c.gridy = 3;
        form.add(new JLabel("Time Slot"), c);

        c.gridx = 1;
        timeBox.setPreferredSize(fieldSize);
        form.add(timeBox, c);

        // ---------- Row 5 ----------
        c.gridx = 0;
        c.gridy = 4;
        form.add(new JLabel("Max Seats"), c);

        c.gridx = 1;
        seatsField.setPreferredSize(fieldSize);
        form.add(seatsField, c);

        // ---------- Button ----------
        JButton addBtn = UIHelper.button("Add Course");
        addBtn.setPreferredSize(new Dimension(150, 35));

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(addBtn, c);

        add(form, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addCourse());

        setVisible(true);
    }

    private void addCourse() {
        try {
            int seats = Integer.parseInt(seatsField.getText().trim());

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO courses(course_code,course_title,day,time_slot,max_seats,available_seats) " +
                            "VALUES(?,?,?,?,?,?)");

            ps.setString(1, codeField.getText().trim());
            ps.setString(2, titleField.getText().trim());
            ps.setString(3, dayBox.getSelectedItem().toString());
            ps.setString(4, timeBox.getSelectedItem().toString());
            ps.setInt(5, seats);
            ps.setInt(6, seats);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Course Added Successfully");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Seats must be a number");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
