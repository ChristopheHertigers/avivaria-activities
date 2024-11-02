CREATE TABLE aantal (
	id int NOT NULL,
	naam varchar(5) NOT NULL,
	aantal int NOT NULL,
	aantal2 smallint,
  CONSTRAINT PK_aantal PRIMARY KEY ( id )
);

CREATE TABLE deelnemer (
	id int NOT NULL,
	naam varchar(50) NOT NULL,
	straat varchar(100),
	woonplaats varchar(100),
	telefoon varchar(50),
	vereniging_id int,
	fokkerskaart_nummer varchar(20),
	jeugddeelnemer numeric(4, 0),
  CONSTRAINT PK_deelnemer PRIMARY KEY ( id )
);

CREATE TABLE event (
	id int NOT NULL,
	type varchar(50) NOT NULL,
	naam varchar(100),
	dieren numeric(6, 2) NOT NULL,
	dieren_club numeric(6, 2) NOT NULL,
	dieren_te_koop numeric(6, 2) NOT NULL,
	palmares numeric(6, 2) NOT NULL,
	lidgeld numeric(6, 2) NOT NULL,
	lidgeld_jeugd numeric(6, 2) NOT NULL,
	fokkerskaart numeric(6, 2) NOT NULL,
	fokkerskaart_2 numeric(6, 2) NOT NULL,
	selected varchar(5) NOT NULL DEFAULT 'False',
  CONSTRAINT PK_event PRIMARY KEY ( id )
);

CREATE TABLE hok (
	id int NOT NULL,
	inschrijving_lijn_id int NOT NULL,
	hoknummer int,
	aantal_id int,
	prijs varchar(50),
	opmerking varchar(50),
  CONSTRAINT PK_hok PRIMARY KEY ( id )
);

CREATE TABLE inschrijving_header (
	id int NOT NULL,
	event_id int NOT NULL,
	volgnummer int,
	deelnemer_id int NOT NULL,
	palmares varchar(5) NOT NULL DEFAULT 'False',
	fokkerskaart varchar(5) NOT NULL DEFAULT 'False',
	fokkerskaart2 varchar(5) NOT NULL DEFAULT 'False',
	lidgeld varchar(5) NOT NULL DEFAULT 'False',
	lidgeld2 varchar(5) NOT NULL DEFAULT 'False',
  CONSTRAINT PK_inschrijving_header PRIMARY KEY ( id )
);

CREATE TABLE inschrijving_lijn (
	id int NOT NULL,
	inschrijving_header_id int NOT NULL,
	ras_id int NOT NULL,
	kleur_id int NOT NULL,
	leeftijd varchar(5),
	aantal_id int,
	ringnummer varchar(50),
	prijs decimal(6, 2),
  CONSTRAINT PK_inschrijving_lijn PRIMARY KEY ( id )
);

CREATE TABLE kleur (
	id int NOT NULL,
	naam varchar(50),
  CONSTRAINT PK_KLEURTEMP PRIMARY KEY ( id )
);

CREATE TABLE ras (
	id int NOT NULL,
	soort_id int NOT NULL,
	naam varchar(100) NOT NULL,
	erkend varchar(5) NOT NULL,
	hoktype varchar(50) NOT NULL,
  CONSTRAINT PK_ras PRIMARY KEY ( id )
);

CREATE TABLE ras_kleur (
	id int NOT NULL,
	ras_id int NOT NULL,
	kleur_id int NOT NULL,
	erkend varchar(5) NOT NULL,
  CONSTRAINT PK_Ras_Kleur PRIMARY KEY ( id )
);

CREATE TABLE soort (
	id int NOT NULL,
	afkorting varchar(2) NOT NULL,
	naam varchar(50) NOT NULL,
  CONSTRAINT PK_soort PRIMARY KEY ( id )
);

CREATE TABLE vereniging (
	id int NOT NULL,
	naam varchar(100) NOT NULL,
  CONSTRAINT PK_vereniging PRIMARY KEY ( id )
);

ALTER TABLE deelnemer ADD CONSTRAINT FK_deelnemer_vereniging FOREIGN KEY(vereniging_id)
REFERENCES vereniging (id);

ALTER TABLE hok ADD CONSTRAINT FK_hok_aantal FOREIGN KEY(aantal_id)
REFERENCES aantal (id);

ALTER TABLE hok ADD CONSTRAINT FK_hok_inschrijving_lijn FOREIGN KEY(inschrijving_lijn_id)
REFERENCES inschrijving_lijn (id);

ALTER TABLE inschrijving_header ADD CONSTRAINT FK_inschrijving_header_deelnemer FOREIGN KEY(deelnemer_id)
REFERENCES deelnemer (id);

ALTER TABLE inschrijving_header ADD CONSTRAINT FK_inschrijving_header_event FOREIGN KEY(event_id)
REFERENCES event (id);

ALTER TABLE inschrijving_lijn ADD CONSTRAINT FK_inschrijving_lijn_aantal FOREIGN KEY(aantal_id)
REFERENCES aantal (id);

ALTER TABLE inschrijving_lijn ADD CONSTRAINT FK_inschrijving_lijn_inschrijving_header FOREIGN KEY(inschrijving_header_id)
REFERENCES inschrijving_header (id);

ALTER TABLE inschrijving_lijn ADD CONSTRAINT FK_inschrijving_lijn_kleur FOREIGN KEY(kleur_id)
REFERENCES kleur (id);

ALTER TABLE inschrijving_lijn ADD CONSTRAINT FK_inschrijving_lijn_ras FOREIGN KEY(ras_id)
REFERENCES ras (id);

ALTER TABLE ras ADD CONSTRAINT FK_ras_soort FOREIGN KEY(soort_id)
REFERENCES soort (id);

ALTER TABLE ras_kleur ADD CONSTRAINT FK_ras_kleur_kleur FOREIGN KEY(kleur_id)
REFERENCES kleur (id);

ALTER TABLE ras_kleur ADD CONSTRAINT FK_ras_kleur_ras FOREIGN KEY(ras_id)
REFERENCES ras (id);

CREATE TABLE version (
	version VARCHAR(10) NOT NULL
);

INSERT INTO version VALUES('1.0');