INSERT INTO shows (_id, title, currentSeason,currentEpisode,lastSeason,lastEpisode,image,banner,url) VALUES (1, 'The big bang theory', 8, 20, 9, 24, "", "", "");
INSERT INTO episodes (showId, season, episode, title, release_date, seen) VALUES (1, 8, 20, "Test1", "2006-01-02", 1);
INSERT INTO episodes (showId, season, episode, title, release_date, seen) VALUES (1, 8, 21, "Test2", "2006-02-02", 0);
INSERT INTO episodes (showId, season, episode, title, release_date, seen) VALUES (1, 9, 24, "Test3", "2006-03-02", 0);