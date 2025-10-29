package com.tuconnect.dorm_connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-connection")
    public String testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return "✅ Database connected successfully! \n" +
                    "Database: " + connection.getMetaData().getDatabaseProductName() + "\n" +
                    "URL: " + connection.getMetaData().getURL();
        } catch (Exception e) {
            return "❌ Database connection failed: " + e.getMessage();
        }
    }
}
