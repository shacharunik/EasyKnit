package com.shacharunik.EasyKnit.models;

import java.util.List;

public class Pattern {
    private String name;
    private String materials;
    private List<String> instructions;
    private String img;
    private String difficulty;
    private String creator;

    public Pattern(String name, String materials, List<String> instructions, String img, String difficulty, String creator) {
        this.name = name;
        this.materials = materials;
        this.instructions = instructions;
        this.img = img;
        this.difficulty = difficulty;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Pattern() {

    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
