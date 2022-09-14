package com.amboiko.services;

import com.amboiko.model.Pilot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PilotManager {
    private final Connection connection;
    private static final String SELECT_PILOTS = "SELECT name, awards FROM pilot WHERE id IN (SELECT pilot_id FROM car_pilot WHERE car_id = %d);";

    public PilotManager(Connection connection) {
        this.connection = connection;
    }

    public List<Pilot> getPilots(final int id) throws SQLException {
        List<Pilot> result = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet;

        resultSet = statement.executeQuery(String.format(SELECT_PILOTS, id));

        while (resultSet.next()) {
            result.add(
                    new Pilot(
                            resultSet.getString("name"),
                            resultSet.getInt("awards")
                    )
            );
        }

        resultSet.close();
        statement.close();
        return result;
    }
}
