DELETE FROM user_roles ur WHERE user_id<0;
DELETE FROM users WHERE id<0;

INSERT INTO users (ID, password, username)
VALUES (-1,'yes-yes,you can see this password :)','TestBill'),
       (-2,'yes-yes,you can see this password :)','TestMaria'),
       (-3,'yes-yes,you can see this password :)','TestAnna Franziska Srna'),
       (-4,'yes-yes,you can see this password :)','TestGabi'),
       (-5,'yes-yes,you can see this password :)','TestVlad'),
       (-6,'yes-yes,you can see this password :)','TestBartholomew'),
       (-7,'yes-yes,you can see this password :)','TestDomork'),
       (-8,'yes-yes,you can see this password :)','TestVery very very long name so you could scroll like a boss/in'),
       (-9,'yes-yes,you can see this password :)','TestTixxx'),
       (-10,'yes-yes,you can see this password :)','TestJean-Baptiste Joseph Fourier'),
       (-11,'yes-yes,you can see this password :)','TestNapoléon Bonaparte'),
       (-12,'yes-yes,you can see this password :)','TestPetar')
;

INSERT INTO user_roles(user_id, role_id)
VALUES (-1,1),
       (-2,1),
       (-3,1),
       (-4,1),
       (-5,1),
       (-6,1),
       (-7,1),
       (-8,1),
       (-9,1),
       (-10,1),
       (-11,1),
       (-12,1)
ON CONFLICT DO NOTHING;
