package model;

import android.os.SystemClock;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GroceryList {
    //Image placed on List
    private int mImageResource;
    //Title of List
    private String listTitle;
    //Date Created
    private String mText2;
    //id of List Item
    private int glistID;
    //Time Stamp
    private Timestamp timeAdded;

    private String userId;
    private String username;


    //need default constructor in order to store information in FireStore
    public GroceryList(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date created = new Date();
        mText2 = df.format(created);
    }


    public GroceryList(String title) {
        listTitle = title;
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date created = new Date();
        mText2 = df.format(created);
    }

    public GroceryList(int imageResource, String text1, String text2) {
        mImageResource = imageResource;
        listTitle = text1;
        mText2 = text2;
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date created = new Date();
        mText2 = df.format(created);
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getListTitle() {
        return listTitle;
    }

    public String getText2() {
        return mText2;
    }

    public void setImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }


    public void setListTitle(String mText1) {
        this.listTitle = mText1;
    }

    public void setText2(String mText2) {
        this.mText2 = mText2;
    }

    public int getGlistID() {
        return glistID;
    }

    public void setGlistID(int glistID) {
        this.glistID = glistID;
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
