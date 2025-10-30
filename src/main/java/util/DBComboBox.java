package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Displays the list of model objects in customizable readable text.
 * @param <T> the model type (probably a POJO)
 */
public class DBComboBox<T> extends ComboBox<T> implements DisplayText<T, GenericCellDisplay.ListViewCellFactory<T>>{

    /**
     * No args constructor so the Scene Builder is happy.
     */
    public DBComboBox() {
        this(text -> "");
    }

    public DBComboBox(Function<T, String> text) {
        this(new GenericCellDisplay.ListViewCellFactory<>(text));
    }

    public DBComboBox(GenericCellDisplay.ListViewCellFactory<T> cellDisplay) {
        this(cellDisplay, FXCollections.observableArrayList());
    }

    public DBComboBox(ObservableList<T> observableList) {
        this(new GenericCellDisplay.ListViewCellFactory<>(text -> ""), observableList);
    }

    public DBComboBox(Function<T, String> text, ObservableList<T> observableList) {
        this(new GenericCellDisplay.ListViewCellFactory<>(text), observableList);
    }

    public DBComboBox(GenericCellDisplay.ListViewCellFactory<T> cellDisplay, ObservableList<T> observableList) {
        super(observableList);
        setText(cellDisplay);
    }

    @Override
    public void setText(Function<T, String> text) {
        setText(new GenericCellDisplay.ListViewCellFactory<>(text));
    }

    @Override
    public void setText(GenericCellDisplay.ListViewCellFactory<T> cellDisplay) {
        setButtonCell(cellDisplay.call(null));
        setCellFactory(cellDisplay);
    }

    /**
     * Should only be used from FXML.
     * <p>
     * Programmatically one should always use a constructor.
     * <p>
     * The input requires a strict pattern. Let's look at an example.
     * <p>
     * Suppose you have generated a POJO for the "Haustier" table. That means our POJO has the following getter methods:
     * <ul>
     *     <li>String getSpitzname()
     *     <li>String getBesitzer()
     *     <li>int getLieblingsspielzeug()
     *     <li>int getAlter()
     * </ul>
     * In FXML you may then create the following fromFXML:
     * <pre>
     * {@code <DBComboBox fx:value="db.jooq.Haustier -> spitzname (alter) von besitzer" />}
     * </pre>
     * First you need to supply the fully qualified class name followed by a "->". Then you can write the fromFXML which will be displayed in the combo box.
     * This method will take all getter methods from the provided class, strip the "get" and look for a match within in the text in all lowercase.
     * So the given fromFXML will be identical to what the programmatic version using lambda expressions would produce
     * <pre>
     * {@code new DBComboBox<Haustier>(new CallbackCellDisplay<>(haustier -> haustier.getSpitzname() + " (" + haustier.getAlter() + ") von " + haustier.getBesitzer()))}
     * </pre>
     *
     *
     * @param fromFXML The fromFXML set in the FXML file following the pattern "fully_qualified_class_name -> any_method".
     * @param <T> The type of POJO.
     * @return An instance of DBComboBox
     */
    public static <T> DBComboBox<T> valueOf(String fromFXML) {
        String[] split = fromFXML.split("->", 2);
        // "->" is not in the fromFXML
        if (split.length == 1) {
            throw new IllegalStateException(String.format("The text '%s' in FXML for the DBComboBox did not contain a '->'.", fromFXML));
        }
        String clazzString = split[0].strip();
        String content = split[1].strip();
        try {
            Class<?> clazz = Class.forName(clazzString);
            Map<String, Method> possiblePlaceholders = Arrays.stream(clazz.getMethods())
                    .filter(method -> method.getName().startsWith("get"))
                    .collect(Collectors.toMap(
                            method -> method.getName().replace("get", "").toLowerCase(),
                            Function.identity(),
                            (method, method2) -> method
                    ));
            Function<T, String> displayFunc = getStringFunction(content, possiblePlaceholders, clazzString);
            return new DBComboBox<>(new GenericCellDisplay.ListViewCellFactory<>(displayFunc));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(String.format("The class '%s' in the text '%s' in FXML for the DBComboBox could not be found. " +
                    "Make sure to always use the fully qualified name (that includes the package name).", clazzString, fromFXML));
        }
    }

    private static <T> Function<T, String> getStringFunction(String content, Map<String, Method> possiblePlaceholders, String clazzString) {
        List<String> foundPlaceholders = getStrings(content, possiblePlaceholders);

        return t -> foundPlaceholders.stream().reduce(content, (tempContent, placeHolder) -> {
            try {
                return tempContent.replace(placeHolder, possiblePlaceholders.get(placeHolder).invoke(t).toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                //practically impossible to be thrown because foundPlaceholders should only contain strings which already match a method in the pojo
                throw new IllegalStateException(String.format("The display text '%s' for the DBComboBox contains the text '%s' which does not correspond to any method in the %s class.",
                                                              content, placeHolder,
                                                              clazzString
                ));
            }
        });
    }

    private static List<String> getStrings(String content, Map<String, Method> possiblePlaceholders) {
        List<String> foundPlaceholders = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            for (String placeholder : possiblePlaceholders.keySet()) {
                StringBuilder partialMatch = new StringBuilder();
                for (int j = 0; j < placeholder.length(); j++) {
                    if (content.charAt(i) == placeholder.charAt(j)) {
                        partialMatch.append(content.charAt(i));
                        i++;
                        if(i >= content.length()) {
                            break;
                        }
                    }
                }
                if (partialMatch.toString().equals(placeholder)) {
                    foundPlaceholders.add(placeholder);
                }
                i -= partialMatch.length();
            }
        }
        return foundPlaceholders;
    }
}
