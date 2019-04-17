package com.CSCE4901.Mint.Search;

public class SearchItem {
    final String category;
    final String date;
    final String description;
    final String flag;
    public final String title;
    final String id;

    public SearchItem(String category, String date, String description, String flag, String title, String ID) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.flag = flag;
        this.title = title;
        this.id=ID;


    }

}
