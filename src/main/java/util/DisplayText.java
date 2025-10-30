package util;

import java.util.function.Function;

/**
 * Provides methods to set display strings of implementing controls.
 * @param <T> The type from which data will be mapped into a string.
 * @param <G> The type of control showing the display string.
 */
public interface DisplayText<T, G extends GenericCellDisplay<?,T,?>> extends RawDisplayText<T> {

    /**
     * Sets the display string.
     * @param cellDisplay The implementing cell display containing the display string.
     */
    void setText(G cellDisplay);
}
