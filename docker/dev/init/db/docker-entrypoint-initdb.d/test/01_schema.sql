--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 11.6

-- Started on 2020-03-16 14:26:02

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
-- TOC entry 196 (class 1259 OID 118135)
-- Name: author; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.author (
    id bigint NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30)
);


ALTER TABLE public.author OWNER TO usr_library;

--
-- TOC entry 197 (class 1259 OID 118138)
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.author_id_seq OWNER TO usr_library;

--
-- TOC entry 2902 (class 0 OID 0)
-- Dependencies: 197
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.author_id_seq OWNED BY public.author.id;


--
-- TOC entry 198 (class 1259 OID 118140)
-- Name: book; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.book (
    id bigint NOT NULL,
    isbn character varying(255),
    title character varying(255) NOT NULL
);


ALTER TABLE public.book OWNER TO usr_library;

--
-- TOC entry 199 (class 1259 OID 118146)
-- Name: book_author; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.book_author (
    book_id bigint NOT NULL,
    author_id bigint NOT NULL
);


ALTER TABLE public.book_author OWNER TO usr_library;

--
-- TOC entry 200 (class 1259 OID 118149)
-- Name: book_copy; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.book_copy (
    id bigint NOT NULL,
    available boolean NOT NULL,
    barcode character varying(255),
    editor character varying(255),
    book_id bigint
);


ALTER TABLE public.book_copy OWNER TO usr_library;

--
-- TOC entry 201 (class 1259 OID 118155)
-- Name: book_copy_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.book_copy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.book_copy_id_seq OWNER TO usr_library;

--
-- TOC entry 2903 (class 0 OID 0)
-- Dependencies: 201
-- Name: book_copy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.book_copy_id_seq OWNED BY public.book_copy.id;


--
-- TOC entry 202 (class 1259 OID 118157)
-- Name: book_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.book_id_seq OWNER TO usr_library;

--
-- TOC entry 2904 (class 0 OID 0)
-- Dependencies: 202
-- Name: book_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;


--
-- TOC entry 203 (class 1259 OID 118159)
-- Name: library_user; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.library_user (
    id bigint NOT NULL,
    active integer NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    password character varying(255) NOT NULL,
    roles character varying(255),
    notification boolean
);


ALTER TABLE public.library_user OWNER TO usr_library;

--
-- TOC entry 204 (class 1259 OID 118165)
-- Name: library_user_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.library_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.library_user_id_seq OWNER TO usr_library;

--
-- TOC entry 2905 (class 0 OID 0)
-- Dependencies: 204
-- Name: library_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.library_user_id_seq OWNED BY public.library_user.id;


--
-- TOC entry 205 (class 1259 OID 118167)
-- Name: loan; Type: TABLE; Schema: public; Owner: usr_library
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


ALTER TABLE public.loan OWNER TO usr_library;

--
-- TOC entry 206 (class 1259 OID 118170)
-- Name: loan_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.loan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.loan_id_seq OWNER TO usr_library;

--
-- TOC entry 2906 (class 0 OID 0)
-- Dependencies: 206
-- Name: loan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.loan_id_seq OWNED BY public.loan.id;


--
-- TOC entry 207 (class 1259 OID 118172)
-- Name: loan_status; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.loan_status (
    loan_id bigint NOT NULL,
    status_id bigint NOT NULL,
    date timestamp without time zone
);


ALTER TABLE public.loan_status OWNER TO usr_library;

--
-- TOC entry 208 (class 1259 OID 118175)
-- Name: reservation; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.reservation (
    id bigint NOT NULL,
    current_status character varying(255),
    current_status_date timestamp without time zone,
    book_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.reservation OWNER TO usr_library;

--
-- TOC entry 209 (class 1259 OID 118178)
-- Name: reservation_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.reservation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reservation_id_seq OWNER TO usr_library;

--
-- TOC entry 2907 (class 0 OID 0)
-- Dependencies: 209
-- Name: reservation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.reservation_id_seq OWNED BY public.reservation.id;


--
-- TOC entry 210 (class 1259 OID 118180)
-- Name: reservation_status; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.reservation_status (
    id bigint NOT NULL,
    date timestamp without time zone,
    status character varying(255) NOT NULL,
    reservation_id bigint
);


ALTER TABLE public.reservation_status OWNER TO usr_library;

--
-- TOC entry 211 (class 1259 OID 118183)
-- Name: reservation_status_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.reservation_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reservation_status_id_seq OWNER TO usr_library;

--
-- TOC entry 2908 (class 0 OID 0)
-- Dependencies: 211
-- Name: reservation_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.reservation_status_id_seq OWNED BY public.reservation_status.id;


--
-- TOC entry 212 (class 1259 OID 118185)
-- Name: status; Type: TABLE; Schema: public; Owner: usr_library
--

CREATE TABLE public.status (
    id bigint NOT NULL,
    designation character varying(255)
);


ALTER TABLE public.status OWNER TO usr_library;

--
-- TOC entry 213 (class 1259 OID 118188)
-- Name: status_id_seq; Type: SEQUENCE; Schema: public; Owner: usr_library
--

CREATE SEQUENCE public.status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status_id_seq OWNER TO usr_library;

--
-- TOC entry 2909 (class 0 OID 0)
-- Dependencies: 213
-- Name: status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: usr_library
--

ALTER SEQUENCE public.status_id_seq OWNED BY public.status.id;


--
-- TOC entry 2738 (class 2604 OID 118190)
-- Name: author id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.author ALTER COLUMN id SET DEFAULT nextval('public.author_id_seq'::regclass);


--
-- TOC entry 2739 (class 2604 OID 118191)
-- Name: book id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book ALTER COLUMN id SET DEFAULT nextval('public.book_id_seq'::regclass);


--
-- TOC entry 2740 (class 2604 OID 118192)
-- Name: book_copy id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_copy ALTER COLUMN id SET DEFAULT nextval('public.book_copy_id_seq'::regclass);


--
-- TOC entry 2741 (class 2604 OID 118193)
-- Name: library_user id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.library_user ALTER COLUMN id SET DEFAULT nextval('public.library_user_id_seq'::regclass);


--
-- TOC entry 2742 (class 2604 OID 118194)
-- Name: loan id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan ALTER COLUMN id SET DEFAULT nextval('public.loan_id_seq'::regclass);


--
-- TOC entry 2743 (class 2604 OID 118195)
-- Name: reservation id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation ALTER COLUMN id SET DEFAULT nextval('public.reservation_id_seq'::regclass);


--
-- TOC entry 2744 (class 2604 OID 118196)
-- Name: reservation_status id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation_status ALTER COLUMN id SET DEFAULT nextval('public.reservation_status_id_seq'::regclass);


--
-- TOC entry 2745 (class 2604 OID 118197)
-- Name: status id; Type: DEFAULT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status_id_seq'::regclass);


--
-- TOC entry 2747 (class 2606 OID 118199)
-- Name: author author_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 2751 (class 2606 OID 118201)
-- Name: book_author book_author_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT book_author_pkey PRIMARY KEY (book_id, author_id);


--
-- TOC entry 2753 (class 2606 OID 118203)
-- Name: book_copy book_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_copy
    ADD CONSTRAINT book_copy_pkey PRIMARY KEY (id);


--
-- TOC entry 2749 (class 2606 OID 118205)
-- Name: book book_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (id);


--
-- TOC entry 2755 (class 2606 OID 118207)
-- Name: library_user library_user_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.library_user
    ADD CONSTRAINT library_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2757 (class 2606 OID 118209)
-- Name: loan loan_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT loan_pkey PRIMARY KEY (id);


--
-- TOC entry 2759 (class 2606 OID 118211)
-- Name: loan_status loan_status_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT loan_status_pkey PRIMARY KEY (loan_id, status_id);


--
-- TOC entry 2761 (class 2606 OID 118213)
-- Name: reservation reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation
    ADD CONSTRAINT reservation_pkey PRIMARY KEY (id);


--
-- TOC entry 2763 (class 2606 OID 118215)
-- Name: reservation_status reservation_status_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation_status
    ADD CONSTRAINT reservation_status_pkey PRIMARY KEY (id);


--
-- TOC entry 2765 (class 2606 OID 118217)
-- Name: status status_pkey; Type: CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- TOC entry 2771 (class 2606 OID 118218)
-- Name: loan_status fk8t5ghbx6nh0p51id0y0ugcx4w; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT fk8t5ghbx6nh0p51id0y0ugcx4w FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 2773 (class 2606 OID 118223)
-- Name: reservation fk9rp3xsj4p96vx5p73ucfy3bey; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation
    ADD CONSTRAINT fk9rp3xsj4p96vx5p73ucfy3bey FOREIGN KEY (user_id) REFERENCES public.library_user(id);


--
-- TOC entry 2766 (class 2606 OID 118228)
-- Name: book_author fkbjqhp85wjv8vpr0beygh6jsgo; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fkbjqhp85wjv8vpr0beygh6jsgo FOREIGN KEY (author_id) REFERENCES public.author(id);


--
-- TOC entry 2775 (class 2606 OID 118233)
-- Name: reservation_status fkdtmvyatj3yl2hf6ju9emb3x1l; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation_status
    ADD CONSTRAINT fkdtmvyatj3yl2hf6ju9emb3x1l FOREIGN KEY (reservation_id) REFERENCES public.reservation(id);


--
-- TOC entry 2769 (class 2606 OID 118238)
-- Name: loan fkhq4lfr6shkjcbqvn0if6sqtwf; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT fkhq4lfr6shkjcbqvn0if6sqtwf FOREIGN KEY (book_copy_id) REFERENCES public.book_copy(id);


--
-- TOC entry 2767 (class 2606 OID 118243)
-- Name: book_author fkhwgu59n9o80xv75plf9ggj7xn; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fkhwgu59n9o80xv75plf9ggj7xn FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- TOC entry 2774 (class 2606 OID 118248)
-- Name: reservation fkirxtcw4s6lhwi6l9ocrk6bjfy; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.reservation
    ADD CONSTRAINT fkirxtcw4s6lhwi6l9ocrk6bjfy FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- TOC entry 2770 (class 2606 OID 118253)
-- Name: loan fklt1cbgnkcolt3440fhbik263g; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan
    ADD CONSTRAINT fklt1cbgnkcolt3440fhbik263g FOREIGN KEY (user_id) REFERENCES public.library_user(id);


--
-- TOC entry 2768 (class 2606 OID 118258)
-- Name: book_copy fkpqftp65hd66ae8wsx7pp2cxcs; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.book_copy
    ADD CONSTRAINT fkpqftp65hd66ae8wsx7pp2cxcs FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- TOC entry 2772 (class 2606 OID 118263)
-- Name: loan_status fkpxt59objpb935dbp8qn1ycvg5; Type: FK CONSTRAINT; Schema: public; Owner: usr_library
--

ALTER TABLE ONLY public.loan_status
    ADD CONSTRAINT fkpxt59objpb935dbp8qn1ycvg5 FOREIGN KEY (loan_id) REFERENCES public.loan(id);


-- Completed on 2020-03-16 14:26:02

--
-- PostgreSQL database dump complete
--

