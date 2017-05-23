	package net.chromaryu.fakeai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.config.Config;
import net.chromaryu.fakeai.mysql.mySqlHandler;
import net.chromaryu.fakeai.mysql.mySqlHandlerOld;
import net.chromaryu.fakeai.thread.MySqlSyncer;
import net.chromaryu.fakeai.thread.ifrit.*;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    static Path s1 = Paths.get("key/keyfile.key");
    public static AtomicInteger atomicInteger = new AtomicInteger();
    public static AtomicInteger respNum = new AtomicInteger();
    public static Cipher cipher;
    public static void main(String[] args) throws TwitterException, IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        om.enable(SerializationFeature.INDENT_OUTPUT);
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //HashMap<String,String> str = new HashMap<>();
        Config config = om.readValue(new File("config.json"),Config.class);
        if(config.getAes().get("key").equals("") || config.getAes().get("iv").equals("")) {
            // Key Gen. Maybe.
            try {
                SecureRandom sr = SecureRandom.getInstance("NativePRNGNonBlocking"); // Linux ONLY! look at Javadoc!
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256,sr);
                SecretKey seckey = keyGenerator.generateKey();
                config.getAes().put("key", Base64.getEncoder().encodeToString(seckey.getEncoded()));
                byte[] iv = new byte[16];
                sr.nextBytes(iv);
                config.getAes().put("iv",Base64.getEncoder().encodeToString(iv));
                cipher.init(Cipher.ENCRYPT_MODE,seckey,new IvParameterSpec(iv));
                om.writeValue(new File("config.json"),config);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                e.printStackTrace();
            }
        } else try {
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(Base64.getDecoder().decode(config.getAes().get("key")),"AES"),
                    new IvParameterSpec(Base64.getDecoder().decode(config.getAes().get("iv"))));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

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
        //es.execute(new StatusThread());
        TwitterStream ts = new TwitterStreamFactory(cf).getInstance();
        ts.addListener(new Stream());
        LocalDateTime ldt = LocalDateTime.now();
        //tw.updateStatus("ちさき「こんにちは！　きどうじこくは:"+ldt.format(DateTimeFormatter.ISO_DATE_TIME)+"だよ！」" );
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new MySqlSyncer(), 0L,300000L);
        t.scheduleAtFixedRate(new StatusThread(), 60000L, 1800000L);
        ts.user();
        //BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        //at.addListener(new Stream());

    }
}
