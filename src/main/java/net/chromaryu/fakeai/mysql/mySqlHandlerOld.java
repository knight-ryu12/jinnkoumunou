package net.chromaryu.fakeai.mysql;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.config.Config;
import net.chromaryu.fakeai.fakeai;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by midgard on 17/04/13.
 */
public class mySqlHandlerOld {
    Connection connection;
    static Logger logger = LoggerFactory.getLogger("MySQLHandler");
    private String url, username, password;
    public mySqlHandlerOld(Config c) {
        try {
            url = c.getMysql().get("url");
            username = c.getMysql().get("username");
            password = c.getMysql().get("password");
            //str = om.readValue(new File("config.json"),new TypeReference<Map<String,String>>(){});
            //System.out.println(config.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void close(Connection connection) {
        try {
            connection.close();
            logger.info("Disconnected!");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public Connection open() throws SQLException {
        Connection conn = null;
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,username,password);
            logger.info("Connected!");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    public void makeTable() {
        Connection con = null;
        try {
            con = open();
            PreparedStatement ps = con.prepareStatement(
                    "create table IF NOT EXISTS chisakiTable\n" +
                            "(" +
                            "id int not null auto_increment primary key," +
                            "keyword varchar(1000) null," +
                            "responce varchar(1000) null" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            ps.execute();
            logger.info("Table Initialized!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(con);
        }
    }
    public void addResponce(String k,String v) {
        Connection connection = null;
        try {
            connection = open();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO chisakiTable(keyword, responce) VALUES (?,?)"
            );
            ps.setString(1,k);
            ps.setString(2,v);
            ps.execute();
            logger.info("Inserted k:"+k+" v:"+v);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
    }
    public ListMultimap<String, String> getAllResponce() {
        Connection connection = null;
        ResultSet rs = null;
        ListMultimap<String,String> multimap = ArrayListMultimap.create();
        try {
            connection = open();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM chisakiTable"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                multimap.put(rs.getString("keyword"),rs.getString("responce"));
            }
            logger.info("Done getting all.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
        return multimap;
    }

}
