package com.jackie.treasuremarker.ui.card;

import org.jetbrains.annotations.NotNull;

public class CardInfo {
    private String title;
    private String address;
    private CategoryType type;
    private AlarmDate date;

    public CardInfo() {}

    public CardInfo(String title, String address) {
        this.title = title;
        this.address = address;
    }

    public CardInfo(String title, String address, CategoryType type) {
        this.title = title;
        this.address = address;
        this.type = type;
    }

    public CardInfo(String title, String address, CategoryType type, AlarmDate date) {
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

    public AlarmDate getDate() {
        return date;
    }

    public void setDate(AlarmDate date) {
        this.date = date;
    }

    @NotNull
    @Override
    public String toString() {
        return title + "," + address + "," + type + "," + date.toString();
    }
}
