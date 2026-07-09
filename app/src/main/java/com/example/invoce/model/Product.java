package com.example.invoce.model;

public class Product {

    private final String name;
    private final String type;
    private final String thickness;
    private final String grade;
    private final String diameter;
    private final String casing;
    private final String chimneyType;
    private final double price;


    public Product(
            String name,
            String type,
            String thickness,
            String grade,
            String diameter,
            String casing,
            String chimneyType,
            double price
    ) {
        this.name = name;
        this.type = type;
        this.thickness = thickness;
        this.grade = grade;
        this.diameter = diameter;
        this.casing = casing;
        this.chimneyType = chimneyType;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getThickness() {
        return thickness;
    }

    public String getGrade() {
        return grade;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getCasing() {
        return casing;
    }

    public String getChimneyType() {
        return chimneyType;
    }

    public double getPrice() {
        return price;
    }


    public String displayNameLength() {
        return name;
    }


    public String displayPrice() {
        return ((int) price) + " грн.";
    }
}