package edu.dirla.app.ws.rest.services;/* Licensed under the Apache License, Version 2.0 (the "License");
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

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import edu.dirla.app.ws.rest.common.AbstractRestService;
import edu.dirla.app.ws.rest.common.RestEntityMapper;
import edu.dirla.common.exceptions.NotAuthorizedException;
import edu.dirla.common.exceptions.ResourceNotFoundException;
import edu.dirla.common.security.LoginRep;
import edu.dirla.common.security.SecurityService;
import edu.dirla.common.security.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(value = "Session", description = "Session management") // Swagger annotation
@RestController
@RequestMapping(value = "/rest/session",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class SessionRestService extends AbstractRestService {

    @Autowired
    RestEntityMapper entityMapper;

    @Autowired
    private SecurityService securityService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public UserSession login(@RequestBody LoginRep user) {
        Optional<UserSession> userSessionOptional = securityService.login(user.getUserName(), user.getPassword(), user.getKeepLogin());
        if (userSessionOptional.isPresent()) {
            return userSessionOptional.get();
        } else {
            throw new NotAuthorizedException();
        }
    }

/*  @ResponseBody
  @RequestMapping(method = RequestMethod.GET,
          consumes = { MediaType.ALL_VALUE })
  public Collection<UserSession> listActiveSessions() {
    Collection<UserSession> sessionsCollection = securityService.listSessions();
    return sessionsCollection;
  }*/

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE})
    public UserSession getCurrentSession(@ApiParam(value = "DRL-AUTH", required = true)
                                         @RequestHeader("DRL-AUTH") String sessionId) {
        Optional<UserSession> userSessionOptional = securityService.getSession(sessionId);
        if (userSessionOptional.isPresent()) {
            return userSessionOptional.get();
        } else {
            throw new ResourceNotFoundException("Invalid token");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE,
            consumes = {MediaType.ALL_VALUE})
    public boolean logout(
            @ApiParam(value = "DRL-AUTH", required = true)
            @RequestHeader("DRL-AUTH") String sessionId) {
        try {
            if (securityService.checkSession(sessionId)) {
                securityService.logout(sessionId);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}