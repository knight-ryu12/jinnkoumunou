package net.chromaryu.fakeai.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.chromaryu.fakeai.fakeai;

/**
 * Created by reimu on 17/06/02.
 */
public class apiAiHandler {
    public static String submit(String q,String sid) throws UnirestException {
        HttpResponse<String> jr = Unirest.get("https://api.api.ai/v1/query")
                .header("Authorization", "Bearer "+fakeai.config.getApiai().get("token"))
                .queryString("query",q)
                .queryString("v",20170531)
                .queryString("sessionId",sid)
                .queryString("lang","ja")
                .asString();
        return jr.getBody();
    }
}
