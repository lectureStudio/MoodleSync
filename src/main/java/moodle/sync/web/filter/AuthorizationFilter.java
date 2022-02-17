package moodle.sync.web.filter;

import static java.util.Objects.nonNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * Implements an authorization filter which adds the token
 *
 * @author Daniel Schröter
 */
public class AuthorizationFilter implements ClientRequestFilter {

    private static final String BEARER = "Bearer ";
    private static String wstoken = "31a1f216fd60a5be89c6a25debe82505";

    /**
     * Method to set the Token
     * @param Token
     */
    public static void setToken(String Token) {
        wstoken = Token;
    }

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (nonNull(wstoken)) {
            requestContext.getHeaders().putSingle(AUTHORIZATION, BEARER + wstoken);
        }
    }

}