package controller.overview.caseop;
import controller.Refreshable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class DiagnosisController implements Refreshable /*, EditController<POJO>*/{

	private static final Logger logger = LoggerFactory.getLogger(DiagnosisController.class);

	@FXML
	private ComboBox<?> diagnosisOpId;
	@FXML
	private ComboBox<?> diagnosisIcdCode;
	@FXML
	private ComboBox<?> diagnosisType;
	@FXML
	private TextField diagnosisFreetext;	
	@FXML
	private TableView<?> diagnosisTable;

	@FXML
	private void initialize() {
		logger.info("Initialize Diagnosis-Tab!");
	}
	
	@FXML
	private void createDiagnosis(ActionEvent event){
		logger.info("Create diagnosis!");
	}

	@Override
	public void fetchAndSetAll() {
		// TODO: fetch contents from db and set in UI
	}
}
