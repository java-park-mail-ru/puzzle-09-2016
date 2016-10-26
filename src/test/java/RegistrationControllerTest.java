import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.services.DataBaseService;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class RegistrationControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DataBaseService dataBaseService;

    @Before
    public void init() {
        dataBaseService.getJdbcTemplate().execute("TRUNCATE user_profile;");
        postUser("a", "b", "c");
    }

    @Test
    public void addNewUser() {
        final ResponseEntity<String> responseEntity = postUser("q", "w", "e");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        final JSONObject response = new JSONObject(responseEntity.getBody());
        assertEquals("q", response.get("login"));
    }

    @Test
    public void addUserWithExistingLogin() {
        final ResponseEntity responseEntity = postUser("a", "new", "new");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{}", responseEntity.getBody());
    }

    @Test
    public void addUserWithExistingEmail() {
        final ResponseEntity responseEntity = postUser("new", "new", "c");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{}", responseEntity.getBody());
    }

    @Test
    public void login() {
        final ResponseEntity<String> responseEntity = postSession("a", "b");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        final JSONObject response = new JSONObject(responseEntity.getBody());
        assertEquals("a", response.get("login"));
    }

    @Test
    public void badLogin() {
        final ResponseEntity responseEntity = postSession("a", "c");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{}", responseEntity.getBody());
    }

    @Test
    public void notExistingUser() {
        final ResponseEntity responseEntity = postSession("www", "www");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{}", responseEntity.getBody());
    }

    private ResponseEntity<String> postUser(String login, String password, String email) {
        final JSONObject request = new JSONObject();
        request.put("login", login);
        request.put("password", password);
        request.put("email", email);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        return restTemplate.postForEntity("/api/user/", entity, String.class);
    }

    private ResponseEntity<String> postSession(String login, String password) {
        final JSONObject request = new JSONObject();
        request.put("login", login);
        request.put("password", password);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        return restTemplate.postForEntity("/api/session/", entity, String.class);
    }
}
