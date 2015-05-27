package edu.dirla.app.domainModel.services;
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
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.entities.RoleEntity;
import edu.dirla.app.domainModel.entities.UserEntity;
import edu.dirla.app.domainModel.repositories.AuthorRepository;
import edu.dirla.app.domainModel.repositories.RoleRepository;
import edu.dirla.app.domainModel.repositories.UserRepository;
import edu.dirla.common.exceptions.ResourceNotFoundException;
import edu.dirla.common.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public AuthorEntity addUser(String userName, String firstName, String lastName, Role role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);

        List<RoleEntity> roleEntities = roleRepository.findByName(role.getRoleName());
        if (roleEntities == null || roleEntities.size() == 0) {
            throw new ResourceNotFoundException("No role associated with name = " + role.getRoleName());
        }
        userEntity.setUserRole(roleEntities.get(0));

        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setFirstName(firstName);
        authorEntity.setLastName(lastName);
        authorEntity.setUser(userEntity);
        userEntity.setAuthor(authorEntity);

        authorRepository.save(authorEntity);

        return authorEntity;
    }

    public List<AuthorEntity> findByName(String name) {
        return authorRepository.findByName(name);
    }

    public void deleteUserById(Long userId) {
        authorRepository.delete(userId);
    }

    public AuthorEntity findById(Long userId) {
        return authorRepository.findOne(userId);
    }

    public void updateUser(Long userId) {

    }

    public String getUserRole(Long userId) {
        AuthorEntity authorEntity = findById(userId);
        if (authorEntity != null) {
            return authorEntity.getUser().getUserRole().getRoleName();
        }
        return null;
    }

    public Optional<UserEntity> findUserByNameAndPassword(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password);
    }

    public List<AuthorEntity> findAll() {
        return authorRepository.findAll();
    }
}
