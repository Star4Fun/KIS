package util;

import javafx.beans.NamedArg;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * "Connects" the textfield in a date picker with the model value (property).
 */
public class CompleteDatePicker extends DatePicker {

    public CompleteDatePicker(@NamedArg("pattern") String pattern) {
        this(null, DateUtil.DATE_PATTERN);
    }

    public CompleteDatePicker(LocalDate localDate, String pattern) {
        super(localDate);
        setPromptText(pattern);
        setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate object) {
                return object != null ? object.format(DateTimeFormatter.ofPattern(pattern)) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return LocalDate.parse(string, DateTimeFormatter.ofPattern(pattern));
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                setValue(getConverter().fromString(getEditor().getText()));
            }
        });
    }
}
