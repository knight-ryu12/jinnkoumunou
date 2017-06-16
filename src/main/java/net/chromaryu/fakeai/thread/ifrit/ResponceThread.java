package net.chromaryu.fakeai.thread.ifrit;

import net.chromaryu.fakeai.api.apiAiHandler;
import net.chromaryu.fakeai.fakeai;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.chromaryu.fakeai.fakeai.*;

/**
 * Created by midgard on 17/04/16.
 */
// Read Queue. Respond.

public class ResponceThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger("Ifrit Engine");
    private SecureRandom sr;
    public boolean EXIT = false;
    private boolean tweeted = false;
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
                    tweeted = false;
                    if (!status.getText().startsWith("RT")) {
                        if (status.getInReplyToUserId() == fakeai.tw.getId()) {

                            continue;
                        }
                        //logger.info(apiAiHandler.submit(status.getText(), String.valueOf(fakeai.apiaiReqC.getAndIncrement())));
                        for (String b64keyword : testal.keySet()) {
                            if (!tweeted) {
                                logger.debug(b64keyword);
                                String keyw = new String(Base64.getDecoder().decode(b64keyword.getBytes()), "UTF-8");
                                String[] arg = keyw.split(":");
                                switch (arg.length) {
                                    case 1:
                                        if (status.getText().matches(arg[0])) {
                                            List<String> ls = testal.get(b64keyword);
                                            if (ls.size() > 0) {
                                                tweet(sr, ls, status);
                                                tweeted = true;
                                            }
                                        }
                                        break;
                                    case 2:
                                        switch (arg[0]) {
                                            case "T":
                                                LocalDateTime ldt = LocalDateTime.now();
                                                if (status.getText().matches(arg[1])) {
                                                    List<String> ls = testal.get(b64keyword);
                                                    if (ls.size() > 0) {
                                                        for (String s : ls) {
                                                            String[] a = new String(Base64.getDecoder().decode(s), "UTF-8").split(":");
                                                            if (a.length > 1) {
                                                                if (a[0].equals(String.valueOf(ldt.getHour()))) {
                                                                    tweet(status, a[1]);
                                                                    tweeted = true;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;

                                        }
                                        break;
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


    public static void tweet(SecureRandom sr, List<String> ls,Status status) {

        try {
            logger.info("Got Matched Size:" + ls.size() + " Resp Number:" + atomicInteger.getAndIncrement());
            String resp = null;
            resp = new String(Base64.getDecoder().decode(ls.get(sr.nextInt(ls.size()))), "UTF-8");
            logger.info("RespSend:" + resp);
            fakeai.tw.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + resp).inReplyToStatusId(status.getId()));
        } catch (UnsupportedEncodingException | TwitterException e) {
            e.printStackTrace();
        }

    }
    public static void tweet(Status status,String s) {
        try {
            fakeai.tw.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + s).inReplyToStatusId(status.getId()));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    //}//
}
