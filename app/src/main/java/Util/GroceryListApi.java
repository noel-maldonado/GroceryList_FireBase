package Util;

import android.app.Application;

public class GroceryListApi extends Application {

    private String username;
    private String userId;
    private static GroceryListApi instance;

    public static GroceryListApi getInstance() {
        if (instance == null) {
            instance = new GroceryListApi();
        }
        return instance;
    }

    public  GroceryListApi(){}

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
