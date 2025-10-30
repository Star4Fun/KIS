package util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.function.Function;

/**
 * Allows for easy and clean display of complex elements in a combobox while keeping the model logic in objects without
 * the need for parsing display strings back and forth.
 * @param <T> the type of the combobox, most likely a POJO from JOOQ.
 */
public class CallbackCellDisplay<T> implements Callback<ListView<T>, ListCell<T>> {

    private final Function<T, String> text;

    public CallbackCellDisplay(Function<T, String> text) {
        this.text = text;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new ListCellDisplay<>(text);
    }

    // wird mittels "cellFactoryProvider.cellFactory" in fxml aufgerufen
    public Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return this;
    }
    // wird mittels "cellFactoryProvider.buttonCell" in fxml aufgerufen
    public ListCell<T> getButtonCell() {
        return getCellFactory().call(null);
    }

    public static class ListCellDisplay<T> extends ListCell<T> {
        private final Function<T, String> text;

        private ListCellDisplay(Function<T, String> text) {
            this.text = text;
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
            } else {
                setText(text.apply(item));
            }
        }
    }
}
