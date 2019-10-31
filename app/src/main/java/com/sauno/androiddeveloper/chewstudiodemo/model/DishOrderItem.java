package com.sauno.androiddeveloper.chewstudiodemo.model;

import java.math.BigDecimal;

//POJO
public class DishOrderItem {
    private int idDish;
    private String dishName;
    private BigDecimal quantityDishes;
    private BigDecimal calories;
    private BigDecimal proteins;
    private BigDecimal fats;
    private BigDecimal carbs;
    private BigDecimal xe;
    private int[] compatibilityArray;
    private String category;
    private int[] ingredientIDArray;

    public DishOrderItem(int idDish, String dishName, BigDecimal quantityDishes, BigDecimal calories, BigDecimal proteins, BigDecimal fats, BigDecimal carbs, BigDecimal xe, int[] compatibilityArray, String category) {
        this.idDish = idDish;
        this.dishName = dishName;
        this.quantityDishes = quantityDishes;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.xe = xe;
        this.compatibilityArray = compatibilityArray;
        this.category = category;
    }

    public DishOrderItem(int idDish, String dishName, BigDecimal quantityDishes, BigDecimal calories, BigDecimal proteins, BigDecimal fats, BigDecimal carbs, BigDecimal xe, int[] compatibilityArray, String category, int[] ingredientIDArray) {
        this.idDish = idDish;
        this.dishName = dishName;
        this.quantityDishes = quantityDishes;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.xe = xe;
        this.compatibilityArray = compatibilityArray;
        this.category = category;
        this.ingredientIDArray = ingredientIDArray;
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

    public BigDecimal getQuantityDishes() {
        return quantityDishes;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setQuantityDishes(BigDecimal quantityDishes) {
        this.quantityDishes = quantityDishes;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getProteins() {
        return proteins;
    }

    public void setProteins(BigDecimal proteins) {
        this.proteins = proteins;
    }

    public BigDecimal getFats() {
        return fats;
    }

    public void setFats(BigDecimal fats) {
        this.fats = fats;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }

    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    public BigDecimal getXe() {
        return xe;
    }

    public void setXe(BigDecimal xe) {
        this.xe = xe;
    }

    public int[] getCompatibilityArray() {
        return compatibilityArray;
    }

    public void setCompatibilityArray(int[] compatibilityArray) {
        this.compatibilityArray = compatibilityArray;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int[] getIngredientIDArray() {
        return ingredientIDArray;
    }

    public void setIngredientIDArray(int[] ingredientIDArray) {
        this.ingredientIDArray = ingredientIDArray;
    }
}
