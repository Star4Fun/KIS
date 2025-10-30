package util;

import java.util.function.Function;

public interface RawDisplayText<T> {

    /**
     * Sets the display string.
     * @param text Mapper function
     */
    void setText(Function<T, String> text);

}
