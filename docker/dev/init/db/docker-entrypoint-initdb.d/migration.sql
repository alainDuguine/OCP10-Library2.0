ALTER TABLE public.library_user
ADD COLUMN notification boolean;

UPDATE public.library_user
SET notification = true;

CREATE TABLE public.reservation (
    id bigint NOT NULL,
    current_status character varying(255),
    current_status_date timestamp without time zone,
    book_id bigint NOT NULL,
    user_id bigint NOT NULL
);

ALTER TABLE public.reservation OWNER TO usr_library;

CREATE SEQUENCE public.reservation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.reservation_id_seq OWNER TO usr_library;

ALTER SEQUENCE public.reservation_id_seq OWNED BY public.reservation.id;

CREATE TABLE public.reservation_status (
    id bigint NOT NULL,
    date timestamp without time zone,
    status character varying(255) NOT NULL,
    reservation_id bigint
);

ALTER TABLE public.reservation_status OWNER TO usr_library;

CREATE SEQUENCE public.reservation_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.reservation_status_id_seq OWNER TO usr_library;

ALTER SEQUENCE public.reservation_status_id_seq OWNED BY public.reservation_status.id;

ALTER TABLE ONLY public.reservation ALTER COLUMN id SET DEFAULT nextval('public.reservation_id_seq'::regclass);

ALTER TABLE ONLY public.reservation_status ALTER COLUMN id SET DEFAULT nextval('public.reservation_status_id_seq'::regclass);

ALTER TABLE ONLY public.reservation
ADD CONSTRAINT reservation_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.reservation_status
ADD CONSTRAINT reservation_status_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.reservation
ADD CONSTRAINT fk9rp3xsj4p96vx5p73ucfy3bey FOREIGN KEY (user_id) REFERENCES public.library_user(id);

ALTER TABLE ONLY public.reservation_status
ADD CONSTRAINT fkdtmvyatj3yl2hf6ju9emb3x1l FOREIGN KEY (reservation_id) REFERENCES public.reservation(id);

ALTER TABLE ONLY public.reservation
ADD CONSTRAINT fkirxtcw4s6lhwi6l9ocrk6bjfy FOREIGN KEY (book_id) REFERENCES public.book(id);

