package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CourseManagerFrame extends JFrame {
    JTextField code = new JTextField(10);
    JTextField name = new JTextField(15);
    JTextField day = new JTextField(10);
    JTextField time = new JTextField(10);
    JTextField seats = new JTextField(5);

    public CourseManagerFrame() {
        setTitle("Manage Courses");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        c.gridx=0;c.gridy=0; add(new JLabel("Code"),c);
        c.gridx=1; add(code,c);

        c.gridx=0;c.gridy=1; add(new JLabel("Name"),c);
        c.gridx=1; add(name,c);

        c.gridx=0;c.gridy=2; add(new JLabel("Day"),c);
        c.gridx=1; add(day,c);

        c.gridx=0;c.gridy=3; add(new JLabel("Time"),c);
        c.gridx=1; add(time,c);

        c.gridx=0;c.gridy=4; add(new JLabel("Seats"),c);
        c.gridx=1; add(seats,c);

        JButton add = new JButton("Add Course");
        c.gridx=1;c.gridy=5; add(add,c);

        add.addActionListener(e -> addCourse());

        setSize(400,350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void addCourse() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = """
                INSERT INTO courses(course_code,course_name,day,time_slot,seats)
                VALUES(?,?,?,?,?)
            """;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, code.getText());
            ps.setString(2, name.getText());
            ps.setString(3, day.getText());
            ps.setString(4, time.getText());
            ps.setInt(5, Integer.parseInt(seats.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Course Added");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
