package com.amboiko.services;

import com.amboiko.model.Pilot;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadPilotService extends Service<List<Pilot>> {
    private final PilotManager pilotManager;
    private int carId;

    public LoadPilotService(PilotManager pilotManager) {
        this.pilotManager = pilotManager;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    @Override
    protected Task<List<Pilot>> createTask() {
        return new Task<>() {
            @Override
            protected List<Pilot> call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return pilotManager.getPilots(carId);
            }
        };
    }
}