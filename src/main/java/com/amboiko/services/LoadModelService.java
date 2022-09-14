package com.amboiko.services;

import com.amboiko.model.Car;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadModelService extends Service<List<Car>> {
    private final CarManager carManager;
    private int brandId;

    public LoadModelService(CarManager carManager) {
        this.carManager = carManager;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    @Override
    protected Task<List<Car>> createTask() {
        return new Task<>() {
            @Override
            protected List<Car> call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return carManager.getCarByBrand(brandId);
            }
        };
    }
}