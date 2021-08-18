CREATE TABLE IF NOT EXISTS schedule_group
(
    ID            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    time_to_start TIMESTAMP,
    description   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS group_members
(
    group_ID        SERIAL,
    user_ID         SERIAL,
    group_user_UUID VARCHAR(255) NOT NULL UNIQUE,
    color           VARCHAR(7),
    name            VARCHAR(255) NOT NULL,
    FOREIGN KEY (group_ID) REFERENCES schedule_group (ID) ON DELETE CASCADE,
    FOREIGN KEY (user_ID) REFERENCES users (ID),
    PRIMARY KEY (group_ID, user_ID)
);

CREATE TABLE IF NOT EXISTS time_of_unique_user_in_group
(
    group_user_UUID VARCHAR(255),
    time_start      TIMESTAMP NOT NULL,
    time_end        TIMESTAMP NOT NULL,
    FOREIGN KEY (group_user_UUID) REFERENCES group_members (group_user_UUID) ON DELETE CASCADE,
    PRIMARY KEY (group_user_UUID, time_end)

);
CREATE TABLE IF NOT EXISTS role_of_user_in_specific_group
(
    group_user_UUID VARCHAR(255) PRIMARY KEY,
    role            schedule_user_roles NOT NULL,
    FOREIGN KEY (group_user_UUID) REFERENCES group_members (group_user_UUID) ON DELETE CASCADE

);

CREATE OR REPLACE FUNCTION trigger_add_schedule_user_roles() RETURNS trigger AS
$$
BEGIN
    IF NOT EXISTS(SELECT 1 FROM pg_type WHERE typname = 'schedule_user_roles') THEN
        CREATE TYPE schedule_user_roles AS
            ENUM ('user','moderator','admin');
    END IF;
    RETURN NEW;
end
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr1 on roles;
CREATE TRIGGER tr1
    BEFORE INSERT
    ON roles
    FOR ROW
    WHEN (new.name = 'ROLE_USER')
EXECUTE PROCEDURE trigger_add_schedule_user_roles();

INSERT INTO roles(name)
VALUES ('ROLE_USER')
ON CONFLICT DO NOTHING;
INSERT INTO roles(name)
VALUES ('ROLE_ADMIN')
ON CONFLICT DO NOTHING;

/*CREATE OR REPLACE FUNCTION trigger_add_schedule_user_roles() RETURNS trigger AS
$$
BEGIN
    IF ()
    THEN
        RETURN NEW;

    END IF;
    RETURN NEW;
END
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr2 on group_members;

CREATE TRIGGER tr2
    AFTER DELETE
    ON group_members
    FOR STATEMENT
EXECUTE PROCEDURE trigger_add_schedule_user_roles();

*/