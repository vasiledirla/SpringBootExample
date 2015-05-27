package edu.dirla.app.ws.rest.common;/* Licensed under the Apache License, Version 2.0 (the "License");
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

import edu.dirla.app.domainModel.entities.ArticleEntity;
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.entities.RoleEntity;
import edu.dirla.app.domainModel.entities.UserEntity;
import edu.dirla.app.ws.mappers.EntityMapper;
import edu.dirla.app.ws.rest.representations.articles.ArticleRep;
import edu.dirla.app.ws.rest.representations.user.AuthorRep;
import edu.dirla.common.UserRep;
import edu.dirla.common.security.Role;
import ma.glasnost.orika.MapperFactory;
import org.springframework.stereotype.Component;

@Component
public class RestEntityMapper extends EntityMapper {

  @Override
  public void configure(MapperFactory mapperFactory) {

 mapperFactory.registerClassMap(mapperFactory.classMap(UserEntity.class, UserRep.class)
            .field("userRole.roleName", "role")
            .byDefault().toClassMap());

    mapperFactory.registerClassMap(mapperFactory.classMap(ArticleEntity.class, ArticleRep.class)
            .byDefault().toClassMap());

    mapperFactory.registerClassMap(mapperFactory.classMap(AuthorEntity.class, AuthorRep.class)
            .byDefault().toClassMap());

    //rest mapper
    mapperFactory.registerClassMap(mapperFactory.classMap(AuthorEntity.class, UserRep.class)
            .field("user.userName", "userName")
            .field("user.userRole.roleName", "role")
            .byDefault().toClassMap());
  }

}
