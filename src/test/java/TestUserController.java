import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.services.DataBaseService;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class TestUserController {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AccountService accountService;

    @Before
    public void init() {
        DataBaseService.getJdbcTemplate().execute("TRUNCATE user_profile;");
        UserProfile a = new UserProfile("a", "b", "c");
        UserProfile q = new UserProfile("q", "w", "e");
        UserProfile s = new UserProfile("s", "ss", "sss");
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
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/user/top/", String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
        JSONArray array = new JSONArray(responseEntity.getBody());
        assertEquals(3, array.length());
        assertEquals("q", array.getJSONObject(0).get("login"));
        assertEquals("a", array.getJSONObject(1).get("login"));
        assertEquals("s", array.getJSONObject(2).get("login"));
    }
}
