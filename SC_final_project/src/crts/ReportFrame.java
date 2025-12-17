package crts;
import javax.swing.*;
import java.sql.*;
import java.util.*;

public class ReportFrame extends JFrame {

    public ReportFrame() {
        String[] cols={"Course","Enrolled"};
        ArrayList<Object[]> data=new ArrayList<>();

        try(Connection con=DBConnection.getConnection()){
            ResultSet rs=con.createStatement().executeQuery(
                "SELECT c.course_title, COUNT(r.course_id) " +
                "FROM courses c LEFT JOIN registrations r " +
                "ON c.course_id=r.course_id GROUP BY c.course_id"
            );
            while(rs.next())
                data.add(new Object[]{rs.getString(1),rs.getInt(2)});
        }catch(Exception e){}

        JTable t=new JTable(data.toArray(new Object[0][]),cols);
        add(new JScrollPane(t));
        setSize(500,300);
        setVisible(true);
    }
}
