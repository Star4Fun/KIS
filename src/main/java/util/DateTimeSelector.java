package util;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Combines 3 javafx elements into one group.
 * Uses (in order)
 *  - {@link DatePicker} date selection
 *  - {@link TextField} time selection
 *  - {@link CheckBox} current time selected
 * The elements will be arranged in an {@link HBox}.
 */
@SuppressWarnings("unused")
public class DateTimeSelector extends HBox {

    private final ObjectProperty<LocalDateTime> localDateTime;

    public DateTimeSelector() {
        this(DateUtil.DATE_PATTERN, DateUtil.TIME_PATTERN);
    }

    public DateTimeSelector(@NamedArg("datePattern") String datePattern, @NamedArg("timePattern") String timePattern) {
        super(10);
        localDateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        DatePicker datePicker = new CompleteDatePicker(datePattern);
        TextField time = new TextField();
        time.setPromptText(timePattern);
        CheckBox currentDateTimeCheckBox = new CheckBox("Aktuelle Zeit");
        DateUtil.link(datePicker, time, DateTimeFormatter.ofPattern(timePattern), currentDateTimeCheckBox, localDateTime);
        getChildren().addAll(datePicker, time, currentDateTimeCheckBox);
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime.get();
    }

    public ObjectProperty<LocalDateTime> localDateTimeProperty() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime.set(localDateTime);
    }
}
