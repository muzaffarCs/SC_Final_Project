package crts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CourseManagerFrame extends JFrame {

    private JTextField codeField = new JTextField(18);
    private JTextField titleField = new JTextField(18);
    private JTextField maxSeatsField = new JTextField(18);
    private JTextField availableSeatsField = new JTextField(18);

    private JComboBox<String> dayBox = new JComboBox<>(
            new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday"});

    private JComboBox<String> timeBox = new JComboBox<>(
            new String[]{
                    "09:05-10:35",
                    "10:45-12:15",
                    "12:25-13:55",
                    "14:15-15:45",
                    "16:00-17:30"
            });

    private JComboBox<String> roomBox = new JComboBox<>(
            new String[]{"201","237","235","F6","F9","BS Lab","Lab 205"});

    private JTable table;
    private DefaultTableModel model;
    private int selectedCourseId = -1;

    public CourseManagerFrame() {

        setTitle("Manage Courses");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(UIHelper.title("MANAGE COURSES"), BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Course Code:"));
        searchPanel.add(codeField);
        JButton searchBtn = UIHelper.button("Search");
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Code","Title","Day","Time","Room","Max","Available"};
        model = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Title"),c);
        c.gridx=1; form.add(titleField,c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Day"),c);
        c.gridx=1; form.add(dayBox,c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Time"),c);
        c.gridx=1; form.add(timeBox,c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Room"),c);
        c.gridx=1; form.add(roomBox,c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Max Seats"),c);
        c.gridx=1; form.add(maxSeatsField,c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Available"),c);
        c.gridx=1; form.add(availableSeatsField,c); y++;

        JPanel btns = new JPanel();
        JButton add = UIHelper.button("Add");
        JButton upd = UIHelper.button("Update");
        JButton del = UIHelper.button("Delete");
        btns.add(add); btns.add(upd); btns.add(del);

        c.gridx=0; c.gridy=y; c.gridwidth=2;
        form.add(btns,c);

        add(form, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> search());
        table.getSelectionModel().addListSelectionListener(e -> load());
        add.addActionListener(e -> addCourse());
        upd.addActionListener(e -> updateCourse());
        del.addActionListener(e -> deleteCourse());

        setVisible(true);
    }

    private void search(){
        model.setRowCount(0);
        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM courses WHERE course_code=?");
            ps.setString(1, codeField.getText().trim());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("day"),
                        rs.getString("time_slot"),
                        rs.getString("room"),
                        rs.getInt("max_seats"),
                        rs.getInt("available_seats")
                });
            }
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void load(){
        int r = table.getSelectedRow();
        if(r==-1) return;
        selectedCourseId = (int) model.getValueAt(r,0);
        titleField.setText(model.getValueAt(r,2).toString());
        dayBox.setSelectedItem(model.getValueAt(r,3));
        timeBox.setSelectedItem(model.getValueAt(r,4));
        roomBox.setSelectedItem(model.getValueAt(r,5));
        maxSeatsField.setText(model.getValueAt(r,6).toString());
        availableSeatsField.setText(model.getValueAt(r,7).toString());
    }

    private void addCourse(){
        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO courses(course_code,course_title,day,time_slot,room,max_seats,available_seats) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, codeField.getText().trim());
            ps.setString(2, titleField.getText().trim());
            ps.setString(3, dayBox.getSelectedItem().toString());
            ps.setString(4, timeBox.getSelectedItem().toString());
            ps.setString(5, roomBox.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(maxSeatsField.getText()));
            ps.setInt(7, Integer.parseInt(availableSeatsField.getText()));
            ps.executeUpdate();
            search();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void updateCourse(){
        if(selectedCourseId==-1) return;
        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                "UPDATE courses SET course_title=?,day=?,time_slot=?,room=?,max_seats=?,available_seats=? WHERE course_id=?");
            ps.setString(1, titleField.getText());
            ps.setString(2, dayBox.getSelectedItem().toString());
            ps.setString(3, timeBox.getSelectedItem().toString());
            ps.setString(4, roomBox.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(maxSeatsField.getText()));
            ps.setInt(6, Integer.parseInt(availableSeatsField.getText()));
            ps.setInt(7, selectedCourseId);
            ps.executeUpdate();
            search();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void deleteCourse(){
        if(selectedCourseId==-1) return;
        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM courses WHERE course_id=?");
            ps.setInt(1, selectedCourseId);
            ps.executeUpdate();
            search();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
}
