package net.chromaryu.fakeai.config;

import java.util.HashMap;

/**
 * Created by midgard on 17/04/13.
 */
public class Config {
    private HashMap<String,String> twitter,mysql;

    public HashMap<String, String> getTwitter() {
        return twitter;
    }

    public HashMap<String, String> getMysql() {
        return mysql;
    }

    public void setTwitter(HashMap<String, String> twitter) {

        this.twitter = twitter;
    }

    public void setMysql(HashMap<String, String> mysql) {
        this.mysql = mysql;
    }

    @Override
    public String toString() {
        return "config{"+twitter.toString()+","+mysql.toString()+"}";
    }
}

