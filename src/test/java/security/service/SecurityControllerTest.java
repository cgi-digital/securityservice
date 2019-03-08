package security.service;

import com.github.seratch.jslack.Slack;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({PayloadHandler.class})
public class SecurityControllerTest {

    @Rule
    final EnvironmentVariables variables = new EnvironmentVariables();

    private SecurityController controller;
    private Slack slack;

    @Before
    public void setup() {
        controller= new SecurityController();
        slack = Slack.getInstance();
    }

    @Test
    public void testDoAuditUserNotAdmin() {
        String channelId = "test";
        String responseUrl = "http://test/response/url";
        String text = "";
        String userId = "12345";
        String username = "test.test";

        PowerMockito.mockStatic(PayloadHandler.class);
        PowerMockito.doNothing().when(PayloadHandler.class);

        HttpResponse actual = controller.doAudit(channelId, responseUrl, text, userId, username);
        assertThat(actual.code()).isEqualTo(HttpStatus.OK.getCode());
    }

    @Test
    public void testDoAuditUserIsAdmin() {
        String channelId = "test";
        String responseUrl = "http://test/response/url";
        String text = "";
        String userId = "12345";
        String username = "test.test";
        variables.set("ADMIN_USERS", "test.test");

        PowerMockito.mockStatic(PayloadHandler.class);
        PowerMockito.doNothing().when(PayloadHandler.class);


        HttpResponse actual = controller.doAudit(channelId, responseUrl, text, userId, username);
        assertThat(actual.code()).isEqualTo(HttpStatus.OK.getCode());
    }
}
