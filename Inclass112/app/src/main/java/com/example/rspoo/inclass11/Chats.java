package com.example.rspoo.inclass11;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rspoo on 11/14/2016.
 */

public class Chats {
    ArrayList<Comments> com;

    public ArrayList<Comments> getCom() {
        return com;
    }

    public void setCom(ArrayList<Comments> com) {
        this.com = com;
    }

    private  String fulname;
    private String message;
    private String comments;
    private String imageUrl;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String userID;
    private String key;
    private Date when;



    public String getFulname() {
        return fulname;
    }

    public void setFulname(String fulname) {
        this.fulname = fulname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }
}
