package com.sauno.androiddeveloper.chewstudiodemo.model;

public class Dish {
    private int id;
    private String description;
    private String category;

    public Dish() {

    }

    public Dish(int id, String description, String category) {
        this.id = id;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
