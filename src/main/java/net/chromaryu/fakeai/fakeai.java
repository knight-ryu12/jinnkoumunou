	package net.chromaryu.fakeai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.api.Sword;
import net.chromaryu.fakeai.api.twitterSerializer;
import net.chromaryu.fakeai.config.Config;
import net.chromaryu.fakeai.mysql.mySqlHandler;
import net.chromaryu.fakeai.thread.MySqlSyncer;
import net.chromaryu.fakeai.thread.ifrit.*;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import static spark.Spark.*;
import static net.chromaryu.fakeai.initializer.init;
/**
 * Created by midgard on 17/04/08.
 */
public class fakeai {
    public static Twitter tw;
    public static boolean FORCE_FOLLOWED = false,FOLLOWED = false;
    public static mySqlHandler mysql;
    public static ListMultimap<String,String> testal = ArrayListMultimap.create();
    public static ConcurrentLinkedQueue<Status> cld = new ConcurrentLinkedQueue<>();
    static ObjectMapper om = new ObjectMapper();
    final static ResponceThread rt = new ResponceThread();
    public static Semaphore g_sm = new Semaphore(5);
    public static ExecutorService es = Executors.newCachedThreadPool();
    public static void main(String[] args) throws TwitterException,IOException {
        //HashMap<String,String> str = new HashMap<>();
        Config config = om.readValue(new File("config.json"),Config.class);

        mysql = new mySqlHandler(config);
        mysql.makeTable();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setAsyncNumThreads(2).setOAuthAccessToken(config.getTwitter().get("accessToken"))
                .setDebugEnabled(true)
                .setOAuthConsumerKey(config.getTwitter().get("consumerKey"))
                .setOAuthAccessTokenSecret(config.getTwitter().get("accessSecret"))
                .setOAuthConsumerSecret(config.getTwitter().get("consumerSecret")).setGZIPEnabled(true);
        Configuration cf = cb.build();
        TwitterFactory tf = new TwitterFactory(cf);
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","DEBUG");
        tw = tf.getInstance();
        //AsyncTwitterFactory atf = new AsyncTwitterFactory(cb.build());
        //AsyncTwitter at = atf.getInstance();
        init();
        awaitInitialization();
        es.execute(rt);
        TwitterStream ts = new TwitterStreamFactory(cf).getInstance();
        ts.addListener(new Stream());
        LocalDateTime ldt = LocalDateTime.now();
        //tw.updateStatus("Bot Start! t:"+ldt.format(DateTimeFormatter.ISO_DATE_TIME) );
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new MySqlSyncer(), 0,300000);
        ts.user();
        //BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        //at.addListener(new Stream());

    }
}
