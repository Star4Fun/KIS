package controller.admission;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class OPController /*implements EditController<POJO>*/ {

    private static final Logger logger = LoggerFactory.getLogger(OPController.class);

    @FXML
    private ComboBox<?> opCaseId;
	@FXML
    private DatePicker opDate;
    @FXML
    private ComboBox<?> opType;
    @FXML
    private ComboBox<?> opRoom;
    @FXML
    private ComboBox<?> opSurgeonId;
	
    @FXML
	private void initialize() {
        logger.info("Initialize OP-Tab!");
	}

}
