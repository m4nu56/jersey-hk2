package com.example.security;

import com.example.business.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import java.util.HashSet;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class PreMatchingCurrentUserFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            Jws<Claims> jws = new AuthorizationValidator(false).validate(requestContext);
            AppSecurityContext appSecurityContext = new AppSecurityContext(
                    new HashSet<String>(),
                    new User(
                            (String) jws.getBody().get("login"),
                            (String) jws.getBody().get("compte")
                    ),
                    true
            );
            requestContext.setSecurityContext(appSecurityContext);
        }
        catch (Exception ignored) {
        }
    }
}
