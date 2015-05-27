package edu.dirla.app.domainModel.repositories;/* Licensed under the Apache License, Version 2.0 (the "License");
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
import edu.dirla.app.domainModel.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT user FROM UserEntity user WHERE LOWER(user.userName) like %:name%")
  public List<UserEntity> findByUserName(@Param("name") String name);

  @Query("SELECT user FROM UserEntity user WHERE LOWER(user.userName) = :name and password = :password")
  public Optional<UserEntity> findByUserNameAndPassword(@Param("name") String name, @Param("password") String password);

}
