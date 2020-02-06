--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5
-- Dumped by pg_dump version 11.5

-- Started on 2019-12-13 11:14:01

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

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 197 (class 1259 OID 84013)
-- Name: author; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.author (
    id bigint NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30)
);


--
-- TOC entry 196 (class 1259 OID 84011)
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2881 (class 0 OID 0)
-- Dependencies: 196
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.author_id_seq OWNED BY public.author.id;


--
-- TOC entry 199 (class 1259 OID 84021)
-- Name: book; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.book (
    id bigint NOT NULL,
    isbn character varying(255),
    title character varying(255) NOT NULL
);


--
-- TOC entry 200 (class 1259 OID 84030)
-- Name: book_author; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.book_author (
    book_id bigint NOT NULL,
    author_id bigint NOT NULL
);


--
-- TOC entry 202 (class 1259 OID 84037)
-- Name: book_copy; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.book_copy (
    id bigint NOT NULL,
    available boolean NOT NULL,
    barcode character varying(255),
    editor character varying(255),
    book_id bigint
);


--
-- TOC entry 201 (class 1259 OID 84035)
-- Name: book_copy_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.book_copy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2882 (class 0 OID 0)
-- Dependencies: 201
-- Name: book_copy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.book_copy_id_seq OWNED BY public.book_copy.id;


--
-- TOC entry 198 (class 1259 OID 84019)
-- Name: book_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2883 (class 0 OID 0)
-- Dependencies: 198
-- Name: book_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;


--
-- TOC entry 204 (class 1259 OID 84048)
-- Name: library_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.library_user (
    id bigint NOT NULL,
    active integer NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    password character varying(255) NOT NULL,
    roles character varying(255)
);


--
-- TOC entry 203 (class 1259 OID 84046)
-- Name: library_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.library_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2884 (class 0 OID 0)
-- Dependencies: 203
-- Name: library_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.library_user_id_seq OWNED BY public.library_user.id;


--
-- TOC entry 206 (class 1259 OID 84059)
-- Name: loan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan (
    id bigint NOT NULL,
    current_status character varying(255),
    current_status_date timestamp without time zone,
    end_date date,
    start_date date NOT NULL,
    book_copy_id bigint,
    user_id bigint
);


--
-- TOC entry 205 (class 1259 OID 84057)
-- Name: loan_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.loan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2885 (class 0 OID 0)
-- Dependencies: 205
-- Name: loan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.loan_id_seq OWNED BY public.loan.id;


--
-- TOC entry 207 (class 1259 OID 84065)
-- Name: loan_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_status (
    loan_id bigint NOT NULL,
    status_id bigint NOT NULL,
    date timestamp without time zone
);


--
-- TOC entry 209 (class 1259 OID 84072)
-- Name: status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.status (
    id bigint NOT NULL,
    designation character varying(255)
);


--
-- TOC entry 208 (class 1259 OID 84070)
-- Name: status_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2886 (class 0 OID 0)
-- Dependencies: 208
-- Name: status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.status_id_seq OWNED BY public.status.id;


--
-- TOC entry 2726 (class 2604 OID 84016)
-- Name: author id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.author ALTER COLUMN id SET DEFAULT nextval('public.author_id_seq'::regclass);


--
-- TOC entry 2727 (class 2604 OID 84024)
-- Name: book id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book ALTER COLUMN id SET DEFAULT nextval('public.book_id_seq'::regclass);


--
-- TOC entry 2728 (class 2604 OID 84040)
-- Name: book_copy id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_copy ALTER COLUMN id SET DEFAULT nextval('public.book_copy_id_seq'::regclass);


--
-- TOC entry 2729 (class 2604 OID 84051)
-- Name: library_user id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.library_user ALTER COLUMN id SET DEFAULT nextval('public.library_user_id_seq'::regclass);


--
-- TOC entry 2730 (class 2604 OID 84062)
-- Name: loan id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan ALTER COLUMN id SET DEFAULT nextval('public.loan_id_seq'::regclass);


--
-- TOC entry 2731 (class 2604 OID 84075)
-- Name: status id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status_id_seq'::regclass);


--
-- TOC entry 2733 (class 2606 OID 84018)
-- Name: author author_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 2737 (class 2606 OID 84034)
-- Name: book_author book_author_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT book_author_pkey PRIMARY KEY (book_id, author_id);


--
-- TOC entry 2739 (class 2606 OID 84045)
-- Name: book_copy book_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_copy
    ADD CONSTRAINT book_copy_pkey PRIMARY KEY (id);


--
-- TOC entry 2735 (class 2606 OID 84029)
-- Name: book book_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (id);


--
-- TOC entry 2741 (class 2606 OID 84056)
-- Name: library_user library_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.library_user
    ADD CONSTRAINT library_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2743 (class 2606 OID 84064)
-- Name: loan loan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT loan_pkey PRIMARY KEY (id);


--
-- TOC entry 2745 (class 2606 OID 84069)
-- Name: loan_status loan_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT loan_status_pkey PRIMARY KEY (loan_id, status_id);


--
-- TOC entry 2747 (class 2606 OID 84077)
-- Name: status status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- TOC entry 2754 (class 2606 OID 84108)
-- Name: loan_status fk8t5ghbx6nh0p51id0y0ugcx4w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT fk8t5ghbx6nh0p51id0y0ugcx4w FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 2748 (class 2606 OID 84078)
-- Name: book_author fkbjqhp85wjv8vpr0beygh6jsgo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fkbjqhp85wjv8vpr0beygh6jsgo FOREIGN KEY (author_id) REFERENCES public.author(id);


--
-- TOC entry 2751 (class 2606 OID 84093)
-- Name: loan fkhq4lfr6shkjcbqvn0if6sqtwf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT fkhq4lfr6shkjcbqvn0if6sqtwf FOREIGN KEY (book_copy_id) REFERENCES public.book_copy(id);


--
-- TOC entry 2749 (class 2606 OID 84083)
-- Name: book_author fkhwgu59n9o80xv75plf9ggj7xn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fkhwgu59n9o80xv75plf9ggj7xn FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- TOC entry 2752 (class 2606 OID 84098)
-- Name: loan fklt1cbgnkcolt3440fhbik263g; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT fklt1cbgnkcolt3440fhbik263g FOREIGN KEY (user_id) REFERENCES public.library_user(id);


--
-- TOC entry 2750 (class 2606 OID 84088)
-- Name: book_copy fkpqftp65hd66ae8wsx7pp2cxcs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_copy
    ADD CONSTRAINT fkpqftp65hd66ae8wsx7pp2cxcs FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- TOC entry 2753 (class 2606 OID 84103)
-- Name: loan_status fkpxt59objpb935dbp8qn1ycvg5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT fkpxt59objpb935dbp8qn1ycvg5 FOREIGN KEY (loan_id) REFERENCES public.loan(id);


-- Completed on 2019-12-13 11:14:01

--
-- PostgreSQL database dump complete
--

