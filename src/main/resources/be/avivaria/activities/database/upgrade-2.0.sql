ALTER TABLE hok ADD type INT NOT NULL DEFAULT 0;
ALTER TABLE ras ADD hoktype_man INT NOT NULL DEFAULT 0;
UPDATE ras set hoktype_man = cast(hoktype as integer);
alter table ras drop column hoktype;
ALTER TABLE ras ADD hoktype_vrouw INT NOT NULL DEFAULT 0;
UPDATE ras SET hoktype_vrouw = hoktype_man;
ALTER TABLE ras ADD belgisch VARCHAR( 5 ) NOT NULL DEFAULT  'False';

UPDATE  ras SET  belgisch =  'True' WHERE id=389;
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1045, 20, 'Hangoor angoradwerg', 'True', 4, 4, 'True');
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1046, 21, 'Schwarzgrannen', 'True', 4, 4, 'False');
UPDATE  ras SET  belgisch =  'True' WHERE id=430;
UPDATE  ras SET  belgisch =  'True' WHERE id=442;
UPDATE  ras SET  belgisch =  'True' WHERE id=393;
UPDATE  ras SET  belgisch =  'True' WHERE id=394;
UPDATE  ras SET  belgisch =  'True' WHERE id=391;
UPDATE  ras SET  belgisch =  'True' WHERE id=392;
UPDATE  ras SET  belgisch =  'True' WHERE id=403;
UPDATE  ras SET  belgisch =  'True' WHERE id=447;
UPDATE  ras SET  belgisch =  'True' WHERE id=455;
UPDATE  ras SET  belgisch =  'True' WHERE id=448;
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1047, 31, 'Maltezer kipduif', 'True', 4, 4, 'False');
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1048, 31, 'Triganino', 'True', 5, 5, 'False');
UPDATE  ras SET  belgisch =  'True' WHERE id=120;
UPDATE  ras SET  belgisch =  'True' WHERE id=29;
UPDATE  ras SET  belgisch =  'True' WHERE id=93;
UPDATE  ras SET  belgisch =  'True' WHERE id=121;
UPDATE  ras SET  belgisch =  'True' WHERE id=122;
UPDATE  ras SET  belgisch =  'True' WHERE id=208;
UPDATE  ras SET  belgisch =  'True' WHERE id=34;
UPDATE  ras SET  belgisch =  'True' WHERE id=35;
UPDATE  ras SET  belgisch =  'True' WHERE id=37;
UPDATE  ras SET  belgisch =  'True' WHERE id=183;
UPDATE  ras SET  belgisch =  'True' WHERE id=50;
UPDATE  ras SET  belgisch =  'True' WHERE id=36;
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1049, 36, 'Italiaanse Sotto Banca', 'True', 4, 4, 'False');
UPDATE  ras SET  belgisch =  'True' WHERE id=123;
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1050, 36, 'Montauban', 'True', 4, 4, 'False');
UPDATE  ras SET  belgisch =  'True' WHERE id=162;
UPDATE  ras SET  belgisch =  'True' WHERE id=180;
UPDATE  ras SET  belgisch =  'True' WHERE id=89;
UPDATE  ras SET  belgisch =  'True' WHERE id=90;
UPDATE  ras SET  belgisch =  'True' WHERE id=91;
UPDATE  ras SET  belgisch =  'True' WHERE id=92;
UPDATE  ras SET  belgisch =  'True' WHERE id=118;
UPDATE  ras SET  hoktype_man =  4, hoktype_vrouw =  4 WHERE id=206;
DELETE FROM soort WHERE id = 40;
INSERT INTO ras (id, soort_id, naam, erkend, hoktype_man, hoktype_vrouw, belgisch) VALUES (1051, 60, 'Aarschots hoen', 'True', 3, 3, 'True');
UPDATE  ras SET  erkend = 'True', hoktype_man =  4, hoktype_vrouw =  4, belgisch =  'True' WHERE id=624;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=467;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=471;
UPDATE  ras SET  belgisch =  'True' WHERE id=470;
UPDATE  ras SET  belgisch =  'True' WHERE id=477;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=478;
UPDATE  ras SET  belgisch =  'True' WHERE id=482;
UPDATE  ras SET  belgisch =  'True' WHERE id=484;
UPDATE  ras SET  belgisch =  'True' WHERE id=486;
UPDATE  ras SET  belgisch =  'True' WHERE id=487;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=489;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=490;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=491;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=492;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=493;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=494;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=495;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=496;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=497;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=498;
UPDATE  ras SET  belgisch =  'True' WHERE id=503;
UPDATE  ras SET  belgisch =  'True' WHERE id=517;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=518;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=520;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=521;
UPDATE  ras SET  belgisch =  'True' WHERE id=523;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=524;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=526;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=533;
UPDATE  ras SET  hoktype_man =  3 WHERE id=534;
UPDATE  ras SET  belgisch =  'True' WHERE id=543;
UPDATE  ras SET  belgisch =  'True' WHERE id=547;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=554;
UPDATE  ras SET  hoktype_vrouw =  4 WHERE id=573;
UPDATE  ras SET  hoktype_vrouw =  4 WHERE id=583;
UPDATE  ras SET  belgisch =  'True' WHERE id=587;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=589;
UPDATE  ras SET  hoktype_vrouw =  5 WHERE id=590;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=591;
UPDATE  ras SET  hoktype_vrouw =  5, belgisch =  'True' WHERE id=592;
UPDATE  ras SET  hoktype_vrouw =  4 WHERE id=595;
UPDATE  ras SET  belgisch =  'True' WHERE id=234;
UPDATE  ras SET  belgisch =  'True' WHERE id=235;
UPDATE  ras SET  belgisch =  'True' WHERE id=249;
UPDATE  ras SET  belgisch =  'True' WHERE id=251;
UPDATE  ras SET  hoktype_man =  1, belgisch =  'True' WHERE id=253;
UPDATE  ras SET  hoktype_man =  1 WHERE id=261;
UPDATE  ras SET  hoktype_man =  1 WHERE id=267;
UPDATE  ras SET  belgisch =  'True' WHERE id=281;
UPDATE  ras SET  belgisch =  'True' WHERE id=288;
UPDATE  ras SET  belgisch =  'True' WHERE id=293;
UPDATE  ras SET  belgisch =  'True' WHERE id=294;
UPDATE  ras SET  belgisch =  'True' WHERE id=302;
UPDATE  ras SET  hoktype_man =  1 WHERE id=307;
UPDATE  ras SET  hoktype_man =  1, belgisch =  'True' WHERE id=317;
UPDATE  ras SET  hoktype_man =  1 WHERE id=320;
UPDATE  ras SET  hoktype_man =  1 WHERE id=326;
UPDATE  ras SET  hoktype_man =  1 WHERE id=327;
UPDATE  ras SET  hoktype_man =  1 WHERE id=328;
UPDATE  ras SET  belgisch =  'True' WHERE id=323;
UPDATE  ras SET  belgisch =  'True' WHERE id=324;
UPDATE  ras SET  hoktype_man =  1 WHERE id=338;
UPDATE  ras SET  hoktype_man =  1 WHERE id=347;
UPDATE  ras SET  hoktype_man =  1 WHERE id=358;
UPDATE  ras SET  hoktype_man =  1 WHERE id=364;
UPDATE  ras SET  hoktype_man =  1, belgisch =  'True' WHERE id=368;
UPDATE  ras SET  belgisch =  'True' WHERE id=372;
UPDATE  ras SET  hoktype_man =  1 WHERE id=380;
UPDATE  ras SET  belgisch =  'True' WHERE id=383;
UPDATE  ras SET  belgisch =  'True' WHERE id=384;
UPDATE  ras SET  belgisch =  'True' WHERE id=385;
UPDATE  ras SET  belgisch =  'True' WHERE id=616;
UPDATE  ras SET  belgisch =  'True' WHERE id=618;
UPDATE  ras SET  belgisch =  'True' WHERE id=739;
UPDATE  ras SET  belgisch =  'True' WHERE id=781;
UPDATE  ras SET  hoktype_man =  1 WHERE id=715;
UPDATE  ras SET  hoktype_man =  1 WHERE id=728;
UPDATE  ras SET  hoktype_man =  1 WHERE id=752;
UPDATE  ras SET  belgisch =  'True' WHERE id=723;
UPDATE  ras SET  belgisch =  'True' WHERE id=730;
UPDATE  ras SET  belgisch =  'True' WHERE id=745;
UPDATE  ras SET  belgisch =  'True' WHERE id=751;
UPDATE  ras SET  belgisch =  'True' WHERE id=768;
UPDATE  ras SET  belgisch =  'True' WHERE id=769;
UPDATE  ras SET  hoktype_man =  2, hoktype_vrouw =  2 WHERE id=791;
UPDATE  ras SET  hoktype_man =  5, hoktype_vrouw =  5 WHERE id =1040;

UPDATE ras SET hoktype_man = 2, hoktype_vrouw = 2 WHERE soort_id = 78;

UPDATE  ras SET  hoktype_man =  2, hoktype_vrouw =  2 WHERE  id =1029;
UPDATE  ras SET  hoktype_man =  1, hoktype_vrouw =  1 WHERE  id =1030;

ALTER TABLE ras ADD volgorde INT NOT NULL DEFAULT  1000;

UPDATE ras SET volgorde = 1 WHERE id in (467,482,517,523,589,592);

ALTER TABLE aantal ADD benaming VARCHAR( 20 ) NOT NULL DEFAULT  '';

UPDATE aantal SET benaming = 'koppel' WHERE id = 3;
UPDATE aantal SET benaming = 'trio' WHERE id = 4;
UPDATE aantal SET benaming = 'duo' WHERE id in (5,6);

UPDATE kleur SET naam = '' WHERE id = 750;

UPDATE inschrijving_lijn SET kleur_id = 750 WHERE kleur_id = 0 and inschrijving_header_id >= 690;

ALTER TABLE hok ADD predicaat VARCHAR( 8 );

UPDATE VERSION SET VERSION = '2.0';
