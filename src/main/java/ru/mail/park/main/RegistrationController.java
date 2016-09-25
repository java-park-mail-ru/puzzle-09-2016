package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;

@RestController
public class RegistrationController {
    private final AccountService accountService;

    @Autowired
    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody RegistrationRequest body) {
        String login = body.getLogin();
        String password = body.getPassword();
        String email = body.getEmail();
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        if (accountService.getUserByLogin(login) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        accountService.addUser(login, password, email);
        return ResponseEntity.ok(new SuccessResponse(login));
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.POST)
    public ResponseEntity auth(@RequestBody AuthRequest body, HttpSession httpSession) {
        String httpSessionId = httpSession.getId();
        UserProfile user = accountService.getUserBySessionId(httpSessionId);
        if (user != null) {
            return ResponseEntity.ok(new SuccessResponse(user.getLogin()));
        }
        String login = body.getLogin();
        String password = body.getPassword();
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        user = accountService.getUserByLogin(login);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        if (!StringUtils.isEmpty(httpSessionId)) {
            accountService.associateSessionIdWithUser(httpSession.getId(), user);
        }
        return ResponseEntity.ok(new SuccessResponse(login));
    }

    @SuppressWarnings("unused")
    private static final class SuccessResponse {
        private String login;

        private SuccessResponse(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    @SuppressWarnings("unused")
    private static final class RegistrationRequest {
        private String login;
        private String password;
        private String email;

        public RegistrationRequest() {
        }

        public RegistrationRequest(String login, String password, String email) {
            this.login = login;
            this.password = password;
            this.email = email;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @SuppressWarnings("unused")
    private static final class AuthRequest {
        private String login;
        private String password;

        public AuthRequest() {
        }

        public AuthRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}