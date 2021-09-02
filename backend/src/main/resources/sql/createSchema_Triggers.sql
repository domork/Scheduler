CREATE OR REPLACE FUNCTION trigger_add_schedule_user_roles() RETURNS trigger AS
$$
BEGIN
    IF NOT EXISTS(SELECT 1 FROM pg_type WHERE typname = 'schedule_user_roles') THEN
        CREATE TYPE schedule_user_roles AS
            ENUM ('user','moderator','admin', 'demo');
    END IF;
    RETURN NEW;
end
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr1 on roles;
CREATE TRIGGER tr1
    BEFORE INSERT
    ON roles
    FOR ROW
EXECUTE PROCEDURE trigger_add_schedule_user_roles();

INSERT INTO roles(name)
VALUES ('ROLE_USER')
ON CONFLICT DO NOTHING;
INSERT INTO roles(name)
VALUES ('ROLE_ADMIN')
ON CONFLICT DO NOTHING;
INSERT INTO roles(name)
VALUES ('ROLE_DEMO')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS schedule_group
(
    ID            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    time_to_start TIMESTAMP,
    description   VARCHAR(255),
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_members
(
    group_ID        SERIAL,
    user_ID         SERIAL,
    group_user_UUID VARCHAR(255) NOT NULL UNIQUE,
    color           VARCHAR(7) UNIQUE,
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

-- DELETE OLD ENTRIES ----------------------------------------

CREATE OR REPLACE FUNCTION trigger_delete_old_demo_entries() RETURNS trigger AS
$$
BEGIN
    DELETE
    FROM time_of_unique_user_in_group t
    WHERE group_user_UUID IN (SELECT g.group_user_UUID
                              FROM group_members g
                                       LEFT JOIN users u ON g.user_id = u.id
                              WHERE u.username LIKE '%test_user-%'
                                AND registration_time < now() - interval '3 hours');

    DELETE
    FROM user_roles ur
    WHERE user_id IN (SELECT id as user_id
                      FROM users
                      WHERE users.username LIKE 'test_user-%'
                        AND users.registration_time < now() - interval '3 hours');
    DELETE
    FROM role_of_user_in_specific_group ur
    WHERE group_user_uuid IN (SELECT g.group_user_uuid
                              FROM group_members g
                                       LEFT JOIN users u ON g.user_id = u.id
                              WHERE u.username LIKE '%test_user-%'
                                AND registration_time < now() - interval '3 hours');
    DELETE
    FROM group_members ur
    WHERE user_id IN (SELECT user_id
                      FROM group_members g
                               LEFT JOIN schedule_group sg on g.group_ID = sg.ID
                      WHERE (sg.name LIKE '%Small Random Group #%' OR sg.name LIKE '%Medium Random Group #-%' OR
                             sg.name LIKE '%Large Random Group #-%')
                        AND sg.creation_time < now() - interval '3 hours');
    DELETE
    FROM schedule_group
    WHERE (name LIKE '%Small Random Group #%' OR name LIKE '%Medium Random Group #-%' OR
           name LIKE '%Large Random Group #-%')
      AND creation_time < now() - interval '3 hours';

    DELETE
    FROM users
    WHERE username LIKE '%test_user-%'
      AND registration_time < now() - interval '3 hours';

    RETURN NEW;
END
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr2 on users;

CREATE TRIGGER tr2
    BEFORE INSERT
    ON users
    FOR EACH STATEMENT
EXECUTE PROCEDURE trigger_delete_old_demo_entries();

-- ADD MOCK DATA  ----------------------------------------

CREATE OR REPLACE FUNCTION trigger_add_demo_users_to_new_Groups() RETURNS trigger AS
$$
DECLARE
    uuid1   text      := gen_random_uuid();
    uuid2   text      := gen_random_uuid();
    uuid3   text      := gen_random_uuid();
    uuid4   text      := gen_random_uuid();
    uuid5   text      := gen_random_uuid();
    uuid6   text      := gen_random_uuid();
    uuid7   text      := gen_random_uuid();
    uuid8   text      := gen_random_uuid();
    uuid9   text      := gen_random_uuid();
    uuid10  text      := gen_random_uuid();
    uuid11  text      := gen_random_uuid();
    uuid12  text      := gen_random_uuid();
    nextDay timestamp := current_date + 1; -- + '00:00:01'::time
BEGIN
    IF (NEW.name LIKE '%Large Random Group #%') THEN
        INSERT INTO group_members (group_id, user_id, group_user_UUID, color, name)
        VALUES (NEW.ID, -1, uuid1, random_color(), 'Bill'),
               (NEW.ID, -2, uuid2, random_color(), 'Andrey'),
               (NEW.ID, -3, uuid3, random_color(), 'Anna Franziska Srna'),
               (NEW.ID, -4, uuid4, random_color(), 'Gabi'),
               (NEW.ID, -5, uuid5, random_color(), 'Vlad'),
               (NEW.ID, -6, uuid6, random_color(), 'Bartholomew'),
               (NEW.ID, -7, uuid7, random_color(), 'Domork'),
               (NEW.ID, -8, uuid8, random_color(), 'Very_very_very_long_name so you could scroll like a boss/in'),
               (NEW.ID, -9, uuid9, random_color(), 'Tixxx'),
               (NEW.ID, -10, uuid10, random_color(), 'Jean-Baptiste Joseph Fourier'),
               (NEW.ID, -11, uuid11, random_color(), 'Napoléon Bonaparte'),
               (NEW.ID, -12, uuid12, random_color(), 'Petar');
        INSERT INTO time_of_unique_user_in_group (group_user_UUID, time_start, time_end)
        VALUES (uuid1, nextDay + '09:30:00'::time, nextDay + '10:30:00'::time),
               (uuid1, nextDay + '12:30:00'::time, nextDay + '16:00:00'::time),
               (uuid2, nextDay + '15:30:00'::time, nextDay + '18:30:00'::time),
               (uuid3, nextDay + '08:00:00'::time, nextDay + '10:00:00'::time),
               (uuid4, nextDay + '11:00:00'::time, nextDay + '14:30:00'::time),
               (uuid5, nextDay + '11:00:00'::time, nextDay + '19:00:00'::time),
               (uuid6, nextDay + '16:30:00'::time, nextDay + '19:30:00'::time),
               (uuid7, nextDay + '20:30:00'::time, nextDay + '22:00:00'::time),
               (uuid8, nextDay + '08:00:00'::time, nextDay + '11:00:00'::time),
               (uuid9, nextDay + '13:30:00'::time, nextDay + '18:30:00'::time),
               (uuid10, nextDay + '09:30:00'::time, nextDay + '12:30:00'::time),
               (uuid10, nextDay + '15:30:00'::time, nextDay + '18:30:00'::time),
               (uuid10, nextDay + '20:00:00'::time, nextDay + '22:00:00'::time),
               (uuid11, nextDay + '15:30:00'::time, nextDay + '17:30:00'::time);

    ELSIF (NEW.name LIKE '%Medium Random Group #%') THEN
        INSERT INTO group_members (group_id, user_id, group_user_UUID, color, name)
        VALUES (NEW.ID, -1, uuid1, random_color(), 'Bill'),
               (NEW.ID, -2, uuid2, random_color(), 'Andrey'),
               (NEW.ID, -3, uuid3, random_color(), 'Anna Franziska Srna'),
               (NEW.ID, -4, uuid4, random_color(), 'Gabi'),
               (NEW.ID, -5, uuid5, random_color(), 'Vlad'),
               (NEW.ID, -6, uuid6, random_color(), 'Bartholomew'),
               (NEW.ID, -7, uuid7, random_color(), 'Domork'),
               (NEW.ID, -8, uuid8, random_color(),
                'Very_very_very_long_name so you could scroll like a boss/in');

        INSERT INTO time_of_unique_user_in_group (group_user_UUID, time_start, time_end)
        VALUES (uuid1, nextDay + '09:30:00'::time, nextDay + '10:30:00'::time),
               (uuid1, nextDay + '12:30:00'::time, nextDay + '16:00:00'::time),
               (uuid2, nextDay + '15:30:00'::time, nextDay + '18:30:00'::time),
               (uuid3, nextDay + '08:00:00'::time, nextDay + '10:00:00'::time),
               (uuid4, nextDay + '11:00:00'::time, nextDay + '14:30:00'::time),
               (uuid5, nextDay + '11:00:00'::time, nextDay + '19:00:00'::time),
               (uuid6, nextDay + '16:30:00'::time, nextDay + '19:30:00'::time),
               (uuid7, nextDay + '20:30:00'::time, nextDay + '22:00:00'::time),
               (uuid8, nextDay + '08:00:00'::time, nextDay + '11:00:00'::time);

    ELSIF (NEW.name LIKE '%Small Random Group #%') THEN
        INSERT INTO group_members (group_id, user_id, group_user_UUID, color, name)
        VALUES (NEW.ID, -1, uuid1, random_color(), 'Bill'),
               (NEW.ID, -2, uuid2, random_color(), 'Maria'),
               (NEW.ID, -3, uuid3, random_color(), 'Andrey'),
               (NEW.ID, -4, uuid4, random_color(), 'Gabi');
        INSERT INTO time_of_unique_user_in_group (group_user_UUID, time_start, time_end)
        VALUES (uuid1, nextDay + '09:30:00'::time, nextDay + '10:30:00'::time),
               (uuid1, nextDay + '12:30:00'::time, nextDay + '16:00:00'::time),
               (uuid2, nextDay + '15:30:00'::time, nextDay + '18:30:00'::time),
               (uuid3, nextDay + '08:00:00'::time, nextDay + '10:00:00'::time),
               (uuid4, nextDay + '11:00:00'::time, nextDay + '14:30:00'::time);
    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr_add_demo_users_entries on schedule_group;

CREATE TRIGGER tr_add_demo_users_entries
    AFTER INSERT
    ON schedule_group
    FOR EACH ROW
EXECUTE PROCEDURE trigger_add_demo_users_to_new_Groups();


Create or replace function random_color() returns text as
$$
declare
    chars  text[]  := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F}';
    result text    := '#';
    length integer := 6;
    i      integer := 0;
begin
    for i in 1..length
        loop
            result := result || chars[1 + random() * (array_length(chars, 1) - 1)];
        end loop;
    return result;
end;
$$ language plpgsql ^;

/*
CREATE OR REPLACE FUNCTION trigger_add_new_member_check() RETURNS trigger AS
$$
BEGIN
    RAISE NOTICE 'Trigger\n';
    IF EXISTS(SELECT 1 FROM group_members WHERE group_id=NEW.group_ID AND user_id=NEW.user_ID)
    THEN
        RAISE EXCEPTION 'This user is already in this group';
    END IF;
    RETURN NEW;
END
$$ LANGUAGE plpgsql ^;

DROP TRIGGER IF EXISTS tr2 on group_members;

CREATE TRIGGER tr2
    BEFORE INSERT
    ON group_members
    FOR EACH STATEMENT
EXECUTE PROCEDURE trigger_add_new_member_check();
*/
