package edu.dirla.app.domainModel.entities;/* Licensed under the Apache License, Version 2.0 (the "License");
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


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
public class RoleEntity {

  @Id
  @Column(name="id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "roleName")
  private String roleName;

  @Column(name = "description")
  private String description;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name="roles_rights",
          joinColumns={@JoinColumn(name="roleId", referencedColumnName="id")},
          inverseJoinColumns={@JoinColumn(name="rightId", referencedColumnName="id")})
  private List<UserRightEntity> rights;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }

  public String getRoleName() {
    return roleName;
  }
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public List<UserRightEntity> getRights() {
    return rights;
  }
  public void setRights(List<UserRightEntity> rights) {
    this.rights = rights;
  }
}