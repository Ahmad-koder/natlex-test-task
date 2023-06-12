create schema natlex;

create table natlex.section
(
    id   uuid not null
        constraint section_pk
            primary key,
    name varchar(255)
);

create table natlex.geological_class
(
    id         uuid not null
        constraint geological_class_pk
            primary key,
    name       varchar(255),
    code       varchar(255),
    section_id uuid
        constraint geological_class_section_fk
            references section
        constraint geological_class_fk
            references section
);

create table natlex.task
(
    id        uuid not null
        constraint task_status_pk
            primary key,
    status    varchar(255),
    task_type varchar(255)
);

create table natlex.file
(
    id           uuid not null
        constraint files_pk
            primary key,
    file         bytea,
    task_id      uuid
        constraint files_task_status_id_fk
            references task,
    content_type varchar(255),
    name         varchar(255)
);


