import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.Application;
import ru.mail.park.services.SecurityService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class SecurityServiceTest {
    @Autowired
    private SecurityService securityService;

    @Test
    public void test() {
        final String password = "my password";
        final String hash = securityService.encode(password);
        assertTrue(securityService.matches(password, hash));
        assertFalse(securityService.matches("not my password", hash));
    }
}
