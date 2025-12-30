package crts;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class ReportFrame extends JFrame {

    public ReportFrame() {

        setTitle("Course Enrollment Report");

        String[] cols = { "Code", "Course", "Day", "Time", "Enrolled" };
        ArrayList<Object[]> data = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT c.course_code, c.course_title, c.day, c.time_slot, " +
                            "COUNT(r.course_id) AS enrolled " +
                            "FROM courses c " +
                            "LEFT JOIN registrations r ON c.course_id = r.course_id " +
                            "GROUP BY c.course_id, c.course_code, c.course_title, c.day, c.time_slot " +
                            "ORDER BY c.course_code, c.day");

            while (rs.next()) {
                data.add(new Object[] {
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("day"),
                        rs.getString("time_slot"),
                        rs.getInt("enrolled")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        JTable table = new JTable(data.toArray(new Object[0][]), cols);
        add(new JScrollPane(table));

        setSize(700, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
