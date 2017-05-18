/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlnavigator;

/**
 ** @author Swapnil * 28-01-2016
 */
import java.sql.*;
import java.util.*;
import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sqlnavigator.Error_Message.error;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.web.*;
import netscape.javascript.JSObject;

//Apache poi imports 
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.*;

//XML Imports
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

class Table {

    String Field;
    String Type;
    String Null;
    String Key;
    String Extra;

    Table(String F, String T, String N, String K, String E) {
        Field = F;
        Type = T;
        Null = N;
        Key = K;
        Extra = E;
    }
    
    public Table(Table t) {
        Field = t.Field;
        Type = t.Type;
        Null = t.Null;
        Key = t.Key;
        Extra = t.Extra;
    }
}

class SQLUser implements Serializable {

    String username;
    String password;
    public String email;
    public Vector<UserAccountInformation> accounts;

    public SQLUser(String user, String pass, String em, Vector<UserAccountInformation> accs) {
        username = user;
        password = pass;
        email = em;
        accounts = accs;
    }

    public SQLUser(SQLUser user) {
        username = user.username;
        password = user.password;
        accounts = user.accounts;
        email = user.email;
    }

    public void copy(SQLUser user) {
        username = user.username;
        password = user.password;
        accounts = user.accounts;
    }

    public void copyFrom(sqlnavigator.UserManager.SQLUsers users) {
        username = users.username;
        password = users.password;
        email = users.email;
        copyaccounts(users);
    }

    public void copyaccounts(sqlnavigator.UserManager.SQLUsers user) {
        Vector<UserAccountInformation> accs = new Vector<>();
        for (sqlnavigator.UserManager.UserAccountInformation g : user.accounts) {
            String usern = g.username;
            String pass = g.password;
            sqlnavigator.UserManager.DriverData d = g.driver;
            DriverData dr = new DriverData(d.Driver, d.url, d.file, d.title);
            dr.setEnabled(d.isEnabled());
            dr.setIntegratedSecurity(d.windowsAuthentication());
            accs.add(new UserAccountInformation(usern, pass, dr));
        }
        accounts = accs;
    }

    public void setData(String user, String Pass, String em) {
        username = user;
        password = Pass;
        email = em;
    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}

class UserAccountInformation implements Serializable {

    public String username;
    public String password;
    public DriverData driver;

    public UserAccountInformation(String user, String pass, DriverData dr) {
        username = user;
        password = pass;
        driver = dr;
    }

    void setData(String user, String Pass) {
        username = user;
        password = Pass;
    }

    @Override
    public String toString() {
        return driver.title;
    }
}

class DriverData implements Serializable {

    public String Driver;
    public String url;
    public String file;
    public String title;
    public boolean enabled;
    public boolean integratedSecurity;

    public DriverData(String d, String u, String f, String t) {
        Driver = d;
        url = u;
        file = f;
        title = t;
        enabled = false;
        integratedSecurity = false;
    }

    public DriverData(DriverData d) {
        Driver = d.Driver;
        url = d.url;
        file = d.file;
        title = d.title;
        enabled = d.enabled;
        integratedSecurity = d.integratedSecurity;
    }

    public void setData(String d, String u, String t) {
        Driver = d;
        url = u;
        title = t;
    }

    public void setFileInfo(String f) {
        file = f;
    }

    public void setTitle(String t) {
        title = t;
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
    }

    public void setIntegratedSecurity(boolean set) {
        integratedSecurity = set;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean windowsAuthentication() {
        return integratedSecurity;
    }

    @Override
    public String toString() {
        return title;
    }
}

public class SQLNavigator {

    public static void main(String[] args) {
        //Now these will be output streams that will capture the error messages generated
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(baos);
        //Now the frame that holds the output
        JFrame frame = new JFrame();
        frame.setSize(400, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //
        EventQueue.invokeLater(() -> {
            //System.out.println("Inside Event Queue");
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    frame.setVisible(true);
                    error(e, frame);
                }
                //System.out.println(new SQLNavigator().getURL());
                new SQLIntro().setVisible(true);
                //new sqlnavigator.UserManager().setVisible(true);
                //                  File currentDirectory = new File(new File(".").getAbsolutePath());
                //                  System.out.println(currentDirectory.getAbsolutePath());
                //                  System.out.println(currentDirectory.getCanonicalPath());
                /*FileOutputStream fs = new FileOutputStream("Users.ser");
                ObjectOutputStream os = new ObjectOutputStream(fs);
                Vector <UserAccountInformation> user = new Vector<>();

                user.add(new UserAccountInformation("root","swapnil",new DriverData("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/","mysql.txt","MySQL")));
                user.add(new UserAccountInformation("root", "swapnil",new DriverData("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://localhost:1443;instance=SQLEXPRESS;databaseName=master;integratedSecurity=true;","msaccess.txt","Microsoft SQL Server")));
                user.add(new UserAccountInformation("SYSTEM","swapnil",new DriverData("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:SYSTEM/user@localhost:1521:xe","oracle.txt","Oracle")));
                SQLUser useri = new SQLUser("swapnil31","swapnil@123","swapnilbhattacharjee187@gmail.com",user);
                SQLUser userj = new SQLUser("arrkdp","alig","swapnilbhattacharjee187@gmail.com",user);
                os.writeObject(useri);
                os.writeObject(userj);
                os.close();
                fs.close();*/
 /*FileOutputStream fs = new FileOutputStream("DriverData.ser");
                ObjectOutputStream os = new ObjectOutputStream(fs);
                Vector <DriverData> drivers = new Vector<>();
                drivers.add(new DriverData("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/","mysql.txt","MySQL"));
                drivers.add(new DriverData("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://localhost:1443;instance=SQLEXPRESS;databaseName=master;","msaccess.txt","Microsoft SQL Server"));
                drivers.add(new DriverData("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:SYSTEM/user@localhost:1521:xe","oracle.txt","Oracle"));
                os.writeObject(drivers);
                os.close();
                fs.close();*/
            } catch (Exception e) {
                frame.setVisible(true);
                error(e, frame);
                e.printStackTrace();
            }
        });

    }

    public String getPath() throws IOException {
        File currentDirectory = new File(new File(".").getAbsolutePath());
        String path = currentDirectory.getCanonicalPath();
        String paths[] = path.split("\\\\");
        String shortHandPath = "";
        int i = 0;
        while (!paths[i].equals("SQLNavigator")) {
            shortHandPath += paths[i] + "\\";
            i++;
        }
        return shortHandPath;
    }

    public String getURL() throws IOException {
        File currentDirectory = new File(new File(".").getAbsolutePath());
        String path = currentDirectory.getCanonicalPath();
        String paths[] = path.split("\\\\");
        String proto = "file:///";
        String url = "";
        int i = 0;
        while (!paths[i].equals("SQLNavigator")) {
            url += paths[i] + "/";
            i++;
        }
        return proto + url;
    }
}

class SQLIntro extends JFrame {

    JButton Create = new JButton();
    JButton Update = new JButton();
    JButton View = new JButton();
    JButton Login = new JButton("Login");
    DefaultListModel listmodel;
    JComboBox<UserAccountInformation> Database;
    UserAccountInformation currUser;
    Vector<UserAccountInformation> accounts = new Vector<>();
    Vector<DriverData> fetched_drivers;
    public SQLUser user = new SQLUser("None", "None", "None", accounts);
    SQLViewer view;
    ProcessBuilder g;
    //Now these will be output streams that will capture the error messages generated
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    //This is a new addition to my code that finds out the absolute path to it's parent directory to correctly fetch all the resources
    String path;

    public SQLIntro() throws Exception {
        SQLIntr();
    }

    public void SQLIntr() throws Exception {
        path = new SQLNavigator().getPath();

        //fetched_drivers = fetch_DriverData();
        Database = new JComboBox<>();
        DriverData sel = new DriverData("", "", "", "Select");
        UserAccountInformation select = new UserAccountInformation("", "", sel);
        Database.addItem(select);
        Database.setEnabled(false);
        Create.setEnabled(false);
        Update.setEnabled(false);
        View.setEnabled(false);
//        getContentPane().setLayout(new FlowLayout());
        JPanel pan = new JPanel();
        JMenuBar IntrMenu = new JMenuBar();
        IntrMenu.setPreferredSize(new Dimension(275, 20));
        add(IntrMenu, BorderLayout.NORTH);
        pan.add(Create);
        pan.add(Update);
        pan.add(View);
        Create.setPreferredSize(new Dimension(200, 120));
        Create.setIcon(new ImageIcon(path + "SQLNavigator\\Icons\\create.png"));
        Update.setPreferredSize(new Dimension(200, 120));
        Update.setIcon(new ImageIcon(path + "SQLNavigator\\Icons\\update.png"));
        View.setPreferredSize(new Dimension(200, 120));
        View.setIcon(new ImageIcon(path + "SQLNavigator\\Icons\\view.png"));
        add(pan);
        pan.setPreferredSize(new Dimension(650, 1200));
        JMenu File = new JMenu("File");
        JMenu Passwords = new JMenu("Drivers");
        JMenu Help = new JMenu("Help");
        JMenuItem New = new JMenuItem("New");
        JMenuItem Exit = new JMenuItem("Exit");
        JMenuItem Edit = new JMenuItem("Edit Drivers");
        JMenuItem Add = new JMenuItem("Add New Drivers");
        JMenuItem Users = new JMenuItem("User Manager");
        Edit.setEnabled(false);
        Add.addActionListener((ActionEvent ev) -> {
            new NewDriver(this, true, fetched_drivers).setVisible(true);
        });
        Edit.addActionListener((ActionEvent ev) -> {
            new Edit_Drivers(this, true, accounts).setVisible(true);
            //Database.removeAllItems();
            //for(UserAccountInformation d : accounts)
            //Database.addItem(d);
        });
        Users.addActionListener((ActionEvent) -> {
            new sqlnavigator.UserManager().setVisible(true);
        });
        Database.addActionListener((ActionEvent ev) -> {
            currUser = Database.getItemAt(Database.getSelectedIndex());
        });

        Exit.addActionListener((ActionEvent ev) -> {
            System.exit(0);
        });

        Login.addActionListener((Actionevent) -> {
            if (Login.getText().equals("Logout")) {
                int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to Logout?", "Logout Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    user = new SQLUser("None", "None", "None", accounts);
                    Database.setEnabled(false);
                    Database.removeAllItems();
                    Database.addItem(select);
                    Create.setEnabled(false);
                    Update.setEnabled(false);
                    View.setEnabled(false);
                    Edit.setEnabled(false);
                    Login.setText("Login");
                    this.setTitle("SQL Navigator - Home");
                }
            } else {
                try {
                    new Login(this, true, user).setVisible(true);
                    //System.out.print(user);
                    if (!user.username.equals("None")) {
                        accounts = user.accounts;
                        int i = 0;
                        for (UserAccountInformation ui : accounts) {
                            if (ui.driver.isEnabled()) {
                                Database.addItem(ui);
                                i++;
                            }
                        }
                        if (i == 0) {
                            Database.addItem(select);
                            Database.setEnabled(false);
                        } else {
                            Database.removeItemAt(0);
                            Database.setEnabled(true);
                            Create.setEnabled(true);
                            Update.setEnabled(true);
                            View.setEnabled(true);
                        }
                        Edit.setEnabled(true);
                        Login.setText("Logout");
                        this.setTitle("SQL Navigator - " + user.username);
                    }
                } catch (IOException e) {
                    error(e, this);
                } catch (Exception e) {
                    error(e, this);
                }
            }

        });
        JLabel label = new JLabel("   Choose the Database to use : ");
        File.add(New);
        File.add(Exit);
        Passwords.add(Add);
        Passwords.add(Edit);
        Passwords.add(Users);
        IntrMenu.add(File);
        IntrMenu.add(Passwords);
        IntrMenu.add(Help);
        IntrMenu.add(label);
        IntrMenu.add(Database);
        IntrMenu.add(Login);
        /*Database.addActionListener((ActionEvent AE) -> {
                driver = Database.getItemAt(Database.getSelectedIndex());

        });*/
        
        Create.addActionListener((ActionEvent ae) -> {
            new SQL_Create(currUser).setVisible(true);
        });
        
        View.addActionListener((ActionEvent Event) -> {
            try {
                view = new SQLViewer(currUser, "");
                view.setVisible(true);
            } catch (com.microsoft.sqlserver.jdbc.SQLServerException e) {
                JOptionPane.showMessageDialog(this,
                        "An error is preventing the Program from connecting to the Microsoft SQL Server. It may be \n"
                        + "due to the following errors :\n"
                        + " 1. " + currUser + " is not installed on your System\n"
                        + " 2. Incorrect IP address or JDBC URL\n"
                        + " 3. Incorrect or missing port number in JDBC URL\n"
                        + " 4. Server is down or isn't accepting TCP/IP connections\n"
                        + " 5. DB has run out of connections or a firewall or proxy is preventing the connection\n"
                        + "Refer to the Help section for troubleshooting information",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            } catch (com.mysql.jdbc.CommunicationsException e) {
                JOptionPane.showMessageDialog(this,
                        "An error is preventing the Program from connecting to the mySQL Server. It may be \n"
                        + "due to the following errors :\n"
                        + " 1. " + currUser + " is not installed on your System\n"
                        + " 2. Incorrect IP address or JDBC URL\n"
                        + " 3. Incorrect or missing port number in JDBC URL\n"
                        + " 4. Server is down or isn't accepting TCP/IP connections\n"
                        + " 5. DB has run out of connections or a firewall or proxy is preventing the connection\n"
                        + "Refer to the Help section for troubleshooting information",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                error(e, this);
            } catch (SQLException s) {
                error(s, this);
            } catch (Exception e) {
                error(e, this);
            }
        });
        Update.addActionListener((ActionEvent ev) -> {
            try {

                new SQLUpdate(currUser).setVisible(true);
            } catch (com.microsoft.sqlserver.jdbc.SQLServerException e) {
                JOptionPane.showMessageDialog(this,
                        "An error is preventing the Program from connecting to the Microsoft SQL Server. It may be \n"
                        + "due to the following errors :\n"
                        + " 1. Microft SQL Server is not installed on your System\n"
                        + " 2. Incorrect IP address or JDBC URL\n"
                        + " 3. Incorrect or missing port number in JDBC URL\n"
                        + " 4. Server is down or isn't accepting TCP/IP connections\n"
                        + " 5. DB has run out of connections or a firewall or proxy is preventing the connection\n"
                        + "Refer to the Help section for troubleshooting information",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);

            } catch (com.mysql.jdbc.CommunicationsException e) {
                JOptionPane.showMessageDialog(this,
                        "An error is preventing the Program from connecting to the mySQL Server. It may be \n"
                        + "due to the following errors :\n"
                        + " 1. mySQLServer is not installed on your System\n"
                        + " 2. Incorrect IP address or JDBC URL\n"
                        + " 3. Incorrect or missing port number in JDBC URL\n"
                        + " 4. Server is down or isn't accepting TCP/IP connections\n"
                        + " 5. DB has run out of connections or a firewall or proxy is preventing the connection\n"
                        + "Refer to the Help section for troubleshooting information",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException s) {
                error(s, this);
            } catch (IOException e) {
                error(e, this);
            } catch (Exception e) {
                error(e, this);
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
                //save_DriverData();
            }
        });
        setSize(1220, 350);
        setTitle("SQL Navigator - Home");
        //this.setUndecorated(true);
        setIconImage(new ImageIcon(path + "SQLNavigator\\Icons\\Icon.png").getImage());
        setResizable(false);
    }

    /*public Vector<DriverData> fetch_DriverData() throws IOException, ClassNotFoundException {
        FileInputStream fs = new FileInputStream(path + "SQLNavigator\\DriverData.ser");
        ObjectInputStream os = new ObjectInputStream(fs);
        Vector<sqlnavigator.DriverData> arr = new Vector<>();
        while (fs.available() > 0) {
            arr.add(new DriverData((DriverData) os.readObject()));
        }
        os.close();
        fs.close();
        return arr;
    }

    public void save_DriverData() {
        try {
            FileOutputStream fs = new FileOutputStream(path + "SQLNavigator\\DriverData.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            for (DriverData g : fetched_drivers) {
                os.writeObject(g);
            }
            os.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}

class Login extends JDialog {

    SQLNavigator t = new SQLNavigator();
    Vector<SQLUser> accounts = new Vector<>();
    SQLUser currentUser;
    String user, pass;
    boolean exists = false;

    /**
     * Creates new form Login
     */
    public Login(java.awt.Frame parent, boolean modal, SQLUser currUser) throws IOException, Exception {
        super(parent, modal);
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });

        currentUser = currUser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() throws Exception {
        getInfo();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Password = new javax.swing.JPasswordField();
        LoginButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        Status = new javax.swing.JLabel();

        jLabel1.setText("Username ");

        jLabel2.setText("Password");

        LoginButton.setText("Login");

        CloseButton.setText("Close");
        LoginButton.addActionListener((ActionEvent ev) -> {
            user = username.getText();
            pass = new String(Password.getPassword());
            if (accounts.size() > 0) {
                for (SQLUser u : accounts) {
                    if (user.equals(u.username) && pass.equals(u.password)) {
                        exists = true;
                        currentUser.copy(u);
                        //System.out.println(currentUser);
                        setVisible(false);
                        try {
                            finalize();
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No Users have been created on this system", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
            if (!exists) {
                JOptionPane.showMessageDialog(this, "The username and password combination doesn't exist!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        CloseButton.addActionListener((ActionEvent ev) -> {
            setVisible(false);
            try {
                finalize();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(LoginButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(CloseButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(18, 18, 18)
                                                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(Status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(LoginButton)
                                        .addComponent(CloseButton))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold> 

    void getInfo() throws IOException, Exception {
        File f = new File(t.getPath() + "SQLNavigator\\Users.ser");
        if (f.exists() && !f.isDirectory()) {
            FileInputStream fis = new FileInputStream(t.getPath() + "SQLNavigator\\Users.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                accounts.add((SQLUser) ois.readObject());
            }
        } else {
            FileWriter fw = new FileWriter(f);
        }
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton LoginButton;
    private javax.swing.JPasswordField Password;
    private javax.swing.JLabel Status;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField username;
    // End of variables declaration                   
}

class Edit_Drivers extends JDialog {

    JButton jButton1;
    JButton jButton2;
    JCheckBox jCheckBox1;
    JComboBox<UserAccountInformation> jComboBox1;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    JLabel jLabel6;
    JTextField jTextField1;
    JTextField jTextField2;
    JTextField jTextField3;
    JTextField jTextField4;
    Vector<UserAccountInformation> drivers;
    DriverData selected;

    // End of variables declaration   
    /**
     * Creates new form NewJDialog
     */
    public Edit_Drivers(java.awt.Frame parent, boolean modal, Vector<UserAccountInformation> recieved) {
        super(parent, modal);
        drivers = recieved;
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
        setTitle("Edit Drivers");
    }

    public void initComponents() {

        jLabel1 = new JLabel();
        jComboBox1 = new JComboBox();
        jLabel2 = new JLabel();
        jTextField1 = new JTextField();
        jLabel3 = new JLabel();
        jTextField2 = new JTextField();
        jLabel4 = new JLabel();
        jTextField3 = new JTextField();
        jLabel5 = new JLabel();
        jTextField4 = new JTextField();
        jCheckBox1 = new JCheckBox();
        jButton1 = new JButton();
        jButton2 = new JButton();

        //setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jLabel1.setText("Choose a database to Edit");

        for (UserAccountInformation g : drivers) {
            jComboBox1.addItem(g);
        }

        jLabel2.setText("Title : ");
        jLabel3.setText("Driver Name : ");
        jLabel4.setText("Driver URL : ");
        jLabel5.setText("File : ");
        jCheckBox1.setText("Check this box, if you wish to edit the protected fields");
        jButton1.setText("Save");
        jButton2.setText("Close");

        jTextField2.setEditable(false);
        jTextField3.setEditable(false);
        jTextField4.setEditable(false);

        jButton2.addActionListener((ActionEvent ev) -> {
            setVisible(false);
        });

        jCheckBox1.addItemListener((ItemEvent ev) -> {
            if (jCheckBox1.isSelected()) {
                jTextField2.setEditable(true);
                jTextField3.setEditable(true);
            } else {
                jTextField2.setEditable(false);
                jTextField3.setEditable(false);
            }
        });

        jComboBox1.addActionListener((ActionEvent ev) -> {
            selected = jComboBox1.getItemAt(jComboBox1.getSelectedIndex()).driver;
            jTextField1.setText(selected.title);
            jTextField2.setText(selected.Driver);
            jTextField3.setText(selected.url);
            jTextField4.setText(selected.file);
        });

        jButton1.addActionListener((ActionEvent ev) -> {
            if (isNull(jTextField1.getText(), jTextField2.getText(), jTextField3.getText(), jTextField2.getText())) {
                JOptionPane.showMessageDialog(this, "An error is preventing the program from updating the values for Driver attributes."
                        + "\nIt may be due to the following reasons : \n"
                        + "  1. One or more mandatory Fileds have been left empty/blank. Enter the data for those fields \n"
                        + "  2. No driver has been selected. Selected a driver and then edit the fields",
                        "Error : Updatation Failed !", JOptionPane.ERROR_MESSAGE);
            } else {
                selected.setData(jTextField2.getText(), jTextField3.getText(), jTextField1.getText());
                JOptionPane.showMessageDialog(this, "The Value has been saved", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);
            }
            revalidate();
        });

        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(25, 25, 25)
                                                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(50, 50, 50))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(18, 18, 18)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jTextField3)
                                                                                .addComponent(jTextField4)
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox1)
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }

    public boolean isNull(String field1, String field2, String field3, String field4) {
        return (field1.isEmpty() || field2.isEmpty() || field3.isEmpty() || field4.isEmpty());
    }

    //End of class
}

class NewDriver extends JDialog {

    JButton jButton1;
    JButton jButton2;
    JButton jButton3;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    JLabel jLabel6;
    JLabel jLabel7;
    JLabel jLabel8;
    JLabel jLabel9;
    JTextField jTextField1;
    JTextField jTextField2;
    JTextField jTextField3;
    JTextField jTextField4;
    JTextField jTextField5;
    JFileChooser FileChooser;
    ProcessBuilder process;
    Process pr;
    int errCode;

    //to save the pathname of the Driver File
    String driver_pathname;

    //The Existing Driver List
    Vector<DriverData> drivers = new Vector<>();

    /**
     * Creates new form NewDriver
     */
    public NewDriver(java.awt.Frame parent, boolean modal, Vector<DriverData> recieved) {
        super(parent, modal);
        drivers = recieved;
        addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent ev) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
        setTitle("Add New Driver");
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    public void initComponents() {

        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jButton1 = new JButton();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jTextField2 = new JTextField();
        jTextField3 = new JTextField();
        jTextField4 = new JTextField();
        jTextField5 = new JTextField();
        jButton2 = new JButton();
        jButton3 = new JButton();
        FileChooser = new JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Select the Driver (.jar) file");
        jTextField1.setText("");

        jButton1.setText("...");
        jButton1.addActionListener((ActionEvent ev) -> {
            int result = FileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                driver_pathname = FileChooser.getSelectedFile().getAbsolutePath();
                jTextField1.setText(driver_pathname);
            }
        });

        jLabel2.setText("Enter thefollowing information about the Databse Connectivity (JDBC) Driver.");
        jLabel3.setText("Fields marked with asterisk (*) are mandatory.");
        jLabel4.setText("* Title :");
        jLabel5.setText("* Driver Name : ");
        jLabel6.setText("* Driver URL : ");
        jLabel7.setText("* File Name :");
        jLabel8.setText("Note : The name of the User Information File you select cannot be");
        jLabel9.setText("changed later. It can only be specified at the time of Adding the Driver.");

        jButton2.setText("Add Driver");
        jButton2.addActionListener((ActionEvent ev) -> {
            if (isNull(jTextField1.getText(), jTextField2.getText(), jTextField3.getText(), jTextField4.getText(), jTextField5.getText())) {
                JOptionPane.showMessageDialog(this,
                        "One or more mandatory fields have been left empty. Enter the data for those fields",
                        "Blank Field Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {

                drivers.add(new DriverData(jTextField3.getText(), jTextField4.getText(), jTextField5.getText(), jTextField2.getText()));
                try {
                    process = new ProcessBuilder("cmd.exe", "/C COPY \"" + jTextField1.getText() + "\" \"" + new SQLNavigator().getPath() + "NetBeansProjects\\SQLNavigator\\dist\\lib\\\"");
                    pr = process.start();
                    errCode = pr.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Some error occured that prevented the addition of the driver!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NewDriver.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (errCode != 0) {
                    JOptionPane.showMessageDialog(this, "Some error occured that prevented the addition of the driver", "Error", JOptionPane.ERROR_MESSAGE);
                    drivers.removeElementAt(drivers.size() - 1);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The Driver for " + jTextField2.getText() + " \nhas been added to our list\n of drivers", "Add Confirmation", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        jButton3.setText("Close");
        jButton3.addActionListener((ActionEvent ev) -> {
            setVisible(false);
        });

        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel8)
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel3)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jLabel7))
                                                .addGap(63, 63, 63)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                                                        .addComponent(jTextField3)
                                                        .addComponent(jTextField4)
                                                        .addComponent(jTextField5))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3)
                                .addGap(64, 64, 64))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jButton1))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jLabel2)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jLabel3)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(jLabel4))
                                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel5))
                                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel6))
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel7)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2)
                                        .addComponent(jButton3))
                                .addContainerGap(27, Short.MAX_VALUE))
        );
        pack();
    }

    public boolean isNull(String field1, String field2, String field3, String field4, String field5) {
        return (field1.isEmpty() || field2.isEmpty() || field3.isEmpty() || field4.isEmpty() || field5.isEmpty());
    }

    // End of variables declaration                   
}

class SQLViewer extends JFrame {

    Connection con;
    String url;
    String driver;
    String user;
    String pass;
    JPanel Panel;
    JComboBox<String> db;
    JComboBox<String> tables;
    String selected_table;
    JScrollPane st;
    DefaultListModel listmodel;
    Dimension d;
    final JFXPanel label = new JFXPanel();
    ;
    private WebEngine engine;
    JMenuBar ViewMenu = new JMenuBar();
    JButton maketable;
    JButton toHTML;
    String Current;
    String printed_table;
    //Now these will be output streams that will capture the error messages generated
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);

    //These to find out the paths of the current Path and URL
    String path = new SQLNavigator().getPath();
    String Lurl = new SQLNavigator().getURL();

    ;
    
    public SQLViewer(UserAccountInformation u) throws SQLException, Exception {
        DriverData d = u.driver;
        url = d.url;
        driver = d.Driver;
        url = d.url;
        user = u.username;
        pass = u.password;
    }

    public SQLViewer(UserAccountInformation u, String path) throws SQLException, Exception {
        DriverData d = u.driver;
        url = d.url;
        driver = d.Driver;
        url = d.url;
        user = u.username;
        pass = u.password;
        SQLV();
    }

    private void createScene() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                WebView view = new WebView();
                engine = view.getEngine();
                label.setScene(new Scene(view));
            }
        });
    }

    public void SQLV() throws SQLException, Exception {
        createScene();
        Vector<String> dbase = Give_Database();
        db = new JComboBox<>();
        for (String g : dbase) {
            db.addItem(g);
        }

        db.addActionListener((ActionEvent AE) -> {
            try {
                System.out.println("Inside the ListListener");
                Current = db.getItemAt(db.getSelectedIndex());
                Vector<String> table = Give_Tables(Current);
                setTitle("SQL Navigator - View - " + Current + "/");
                if (table.contains("No Tables")) {
                    tables.setEnabled(false);
                    maketable.setEnabled(false);
                    toHTML.setEnabled(false);
                } else {
                    tables.setEnabled(true);
                    maketable.setEnabled(true);
                    toHTML.setEnabled(true);
                }
                tables.removeAllItems();
                for (String g : table) {
                    tables.addItem(g);
                }
            } catch (SQLException e) {
                error(e, this);
            }
        }
        );
//      getContentPane().setLayout(new FlowLayout());
        Panel = new JPanel();
        ViewMenu.add(db);
        tables = new JComboBox<>();
        tables.addItem("Select");

        maketable = new JButton("Go");
        toHTML = new JButton("Port");

        tables.setEnabled(false);
        maketable.setEnabled(false);
        toHTML.setEnabled(false);

        tables.addActionListener(
                (ActionEvent AE) -> {
                    selected_table = tables.getItemAt(tables.getSelectedIndex());
                    System.out.println(selected_table);
                    setTitle("SQL Navigator - View - " + Current + "/" + (selected_table == null ? "" : selected_table));
                }
        );
        ViewMenu.add(tables);
        maketable.addActionListener((ActionEvent e) -> {
            try {
                selected_table = tables.getItemAt(tables.getSelectedIndex());
                printed_table = new String(selected_table);
                String html = write_to_html(desc_table(selected_table), selected_table);
                if (html.equalsIgnoreCase("Empty Set")) {
                    JOptionPane.showMessageDialog(this,
                            "Empty Set! No value is stored in the Table yet!",
                            "Empty Set Error",
                            JOptionPane.ERROR_MESSAGE);
                    printed_table = null;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            engine.load(Lurl + "SQLNavigator/temp/temp.html");
                        }
                    });
                } else {
                    System.out.println(html);
                    Platform.runLater(() -> {

                        engine.load(Lurl + "SQLNavigator/temp/temp.html");
                        engine.getLoadWorker().stateProperty().addListener(
                                (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                                    if (newState == Worker.State.SUCCEEDED) {
                                        engine.executeScript("primaryKey = -1");
                                        engine.executeScript("unique = []");
                                        engine.executeScript("var table = document.getElementById(\"myTable\");primaryKey = parseInt(table.getAttribute(\"data-primary\"));");
                                        engine.executeScript("getUnique()");
                                        engine.executeScript("addToClicked()");
                                        engine.executeScript("checkForReadOnly()");
                                        JSObject win = (JSObject) engine.executeScript("window");
                                        win.setMember("QuickUpdate", new QuickUpdate(con, this));
                                        //engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                                    }
                                }
                        );
                    });
                }
            } catch (IOException ex) {
                error(ex, this);
            } catch (SQLException ex) {
                error(ex, this);
            } catch (Exception ex) {
                error(ex, this);
            }
        });
        //maketable.setIcon(new ImageIcon("C:\Users\SWAPNIL\Documents\NetBeansProjects\SQLNavigatorupdate.ico"));
        ViewMenu.add(maketable);

        toHTML.addActionListener((ActionEvent ev) -> {
            if (printed_table == null) {
                JOptionPane.showMessageDialog(this,
                        "Select a table before requesting this action. Go back  and \n"
                        + "\t 1. Print a table on the application screen by clicking the Go Button \n"
                        + "\t 2. Then come back and click this button", "Null Selection Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    new WebPort(this, true, desc_table(selected_table), selected_table, con, path, this).setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    error(ex, this);
                }
            }
        });
        System.out.println("This works");
        ViewMenu.add(toHTML);
        System.out.println("So does this");
        add(ViewMenu, BorderLayout.NORTH);
        Dimension d = label.getPreferredSize();
        d.height = this.getWidth() - 10;
        d.width = this.getHeight() - 10;
        label.setPreferredSize(d);
        this.getContentPane().add(label);
        this.setIconImage(new ImageIcon(path + "SQLNavigator\\Icons\\Icon.png").getImage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
        setTitle("SQL Navigator - View ");
        setSize(900, 700);
    }

    public Vector<String> Give_Tables(String db) throws SQLException {
        Vector<String> arr = new Vector<>();
        Statement st = con.createStatement();
        System.out.println("use `" + db + "`;");
        st.executeUpdate("use " + db + ";");
        ResultSet res = st.executeQuery("show tables;");
        if (res.next() == false) {
            arr.add("No Tables");

        } else {
            do {
                String table = res.getString(1);
                arr.add(table);
            } while (res.next());

        }
        return arr;
    }

    public Vector<String> Give_Database() throws SQLException, Exception {
        Class.forName(driver).newInstance();
        con = DriverManager.getConnection(url, user, pass);
        Statement st = con.createStatement();
        ResultSet res = st.executeQuery("show databases;");
        Vector<String> arr = new Vector<>();
        String db = null;
        if (res.next() == false) {
            arr.add("No Databases");
            return arr;
        } else {
            do {
                String DB = res.getString("Database");
                arr.add(DB);
            } while (res.next());
            return arr;
        }

    }

    public ArrayList<Table> desc_table(String table) throws SQLException, Exception {
        Statement st = con.createStatement();
        ResultSet res = st.executeQuery("desc `" + table + "`;");
        res.getMetaData();
        /*System.out.println("Is res false = "+*/
        res.next()/*)*/;
        ArrayList<Table> attrib = new ArrayList<>();
        do {
            String F = res.getString(1);
            String T = res.getString(2);
            String K = res.getString(3);
            String N = res.getString(4);
            String E = res.getString(5);
            attrib.add(new Table(F, T, K, N, E));
        } while (res.next());
        return attrib;
    }

    public int indexOfPrimary(ArrayList<Table> arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).Key.equals("PRI")) {
                return i;
            }
        }
        return -1;
    }

    public Integer[] indexOfUnique(ArrayList<Table> arr) {
        Vector<Integer> uniqueKeys = new Vector<>();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).Key.equals("UNI")) {
                uniqueKeys.add(i);
            }
        }
        Integer unique[] = uniqueKeys.toArray(new Integer[uniqueKeys.size()]);
        return unique;
    }

    String createSpaces(Integer[] arr) {
        String result;
        if (arr.length > 0) {
            result = arr[0].toString();
            for (int i = 1; i < arr.length; i++) {
                result += " " + arr[i];
            }
        } else {
            result = "";
        }
        return result;
    }

    public String write_to_html(ArrayList<Table> arr, String tab) throws IOException, SQLException, Exception {
        int size = arr.size();
        int primary = indexOfPrimary(arr);
        Integer unique[] = indexOfUnique(arr);
        String uniqueValue = createSpaces(unique);
        if (primary == -1 && uniqueValue.equals("")) {
            JOptionPane.showMessageDialog(this, "The selected table does not have any identifiable constraints, such as Candidate Keys, Primary Key\n"
                    + "or a Foreign Key and thus specific records cannot be individually updated. \n"
                    + "The view is being created as Read-Only. This table cannot be updated with the quick update mode.",
                    "Constraint Error", JOptionPane.WARNING_MESSAGE);
        }
        FileWriter file = new FileWriter(new File(path + "SQLNavigator\\temp\\temp.html"));
        BufferedWriter s = new BufferedWriter(file);
        Statement st = con.createStatement();
        ResultSet res = st.executeQuery("Select * from `" + tab + "`;");
        if (!res.next()) {
            s.append("<html></html>");
            s.close();
            return new String("Empty Set");
        } else {
            s.append("<html>\n\t<head>\n\t\t<title>" + tab + "</title>\n\t\t<link rel = 'stylesheet' type = 'text/css' href = '../StyleSheet/tab.css'/><link rel = \"Stylesheet\" href=\"dialog.css\">"
                    + "<script src = \"Resource/resource.js\"></script>"
                    + "</script>\n\t</head>");
            s.append("\n\t<body>\n\t\t<div align = 'center'>\n\t\t\t<input class=\"in\" type=\"text\" placeholder=\"Search for Values..\" id=\"myInput\" onkeyup=\"search()\"><table id = \"myTable\" data-primary = \"" + primary + "\" data-unique = \"" + uniqueValue + "\" data-name = \"" + tab + "\">\n\t\t\t\t<caption> Table : " + tab.toUpperCase() + "</caption>");
            s.append("\n\t\t\t\t<tr class = 'b'>");
            for (int i = 0; i < size; i++) {
                s.append("\n\t\t\t\t\t<th>" + arr.get(i).Field + "</th>");
            }
            s.append("\n\t\t\t\t</tr>");
            int j = 0;
            do {
                s.append("\n\t\t\t\t<tr>");
                for (int i = 0; i < size; i++) {
                    String si = res.getString(i + 1);
                    s.append("\n\t\t\t\t\t<td style = 'vertical-align : middle !important;' data-type = \"" + arr.get(i).Type + "\" data-null = \"" + arr.get(i).Null + "\" ondblclick = 'toInput(this)' id ='" + String.valueOf(j) + "," + String.valueOf(i) + "'><pre>" + si + "</pre></td>");
                }
                s.append("\n\t\t\t\t</tr>");
                j++;
            } while (res.next());
            s.append("\n\t\t\t</table><br><br><br>\n\t\t</div>"
                    + "<div class = \"scroll-down scroll\" align = \"center\" id = \"dialog\" style = 'display : none;'>\n"
                    + "   <div class = \"modal-content\">\n"
                    + "   <p id = \"message\">A null value cannot be accepted for this column</p>\n"
                    + "   <button onclick = \"this.parentElement.parentElement.style.display = 'none';\">OK</button>\n"
                    + "  </div>\n"
                    + " </div>"
                    + "  <div class = \"status-bar\">\n"
                    + "      <span id = \"typeChosen\"></span>\n"
                    + "      <span id = \"status\">OK</span>\n"
                    + "    </div>"
                    + "\n\t</body>");
            s.append("\n</html>");
            s.close();
            return new String("Not Empty");
        }
    }

    public class QuickUpdate {

        Connection connect;
        JFrame frame;

        public QuickUpdate(Connection con, JFrame parent) {
            connect = con;
            frame = parent;
        }

        public boolean update(String table, String valueColumn, String newValue, String keyColumn, String keyValue) {
            boolean success = false;
            try {
                System.out.println("UPDATE `" + table + "` SET `" + valueColumn + "` = '" + newValue + "' WHERE `" + keyColumn + "` = '" + keyValue + "';");
                Statement st = con.createStatement();
                if (newValue == null) {
                    st.executeUpdate("UPDATE `" + table + "` SET `" + valueColumn + "` = '" + newValue + "' WHERE `" + keyColumn + "` = '" + keyValue + "';");
                    success = true;
                } else {
                    //System.out.println("UPDATE " + table + " SET " + valueColumn + " = \"" + newValue + "\" WHERE " + keyColumn + " = \"" + keyValue + "\";");
                    st.executeUpdate("UPDATE `" + table + "` SET `" + valueColumn + "` = '" + newValue + "' WHERE `" + keyColumn + "` = '" + keyValue + "';");
                    success = true;
                }
            } catch (SQLException e) {
                error(e, frame);
            }
            System.out.println("From quick update. Operation was " + success);
            return success;
        }
    }
}

class WebPort extends javax.swing.JDialog {

    /**
     * Creates new form WebPort
     */
    ArrayList<Table> arr;
    Connection conn;
    String tab;
    String format;
    String path;
    String selected;
    JFrame parent;

    public WebPort(java.awt.Frame parent, boolean modal, ArrayList<Table> tableDescriptor, String Table, Connection con, String pathname, JFrame frame) {
        super(parent, modal);
        arr = tableDescriptor;
        conn = con;
        tab = Table;
        path = pathname;
        parent = frame;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"HTML", "JSON", "XML", "CSV", "XLS"}));

        jLabel1.setText("Choose a format :");

        jButton1.setText("Port");

        jButton1.addActionListener((ActionEvent ev) -> {
            selected = jComboBox1.getItemAt(jComboBox1.getSelectedIndex()).toString();
            format = selected;
            try {
                port(selected);
            } catch (IOException | SQLException e) {
                error(e, parent);
            } catch (Exception e) {
                error(e, parent);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addGap(160, 160, 160))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addContainerGap())
        );

        pack();
    }

    public void port(String format) throws IOException, SQLException, Exception {
        switch (format) {
            case "HTML":
                Write_html(arr, tab);
                break;
            case "JSON":
                WriteJSON(arr, tab);
                break;
            case "XML":
                WriteXML(arr, tab);
                break;
            case "CSV":
                WriteCSV(arr, tab);
                break;
            case "XLS":
                WriteExcel(arr, tab);
        }
    }

    public void Write_html(ArrayList<Table> arr, String tab) throws IOException, SQLException, Exception {
        int size = arr.size();
        FileWriter file = new FileWriter(new File(path + "SQLNavigator\\toHTML\\" + tab + ".html"));
        BufferedWriter s = new BufferedWriter(file);
        s.append("<html>\n\t<head>\n\t\t<title>" + tab + "</title>\n\t\t<link rel = 'stylesheet' type = 'text/css' href = '../StyleSheet/tab.css'/>\n\t</head>");
        s.append("\n\t<body>\n\t\t<div align = 'center'>\n\t\t\t<br><br><br><table>\n\t\t\t\t<caption> Table : " + tab.toUpperCase() + "</caption>");
        s.append("\n\t\t\t\t<tr class = 'b'>");
        for (int i = 0; i < size; i++) {
            s.append("\n\t\t\t\t\t<th>" + arr.get(i).Field + "</th>");
        }
        s.append("\n\t\t\t\t</tr>");
        Statement st = conn.createStatement();
        ResultSet res = st.executeQuery("Select * from `" + tab + "`;");
        res.next();

        do {
            s.append("\n\t\t\t\t<tr>");
            for (int i = 0; i < size; i++) {
                String si = res.getString(i + 1);
                s.append("\n\t\t\t\t\t<td><pre>" + si + "</pre></td>");
            }
            s.append("\n\t\t\t\t</tr>");
        } while (res.next());
        s.append("\n\t\t\t</table>\n\t\t</div>\n\t</body>");
        s.append("\n</html>");
        s.close();
        this.setVisible(false);
        new Write_HTML(parent, true, path, tab, format).setVisible(true);
    }

    public void WriteJSON(ArrayList<Table> meta, String table) throws IOException, SQLException {
        int size = meta.size();
        FileWriter fw = new FileWriter(new File(path + "SQLNavigator\\toJSON\\" + table + ".json"));
        BufferedWriter br = new BufferedWriter(fw);
        br.append("{\n\t\"fields\" : \n\t[");
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                br.append("\n\t\t{ \"label\" : \"" + meta.get(i).Field + "\",\"type\" : \"" + meta.get(i).Type + "\"}");
            } else {
                br.append("\n\t\t{ \"label\" : \"" + meta.get(i).Field + "\",\"type\" : \"" + meta.get(i).Type + "\"},");
            }
        }
        br.append("\n\t],\n\t\"data\" : \n\t[");
        Statement st = conn.createStatement();
        ResultSet res = st.executeQuery("Select * from `" + table + "`;");
        while (res.next()) {
            br.append("\n\t\t[");
            for (int i = 0; i < size; i++) {
                String cell = res.getString(i + 1);
                if (i == size - 1) {
                    br.append("\"" + cell + "\"");
                } else {
                    br.append("\"" + cell + "\",");
                }
            }
            br.append("],");
        }
        br.append("\n\t]");
        br.append("\n}");
        br.close();
        this.setVisible(false);
        new Write_HTML(parent, true, path, tab, format).setVisible(true);
    }

    public void WriteCSV(ArrayList<Table> meta, String table) throws IOException, SQLException {
        int size = meta.size();
        FileWriter fw = new FileWriter(new File(path + "SQLNavigator\\toCSV\\" + table + ".csv"));
        BufferedWriter br = new BufferedWriter(fw);
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                br.append(meta.get(i).Field);
            } else {
                br.append(meta.get(i).Field + ",");
            }
        }
        br.append("\n");
        Statement st = conn.createStatement();
        ResultSet res = st.executeQuery("Select * from `" + table + "`;");
        while (res.next()) {
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    br.append(res.getString(i + 1));
                } else {
                    br.append(res.getString(i + 1) + ",");
                }
            }
            br.append("\n");
        }
        br.close();
        this.setVisible(false);
        new Write_HTML(parent, true, path, tab, format).setVisible(true);
    }

    public void WriteXML(ArrayList<Table> meta, String table) throws IOException, SQLException {
        int size = meta.size();
        final Map<String, String> NULL = new HashMap<String, String>() {
            {
                put("YES", "true");
                put("NO", "false");
            }
        };
        FileWriter fw1 = new FileWriter(new File(path + "SQLNavigator\\toXML\\" + table + ".xml"));
        FileWriter fw2 = new FileWriter(new File(path + "SQLNavigator\\toXML\\" + table + ".xsd"));
        BufferedWriter xml = new BufferedWriter(fw1);
        BufferedWriter xsd = new BufferedWriter(fw2);

        //The XSD File will be created first
        xsd.append("<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n"
                + "<xs:schema xmlns:xs = \"http://www.w3.org/2001/XMLSchema\">");
        xsd.append("\n<xs:element name = \""+table+"\">\n"
                +"\t<xs:complexType>\n"
                +"\t\t<xs:sequence>\n"
                +"\t\t\t<xs:element name = \"Attributes\">\n" +
                 "\t\t\t\t<xs:complexType>\n" +
                 "\t\t\t\t\t<xs:sequence>\n" +
                 "\t\t\t\t\t\t<xs:element name = \"Attribute\" maxOccurs = \"unbounded\">\n" +
                 "\t\t\t\t\t\t\t<xs:complexType>\n" +
                 "\t\t\t\t\t\t\t\t<xs:sequence>\n" +
                 "\t\t\t\t\t\t\t\t\t<xs:element name = \"dataType\" type = \"xs:string\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t<xs:element name = \"name\" type = \"xs:string\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t<xs:element name = \"null\" type = \"xs:boolean\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t<xs:element name = \"key\">\n" +
                 "\t\t\t\t\t\t\t\t\t\t<xs:simpleType>\n" +
                 "\t\t\t\t\t\t\t\t\t\t\t<xs:restriction base = \"xs:string\">\n" +
                 "\t\t\t\t\t\t\t\t\t\t\t\t<xs:enumeration value = \"PRI\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t\t\t\t<xs:enumeration value = \"UNI\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t\t\t\t<xs:enumeration value = \"\"/>\n" +
                 "\t\t\t\t\t\t\t\t\t\t\t</xs:restriction>\n" +
                 "\t\t\t\t\t\t\t\t\t\t</xs:simpleType>\n" +
                 "\t\t\t\t\t\t\t\t\t</xs:element>\n" +
                 "\t\t\t\t\t\t\t\t</xs:sequence>\n" +
                 "\t\t\t\t\t\t\t</xs:complexType>\n" +
                 "\t\t\t\t\t\t</xs:element>\n" +
                 "\t\t\t\t\t</xs:sequence>\n" +
                 "\t\t\t\t</xs:complexType>\n" +
                 "\t\t\t</xs:element>"
        );
        xsd.append("\n\t\t\t<xs:element name = \"Rows\">");
        xsd.append("\n\t\t\t\t<xs:complexType>");
        xsd.append("\n\t\t\t\t\t<xs:sequence>");
        xsd.append("\n\t\t\t\t\t\t<xs:element name = \"Row\" maxOccurs = \"unbounded\">");
        xsd.append("\n\t\t\t\t\t\t\t<xs:complexType>");
        xsd.append("\n\t\t\t\t\t\t\t\t<xs:sequence>");
        for (int i = 0; i < size; i++) {
            Table curr = meta.get(i);
            if (curr.Type.toUpperCase().contains("ENUM") || curr.Type.toUpperCase().contains("SET")) {
                String b[] = meta.get(i).Type.split("\\(");
                String c[] = b[1].split(",");
                String d[] = c[c.length - 1].split("\\)");
                xsd.append("\n\t\t\t\t\t\t\t\t\t<xs:element name = \"" + curr.Field + "\">");
                xsd.append("\n\t\t\t\t\t\t\t\t\t\t<xs:simpleType>");
                xsd.append("\n\t\t\t\t\t\t\t\t\t\t\t<xs:restriction base = \"xs:string\">");
                for (int k = 0; k < c.length - 1; k++) {
                    xsd.append("\n\t\t\t\t\t\t\t\t\t\t\t\t<xs:enumeration value = \"" + c[k].substring(1, c[k].length() - 1) + "\"/>");
                    System.out.println("c[" + k + "] = " + c[k].substring(1, c[k].length() - 1));
                }
                xsd.append("\n\t\t\t\t\t\t\t\t\t\t\t\t<xs:enumeration value = \"" + d[0].substring(1, d[0].length() - 1) + "\"/>");
                System.out.println("d[0] = " + d[0].substring(1, d[0].length() - 1));
                xsd.append("\n\t\t\t\t\t\t\t\t\t\t\t</xs:restriction>");
                xsd.append("\n\t\t\t\t\t\t\t\t\t\t</xs:simpleType>");
                xsd.append("\n\t\t\t\t\t\t\t\t\t</xs:element>");
            } else {
                xsd.append("\n\t\t\t\t\t\t\t\t\t<xs:element name = \"" + curr.Field + "\" type = \"" + SQLtoXML(curr.Type) + "\"/>");
            }
        }
        xsd.append("\n\t\t\t\t\t\t\t\t</xs:sequence>"
                + "\n\t\t\t\t\t\t\t</xs:complexType>"
                + "\n\t\t\t\t\t\t</xs:element>"
                + "\n\t\t\t\t\t</xs:sequence>"
                + "\n\t\t\t\t</xs:complexType>"
                + "\n\t\t\t</xs:element>"
                + "\n\t\t</xs:sequence>"
                + "\n\t</xs:complexType>" 
                + "\n</xs:element>");
        xsd.append("\n</xs:schema>");
        xsd.close();

        //Now we create the XML File
        xml.append(
                "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n"
                + "<" + table + " xmlns:xsi = \"http://www.w3.org//2001/XMLSchema-instance\">\n"
                + "\t<Attributes>"
        );
        for (int i = 0; i < size; i++) {
            Table curr = meta.get(i);
            xml.append("\n\t\t<Attribute>");
            xml.append("\n\t\t\t<dataType>" + curr.Type + "</dataType>");
            xml.append("\n\t\t\t<name>" + curr.Field + "</name>");
            xml.append("\n\t\t\t<null>" + NULL.get(curr.Null) + "</null>");
            xml.append("\n\t\t\t<key>" + curr.Key + "</key>");
            xml.append("\n\t\t</Attribute>");
        }
        xml.append("\n\t</Attributes>");
        Statement st = conn.createStatement();
        ResultSet res = st.executeQuery("Select * from " + table + ";");
        xml.append("\n\t<Rows>");
        while (res.next()) {
            xml.append("\n\t\t<Row>");
            for (int i = 0; i < size; i++) {
                Table curr = meta.get(i);
                xml.append("\n\t\t\t<" + curr.Field + ">" + res.getString(i + 1) + "</" + curr.Field + ">");
            }
            xml.append("\n\t\t</Row>");
        }
        xml.append("\n\t</Rows>");
        xml.append("\n</" + table + ">");
        xml.close();
        this.setVisible(false);
        new Write_HTML(parent, true, path, tab, format).setVisible(true);
    }

    /**
     * Rules for transformation of data types from SQL to XML * Numeric Types *
     * 1. Bit - xs:byte 2. Tinyint - xs:int 3. Bool, Boolean -xs:boolean 4.
     * smallint : xs:int 5. mediumint - xs:integer 6. int - xs:long 7. integer -
     * xs:long 8. bigint - xs:long 9. double - xs:decimal 10. decimal -
     * xs:decimal 11. dec - xs:decimal 12. float - xs:decimal
     *
     * Date Time Types * 1. Date - xs:date 2. datetime - xs:dateTime 3.
     * timestamp - xs:dateTime 4. time - xs:time 5. year - xs:gYear
     *
     * String Types * 1. char - xs:string 2. varchar - xs:string 3. text -
     * xs:string 4. enum(values) 5. set(values)
     *
     * for four and five
     *
     * <xs:element name = "">
     * <xs:simpleType>
     * <xs:restriction base = "xs:string">
     * <xs:enumeration value = ""/>
     * <xs:enumeration value = ""/>
     * ...........................
     * <xs:enumeration value = ""/>
     * </xs:restriction>
     * </xs:simpleType>
     * </xs:element>
     */
    public String SQLtoXML(String type) {
        //Numeric Types
        if (type.toUpperCase().contains("BIT")) {
            return "xs:byte";
        } else if (type.toUpperCase().contains("BOOL") || type.toUpperCase().contains("BOOLEAN")) {
            return "xs:boolean";
        } else if (type.toUpperCase().contains("TINYINT") || type.toUpperCase().contains("SMALLINT")) {
            return "xs:int";
        } else if (type.toUpperCase().contains("MEDIUMINT")) {
            return "xs:integer";
        } else if (type.toUpperCase().contains("INT") || type.toUpperCase().contains("INTEGER") || type.toUpperCase().contains("BIGINT")) {
            return "xs:long";
        } else if (type.toUpperCase().contains("DOUBLE") || type.toUpperCase().contains("DECIMAL") || type.toUpperCase().contains("DEC") || type.toUpperCase().contains("FLOAT")) {
            return "xs:decimal";
        } //Date Time Types
        else if (type.toUpperCase().contains("DATE")) {
            return "xs:date";
        } else if (type.toUpperCase().contains("DATETIME") || type.toUpperCase().contains("TIMESTAMP")) {
            return "xs:dateTime";
        } else if (type.toUpperCase().contains("TIME")) {
            return "xs:time";
        } else if (type.toUpperCase().contains("YEAR")) {
            return "xs:gYear";
        } //String types
        else if (type.toUpperCase().contains("CHAR") || type.toUpperCase().contains("VARCHAR") || type.toUpperCase().contains("TEXT")) {
            return "xs:string";
        }
        return "xs:string";
    }

    public void WriteExcel(ArrayList<Table> meta, String table) throws SQLException, FileNotFoundException, IOException {
        final ArrayList <String> intTypes = new ArrayList <>(Arrays.asList("TINYINT","SMALLINT","MEDIUMINT","INT","INTEGER","BIGINT"));
        final ArrayList <String> floatTypes = new ArrayList <>(Arrays.asList("DECIMAL","NUMERIC","FLOAT","DOUBLE"));
        final ArrayList <String> stringTypes = new ArrayList <>(Arrays.asList("CHAR","VARCHAR","BLOB","TEXT"));
        SQLViewer vwr; 
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(table);
        HSSFCellStyle style = workbook.createCellStyle();
        sheet.setDefaultColumnWidth(40);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        HSSFCellStyle rowstyle = workbook.createCellStyle();
        rowstyle.setWrapText(true);
        style.setFont(font);
        int rowCount = 0;
        HSSFRow row = sheet.createRow(rowCount);
        for (int i = 0; i < meta.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(meta.get(i).Field);
            cell.setCellStyle(style);
        }
        rowCount = 1;
        Statement st = conn.createStatement();
        ResultSet res = st.executeQuery("Select * from " + table + ";");
        while (res.next()) {
            row = sheet.createRow(rowCount);
            for (int i = 0; i < meta.size(); i++) {
                HSSFCell cell = row.createCell(i);
                String types[] = meta.get(i).Type.split("\\(");
                String type = types[0];
                if(intTypes.contains(type.toUpperCase())) {
                    cell.setCellValue(Double.valueOf(res.getInt(i+1)));
                    //System.out.println("Took an Integer value");
                }
                else if(floatTypes.contains(type.toUpperCase())) {
                    cell.setCellValue(res.getDouble(i+1));
                    //System.out.println("Took a Float Value");
                }
                else if(stringTypes.contains(type.toUpperCase())) {
                    cell.setCellValue(res.getString(i+1));
                    //System.out.println("Took a String value");
                }
                else if(type.toUpperCase().equals("DATE")) {
                    cell.setCellValue(res.getDate(i+1));
                    //System.out.println("Took a Date value");
                }
                else {
                    cell.setCellValue(res.getString(i+1));
                }
                cell.setCellStyle(rowstyle);
            }
            rowCount++;
        }
        FileWriter fw = new FileWriter(new File(path + "SQLNavigator\\toXLS\\" + table + ".xls"));
        BufferedWriter br = new BufferedWriter(fw);
        br.close();
        FileOutputStream out = new FileOutputStream(new File(path + "SQLNavigator\\toXLS\\" + table + ".xls"));
        workbook.write(out);
        out.close();
        this.setVisible(false);
        new Write_HTML(parent, true, path, tab, format).setVisible(true);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration                   
}

class Write_HTML extends javax.swing.JDialog {

    javax.swing.JButton jButton1;
    javax.swing.JButton jButton2;
    javax.swing.JButton jButton3;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JTextField jTextField1;
    String pathname;
    String table;
    ProcessBuilder process;
    Process pr;
    int errCode;
    String path;
    String format;

    final Map<String, String> icons = new HashMap<String, String>() {
        {
            put("HTML", "HTML.png");
            put("JSON", "JSON.png");
            put("CSV", "CSV.png");
            put("XML", "XML.png");
            put("XLS", "XLS.png");
        }
    };

    final Map<String, String> folders = new HashMap<String, String>() {
        {
            put("HTML", "toHTML");
            put("JSON", "toJSON");
            put("CSV", "toCSV");
            put("XML", "toXML");
            put("XLS", "toXLS");
        }
    };

    final Map<String, String> ext = new HashMap<String, String>() {
        {
            put("HTML", ".html");
            put("JSON", ".json");
            put("CSV", ".csv");
            put("XML", ".xml");
            put("XLS", ".xls");
        }
    };

    /**
     * Creates new form Write_HTML
     */
    public Write_HTML(java.awt.Frame parent, boolean modal, String path, String tab, String form) {
        super(parent, modal);
        pathname = path;
        table = tab;
        format = form;
        initComponents();
        addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent ev) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jTextField1.setEditable(false);
        jTextField1.setText(pathname + "SQLNavigator\\" + folders.get(format) + "\\" + table + ext.get(format));

        //Getting the path of the parent directory
        try {
            path = new SQLNavigator().getPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("pathname = " + pathname + "SQLNavigator\\Icons\\" + icons.get(format));
        jLabel1.setIcon(new ImageIcon(pathname + "SQLNavigator\\Icons\\" + icons.get(format)));
        //C:\Users\SWAPNIL\Documents\NetBeansProjects\SQLNavigator\Icons
        jLabel2.setText("The file is saved as :");

        jButton1.setText("Open File");

        jButton1.addActionListener((ActionEvent ev) -> {
            try {
                process = new ProcessBuilder("cmd.exe", "/C \"" + pathname + "SQLNavigator\\" + folders.get(format) + "\\" + table + ext.get(format) + "\"");
                pr = process.start();
                errCode = pr.waitFor();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Logger.getLogger(Write_HTML.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (errCode != 0) {
                JOptionPane.showMessageDialog(this, "Some error occured that prevented the completion of this action", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        jButton2.setText("Open Folder");
        jButton2.addActionListener((ActionEvent ev) -> {
            try {
                process = new ProcessBuilder("explorer.exe", "/select,\"" + path + "SQLNavigator\\" + folders.get(format) + "\\" + table + ext.get(format) + "\"");
                System.out.println(pathname + table + ".html");
                pr = process.start();
                errCode = pr.waitFor();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Logger.getLogger(Write_HTML.class.getName()).log(Level.SEVERE, null, ex);
            }
            // if(errCode != 0)
            //JOptionPane.showMessageDialog(this, "Some error occured that prevented the completion of this action","Error",JOptionPane.ERROR_MESSAGE);
        });

        jButton3.setText("Close");
        jButton3.addActionListener((ActionEvent ev) -> {
            setVisible(false);
        });

        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2))
                                                .addGap(29, 29, 29))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jButton1)
                                                .addGap(102, 102, 102)
                                                .addComponent(jButton2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton3)
                                                .addGap(38, 38, 38))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(24, 24, 24)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addComponent(jButton3))
                                .addContainerGap())
        );
        pack();
    }

}

class EditTableModel extends javax.swing.table.DefaultTableModel {
    private String internalModel[][] = new String [44][17];
    
    public EditTableModel() {
        super(44,17);
    }
    
    
    @Override
    public int getRowCount() {
        return 44;
    }
    
    @Override
     public Object getValueAt(int rowIndex, int columnIndex) {
         //System.out.println("Get value at "+rowIndex+","+columnIndex+" = "+internalModel[rowIndex][columnIndex]);
        return internalModel[rowIndex][columnIndex];
    }
     
    public void setModel(int colSize) {
        internalModel = new String[44][colSize];
        System.out.println("In setModel");
        System.out.println(internalModel.length+","+internalModel[0].length);
        for(int i = 0; i < internalModel.length; i++) {
            for(int j = 0; j < internalModel[i].length; j++) {
                internalModel[i][j] = new String("");
            }
        }
    }
    
    @Override
    public void setValueAt(Object o, int rowIndex, int columnIndex) {
        internalModel[rowIndex][columnIndex] = o.toString();
        fireTableCellUpdated(rowIndex, columnIndex);
        //System.out.println(internalModel[rowIndex][columnIndex]);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
}


class SQLUpdate extends SQLViewer {

    JTable TableRep = new JTable(44, 17);
    EditTableModel TabModel = new EditTableModel();
    JButton Import = new JButton("Import");
    UserAccountInformation u;
    JMenuBar bottomMenu = new JMenuBar();
    
    //
    JLabel first  = new JLabel("                                               Insert values upto row   ");
    JTextField rowNo = new JTextField();
    JButton checkandPush = new JButton("  Into The Table ");
    JButton direct = new JButton("Directly");
    JLabel second = new JLabel("                                                                        ");
    
    //
    ArrayList<Table> attrib;
    Vector<String> columnNames;

    public SQLUpdate(UserAccountInformation d) throws SQLException, Exception {
        super(d);
        u = d;
        SceneBuilder();
    }

    public void SceneBuilder() throws SQLException, Exception {
        Vector<String> dbase = Give_Database();
        //dbase.remove("performance_schema");
        db = new JComboBox<>();
        for (String g : dbase) {
            db.addItem(g);
        }
        
        /**                */
        checkandPush.setEnabled(false);
        rowNo.setEditable(false);
        direct.setEnabled(false);

        TableRep.setModel(TabModel);
        
        /*TableRep.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int row = e.getLastRow();
                int column = e.getColumn();
                System.out.println(row+","+column);
            }
        });*/
        //TableRep.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableRep.setAutoCreateRowSorter(true);
        db.addActionListener((ActionEvent AE) -> {
            try {
                System.out.println("Inside the ListListener");
                Current = db.getItemAt(db.getSelectedIndex());
                Vector<String> table = Give_Tables(Current);
                setTitle("SQL Navigator - Update - " + Current + "/");
                if (table.contains("No Tables")) {
                    tables.setEnabled(false);
                    maketable.setEnabled(false);
                } else {
                    tables.setEnabled(true);
                    maketable.setEnabled(true);
                }
                tables.removeAllItems();
                for (String g : table) {
                    tables.addItem(g);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        getContentPane().setLayout(new FlowLayout());
        Panel = new JPanel();
        ViewMenu.add(db);
        tables = new JComboBox<>();
        tables.addItem("Select");

        maketable = new JButton("Go");

        tables.setEnabled(false);
        maketable.setEnabled(false);

        tables.addActionListener((ActionEvent AE) -> {
            selected_table = tables.getItemAt(tables.getSelectedIndex());
            //System.out.println(selected_table);
            setTitle("SQL Navigator - Update - " + Current + "/" + (selected_table == null ? "" : selected_table));
        });
        ViewMenu.add(tables);
        maketable.addActionListener((ActionEvent ev) -> {

            try {
                selected_table = tables.getItemAt(tables.getSelectedIndex());
                attrib = desc_table(selected_table);
                columnNames = columnHeader(attrib);
                TabModel.setColumnIdentifiers(columnNames);
                TabModel.setModel(columnNames.size());
                TableRep.repaint();
                TabModel.fireTableDataChanged();
                checkandPush.setEnabled(true);
                rowNo.setEditable(true);
                direct.setEnabled(true);
            } catch (SQLException ex) {
                error(ex, this);
            } catch (Exception ex) {
                error(ex, this);
            }
        });
        //maketable.setIcon(new ImageIcon("C:\Users\SWAPNIL\Documents\NetBeansProjects\SQLNavigatorupdate.ico"));
        checkandPush.addActionListener((ActionEvent ae) -> {
            if(!rowNo.getText().isEmpty()) {
                if(typeCheck.isNum(rowNo.getText()) && Integer.parseInt(rowNo.getText()) <= 44) {
                    TabModel.fireTableDataChanged();
                    if(validateTable()) {
                        try {
                            int rows = Integer.parseInt(rowNo.getText());
                            int cols = columnNames.size();
                            Statement st = con.createStatement();
                            String statement = "INSERT INTO `"+selected_table+"` values";
                            for(int i = 0; i < rows - 1; i++) {
                                String rowSt = "(";
                                for(int j = 0; j < cols - 1; j++) {
                                    rowSt += "'"+TabModel.getValueAt(i,j).toString()+"',";
                                }
                                rowSt += "'"+TabModel.getValueAt(i,cols - 1).toString()+"'),";
                                statement += rowSt;
                            }
                            String lastRow = "(";
                            for(int k = 0; k < cols - 1; k++) {
                                lastRow += "'"+TabModel.getValueAt(rows - 1,k).toString()+"',";
                            }
                            lastRow += "'"+TabModel.getValueAt(rows - 1, cols - 1).toString() + "');";
                            statement += lastRow;
                            st.executeUpdate(statement);
                            JOptionPane.showMessageDialog(this,"The rows have been succesfully inserted into the table '"+selected_table+"'","Insertion Successful",JOptionPane.INFORMATION_MESSAGE);
                            TabModel.setModel(cols);
                            TabModel.fireTableDataChanged();
                        }catch(SQLException e) {
                            error(e, this);
                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this,"Enter a valid row count (less than 44)!");
                }
            }
            else {
                JOptionPane.showMessageDialog(this,"Enter a valid row count (less than 44)!");
            }
        });
        
        ViewMenu.add(maketable);
        
        rowNo.setSize(200,50);
        
        bottomMenu.add(first);
        bottomMenu.add(rowNo);
        bottomMenu.add(checkandPush);
        bottomMenu.add(second);
        
        

        Import.addActionListener((ActionEvent ae) -> {
            new Import(this, true, u,con).setVisible(true);
        });
        ViewMenu.add(Import);
        this.getContentPane().setLayout(new BorderLayout());
        add(ViewMenu, BorderLayout.NORTH);
        add(bottomMenu, BorderLayout.SOUTH);
        JScrollPane scroll = new JScrollPane(TableRep);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.getContentPane().add(scroll);
        this.setIconImage(new ImageIcon(new SQLNavigator().getPath() + "SQLNavigator\\Icons\\Icon.png").getImage());

        TabModel.addTableModelListener((TableModelEvent ev) -> {

        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
        setTitle("SQL Navigator - Update ");
        setSize(900, 700);
    }
    
    public boolean validateTable() {
        final ArrayList <String> intTypes = new ArrayList <>(Arrays.asList("TINYINT","SMALLINT","MEDIUMINT","INT","INTEGER","BIGINT"));
        final ArrayList <String> floatTypes = new ArrayList <>(Arrays.asList("DECIMAL","NUMERIC","FLOAT","DOUBLE"));
        final ArrayList <String> stringTypes = new ArrayList <>(Arrays.asList("CHAR","VARCHAR","BLOB","TEXT"));
        int rows = Integer.parseInt(rowNo.getText());
        boolean valid = true;
        String errStr = "";
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columnNames.size(); j++) {
                System.out.println("Inside validate "+i+","+j);
                String value = TabModel.getValueAt(i, j).toString();
                String type = attrib.get(j).Type;
                String types[] = type.contains("(") ? type.split("\\(") : new String[] {type};
                System.out.println("Type = "+types[0]);
                if(intTypes.contains(types[0].toUpperCase())) {
                    if(!typeCheck.checkIntType(value, type)) {
                        valid = false;
                        errStr += " - In the Column - "+columnNames.get(j)+" of Row - "+i+", the value '"+value+"' doesn't comply with the specified datatype - "+type+"\n";
                    }
                }
                else if(floatTypes.contains(types[0].toUpperCase())) {
                    if(!typeCheck.checkFloatType(value, type)) {
                        valid = false;
                        errStr += " - In the Column - "+columnNames.get(j)+" of Row - "+i+", the value '"+value+"' doesn't comply with the specified datatype - "+type+"\n";
                    }
                }
                else if(stringTypes.contains(types[0].toUpperCase())) {
                    System.out.println("typeCheck.checkStringType("+value+","+ type+") = "+typeCheck.checkStringType(value, type));
                    if(!typeCheck.checkStringType(value, type)) {
                        valid = false;
                        errStr += " - In the Column - "+columnNames.get(j)+" of Row - "+i+", the value '"+value+"' doesn't comply with the specified datatype - "+type+"\n";
                    }
                }
            }
        }
        if(!valid) {
            Error_Message err = new Error_Message(this,true, errStr);
            err.errorInputMessage();
            err.setVisible(true);
            //JOptionPane.showMessageDialog(this, "A few of the fields were incorrectly filled, details follow :\n"+errStr,"Invalid Entry Error",JOptionPane.ERROR_MESSAGE);
        }
        return valid;
    }


    public Vector<String> columnHeader(ArrayList<Table> arr) {
        Vector<String> colHeader = new Vector<>();
        for (Table dud : arr) {
            colHeader.add(dud.Field);
        }
        return colHeader;
    }
}

final class typeCheck {
   public static boolean checkIntType(String value, String type) {
       boolean isInt = false;
       type = type.toLowerCase();
       String types[] = type.split("\\(");
       String len[] = types[1].split("\\)");
       if(value.charAt(0) == '-') {
           len[0] = String.valueOf(Integer.parseInt(len[0])+1);
       }
        if(!isNum(value))
            return false;
        else {
            switch(types[0]) {
                case "tinyint":
                    isInt = value.length() <= Integer.parseInt(len[0]) && Integer.parseInt(value) <= 255 && Integer.parseInt(value) >= 0;
                    break;
                case "smallint":
                    isInt = value.length() <= Integer.parseInt(len[0]) && Integer.parseInt(value) <= 32767 && Integer.parseInt(value) >= -32768;
                    break;
                case "mediumint":
                    isInt = value.length() <= Integer.parseInt(len[0]) && Integer.parseInt(value) <= 8388607 && Integer.parseInt(value) >= -8388608;
                    break;
                case "int":
                    isInt = value.length() <= Integer.parseInt(len[0]) && Long.valueOf(value) <= 2147483647 &&  Long.valueOf(value) >= -2147483647;
                    break;
                case "bigint":
                    isInt = value.length() <= 19;
                    break;
            }
        }
        return isInt;
    }
    
    public static boolean checkFloatType(String value, String type) {
        boolean isFloat = false;
        String types[] = type.split("\\(");
        String len[] = types[1].split("\\)");
        String lens[] = len[0].split(","); 
        int lengths[] = new int[2];
        lengths[0] = Integer.parseInt(lens[0]);
        lengths[1] = Integer.parseInt(lens[1]);
        lengths[0] = lengths[0] - lengths[1];
        if(value.charAt(0) == '-') {
            lengths[0]++;
        }
        if(value.contains(".")) {
            String nums[] = value.split("\\.");
            if(isNum(nums[0]) && isNum(nums[1])) {
                switch(types[0]) {
                    case "float":
                    case "double":
                    case "real":
                        isFloat = nums[0].length() <= lengths[0] && nums[1].length() <= lengths[1];
                        break;
                    case "numeric":
                    case "decimal":
                        isFloat = nums[0].length() <= lengths[0] && nums[1].length() <= lengths[1];
                        break;
                }
            }
            else {
                isFloat = false;
            }
        }
        else if(isNum(value)) {
            switch(types[0]) {
                case "float":
                case "double":
                case "real":
                    isFloat = value.length() <= lengths[0];
                    break;
                case "numeric":
                case "decimal":
                    isFloat = value.length() <= lengths[0];
                    break;
            }
        }
        else {
            isFloat = false;
        }
        
        return isFloat;
    }
    
    public static boolean checkStringType(String value, String type) {
        boolean isString = false;
        if(type.contains("(")) {
            String types[] = type.split("\\(");
            String len[] = types[1].split("\\)");
            int lenth = Integer.parseInt(len[0]);
            switch(types[0]) {
                case "char":
                case "varchar":
                    isString = value.length() <= lenth;
                    break;
            }
        }
        else {
            isString = true;
        }
        return isString;
    }
    
    private static boolean isNum (char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    public static boolean isNum (String num) {
        for(int i = 0; i < num.length(); i++) {
            if(num.charAt(0) == '-');
            else if(!isNum(num.charAt(i)))
                return false;
        }
        return true;
    }
}

class Error_Message extends javax.swing.JDialog {

    /**
     * Creates new form Error_Message
     */
    public Error_Message(java.awt.Frame parent, boolean modal, String error_msg) {
        super(parent, modal);
        initComponents();
        setErrorMessage(error_msg);
        addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent ev) {
                setVisible(false);
                try {
                    finalize();
                } catch (Throwable ex) {/*error(ex,parent);*/
                }
            }
        });
    }

    public void errorInputMessage() {
        jLabel1.setText("A few of the fields were incorrectly filled, details follow :");
    }
    
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        //Getting the path of the lcal directory
        try {
            path = new SQLNavigator().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jLabel1.setText("Some error occured while the run of this application.");
        jLabel2.setIcon(new javax.swing.ImageIcon(path + "SQLNavigator\\Icons\\error_message.png"));
        jLabel3.setText("Deatils are as follows :");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new Font("Helvetica", Font.BOLD, 9));
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("OK");
        jButton1.addActionListener((ActionEvent ev) -> {
            setVisible(false);
        });

        jButton2.setText("Send Error Report");

        setResizable(false);
        setIconImage(new ImageIcon(path+"SQLNavigator\\Icons\\Icon.png").getImage());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(46, 46, 46)
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(104, 104, 104)
                                                .addComponent(jButton2))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel3)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel3))
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addGap(25, 25, 25))
        );

        pack();
    }/// </editor-fold>                        

    public void setErrorMessage(String error_msg) {
        jTextArea1.setText(error_msg);
    }

    public static void error(Exception e, JFrame frame) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        baos.reset();
        e.printStackTrace(ps);
        ps.flush();
        new Error_Message(frame, true, baos.toString()).setVisible(true);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel jLabel3;
    private String path;
    // End of variables declaration                   
}

class Import extends javax.swing.JDialog {

    /**
     * Creates new form Export
     */
    String path;
    String fileName;
    JFileChooser FileChooser;
    SQLViewer vwr;
    Vector<String> tables;
    KeyEventHandler handler;
    String extension;
    String database;
    Boolean tableNameAvail = true;
    String root;
    Connection con;
    
    //***
    boolean error = true;
    String errStr = "";
    
    public Import(java.awt.Frame parent, boolean modal, UserAccountInformation u, Connection conn) {
        super(parent, modal);
        con = conn;
        try {
            vwr = new SQLViewer(u);
            root = new SQLNavigator().getPath();
            initComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() throws Exception {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        FileChooser = new JFileChooser();

        javax.swing.filechooser.FileFilter f1 = new javax.swing.filechooser.FileNameExtensionFilter("JSON Files (*.json)", "json");
        javax.swing.filechooser.FileFilter f2 = new javax.swing.filechooser.FileNameExtensionFilter("XML Files (*.xml)", "xml");
        javax.swing.filechooser.FileFilter f3 = new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xls,*.xlsx)", "xls","xlsx");
        FileChooser.setFileFilter(f1);
        FileChooser.addChoosableFileFilter(f2);
        FileChooser.addChoosableFileFilter(f3);
        FileChooser.setAcceptAllFileFilterUsed(false);

        handler = new KeyEventHandler(jTextField2, tables, jLabel4,this);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Choose File :");

        jTextField1.setEditable(false);

        jButton1.setText("...");
        jButton1.addActionListener((ActionEvent ev) -> {
            int result = FileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                path = FileChooser.getSelectedFile().getAbsolutePath();
                String FileName[] = path.split("\\\\");
                String parts[] = FileName[FileName.length - 1].split("\\.");
                String name = "";
                for (int i = 0; i < parts.length - 1; i++) {
                    name += parts[i];
                }
                extension = parts[parts.length - 1];
                jTextField2.setText(name);
                jTextField1.setText(path);
                if(tables.contains(name.toLowerCase()) || tables.contains(name.toUpperCase())) {
                    jLabel4.setText("<html><font color = \"red\"><html><font color = \"red\">The name is already associated with a table!</font></html>");
                    tableNameAvail = false;
                }
                else {
                    jLabel4.setText("<html><font color = \"green\">Table Name is available for use</font></html>");
                    tableNameAvail = true;
                } 
                fileName = name;
            }
        });

        jLabel2.setText("Select Database : ");

        Vector<String> db = vwr.Give_Database();
        for (String g : db) {
            jComboBox1.addItem(g);
        }
        database = db.get(0);
        
        try {
         tables = vwr.Give_Tables(db.get(0));
         handler.setTableList(tables);  
        }catch(SQLException e) {e.printStackTrace();}

        jComboBox1.addActionListener((ActionEvent ae) -> {
            String selected_db = jComboBox1.getItemAt(jComboBox1.getSelectedIndex());
            database = selected_db; 
            try {
                tables = new Vector<>(vwr.Give_Tables(selected_db));
                handler.setTableList(tables);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (tables.contains(jTextField2.getText().toUpperCase()) || tables.contains(jTextField2.getText().toLowerCase())) {
                jLabel4.setText("<html><font color = \"red\"><html><font color = \"red\">The name is already associated with a table!</font></html>");
                tableNameAvail = false;
            } else {
                jLabel4.setText("<html><font color = \"green\">Table Name is available for use</font></html>");
                tableNameAvail = true;
            }
        });
        
            
        jButton2.addActionListener((ActionEvent ae)->{
            error = true;
            if(jTextField1.getText().isEmpty()) {
                error = false;
                errStr += "\n\t- You have to select a file to import";
            }
            //System.out.println("tabelNameAvail = "+tableNameAvail);
            if(!tableNameAvail) {
                error = false;
                errStr += "\n\t- The Table Name is associated with an existing table in the database "+database;
            }
            if(jTextField2.getText().isEmpty()) {
                error = false;
                errStr += "\n\t- Tabel Name cannot be Blank";
            }
            if(!error)
                JOptionPane.showMessageDialog(this,"The Import Operation cannot be completed because :"+errStr,"",JOptionPane.ERROR_MESSAGE);
            else {
                setVisible(false);
                new ImportTool(path,jTextField2.getText(),extension,database,con,this);
            }
            errStr = "";
        });

        jLabel3.setText("Table Name :");

        jButton2.setText("Import");

        jTextField2.setFocusable(true);
        jTextField2.setFocusTraversalKeysEnabled(false);
        jTextField2.addKeyListener(handler);

        this.setResizable(false);
        setIconImage(new ImageIcon(root+"SQLNavigator\\Icons\\Icon.png").getImage());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jTextField2)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(173, 173, 173)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(152, 152, 152)
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2)
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration

    class KeyEventHandler implements KeyListener {

        JTextField field;
        Vector<String> tables = new Vector<>();
        JLabel label;
        Import avail;

        public KeyEventHandler(JTextField e, Vector<String> t, JLabel l,Import tbl) {
            field = e;
            tables = t;
            label = l;
            avail = tbl;
        }

        public void setTableList(Vector<String> t) {
            tables = new Vector<>(t);
            //System.out.println("Table List was reset");

        }

        @Override
        public void keyTyped(KeyEvent ke) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyReleased(KeyEvent ke) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            String tableName = field.getText();
            if (tables.contains(tableName.toUpperCase()) || tables.contains(tableName.toLowerCase())) {
                label.setText("<html><font color = \"red\">The name is already associated with a table!</font></html>");
                avail.tableNameAvail= false;
            } else {
                label.setText("<html><font color = \"green\">Table Name is available for use</font></html>");
                avail.tableNameAvail = true;
            }
        }
    }
}

class ImportTool extends JFrame {
    
    final JFXPanel FXPanel = new JFXPanel();
    public WebEngine engine;
    String filePath;
    String fileName;
    String fileExt;
    String database;
    String pathName;
    String url;
    Connection conn;
    ExcelData data; 
    QueryExecutor exec;
    int rowSize, colSize;
    Import prnt;
    HSSFWorkbook workbook1;
    HSSFSheet sheet1;
    XSSFWorkbook workbook2;
    XSSFSheet sheet2;
    Iterator <Row> rowIterator;
    
    public ImportTool (String path,String name, String ext, String db, Connection con, Import p) {
        filePath = path;
        fileName = name;
        fileExt = ext;
        database = db;
        conn = con;
        prnt = p;
        try {
            SQLNavigator t = new SQLNavigator();
            pathName = t.getPath();
            url = t.getURL();
        } catch(Exception e) {e.printStackTrace();}
        if(prnt.extension.equals("xls") || prnt.extension.equals("xlsx"))
            writeImportPage(getExcelData(filePath));
        else if(prnt.extension.equals("json"))
            writeJSONPage(filePath);
    }
    
    public void writeImportPage(ExcelData ex) {
        data = ex;
        try {
            FileWriter file = new FileWriter(new File(pathName+"SQLNavigator\\Import\\Import.html"));
            BufferedWriter br = new BufferedWriter(file);
            ArrayList <AttributeData> arr = ex.tuples;
            String select = "\n\t\t\t\t\t\t\t<optgroup label=\"Numeric\">\n\t\t\t\t\t\t\t\t<option value = \"tinyint\">TINYINT</option>"
                    + "\n\t\t\t\t\t\t\t\t<option value = \"smallint\">SMALLINT</option>\n\t\t\t\t\t\t\t\t<option value = \"mediumint\">MEDIUMINT</option>\n\t\t\t\t\t\t\t\t<option value = \"int\">INT</option>"
                    + "\n\t\t\t\t\t\t\t\t<option value = \"bigint\">BIGINT</option>\n\t\t\t\t\t\t\t\t<option value = \"numeric\">NUMERIC</option>\n\t\t\t\t\t\t\t\t<option value = \"double\">DOUBLE</option>\n\t\t\t\t\t\t\t\t"
                    + "<option value = \"bit\">BIT</option>\n\t\t\t\t\t\t\t\t<option value = \"boolean\">BOOLEAN</option>\n\t\t\t\t\t\t\t</optgroup>\n\t\t\t\t\t\t\t<optgroup label=\"Date and time\">\n\t\t\t\t\t\t\t\t"
                    + "<option value = \"date\">DATE</option>\n\t\t\t\t\t\t\t\t<option value = \"datetime\">DATETIME</option>\n\t\t\t\t\t\t\t\t<option value = \"timestamp\">TIMESTAMP</option>\n\t\t\t\t\t\t\t\t"
                    + "<option value = \"time\">TIME</option>\n\t\t\t\t\t\t\t\t<option value = \"year\">YEAR</option>\n\t\t\t\t\t\t\t</optgroup>\n\t\t\t\t\t\t\t<optgroup label=\"String\">\n\t\t\t\t\t\t\t\t<option value = \"char\">CHAR</option>"
                    + "\n\t\t\t\t\t\t\t\t<option value = \"varchar\">VARCHAR</option>\n\t\t\t\t\t\t\t\t<option value = \"text\">TEXT</option>\n\t\t\t\t\t\t\t\t<option value = \"blob\">BLOB</option>\n\t\t\t\t\t\t\t</optgroup>";
            String unq = "\n\t\t\t\t\t\t\t<option value = 'unique'>UNIQUE</option>"
                    + "\n\t\t\t\t\t\t\t<option value = 'primary key'>PRIMARY KEY</option>";
            br.append("<html>\n" +
                      "\t<head>\n" +
                      " \t\t<title>Import "+fileName+"</title>\n"+
                      "\t\t<link rel = \"Stylesheet\" href=\"import.css\">\n"+
                      "\t\t<link rel = \"Stylesheet\" href=\"dialog.css\">\n"+
                      "\t\t<script src = \"Import.js\"></script>\n"+
                      "\t\t<!--<script type='text/javascript' src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script>-->"+
                      "\t</head>\n" +
                      "\t<body>\n" +
                      "\t\t <div align = \"center\" data-tab = '"+fileName+"' data-db = '"+database+"'>\n" +
                      "\t\t\t<h3>Confirm the attribute specification for the Table : "+fileName+", of Database : "+database+"</h3>\n");
            br.append("\t\t\t<table>\n" +
                      "\t\t\t\t<tr class = \"b\">\n" +
                      "\t\t\t\t\t<th>Field</th>\n" +
                      "\t\t\t\t\t<th>Type</th>\n" +
                      "\t\t\t\t\t<th>Length</th>\n" +
                      "\t\t\t\t\t<th>Null</th>\n" +
                      "\t\t\t\t\t<th>Key</th>\n" +
                      "\t\t\t\t</tr>");
            for(AttributeData t : arr) {
                br.append("\n\t\t\t\t<tr>");
                br.append("\n\t\t\t\t\t<td><input type = \"text\" value = \""+t.name+"\" data-def = \""+t.name+"\"/></td>");
                br.append("\n\t\t\t\t\t<td>");
                br.append("\n\t\t\t\t\t\t<select>");
                br.append("\n\t\t\t\t\t\t\t<option value = \""+t.type.toLowerCase()+"\">"+t.type+"</option>");
                br.append(select);
                br.append("\n\t\t\t\t\t\t</select>");
                br.append("\n\t\t\t\t\t</td>");
                br.append("\n\t\t\t\t\t<td><input type = \"text\" value = \""+(t.fracLen == -1 ? t.length : t.length+t.fracLen+","+t.fracLen)+"\" data-def = \""+(t.fracLen == -1 ? t.length : t.length+t.fracLen+","+t.fracLen)+"\"></td>");
                br.append("\n\t\t\t\t\t<td><input type = \"checkbox\" checked=\"checked\"></td>");
                br.append("\n\t\t\t\t\t<td>");
                br.append("\n\t\t\t\t\t\t<select onchange = 'NotNull(this)'>");
                br.append((t.unique ? unq + "\n\t\t\t\t\t\t\t<option value = 'none'>NONE</option>" : "\n\t\t\t\t\t\t\t<option value = 'none'>NONE</option>"+unq));
                br.append("\n\t\t\t\t\t\t</select>");
                br.append("\n\t\t\t\t\t</td>");
                br.append("\n\t\t\t\t</tr>");
            }
            br.append("\n\t\t\t</table>");
            br.append("\n\t\t\t<button class = \"button\" onclick = \"go()\">Proceed</button>");
            br.append("\n\t\t\t<button class = \"resetButton\" onclick = \"reset()\">Reset</button>");
            br.append("\n\t\t</div>");
            br.append("<div align = \"center\" id = \"dialog\" class = \"scroll-down scroll\" style = 'display : none;'>\n" +
"			<div class = \"modal-content\">\n" +
"				<div style = \"text-align : left !important\">Some fields are incorrectly filled. Details follow : </div>\n" +
"				<textarea id = \"message\" style = \"text-align : left !important\" readonly>A null value cannot be accepted for this column</textarea>\n" +
"				<button class=\"dia-button\" onclick = \"this.parentElement.parentElement.style.display = 'none';\">OK</button>\n" +
"			</div>\n" +
"		</div>");
            br.append("\n\t</body>");
            br.append("\n</html>");
            br.close();
            initComponents();
            setVisible(true);
            
       } catch(Exception e) {
           error(e, this);
       }
    }
    
    public void writeJSONPage(String file) {
        try {
            FileWriter f = new FileWriter(new File(pathName+"SQLNavigator\\Import\\Import.html"));
            BufferedWriter bw = new BufferedWriter(f);
            bw.append("<html>\n" +
                      "\t<head>\n"+
                      "\t\t<title>import "+fileName+"</title>\n" +
                      "\t\t<script src=\"Import.js\"></script>\n" +
                       "\t\t<!--<script type='text/javascript' src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script>-->\n"+
                      "\t\t<link rel=\"Stylesheet\" href = \"Import.css\">\n" +
                      "\t\t<link rel=\"Stylesheet\" href = \"dialog.css\">\n" +
                      "\t\t<script>");
            FileReader fileReader = new FileReader(new File(filePath));
            BufferedReader br = new BufferedReader(fileReader);
            String json = "\n\t\t\tvar obj = ";
            String line;
            while((line = br.readLine()) != null) {
                json += "\t\t\t\t"+line+"\n";
            }
            br.close();
            json = json.substring(0,json.length()-1)+";";
            bw.append(json);
            bw.append("\n\t\t\tvar attr = function(field,type,len) {\n" +
"                this.field = field;\n" +
"                this.type = type;\n" +
"                this.len = len;\n" +
"                this.unique = \"test\";\n" +
"				this.values = [];\n" +
"\n" +
"                this.setUnique = function(uni) {\n" +
"                    this.unique = uni;\n" +
"                };\n" +
"            };\n" +
"\n" +
"            function findAttribute() {\n" +
"                if(obj.hasOwnProperty('fields') && obj.hasOwnProperty('data')) {\n" +
"                    var attributes = [];\n" +
"                    var fields = obj.fields;\n" +
"                    for(var i = 0; i < fields.length; i++) {\n" +
"                        var field = fields[i].label;\n" +
"                        var dataType = fields[i].type;\n" +
"                        var len = -1;\n" +
"                        var type = dataType.split(\"(\")[0];\n" +
"                        if(dataType.indexOf(\"(\") > -1) {\n" +
"                            len = dataType.split(\"(\")[1].split(\")\")[0];\n" +
"                        }\n" +
"                        attributes.push(new attr(field,(type == 'string' ? 'text' : type),len));\n" +
"                    }\n" +
"\n" +
"                    var data = obj.data;\n" +
"                    if(fields.length == data[0].length) {\n" +
"                        for(var i = 0; i < data.length; i++) {\n" +
"                            for(var j = 0; j < fields.length; j++) {\n" +
"                                if(attributes[j].unique == \"test\") {\n" +
"                                    attributes[j].setUnique(true);\n" +
"									attributes[j].values.push(data[i][j]);\n" +
"									\n" +
"                                }\n" +
"                                else if(attributes[j].unique) {\n" +
"                                    if(attributes[j].values.indexOf(data[i][j]) > -1) {\n" +
"                                        attributes[j].setUnique(false);\n" +
"                                    }\n" +
"									else {\n" +
"										attributes[j].values.push(data[i][j]);\n" +
"									}\n" +
"                                }\n" +
"                            }\n" +
"                        }\n" +
"                        buildTable(attributes);\n" +
"                    }\n" +
"                    else {\n" +
"                        document.getElementsByTagName('div')[0].innerHTML = \"<div class = \\\"newBody\\\" align = \\\"center\\\"><h4 style = \\\"font-family : 'Lucida Calligraphy', Serif; text-align : left !important; color : red\\\">The transfer of Data from the excel document to the SQL Table because some rows in the table had illegal values, incompatible with standard SQL datatypes.<br> This table cannot be imported. Sorry!</h4></div>\";\n" +
"                        return false;\n" +
"                    }\n" +
"                }\n" +
"                else {\n" +
"                    document.getElementsByTagName('div')[0].innerHTML = \"<div class = \\\"newBody\\\" align = \\\"center\\\"><h4 style = \\\"font-family : 'Lucida Calligraphy', Serif; text-align : left !important; color : red\\\">The transfer of Data from the excel document to the SQL Table because some rows in the table had illegal values, incompatible with standard SQL datatypes.<br> This table cannot be imported. Sorry!</h4></div>\";\n" +
"                    return false;\n" +
"                }\n" +
"            }\n" +
"\n" +
"            function buildTable(attributes) {\n" +
"                var div1 = document.getElementsByTagName('div')[0];\n" +
"                var select = \"<optgroup label=\\\"Numeric\\\"><option value = \\\"tinyint\\\">TINYINT</option>\"\n" +
"                    + \"<option value = \\\"smallint\\\">SMALLINT</option><option value = \\\"mediumint\\\">MEDIUMINT</option><option value = \\\"int\\\">INT</option>\"\n" +
"                    + \"<option value = \\\"bigint\\\">BIGINT</option><option value = \\\"numeric\\\">NUMERIC</option><option value = \\\"double\\\">DOUBLE</option>\"\n" +
"                    + \"<option value = \\\"bit\\\">BIT</option><option value = \\\"boolean\\\">BOOLEAN</option></optgroup><optgroup label=\\\"Date and time\\\">\"\n" +
"                    + \"<option value = \\\"date\\\">DATE</option><option value = \\\"datetime\\\">DATETIME</option><option value = \\\"timestamp\\\">TIMESTAMP</option>\"\n" +
"                    + \"<option value = \\\"time\\\">TIME</option><option value = \\\"year\\\">YEAR</option></optgroup><optgroup label=\\\"String\\\"><option value = \\\"char\\\">CHAR</option>\"\n" +
"                    + \"<option value = \\\"varchar\\\">VARCHAR</option><option value = \\\"text\\\">TEXT</option><option value = \\\"blob\\\">BLOB</option></optgroup>\";\n" +
"                \n" +
"                var uni = \"<option value = 'unique'>UNIQUE</option>\"\n" +
"                    + \"<option value = 'primary key'>PRIMARY KEY</option>\";\n" +
"\n" +
"                var html = \"<h3>Confirm the attribute specification for the Table : \"+div1.getAttribute('data-tab')+\", of Database : \"+div1.getAttribute('data-db')+\"</h3>\";\n" +
"\n" +
"                html += \"<table><tr class = \\\"b\\\"><th>Field</th><th>Type</th><th>Length</th><th>Null</th><th>Key</th></tr>\"\n" +
"                for(var i = 0; i < attributes.length; i++) {\n" +
"                    html += \"<tr>\";\n" +
"                    html += \"<td><input type = \\\"text\\\" value = \\\"\"+attributes[i].field+\"\\\" data-def = \\\"\"+attributes[i].field+\"\\\"></td>\";\n" +
"                    html += \"<td><select><option value = \\\"\"+attributes[i].type+\"\\\">\"+attributes[i].type.toLocaleUpperCase()+\"</option>\"+select+\"</select></td>\";\n" +
"                    html += \"<td><input type = 'text' value = \\\"\"+(attributes[i].len == -1 ? '' : attributes[i].len)+\"\\\" data-def = \\\"\"+(attributes[i].len == -1 ? '' : attributes[i].len)+\"\\\"></td>\";\n" +
"                    html += \"<td><input type = 'checkbox' checked = 'checked'></td>\";\n" +
"                    html += \"<td><select onchange = 'NotNull(this)'>\"+(attributes[i].unique == true ? uni + \"<option value = 'none'>NONE</option>\" : \"<option value = 'none'>NONE</option>\" + uni)+\"</select></td>\";\n" +
"                    html += \"</tr>\"\n" +
"                    console.log(\"attributes[\"+i+\"].field = \"+attributes[i].field);\n" +
"                    console.log(\"attributes[\"+i+\"].type = \"+attributes[i].type);\n" +
"                    console.log(\"attributes[\"+i+\"].len = \"+attributes[i].len);\n" +
"                    console.log(\"attributes[\"+i+\"].unique = \"+attributes[i].unique);\n" +
"                }\n" +
"                html += \"</table>\";\n" +
"                html += \"<button class = \\\"button\\\">Proceed</button>\";\n" +
"                html += \"<button class = \\\"resetButton\\\" onclick = \\\"reset()\\\">Reset</button>\";\n" +
"                div1.innerHTML = html;\n" +
"                var button = document.getElementsByClassName('button')[0];\n" +
"                button.addEventListener(\"click\",function(){\n" +
"                    //console.log(obj.data);\n" +
"                    go(obj.data);\n" +
"                });\n" +
"            } \n" +
"        </script>\n" +
"    </head>\n" +
"    <body onload = \"findAttribute()\">\n" +
"        <div align = \"center\" data-tab = '"+fileName+"' data-db = '"+database+"'>\n" +
"\n" +
"        </div>\n" +
"<div align = \"center\" id = \"dialog\" class = \"scroll-down scroll\" style = 'display : none;'>\n" +
"			<div class = \"modal-content\">\n" +
"				<div style = \"text-align : left !important\">Some fields are incorrectly filled. Details follow : </div>\n" +
"				<textarea id = \"message\" style = \"text-align : left !important\" readonly>A null value cannot be accepted for this column</textarea>\n" +
"				<button class=\"dia-button\" onclick = \"this.parentElement.parentElement.style.display = 'none';\">OK</button>\n" +
"			</div>\n" +
"		</div>" +
"    </body>\n" +
"</html>");
            bw.close();
            setVisible(true);
            initComponents();
        } catch(Exception e) {error(e, this);}
    }
    
    public void initComponents() {
        this.setLayout(new BorderLayout());
        add(FXPanel,BorderLayout.CENTER);
        if(fileExt.equals("xls") || fileExt.equals("xlsx"))
            exec = new QueryExecutor(conn, this,data.arr,rowSize,colSize);
        else if(fileExt.equals("json"))
            exec = new QueryExecutor(conn, this,new ArrayList<>(),-1,-1);
        createScene(); 
        setIconImage(new ImageIcon(pathName+"SQLNavigator\\Icons\\Icon.png").getImage());
        this.setResizable(false);
        this.setSize(900,600);
    }
    
    public void createScene() {
        Platform.runLater(()->{
            WebView view = new WebView();
            engine = view.getEngine();
            FXPanel.setScene(new Scene(view));
            engine.load(url+"SQLNavigator/Import/Import.html");
            engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov,Worker.State oldState, Worker.State newState) -> {  
                            if (newState == Worker.State.SUCCEEDED) {
                                //engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                                JSObject win =  (JSObject) engine.executeScript("window");
                                win.setMember("SQL",exec);
                            }
                        }
                );
            engine.titleProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ImportTool.this.setTitle(newValue);
                            }
                        });
                    }
                });
        });
    }
    
    public String[][] vectorToString(ArrayList <ArrayList<String>> vec){
        System.out.println("rowSize = "+rowSize+" and colSize = "+colSize);
        String [][] a = new String[rowSize][colSize];
        System.out.println("List size = "+vec.size()+ "vec rows = "+vec.size()/colSize);
        int i = 0;
        Iterator <ArrayList<String>> itr = vec.iterator();
        while(itr.hasNext()) {
            ArrayList<String> temp = itr.next();
            Iterator<String> itr1= temp.iterator();
            while(itr1.hasNext()) {
                //double d = new Double(i) / new Double(colSize);
                //int r = Integer.parseInt(String.valueOf(d));
                a[i/colSize][i%colSize] = itr1.next();
            }
            i++;
        }
        return a;
    }
    
    public ExcelData getExcelData(String fileName) {
        ArrayList <AttributeData> tupleData = new ArrayList<>();
        ArrayList <ArrayList<String>> excelData = new ArrayList<>();        
        try {
            FileInputStream file = new FileInputStream(new File(fileName));
            if(fileExt.equals("xls")) {
                workbook1 = new HSSFWorkbook(file);
                sheet1 = workbook1.getSheetAt(0);
                rowIterator = sheet1.iterator();
            }
            else if(fileExt.equals("xlsx")) {
                workbook2 = new XSSFWorkbook(file);
                sheet2 = workbook2.getSheetAt(0);
                rowIterator = sheet2.iterator();
            }
            
            
            Row row1 = rowIterator.next();
            Iterator <Cell> cellIterator1 = row1.cellIterator();
            int i = 0;
            ArrayList <AttributeData> tuples = new ArrayList<>();
            ArrayList <String> rowData = new ArrayList<>();
            while(cellIterator1.hasNext()) {
                Cell cell = cellIterator1.next();
                CellType type = cell.getCellTypeEnum();
                AttributeData temp = new AttributeData();
                switch(type) {
                    case BOOLEAN:
                        temp.setName(String.valueOf(cell.getBooleanCellValue()));
                        temp.setIndex(i);
                        break;
                    case NUMERIC:
                        temp.setName(String.valueOf(cell.getNumericCellValue()));
                        temp.setIndex(i);
                        break;
                    case STRING:
                        temp.setName(cell.getStringCellValue());
                        temp.setIndex(i);
                }
                tuples.add(new AttributeData(temp));
                i++;
            }
            colSize = i;
            int rs = 0;
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator <Cell> cellIterator = row.cellIterator();
                int j = 0, k = 0;
                while(cellIterator.hasNext()) {
                    while(k < tuples.get(0).index) {
                        cellIterator.next();
                        k++;
                    }
                    Cell cell = cellIterator.next();
                    CellType type = cell.getCellTypeEnum();
                    String typeOf = "";
                    int len = -1;
                    int fracLen = -1;
                    boolean frac = false;
                    String lv = "";
                    long large = -1; 
                    switch(type) {
                        case _NONE:
                                typeOf = "_NONE";
                                len = -1;
                                break;
                        case BLANK:
                                typeOf = "_BLANK";
                                len = 0;
                                break;
                        case BOOLEAN:
                                typeOf = "BOOLEAN";
                                lv = String.valueOf(cell.getBooleanCellValue());
                                len = lv.length();
                                rowData.add(lv);
                            break;
                        case NUMERIC:
                            typeOf = "NUMERIC";
                            Double d = cell.getNumericCellValue();
                            String num = String.valueOf(d);
                            if(num.split("\\.")[1].contains("E")) {
                                java.math.BigDecimal bd = new java.math.BigDecimal(d);
                                num = bd.toString();
                            }
                            //System.out.println(num);
                            lv = num;
                            //System.out.println(num);
                            if(num.contains(".")) {
                                
                                String ta[] = num.split("\\.");
                                if(ta[1].equals("0")) {
                                    len = ta[0].length();
                                    large = Long.valueOf(ta[0]);
                                    rowData.add(ta[0]);
                                }
                                else {
                                    frac = true;
                                    len = ta[0].length();
                                    fracLen = ta[1].length();
                                    rowData.add(num);
                                }
                            }
                            else {
                                len = num.length();
                                large = Long.valueOf(num);
                                rowData.add(num);
                            }                             
                            break;
                        case STRING:
                            typeOf = "STRING";
                            lv = cell.getStringCellValue();
                            len = lv.length();
                            rowData.add(lv);
                            break;
                    }
                    
                    /** */
                    excelData.add(new ArrayList<>(rowData));
                    rowData.clear();
                    //System.out.println(len);
                    AttributeData t = tuples.get(j);
                    if(t.type.equals("NONE")) {
                        t.setType(typeOf);
                        t.setLength(len);
                        t.setFraction(frac);
                        t.setFracLen(fracLen);
                        t.setUnique(true);
                        t.lastValue.set(0, lv);
                        t.largestValue = large;
                    }
                    else {
                        if(!t.type.equals("STRING") && typeOf.equals("STRING")) {
                            t.setType("STRING");
                        }
                        if(len > t.length) {
                            t.length = len;
                        }
                        if(t.fraction == false && frac == true) 
                            t.setFraction(frac);
                        if(fracLen > t.fracLen)
                            t.setFracLen(fracLen);
                        if(t.unique && !t.lastValue.get(0).equals("TEST")){
                            if(t.lastValue.contains(lv))
                                t.setUnique(false);
                        }
                        if(t.largestValue != -1 && large > t.largestValue) {
                            t.largestValue = large;
                        }
                    }
                    t.setLastValue(lv);
                    j++;
                }
                tupleData = new ArrayList<>(tuples);
                rs++;
            }
            rowSize = rs;
        }catch(Exception e) {e.printStackTrace();}
        for(AttributeData d : tupleData) {
            if(d.type.equals("NUMERIC")) {
                if(d.fraction) {
                    if(d.fracLen <= 30)
                        d.setType("NUMERIC");
                    else if(d.fracLen <= 53)
                        d.setType("DOUBLE");                       
                }
                else {
                    System.out.println("largestValue = "+d.largestValue);
                    if(d.largestValue > 0)
                        d.setType("TINYINT");
                    if(d.largestValue > 127)
                        d.setType("SMALLINT");
                    if(d.largestValue > 32767)
                        d.setType("MEDIUMINT");
                    if(d.largestValue > 8388607)
                        d.setType("INT");
                    if(d.largestValue > 2147483647)
                        d.setType("BIGINT");
                }
            }
            else if(d.type.equals("STRING")) {
                if(d.length <= 65535)
                    d.setType("VARCHAR");
                else
                    d.setType("TEXT");
            }
            else if(d.type.equals("_BLANK")) {
                d.setType("CHAR");
                d.setLength(1);
            }
        }
        return new ExcelData(tupleData,excelData);    
    }
    
    public class QueryExecutor {
        public Connection conn;
        public JFrame prnt;
        public ArrayList<ArrayList<String>> array;
        public int row;
        public int col;
        public QueryExecutor(Connection con, JFrame par, ArrayList<ArrayList<String>> arr, int r, int c) {
            conn = con;
            prnt = par;
            array = arr;
            row = r;
            col = c;
        }
        
        public boolean execQuery(String query){
            boolean success = false;
            try {
                Statement st = conn.createStatement();
                st.executeUpdate(query);
                success = true;
             
            } catch(SQLException e) {error(e, prnt); e.printStackTrace();}
            return success;
        }
    }
    
    public class AttributeData {
        public String name;
        public int length;
        public String type;
        public int index;
        public boolean fraction;
        public int fracLen;
        public ArrayList<String> lastValue;
        public boolean unique;
        public long largestValue;

        public AttributeData() {
            name = "";
            length = -1;
            type = "NONE";
            index = -1;
            fraction = false;
            fracLen = -1;
            unique = true;
            lastValue = new ArrayList<>();
            lastValue.add("TEST");
            largestValue = -1;
        }

        public AttributeData(AttributeData o) {
            name = o.name;
            length = o.length;
            type = o.type;
            index = o.index;
            fraction = o.fraction;
            fracLen = o.fracLen;
            unique = o.unique;
            lastValue = o.lastValue;
            largestValue = o.largestValue;
        }

        public void setName (String name) {
            this.name = name;
        }
        public void setLength(int l) {
            length = l;
        }
        public void setType(String t) {
            type = t;
        }
        public void setIndex(int i) {
            index = i;
        }
        public void setFraction(boolean f) {
            fraction = f;
        }
        public void setFracLen(int frl) {
            fracLen = frl;
        }
        public void setUnique(boolean u) {
            unique = u;
        }
        public void setLastValue(String lv) {
            lastValue.add(lv);
        }
        
        public void setLargestValue(long large) {
            largestValue = large;
        }
        @Override
        public String toString() {
            return "Name : "+name+", Length : "+length+", Type : "+type+", Index = "+index+", Fraction : "+fraction+", FracLen : "+fracLen+", Unique : "+unique;
        }
    }

    public class ExcelData {
        public ArrayList <AttributeData> tuples;
        public ArrayList <ArrayList<String>> arr;

        public ExcelData(ArrayList <AttributeData> tup, ArrayList<ArrayList<String>> data) {
            tuples = new ArrayList<>(tup);
            arr = new ArrayList<>(data);
        }
        
        public ArrayList<ArrayList<String>> giveArr() {
            return arr;
        }
    }
}
