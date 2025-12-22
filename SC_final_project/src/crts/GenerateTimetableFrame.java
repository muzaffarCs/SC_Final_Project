package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class GenerateTimetableFrame extends JFrame {

    public GenerateTimetableFrame() {

        setTitle("Optimized Timetable (All Students)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIHelper.title("Generated Conflict-Free Timetable"), BorderLayout.NORTH);

        String[] cols = { "Student ID", "Course Code", "Course Title", "Day", "Time Slot", "Status" };
        ArrayList<Object[]> data = new ArrayList<>();

         LinkedHashMap<String, java.util.List<String>> daySlots = new LinkedHashMap<>();
            daySlots.put("Monday",
                    java.util.Arrays.asList("09:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"));
            daySlots.put("Tuesday",
                    Arrays.asList("09:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"));
            daySlots.put("Wednesday",
                    Arrays.asList("09:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"));
            daySlots.put("Thursday",
                    Arrays.asList("09:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"));
            daySlots.put("Friday",
                    Arrays.asList("09:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"));

        try (Connection con = DBConnection.getConnection()) {

            ResultSet rsStudents =
                    con.prepareStatement("SELECT registration_id FROM students")
                            .executeQuery();

            while (rsStudents.next()) {

                int studentId = rsStudents.getInt(1);

                PreparedStatement psCourses = con.prepareStatement(
                        "SELECT c.course_id, c.course_code, c.course_title, c.day, c.time_slot " +
                        "FROM courses c JOIN registrations r ON c.course_id = r.course_id " +
                        "WHERE r.registration_id = ?");
                psCourses.setInt(1, studentId);

                ResultSet rsCourses = psCourses.executeQuery();

                while (rsCourses.next()) {

                    int courseId = rsCourses.getInt("course_id");
                    String code = rsCourses.getString("course_code");
                    String title = rsCourses.getString("course_title");
                    String day = rsCourses.getString("day");
                    String slot = rsCourses.getString("time_slot");

                    if (isSlotFree(con, studentId, day, slot)) {

                        data.add(new Object[] {
                                studentId, code, title, day, slot, "OK"
                        });

                    } else {
                        boolean fixed = false;

                        for (String alt : daySlots.get(day)) {
                            if (isSlotFree(con, studentId, day, alt)) {

                                PreparedStatement upd = con.prepareStatement(
                                        "UPDATE courses SET time_slot=? WHERE course_id=?");
                                upd.setString(1, alt);
                                upd.setInt(2, courseId);
                                upd.executeUpdate();

                                data.add(new Object[] {
                                        studentId, code, title, day, alt, "RESOLVED"
                                });
                                fixed = true;
                                break;
                            }
                        }

                        if (!fixed) {
                            data.add(new Object[] {
                                    studentId, code, title, day, slot, "CONFLICT"
                            });
                        }
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }

        JTable table = new JTable(data.toArray(new Object[0][]), cols);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }

    private boolean isSlotFree(Connection con, int studentId, String day, String slot) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM courses c " +
                "JOIN registrations r ON c.course_id = r.course_id " +
                "WHERE r.registration_id=? AND c.day=? AND c.time_slot=?");
        ps.setInt(1, studentId);
        ps.setString(2, day);
        ps.setString(3, slot);

        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) == 0;
    }
}