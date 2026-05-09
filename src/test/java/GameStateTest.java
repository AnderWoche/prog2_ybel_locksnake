import de.hsbi.lockgame.io.LevelLoader;
import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GameStateTest {


    private Level level;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        level = LevelLoader.loadLevelFromString("""
            ####################
            #..................#
            #..####........>...#
            #..#..#........#...#
            #..#..#.v......>..S#
            #..####........#...#
            #..................#
            ####################
            """);

        gameState = new GameState(
            level,
            new Snake(new ArrayList<>(List.of(level.snakeStart()))),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );
    }

    @Test
    void getPinAt_gettingCorrectPin_returnCorrectPin() {
        var pin = gameState.getPinAt(new Position(15, 4));

        Assertions.assertEquals(Pin.State.LOW, pin.state());
        Assertions.assertEquals(new Position(15, 4), pin.position());
    }

    @Test
    void getPinAt_gettingWrongPin_returnNull() {
        var pin = gameState.getPinAt(new Position(1, 1));

        Assertions.assertNull(pin);
    }


    @Test
    void hasWon_allPinsHigh_returnsTrue() {
        var pins = gameState.pins().stream()
            .map(it -> it.withState(Pin.State.HIGH)).toList();

        Assertions.assertTrue(gameState.hasWon(pins));
    }

    @Test
    void tick_withLegalDirection_returnsBiggerSnake() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        var head = gameState.snake().head();
        var expectedPosition = new Position(head.x() - 1, head.y());

        var newGameState = gameState.tick();

        Assertions.assertEquals(2, newGameState.snake().body().size());
        Assertions.assertEquals(expectedPosition, newGameState.snake().head());
    }

    @Test
    void tick_withPinBlockingDirection_returnsSameSnake() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        var head = gameState.snake().head();
        var expectedPosition = new Position(head.x() - 2, head.y());

        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = gameState.tick();

        Assertions.assertEquals(3, gameState.snake().body().size());
        Assertions.assertEquals(expectedPosition, gameState.snake().head());
    }

    @Test
    void tick_runningBeforAWallButNotInto_returnsPinStateLow() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        var head = gameState.snake().head();
        var expectedPosition = new Position(head.x() - 2, head.y());

        gameState = gameState.tick();
        gameState = gameState.tick();

        var newHead = gameState.snake().head();
        Assertions.assertEquals(3, gameState.snake().body().size());
        Assertions.assertEquals(expectedPosition, gameState.snake().head());
        Assertions.assertEquals(Direction.LEFT, gameState.pendingDirection());
        Assertions.assertEquals(Pin.State.LOW, gameState.getPinAt(new Position(newHead.x() - 1, newHead.y())).state());
    }

    @Test
    void tick_withPinBlockingDirection_returnsPinStateHigh() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        var head = gameState.snake().head();
        var expectedPosition = new Position(head.x() - 2, head.y());

        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = gameState.tick();

        var newHead = gameState.snake().head();
        Assertions.assertEquals(3, gameState.snake().body().size());
        Assertions.assertEquals(expectedPosition, gameState.snake().head());
        Assertions.assertEquals(Direction.NONE, gameState.pendingDirection());
        Assertions.assertEquals(Pin.State.HIGH, gameState.getPinAt(new Position(newHead.x() - 1, newHead.y())).state());
    }

    @Test
    void tick_withSelfCollision_returnsLOST_SELF_COLLISION() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        var expectedPosition = gameState.snake().head();

        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.UP
        );
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.RIGHT
        );
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.DOWN
        );
        gameState = gameState.tick();

        Assertions.assertEquals(5, gameState.snake().body().size());
        Assertions.assertEquals(expectedPosition, gameState.snake().head());
        Assertions.assertEquals(Direction.NONE, gameState.pendingDirection());
    }

    @Test
    void tick_pinsAllHight_returnsWinningState() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins().stream().map(pin -> pin.withState(Pin.State.HIGH)).toList(),
            gameState.status(),
            Direction.LEFT
        );

        gameState = gameState.tick();

        Assertions.assertEquals(GameState.Status.WON, gameState.status());
        Assertions.assertEquals(1, gameState.snake().body().size());
        Assertions.assertEquals(level.snakeStart(), gameState.snake().head());
        Assertions.assertEquals(Direction.NONE, gameState.pendingDirection());
    }

    @Test
    void tick_winByGamePlay_returnsGameStateWon() {
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );

        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.UP
        );
        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.UP
        );
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        for(int i = 0; i < 7; i++) gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.DOWN
        );
        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.LEFT
        );
        gameState = gameState.tick();
        gameState = new GameState(
            level,
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            Direction.UP
        );
        gameState = gameState.tick();

        Assertions.assertEquals(GameState.Status.WON, gameState.status());
        Assertions.assertEquals(18, gameState.snake().body().size());
        Assertions.assertEquals(Direction.NONE, gameState.pendingDirection());
    }
}
