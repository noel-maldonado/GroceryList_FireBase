package model;

import android.graphics.Bitmap;

public class Product extends Store {

    private int itemID;
    private String itemName;
    private String itemCode;
    private Bitmap picture;
    private int quantity;
    private double itemPrice;

    public Product(int itemID, String itemName, String itemCode, Bitmap picture, int quantity, double itemPrice, String storeName) {
        super(storeName);
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.picture = picture;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }


    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
