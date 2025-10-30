package util;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.controlsfx.tools.ValueExtractor;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.<p>
 * <p>
 * If the entered text matches a part of any of the supplied entries these are
 * going to be displayed in a popup. Further the matching part of the entry is
 * going to be displayed in a special style, defined by
 * {@link #textOccurenceStyle textOccurenceStyle}. The maximum number of
 * displayed entries in the popup is defined by
 * {@link #maxEntries maxEntries}.<br>
 * By default the pattern matching is not case-sensitive. This behaviour is
 * defined by the {@link #caseSensitive caseSensitive}
 * .<p>
 * <p>
 * The AutoCompleteTextField also has a List of
 * {@link #filteredEntries filteredEntries} that is equal to the search results
 * if search results are not empty, or {@link #filteredEntries filteredEntries}
 * is equal to {@link #entries entries} otherwise. If
 * {@link #popupHidden popupHidden} is set to true no popup is going to be
 * shown. This list can be used to bind all entries to another node (a ListView
 * for example) in the following way:
 * <pre>
 * <code>
 * AutoCompleteTextField auto = new AutoCompleteTextField(entries);
 * auto.setPopupHidden(true);
 * SimpleListProperty filteredEntries = new SimpleListProperty(auto.getFilteredEntries());
 * listView.itemsProperty().bind(filteredEntries);
 * </code>
 * </pre>
 *
 * @param <S>
 *
 * @author Caleb Brinkman
 * @author Fabian Ochmann
 */
public class AutoCompleteTextField<S> extends TextField implements RawDisplayText<S> {

    private static final Logger logger = LoggerFactory.getLogger(AutoCompleteTextField.class);

    private static final Locale DE_LOCALE = Locale.GERMAN;

    private Class<S> clazz;

    private final ObjectProperty<S> lastSelectedItem = new SimpleObjectProperty<>();

    /**
     * The existing autocomplete entries.
     */
    private Collection<S> entries;

    /**
     * The set of filtered entries:<br>
     * Equal to the search results if search results are not empty, equal to
     * {@link #entries entries} otherwise.
     */
    private final ObservableList<S> filteredEntries
            = FXCollections.observableArrayList();

    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;

    /**
     * Indicates whether the search is case sensitive or not. <br>
     * Default: false
     */
    private boolean caseSensitive = false;

    /**
     * Indicates whether the Popup should be hidden or displayed. Use this if
     * you want to filter an existing list/set (for example values of a
     * {@link javafx.scene.control.ListView ListView}). Do this by binding
     * {@link #getFilteredEntries() getFilteredEntries()} to the list/set.
     */
    private boolean popupHidden = false;

    /**
     * The CSS style that should be applied on the parts in the popup that match
     * the entered text. <br>
     * Default: "-fx-font-weight: bold; -fx-fill: red;"
     * <p>
     * Note: This style is going to be applied on an
     * {@link javafx.scene.text.Text Text} instance. See the <i>JavaFX CSS
     * Reference Guide</i> for available CSS Propeties.
     */
    private String textOccurenceStyle = "-fx-font-weight: bold; "
            + "-fx-fill: red;";

    /**
     * The maximum Number of entries displayed in the popup.<br>
     * Default: 10
     */
    private int maxEntries = 10;

    /**
     * Mapping function for displaying objects of type S.
     */
    private Function<S, String> textMapper;

    /**
     * If true, then only the supplied entries are valid options.
     * If false, then any input string is considered valid, even if it does not match any existing valid option. Only works on strings.
     */
    private boolean closedWorld = true;

    /**
     * Validation Support for rich error/warning messages.
     */
    private ValidationSupport validationSupport;

    /**
     * No-args constructor so the SceneBuilder is happy.
     */
    public AutoCompleteTextField() {
        super();
    }

    /**
     * Construct a new AutoCompleteTextField.
     *
     * @param entrySet
     */
    public AutoCompleteTextField(Collection<S> entrySet) {
        super();
        init(entrySet);
    }

    private void init(Collection<S> collection) {
        this.entries = (collection == null ? new TreeSet<>() : collection);
        this.filteredEntries.addAll(entries);

        entriesPopup = new ContextMenu();

        textProperty().addListener((ObservableValue<? extends String> observableValue, String s, String s2) ->
                                   {
                                       if (getText() == null || getText().isEmpty()) {
                                           filteredEntries.clear();
                                           filteredEntries.addAll(entries);
                                           lastSelectedItem.set(null);
                                           entriesPopup.hide();
                                       } else {
                                           LinkedList<S> searchResult = new LinkedList<>();
                                           String searchText = getText().trim().toLowerCase(DE_LOCALE); // Normalize input
                                           String[] searchWords = searchText.split("\\s+"); // Split input by spaces

                                           for (S entry : entries) {
                                               String entryText = textMapper != null ? textMapper.apply(entry) : entry.toString();
                                               String entryLower = entryText.toLowerCase(DE_LOCALE); // Normalize entry text

                                               // Check if **ALL** search words are contained somewhere in entry text
                                               boolean allWordsMatch = Arrays.stream(searchWords)
                                                                             .allMatch(entryLower::contains);

                                               if (allWordsMatch) {
                                                   searchResult.add(entry);
                                               }
                                           }
                                           if(!entries.isEmpty()) {
                                               filteredEntries.clear();
                                               filteredEntries.addAll(searchResult);
                                               //Only show popup if not in filter mode
                                               if(!isPopupHidden()) {
                                                   populatePopup(searchResult, getText());
                                                   if(!entriesPopup.isShowing()) {
                                                       entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                                                   }
                                               }
                                           } else {
                                               entriesPopup.hide();
                                           }
                                       }
                                   });


        EventHandler<? super KeyEvent> enterHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                selectSingleResult();
            }
        };

        addEventFilter(KeyEvent.KEY_PRESSED, enterHandler);

        if(!isPopupHidden()) {
            entriesPopup.addEventFilter(KeyEvent.KEY_PRESSED, enterHandler);
        }

        focusedProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) ->
                                      {
                                          selectSingleResult();
                                          entriesPopup.hide();
                                      });

        getEntryMenu().setOnAction(e ->
                                           e.getTarget().addEventHandler(Event.ANY, event ->
                                                                         {
                                                                             setTextFieldControlText(getLastSelectedObject());
                                                                         }
                                           ));
    }

    private void selectSingleResult() {
        if(closedWorld) {
            if(filteredEntries.isEmpty()) {
                if(validationSupport == null) {
                    logger.warn("Validation Support was not provided for the Auto-Complete TextField.");
                    logger.warn("The closed world assumption does not allow unknown states, only {} are known.", entries);
                }
                else {
                    // Easiest solution, but does not work because ControlsFX does not propagate changes in the message list
                    // to the outer validationResultProperty (for whatever reason they thought this was a good design decision)
                    // Alternative solution: Use ValueExtractor (at the end of the file)
                }
            }
            else if (filteredEntries.size() == 1) {
                lastSelectedItem.set(filteredEntries.get(0));
                setTextFieldControlText(lastSelectedItem.get());
                entriesPopup.hide();
            }
            else {
                lastSelectedItem.set(null);
            }
        }
        else {
            if(filteredEntries.isEmpty()) {
                S s = (S) getText(); // we checked earlier to make sure only strings work
                entries.add(s);
                filteredEntries.add(s);
                lastSelectedItem.set(s);
                //entriesPopup.hide();
            }
        }
    }

    private void setTextFieldControlText(S selectedObject) {
        if(selectedObject != null) {
            if(textMapper == null) {
                logger.warn(
                        "No display function found. Did you forget to set a display function for a AutoCompleteTextField?");
                setText(Object::toString); // Fallback to toString()
            }
            setText(textMapper.apply(getLastSelectedObject()));
        }
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public Collection<S> getEntries() {
        return entries;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult
     *         The set of matching strings.
     */
    private void populatePopup(List<S> searchResult, String text) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for(int i = 0; i < count; i++) {
            final S itemObject = searchResult.get(i);
            if(textMapper == null) {
                logger.warn(
                        "No display function found. Did you forget to set a display function for a AutoCompleteTextField?");
                setText(Object::toString); // Fallback to toString()
            }
            final String result = textMapper.apply(itemObject);
            int occurence;

            if(isCaseSensitive()) {
                occurence = result.indexOf(text);
            } else {
                occurence = result.toLowerCase().indexOf(text.toLowerCase());
            }
            if(occurence < 0) {
                continue;
            }
            //Part before occurrence (might be empty)
            Text pre = new Text(result.substring(0, occurence));
            //Part of (first) occurrence
            Text in = new Text(result.substring(occurence, occurence + text.length()));
            in.setStyle(getTextOccurenceStyle());
            //Part after occurrence
            Text post = new Text(result.substring(occurence + text.length()));

            TextFlow entryFlow = new TextFlow(pre, in, post);

            CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            item.setOnAction((ActionEvent actionEvent) ->
                             {
                                 lastSelectedItem.set(itemObject);
                                 entriesPopup.hide();
                             });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }

    public ObjectProperty<S> lastSelectedItemProperty() {
        return lastSelectedItem;
    }

    public S getLastSelectedObject() {
        return lastSelectedItem.get();
    }

    public ContextMenu getEntryMenu() {
        return entriesPopup;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getTextOccurenceStyle() {
        return textOccurenceStyle;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setTextOccurenceStyle(String textOccurenceStyle) {
        this.textOccurenceStyle = textOccurenceStyle;
    }

    public boolean isPopupHidden() {
        return popupHidden;
    }

    public void setPopupHidden(boolean popupHidden) {
        this.popupHidden = popupHidden;
    }

    public ObservableList<S> getFilteredEntries() {
        return filteredEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    public void setEntries(Collection<S> entries) {
        entries.stream().findFirst().ifPresent(s -> clazz = (Class<S>) s.getClass());
        init(entries);
    }

    @Override
    public void setText(Function<S, String> text) {
        this.textMapper = text;
    }

    public void setClosedWorld(boolean closedWorld) {
        if(!closedWorld) {
            if(clazz == null) {
                logger.error("Set entries before setting closed world!");
                return;
            }
            else if(clazz != String.class) {
                logger.error("Open world assumption only works with strings!");
                return;
            }
        }
        this.closedWorld = closedWorld;
    }

    public void initValidation(ValidationSupport validationSupport, boolean autoCompleteTextFieldIsRequired) {
        this.validationSupport = validationSupport;

        ValueExtractor.addObservableValueExtractor(control -> control instanceof AutoCompleteTextField<?>, param -> lastSelectedItem);

        this.validationSupport.registerValidator(this, autoCompleteTextFieldIsRequired, (control, o) -> {
            System.out.println(o);
            return ValidationResult.fromErrorIf(this, "Nur %s sind gültig!".formatted(entries.stream().map(textMapper).toList()), o == null || (closedWorld && entries.stream().noneMatch(o::equals)));
        });
    }
}
