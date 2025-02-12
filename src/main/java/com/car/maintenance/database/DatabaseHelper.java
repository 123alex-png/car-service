package com.car.maintenance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
public class DatabaseHelper {

    private static final String DATABASE_URL = "jdbc:sqlite:car_maintenance.db";

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}
