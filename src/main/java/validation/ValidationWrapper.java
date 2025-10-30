package validation;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import util.ValidationUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ValidationWrapper extends VBox implements Initializable {

    @FXML
    HBox controlWrapper;

    @FXML
    Label validationIcon;

    @FXML
    Label controlLabel;

    private final StringProperty _labelText = new SimpleStringProperty("Enter a label through the 'labelText' property");

    private final Tooltip messageTooltip = new Tooltip();

    @SuppressWarnings("unused")
    public StringProperty labelTextProperty() {
        return _labelText;
    }

    @SuppressWarnings("unused")
    public void setLabelText(String labelText) {
        this._labelText.set(labelText);
    }

    @SuppressWarnings("unused")
    public String getLabelText() {
        return _labelText.get();
    }

    private final DoubleProperty labelSize = new SimpleDoubleProperty(100);

    public DoubleProperty labelSizeProperty() {
        return labelSize;
    }

    public void setLabelSize(double labelSize) {
        this.labelSize.set(labelSize);
    }

    public double getLabelSize() {
        return labelSize.get();
    }

    private final ObjectProperty<Control> _content = new SimpleObjectProperty<>();

    public ObjectProperty<Control> contentProperty() {
        return _content;
    }

    private final ObservableList<ValidationMessage> validationMessages = FXCollections.observableArrayList();

    private final ValidationSupport validationSupport = ValidationUtil.newValidationSupport();

    private final BooleanProperty _isControlValid = new SimpleBooleanProperty();

    public BooleanProperty isControlValidProperty() {
        return _isControlValid;
    }

    public void setContent(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("control must not be null");
        }
        this._content.set(control);
        controlWrapper.getChildren().add(control);
    }

    public Control getContent() {
        return _content.get();
    }


    public ValidationWrapper() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/validation/ValidationWrapper.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _labelText.addListener((observable, oldValue, newValue) -> controlLabel.setText(newValue));
        validationIcon.setMinSize(24, 24);
        controlLabel.setMinWidth(labelSize.doubleValue());
        labelSize.addListener((o, o2, n) -> controlLabel.setMinWidth(n.doubleValue()));
        validationIcon.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (_isControlValid.get()) {
                        return "✓";
                    } else {
                        return "⚠";
                    }
                },
                _isControlValid
        ));

        validationIcon.textFillProperty().bind(Bindings.createObjectBinding(
                () -> {
                    if (_isControlValid.get()) {
                        return Color.GREEN;
                    } else {
                        return Color.ORANGE;
                    }
                }, _isControlValid
        ));
        messageTooltip.textProperty().bind(Bindings.createStringBinding(() -> {
            if (validationMessages.isEmpty()) {
                return "Your input is valid";
            } else {
                return String.format("%d validation error%s, click for details", validationMessages.size(), validationMessages.size() == 1 ? "" : "s");
            }
        }, validationMessages));
        validationIcon.setOnMouseClicked((e) -> {
            showValidationAlert();
        });
        controlLabel.setOnMouseClicked((e) -> {
            showValidationAlert();
        });
        _isControlValid.set(false);
        this.validationIcon.setTooltip(messageTooltip);
        this.controlLabel.setTooltip(messageTooltip);
    }

    private void showValidationAlert() {
        Alert alert;
        if (_isControlValid.get()) {
            alert = new Alert(Alert.AlertType.INFORMATION, "No validation errors", ButtonType.OK);
        } else {
            StringBuilder sb = new StringBuilder();
            this.validationMessages.forEach(m -> sb.append(m.getText()).append("\n"));
            alert = new Alert(Alert.AlertType.WARNING, sb.toString(), ButtonType.OK);
            alert.setHeaderText("Validation errors");
        }
        Platform.runLater(() -> alert.show());
    }

    public void setupValidation(List<Validator<String>> validators) {
        this.validationSupport.validationResultProperty().addListener((o1, o2, n) -> {
            validationMessages.clear();
            validationMessages.addAll(n.getMessages());
            _isControlValid.set(validationMessages.isEmpty());
        });
        @SuppressWarnings("unchecked")
        Validator<String>[] validatorsArray = validators.toArray(new Validator[0]);
        Validator<String> combined = Validator.combine(validatorsArray);
        validationSupport.registerValidator(this.getContent(), combined);

        this.validationSupport.invalidProperty().addListener((o1, o2, n) -> {
            System.out.println("Invalid: " + n);
        });
        if (getContent() instanceof TextInputControl) {
            ((TextInputControl) getContent()).textProperty().addListener((o1, o2, nv) -> revalidate());
        }
        if (getContent() instanceof ComboBoxBase) {
            ((ComboBoxBase<?>) getContent()).valueProperty().addListener((o1, o2, nv) -> revalidate());
        }
        // TODO @STUDENTS you MAY need to add more handlers for other controls
        revalidate();
    }

    public void revalidate() {
        this.validationSupport.revalidate();
    }
}
