package crts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CourseRegistrationFrame extends JFrame {

    private int registrationId;
    private JTable table;
    private DefaultTableModel model;

    public CourseRegistrationFrame(int registrationId) {
        this.registrationId = registrationId;

        setTitle("Course Registration");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(UIHelper.title("COURSE REGISTRATION"), BorderLayout.NORTH);

        String[] cols = {
                "ID", "Code", "Title", "Day",
                "Time Slot", "Room", "Seats", "Drop Deadline"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton registerBtn = UIHelper.button("Register Selected Course");
        JPanel bottom = new JPanel();
        bottom.add(registerBtn);
        add(bottom, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerCourse());

        loadCourses();
        setVisible(true);
    }

    // Load available courses
    private void loadCourses() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT * FROM courses WHERE available_seats > 0 " +
                            "ORDER BY course_code, day, time_slot");

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("day"),
                        rs.getString("time_slot"),
                        rs.getString("room"),
                        rs.getInt("available_seats"),
                        rs.getDate("drop_deadline")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Main registration
    private void registerCourse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }

        String courseCode = model.getValueAt(row, 1).toString();

        try (Connection con = DBConnection.getConnection()) {

            // Check prerequisite
            if (!prerequisiteSatisfied(con, courseCode)) {
                JOptionPane.showMessageDialog(this,
                        "Prerequisite not satisfied!\nYou must pass the prerequisite course first.");
                return;
            }

            // Get all course slots
            PreparedStatement psSlots = con.prepareStatement(
                    "SELECT course_id, day, time_slot, available_seats " +
                            "FROM courses WHERE course_code=?");
            psSlots.setString(1, courseCode);
            ResultSet rsSlots = psSlots.executeQuery();

            Set<Integer> courseIdsToRegister = new HashSet<>();
            StringBuilder conflictMsg = new StringBuilder();
            boolean canRegister = true;

            while (rsSlots.next()) {
                int courseId = rsSlots.getInt("course_id");
                String day = rsSlots.getString("day");
                String time = rsSlots.getString("time_slot");
                int seats = rsSlots.getInt("available_seats");

                // Already registered
                PreparedStatement already = con.prepareStatement(
                        "SELECT 1 FROM registrations WHERE registration_id=? AND course_id=?");
                already.setInt(1, registrationId);
                already.setInt(2, courseId);
                if (already.executeQuery().next()) {
                    conflictMsg.append(day).append(" ").append(time).append(" already registered\n");
                    canRegister = false;
                    continue;
                }

                // Time conflict
                PreparedStatement clash = con.prepareStatement(
                        "SELECT 1 FROM registrations r " +
                                "JOIN courses c ON r.course_id=c.course_id " +
                                "WHERE r.registration_id=? AND c.day=? AND c.time_slot=?");
                clash.setInt(1, registrationId);
                clash.setString(2, day);
                clash.setString(3, time);
                if (clash.executeQuery().next()) {
                    conflictMsg.append(day).append(" ").append(time).append(" time conflict\n");
                    canRegister = false;
                    continue;
                }

                // Seat check
                if (seats <= 0) {
                    conflictMsg.append(day).append(" ").append(time).append(" no seats\n");
                    canRegister = false;
                    continue;
                }

                courseIdsToRegister.add(courseId);
            }

            if (!canRegister) {
                JOptionPane.showMessageDialog(this, "Cannot register for all slots:\n" + conflictMsg);
                return;
            }

            // Register for all slots
            for (int courseId : courseIdsToRegister) {
                PreparedStatement insert = con.prepareStatement(
                        "INSERT INTO registrations(registration_id, course_id) VALUES (?, ?)");
                insert.setInt(1, registrationId);
                insert.setInt(2, courseId);
                insert.executeUpdate();

                PreparedStatement update = con.prepareStatement(
                        "UPDATE courses SET available_seats = available_seats - 1 WHERE course_id=?");
                update.setInt(1, courseId);
                update.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Successfully registered for " + courseCode + " (all slots)");
            loadCourses();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Prerequisite check
    private boolean prerequisiteSatisfied(Connection con, String courseCode) throws SQLException {

        // Get prerequisite
        PreparedStatement ps = con.prepareStatement(
                "SELECT prerequisite_code FROM course_prerequisites WHERE course_code=?");
        ps.setString(1, courseCode);
        ResultSet rs = ps.executeQuery();

        if (!rs.next())
            return true; // No prerequisite

        String prereqCode = rs.getString("prerequisite_code");

        // Check if student already passed prereq
        PreparedStatement check = con.prepareStatement(
                "SELECT 1 FROM registrations r " +
                        "JOIN courses c ON r.course_id=c.course_id " +
                        "WHERE r.registration_id=? AND c.course_code=?");
        check.setInt(1, registrationId);
        check.setString(2, prereqCode);
        return check.executeQuery().next();
    }
}
