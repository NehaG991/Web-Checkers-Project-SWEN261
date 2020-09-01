package com.webcheckers.ui;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.HashMap;

import static com.webcheckers.ui.PostSignInRoute.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The unit test suite for the {@link PostSignInRoute} component.
 *
 * @author Thomas Daley, ted5363@rit.edu
 */
@Tag("UI-tier")
public class PostSignInRouteTest {

    private PostSignInRoute CuT;
    private PlayerLobby lobby;
    private Request request;
    private Response response;
    private TemplateEngine engine;
    private String username;
    private Session session;

    @BeforeEach
    public void setUp() {
        HashMap<String, Player> playerList = new HashMap<>();
        lobby = new PlayerLobby(playerList);
        session = mock(Session.class);
        CuT = new PostSignInRoute(lobby);
        request = mock(Request.class);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);

        username = "Buttercup";

        when(request.session()).thenReturn(session);
    }

    /**
     * Test the {@link PostSignInRoute#handle(Request, Response)} method
     */
    @Test
    public void testHandle() {
        TemplateEngineTester testEngine = new TemplateEngineTester();

        //Initialize ModelView
        when(engine.render(any(ModelAndView.class))).thenAnswer(testEngine.makeAnswer());
        //Set up the handle method
        when(request.queryParams(USERNAME_PARAM)).thenReturn(username);

        //Start the Test

        //Checks if halt() is in a try and catch block
        //Runs through code for creating a new player
        try {
            CuT.handle(request, response);
        } catch (HaltException e){
            //expected
        }

    }

    @Test
    public void testUsernameAlreadyInUse() {
        // simulate a player with the username already in the lobby.
        Player buttercup = new Player(username, session);
        lobby.signIn(username, buttercup);
        when(request.queryParams(USERNAME_PARAM)).thenReturn(username);

        // invoke the test, using try/catch to handle the halt.
        try {
            CuT.handle(request, response);
        } catch (HaltException e) {
            // expected
        }

        verify(response).redirect(WebServer.HOME_URL);

        // assert that the sign-in status says that the username is already in use.
    }
}
