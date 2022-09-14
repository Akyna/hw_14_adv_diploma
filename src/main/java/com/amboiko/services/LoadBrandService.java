package com.amboiko.services;

import com.amboiko.model.Brand;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadBrandService extends Service<List<Brand>> {
    private final CarManager carManager;

    public LoadBrandService(CarManager carManager) {
        this.carManager = carManager;
    }

    @Override
    protected Task<List<Brand>> createTask() {
        return new Task<>() {
            @Override
            protected List<Brand> call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return carManager.getCarBrands();
            }
        };
    }
}