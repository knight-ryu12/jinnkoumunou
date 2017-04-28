package net.chromaryu.fakeai.thread;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.fakeai;

import java.util.concurrent.Semaphore;

public class MySqlSyncAsTask implements Runnable {
    private Semaphore sm;
    public MySqlSyncAsTask(Semaphore sm) {
        this.sm = sm;
    }

    @Override
    public void run() {
        try {
            sm.acquire();
            ListMultimap<String,String> map= fakeai.mysql.getAllResponce();
            fakeai.respNum.incrementAndGet();
            fakeai.testal = ArrayListMultimap.create(map);
            //System.out.println(map);
            //fakeai.testal.putAll(map);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            sm.release();
        }
    }
}
