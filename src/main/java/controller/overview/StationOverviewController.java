package controller.overview;

import controller.Refreshable;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

@SuppressWarnings("unused")
public class StationOverviewController implements Refreshable {
    @FXML
    private TableView<?> stationTable;

    @FXML
    private void initialize() {

    }

    @Override
    public void fetchAndSetAll() {
        // TODO: fetch contents from db and set in UI
    }
}
