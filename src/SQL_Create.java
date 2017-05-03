package sqlnavigator;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.*;
import sqlnavigator.SQLViewer;
import sqlnavigator.UserAccountInformation;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;
import static sqlnavigator.Error_Message.error;


public class SQL_Create extends javax.swing.JFrame {
    UserAccountInformation user;
    Vector <Database> dbinfo = new Vector<>();
    final JFXPanel FXPanel = new JFXPanel();
    public WebEngine engine;
    SQLNavigator root = new SQLNavigator();
    String path;
    DBList dblist;
    Connection con;
    executeSQL exec;
    
    
    public SQL_Create(UserAccountInformation u) {
        user = u;
        fetch_dbinfo();
        buildCreateMenu();
        connect(user);
    }
    
     private void createScene() {
        Platform.runLater(() -> {


                WebView view = new WebView();
                //view.setContextMenuEnabled(false);
                System.out.println("This happened");
                engine = view.getEngine();
                System.out.println("So did this");
                FXPanel.setScene(new Scene(view));
                engine.load(path+"SQLNavigator/Create/home.html");
                engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov,Worker.State oldState, Worker.State newState) -> {  
                            if (newState == Worker.State.SUCCEEDED) {
                                exec.use_db(dblist.dblist.get(0).database);
                                dbinfo.removeAllElements();
                                fetch_dbinfo();
                                JSObject win =  (JSObject) engine.executeScript("window");
                                win.setMember("DB", dblist);
                                win.setMember("SQL", exec);
                                engine.executeScript("run()");
                            }
                            //engine.executeScript("run()");
                        }
                );
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                SQL_Create.this.setTitle(newValue);
                            }
                        });
                    }
                });
        });
    }
     
        public void buildCreateMenu() {
            this.setLayout(new BorderLayout());
            add(FXPanel,BorderLayout.CENTER);
            try {
                path = root.getURL();
            } catch(IOException e) {error(e, this);}
            createScene();
            dblist = new DBList(dbinfo);
            exec = new executeSQL(con, this);
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
            setTitle("SQL Navigator - Create");
            setSize(900,700);
            //this.setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    
    
    public void fetch_dbinfo() {
        try {
            SQLViewer vwr = new SQLViewer(user);
            Vector <String> dbs = vwr.Give_Database();
            for(String g : dbs) {
                Vector <String> tables = vwr.Give_Tables(g);
                //System.out.println(g);
                dbinfo.add(new Database(g,tables));
            }
        } catch(Exception e) {
            error(e, this);
        }
    }
    
    public void connect(UserAccountInformation u) {
        try {
            Class.forName(u.driver.Driver);
            con = DriverManager.getConnection(u.driver.url,u.username,u.password);
            Statement st = con.createStatement();
        } catch(Exception e) {error(e, this);}
    }
    
    public class DBList {
        public Vector <Database> dblist;
        
        public DBList(Vector <Database> List) {
            dblist = List;
        }
    }
    
    public class Database {
        public String database;
        public Vector <String> tables;

        public Database(String db, Vector<String> tab) {
            database = db;
            tables = tab;
        }
    }
    
    public class Table {
        public String Field;
        public String Type;
        public String Null;
        public String Key;
        public String Extra;

        public Table(String F, String T, String N, String K, String E) {
            Field = F;
            Type = T;
            Null = N;
            Key = K;
            Extra = E;
        }
    }
    
    public class executeSQL {
        public Connection conn;
        public JFrame prnt;
        
        public executeSQL(Connection con, JFrame p) {
            conn = con;
            prnt = p;
        }
        
        public boolean use_db(String db) {
            boolean success = false;
            try {
                Statement st = con.createStatement();
                st.executeUpdate("use "+db+"");
                success = true;
            } catch(SQLException e) {error(e, prnt);}
            return success;
        }
        
        public String getIndex(String table,String column) {
            String index = "";
            try {
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("show index from "+table+" where Column_name = '"+column+"'");
                res.next();
                index = res.getString("Key_name");
                
            } catch(SQLException e) {}
            System.out.println(index);
            return index;
        }

        public ArrayList<SQL_Create.Table> desc_table(String table) {
            ArrayList<SQL_Create.Table> attrib = new ArrayList<>();
            try {
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("desc `" + table + "`;");
                res.getMetaData();
                /*System.out.println("Is res false = "+*/
                res.next()/*)*/;
                do {
                    String F = res.getString(1);
                    String T = res.getString(2);
                    String K = res.getString(3);
                    String N = res.getString(4);
                    String E = res.getString(5);
                    attrib.add(new Table(F, T, K, N, E));
                } while (res.next());
            } catch(SQLException e) {error(e, prnt);}
            return attrib;
        }
        public boolean execQuery(String query){
            boolean success = false;
            try {
                Statement st = con.createStatement();
                st.executeUpdate(query);
                success = true;
            } catch(SQLException e) {
                success = false;
                error(e, prnt);
            }
            return success;
        }
        public ArrayList<String> giveCharSets() {
            ArrayList<String> charSet = new ArrayList<>();
            try {
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("select character_set_name from information_schema.character_sets;");
                while(res.next()) {
                    charSet.add(res.getString("character_set_name"));
                }
            } catch(SQLException e) {}
            return charSet;
        }
        public ArrayList<String> giveCollate() {
            ArrayList<String> collate = new ArrayList<>();
            try {
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("select default_collate_name from information_schema.character_sets;");
                while(res.next()) {
                    collate.add(res.getString("default_collate_name"));
                }
            } catch(SQLException e) {}
            return collate;
        }
        
        public ArrayList<String> giveDBInfo(String db) {
            ArrayList <String> info = new ArrayList<>();
            try {
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("select DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME from information_schema.schemata where SCHEMA_NAME = '"+db+"';");
                res.next();
                info.add(res.getString("DEFAULT_CHARACTER_SET_NAME"));
                info.add(res.getString("DEFAULT_COLLATION_NAME"));
            } catch(SQLException e) {error(e, prnt);}
            return info;
        }        
    }
}