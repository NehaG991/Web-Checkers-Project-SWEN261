package com.webcheckers.ui;

import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * The UI Controller to GET the Sign-in interface.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GetSignInRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetSignInRoute.class.getName());

    private final TemplateEngine templateEngine;

    /**
     * Create the Spark Route (UI Controller) to handle all {@code GET /signin} HTTP requests.
     *
     * @param templateEngine: The HTML template rendering engine
     */
    public GetSignInRoute(final TemplateEngine templateEngine) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        LOG.config("GetSignInRoute is initialized.");
    }

    /**
     * Render the sign-in interface on the Home page.
     *
     * @param request: The HTTP request.
     * @param response: The HTTP response.
     * @return the rendered HTML for the sign-in interface.
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetSignInRoute is invoked.");

        Map<String, Object> vm = new HashMap<>();
        vm.put("formType", "Sign-in");

        return templateEngine.render(new ModelAndView(vm, "signin.ftl"));
    }
}
