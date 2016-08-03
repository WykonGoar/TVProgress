CREATE TABLE `shows` ( 	`_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, 	`title`	VARCHAR (255), 	`currentSeason`	INTEGER, 	`currentEpisode`	INTEGER, 	`lastSeason`	INTEGER DEFAULT(1), 	`lastEpisode`	INTEGER DEFAULT(-1), 	`image`	VARCHAR (255), 	`banner`	VARCHAR (255), 	`url`	VARCHAR (255) );
CREATE TABLE `episodes` (`showId`	INTEGER, 	`season`	INTEGER, 	`episode`	INTEGER,	`title`	VARCHAR (255),    `release_date`   VARCHAR (10), 	`seen`	INTEGER DEFAULT(0), PRIMARY KEY (showId,season, episode), CONSTRAINT fk_show_id     FOREIGN KEY (showId)     REFERENCES shows (_id)     ON DELETE CASCADE );