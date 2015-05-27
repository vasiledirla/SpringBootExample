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

@Entity
@Table(name = "user")
@org.hibernate.annotations.GenericGenerator(name="author-primaryKey", strategy="foreign",
        parameters={@org.hibernate.annotations.Parameter(name="property", value="author")})
public class UserEntity {

  @Id
  @GeneratedValue(generator = "author-primaryKey")
  private long id;

  @Column(name = "userName" , unique = true)
  private String userName;

  @Column(name = "password")
  private String password;

  @OneToOne(fetch=FetchType.LAZY) //one author for a user // The author generates the PK and is shared to user
  @PrimaryKeyJoinColumn
  private AuthorEntity author;

  @ManyToOne(fetch=FetchType.EAGER) //many users for a role
  @JoinColumn(name="roleId", insertable = false, updatable = false)
  private RoleEntity userRole;


  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public RoleEntity getUserRole() {
    return userRole;
  }
  public void setUserRole(RoleEntity userRole) {
    this.userRole = userRole;
  }

  public AuthorEntity getAuthor() {
    return author;
  }
  public void setAuthor(AuthorEntity author) {
    this.author = author;
  }
}