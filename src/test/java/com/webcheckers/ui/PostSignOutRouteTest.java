package com.webcheckers.ui;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import spark.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.webcheckers.ui.PostSignOutRoute;
import com.webcheckers.ui.PostSignInRoute;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The unit test suite for the {@link PostSignOutRoute} component.
 *
 * @author Neha Ghanta, ng8975@rit.edu
 */
@Tag("UI-tier")
class PostSignOutRouteTest {
    /**
     * The component under test.
     */
    private PostSignOutRoute CuT;

    /**
     * The {@link PlayerLobby} (friendly entity) that the CuT depends on.
     */
    private PlayerLobby playerLobby;

    private static final String PLAYER_NAME = "Player";

    private Request request;
    private Session session;
    private TemplateEngine engine;
    private Response response;
    private Player testPlayer;

    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);


        // set up test player
        testPlayer = new Player(PLAYER_NAME, session);

        // set up the CuT dependencies
        playerLobby = new PlayerLobby(new HashMap<>());
        engine = mock(TemplateEngine.class);

        // sign-in the player
        playerLobby.signIn(PLAYER_NAME, testPlayer);
        when(session.attribute("currentUser")).thenReturn(testPlayer);

        CuT = new PostSignOutRoute(playerLobby);
    }

    /**
     * Test that the Sign-Out handle method will render the Home Page
     */
    @Test
    public void signOut(){

        // Invoke the test
        CuT.handle(request, response);

        //  * test view name
        verify(response).redirect(WebServer.HOME_URL);

    }

}