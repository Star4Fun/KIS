package controller.overview;

import controller.Refreshable;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

@SuppressWarnings("unused")
public class PatientOverviewController implements Refreshable {

    @FXML
    private ComboBox<?> stations;
    @FXML
    private TableView<?> opListPatients;
    @FXML
    private CheckBox opListFilterPostOp;

    @FXML
    private void initialize() {
    }

    @Override
    public void fetchAndSetAll() {
        // TODO: fetch contents from db and set in UI
    }
}
