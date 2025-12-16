package crts;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class ReportFrame extends JFrame {

    public ReportFrame() {
        setTitle("Enrollment Report");

        String[] cols = {"Course Name", "Enrolled Students"};
        ArrayList<Object[]> rows = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT course_name, COUNT(r.course_id) AS total " +
                "FROM courses c LEFT JOIN registrations r " +
                "ON c.course_id = r.course_id " +
                "GROUP BY c.course_id"
            );

            while (rs.next()) {
                rows.add(new Object[] {
                    rs.getString("course_name"),
                    rs.getInt("total")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No report data available.");
            dispose();
            return;
        }

        Object[][] data = rows.toArray(new Object[0][]);
        JTable table = new JTable(data, cols);

        add(new JScrollPane(table));

        setSize(450,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

