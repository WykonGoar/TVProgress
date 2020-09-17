CREATE TABLE `db_version` (
    `version`   INTEGER   NOT NULL    PRIMARY KEY
);

CREATE TABLE `shows` (
    `_id`       INTEGER         NOT NULL    PRIMARY KEY AUTOINCREMENT UNIQUE,
    `title`	VARCHAR (255)       NULL,
    `currentSeason`	INTEGER     NULL,
    `currentEpisode`	INTEGER NULL,
    `banner`	VARCHAR (255)   NULL,
    `url`	VARCHAR (255)       NULL,
    `status` VARCHAR (255)      NULL
);

CREATE TABLE `episodes` (
    `showId`	INTEGER,
    `season`	INTEGER,
    `episode`	INTEGER,
    `title`	VARCHAR (255),
    `release_date`   VARCHAR (10),
    `seen`	INTEGER DEFAULT(0),

    PRIMARY KEY (showId, season, episode),

    CONSTRAINT fk_show_id
        FOREIGN KEY (showId) REFERENCES shows (_id)
        ON DELETE CASCADE
);
