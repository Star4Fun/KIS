package util;

import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.function.Function;

public class GenericCellDisplay<S, T, C extends Cell<T>> implements Callback<S, C> {

    private final Function<T, String> textFunction;
    private final CellFactory<T, C> cellFactory;

    public GenericCellDisplay(Function<T, String> textFunction, CellFactory<T, C> cellFactory) {
        this.textFunction = textFunction;
        this.cellFactory = cellFactory;
    }

    @Override
    public C call(S param) {
        return cellFactory.create(textFunction);
    }

    public interface CellFactory<T, C extends Cell<T>> {
        C create(Function<T, String> textFunction);
    }

    public static class ListViewCellFactory<T> extends GenericCellDisplay<ListView<T>, T, ListCell<T>> {

        public ListViewCellFactory(Function<T, String> textFunction) {
            super(textFunction, GenericListCell::new);
        }

        public static class GenericListCell<T> extends ListCell<T> {
            private final Function<T, String> textFunction;

            public GenericListCell(Function<T, String> textFunction) {
                this.textFunction = textFunction;
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(textFunction.apply(item));
                }
            }
        }
    }

    public static class TableViewCellFactory<S, T> extends GenericCellDisplay<TableColumn<S, T>, T, TableCell<S, T>> {

        public TableViewCellFactory(Function<T, String> textFunction) {
            super(textFunction, GenericTableCell::new);
        }

        public static class GenericTableCell<S, T> extends TableCell<S, T> {
            private final Function<T, String> textFunction;

            public GenericTableCell(Function<T, String> textFunction) {
                this.textFunction = textFunction;
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(textFunction.apply(item));
                }
            }
        }
    }
}
