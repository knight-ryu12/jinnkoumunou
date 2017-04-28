package net.chromaryu.fakeai.thread.ifrit;

import net.chromaryu.fakeai.fakeai;

import java.util.Base64;
import java.util.TimerTask;

/**
 * Created by midgard on 17/04/28.
 */
public class StatusThread extends TimerTask{
    @Override
    public void run() {
        // Ready for Some Encryption.
        //StringBuilder sb = new StringBuilder();
        try {
            String msg = "msg:"+fakeai.atomicInteger.get()+ " requestMySQL:"+fakeai.respNum.get();
            byte[] enc = fakeai.cipher.update(msg.getBytes("UTF-8"));
            fakeai.tw.sendDirectMessage("Chromaryu", Base64.getEncoder().encodeToString(enc));

            //sb.append(fakeai.tw.getRateLimitStatus().containsKey())
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
