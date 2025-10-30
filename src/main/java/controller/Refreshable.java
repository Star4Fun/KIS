package controller;

/**
 * Controller implementing this interface are responsible to showing information from the database in the UI.
 */
@SuppressWarnings("unused")
public interface Refreshable {

    /**
     * Fetches (for this controller) relevant data from the database and shows them in the UI.
     */
    void fetchAndSetAll();
}
