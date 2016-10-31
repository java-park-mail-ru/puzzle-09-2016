package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.services.AccountService;

@CrossOrigin
@RestController
public class UserController {
    private final AccountService accountService;

    @Autowired
    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user/top", method = RequestMethod.GET)
    public ResponseEntity top(@RequestParam(required = false) Integer limit) {
        if (limit == null) {
            limit = 0;
        }
        return ApiResponse.ok(accountService.getTopRanked(limit).stream().map(
                userProfile -> new LoginAndRank(userProfile.getLogin(), userProfile.getRank())).toArray());

    }

    @SuppressWarnings("unused")
    private static final class LoginAndRank {
        private String login;
        private int rank;

        private LoginAndRank() {
        }

        private LoginAndRank(String login, int rank) {
            this.login = login;
            this.rank = rank;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }
}
