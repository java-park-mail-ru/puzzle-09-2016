import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.main.ResponseCode;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountServiceImpl;
import ru.mail.park.services.DataBaseService;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "MagicNumber"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private DataBaseService dataBaseService;

    @Before
    public void init() {
        dataBaseService.getJdbcTemplate().execute("TRUNCATE user_profile;");
        final UserProfile a = new UserProfile("a", "b", "c");
        final UserProfile q = new UserProfile("q", "w", "e");
        final UserProfile s = new UserProfile("s", "ss", "sss");
        accountService.addUser(a.getLogin(), a.getPassword(), a.getEmail());
        accountService.addUser(q.getLogin(), q.getPassword(), q.getEmail());
        accountService.addUser(s.getLogin(), s.getPassword(), s.getEmail());
        a.setRank(1100);
        q.setRank(2222);
        s.setRank(500);
        accountService.updateUser(a);
        accountService.updateUser(q);
        accountService.updateUser(s);
    }

    @Test
    public void testTop() {
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/user/top/", String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
        final JSONObject response = new JSONObject(responseEntity.getBody());
        assertEquals(ResponseCode.OK.getCode(), response.getInt("code"));
        final JSONArray array = response.getJSONArray("content");
        assertEquals(3, array.length());
        assertEquals("q", array.getJSONObject(0).get("login"));
        assertEquals("a", array.getJSONObject(1).get("login"));
        assertEquals("s", array.getJSONObject(2).get("login"));
    }
}
