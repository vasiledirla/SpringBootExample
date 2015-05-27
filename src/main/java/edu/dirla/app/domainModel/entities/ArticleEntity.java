package edu.dirla.app.domainModel.entities;
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


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "article")
public class ArticleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;


  @Column(name = "title")
  String title;

  @Column(name = "description")
  String description;


  @Column(name = "content")
  String content;

  @Column(name = "publication_date")
  Date publicationDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name="articles_authors",
          joinColumns={@JoinColumn(name="articleId", referencedColumnName="id")},
          inverseJoinColumns={@JoinColumn(name="authorId", referencedColumnName="id")})
  List<AuthorEntity> authors;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public List<AuthorEntity> getAuthors() {
    return authors;
  }

  public void setAuthors(List<AuthorEntity> authors) {
    this.authors = authors;
  }
}