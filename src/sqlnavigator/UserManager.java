/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlnavigator;

/**
 * Oct-10-2016
 * @author SWAPNIL
 */
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.web.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import netscape.javascript.JSObject;
import java.io.*;
import java.util.*;
import static sqlnavigator.Error_Message.error;

public class UserManager extends JFrame {
    
    final JFXPanel FXPanel = new JFXPanel();
    public WebEngine engine;
    SQLNavigator root = new SQLNavigator();
    UserHandler handler = new UserHandler(engine,this);
    String path;
    
    public UserManager() {
        buildUserLogin();
    }
    
    private void createScene() {

        Platform.runLater(() -> {


                WebView view = new WebView();
                //view.setContextMenuEnabled(false);
                System.out.println("This happened");
                engine = view.getEngine();
                System.out.println("So did this");
                FXPanel.setScene(new Scene(view));
                engine.load(path+"SQLNavigator/User%20Login/login.html");
                engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> ov,State oldState, State newState) -> {  
                            if (newState == State.SUCCEEDED) {
                                JSObject win =  (JSObject) engine.executeScript("window");
                                win.setMember("Handler", handler);
                            }
                            engine.executeScript("run()");
                        }
                );
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                UserManager.this.setTitle(newValue);
                            }
                        });
                    }
                });
        });
    }
    


    
    public void buildUserLogin() {
        this.setLayout(new BorderLayout());
        add(FXPanel,BorderLayout.CENTER);
        try {
            path = root.getURL();
        } catch(IOException e) {error(e, this);}
        createScene();
        handler.getUsers();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                setVisible(false);
                try {
                    finalize();
                } catch(Throwable ex) {ex.printStackTrace();}
            }      
        });
        try {
            setIconImage(new ImageIcon(root.getPath()+"SQLNavigator\\Icons\\Icon.png").getImage());
        }catch(IOException e) {error(e, this);}
        setTitle("User Login");
        setSize(900,700);
        //this.setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    public class UserHandler {
        WebEngine engine;
        public SQLUsers user = new SQLUsers();
        JFrame parent;
        public String lurl, path;
        public ArrayList <SQLUser> Users = new ArrayList<>();
        public Vector <UserAccountInformation> accounts;
        public int index ;
        
        
        public UserHandler(WebEngine realEngine, JFrame frame) {
            engine = realEngine;
            parent = frame;
        }
        
        
        public void getUsers() {
            try {
                lurl = new SQLNavigator().getURL();
                path = new SQLNavigator().getPath();
                File f = new File(root.getPath()+"SQLNavigator\\Users.ser");
                if(f.exists() && !f.isDirectory()) {
                    FileInputStream fis = new FileInputStream(root.getPath()+"SQLNavigator\\Users.ser");
                    ObjectInputStream ois =  new ObjectInputStream(fis);
                    while(fis.available() > 0) {
                        Users.add((SQLUser)ois.readObject());
                    }
                }
                else {
                    FileWriter fw = new FileWriter(f);
                }
            }catch(IOException | ClassNotFoundException e) {
                 error(e, parent);
            }
        }
        
        public boolean Login(String username, String password) {
            boolean login = false;
            if(Users.size() == 0);
            else {
                int i = 0;
                for(SQLUser u : Users) {
                    if(u.username.equals(username) && u.password.equals(password)) {
                        user.copy(Users.get(Users.indexOf(u)));
                        index = i;
                        System.out.println("i = "+i+" index = "+index);
                        System.out.println(user);

                        for(UserAccountInformation g : user.accounts)
                            System.out.println(g);
                        login = true;
                        break;
                    }
                    i++;
                }
            }
            return login;
        }
        
        public boolean checkDuplicate (String username) {
            boolean duplicate = false;
            if(Users.size() == 0);
            else {
                for(SQLUser u : Users)
                    if(u.username.equals(username)) {
                        duplicate = true;
                    }
            }
            return duplicate;
        }
        
        public boolean saveUserInformation() {
            
            System.out.println("This happened");
            boolean saved = false;
            System.out.println("So did this");
            Users.get(index).copyFrom(user);
            try { 
                FileOutputStream fs = new FileOutputStream(path+"SQLNavigator\\Users.ser");
                ObjectOutputStream os = new ObjectOutputStream(fs);
                for(SQLUser u : Users)
                    os.writeObject(u);
                saved = true;
                os.close();
                fs.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                //error(e, parent);
            }
            return saved;
        }
        
        public boolean Registration(String username, String password) {
            boolean registered = false;
            try{
                Vector <sqlnavigator.DriverData> drivers = new Vector<>();
                drivers.add(new sqlnavigator.DriverData("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/","mysql.txt","MySQL"));
                drivers.add(new sqlnavigator.DriverData("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://localhost:1443;instance=SQLEXPRESS;databaseName=master;","msaccess.txt","Microsoft SQL Server"));
                drivers.add(new sqlnavigator.DriverData("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:SYSTEM/user@localhost:1521:xe","oracle.txt","Oracle"));            
                Vector <sqlnavigator.UserAccountInformation> accounts = new Vector<>();
                for(sqlnavigator.DriverData driver : drivers)
                    accounts.add(new sqlnavigator.UserAccountInformation("", "", driver));
                sqlnavigator.SQLUser new_user = new sqlnavigator.SQLUser(username,password,"",accounts);
                Users.add(new_user);
                index = Users.size() - 1;
                user.copy(new_user);
                FileOutputStream fs = new FileOutputStream(path+"SQLNavigator\\Users.ser");
                ObjectOutputStream os = new ObjectOutputStream(fs);
                for(SQLUser u : Users)
                    os.writeObject(u);
                registered = true;
            } catch(Exception e) {error(e, parent);}
            return registered;
        }
    }

    
    public class SQLUsers implements Serializable{
        public String username;
        String password;
        public String email;
        public Vector <UserAccountInformation> accounts;
        public SQLUsers() {
            username = "user";
            password = "pass";
            email = "mail";
            accounts = new Vector <>(5);
        }
        public SQLUsers(String user, String pass, String em, Vector <UserAccountInformation> accs) {
            username = user;
            password = pass;
            email = em;
            accounts = accs;
        }
        public SQLUsers(SQLUsers user) {
            username = user.username;
            password = user.password;
            accounts = user.accounts;
            email = user.email;
        }

        public void copy(SQLUser user) {
            username = user.username;
            password = user.password;
            email = user.email;
            copyaccounts(user);
        }
        
        public void copy(SQLUsers user) {
        username = user.username;
        password = user.password;
        accounts = user.accounts;
    }

        public void setData (String user, String Pass, String em) {
            username = user;
            password = Pass;
            email = em;
        }
        
        public void setEmail(String em) {
            email = em;
        }
        public void copyaccounts(SQLUser user) {
            Vector <UserManager.UserAccountInformation> accs = new Vector <> ();
            for(sqlnavigator.UserAccountInformation g : user.accounts) {
                String usern = g.username;
                String pass = g.password;
                sqlnavigator.DriverData d  = g.driver;
                UserManager.DriverData dr = new UserManager.DriverData(d.Driver,d.url,d.file,d.title);
                dr.setEnabled(d.isEnabled());
                dr.setIntegratedSecurity(d.windowsAuthentication());
                accs.add(new UserManager.UserAccountInformation(usern,pass,dr));
            }
            accounts = accs;
        }
        @Override
        public String toString() {
            return username + " " + password;
        }
    }

    public class UserAccountInformation implements Serializable {
        public String username;
        public String password;
        public DriverData driver;
        public UserAccountInformation(String user, String pass, DriverData dr) {
            username = user;
            password = pass;
            driver = dr;
        }

        public void setData (String user, String Pass) {
            username = user;
            password = Pass;
        }

        @Override
        public String toString() {
            return driver.title + " " + username + " " + password ;
        }
    }

    public class DriverData implements Serializable {
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
        public void setData(String d, String u, String t) {
            Driver = d;
            url = u;
            title = t;
        }
        public void setFileInfo (String f) {
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
}
