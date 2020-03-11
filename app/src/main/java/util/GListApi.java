package util;

import android.app.Application;

public class GListApi extends Application {

    private String username;
    private String userId;
    private String glistId;
    private String listTitle;
    private String storeId;
    private String storeName;

    private static GListApi instance;

    public GListApi() {}

    public static GListApi getInstance() {
        if(instance == null) {
            instance = new GListApi();
        }
        return instance;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public String getGlistId() {
        return glistId;
    }

    public void setGlistId(String glistId) {
        this.glistId = glistId;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }
}
