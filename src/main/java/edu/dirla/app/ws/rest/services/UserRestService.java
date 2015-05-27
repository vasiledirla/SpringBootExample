package edu.dirla.app.ws.rest.services;
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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.services.UserService;
import edu.dirla.app.ws.rest.common.AbstractRestService;
import edu.dirla.app.ws.rest.common.RestEntityMapper;
import edu.dirla.app.ws.rest.services.responses.AddUserResponse;
import edu.dirla.common.UserRep;
import edu.dirla.common.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Users", description = "User management") // Swagger annotation
@RestController
@RequestMapping(value = "/rest/users",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserRestService extends AbstractRestService {

    @Autowired
    RestEntityMapper entityMapper;

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public AddUserResponse addUser(@RequestBody UserRep user) {
        AuthorEntity authorEntity = userService.addUser(user.getUserName(), user.getFirstName(), user.getLastName(), user.getRole());

        UserRep userRep = entityMapper.map(authorEntity, UserRep.class);
        AddUserResponse addUserResponse = new AddUserResponse();
        addUserResponse.setUser(userRep);

        return addUserResponse;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE})
    public List<UserRep> listUsersByName(@ApiParam(value = "filter") @RequestParam(value = "filter", required = false) String name) {
        List<AuthorEntity> authors = null;
        if (name != null) {
            authors = userService.findByName(name.toLowerCase());
        } else {
            authors = userService.findAll();
        }
        return entityMapper.mapAsList(authors, UserRep.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}",
            consumes = {MediaType.ALL_VALUE})
    public boolean deleteUser(@ApiParam(value = "id", required = true) @PathVariable("id") Long userId) {
        AuthorEntity authorEntity = userService.findById(userId);
        if (authorEntity != null) {
            userService.deleteUserById(userId);
        } else {
            throw new ResourceNotFoundException(String.format("There is no user with ID=%s", userId));
        }
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.HEAD, value = "/{id}",
            consumes = {MediaType.ALL_VALUE})
    public boolean checkUser(@ApiParam(value = "id", required = true) @PathVariable("id") Long userId) {
        AuthorEntity authorEntity = userService.findById(userId);
        if (authorEntity == null)
            throw new ResourceNotFoundException(String.format("There is no user with ID=%s", userId));
        return true;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{id}",
            consumes = {MediaType.ALL_VALUE})
    public boolean putUser(@ApiParam(value = "id", required = true) @PathVariable("id") Long userId, @RequestBody UserRep user) {
        AuthorEntity authorEntity = userService.findById(userId);
        if (authorEntity != null) {
            userService.deleteUserById(userId);
        } else {
            throw new ResourceNotFoundException(String.format("There is no user with ID=%s", userId));
        }
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public boolean patchUser(@ApiParam(value = "id", required = true) @PathVariable("id") Long userId, @RequestBody UserRep user) {
        AuthorEntity authorEntity = userService.findById(userId);
        if (authorEntity != null) {
            userService.updateUser(userId);
        } else {
            throw new ResourceNotFoundException(String.format("There is no user with ID=%s", userId));
        }
        return true;
    }

}