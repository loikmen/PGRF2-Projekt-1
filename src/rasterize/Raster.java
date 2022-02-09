package rasterize;

import java.util.Optional;

public interface Raster<E> {

    default boolean isInsideBounds(int x, int y) {
        /*
        if (getWidth() >= 0) {
            return true;
        } else {
            return false;
        }
        */
        return getWidth() >= 0; // TODO
    }

//    boolean isValid(int x, int y);

    Optional<E> getElement(int x, int y);

    void setElement(int x, int y, E value);

    void clear();

    void setClearValue(E value);

    int getWidth();

    int getHeight();

}
