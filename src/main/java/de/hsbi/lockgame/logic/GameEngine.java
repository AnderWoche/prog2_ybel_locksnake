package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import de.hsbi.lockgame.ui.GamePanel;

import java.util.ArrayList;
import java.util.List;

public final class GameEngine {

    private Level level;
    private GamePanel panel;

    private GameState state;

    public GameEngine(Level level) {
        this.level = level;

        state = new GameState(
            level,
            new Snake(new ArrayList<>(List.of(level.snakeStart()))),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );
    }

    public GameState state() {
        return this.state;
    }

    public void setGamePanel(GamePanel panel) {
        this.panel = panel;
    }

    public void update(Direction d) {
        state = new GameState(level, state.snake(), state.pins(), state.status(), d);
        panel.update(state);
    }

    public void tick() {
        var newState = state.tick();
        if(newState != this.state) {
            this.state = newState;
            panel.update(state);
        }
    }
}
