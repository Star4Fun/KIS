package util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DBComboBoxTest {

    private static Haustier reference;

    @BeforeAll
    public static void init() {
        reference = new Haustier("Wuffi", "Josef", 1, 3);
        Platform.startup(() -> {});
    }

    @Test
    public void testSimpleFXMLTranslation1() {
        String insideFXML = "util.Haustier -> spitzname (alter) von besitzer";
        DBComboBox<Haustier> dbComboBox = DBComboBox.valueOf(insideFXML);
        String actual = setContents(dbComboBox);
        // "Wuffi (3) von Josef"
        String expected = reference.getSpitzname() + " (" + reference.getAlter() + ") von " + reference.getBesitzer();
        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleFXMLTranslation2() {
        String insideFXML = "util.Haustier -> spitzname (spitzname) spitzname alter spitznamealter";
        DBComboBox<Haustier> dbComboBox = DBComboBox.valueOf(insideFXML);
        String actual = setContents(dbComboBox);
        // "Wuffi (Wuffi) Wuffi 3 Wuffi3"
        String expected = reference.getSpitzname() + " (" + reference.getSpitzname() + ") " + reference.getSpitzname() + " " + reference.getAlter() + " " + reference.getSpitzname() + reference.getAlter();
        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleFXMlTranslation3() {
        String insideFXML = "util.Haustier -> lieblingsspielzeug LieBlingsSPIElZeUG LIEBLINGSSPIELZEUG";
        DBComboBox<Haustier> dbComboBox = DBComboBox.valueOf(insideFXML);
        String actual = setContents(dbComboBox);
        // "1 1 1"
        String expected = reference.getLieblingsspielzeug() + " LieBlingsSPIElZeUG LIEBLINGSSPIELZEUG";
        assertEquals(expected, actual);
    }

    @Test
    public void testExceptions() {
        assertThrows(IllegalStateException.class, () -> DBComboBox.valueOf("Haustier -> spitzname (alter) von besitzer"));
        assertThrows(IllegalStateException.class, () -> DBComboBox.valueOf("util.Haustier  spitzname (alter) von besitzer"));
        assertDoesNotThrow(() -> DBComboBox.valueOf("util.Haustier -> spitzname (alter) von besitzer"));
        assertDoesNotThrow(() -> DBComboBox.valueOf("util.Haustier -> "));
    }

    private String setContents(DBComboBox<Haustier> dbComboBox) {
        dbComboBox.setItems(FXCollections.observableArrayList(reference));
        dbComboBox.getSelectionModel().select(reference);
        GenericCellDisplay.ListViewCellFactory.GenericListCell<Haustier> listCell = (GenericCellDisplay.ListViewCellFactory.GenericListCell<Haustier>) dbComboBox.getCellFactory().call(new ListView<>(dbComboBox.getItems()));
        listCell.updateItem(reference, false);
        String actual = listCell.getText();
        return actual;
    }
}
