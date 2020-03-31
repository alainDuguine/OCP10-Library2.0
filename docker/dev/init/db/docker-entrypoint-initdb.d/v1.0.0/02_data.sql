--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5
-- Dumped by pg_dump version 11.5

-- Started on 2019-11-27 11:03:09

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2877 (class 0 OID 84013)
-- Dependencies: 197
-- Data for Name: author; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.author VALUES (1, 'Jules', 'Verne');
INSERT INTO public.author VALUES (2, 'Jules', 'Renard');
INSERT INTO public.author VALUES (3, 'Alexandre', 'Dumas');
INSERT INTO public.author VALUES (4, 'Victor', 'Hugo');
INSERT INTO public.author VALUES (5, 'Georges', 'Sand');


--
-- TOC entry 2879 (class 0 OID 84021)
-- Dependencies: 199
-- Data for Name: book; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.book VALUES (1, '3qy69XMvx0', 'Voyage au centre de la terre');
INSERT INTO public.book VALUES (2, 'iqzYVncflG', 'Le tour du monde en 80 jours');
INSERT INTO public.book VALUES (3, 'yoZO6Nrzzn', 'La mer');
INSERT INTO public.book VALUES (4, 'IErKgjUDPc', 'Histoires naturelles');
INSERT INTO public.book VALUES (5, 'ojnU3qnGNC', 'Les cloportes');
INSERT INTO public.book VALUES (6, 'PwYGyo3Ojs', 'Bucoliques');
INSERT INTO public.book VALUES (7, 'ZXJPqpDRU3', 'Valentine');
INSERT INTO public.book VALUES (8, 'ePQjpuyVnq', 'Légendes rustiques');
INSERT INTO public.book VALUES (9, 'h6V2jNT4hW', 'Les maitres mozaïstes');
INSERT INTO public.book VALUES (10, '2I6gVyl2ni', 'Le comte de Monte-Cristo');
INSERT INTO public.book VALUES (11, 'keIWOHkSwk', 'Les trois mousquetaires');
INSERT INTO public.book VALUES (12, '7GvjFYtbFx', 'La reine Margot');
INSERT INTO public.book VALUES (13, 'IYUiN3WGqB', 'Les misérables');
INSERT INTO public.book VALUES (14, 'LcMoaXTDFe', 'Notre Dame de Paris');
INSERT INTO public.book VALUES (15, 'HYTFW02rE2', 'Le dernier jour d''un condamné');


--
-- TOC entry 2880 (class 0 OID 84030)
-- Dependencies: 200
-- Data for Name: book_author; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.book_author VALUES (1, 1);
INSERT INTO public.book_author VALUES (2, 1);
INSERT INTO public.book_author VALUES (3, 1);
INSERT INTO public.book_author VALUES (4, 2);
INSERT INTO public.book_author VALUES (5, 2);
INSERT INTO public.book_author VALUES (6, 2);
INSERT INTO public.book_author VALUES (7, 5);
INSERT INTO public.book_author VALUES (8, 5);
INSERT INTO public.book_author VALUES (9, 5);
INSERT INTO public.book_author VALUES (10, 3);
INSERT INTO public.book_author VALUES (11, 3);
INSERT INTO public.book_author VALUES (12, 3);
INSERT INTO public.book_author VALUES (13, 4);
INSERT INTO public.book_author VALUES (14, 4);
INSERT INTO public.book_author VALUES (15, 4);


--
-- TOC entry 2882 (class 0 OID 84037)
-- Dependencies: 202
-- Data for Name: book_copy; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.book_copy VALUES (2, true, '3045336767775', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (3, true, '7410905739579', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (5, true, '5741971396896', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (6, true, '8925304091700', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (8, true, '6858888189365', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (10, true, '6555784760368', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (11, true, '5060140735653', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (13, true, '7003350879709', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (15, true, '8259812972627', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (16, true, '2818484830340', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (17, true, '9460451635745', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (18, true, '6964719502885', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (19, true, '2966826085768', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (20, true, '9797113726278', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (21, true, '4636230214019', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (22, true, '5852477938443', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (1, false, '6017610824791', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (4, false, '6231350057749', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (9, false, '9405320263956', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (12, false, '6822298411264', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (7, true, '6233319381632', 'Editions du seuil', 2);
INSERT INTO public.book_copy VALUES (14, false, '0284052612175', 'Editions du seuil', 3);
INSERT INTO public.book_copy VALUES (25, true, '3251570658370', 'Editions du seuil', 4);
INSERT INTO public.book_copy VALUES (26, true, '7018947660774', 'Editions du seuil', 4);
INSERT INTO public.book_copy VALUES (27, true, '4169523120541', 'Editions du seuil', 4);
INSERT INTO public.book_copy VALUES (28, true, '1389967640233', 'Editions du seuil', 4);
INSERT INTO public.book_copy VALUES (24, false, '6612941077484', 'Editions du seuil', 4);
INSERT INTO public.book_copy VALUES (23, false, '0866259673948', 'Editions du seuil', 1);
INSERT INTO public.book_copy VALUES (29, true, '8488660293073', 'Editions du seuil', 5);
INSERT INTO public.book_copy VALUES (30, true, '9307585654590', 'Editions du seuil', 5);
INSERT INTO public.book_copy VALUES (31, true, '3567926184622', 'Editions du seuil', 5);
INSERT INTO public.book_copy VALUES (32, true, '2174923138353', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (33, true, '4154404416559', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (34, true, '6654391316407', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (35, true, '9246184289500', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (36, true, '3363675091850', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (37, true, '2760543078410', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (38, true, '3903000892003', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (39, true, '1934376963850', 'Editions du seuil', 6);
INSERT INTO public.book_copy VALUES (40, true, '6058924221907', 'Editions du seuil', 7);
INSERT INTO public.book_copy VALUES (41, true, '1086264897664', 'Editions du seuil', 8);
INSERT INTO public.book_copy VALUES (42, true, '1378713012603', 'Editions du seuil', 8);
INSERT INTO public.book_copy VALUES (43, true, '2829950545193', 'Editions du seuil', 8);
INSERT INTO public.book_copy VALUES (44, true, '3125709401099', 'Editions du seuil', 8);
INSERT INTO public.book_copy VALUES (45, true, '4776593098393', 'Editions du seuil', 9);
INSERT INTO public.book_copy VALUES (46, true, '5602600438784', 'Editions du seuil', 9);
INSERT INTO public.book_copy VALUES (47, true, '3518568271129', 'Editions du seuil', 10);
INSERT INTO public.book_copy VALUES (48, true, '6492737094954', 'Editions du seuil', 10);
INSERT INTO public.book_copy VALUES (49, true, '8721893290906', 'Editions du seuil', 10);
INSERT INTO public.book_copy VALUES (50, true, '9960774238000', 'Editions du seuil', 10);
INSERT INTO public.book_copy VALUES (51, true, '8517394829944', 'Editions du seuil', 11);
INSERT INTO public.book_copy VALUES (52, true, '5631480824442', 'Editions du seuil', 11);
INSERT INTO public.book_copy VALUES (53, true, '9813806343989', 'Editions du seuil', 11);
INSERT INTO public.book_copy VALUES (54, true, '6471352665775', 'Editions du seuil', 11);
INSERT INTO public.book_copy VALUES (55, true, '4798405094448', 'Editions du seuil', 11);
INSERT INTO public.book_copy VALUES (56, true, '7440600584463', 'Editions du seuil', 12);
INSERT INTO public.book_copy VALUES (57, true, '6528229152666', 'Editions du seuil', 12);
INSERT INTO public.book_copy VALUES (58, true, '8255250682738', 'Editions du seuil', 13);
INSERT INTO public.book_copy VALUES (59, true, '3579281178309', 'Editions du seuil', 13);
INSERT INTO public.book_copy VALUES (60, true, '6379499427005', 'Editions du seuil', 13);
INSERT INTO public.book_copy VALUES (61, true, '7518320890899', 'Editions du seuil', 14);
INSERT INTO public.book_copy VALUES (62, true, '1817253887425', 'Editions du seuil', 14);
INSERT INTO public.book_copy VALUES (63, true, '0731521115131', 'Editions du seuil', 14);
INSERT INTO public.book_copy VALUES (64, true, '7817686572434', 'Editions du seuil', 14);
INSERT INTO public.book_copy VALUES (65, true, '7152615045017', 'Editions du seuil', 14);
INSERT INTO public.book_copy VALUES (66, true, '3332732333270', 'Editions du seuil', 15);
INSERT INTO public.book_copy VALUES (67, true, '6769204264991', 'Editions du seuil', 15);
INSERT INTO public.book_copy VALUES (68, true, '0956736388254', 'Editions du seuil', 15);


--
-- TOC entry 2884 (class 0 OID 84048)
-- Dependencies: 204
-- Data for Name: library_user; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.library_user VALUES (2, 1, 'admin@openlibrary.fr', 'Admin', 'Library', '$2a$10$3dY.jEP/Qpc86pTfDXbRze.C9Q7/LmUMP8mFj20Uh215RZ.kSXvuS', 'USER,ADMIN');
INSERT INTO public.library_user VALUES (1, 1, 'alain_duguine@hotmail.fr', 'Jean', 'Duguine', '$2a$10$cPiv1wG6yhgG0GnqYzMmh.iVjlBn8ebIZphRE7TWMJyocwik5kMqG', 'USER');
INSERT INTO public.library_user VALUES (3, 1, 'audrey.duguine@gmail.com', 'Audrey', 'Duguine', '$2a$10$74kAd8fc6.O7wA7eszEiF.0IBaZvPvfOcihNtCQB/b37aNpCNiEpC', 'USER');
INSERT INTO public.library_user VALUES (4, 1, 'agnes.duguine@gmail.com', 'Agnès', 'Duguine', '$2a$10$.pXWR1Q5/0RsVSkfwg/Wae/ztEQxxxOpmtvYzr/HPmFNOZ8fWvVj6', 'USER');


--
-- TOC entry 2886 (class 0 OID 84059)
-- Dependencies: 206
-- Data for Name: loan; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.loan VALUES (3, 'LATE', '2019-11-21 16:43:53.647', '2019-11-21', '2019-11-21', 4, 1);
INSERT INTO public.loan VALUES (4, 'PROLONGED', '2019-11-21 16:41:24.205', '2019-12-21', '2019-11-21', 9, 1);
INSERT INTO public.loan VALUES (2, 'RETURNED', '2019-11-21 16:43:28.943', '2019-10-21', '2019-11-21', 7, 1);
INSERT INTO public.loan VALUES (1, 'LATE', '2019-11-22 15:01:29.477', '2019-11-21', '2019-11-21', 1, 1);
INSERT INTO public.loan VALUES (5, 'LATE', '2019-11-22 15:01:51.131', '2019-11-21', '2019-11-21', 12, 1);
INSERT INTO public.loan VALUES (6, 'PROLONGED', '2019-11-26 08:16:12.168', '2020-01-20', '2019-11-25', 14, 1);
INSERT INTO public.loan VALUES (7, 'LOANED', '2019-11-27 09:29:26.558', '2019-12-25', '2019-11-27', 24, 1);
INSERT INTO public.loan VALUES (8, 'LOANED', '2019-11-27 09:30:22.722', '2019-12-25', '2019-11-27', 23, 1);


--
-- TOC entry 2889 (class 0 OID 84072)
-- Dependencies: 209
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.status VALUES (1, 'LOANED');
INSERT INTO public.status VALUES (2, 'RETURNED');
INSERT INTO public.status VALUES (3, 'PROLONGED');
INSERT INTO public.status VALUES (4, 'LATE');


--
-- TOC entry 2887 (class 0 OID 84065)
-- Dependencies: 207
-- Data for Name: loan_status; Type: TABLE DATA; Schema: public; Owner: adm_library
--

INSERT INTO public.loan_status VALUES (1, 1, '2019-11-21 16:38:23.035');
INSERT INTO public.loan_status VALUES (2, 1, '2019-11-21 16:38:48.383');
INSERT INTO public.loan_status VALUES (3, 1, '2019-11-21 16:38:51.12');
INSERT INTO public.loan_status VALUES (4, 1, '2019-11-21 16:38:56.331');
INSERT INTO public.loan_status VALUES (5, 1, '2019-11-21 16:38:59.702');
INSERT INTO public.loan_status VALUES (1, 3, '2019-11-21 16:41:08.426');
INSERT INTO public.loan_status VALUES (4, 3, '2019-11-21 16:41:24.205');
INSERT INTO public.loan_status VALUES (2, 2, '2019-11-21 16:43:28.943');
INSERT INTO public.loan_status VALUES (3, 4, '2019-11-21 16:43:53.647');
INSERT INTO public.loan_status VALUES (1, 4, '2019-11-22 15:01:29.477');
INSERT INTO public.loan_status VALUES (5, 4, '2019-11-22 15:01:51.131');
INSERT INTO public.loan_status VALUES (6, 1, '2019-11-25 09:42:46.821');
INSERT INTO public.loan_status VALUES (6, 3, '2019-11-26 08:16:12.168');
INSERT INTO public.loan_status VALUES (7, 1, '2019-11-27 09:29:26.558');
INSERT INTO public.loan_status VALUES (8, 1, '2019-11-27 09:30:23.509');


--
-- TOC entry 2895 (class 0 OID 0)
-- Dependencies: 196
-- Name: author_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.author_id_seq', 5, true);


--
-- TOC entry 2896 (class 0 OID 0)
-- Dependencies: 201
-- Name: book_copy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.book_copy_id_seq', 68, true);


--
-- TOC entry 2897 (class 0 OID 0)
-- Dependencies: 198
-- Name: book_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.book_id_seq', 15, true);


--
-- TOC entry 2898 (class 0 OID 0)
-- Dependencies: 203
-- Name: library_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.library_user_id_seq', 4, true);


--
-- TOC entry 2899 (class 0 OID 0)
-- Dependencies: 205
-- Name: loan_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.loan_id_seq', 8, true);


--
-- TOC entry 2900 (class 0 OID 0)
-- Dependencies: 208
-- Name: status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: adm_library
--

SELECT pg_catalog.setval('public.status_id_seq', 4, true);


-- Completed on 2019-11-27 11:03:09

--
-- PostgreSQL database dump complete
--

