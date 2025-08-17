DELETE
FROM public.auditlogentity;
DELETE
FROM public.moviemetadata;
DELETE
FROM public.tagmovierelation;
DELETE
FROM public.tagentity;
DELETE
FROM public.movieentity;

INSERT INTO public.movieentity (id, block, name, type, uuid, wikiurl, year, originalname)
VALUES (1, 'A', 'The Godfather', 'BD', 'uuid-1', 'https://en.wikipedia.org/wiki/The_Godfather', 1972, 'Il Padrino'),
       (2, 'B', 'Pulp Fiction', 'BD', 'uuid-2', 'https://en.wikipedia.org/wiki/Pulp_Fiction', 1994, 'Pulp Fiction'),
       (3, 'C', 'Forrest Gump', 'BD', 'uuid-3', 'https://en.wikipedia.org/wiki/Forrest_Gump', 1994, 'Forrest Gump'),
       (4, 'D', 'The Shawshank Redemption', 'BD', 'uuid-4', 'https://en.wikipedia.org/wiki/The_Shawshank_Redemption',
        1994, 'The Shawshank Redemption'),
       (5, 'E', 'Fight Club', 'BD', 'uuid-5', 'https://en.wikipedia.org/wiki/Fight_Club', 1999, 'Fight Club'),
       (6, 'F', 'Inception', 'BD', 'uuid-6', 'https://en.wikipedia.org/wiki/Inception', 2010, 'Inception'),
       (7, 'G', 'The Matrix', 'BD', 'uuid-7', 'https://en.wikipedia.org/wiki/The_Matrix', 1999, 'Matrix'),
       (8, 'H', 'Goodfellas', 'BD', 'uuid-8', 'https://en.wikipedia.org/wiki/Goodfellas', 1990, 'Goodfellas'),
       (9, 'I', 'The Dark Knight', 'BD', 'uuid-9', 'https://en.wikipedia.org/wiki/The_Dark_Knight_(film)', 2008,
        'The Dark Knight'),
       (10, 'J', 'Interstellar', 'BD', 'uuid-10', 'https://en.wikipedia.org/wiki/Interstellar_(film)', 2014,
        'Interstellar'),
       (11, 'K', 'Gladiator', 'BD', 'uuid-11', 'https://en.wikipedia.org/wiki/Gladiator_(2000_film)', 2000,
        'Gladiator'),
       (12, 'L', 'The Silence of the Lambs', 'BD', 'uuid-12',
        'https://en.wikipedia.org/wiki/The_Silence_of_the_Lambs_(film)', 1991, 'The Silence of the Lambs'),
       (13, 'M', 'Saving Private Ryan', 'BD', 'uuid-13', 'https://en.wikipedia.org/wiki/Saving_Private_Ryan', 1998,
        'Saving Private Ryan'),
       (14, 'N', 'Se7en', 'BD', 'uuid-14', 'https://en.wikipedia.org/wiki/Seven_(1995_film)', 1995, 'Se7en'),
       (15, 'O', 'The Prestige', 'BD', 'uuid-15', 'https://en.wikipedia.org/wiki/The_Prestige_(film)', 2006,
        'The Prestige'),
       (16, 'P', 'Memento', 'BD', 'uuid-16', 'https://en.wikipedia.org/wiki/Memento_(film)', 2000, 'Memento'),
       (17, 'Q', 'The Departed', 'BD', 'uuid-17', 'https://en.wikipedia.org/wiki/The_Departed', 2006, 'The Departed'),
       (18, 'R', 'Whiplash', 'BD', 'uuid-18', 'https://en.wikipedia.org/wiki/Whiplash_(film)', 2014, 'Whiplash'),
       (19, 'S', 'Parasite', 'BD', 'uuid-19', 'https://en.wikipedia.org/wiki/Parasite_(2019_film)', 2019, 'Parasite'),
       (20, 'T', 'Joker', 'BD', 'uuid-20', 'https://en.wikipedia.org/wiki/Joker_(2019_film)', 2019, 'Joker');

-- Insert test data into moviemetadata
INSERT INTO public.moviemetadata (id, genres, imdbid, primaryimage, rating, runtime, year, movieentity_id)
VALUES (1, ARRAY ['Crime', 'Drama'], 'tt0068646', 'godfather.jpg', 9.2, 175, 1972, 1),
       (2, ARRAY ['Crime', 'Drama'], 'tt0110912', 'pulpfiction.jpg', 8.9, 154, 1994, 2),
       (3, ARRAY ['Drama', 'Romance'], 'tt0109830', 'forrestgump.jpg', 8.8, 142, 1994, 3),
       (4, ARRAY ['Drama'], 'tt0111161', 'shawshank.jpg', 9.3, 142, 1994, 4),
       (5, ARRAY ['Drama', 'Thriller'], 'tt0137523', 'fightclub.jpg', 8.8, 139, 1999, 5),
       (6, ARRAY ['Action', 'Sci-Fi'], 'tt1375666', 'inception.jpg', 8.8, 148, 2010, 6),
       (7, ARRAY ['Action', 'Sci-Fi'], 'tt0133093', 'matrix.jpg', 8.7, 136, 1999, 7),
       (8, ARRAY ['Crime', 'Drama'], 'tt0099685', 'goodfellas.jpg', 8.7, 146, 1990, 8),
       (10, ARRAY ['Adventure', 'Drama', 'Sci-Fi'], 'tt0816692', 'interstellar.jpg', 8.6, 169, 2014, 10),
       (12, ARRAY ['Crime', 'Thriller'], 'tt0102926', 'silence.jpg', 8.6, 118, 1991, 12),
       (13, ARRAY ['Drama', 'War'], 'tt0120815', 'savingprivateryan.jpg', 8.6, 169, 1998, 13),
       (14, ARRAY ['Crime', 'Drama', 'Mystery'], 'tt0114369', 'se7en.jpg', 8.6, 127, 1995, 14),
       (15, ARRAY ['Drama', 'Mystery', 'Sci-Fi'], 'tt0482571', 'prestige.jpg', 8.5, 130, 2006, 15),
       (16, ARRAY ['Mystery', 'Thriller'], 'tt0209144', 'memento.jpg', 8.4, 113, 2000, 16),
       (17, ARRAY ['Crime', 'Drama', 'Thriller'], 'tt0407887', 'departed.jpg', 8.5, 151, 2006, 17),
       (18, ARRAY ['Drama', 'Music'], 'tt2582802', 'whiplash.jpg', 8.5, 106, 2014, 18),
       (19, ARRAY ['Drama', 'Thriller'], 'tt6751668', 'parasite.jpg', 8.6, 132, 2019, 19),
       (20, ARRAY ['Crime', 'Drama', 'Thriller'], 'tt7286456', 'joker.jpg', 8.4, 122, 2019, 20);

-- Insert test data into tagentity
INSERT INTO public.tagentity (id, created, name)
VALUES (1, NOW(), 'Oscar Winner'),
       (2, NOW(), 'Cult Classic'),
       (3, NOW(), 'Top Rated'),
       (4, NOW(), 'Drama'),
       (5, NOW(), 'Mind-Bending'),
       (6, NOW(), 'Thriller'),
       (7, NOW(), 'Sci-Fi'),
       (8, NOW(), 'Crime'),
       (9, NOW(), 'Music'),
       (10, NOW(), 'Mystery');

-- Insert test data into tagmovierelation
INSERT INTO public.tagmovierelation (id, movie_id, tag_id)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3),
       (4, 1, 4),
       (5, 2, 2),
       (6, 3, 4),
       (7, 4, 3),
       (8, 5, 5),
       (9, 6, 7),
       (10, 7, 7),
       (11, 8, 8),
       (12, 9, 8),
       (13, 10, 7),
       (14, 11, 4),
       (15, 12, 6),
       (16, 13, 1),
       (17, 14, 10),
       (18, 15, 10),
       (19, 16, 10),
       (20, 17, 8),
       (21, 18, 9),
       (22, 20, 6);

INSERT INTO public.auditlogentity (id, auditlogtype, date, message, movieentityid, username)
VALUES (1, 'CREATE', NOW(), 'Created movie The Godfather', 1, 'admin'),
       (2, 'CREATE', NOW(), 'Created movie Pulp Fiction', 2, 'admin'),
       (3, 'CREATE', NOW(), 'Created movie Forrest Gump', 3, 'admin'),
       (4, 'CREATE', NOW(), 'Created movie The Shawshank Redemption', 4, 'admin'),
       (5, 'CREATE', NOW(), 'Created movie Fight Club', 5, 'admin'),
       (6, 'CREATE', NOW(), 'Created movie Inception', 6, 'admin'),
       (7, 'CREATE', NOW(), 'Created movie The Matrix', 7, 'admin'),
       (8, 'CREATE', NOW(), 'Created movie Goodfellas', 8, 'admin'),
       (9, 'CREATE', NOW(), 'Created movie The Dark Knight', 9, 'admin'),
       (10, 'CREATE', NOW(), 'Created movie Interstellar', 10, 'admin'),
       (11, 'CREATE', NOW(), 'Created movie Gladiator', 11, 'admin'),
       (12, 'CREATE', NOW(), 'Created movie The Silence of the Lambs', 12, 'admin'),
       (13, 'CREATE', NOW(), 'Created movie Saving Private Ryan', 13, 'admin'),
       (14, 'CREATE', NOW(), 'Created movie Se7en', 14, 'admin'),
       (15, 'CREATE', NOW(), 'Created movie The Prestige', 15, 'admin'),
       (16, 'CREATE', NOW(), 'Created movie Memento', 16, 'admin'),
       (17, 'CREATE', NOW(), 'Created movie The Departed', 17, 'admin'),
       (18, 'CREATE', NOW(), 'Created movie Whiplash', 18, 'admin'),
       (19, 'CREATE', NOW(), 'Created movie Parasite', 19, 'admin'),
       (20, 'CREATE', NOW(), 'Created movie Joker', 20, 'admin');

-- Reset sequences to max id in each table
-- Reset sequences to static values based on inserted rows
SELECT setval('public.movie_id_seq', 20, true);
SELECT setval('public.meta_id_seq', 20, true);
SELECT setval('public.tag_id_seq', 10, true);
SELECT setval('public.tag_movie_relation_id_seq', 20, true);
SELECT setval('public.auditlogentity_seq', 20, true);
SELECT setval('public.tagentity_seq', 10, true);
SELECT setval('public.tagmovierelation_seq', 20, true);
SELECT setval('public.hibernate_sequence', 20, true);