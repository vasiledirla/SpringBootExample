package edu.dirla.app.ws.rest.common;
/* Licensed under the Apache License, Version 2.0 (the "License");
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
import com.google.common.collect.Collections2;
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.entities.UserEntity;
import edu.dirla.common.exceptions.NotAuthorizedException;
import edu.dirla.app.domainModel.services.UserService;
import edu.dirla.common.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class AuthorizedUserResolver implements HandlerMethodArgumentResolver {

  @Autowired
  private UserService userService;

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterAnnotation(RequiredRoles.class) != null;
  }
  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
          WebDataBinderFactory webDataBinderFactory) throws Exception {

    // Get the annotation
    RequiredRoles userAnnotation = methodParameter.getParameterAnnotation(RequiredRoles.class);
    String paramValue = null;
    Role[] requiredRoles = userAnnotation.value();
    List<Role> requiredRoleList = Arrays.asList(requiredRoles);

    Collection<String> roleNames = Collections2.transform(requiredRoleList, new Function<Role, String>() {

              @Override
              public String apply(Role role) {
                return role.getRoleName();
              }
            });

    String paramName = userAnnotation.name();
    HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
    paramValue = request.getParameter(paramName);
    try {
      Long requestedUserId = Long.parseLong(paramValue);
      AuthorEntity authorEntity = userService.findById(requestedUserId);
      if (authorEntity != null) {
        UserEntity userEntity = authorEntity.getUser();
        if (roleNames.contains(userEntity.getUserRole().getRoleName().toLowerCase())) {
          return userEntity;
        }
      }
    }catch (Exception ex){

    }
    throw new NotAuthorizedException(paramValue, requiredRoles);
  }
}
