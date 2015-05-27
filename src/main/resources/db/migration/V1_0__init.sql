CREATE TABLE article
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(1000),
    content VARCHAR(1000),
    publication_date DATETIME
);
CREATE TABLE articles_authors
(
    article_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL
);
CREATE TABLE author
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    academic_title VARCHAR(255),
    affiliation VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);

CREATE TABLE role
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    role_name VARCHAR(255)
);
CREATE TABLE roles_rights
(
    role_id BIGINT NOT NULL,
    right_id BIGINT NOT NULL
);
CREATE TABLE user
(
    id BIGINT PRIMARY KEY NOT NULL,
    password VARCHAR(255),
    user_name VARCHAR(255),
    role_id BIGINT
);
CREATE TABLE user_right
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    right_nam VARCHAR(255)
);
ALTER TABLE articles_authors ADD FOREIGN KEY (article_id) REFERENCES article (id);
ALTER TABLE articles_authors ADD FOREIGN KEY (author_id) REFERENCES author (id);
CREATE INDEX FK_fv6nltob63g9arln6ogqkjh9v ON articles_authors (article_id);
CREATE INDEX FK_i0q9xvexeex7dk88qak7pnj6y ON articles_authors (author_id);
ALTER TABLE roles_rights ADD FOREIGN KEY (right_id) REFERENCES user_right (id);
ALTER TABLE roles_rights ADD FOREIGN KEY (role_id) REFERENCES role (id);
CREATE INDEX FK_19ae0h7b4glfjp1lryawbmf7p ON roles_rights (right_id);
CREATE INDEX FK_m8r7lwe8quy6d7ntqeynxj1t2 ON roles_rights (role_id);
ALTER TABLE user ADD FOREIGN KEY (role_id) REFERENCES role (id);
CREATE UNIQUE INDEX UK_lqjrcobrh9jc8wpcar64q1bfh ON user (user_name);
CREATE INDEX FK_qleu8ddawkdltal07p8e6hgva ON user (role_id);



--insert default roles into the database
insert into role (role_name, description) values
('GUEST', 'Guest user'),
('AUTHOR','this is admin'),
('EDITOR','an article editor'),
('ADMIN','This is like a god');

