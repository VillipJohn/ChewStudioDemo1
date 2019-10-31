package com.sauno.androiddeveloper.chewstudiodemo.model;

import java.io.Serializable;

//POJO
public class Ingredient implements Serializable {
    private int id;
    private String name;
    private int calories;
    private int proteins;
    private int fats;
    private int carbs;
    private int xe;
    private int grams;
    private int compatibilityEvaluation;
    private int[] compatibilityArray;
    private int vegetarian;
    private int lenten;
    private String diabetes;



   /* public Ingredient(int id, String name, int calories, int proteins, int fats, int carbs, int xe, int grams, int compatibilityEvaluation, int[] compatibilityArray) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.xe = xe;
        this.grams = grams;
        this.compatibilityEvaluation = compatibilityEvaluation;
        this.compatibilityArray = compatibilityArray;
    }*/

/*    public Ingredient(int id, String name, int calories, int proteins, int fats, int carbs, int xe, int grams, int compatibilityEvaluation, int[] compatibilityArray, int lenten, String diabetes) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.xe = xe;
        this.grams = grams;
        this.compatibilityEvaluation = compatibilityEvaluation;
        this.compatibilityArray = compatibilityArray;
        this.lenten = lenten;
        this.diabetes = diabetes;
    }*/

    public Ingredient(int id, String name, int calories, int proteins, int fats, int carbs, int xe, int grams, int compatibilityEvaluation, int[] compatibilityArray, int vegetarian, int lenten, String diabetes) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.xe = xe;
        this.grams = grams;
        this.compatibilityEvaluation = compatibilityEvaluation;
        this.compatibilityArray = compatibilityArray;
        this.vegetarian = vegetarian;
        this.lenten = lenten;
        this.diabetes = diabetes;
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProteins() {
        return proteins;
    }

    public void setProteins(int proteins) {
        this.proteins = proteins;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getXe() {
        return xe;
    }

    public void setXe(int xe) {
        this.xe = xe;
    }

    public int getGrams() {
        return grams;
    }

    public void setGrams(int grams) {
        this.grams = grams;
    }

    public int getCompatibilityEvaluation() {
        return compatibilityEvaluation;
    }

    public void setCompatibilityEvaluation(int compatibilityEvaluation) {
        this.compatibilityEvaluation = compatibilityEvaluation;
    }

    public int[] getCompatibilityArray() {
        return compatibilityArray;
    }

    public void setCompatibilityArray(int[] compatibilityArray) {
        this.compatibilityArray = compatibilityArray;
    }

    public int getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(int vegetarian) {
        this.vegetarian = vegetarian;
    }

    public int getLenten() {
        return lenten;
    }

    public void setLenten(int lenten) {
        this.lenten = lenten;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(String diabetes) {
        this.diabetes = diabetes;
    }
}

