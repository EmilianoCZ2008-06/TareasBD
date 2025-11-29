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
        launch(args);
	}

    @Override
    public void start(Stage stage) throws Exception {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(TareasControlador.class);
        stage.setTitle("Tareas");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> {
            //cancela el evento de cierre por defecto
            event.consume();
            //muestra nuestro metodo mostrarConfirmacionDeCierre
            mostrarConfirmacionDeCierre(stage);
        });
        stage.show();
    }

    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        applicationContext = SpringApplication.run(TareasApplication.class, args);
    }

    private void mostrarConfirmacionDeCierre(Stage stage) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar cierre");
        a.setHeaderText("Cerrar la aplicacion?");
        a.setContentText("Elige tu respuesta:");
        Optional<ButtonType> resp = a.showAndWait();
        //Condicional que responde a si el usuario presiona "ok", entonces cerrara la ventana
        if (resp.isPresent() && resp.get()==ButtonType.OK) {
            stage.close();
        }
    }
}
