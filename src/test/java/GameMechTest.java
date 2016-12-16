import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.game.GameMechService;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Square;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.game.messaging.ServerSnap;
import ru.mail.park.model.UserProfile;
import ru.mail.park.model.exception.UserAlreadyExistsException;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "ConstantConditions"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class GameMechTest {
    @MockBean
    private RemotePointService remotePointService;
    @Autowired
    private GameMechService gameMechService;
    @Autowired
    private AccountService accountService;

    private UserProfile first;
    private UserProfile second;
    private Queue<Message> messages = new ArrayDeque<>();

    @Before
    public void init() throws IOException {
        when(remotePointService.isConnected(any())).thenReturn(true);
        doAnswer(invocationOnMock -> {
            final Object[] args = invocationOnMock.getArguments();
            messages.add((Message) args[1]);
            return null;
        }).when(remotePointService).sendMessageToUser(any(), any());
        try {
            accountService.addUser("TestUser-1", "TestPassword-1", "TestEmail-1");
        } catch (UserAlreadyExistsException ignore) {
        }
        try {
            accountService.addUser("TestUser-2", "TestPassword-2", "TestEmail-2");
        } catch (UserAlreadyExistsException ignore) {
        }
        first = accountService.getUserByLogin("TestUser-1");
        second = accountService.getUserByLogin("TestUser-2");
    }

    @SuppressWarnings({"OverlyBroadThrowsClause", "unchecked"})
    @Test
    public void test() throws Exception {
        gameMechService.addPlayer(first);
        gameMechService.addPlayer(second);
        final Field sessionsField = gameMechService.getClass().getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        final Map<UserProfile, GameSession> sessions = (Map<UserProfile, GameSession>) sessionsField.get(gameMechService);
        final GameSession session = sessions.get(first);
        final Field targetField = session.getClass().getDeclaredField("target");
        targetField.setAccessible(true);
        final int[][] initialMatrix = new Square().getMatrix();
        final int[][] target = ((Square) targetField.get(session)).getMatrix();
        testServerSnap(first, second, messages.poll(), initialMatrix, initialMatrix, target, false, false);
        testServerSnap(second, first, messages.poll(), initialMatrix, initialMatrix, target, false, false);
        final Square newTarget = new Square();
        newTarget.activate(1, 2, 2, 1);
        newTarget.activate(3, 4, 2, 1);
        targetField.set(session, newTarget);
        final PlayerAction playerAction = new PlayerAction();
        playerAction.setRow(1);
        playerAction.setCol(2);
        playerAction.setPositive(true);
        gameMechService.addPlayerAction(first, playerAction);
        final Square square = new Square();
        square.activate(1, 2, 2, 1);
        testServerSnap(first, second, messages.poll(), square.getMatrix(), initialMatrix, newTarget.getMatrix(),
                false, false);
        testServerSnap(second, first, messages.poll(), initialMatrix, square.getMatrix(), newTarget.getMatrix(),
                false, false);
        final PlayerAction winningAction = new PlayerAction();
        winningAction.setRow(3);
        winningAction.setCol(4);
        winningAction.setPositive(true);
        gameMechService.addPlayerAction(first, winningAction);
        testServerSnap(first, second, messages.poll(), newTarget.getMatrix(), initialMatrix, newTarget.getMatrix(),
                true, true);
        testServerSnap(second, first, messages.poll(), initialMatrix, newTarget.getMatrix(), newTarget.getMatrix(),
                true, false);
    }

    private void testServerSnap(UserProfile player, UserProfile opponent, Message message, int[][] playerMatrix,
                                int[][] opponentMatrix, int[][] target, boolean gameOver, boolean win) {
        assertEquals(ServerSnap.class.getSimpleName(), message.getType());
        final JSONObject content = new JSONObject(message.getContent());
        assertEquals(player.getLogin(), content.getString("player"));
        assertEquals(opponent.getLogin(), content.getString("opponent"));
        assertEquals(new JSONArray(playerMatrix).toString(), content.getJSONArray("playerMatrix").toString());
        assertEquals(new JSONArray(opponentMatrix).toString(), content.getJSONArray("opponentMatrix").toString());
        assertEquals(new JSONArray(target).toString(), content.getJSONArray("target").toString());
        assertEquals(gameOver, content.getBoolean("gameOver"));
        assertEquals(win, content.getBoolean("win"));
    }
}
