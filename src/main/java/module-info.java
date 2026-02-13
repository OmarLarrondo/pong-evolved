module pong.evolved {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires io.vavr;
    requires com.zaxxer.hikari;
    requires org.slf4j;

    exports app;
    exports mvc.modelo;
    exports mvc.modelo.entidades;
    exports mvc.modelo.items;
    exports mvc.modelo.enums;
    exports mvc.vista;
    exports mvc.controlador;
    exports patrones.builder;
    exports patrones.factory.ia;
    exports patrones.strategy.movimiento;
    exports patrones.strategy.colision;
    exports patrones.observer;
    exports patrones.singleton;
    exports patrones.adapter;
    exports persistencia;
    exports persistencia.conexion;
    exports persistencia.dto;
    exports persistencia.repositorio;
    exports util;

    opens mvc.controlador to javafx.fxml;
    opens mvc.vista to javafx.fxml;
}
