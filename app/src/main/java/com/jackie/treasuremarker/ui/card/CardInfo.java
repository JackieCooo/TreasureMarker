package com.jackie.treasuremarker.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CardInfo {
    private String title;
    private String address;
    private CategoryType type;
    private Date date;
    private Uri picUri;
    private Boolean visited;

    public CardInfo() {
        this.visited = false;
    }

    public CardInfo(String title, String address) {
        this.title = title;
        this.address = address;
    }

    public CardInfo(String title, String address, CategoryType type) {
        this.title = title;
        this.address = address;
        this.type = type;
    }

    public CardInfo(String title, String address, CategoryType type, Date date) {
        this.title = title;
        this.address = address;
        this.type = type;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Uri getPicUri() {
        return picUri;
    }

    public void setPicUri(Uri picPath) {
        this.picUri = picPath;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    @NotNull
    @Override
    public String toString() {
        String strDate;
        if (date == null) {
            strDate = "null";
        }
        else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strDate = dateFormat.format(date);
        }

        return title + "," + address + "," + type + "," + strDate + "," + picUri + "," + visited;
    }
}
