package com.amboiko.model;

import com.amboiko.common.Logger;
import javafx.animation.PathTransition;

import java.util.concurrent.CountDownLatch;

public class RacingCar implements Runnable {
    private Car car;
    private Pilot pilot;
    private CountDownLatch latch;
    private PathTransition pathTransition;

    public RacingCar() {
    }

    public RacingCar(Car car, Pilot pilot, CountDownLatch latch) {
        this.car = car;
        this.pilot = pilot;
        this.latch = latch;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Logger.info(String.format("The %s car went to the start. Pilot: %s, awards: %d",
                    car.getModel(), pilot.getName(), pilot.getAwards()
            ));
            latch.countDown();

            latch.await();
            pathTransition.play();
            Logger.info(String.format("%s finished the race.", car.getModel()));
        } catch (InterruptedException ignored) {
        }
    }

    public void setPathTransition(PathTransition pathTransition) {
        this.pathTransition = pathTransition;
    }

    @Override
    public String toString() {
        return "RacingCar{" + "car=" + car +
                ", pilot=" + pilot +
                ", latch=" + latch +
                ", pathTransition=" + pathTransition +
                '}';
    }
}
