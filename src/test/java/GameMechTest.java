import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.game.mechanics.PlayerActionService;
import ru.mail.park.game.mechanics.Square;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.model.UserProfile;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class GameMechTest {
    @Autowired
    private PlayerActionService playerActionService;

    @Test
    public void test() {
        final UserProfile first = new UserProfile("a", "b", "c");
        final UserProfile second = new UserProfile("q", "w", "e");
        final GameSession session = new GameSession(new Player(first), new Player(second));
        final PlayerAction action = new PlayerAction();
        action.setRow(1);
        action.setCol(2);
        action.setPositive(true);
        playerActionService.add(first, action);
        playerActionService.processActionsForSession(session);
        final Square square = new Square();
        square.activate(1, 2, 2, 1);
        assertEquals(square, session.getFirst().getSquare());
    }
}
