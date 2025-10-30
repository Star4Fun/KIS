package util;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class containing utility methods where anything with a date is involved.
 */
@SuppressWarnings("unused")
public class DateUtil {

    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    public static void link(DatePicker datePicker, TextField time, CheckBox currentDateTimeCheckBox, ObjectProperty<LocalDateTime> localDateTime) {
        link(datePicker, time, TIME_FORMATTER, currentDateTimeCheckBox, localDateTime);
    }

    /**
     * Adds logic between the nodes.
     * If the checkbox is checked, the input for the other fields is disabled and set to the current date and time.
     * If the checkbox is unchecked (after previously being checked), the input for the other fields is enabled and cleared.
     * @param datePicker The datepicker to select the date from.
     * @param time The textfield to select the time from.
     * @param timeFormatter The formatter used for the display of the time.
     * @param currentDateTimeCheckBox The checkbox for selecting the current time.
     * @param localDateTime The property containing all three model values.
     */
    public static void link(DatePicker datePicker, TextField time, DateTimeFormatter timeFormatter, CheckBox currentDateTimeCheckBox, ObjectProperty<LocalDateTime> localDateTime) {
        // disable user inputs if currentDateTimeCheckBox is selected
        datePicker.disableProperty().bind(currentDateTimeCheckBox.selectedProperty());
        time.disableProperty().bind(currentDateTimeCheckBox.selectedProperty());

        // modelObject <-> datePicker
        localDateTime.addListener((observable, oldValue, newValue) -> {
            datePicker.setValue(newValue != null ? newValue.toLocalDate() : null);
        });
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            localDateTime.setValue(newValue != null ? LocalDateTime.of(newValue, localDateTime.getValue() != null ? localDateTime.getValue().toLocalTime() : LocalTime.now()) : null);
        });

        // modelObject <-> time
        localDateTime.addListener((observable, oldValue, newValue) -> {
            time.setText(newValue != null ? newValue.toLocalTime().format(timeFormatter) : "");
        });
        time.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // textfield is now deselected
            if(!newValue) {
                try {
                    localDateTime.setValue(LocalDateTime.of(localDateTime.getValue() != null ? localDateTime.getValue().toLocalDate() : LocalDate.now(), LocalTime.parse(time.getText(), timeFormatter)));
                } catch (DateTimeParseException e) {
                    time.setText("");
                }

            }
        });

        // modelObject <-> currentDateTimeCheckBox
        currentDateTimeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            localDateTime.setValue(newValue ? LocalDateTime.now() : null);
        });

    }
}
