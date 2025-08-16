CREATE TABLE public.auditlogentity (
    id bigint NOT NULL,
    auditlogtype character varying(255),
    date timestamp(6) without time zone,
    message character varying(255),
    movieentityid bigint NOT NULL,
    username character varying(255)
);

CREATE TABLE public.movieentity (
    id bigint NOT NULL,
    block character varying(255),
    name character varying(255),
    type character varying(255),
    uuid character varying(255),
    wikiurl character varying(255),
    year integer NOT NULL,
    originalname character varying(255)
);

CREATE TABLE public.moviemetadata (
    id bigint NOT NULL,
    genres character varying(255)[],
    imdbid character varying(255),
    primaryimage character varying(255),
    rating double precision NOT NULL,
    runtime integer NOT NULL,
    year integer NOT NULL,
    movieentity_id bigint
);

CREATE TABLE public.tagentity (
    id bigint NOT NULL,
    created timestamp(6) without time zone,
    name character varying(255)
);

CREATE TABLE public.tagmovierelation (
    id bigint NOT NULL,
    movie_id bigint,
    tag_id bigint
);