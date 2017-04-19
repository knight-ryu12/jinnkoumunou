package net.chromaryu.fakeai;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.collect.ArrayListMultimap;
import twitter4j.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.chromaryu.fakeai.fakeai.testal;
import static net.chromaryu.fakeai.fakeai.FOLLOWED;

/**
 * Created by midgard on 17/04/08.
 */
public class Stream extends UserStreamAdapter {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    //Stream() {
    //    testal.put("こんにちは","ちさき「こんにちは！」");
    //}
    @Override
    public void onStatus(Status status) {
        try {
            //ArrayList<String> strings = null;
            SecureRandom sr = SecureRandom.getInstance("NativePRNGNonBlocking");
            LocalDateTime ldt = LocalDateTime.now();
            String str = "fav:" + status.getFavoriteCount() + " rt:" + status.getRetweetCount();
            System.out.println(ldt.format(dtf) + " @" + status.getUser().getScreenName() + " " + str + " " + status.getText());
            // Checking for Word
            if (status.getUser().getId() != fakeai.tw.getId()) {
                fakeai.cld.add(status);
                synchronized (fakeai.rt) {
                    fakeai.rt.notify();
                }
                //if(status.getInReplyToUserId() == fakeai.tw.getId()) {
                /*for (String b64keyw : testal.keySet()) {
                    String keyw = new String(Base64.getDecoder().decode(b64keyw), "UTF-8");
                    //System.out.println(keyw);
                    if(!status.getText().startsWith("RT")) {
                        if (status.getText().contains(keyw)) {
                            //strings.addAll(testal.get(keyw));

                            List<String> ls = testal.get(b64keyw);
                            if (ls.size() > 0) {
                                try {

                                    //System.out.println(testal.get(b64keyw).toString());
                                    System.out.println("RESP ENGINE> Got:" + ls.size());

                                    String resp = new String(Base64.getDecoder().decode(ls.get(sr.nextInt(ls.size()))), "UTF-8");


                                    System.out.println("RESP ENGINE> RespSend:" + resp);
                                    fakeai.tw.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + resp).inReplyToStatusId(status.getId()));
                                } catch (TwitterException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }*/
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFollow(User source, User followedUser) {
        if (!fakeai.FORCE_FOLLOWED && !FOLLOWED) {
            try {
                FOLLOWED = true;
                fakeai.tw.createFriendship(source.getId());
                System.out.println("UserFollow: id>" + source.getId() + " screenName>" + source.getScreenName());
                StatusUpdate su = new StatusUpdate("@" + source.getScreenName() + " ちさき「よろしくね！」").inReplyToStatusId(source.getId());
                fakeai.tw.updateStatus(su);
                FOLLOWED = false;
                //fakeai.tw.sendDirectMessage(source.getId(),"ちさき「ふぉろー　ありがとう！、よろしくね！");
                //fakeai.tw.updateStatus();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (fakeai.FORCE_FOLLOWED && !FOLLOWED) {
            try {
                System.out.println("FORCED: UserFollow: id>" + followedUser.getId() + " screenName>" + followedUser.getScreenName());
                StatusUpdate su = new StatusUpdate("@" + followedUser.getScreenName() + " ちさき「よろしくね！」");
                fakeai.tw.updateStatus(su);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fakeai.FORCE_FOLLOWED = false;
            FOLLOWED = false;

        }
    }
}
