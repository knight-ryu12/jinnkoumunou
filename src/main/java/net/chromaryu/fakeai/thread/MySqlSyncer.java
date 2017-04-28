package net.chromaryu.fakeai.thread;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.fakeai;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by midgard on 17/04/13.
 */
public class MySqlSyncer extends TimerTask {
    @Override
    public void run() {
        try {
            ListMultimap<String,String> map= fakeai.mysql.getAllResponce();
            fakeai.testal = ArrayListMultimap.create(map);
            fakeai.respNum.incrementAndGet();
            //fakeai.testal.putAll(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
