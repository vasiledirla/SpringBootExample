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

import edu.dirla.app.domainModel.entities.ArticleEntity;
import edu.dirla.app.domainModel.entities.AuthorEntity;
import edu.dirla.app.domainModel.repositories.ArticleRepository;
import edu.dirla.app.domainModel.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public void addArticle(String title, String description, String content, Date creationDate, Collection<Long> authorIds) {

        List<AuthorEntity> authors = authorRepository.findAll(authorIds);

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(title);
        articleEntity.setDescription(description);
        articleEntity.setContent(content);

        articleEntity.setPublicationDate(creationDate);
        articleEntity.setAuthors(authors);

        articleRepository.save(articleEntity);
    }

    public List<ArticleEntity> findByTitle(String title) {
        return articleRepository.findByTitle(title);
    }

    public void updateArticle(ArticleEntity articleEntity, String title, String description, String content, Date creationDate, List<Long> authorIds) {
        if (articleEntity != null) {
            List<AuthorEntity> authors = authorRepository.findAll(authorIds);
            articleEntity.setTitle(title);
            articleEntity.setDescription(description);
            articleEntity.setContent(content);

            articleEntity.setPublicationDate(creationDate);
            articleEntity.setAuthors(authors);

            articleRepository.save(articleEntity);
        }
    }

    public List<ArticleEntity> findAll() {
        return articleRepository.findAll();
    }

    public ArticleEntity findById(Long articleId) {
        return articleRepository.findOne(articleId);
    }

    public void deleteById(Long articleId) {
        articleRepository.delete(articleId);
    }
}
