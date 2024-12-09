module com.example.institute1 {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.jfoenix;
    requires static lombok;
    requires java.sql;
    requires java.desktop;
    requires net.sf.jasperreports.core;
    requires java.mail;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;

    // Exports for external use
    exports com.example.institute1.controller;
    exports com.example.institute1.model;
    exports com.example.institute1.Main;

    // Open only when reflection is necessary
    opens com.example.institute1.controller to javafx.fxml;
    opens com.example.institute1.dto to javafx.base;
}
