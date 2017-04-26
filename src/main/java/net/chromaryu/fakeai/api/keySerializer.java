package net.chromaryu.fakeai.api;

/**
 * Created by midgard on 17/04/20.
 */
public class keySerializer {
    private String authKey,oneTimeKey;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getOneTimeKey() {
        return oneTimeKey;
    }

    public void setOneTimeKey(String oneTimeKey) {
        this.oneTimeKey = oneTimeKey;
    }
}
