CREATE TABLE chat_user
(
    id            SERIAL PRIMARY KEY,
    login         text UNIQUE,
    hash_password text
);

CREATE TABLE refresh_token
(
    id SERIAL PRIMARY KEY,
    token text UNIQUE ,
    user_id bigint REFERENCES chat_user(id)
)