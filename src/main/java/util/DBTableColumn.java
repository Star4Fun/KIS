package util;

import javafx.scene.control.TableColumn;

import java.util.function.Function;

public class DBTableColumn<S,T> extends TableColumn<S,T> implements DisplayText<T, GenericCellDisplay.TableViewCellFactory<S,T>> {

    /**
     * No args constructor so the Scene Builder is happy.
     */
    public DBTableColumn() {
        super();
    }

    @Override
    public void setText(Function<T, String> text) {
        setText(new GenericCellDisplay.TableViewCellFactory<>(text));
    }

    @Override
    public void setText(GenericCellDisplay.TableViewCellFactory<S, T> cellDisplay) {
        setCellFactory(cellDisplay);
    }


}
