module Tareas {
    requires jakarta.persistence;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires net.rgielen.fxweaver.core;
    requires spring.jdbc;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires org.hibernate.orm.core;
    requires spring.data.jpa;

    opens com.example.Tareas to javafx.fxml, spring.core, spring.beans, spring.context, org.hibernate.orm.core;
    exports com.example.Tareas;
}