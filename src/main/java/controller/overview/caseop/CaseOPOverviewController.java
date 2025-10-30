package controller.overview.caseop;

import controller.Refreshable;
import controller.overview.PatientOverviewController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ControllerInjection;

import java.io.IOException;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "Convert2MethodRef"})
public class CaseOPOverviewController implements Refreshable {

    private static final Logger logger = LoggerFactory.getLogger(CaseOPOverviewController.class);

    @FXML
    private TableView<?> caseOPTable;
    @FXML
    private Button btnDiag;
    @FXML
    private Button btnProc;

    private final PatientOverviewController patientOverviewController;

    public CaseOPOverviewController(PatientOverviewController patientOverviewController) {
        this.patientOverviewController = patientOverviewController;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void createAndShowDiagnosisWindow(ActionEvent actionEvent) {
        logger.info("New Patient Window!");

        try {
            Parent root = ControllerInjection.load("/fxml/PaneDiagnosis.fxml",
                    DiagnosisController.class, () -> new DiagnosisController());
            Stage stage = new Stage();
            stage.setTitle("Diagnosen");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            logger.error("Error loading Diagnosis window", e);
        }
    }

    @FXML
    private void createAndShowProcedureWindow(ActionEvent actionEvent) {
        logger.info("New Patient Window!");
        try {
            Parent root = ControllerInjection.load("/fxml/PaneProcedure.fxml",
                    ProcedureController.class, () -> new ProcedureController());
            Stage stage = new Stage();
            stage.setTitle("Prozeduren");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            logger.error("Error loading Procedure window", e);
        }
    }

    @Override
    public void fetchAndSetAll() {
        // TODO: fetch contents from db and set in UI
    }
}
