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
@Table(name = "author")
public class AuthorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "author")
  private UserEntity user;

  @Column(name = "lastName")
  String lastName;

  @Column(name = "firstName")
  String firstName;

  @Column(name = "academicTitle")
  String academicTitle;

  @Column(name = "affiliation")
  String affiliation;

  @ManyToMany(mappedBy = "authors")
  List<ArticleEntity> articles;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public UserEntity getUser() {
    return user;
  }
  public void setUser(UserEntity user) {
    this.user = user;
  }

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getAcademicTitle() {
    return academicTitle;
  }
  public void setAcademicTitle(String academicTitle) {
    this.academicTitle = academicTitle;
  }
  public String getAffiliation() {
    return affiliation;
  }
  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  public List<ArticleEntity> getArticles() {
    return articles;
  }
  public void setArticles(List<ArticleEntity> articles) {
    this.articles = articles;
  }
}