package com.bibupp.androidvantage;

/**
 * Created by ADMIN on 16-Jan-17.
 */
public class CountryHolder {

    private String name,id;

    public CountryHolder(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}