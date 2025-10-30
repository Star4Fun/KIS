package controller.overview.caseop;
import controller.Refreshable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ProcedureController implements Refreshable /*, EditController<POJO>*/ {

	private static final Logger logger = LoggerFactory.getLogger(ProcedureController.class);

	@FXML
	private TableView<?> procedureTable;
	@FXML
	private ComboBox<?> procedureOp;
	@FXML
	private ComboBox<?> procedureOpsCode;
	
	
    @FXML
	private void initialize() {
		logger.info("Initialize Procedure-Tab!");
    }
	
	@FXML
	private void createProcedure() {
		logger.info("Create procedure!");
	}

	@Override
	public void fetchAndSetAll() {
		// TODO: fetch contents from db and set in UI
	}
}
