package com.example.Tareas;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@FxmlView("/TareasVista.fxml")

public class TareasControlador {
    @Autowired
    TareasServicio tareasServicio;
    @FXML
    public ListView<Tarea> lstVwListaDeTareas;
    @FXML
    public TextField txtFldTitulo;
    @FXML
    public TextField txtArdDescripcion;
    @FXML
    public CheckBox chkBxRealizada;
    @FXML
    public Button btnNuevaTarea;
    @FXML
    public Button btnModificar;
    @FXML
    public Button btnEliminar;
    @FXML
    public Button btnSalir;
    @FXML
    public Button btnGuardar;

    ObservableList<Tarea> observableList;

    @FXML
    public void oABtnNuevaTarea(ActionEvent actionEvent) {
        Tarea nuevaTarea = new Tarea("Nueva tarea", "Descripcion de la tarea", false);
        observableList.add(nuevaTarea);
        lstVwListaDeTareas.getSelectionModel().select(nuevaTarea);
        lstVwListaDeTareas.scrollTo(nuevaTarea);
        txtFldTitulo.setDisable(false);
        txtArdDescripcion.setDisable(false);
        chkBxRealizada.setDisable(false);
        txtFldTitulo.requestFocus();
        btnNuevaTarea.setDisable(true);
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
        btnGuardar.setDisable(false);
    }

    @FXML
    public void oABtnModificar(ActionEvent actionEvent) {
        txtFldTitulo.setDisable(false);
        txtArdDescripcion.setDisable(false);
        chkBxRealizada.setDisable(false);
        txtFldTitulo.requestFocus();
        btnNuevaTarea.setDisable(true);
        btnModificar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }

    @FXML
    public void oABtnEliminar(ActionEvent actionEvent) {
        Tarea tareaSeleccionada = lstVwListaDeTareas.getSelectionModel().getSelectedItem();
        if (confirmarAccion("Eliminar tarea" + tareaSeleccionada.getTitulo()))
            observableList.remove(tareaSeleccionada);
    }

    @FXML
    public void oABtnSalir(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
        stage.fireEvent(closeEvent);
    }

    @FXML
    public void oABtnRealizada(ActionEvent actionEvent) {
    }

    @FXML
    public void oABtnGuardar(ActionEvent actionEvent) {
        Tarea tareaSeleccionada = lstVwListaDeTareas.getSelectionModel().getSelectedItem();
        tareaSeleccionada.setTitulo(txtFldTitulo.getText());
        tareaSeleccionada.setDescripcion(txtArdDescripcion.getText());
        tareaSeleccionada.setRealizada(chkBxRealizada.isSelected());
        //1. encontrar el indice del elemento modificado
        int index = observableList.indexOf(tareaSeleccionada);
        //2. notificar a la lista que el elemento fue actualizado
        //forzar la notificacion que dispara change.wasUpdated()
        if (index != -1)
            observableList.set(index, tareaSeleccionada);
        //nota: "set" con el mismo onjeto en el mismo indice dispara el evento de actualizacion
        btnGuardar.setDisable(true); //deshabilita el boton guardar
        reseleccionar(); // fuerza la actualizacion del a seleccion actual
    }

    public void initialize() {

        // 1. Inicializar la ObservableList
        observableList = FXCollections.observableArrayList();
        // Vincular la observableList con el ListView
        lstVwListaDeTareas.setItems(observableList);

        // 2. Cargar los datos iniciales (opcional, pero recomendado)
        List<Tarea> tareasIniciales = tareasServicio.findAll(); // Cargo todas las tareas al observableList
        observableList.addAll(tareasIniciales);

        // 3. Añadir un escuchador ListChangeListener al observableList para detectar cambios en la lista
        observableList.addListener((ListChangeListener<Tarea>)change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    // Iterar sobre todos los elementos agregados en este cambio
                    for (Tarea tareaAgregada : change.getAddedSubList()) {
                        // Llamar al servicio para guardar en la BD
                        tareasServicio.save(tareaAgregada);
                        System.out.println("Tarea guardada automaticamente en la BD: " + tareaAgregada.getTitulo());
                    }

                } else if (change.wasRemoved()) {
                    // Logica de eliminacion. *No es necesario (ej. cuando el usuario presiona "Eliminar")
                    for (Tarea tareaRemovida : change.getRemoved()) {
                        tareasServicio.delete(tareaRemovida.getId());
                    }
                    lstVwListaDeTareas.getSelectionModel().clearSelection();

                } else if (change.wasUpdated()) {
                    System.out.println(" --- Detección de Actualización de Tarea --- ");
                    // change.getFrom() y change.getTo() definen el rango de índices actualizados
                    // Iterar sobre el rango actualizado (generalmente será un solo índice)
                    for (int i = change.getFrom(); i < change.getTo(); i++) {

                        // Obtener el elemento que fue modificado
                        Tarea tareaModificada = observableList.get(i);

                        // Llamada de Persistencia
                        // Se guarda el cambio en la base de datos
                        tareasServicio.update(tareaModificada.getId(), tareaModificada);
                        System.out.println("Tarea con índice " + i +
                                " actualizando en la BD: " + tareaModificada.getTitulo());
                    }

                    // (Opcional) Forzar al refresco de la celda específica en el ListView
                    // Esto es redundante si tu ListView usa ListView.refresh() en el controlador
                    // pero como engagea la actualización se hace en el listener.
                    lstVwListaDeTareas.refresh();
                }
            }
        });
        lstVwListaDeTareas.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Tarea> observable, Tarea oldValue, Tarea newValue) -> {

                    if (newValue != null) {
                        txtFldTitulo.setText(newValue.getTitulo()); // Mostrar el título de la tarea seleccionada en el TextField
                        txtArdDescripcion.setText(newValue.getDescripcion()); // Mostrar el título de la tarea seleccionada en el TextField
                        chkBxRealizada.setAllowIndeterminate(false);
                        chkBxRealizada.setIndeterminate(false);
                        chkBxRealizada.setSelected(newValue.isRealizada()); // Mostrar el estado de realizado de la tarea
                        txtFldTitulo.setDisable(true); // Deshabilitar el control del título
                        txtArdDescripcion.setDisable(true); // Deshabilitar el control de la descripción
                        chkBxRealizada.setDisable(true); // Deshabilitar el control de realizado
                        btnNuevaTarea.setDisable(false); // Habilitar el boton de nueva tarea
                        btnModificar.setDisable(false); // Habilitar el boton de modificar
                        btnEliminar.setDisable(false); // Habilitar el boton de eliminar
                        btnGuardar.setDisable(true); // Deshabilitar el boton de guardar

                    } else {
                        // Si no hay selección
                        txtFldTitulo.setText("");
                        txtArdDescripcion.setText("");
                        chkBxRealizada.setAllowIndeterminate(true);
                        chkBxRealizada.setIndeterminate(true);
                        btnNuevaTarea.setDisable(false); // Habilitar el boton de nueva tarea
                        btnModificar.setDisable(true); // Deshabilitar el boton de modificar
                        btnEliminar.setDisable(true); // Deshabilitar el boton de eliminar
                        btnGuardar.setDisable(true); // Deshabilitar el boton de guardar
                    }
                }
        );
    }

    private boolean confirmarAccion(String pregunta) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("por favor confirme la accion");
        a.setHeaderText("¿" + pregunta + "?");
        a.setContentText("elige tu respuesta:");
        Optional<ButtonType> resp = a.showAndWait();
        if (resp.isPresent() && resp.get()==ButtonType.OK)
            return true;
        return false;
    }

    public void reseleccionar() {
        int selectedIndex = lstVwListaDeTareas.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //1. forzar la deseleccion
            lstVwListaDeTareas.getSelectionModel().clearSelection();
            //2. forzar la reseleccion
            lstVwListaDeTareas.getSelectionModel().select(selectedIndex);
            //3. redibujar el listview
            lstVwListaDeTareas.refresh();
        } else {
            lstVwListaDeTareas.getSelectionModel().clearSelection();
        }
    }

}
