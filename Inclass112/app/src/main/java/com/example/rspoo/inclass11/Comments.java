package com.example.rspoo.inclass11;

import java.util.Date;

/**
 * Created by rspoo on 11/14/2016.
 */

public class Comments {
    public Comments(){}

    public Comments(String comment, Date date) {
        this.comment = comment;
        this.date = date;
    }

    String comment;
    Date date;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
