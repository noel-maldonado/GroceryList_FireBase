package util;

import android.app.Application;

public class GListApi extends Application {

    private String username;
    private String userId;

    private static GListApi instance;

    public GListApi() {}

    public static GListApi getInstance() {
        if(instance == null) {
            instance = new GListApi();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
