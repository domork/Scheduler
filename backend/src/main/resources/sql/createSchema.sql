CREATE TYPE schedule_user_roles AS ENUM ('user','moderator','admin');




CREATE TABLE IF NOT EXISTS schedule_group
(
    ID                      SERIAL PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL UNIQUE,
    password                VARCHAR(255) NOT NULL,
    time_to_start           TIMESTAMP,
    description             VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS group_members
(
    group_ID                SERIAL,
    user_ID                 SERIAL,
    group_user_UUID         VARCHAR(255) UNIQUE,
    FOREIGN KEY (group_ID)  REFERENCES schedule_group(ID),
    FOREIGN KEY (user_ID)   REFERENCES users(ID),
    PRIMARY KEY (group_ID, user_ID)
    );

CREATE TABLE IF NOT EXISTS time_of_unique_user_in_group
(
    group_user_UUID         VARCHAR(255),
    time_start              TIMESTAMP NOT NULL,
    time_end                TIMESTAMP NOT NULL,
    FOREIGN KEY (group_user_UUID) REFERENCES group_members(group_user_UUID),
    PRIMARY KEY (group_user_UUID, time_end)

);
CREATE TABLE IF NOT EXISTS role_of_user_in_specific_group
(
    group_user_UUID         VARCHAR(255) PRIMARY KEY,
    role                    schedule_user_roles NOT NULL,
    FOREIGN KEY (group_user_UUID) REFERENCES group_members(group_user_UUID)

);

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
