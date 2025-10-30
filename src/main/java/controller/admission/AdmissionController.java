package controller.admission;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ControllerInjection;

import java.io.IOException;
import java.util.List;

@SuppressWarnings({"Convert2MethodRef", "FieldCanBeLocal"})
public class AdmissionController {

	private static final Logger logger = LoggerFactory.getLogger(AdmissionController.class);
	
	private Parent root;

	@FXML
    private ComboBox<?> selectPatient;
    
	/**
	 * For KIS System
	 */
    @FXML
    private CaseController caseController; //line 26-27 in TabAdmission.fxml
    
    /**
	 * For OPS System
	 */
    @FXML
    private OPController opController; //line 26-27 in TabAdmission.fxml

	private final MainController mainController;

	public AdmissionController(MainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private void initialize() {
		logger.info("Initialize Admission-Tab!");
    }	
	
    @FXML
	private void create() {
		logger.info("Creating Case/OP!");
    }
    
    @FXML
	private void createAndShowNewPatientWindow() {
		logger.info("New Patient window!");
		try {
			root = ControllerInjection.load("/fxml/PanePatient.fxml",
                                                   PatientController.class, () -> new PatientController(mainController));
			Stage stage = new Stage();
			stage.setTitle("Patient");
			Scene scene = new Scene(root);

			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			logger.error("Error creating patient window!", e);
		}
    }
    
    
}
