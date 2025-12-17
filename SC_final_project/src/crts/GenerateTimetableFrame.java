package crts;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class GenerateTimetableFrame extends JFrame {

    public GenerateTimetableFrame() {
        setTitle("Optimized Timetable (All Students)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIHelper.title("Generated Conflict-Free Timetable"), BorderLayout.NORTH);

        String[] cols = { "Student ID", "Course Code", "Course Title", "Day", "Time Slot" };
        ArrayList<Object[]> data = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            // 1️⃣ Fetch all students
            PreparedStatement psStudents = con.prepareStatement("SELECT registration_id FROM students");
            ResultSet rsStudents = psStudents.executeQuery();

            // 2️⃣ Build available slots map
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

            while (rsStudents.next()) {
                int studentId = rsStudents.getInt("registration_id");

                // 3️⃣ Fetch registered courses for this student
                PreparedStatement psCourses = con.prepareStatement(
                        "SELECT c.course_code, c.course_title, c.day, c.time_slot, c.available_seats " +
                                "FROM courses c JOIN registrations r ON c.course_id=r.course_id " +
                                "WHERE r.registration_id=?");
                psCourses.setInt(1, studentId);
                ResultSet rsCourses = psCourses.executeQuery();

                // Track which slots are already occupied
                Set<String> occupiedSlots = new HashSet<>();

                while (rsCourses.next()) {
                    String code = rsCourses.getString("course_code");
                    String title = rsCourses.getString("course_title");
                    String day = rsCourses.getString("day");
                    String slot = rsCourses.getString("time_slot");

                    String key = day + " " + slot;

                    // Check conflict
                    if (!occupiedSlots.contains(key)) {
                        data.add(new Object[] { studentId, code, title, day, slot });
                        occupiedSlots.add(key);
                    } else {
                        // Conflict found, try to assign another slot
                        boolean assigned = false;
                        for (String alternativeSlot : daySlots.get(day)) {
                            String altKey = day + " " + alternativeSlot;
                            if (!occupiedSlots.contains(altKey)) {
                                data.add(new Object[] { studentId, code, title, day, alternativeSlot + " (Moved)" });
                                occupiedSlots.add(altKey);
                                assigned = true;
                                break;
                            }
                        }
                        if (!assigned) {
                            // Couldn't resolve
                            data.add(new Object[] { studentId, code, title, day, slot + " (CONFLICT!)" });
                        }
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating timetable: " + e.getMessage());
            e.printStackTrace();
        }

        JTable table = new JTable(data.toArray(new Object[0][]), cols);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}
