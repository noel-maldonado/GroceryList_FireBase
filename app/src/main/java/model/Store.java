package model;

public class Store {

    private String storeId;
    private String storeName;
    private String storePictureUrl;

    public Store() {}


    public Store(String storeName) {
        this.storeName = storeName;
    }


    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeID) {
        this.storeId = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public String getStorePictureUrl() {
        return storePictureUrl;
    }

    public void setStorePictureUrl(String storePictureUrl) {
        this.storePictureUrl = storePictureUrl;
    }
}



