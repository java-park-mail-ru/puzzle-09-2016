package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.model.exception.UserAlreadyExistsException;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;

@CrossOrigin
@RestController
public class RegistrationController {
    private final AccountService accountService;

    @Autowired
    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody RegistrationRequest body) {
        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            return ApiResponse.parameterMissing();
        }
        try {
            accountService.addUser(login, password, email);
        } catch (UserAlreadyExistsException e) {
            return ApiResponse.duplicateUser();
        }
        return ApiResponse.ok(new SuccessResponse(login));
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.POST)
    public ResponseEntity auth(@RequestBody AuthRequest body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return ApiResponse.parameterMissing();
        }
        final UserProfile user = accountService.getUserByLogin(login);
        if (user == null || !user.getPassword().equals(password)) {
            return ApiResponse.authError();
        }
        httpSession.setAttribute("login", login);
        return ApiResponse.ok(new SuccessResponse(login));
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.GET)
    public ResponseEntity sessionAuth(HttpSession httpSession) {
        final Object httpSessionLogin = httpSession.getAttribute("login");
        if (httpSessionLogin == null) {
            return ApiResponse.authError();
        }
        final UserProfile user = accountService.getUserByLogin(httpSessionLogin.toString());
        if (user == null) {
            return ApiResponse.authError();
        }
        return ApiResponse.ok(new SuccessResponse(user.getLogin()));
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpSession httpSession) {
        final Object httpSessionLogin = httpSession.getAttribute("login");
        if (httpSessionLogin == null) {
            return ApiResponse.authError();
        }
        httpSession.removeAttribute("login");
        return ApiResponse.ok(new SuccessResponse((String) httpSessionLogin));
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

        private RegistrationRequest() {
        }

        private RegistrationRequest(String login, String password, String email) {
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

        private AuthRequest() {
        }

        private AuthRequest(String login, String password) {
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