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
import ru.mail.park.game.mechanics.Square;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.game.messaging.ServerSnap;
import ru.mail.park.model.UserProfile;
import ru.mail.park.model.exception.UserAlreadyExistsException;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.*;
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

    @Test
    public void test() throws IOException {
        gameMechService.addPlayer(first);
        gameMechService.addPlayer(second);
        final int[][] initialMatrix = new Square().getMatrix();
        testServerSnap(first, second, messages.poll(), initialMatrix, initialMatrix);
        testServerSnap(second, first, messages.poll(), initialMatrix, initialMatrix);
        final PlayerAction playerAction = new PlayerAction();
        playerAction.setRow(1);
        playerAction.setCol(2);
        playerAction.setPositive(true);
        gameMechService.addPlayerAction(first, playerAction);
        final Square square = new Square();
        square.activate(1, 2, 2, 1);
        testServerSnap(first, second, messages.poll(), square.getMatrix(), initialMatrix);
        testServerSnap(second, first, messages.poll(), initialMatrix, square.getMatrix());
    }

    private void testServerSnap(UserProfile player, UserProfile opponent, Message message, int[][] playerMatrix,
                                int[][] opponentMatrix) {
        assertEquals(ServerSnap.class.getSimpleName(), message.getType());
        final JSONObject content = new JSONObject(message.getContent());
        assertEquals(player.getLogin(), content.getString("player"));
        assertEquals(opponent.getLogin(), content.getString("opponent"));
        assertEquals(new JSONArray(playerMatrix).toString(), content.getJSONArray("playerMatrix").toString());
        assertEquals(new JSONArray(opponentMatrix).toString(), content.getJSONArray("opponentMatrix").toString());
        assertNotEquals(new JSONArray(playerMatrix).toString(), content.getJSONArray("target").toString());
        assertFalse(content.getBoolean("gameOver"));
        assertFalse(content.getBoolean("win"));
    }
}
