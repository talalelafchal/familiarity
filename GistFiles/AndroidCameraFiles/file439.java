package com.projet.consulting.lttd.m3appli;

import java.util.Date;

/**
 * Created by APACE on 04/07/2014.
 */
public class Article {
    int id;
    String name;
    double price;
    String user;
    Date create;
    Date update;

    public Article() {
    }

    public Article(int id, String name, double price, String user, Date create, Date update) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.user = user;
        this.create = create;
        this.update = update;
    }
    public Article( String name, double price, String user, Date create, Date update) {
        this.name = name;
        this.price = price;
        this.user = user;
        this.create = create;
        this.update = update;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }
}
