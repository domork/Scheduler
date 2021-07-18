CREATE TYPE schedule_user_roles AS ENUM ('user','moderator','admin');




CREATE TABLE IF NOT EXISTS groups
(
    ID          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    time_to_start TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS group_members
(
    group_ID                SERIAL,
    user_ID                 SERIAL,
    user_unique_group_ID    SERIAL UNIQUE,
    FOREIGN KEY (group_ID) REFERENCES groups(ID),
    FOREIGN KEY (user_ID) REFERENCES users(ID),
    PRIMARY KEY (group_ID, user_ID)
    );

CREATE TABLE IF NOT EXISTS unique_user_in_group_time
(
    user_unique_group_ID    SERIAL PRIMARY KEY,
    time_start              TIMESTAMP NOT NULL,
    time_end                TIMESTAMP NOT NULL,
    FOREIGN KEY (user_unique_group_ID) REFERENCES group_members(user_unique_group_ID)

    );
CREATE TABLE IF NOT EXISTS user_in_specific_group_role
(
    user_unique_group_ID    SERIAL PRIMARY KEY,
    role                    schedule_user_roles NOT NULL,
    FOREIGN KEY (user_unique_group_ID) REFERENCES group_members(user_unique_group_ID)

);

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_PM');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
