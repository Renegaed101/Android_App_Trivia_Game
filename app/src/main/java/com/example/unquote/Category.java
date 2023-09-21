package com.example.unquote;

public class Category {
    public String categoryName;
    public int questionsResourceId;
    public boolean included;
    public int imageResourceId;
    public int cardColourId;
    public int textColourId;

    public Category (String categoryName, int questionsResourceId, int imageResourceId,int cardColourId, int textColourId) {
        this.categoryName = categoryName;
        this.questionsResourceId = questionsResourceId;
        this.included = false;
        this.imageResourceId = imageResourceId;
        this.cardColourId = cardColourId;
        this.textColourId = textColourId;
    }

}
