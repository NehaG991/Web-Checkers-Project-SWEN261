package com.webcheckers.ui;

import com.webcheckers.model.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The unit test suite for the {@link GetSpectatorStopWatchingRouteTest} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class GetSpectatorStopWatchingRouteTest {

    private GetSpectatorStopWatchingRoute CuT;

    private Request request;
    private Response response;
    private Session session;

    /**
     * Setup mock Spark objects, and map the return type when the session is
     * called for.
     */
    @BeforeEach
    public void setup() {
        this.request = mock(Request.class);
        this.response = mock(Response.class);
        this.session = mock(Session.class);
        when(request.session()).thenReturn(session);

        CuT = new GetSpectatorStopWatchingRoute();
    }

    /**
     * Make sure that we are redirected to the home page, and that the active
     * color session attribute is removed from the session.
     */
    @Test
    public void verify_redirect() {
        session.attribute(ACTIVE_COLOR, Color.RED);

        try {
            CuT.handle(request, response);
            fail("redirect invokes halt exception");
        } catch (HaltException e) {
            // expected.
        }
        // verify that we get redirected to the home page.
        verify(response).redirect(WebServer.HOME_URL);
        // verify that the session attribute was removed.
        assertNull(session.attribute(ACTIVE_COLOR));
    }
}
