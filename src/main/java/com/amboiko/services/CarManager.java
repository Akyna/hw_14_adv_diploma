package com.amboiko.services;

import com.amboiko.model.Brand;
import com.amboiko.model.Car;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarManager {
    private final Connection connection;
    private static final String SELECT_CARS_BY_BRAND = "SELECT id, model, power, speed FROM car WHERE brand_id = %d;";
    private static final String SELECT_BRANDS = "SELECT id, title FROM brand";

    public CarManager(Connection connection) {
        this.connection = connection;
    }

    public List<Brand> getCarBrands() throws SQLException {
        List<Brand> result = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet;

        resultSet = statement.executeQuery(SELECT_BRANDS);

        while (resultSet.next()) {
            result.add(
                    new Brand(
                            resultSet.getInt("id"),
                            resultSet.getString("title")
                    )
            );
        }

        resultSet.close();
        statement.close();
        return result;
    }

    public List<Car> getCarByBrand(final int id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        List<Car> result = new ArrayList<>();

        resultSet = statement.executeQuery(String.format(SELECT_CARS_BY_BRAND, id));
        while (resultSet.next()) {
            result.add(
                    new Car(
                            resultSet.getInt("id"),
                            resultSet.getString("model"),
                            resultSet.getInt("power"),
                            resultSet.getInt("speed")
                    )
            );
        }

        resultSet.close();
        statement.close();
        return result;
    }
}
