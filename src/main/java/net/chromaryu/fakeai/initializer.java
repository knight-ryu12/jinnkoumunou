package net.chromaryu.fakeai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.chromaryu.fakeai.api.Sword;
import net.chromaryu.fakeai.api.keySerializer;
import net.chromaryu.fakeai.api.twitterSerializer;
import net.chromaryu.fakeai.thread.MySqlSyncAsTask;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static net.chromaryu.fakeai.fakeai.es;
import static net.chromaryu.fakeai.fakeai.testal;
import static net.chromaryu.fakeai.fakeai.tw;
import static spark.Spark.*;
/**
 * Created by midgard on 17/04/13.
 */
public class initializer {
    private static ObjectMapper om = new ObjectMapper();
    public static void init() {
        port(9000);
        threadPool(6, 1, 3000);

        path("/api", () -> {
            path("/key",() -> {
                post("/getauthkey",(request, response) -> {
                    //keySerializer s;
                    if(!request.headers().contains("Authorization")) {
                        halt(403,"ちさき「ちょっとだめかな。くろまに言ってみてね！」");
                    }
                    String[] args = request.headers("Authorization").split(" ");
                    if(args.length == 2) {
                        if(!args[0].equals("Bearer")) halt(406,"ちさき「Authorizationのへっだーがまちがってるよ！」\nAuthorization:Bearer <AUTHKEY>");
                    }
                    //fakeai.mysql.addOTK()
                    return request.headers("Authorization")+"\n";
                });
                get("/generateauthkey",(req,res) -> {
                    // DATA: <TWID>:<RANDOM-64Byte-MSG>
                    System.out.println(req.ip());
                    String b64 = null;
                    String[] args = null;
                    byte[] authkey = new byte[16*2];
                    if(req.ip().matches("192\\.168\\.[0-9]\\.*[0-9]+") || req.ip().matches("0:0:0:0:0:0:0:1")) {
                        // Sooo This is localhosted... maybe???
                        //return "OK IP:"+req.ip();

                        args = req.body().split(":");
                        if(args.length != 2) {
                            halt(400,"ちさき「リクエストが不正だよ！」");
                        }
                        byte[] auth = fakeai.cipher.update(Arrays.toString(args).getBytes("UTF-8"));
                        b64 = Base64.getEncoder().encodeToString(auth);
                        fakeai.mysql.addOTK(b64);


                        //byte[] otk = fakeai.mysql.addOTK(authkey);
                    }
                    return b64;
                    //return "NG IP:"+req.ip();
                });
            });

            path("/words", () -> {
                put("/add", (req, res) -> {
                    Sword s;
                    try {
                        s = om.readValue(req.body(), Sword.class);
                        String b64key = Base64.getEncoder().encodeToString(s.getKeyword().getBytes(Charset.forName("UTF-8")));
                        String b64res = Base64.getEncoder().encodeToString(s.getResponce().getBytes(Charset.forName("UTF-8")));
                        fakeai.mysql.addResponce(b64key,b64res);
                        fakeai.es.execute(new MySqlSyncAsTask(fakeai.g_sm));
                    } catch (Exception e) {
                        return "{\"status\":\"Job (Adding Word to DB) Failed\"}\n";
                    }

                    return "{\"status\":\"Job (Adding Word to DB) Done\"}\n";
                });
                get("/getAll",(request,response)->{

                        fakeai.es.execute(new MySqlSyncAsTask(fakeai.g_sm));
                        ListMultimap<String, String> test;
                    test = ArrayListMultimap.create();
                    fakeai.testal.forEach((k, v) -> {
                            try {
                                test.put(new String(Base64.getDecoder().decode(k), "UTF-8"), new String(Base64.getDecoder().decode(v), "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        });

                    return test;
                });
            });
            path("/twitter", () -> {
                get("/ratelimit",(request,response) -> tw.getRateLimitStatus().keySet());
                get("/follow",(request,response) -> {
                    System.out.println(request.queryParams());
                    if(request.queryMap().hasKey("follow")) {
                        fakeai.FORCE_FOLLOWED = true;
                        fakeai.tw.createFriendship(request.queryMap("follow").longValue());
                        return "{\"status\":\"Succeeded\"}\n";
                    }
                    return "{\"status\":\"Error!\"}\n";
                });
                post("/:name", (req, res) -> {

                    twitterSerializer ts;
                    try {

                        ts = om.readValue(req.body(), twitterSerializer.class);
                    } catch (Exception e) {
                        return "{\"error\":\"Exception.\"}";
                    }
                    switch (req.params(":name")) {
                        default:
                        case "chisaki":
                            fakeai.tw.updateStatus("ちさき「" + ts.getTweet() + "」");
                            return "OK";

                    }
                    //return "{\"error\":\"No Char was set!\"}";
                });

            });
        });
    }
}
