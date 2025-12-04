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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component //indica que esta clase es administrada por Spring
@FxmlView("/TareasVista.fxml") //conecta esta clase con el archivo FXML de la UI

public class TareasControlador {

    @Autowired
    TareasServicio tareasServicio; //servicio que maneja la logica y conexion con la base de datos
    @FXML
    public ListView<Tarea> lstVwListaDeTareas;   //lista donde se muestran todas las tareas
    @FXML
    public TextField txtFldTitulo;               //campo para mostrar/escribir el titulo
    @FXML
    public TextField txtArdDescripcion;          //campo para mostrar/escribir la descripcion
    @FXML
    public CheckBox chkBxRealizada;              //marca si la tarea está completada
    @FXML
    public Button btnNuevaTarea;                 //boton para crear una nueva tarea
    @FXML
    public Button btnModificar;                  //boton para modificar la tarea seleccionada
    @FXML
    public Button btnEliminar;                   //boton para borrar la tarea seleccionada
    @FXML
    public Button btnSalir;                      //boton para salir de la aplicacion
    @FXML
    public Button btnGuardar;                    //boton para guardar cambios en una tarea
    @FXML
    public CheckBox chkFechaAuto;                //si está activo: usa fecha automatica
    @FXML
    public DatePicker datePickerFecha;           //selector de fecha cuando es manual

    ObservableList<Tarea> observableList;        //lista observable que mantiene sincronizada la UI

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

        //PROYECTO:

        //deshabilitar edición de campos
        txtFldTitulo.setDisable(true);
        txtArdDescripcion.setDisable(true);
        chkBxRealizada.setDisable(true);
        chkFechaAuto.setDisable(true);
        datePickerFecha.setDisable(true);

        //botones
        btnNuevaTarea.setDisable(false);   // SOLO este habilitado
        btnSalir.setDisable(false);
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
        btnGuardar.setDisable(true);

        //estado neutro del checkbox realizado
        chkBxRealizada.setAllowIndeterminate(true);
        chkBxRealizada.setIndeterminate(true);

        //limpieza visual
        txtFldTitulo.clear();
        txtArdDescripcion.clear();
        datePickerFecha.setValue(null);

        //vaciar selección del ListView (muy importante)
        lstVwListaDeTareas.getSelectionModel().clearSelection();

        //PROYECTO: cuando el usuario marque o desmarque el CheckBox:
        chkFechaAuto.selectedProperty().addListener((obs, oldVal, newVal) -> {
            datePickerFecha.setDisable(newVal);
            if (newVal) datePickerFecha.setValue(null);
        });

        lstVwListaDeTareas.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Tarea> observable, Tarea oldValue, Tarea newValue) -> {
                    if (newValue != null) {
                        txtFldTitulo.setText(newValue.getTitulo()); // Mostrar el título de la tarea seleccionada en el TextField
                        txtArdDescripcion.setText(newValue.getDescripcion()); // Mostrar el título de la tarea seleccionada en el TextField
                        chkBxRealizada.setAllowIndeterminate(false);
                        chkBxRealizada.setIndeterminate(false);
                        chkBxRealizada.setSelected(newValue.isRealizada()); // Mostrar el estado de realizado de la tarea

                        //PROYECTO: manejo de fecha
                        if (newValue.isFechaChk()) {
                            //fecha actual
                            chkFechaAuto.setSelected(true);
                            chkFechaAuto.setDisable(true);
                            datePickerFecha.setDisable(true);
                            datePickerFecha.setValue(null);
                        } else {
                            //fecha manual
                            chkFechaAuto.setSelected(false);
                            chkFechaAuto.setDisable(true);
                            datePickerFecha.setDisable(true);
                            datePickerFecha.setValue(newValue.getFecha());
                        }

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

                        //PROYECTO:
                        chkFechaAuto.setDisable(true);
                        chkFechaAuto.setSelected(true);
                        datePickerFecha.setDisable(true);
                        datePickerFecha.setValue(null);
                    }


                }
        );
    }

    @FXML
    public void oABtnNuevaTarea(ActionEvent actionEvent) {
        Tarea nuevaTarea = new Tarea("Nueva tarea", "Descripcion de la tarea", false, true);
        //PROYECTO
        nuevaTarea.setFecha(LocalDate.now());
        observableList.add(nuevaTarea);
        lstVwListaDeTareas.getSelectionModel().select(nuevaTarea);
        lstVwListaDeTareas.scrollTo(nuevaTarea);
        txtFldTitulo.setDisable(false);
        txtArdDescripcion.setDisable(false);
        chkBxRealizada.setDisable(false);

        //PROYECTO
        chkFechaAuto.setDisable(false);
        chkFechaAuto.setSelected(true);
        datePickerFecha.setDisable(true);
        datePickerFecha.setValue(null);

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

        //PROYECTO
        Tarea t = lstVwListaDeTareas.getSelectionModel().getSelectedItem();

        // Manejo de fecha al editar
        chkFechaAuto.setDisable(false);
        chkFechaAuto.setSelected(t.isFechaChk());
        datePickerFecha.setDisable(t.isFechaChk());

        if (!t.isFechaChk()) {
            datePickerFecha.setValue(t.getFecha());
        }

        txtFldTitulo.requestFocus();
        btnNuevaTarea.setDisable(true);
        btnModificar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }

    @FXML
    public void oABtnEliminar(ActionEvent actionEvent) {
        //obtiene la tarea seleccionada actualmente en la lista
        Tarea tareaSeleccionada = lstVwListaDeTareas.getSelectionModel().getSelectedItem();
        //si el usuario confirma la eliminacion, se borra de la lista
        if (confirmarAccion("Eliminar tarea" + tareaSeleccionada.getTitulo()))
            observableList.remove(tareaSeleccionada); //quita la tarea y actualiza la UI
    }

    @FXML
    public void oABtnSalir(ActionEvent actionEvent) {
        //obtiene la ventana actual donde está corriendo la app
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //crea un evento de cierre
        WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
        //ejecuta el evento, mostrando el metodo mostrarConfirmacionDeCierre()
        stage.fireEvent(closeEvent);
    }

    @FXML
    public void oABtnRealizada(ActionEvent actionEvent) {
    }

    @FXML
    public void oABtnGuardar(ActionEvent actionEvent) {

        Tarea tareaSeleccionada = lstVwListaDeTareas.getSelectionModel().getSelectedItem();

        if (tareaSeleccionada == null) return;

        tareaSeleccionada.setTitulo(txtFldTitulo.getText());
        tareaSeleccionada.setDescripcion(txtArdDescripcion.getText());
        tareaSeleccionada.setRealizada(chkBxRealizada.isSelected());

        //PROYECTO
        if (chkFechaAuto.isSelected()) {
            tareaSeleccionada.setFechaChk(true);
            tareaSeleccionada.setFecha(LocalDate.now());
        } else {
            if (datePickerFecha.getValue() == null) {
                mostrarError();
                return;
            }
            tareaSeleccionada.setFechaChk(false);
            tareaSeleccionada.setFecha(datePickerFecha.getValue());
        }

        //1. encontrar el indice del elemento modificado
        int index = observableList.indexOf(tareaSeleccionada);
        //2. notificar a la lista que el elemento fue actualizado
        //forzar la notificacion que dispara change.wasUpdated()
        if (index != -1) {
            observableList.set(index, tareaSeleccionada);
            //nota: "set" con el mismo objeto en el mismo indice dispara el evento de actualizacion
            btnGuardar.setDisable(true); //deshabilita el boton guardar
            reseleccionar(); // fuerza la actualizacion de la seleccion actual
        }
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

    private void mostrarError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText("Debes seleccionar una fecha valida");
        a.show();
    }

}
