ALTER TABLE inschrijving_header ADD lid_avivaria VARCHAR( 5 ) NOT NULL DEFAULT 'False';

UPDATE inschrijving_header SET lid_avivaria = 'True' WHERE deelnemer_id IN (SELECT id FROM deelnemer WHERE vereniging_id = 1);

UPDATE VERSION SET VERSION = '2.1';
