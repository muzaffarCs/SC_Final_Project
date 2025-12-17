package crts;
import javax.swing.*;
import java.sql.*;
import java.util.*;

public class TimetableFrame extends JFrame {

    public TimetableFrame(int regId) {
        String[] cols={"Course","Day","Time"};
        ArrayList<Object[]> data=new ArrayList<>();

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement(
                "SELECT c.course_title,c.day,c.time_slot FROM courses c " +
                "JOIN registrations r ON c.course_id=r.course_id " +
                "WHERE r.registration_id=?"
            );
            ps.setInt(1,regId);
            ResultSet rs=ps.executeQuery();
            while(rs.next())
                data.add(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3)});
        }catch(Exception e){}

        JTable t=new JTable(data.toArray(new Object[0][]),cols);
        add(new JScrollPane(t));
        setSize(500,300);
        setVisible(true);
    }
}
