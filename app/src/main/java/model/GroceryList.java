package model;

import com.google.firebase.Timestamp;

public class GroceryList {
    //Image placed on List
    private String imageUrl;
    //Title of List
    private String listTitle;
    //id of List Item
    private String glistId;
    //Time Stamp
    private Timestamp timeAdded;

    private String userId;
    private String username;


    //need default constructor in order to store information in FireStore
    public GroceryList(){
    }


    public GroceryList(String title) {
        listTitle = title;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setImageUrl(String mImageResource) {
        this.imageUrl = mImageResource;
    }


    public void setListTitle(String mText1) {
        this.listTitle = mText1;
    }

    public String getGlistId() {
        return glistId;
    }

    public void setGlistId(String glistID) {
        this.glistId = glistID;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
