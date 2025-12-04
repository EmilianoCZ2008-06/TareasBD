package com.example.Tareas;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@SpringBootApplication(scanBasePackages = "com.example")
public class TareasApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args); //inicia JavaFX
    }

    @Override
    public void start(Stage stage) throws Exception {

        //FxWeaver conecta JavaFX con Spring y carga la vista principal
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(TareasControlador.class);

        stage.setTitle("Tareas");
        stage.setScene(new Scene(root));

        //manejar el cierre manual de la ventana
        stage.setOnCloseRequest(event -> {
            event.consume();            //evita el cierre automático
            mostrarConfirmacionDeCierre(stage); //muestra mensaje de confirmación
        });

        stage.show(); //muestra la ventana principal
    }

    @Override
    public void init() throws Exception {
        //inicia Spring dentro de JavaFX
        String[] args = getParameters().getRaw().toArray(new String[0]);
        applicationContext = SpringApplication.run(TareasApplication.class, args);
    }

    private void mostrarConfirmacionDeCierre(Stage stage) {
        //mensaje simple para confirmar si el usuario quiere cerrar la app
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar cierre");
        a.setHeaderText("¿Cerrar la aplicación?");
        a.setContentText("Elige tu respuesta:");

        Optional<ButtonType> resp = a.showAndWait();

        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            stage.close();  //cierra la app
        }
    }
}
