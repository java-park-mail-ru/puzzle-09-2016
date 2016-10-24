import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.services.DataBaseService;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class TestRegistrationController {
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {
        DataBaseService.getJdbcTemplate().execute("TRUNCATE user_profile;");
        postUser("a", "b", "c");
    }

    @Test
    public void addNewUser() {
        ResponseEntity<String> responseEntity = postUser("q", "w", "e");
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        JSONObject response = new JSONObject(responseEntity.getBody());
        assertEquals(response.get("login"), "q");
    }

    @Test
    public void addUserWithExistingLogin() {
        ResponseEntity responseEntity = postUser("a", "new", "new");
        assertEquals(responseEntity.getStatusCodeValue(), 400);
        assertEquals(responseEntity.getBody(), "{}");
    }

    @Test
    public void addUserWithExistingEmail() {
        ResponseEntity responseEntity = postUser("new", "new", "c");
        assertEquals(responseEntity.getStatusCodeValue(), 400);
        assertEquals(responseEntity.getBody(), "{}");
    }

    @Test
    public void login() {
        ResponseEntity<String> responseEntity = postSession("a", "b");
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        JSONObject response = new JSONObject(responseEntity.getBody());
        assertEquals(response.get("login"), "a");
    }

    @Test
    public void badLogin() {
        ResponseEntity responseEntity = postSession("a", "c");
        assertEquals(responseEntity.getStatusCodeValue(), 400);
        assertEquals(responseEntity.getBody(), "{}");
    }

    private ResponseEntity<String> postUser(String login, String password, String email) {
        JSONObject request = new JSONObject();
        request.put("login", login);
        request.put("password", password);
        request.put("email", email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        return restTemplate.postForEntity("/api/user/", entity, String.class);
    }

    private ResponseEntity<String> postSession(String login, String password) {
        JSONObject request = new JSONObject();
        request.put("login", login);
        request.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        return restTemplate.postForEntity("/api/session/", entity, String.class);
    }
}
