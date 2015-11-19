INSERT INTO shows (_id, title, currentSeason,currentEpisode,lastSeason,lastEpisode,image,banner,url) VALUES (1, 'The big bang theory', 8, 20, 9, 24, null, null, "https://epguides.frecar.no/show/bigbangtheory/");
INSERT INTO episodes (_id, showId, season, episode, title, seen) VALUES (1, 1, 8, 20, "Test1", 1);
INSERT INTO episodes (_id, showId, season, episode, title, seen) VALUES (2, 1, 8, 21, "Test2", 0);
INSERT INTO episodes (_id, showId, season, episode, title, seen) VALUES (3, 1, 9, 24, "Test3", 0);