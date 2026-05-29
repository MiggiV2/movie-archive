CREATE TABLE public.auditlogentity
(
    id            bigint NOT NULL,
    auditlogtype  character varying(255),
    date          timestamp(6) without time zone,
    message       character varying(255),
    movieentityid bigint NOT NULL,
    username      character varying(255)
);

--
-- Name: auditlogentity_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.auditlogentity_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: meta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.meta_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: movie_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.movie_id_seq
    START WITH 364
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: movieentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.movieentity
(
    id           bigint  NOT NULL,
    block        character varying(255),
    name         character varying(255),
    type         character varying(255),
    uuid         character varying(255),
    wikiurl      character varying(255),
    year         integer NOT NULL,
    originalname character varying(255)
);

--
-- Name: moviemetadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.moviemetadata
(
    id             bigint           NOT NULL,
    genres         character varying(255)[],
    imdbid         character varying(255),
    primaryimage   character varying(255),
    rating         double precision NOT NULL,
    runtime        integer          NOT NULL,
    year           integer          NOT NULL,
    movieentity_id bigint
);

--
-- Name: tag_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_id_seq
    START WITH 12
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: tag_movie_relation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_movie_relation_id_seq
    START WITH 17
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tagentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tagentity
(
    id      bigint NOT NULL,
    created timestamp(6) without time zone,
    name    character varying(255)
);


--
-- Name: tagentity_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tagentity_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: tagmovierelation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tagmovierelation
(
    id       bigint NOT NULL,
    movie_id bigint,
    tag_id   bigint
);


--
-- Name: tagmovierelation_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tagmovierelation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: auditlogentity auditlogentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditlogentity
    ADD CONSTRAINT auditlogentity_pkey PRIMARY KEY (id);


--
-- Name: movieentity movieentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movieentity
    ADD CONSTRAINT movieentity_pkey PRIMARY KEY (id);


--
-- Name: moviemetadata moviemetadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviemetadata
    ADD CONSTRAINT moviemetadata_pkey PRIMARY KEY (id);


--
-- Name: tagentity tagentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tagentity
    ADD CONSTRAINT tagentity_pkey PRIMARY KEY (id);


--
-- Name: tagmovierelation tagmovierelation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tagmovierelation
    ADD CONSTRAINT tagmovierelation_pkey PRIMARY KEY (id);


--
-- Name: moviemetadata ukcrba9kbtem647uq1eq7bpy7qj; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviemetadata
    ADD CONSTRAINT ukcrba9kbtem647uq1eq7bpy7qj UNIQUE (movieentity_id);


--
-- Name: moviemetadata fk7skua1wk9byjjo6a08hjd7t6s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviemetadata
    ADD CONSTRAINT fk7skua1wk9byjjo6a08hjd7t6s FOREIGN KEY (movieentity_id) REFERENCES public.movieentity (id);


--
-- Name: tagmovierelation fkksy1r1us808caijoq0vww7ac0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tagmovierelation
    ADD CONSTRAINT fkksy1r1us808caijoq0vww7ac0 FOREIGN KEY (movie_id) REFERENCES public.movieentity (id);


--
-- Name: tagmovierelation fktibkodxlrhbarj9m41mmg6hlx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tagmovierelation
    ADD CONSTRAINT fktibkodxlrhbarj9m41mmg6hlx FOREIGN KEY (tag_id) REFERENCES public.tagentity (id);