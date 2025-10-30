package controller.admission;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.controlsfx.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.util.*;

@SuppressWarnings("unused")
public class PatientController /*implements EditController<POJO>*/ {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @FXML
    private TextField patientFirstname;
    @FXML
    private TextField patientLastname;
    @FXML
    private DatePicker patientBirthdate;
    @FXML
    private TextField patientBirthplace;
    @FXML
    private TextField patientStreet;
    @FXML
    private TextField patientPostcode;
    @FXML
    private TextField patientCellphone;
    @FXML
    private DBComboBox<DummyGender> patientGender;
    @FXML
    private Button saveButton;

    @FXML
    private AutoCompleteTextField<Patient> patientAutoCompleteTextField;

    @FXML
    private ListView<ValidationMessage> validationListView;

    private final ObservableList<ValidationMessage> customValidationMessages = FXCollections.observableArrayList();

    private final ValidationSupport validationSupport = ValidationUtil.newValidationSupport();

    private final MainController mainController;

    public PatientController(MainController mainController) {
        this.mainController = mainController;
    }

    private record Patient(String vorname, String nachname, int id) {}

    @FXML
    private void initialize() {
        logger.info("Initialize Patient-Tab!");

        List<Patient> patientSet = new ArrayList<>();
        patientSet.add(new Patient("Gustav", "Gans", 1));
        patientSet.add(new Patient("Dagobert", "Duck", 2));
        patientSet.add(new Patient("Donald", "Duck", 3));
        patientSet.add(new Patient("Dänisch", "Bürger", 4));
//        List<String> patientSet = new ArrayList<>(List.of("Gustav", "Dagobert", "Donald", "Dänisch"));

        patientAutoCompleteTextField.setEntries(patientSet);
        patientAutoCompleteTextField.setText(patient -> "%s %s (%s)".formatted(patient.vorname(), patient.nachname(), patient.id()));
        patientAutoCompleteTextField.setClosedWorld(true);
        patientAutoCompleteTextField.lastSelectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Old Value: " + oldValue);
            System.out.println("New Value: " + newValue);
        });
        patientAutoCompleteTextField.initValidation(validationSupport, false);

        ValidationUtil.bindValidationOutputTo(validationListView, validationSupport);


        patientGender.setText(dummyGender -> dummyGender.longDisplay);
        patientGender.setItems(FXCollections.observableArrayList(DummyGender.values()));

        validationSupport.registerValidator(patientFirstname,
                                            false,
                                            Validator.combine(ValidationUtil.LENGTH_50_VALIDATOR,
                                                              ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR
                                            )
        );
        validationSupport.registerValidator(patientLastname,
                                            false,
                                            Validator.combine(ValidationUtil.LENGTH_50_VALIDATOR,
                                                              ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR
                                            )
        );
        validationSupport.registerValidator(patientBirthdate, false, ValidationUtil.DATE_NOT_IN_FUTURE_VALIDATOR);
        validationSupport.registerValidator(patientBirthplace, false, ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR);
        validationSupport.registerValidator(patientStreet, false, ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR);
        validationSupport.registerValidator(patientPostcode, false, ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR);
        validationSupport.registerValidator(patientCellphone, false, ValidationUtil.ILLEGAL_HL7_CHARACTERS_VALIDATOR);
        validationSupport.registerValidator(patientGender,
                                            true,
                                            Validator.combine(Validator.createEmptyValidator("Pflichtfeld!"),
                                                              ValidationUtil.enumValidator(DummyGender.class,
                                                                                           DummyGender.UNKNOWN,
                                                                                           "Unbekannt nur im Notfall auswählen."
                                                              )
                                            )
        );
        saveButton.disableProperty().bind(validationSupport.invalidProperty());
    }

    @FXML
    private void createPatient(ActionEvent event) {
        logger.info("Creating patient!");
        ((Stage) patientFirstname.getScene().getWindow()).close(); // hack to close the window
        TextFlow textFlow = new TextFlow();
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        Text text1 = new Text("Der Patient ");
        Text text2 = new Text(patientFirstname.getText() + " " + patientLastname.getText() + " ");
        text2.setStyle("-fx-font-weight: bold");
        Text text3 = new Text("wurde erfolgreich erstellt.");
        textFlow.getChildren().addAll(text1, text2, text3);
        SuccessNotification.show(mainController.getRoot(), "Patient erstellt", textFlow, 10);

    }

    private enum DummyGender {
        MALE("m", "Männlich"),
        FEMALE("w", "Weiblich"),
        OTHER("o", "Divers"),
        UNKNOWN("u", "unbekannt");
        private final String shortDisplay, longDisplay;

        DummyGender(String shortDisplay, String longDisplay) {
            this.shortDisplay = shortDisplay;
            this.longDisplay = longDisplay;
        }
    }

}

