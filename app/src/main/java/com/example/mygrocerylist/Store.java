package com.example.mygrocerylist;

import android.graphics.Bitmap;

public class Store {

    private int storeID;
    private String storeName;
    private double totalPrice;
    private int numberOfProducts;
    private Bitmap storePicture;


    public Store(String storeName) {
        this.storeName = storeName;
        storeID = -1;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public Bitmap getStorePicture() {
        return storePicture;
    }

    public void setStorePicture(Bitmap storepicture) {
        this.storePicture = storepicture;
    }
}



