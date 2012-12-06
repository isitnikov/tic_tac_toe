PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE 'Players' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'wins' INTEGER);
INSERT INTO "Players" VALUES(1,'Player 1',0);
INSERT INTO "Players" VALUES(2,'Player 2',0);
CREATE TABLE 'Configuration' (
    'locale' VARCHAR(5)
, 'player1' INTEGER, 'player2' INTEGER);
INSERT INTO "Configuration" VALUES('en-US',1,2);
DELETE FROM sqlite_sequence;
INSERT INTO "sqlite_sequence" VALUES('Players',8);
COMMIT;