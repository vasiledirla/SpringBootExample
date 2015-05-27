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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import edu.dirla.app.domainModel.entities.ArticleEntity;
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.entities.UserEntity;
import edu.dirla.app.domainModel.services.ArticleService;
import edu.dirla.app.domainModel.services.UserService;
import edu.dirla.app.ws.rest.common.AbstractRestService;
import edu.dirla.app.ws.rest.common.RequiredRoles;
import edu.dirla.app.ws.rest.common.RestEntityMapper;
import edu.dirla.app.ws.rest.representations.articles.ArticleRep;
import edu.dirla.app.ws.rest.representations.articles.CreateUpdateArticleRep;
import edu.dirla.common.exceptions.NotAuthorizedException;
import edu.dirla.common.exceptions.ResourceNotFoundException;
import edu.dirla.common.security.Role;
import edu.dirla.common.security.SecurityService;
import edu.dirla.common.security.UserPermissions;
import edu.dirla.common.security.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Api(value = "Article X", description = "Article management") // Swagger annotation
@RestController
@RequestMapping(value = "/rest/articles",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class ArticlesRestService extends AbstractRestService {

    @Autowired
    RestEntityMapper entityMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SecurityService securityService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @RequiredRoles({Role.AUTHOR})
    public Boolean addArticle(
            @RequestHeader("DRL-AUTH") String sessionId,
            @ApiParam(value = "doAsUserId", name = "doAsUserId", required = false)
            @RequestParam(value = "doAsUserId", required = false) UserEntity doAsUser,
            @RequestBody CreateUpdateArticleRep articleRep) {

        Optional<UserSession> userSessionOptional = securityService.getSession(sessionId);
        if (!userSessionOptional.isPresent()) {
            throw new NotAuthorizedException("this sessiontoken is not valid");
        }

        Date creationDate = Calendar.getInstance().getTime();
        Collection<Long> authorIds = Lists.newArrayList();
        if (doAsUser != null) {
            authorIds.remove(doAsUser.getId());
            authorIds.add(doAsUser.getId());
        } else {
            authorIds.add(userSessionOptional.get().getUser().getId());
        }
        articleService.addArticle(articleRep.getTitle(), articleRep.getDescription(), articleRep.getContent(), creationDate, authorIds);
        return true;
    }



    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}",
            consumes = {MediaType.ALL_VALUE})
    public boolean deleteArticle(@ApiParam(value = "id", required = true) @PathVariable("id") Long articleId) {
        ArticleEntity articleEntity = articleService.findById(articleId);
        if (articleEntity != null) {
            articleService.deleteById(articleId);
        } else {
            throw new ResourceNotFoundException(String.format("There is no article with ID=%s", articleId));
        }
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE})
    public List<ArticleRep> listArticlesByName(@ApiParam(value = "filter") @RequestParam(value = "filter", required = false) String name) {
        List<ArticleEntity> articles = null;
        if (name != null) {
            articles = articleService.findByTitle(name.toLowerCase());
        } else {
            articles = articleService.findAll();
        }
        return entityMapper.mapAsList(articles, ArticleRep.class);
    }



    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{articleId}/")
    @RequiredRoles({Role.AUTHOR})
    public Boolean updateArticle(@ApiParam(value = "doAsUserId", name = "doAsUserId", required = true) @RequestParam("doAsUserId") UserEntity userEntity,
                                 @ApiParam(value = "articleId", name = "articleId", required = true) @PathVariable("articleId") ArticleEntity articleEntity,
                                 @RequestBody CreateUpdateArticleRep articleRep) {
        if (articleEntity != null) {

            Boolean isAllowed = securityService.checkPermission(userEntity, articleEntity, UserPermissions.EDIT_ARTICLE);
            if (isAllowed) {
//                articleRep.getAuthorIds().remove(userEntity.getId());
//                articleRep.getAuthorIds().add(userEntity.getId());

//                articleService.updateArticle(articleEntity, articleRep.getTitle(), articleRep.getYear(), articleRep.getAuthorIds());
                return true;
            } else {
                throw new NotAuthorizedException(String.format("You are not allowed to update the article '%s'.", articleRep.getTitle()));
            }
        } else {
            throw new ResourceNotFoundException(String.format("There is no such article in the system"));
        }
    }


}