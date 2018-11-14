package recommendation;

/**
 *
 * @author ninja
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqLiteHelper {

    public static Connection con;
    public static Statement stmt;

    public SqLiteHelper() throws ClassNotFoundException, SQLException {

    	
        this.con = null;
        this.stmt = null;
        this.getConnection();
    }

    public static void getConnection() {
    	

        try {
            Class.forName("org.sqlite.JDBC"); //dynamically loaded
            //String DBpath = "jdbc:sqlite:C:\\Users\\Nada Essam\\Documents\\GitHub\\DataVis\\Parser.db"; // Load an SQLite DB
            
           String DBpath="jdbc:sqlite:/media/ninja/Work/Graduation Project/graduation_project_git/VisDiscovery/Yellow_Taxi.db";
           //String DBpath = "jdbc:sqlite:Parser.db"; // Load an SQLite DB
            
            con = DriverManager.getConnection(DBpath); // create a connection to the database
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connection to SQLite has been established.");
    }

    public ResultSet executeQuery(String query) {
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(SqLiteHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

}
