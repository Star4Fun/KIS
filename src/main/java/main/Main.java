package main;

import controller.MainController;
import controller.admission.AdmissionController;
import controller.admission.CaseController;
import controller.admission.OPController;
import controller.communication.CommunicationsController;
import controller.overview.OverviewController;
import controller.overview.PatientOverviewController;
import controller.overview.StationOverviewController;
import controller.overview.caseop.CaseOPOverviewController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ControllerInjection;
import util.UiTheme;

import java.util.Map;
import java.lang.Runtime;

@SuppressWarnings("Convert2MethodRef")
public class Main extends Application {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private Parent root;

    @SuppressWarnings("unused")
    public static void main(String[] args) {
	logger.info("Java runtime version: {}", Runtime.version());
        Application.launch(Main.class, args);
    }

    @Override
    public void init() throws Exception {
        // create one instance of the controller you want to share
        PatientOverviewController patientOverviewController = new PatientOverviewController();
        MainController mainController = new MainController();
        root = ControllerInjection.load("/fxml/MainForm.fxml",
                Map.of(
                        MainController.class, () -> mainController,

                        AdmissionController.class, () -> new AdmissionController(mainController),
                        CaseController.class, () -> new CaseController(),
                        OPController.class, () -> new OPController(),

                        OverviewController.class, () -> new OverviewController(),
                        PatientOverviewController.class, () -> patientOverviewController, // use shared controller
                        CaseOPOverviewController.class, () -> new CaseOPOverviewController(patientOverviewController), // supply shared controller with other controller
                        StationOverviewController.class, () -> new StationOverviewController(),

                        CommunicationsController.class, () -> new CommunicationsController()
                )
        );
        mainController.setRoot(root);
    }

    @Override
    public void start(Stage primaryStage) {
        // more themes are available in the UITheme enumeration
        Application.setUserAgentStylesheet(UiTheme.CUPERTINO_LIGHT.getCssFileName());

        primaryStage.setTitle("eHealth Praktikum GUI");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/modena-bridge.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
    }

}
