package com.CSCE4901.Mint.Search;

public class SearchItem {
    String category;
    String date;
    String description;
    String flag;
    String title;
    String id;

    public String getCategory() {
        return category;
    }
    public String getDate() {
        return date;
    }
    public String getDescription(){
        return description;
    }
    public String getFlag(){
        return flag;
    }
    public String getTitle(){
        return title;
    }
    public  String getId() {return id;}

    public SearchItem(String category, String date, String description, String flag, String title, String ID) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.flag = flag;
        this.title = title;
        this.id=ID;
    }
}
