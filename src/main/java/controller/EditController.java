package controller;

/**
 * The implementing controller deals with the mapping between user input from the UI and model/database objects (e.g. POJOs).
 * @param <T> the model type
 */
@SuppressWarnings("unused")
public interface EditController<T> {

    /**
     * Maps the user input from the UI fields into a model object.
     * @return model object based on contents from UI fields
     */
    T getPOJOFromGUIFields();

    /**
     * Maps the model object into the UI fields.
     * @param from model object where contents will be displayed from
     */
    void setContentsFromPOJO(T from);
}
