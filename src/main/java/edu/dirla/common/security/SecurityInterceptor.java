package edu.dirla.common.security;/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import edu.dirla.app.ws.rest.common.RequiredRoles;
import edu.dirla.common.UserRep;
import edu.dirla.common.exceptions.NotAuthorizedException;
import edu.dirla.common.security.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SecurityService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // Get the annotation
        RequiredRoles userAnnotation = ((HandlerMethod) handler).getMethodAnnotation(RequiredRoles.class);
        if (userAnnotation == null)
            return true;

        String authToken = request.getHeader("DRL-AUTH");

        Optional<UserSession> userSessionOptional = securityService.getSession(authToken);

        if (!userSessionOptional.isPresent()) {
            throw new NotAuthorizedException("Invalid token provided");
        }

        String paramValue = null;
        Role[] requiredRoles = userAnnotation.value();
        List<Role> requiredRoleList = Arrays.asList(requiredRoles);

        Collection<String> roleNames = Collections2.transform(requiredRoleList, new Function<Role, String>() {

            @Override
            public String apply(Role role) {
                return role.getRoleName();
            }
        });

        //if I'm an admin I could execute the action in hehalf of another user.
        UserSession userSession = userSessionOptional.get();
        UserRep currentUser = userSession.getUser();
        if (currentUser.getRole().equals(Role.ADMIN)) {
            //check the doAsUserId parameter on request
            String paramName = userAnnotation.name();
            paramValue = request.getParameter(paramName);
            if (paramValue != null) {
                try {
                    Long requestedUserId = Long.parseLong(paramValue);
                    if (securityService.checkUserAuthorization(requestedUserId, roleNames)) {
                        return true;
                    }
                } catch (Exception ex) {
                    throw new NotAuthorizedException("Invalid parameter value for: doAsUserId");
                }
            }
        }

        //check if the current logged in user have access to do the action
        if (securityService.checkUserAuthorization(currentUser.getId(), roleNames)) {
            return true;
        }


        throw new NotAuthorizedException();
    }

}
