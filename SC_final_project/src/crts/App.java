package crts;
import javax.swing.*;
public class App {
public static void main(String[] args) {
try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
catch (Exception ignored) {}
new LoginFrame();
}
}