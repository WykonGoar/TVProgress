CREATE TABLE `shows` ( 	`_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, 	`title`	VARCHAR (255), 	`currentSeason`	INTEGER, 	`currentEpisode`	INTEGER, 	`lastSeason`	INTEGER, 	`lastEpisode`	INTEGER, 	`image`	VARCHAR (255), 	`banner`	VARCHAR (255), 	`url`	VARCHAR (255) );
CREATE TABLE `episodes` ( 	`_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, 	`showId`	INTEGER, 	`season`	INTEGER, 	`episode`	INTEGER, 	`title`	VARCHAR (255), 	`seen`	INTEGER DEFAULT(0) );