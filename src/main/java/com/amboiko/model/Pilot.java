package com.amboiko.model;

public class Pilot {
    private String name;
    private int awards;

    public Pilot() {
    }

    public Pilot(String name, int numberOfAwards) {
        this.name = name;
        this.awards = numberOfAwards;
    }

    public String getName() {
        return name;
    }

    public int getAwards() {
        return awards;
    }

    @Override
    public String toString() {
        return "Pilot{" + "name='" + name + '\'' +
                ", awards=" + awards +
                '}';
    }
}
