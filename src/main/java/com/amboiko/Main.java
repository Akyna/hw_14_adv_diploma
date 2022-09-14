package com.amboiko;

import com.amboiko.common.Logger;
import com.amboiko.services.CarManager;
import com.amboiko.services.DBService;
import com.amboiko.services.PilotManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static final String DB_NAME = "hw_14_adv_diploma";
    public static final String DB_HOST = "jdbc:mysql://localhost:8889/" + DB_NAME;
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public static void main(String[] args) {
        // CREATE CONNECTION
        DBService dbService = new DBService(DB_HOST, DB_USER, DB_PASS);
        Connection connection = null;


        try {
            connection = dbService.getConnection();
        } catch (SQLException ex) {
            Logger.error(ex.getMessage());
        }

        if (connection != null) {
            CarManager carManager = new CarManager(connection);
            PilotManager pilotManager = new PilotManager(connection);
            CarApplication carApplication = new CarApplication();

            carApplication.setCarManager(carManager);
            carApplication.setPilotManager(pilotManager);
            carApplication.initEntry();
            carApplication.runApplication();
        }

    }
}