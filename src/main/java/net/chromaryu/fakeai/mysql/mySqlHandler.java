package net.chromaryu.fakeai.mysql;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.chromaryu.fakeai.config.Config;
import net.chromaryu.fakeai.fakeai;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

/**
 * Created by midgard on 17/04/25.
 */
public class mySqlHandler {
    private static HikariDataSource ds = null;
    public mySqlHandler(Config config) {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(config.getMysql().get("url"));
        hc.setUsername(config.getMysql().get("username"));
        hc.setPassword(config.getMysql().get("password"));
        hc.addDataSourceProperty("cachePrepStmts","true");
        hc.addDataSourceProperty("prepStmtCacheSize","250");
        hc.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
        ds = new HikariDataSource(hc);
    }
    public void makeTable() {
        try(Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "create table IF NOT EXISTS chisakiTable\n" +
                            "(" +
                            "id int not null auto_increment primary key," +
                            "keyword varchar(1000) null," +
                            "responce varchar(1000) null" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS authTable" +
                            "(" +
                            "id int not null auto_increment PRIMARY KEY," +
                            "authkey VARCHAR(256) null," +
                            "timedkey VARCHAR(256) null," +
                            "mtimeactive VARCHAR(256) null" +
                            ") ENGINE=InnoDB DEFAULT CHARSET =utf8"
            );
            ps2.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public byte[] getOTK(String u) {
        return new byte[2*32];
    }
    public byte[] addOTK(String authkey) { // Returns Stringed OTK
        //OTK calculated with authkey. it's pair
        return new byte[2*32];
    }
    public void addResponce(String k,String v) {
        try(Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO chisakiTable(keyword, responce) VALUES (?,?)");
            ps.setString(1,k);
            ps.setString(2,v);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ListMultimap<String,String> getAllResponce() {
        ListMultimap<String,String> multimap = ArrayListMultimap.create();
        try(Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM chisakiTable"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                multimap.put(rs.getString("keyword"),rs.getString("responce"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multimap;
    }
}
