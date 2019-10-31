package com.sauno.androiddeveloper.chewstudiodemo.model;

//POJO
public class Dish {
    private int idDish;
    private String dishName;
    private int dishCalories;
    private int dishProteins;
    private int dishFats;
    private int dishCarbs;
    private int dishXE;
    private int compatibilityEvaluation;
    private String globalCategory;
    private int quantity;

    public Dish(int idDish, String dishName, int dishCalories, int dishProteins, int dishFats, int dishCarbs, int dishXE, int compatibilityEvaluation, String globalCategory) {
        this.idDish = idDish;
        this.dishName = dishName;
        this.dishCalories = dishCalories;
        this.dishProteins = dishProteins;
        this.dishFats = dishFats;
        this.dishCarbs = dishCarbs;
        this.dishXE = dishXE;
        this.compatibilityEvaluation = compatibilityEvaluation;
        this.globalCategory = globalCategory;
    }

    public int getIdDish() {
        return idDish;
    }

    public void setIdDish(int idDish) {
        this.idDish = idDish;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getDishCalories() {
        return dishCalories;
    }

    public void setDishCalories(int dishCalories) {
        this.dishCalories = dishCalories;
    }

    public int getDishProteins() {
        return dishProteins;
    }

    public void setDishProteins(int dishProteins) {
        this.dishProteins = dishProteins;
    }

    public int getDishFats() {
        return dishFats;
    }

    public void setDishFats(int dishFats) {
        this.dishFats = dishFats;
    }

    public int getDishCarbs() {
        return dishCarbs;
    }

    public void setDishCarbs(int dishCarbs) {
        this.dishCarbs = dishCarbs;
    }

    public int getDishXE() {
        return dishXE;
    }

    public void setDishXE(int dishXE) {
        this.dishXE = dishXE;
    }

    public int getCompatibilityEvaluation() {
        return compatibilityEvaluation;
    }

    public void setCompatibilityEvaluation(int compatibilityEvaluation) {
        this.compatibilityEvaluation = compatibilityEvaluation;
    }

    public String getGlobalCategory() {
        return globalCategory;
    }

    public void setGlobalCategory(String globalCategory) {
        this.globalCategory = globalCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
