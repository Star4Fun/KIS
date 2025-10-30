package controller.communication;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class CommunicationsController {

    private final static Logger logger = LoggerFactory.getLogger(CommunicationsController.class);

    @FXML
    private TextField communicationsIpAddress;
    @FXML
    private TextField communicationsPort;    
    @FXML
    private ComboBox<?> communicationsObject;
    @FXML
    private TableView<?> ts;
    
	@FXML
	private void initialize() {
        logger.info("Initialize Communications-Tab!");
    }	
	
    @FXML
	private void send() {
        logger.info("Sending something!");
    }
    

}

