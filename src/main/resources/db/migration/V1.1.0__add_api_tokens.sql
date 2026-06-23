--
-- Name: apitokenentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.apitokenentity
(
    id         bigint NOT NULL,
    name       character varying(255),
    tokenhash  character varying(255),
    principal  character varying(255),
    role       character varying(255),
    createdat  timestamp(6) without time zone,
    lastusedat timestamp(6) without time zone
);

--
-- Name: apitokenentity_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.apitokenentity_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: apitokenentity apitokenentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.apitokenentity
    ADD CONSTRAINT apitokenentity_pkey PRIMARY KEY (id);

--
-- Name: apitokenentity_tokenhash_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX apitokenentity_tokenhash_idx ON public.apitokenentity (tokenhash);
