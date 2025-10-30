package validation;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import util.ValidationUtil;

import java.util.List;

public class ValidationDemoController {


    private Parent root;

    @FXML
    ValidationWrapper valUsername;

    @FXML
    ValidationWrapper valPassword;

    @FXML
    Button submitButton;

    @FXML
    private void initialize() {
        valUsername.setupValidation(
                List.of(
                        Validator.createEmptyValidator("The username must not be empty"),
                        ValidationUtil.LENGTH_10_VALIDATOR
                )

        );
        valPassword.setupValidation(
                List.of(
                        Validator.createEmptyValidator("The password must not be empty"),
                        Validator.createRegexValidator("The password must contain at least one digit", ".*[0-9].*", Severity.ERROR),
                        Validator.createRegexValidator("The password must contain at least one uppercase letter", ".*[A-Z].*", Severity.ERROR),
                        Validator.createPredicateValidator((value) -> value.length() < 6, "The password is too strong", Severity.ERROR)
                )
        );
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            List<Boolean> allValid = List.of(valUsername.isControlValidProperty().get(), valPassword.isControlValidProperty().get());
            return allValid.contains(false);
        }, valUsername.isControlValidProperty(), valPassword.isControlValidProperty()));
    }

}
