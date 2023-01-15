import javax.swing.*;
import java.awt.*;
import java.sql.Connection;


public class LoginGui {
    private final JFrame login= new GUI(300, 300).createFrame("Login" );
    private final JTextField username=new JTextField( 15);
    private final JPasswordField password = new JPasswordField(15);
    public Connection connection = new Driver().getConn();
    public LoginListener loginListener = new LoginListener(this);
    int studentId;
    LoginGui(){
        ImageIcon icon  = new ImageIcon("resources/LoginIcon.png");
        login.setIconImage(icon.getImage());

        JPanel userPanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel button = new JPanel();

        JLabel USER_LABEL = new JLabel("Username ");
        userPanel.add(USER_LABEL);
        userPanel.add(username);

        JLabel PASSWORD_LABEL = new JLabel("Password ");
        passwordPanel.add(PASSWORD_LABEL);
        passwordPanel.add(password);

        JButton loginButton = new JButton("Log in");
        loginButton.setBackground(Color.LIGHT_GRAY);
        button.add(loginButton);
        loginButton.addActionListener(loginListener);
        JPanel mainPanel = new JPanel();
        mainPanel.add(userPanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(button);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        login.setContentPane(mainPanel);
        login.setResizable(false);
        login.setVisible(true);

    }
    public void setVisibiliy(boolean visibiliy){
        login.setVisible(visibiliy);
    }

    public JFrame getLogin() {
        return login;
    }

    public String getUsername() {
        return username.getText();
    }
@Deprecated
    public String getPassword() {
        return password.getText();
    }
}


