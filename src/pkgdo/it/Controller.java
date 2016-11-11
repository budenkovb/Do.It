package pkgdo.it;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final Task currentTask = new Task();

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final HashMap<Integer, Task> mapTasks = new HashMap<>();

    @FXML
    private TableView<Task> tasksTable;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, String> descriptionColumn;

    @FXML
    private TableColumn<Task, String> progressColumn;


    @FXML
    private Button addButton;

    @FXML
    private TextField taskDescription;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ComboBox<String> prioriteis;

    @FXML
    private Spinner<Integer> progressSpiner;

    @FXML
    private CheckBox completedCheckBox;

    @FXML
    private Button cancelButton;

    int lastId = 0;
    @FXML
    void addButtonClicked(ActionEvent event) {

        if (currentTask.getId() == null) {
            Task t = new Task( ++lastId, currentTask.getPriority(), currentTask.getDescription(), currentTask.getProgress());
            tasks.add(t);
            mapTasks.put(lastId, t);
        } else {
            Task t = mapTasks.get(currentTask.getId());
            t.setProgress(currentTask.getProgress());
            t.setDescription(currentTask.getDescription());
            t.setPriority(currentTask.getPriority());
        }
        setCurrentTask(null);
    }

    @FXML
    void cancelButtonClicked(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm cancel");
        alert.setHeaderText("Are you sure?");
        alert.getButtonTypes().remove(0, 2);
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.YES) {
        setCurrentTask(null);
        tasksTable.getSelectionModel().clearSelection();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        prioriteis.getItems().addAll("High", "Medium", "Low");
        progressSpiner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        taskDescription.setText("Alloha");
        progressSpiner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue.intValue() == 100) completedCheckBox.setSelected(true);
                else completedCheckBox.setSelected(false);
                //tasks.add(new Task(25+newValue,"Medium", "Fix bug 16516"+newValue, newValue));

            }
        });

        ReadOnlyIntegerProperty intProgress = ReadOnlyIntegerProperty.readOnlyIntegerProperty(progressSpiner.valueProperty());
        progressBar.progressProperty().bind(intProgress.divide(100.0));

        prioriteis.valueProperty().bindBidirectional(currentTask.priorityProperty());
        taskDescription.textProperty().bindBidirectional(currentTask.descriptionProperty());
        progressSpiner.getValueFactory().valueProperty().bindBidirectional(currentTask.progressProperty());

        tasksTable.setItems(tasks);
        priorityColumn.setCellValueFactory(rowData->rowData.getValue().priorityProperty());
        descriptionColumn.setCellValueFactory(rowData->rowData.getValue().descriptionProperty());
        progressColumn.setCellValueFactory(rowData-> Bindings.concat( rowData.getValue().progressProperty(), " %"));

        StringBinding addButtonText = new StringBinding() {
            { super.bind(currentTask.idProperty());}
            @Override
            protected String computeValue() {
                if (currentTask.getId() == null) return "Add";
                else return "Update";
            }
        };
        addButton.textProperty().bind(addButtonText);
        addButton.disableProperty().bind(Bindings.greaterThan(3, currentTask.descriptionProperty().length()));
        tasksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setCurrentTask(newValue));

    }

    private void setCurrentTask(Task selectedTask) {
        if (selectedTask != null) {
            currentTask.setId(selectedTask.getId());
            currentTask.setPriority(selectedTask.getPriority());
            currentTask.setDescription(selectedTask.getDescription());
            currentTask.setProgress(selectedTask.getProgress());
        } else {
            currentTask.setId(null);
            currentTask.setPriority("");
            currentTask.setDescription("");
            currentTask.setProgress(0);
        }

    }
}
