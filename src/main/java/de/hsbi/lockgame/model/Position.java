package de.hsbi.lockgame.model;

public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null) {
            return false;
        }

        if(other instanceof Position otherPosition) {
            return x == otherPosition.x && y == otherPosition.y;
        } else return false;
    }

    @Override
    public String toString() {
        return "Position(" + x + ", " + y + ")";
    }
}
