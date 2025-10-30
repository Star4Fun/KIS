package controller.admission;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class CaseController{

    private static final Logger logger = LoggerFactory.getLogger(CaseController.class);

    @FXML
    private ComboBox<?> caseStation;
    @FXML
    private ComboBox<?> caseDoctor;
    @FXML
    private ToggleGroup caseType;
    @FXML
    private RadioButton caseAmb;
    @FXML
    private RadioButton caseStat;
    @FXML
    private DatePicker caseDate;
   
    @FXML
	private void initialize() {
        logger.info("Initialize Case-Tab!");
	}

}
