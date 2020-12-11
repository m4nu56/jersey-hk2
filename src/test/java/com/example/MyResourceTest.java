package com.example;

import com.example.business.User;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MyResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() {
        server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testWhoAmI() {
		String token = MockAuthorizationJWT.mockAuthorizationJWT("m4nu56", "dev10");
		System.out.println(token);
		User user = target
                .path("myresource")
                .request()
                .header("Authorization", token)
                .get(User.class);
        assertEquals("m4nu56", user.getLogin());
    }

    @Test
    public void testGetUsers() {
        List<User> users = target.path("myresource/users").request().get(new GenericType<List<User>>() {});
        assertEquals(2, users.size());
    }
}
