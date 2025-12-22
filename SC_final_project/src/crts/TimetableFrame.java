package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class TimetableFrame extends JFrame {

    public TimetableFrame(int regId) {

        setTitle("Student Timetable");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = UIHelper.title("My Weekly Timetable");
        add(title, BorderLayout.NORTH);

        String[] cols = { "Course Code", "Course Title", "Day", "Time Slot", "Room" };
        ArrayList<Object[]> data = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                    "SELECT c.course_code, c.course_title, c.day, c.time_slot, c.room " +
                    "FROM courses c " +
                    "JOIN registrations r ON c.course_id = r.course_id " +
                    "WHERE r.registration_id = ? " +
                    "ORDER BY FIELD(c.day,'Monday','Tuesday','Wednesday','Thursday','Friday'), c.time_slot";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, regId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new Object[] {
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("day"),
                        rs.getString("time_slot"),
                        rs.getString("room")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load timetable: " + e.getMessage());
            e.printStackTrace();
        }

        JTable table = new JTable(data.toArray(new Object[0][]), cols);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}
