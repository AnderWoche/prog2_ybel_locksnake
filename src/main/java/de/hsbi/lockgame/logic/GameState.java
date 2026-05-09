package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;

import java.util.ArrayList;
import java.util.List;

public final class GameState {

    private final Level level;
    private final Snake snake;
    private final List<Pin> pins;
    private final Status status;
    private final Direction pendingDirection;

    public GameState(Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
        this.level = level;
        this.snake = snake;
        this.pins = pins;
        this.status = status;
        this.pendingDirection = pendingDirection;
    }

    public Level level() {
        return this.level;
    }

    public Snake snake() {
        return this.snake;
    }

    public List<Pin> pins() {
        return this.pins;
    }

    public Status status() {
        return this.status;
    }

    public Direction pendingDirection() {
        return this.pendingDirection;
    }

    public Boolean hasWon(List<Pin> pins) {
        return pins.stream().allMatch(Pin::isSet);
    }

    public Pin getPinAt(Position pos) {
        return pins.stream().filter(it -> it.position().equals(pos)).findFirst().orElse(null);
    }

    public GameState tick() {
        if (status != Status.RUNNING) return this;
        if (pendingDirection == Direction.NONE) return this;

        // habe ich hinzugefügt, damit mein Test funktioniert.
        if(hasWon(pins))
            return new GameState(
                level,
                snake,
                pins,
                Status.WON,
                Direction.NONE
            );

        var newSnake = snake.grow(pendingDirection);

        if(!level.isInside(newSnake.head())) {
            return new GameState(
                level,
                newSnake,
                pins,
                Status.LOST_OUT_OF_BOUNDS,
                Direction.NONE
            );
        }

        var cellType = level.cellAt(newSnake.head());

        if (cellType == CellType.WALL) {
            return new GameState(
                level,
                snake,
                pins,
                status,
                Direction.NONE
            );
        }

        if(snake.occupies(newSnake.head())) {
            return new GameState(
                level,
                newSnake,
                pins,
                Status.LOST_SELF_COLLISION,
                Direction.NONE
            );
        }

        if(cellType == CellType.PIN_SLOT) {
            var newPinList = new ArrayList<Pin>();

            for(var pin : pins()) {
                if(pin.position().equals(newSnake.head())
                && pin.activationDirection() == pendingDirection) {
                    newPinList.add(pin.withState(Pin.State.HIGH));
                } else {
                    newPinList.add(pin);
                }
            }

            return new GameState(
                level,
                snake,
                newPinList.stream().filter(f -> {
                    if(f.isAPin()) return true; else return false;
                }).toList(),
                hasWon(newPinList) ? Status.WON : Status.RUNNING,
                Direction.NONE
            );
        }

        return new GameState(level, newSnake, pins, status, pendingDirection);
    }

    public enum Status {
        RUNNING,
        WON,
        LOST_SELF_COLLISION,
        LOST_OUT_OF_BOUNDS;

        public boolean isRunning() {
            return this == RUNNING;
        }
    }
}
