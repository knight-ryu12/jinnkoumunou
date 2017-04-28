package net.chromaryu.fakeai.thread.ifrit;

import net.chromaryu.fakeai.fakeai;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusUpdate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.chromaryu.fakeai.fakeai.*;
/**
 * Created by midgard on 17/04/16.
 */
// Read Queue. Respond.

public class ResponceThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger("Ifrit Engine");
    private SecureRandom sr;
    public boolean EXIT = false;
    public ResponceThread() {
        //IFRIT ENGINE!

        logger.info("Hi Chisaki. I am IfritEngine.");
        try {
            sr = SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        //while (!fakeai.cld.isEmpty()) {
        while (!EXIT) {
            try {
            synchronized (this) {

                if (fakeai.cld.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Status status = fakeai.cld.poll();
                logger.info("I:" + status.getText());

                    if (!status.getText().startsWith("RT")) {
                        for (String b64keyword : testal.keySet()) {
                            logger.debug(b64keyword);
                            String keyw = new String(Base64.getDecoder().decode(b64keyword.getBytes()), "UTF-8");
                            if (status.getText().matches(keyw)) {
                                List<String> ls = testal.get(b64keyword);
                                if (ls.size() > 0) {

                                    logger.info("Got Matched Size:" + ls.size() + " Resp Number:"+atomicInteger.getAndIncrement());
                                    String resp = new String(Base64.getDecoder().decode(ls.get(sr.nextInt(ls.size()))), "UTF-8");
                                    logger.info("RespSend:" + resp);
                                    fakeai.tw.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + resp).inReplyToStatusId(status.getId()));
                                }
                            }
                        }
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }


        }
    }
    //}//
}
