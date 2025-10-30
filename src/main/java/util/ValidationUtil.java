package util;

import controller.communication.CommunicationsController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.function.Predicate;

public class ValidationUtil {

    public static final Validator<String> LENGTH_10_VALIDATOR = lengthValidator(10);
    public static final Validator<String> LENGTH_20_VALIDATOR = lengthValidator(20);
    public static final Validator<String> LENGTH_50_VALIDATOR = lengthValidator(50);
    public static final Validator<String> LENGTH_100_VALIDATOR = lengthValidator(100);
    public static final Validator<String> LENGTH_500_VALIDATOR = lengthValidator(500);
    public static final Validator<String> ILLEGAL_HL7_CHARACTERS_VALIDATOR = Validator.createRegexValidator(
            "^, \\, & und ~ sind nicht zulässig!",
            "[^&~\\\\^]*",
            Severity.ERROR
    );

    public static final Validator<LocalDate> DATE_NOT_IN_FUTURE_VALIDATOR = Validator.createPredicateValidator(localDate -> localDate == null || (localDate.isEqual(
            LocalDate.now()) || localDate.isBefore(
            LocalDate.now())), "Das Datum darf nicht in der Zukunft liegen!");
    public static final Validator<LocalTime> TIME_NOT_IN_FUTURE_VALIDATOR = Validator.createPredicateValidator(localTime -> localTime != null && localTime.isBefore(
            LocalTime.now()), "Die Uhrzeit darf nicht in der Zukunft liegen!");
    public static final Validator<LocalDateTime> DATE_TIME_NOT_IN_FUTURE_VALIDATOR = Validator.createPredicateValidator(localDateTime -> localDateTime != null && (localDateTime.isEqual(LocalDateTime.now()) || localDateTime.isBefore(
            LocalDateTime.now())), "Der Zeitpunkt darf nicht in der Zukunft liegen!");


    public static <E extends Enum<E>> Validator<E> enumValidator(Class<E> enumClass, E defaultIfNotNullable, String warningMessage) {
        return Validator.createEqualsValidator(warningMessage, Severity.WARNING, EnumSet.allOf(enumClass).stream()
                                                                                        .filter(Predicate.not(defaultIfNotNullable::equals))
                                                                                        .toList()
        );
    }

    private static Validator<String> lengthValidator(int length) {
        return Validator.createPredicateValidator(string -> string.length() <= length,
                                                  "Zeichenlimit von %s überschritten!".formatted(length)
        );
    }

    public static ValidationSupport newValidationSupport() {
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.setValidationDecorator(new NoOPValidationDecoration());
        validationSupport.setErrorDecorationEnabled(false);
        return validationSupport;
    }

    public static void bindValidationOutputTo(ListView<ValidationMessage> listView, ValidationSupport validationSupport) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ValidationMessage validationMessage, boolean empty) {
                super.updateItem(validationMessage, empty);
                if(empty || validationMessage == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String label;
                    Object userData = validationMessage.getTarget().getUserData();
                    if(userData == null) {
                        label = validationMessage.getTarget().getId();
                    } else {
                        label = userData.toString();
                    }
                    setText("%s - %s: %s".formatted(validationMessage.getSeverity(),
                                                    label,
                                                    validationMessage.getText()
                    ));

                    switch(validationMessage.getSeverity()) {
                        case ERROR -> setStyle("-fx-background-color: #fd3a48");
                        case WARNING -> setStyle("-fx-background-color: #f3a516");
                        case INFO -> setStyle("-fx-background-color: #1697f3");
                        case OK -> setStyle("-fx-background-color: #55f316");
                    }
                }
            }
        });

        listView.itemsProperty().bind(
                Bindings.createObjectBinding(() ->
                                             {
                                                 if(validationSupport.getValidationResult() == null) {
                                                     return FXCollections.observableArrayList();
                                                 } else {
                                                     return FXCollections.observableArrayList(
                                                             validationSupport.getValidationResult().getMessages());
                                                 }

                                             },
                                             validationSupport.validationResultProperty()
                ));
    }

    private static class NoOPValidationDecoration implements ValidationDecoration {

        @Override
        public void removeDecorations(Control target) {

        }

        @Override
        public void applyValidationDecoration(ValidationMessage message) {

        }

        @Override
        public void applyRequiredDecoration(Control target) {

        }
    }
}
