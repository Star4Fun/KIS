package controller.overview;

import controller.Refreshable;
import controller.overview.caseop.CaseOPOverviewController;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class OverviewController implements Refreshable {

    private static final Logger logger = LoggerFactory.getLogger(OverviewController.class);

    @FXML
    private PatientOverviewController patientOverviewController; //line 7 in TabOverview.fxml

    @FXML
    private CaseOPOverviewController caseOPOverviewController; //line 7 in TabOverview.fxml

    @FXML
    private StationOverviewController stationOverviewController; //line 7 in TabOverview.fxml
    
    @FXML
	private void initialize() {
        logger.info("Initialize OPlist-Tab!");
    }

    @Override
    public void fetchAndSetAll() {
        // TODO: fetch contents from db and set in UI
        /*
        patientOverviewController.fetchAndSetAll();
        caseOverviewController.fetchAndSetAll();
        stationOverviewController.fetchAndSetAll();
        */
    }
}
