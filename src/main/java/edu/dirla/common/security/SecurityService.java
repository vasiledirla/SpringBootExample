package edu.dirla.common.security;
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

import com.google.common.base.Optional;
import edu.dirla.app.domainModel.entities.ArticleEntity;
import edu.dirla.app.domainModel.entities.UserEntity;
import edu.dirla.app.domainModel.repositories.ArticleRepository;
import edu.dirla.app.domainModel.services.UserService;
import edu.dirla.app.ws.rest.common.RestEntityMapper;
import edu.dirla.common.UserRep;
import edu.dirla.common.security.session.SessionService;
import edu.dirla.common.security.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Collection;

@Service
public class SecurityService {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RestEntityMapper entityMapper;

    @Autowired
    private ArticleRepository articleRepository;

    public Boolean checkUserAuthorization(Long doAsUserId, Collection<String> authorizedRoles) {
        String userRole = userService.getUserRole(doAsUserId);
        return userRole != null && (userRole.equalsIgnoreCase(Role.ADMIN.getRoleName()) || authorizedRoles.contains(userRole.toLowerCase()));
    }

    public Optional<UserSession> login(String userName, String password, Boolean keepSession) {
        Optional<UserEntity> userEntityOptional = userService.findUserByNameAndPassword(userName, encryptPassword(password));
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();

            UserRep userRep = entityMapper.map(userEntity, UserRep.class);
            userRep.setFirstName(userEntity.getAuthor().getFirstName());
            userRep.setLastName(userEntity.getAuthor().getLastName());

            return sessionService.createOrExtendSession(userRep, keepSession);
        } else {
            return Optional.absent();
        }
    }

    public String encryptPassword(String password) {
        //to use an encription algorithm for password (maybe MD5 ? or a stronger one?)
        return password;
    }

    public boolean checkSession(String sessionId) {
        return sessionService.findSessionByToken(sessionId).isPresent();
    }

    public Optional<UserSession> getSession(String sessionId) {
        return sessionService.findSessionByToken(sessionId);
    }

    public void logout(String sessionId) {
        Optional<UserSession> userSessionOptional = sessionService.findSessionByToken(sessionId);
        if (userSessionOptional.isPresent()) {
            sessionService.deleteSession(sessionId);
        }
    }

    public Boolean checkPermission(UserEntity userEntity, ArticleEntity articleEntity, UserPermissions permissionToCheck) {
        if (articleEntity != null) {
            if (UserPermissions.EDIT_ARTICLE.equals(permissionToCheck)) {
                return articleEntity.getAuthors().contains(userEntity.getAuthor());
            } else if (UserPermissions.DELETE_ARTICLE.equals(permissionToCheck)) {
                return articleEntity.getAuthors().contains(userEntity.getAuthor());
            }
        }
        return false;
    }

}
