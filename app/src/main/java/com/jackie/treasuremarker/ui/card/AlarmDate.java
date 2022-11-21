package com.jackie.treasuremarker.ui.card;

import org.jetbrains.annotations.NotNull;

public class AlarmDate {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Integer second;

    public AlarmDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public AlarmDate(String dateFormat) {
        parse(dateFormat);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    @NotNull
    @Override
    public String toString() {
        return year + "." + month + "." + day + " " + hour + ":" + minute + ":" + second;
    }

    public void parse(String tar) {
        String[] tmp1 = tar.split(" ");
        if (tmp1.length != 2) return;
        String[] tmp2 = tmp1[0].split(".");
        if (tmp2.length != 3) return;
        this.year = Integer.parseInt(tmp2[0]);
        this.month = Integer.parseInt(tmp2[1]);
        this.day = Integer.parseInt(tmp2[2]);
        String[] tmp3 = tmp1[1].split(":");
        if (tmp3.length != 3) return;
        this.hour = Integer.parseInt(tmp3[0]);
        this.minute = Integer.parseInt(tmp3[1]);
        this.second = Integer.parseInt(tmp3[2]);
    }

    public static boolean validate(String tar) {
        String[] tmp1 = tar.split(" ");
        if (tmp1.length != 2) return false;
        String[] tmp2 = tmp1[0].split(".");
        if (tmp2.length != 3) return false;
        String[] tmp3 = tmp1[1].split(":");
        if (tmp3.length != 3) return false;
        return true;
    }
}
