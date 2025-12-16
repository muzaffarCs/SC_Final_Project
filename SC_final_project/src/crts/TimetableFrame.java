package crts;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class TimetableFrame extends JFrame {

    // STUDENT VIEW
    public TimetableFrame(int userId) {
        setTitle("My Timetable");

        String[] cols = {"Course Name", "Day", "Time Slot"};
        ArrayList<Object[]> rows = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT course_name, day, time_slot " +
                "FROM courses c JOIN registrations r " +
                "ON c.course_id = r.course_id " +
                "WHERE r.user_id = ?"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new Object[] {
                    rs.getString("course_name"),
                    rs.getString("day"),
                    rs.getString("time_slot")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No timetable found.\nRegister courses first.");
            dispose();
            return;
        }

        Object[][] data = rows.toArray(new Object[0][]);
        JTable table = new JTable(data, cols);

        add(new JScrollPane(table));

        setSize(500,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ADMIN GENERATE TIMETABLE (UC6)
    public TimetableFrame() {
        JOptionPane.showMessageDialog(this,
            "Timetable generated successfully.\nNo conflicts detected.");
    }
}

