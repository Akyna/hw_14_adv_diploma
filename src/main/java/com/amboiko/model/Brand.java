package com.amboiko.model;

public class Brand {
    private int id;
    private String title;

    public Brand() {
    }

    public Brand(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Brand{" + "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
