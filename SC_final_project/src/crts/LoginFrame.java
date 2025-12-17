package crts;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class LoginFrame extends JFrame {
JTextField user = new JTextField(15);
JPasswordField pass = new JPasswordField(15);


public LoginFrame() {
setTitle("Login");
setLayout(new GridBagLayout());
GridBagConstraints c = new GridBagConstraints();
c.insets = new Insets(10,10,10,10);


c.gridx=0;c.gridy=0; add(new JLabel("Username / Email"),c);
c.gridx=1; add(user,c);
c.gridx=0;c.gridy=1; add(new JLabel("Password"),c);
c.gridx=1; add(pass,c);
JButton login = UIHelper.button("Login");
c.gridy=2;c.gridx=1; add(login,c);


login.addActionListener(e -> auth());
setSize(400,300);
setLocationRelativeTo(null);
setDefaultCloseOperation(EXIT_ON_CLOSE);
setVisible(true);
}


void auth() {
try (Connection con = DBConnection.getConnection()) {
PreparedStatement ps = con.prepareStatement(
"SELECT * FROM users WHERE (username=? OR email=?) AND password_hash=?"
);
ps.setString(1,user.getText());
ps.setString(2,user.getText());
ps.setString(3,PasswordUtil.hash(new String(pass.getPassword())));
ResultSet rs = ps.executeQuery();
if(rs.next()){
dispose();
if(rs.getString("role").equals("ADMIN")) new AdminDashboard();
else new StudentDashboard(rs.getInt("user_id"));
} else JOptionPane.showMessageDialog(this,"Invalid Login");
} catch(Exception e){ e.printStackTrace(); }
}
}