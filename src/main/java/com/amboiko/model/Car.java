package com.amboiko.model;

public class Car {
    private int id;
    private String model;
    private int power;
    private int speed;

    public Car() {
    }

    public Car(int id, String model, int power, int speed) {
        this.id = id;
        this.model = model;
        this.power = power;
        this.speed = speed;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getPower() {
        return power;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id +
                ", model='" + model + '\'' +
                ", power=" + power +
                ", speed=" + speed +
                '}';
    }
}