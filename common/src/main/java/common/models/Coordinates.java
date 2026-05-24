package common.models;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private int x;
    private Long y;

    public Coordinates(int x, Long y){
        if (y == null) {
            throw new IllegalArgumentException("y не может быть null");
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public Long getY() {
        return y;
    }
}