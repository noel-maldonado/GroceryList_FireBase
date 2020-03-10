package model;


public class Product {

    private String itemId;
    private String itemName;
    private String productImageUrl;
    private int quantity;
    private double itemPrice;
    private String productStore;


    public Product() {
        //by default quantity is 0
        this.quantity = 0;
    }



    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemID) {
        this.itemId = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
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

    public String getProductStore() {
        return productStore;
    }

    public void setProductStore(String productStore) {
        this.productStore = productStore;
    }
}
