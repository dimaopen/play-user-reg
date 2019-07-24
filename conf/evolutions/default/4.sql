-- Tokens

-- !Ups

create table farm_area
(
    id         serial,
    name       varchar(255) not null,
    parent_id  int references farm_area (id),
    farmer_id  int references farmer (id),
    country_id int references country (id),
    constraint farm_area_username_unique unique (name, parent_id),
    constraint pk_farm_area primary key (id)
);

create table role
(
    id   serial,
    name varchar(255) not null,
    constraint pk_role primary key (id)
);

create table person
(
    id         serial,
    first_name varchar not null,
    last_name  varchar not null,
    birth_day  date    not null,
    origin_id  int     not null references country (id),
    constraint pk_person primary key (id)
);

INSERT INTO person
VALUES (default, 'Админ', 'Админов', '2000-01-01', 1);

create table employee
(
    id         serial,
    person_id  int     not null references person (id),
    farm_area_id  int     not null references farm_area (id),
    constraint pk_employee primary key (id)
);

create table employee_role
(
    employee_id     int not null references employee (id),
    role_id     int not null references role (id),
    constraint pk_employee_role primary key (employee_id, role_id)
);

alter table portal_user
 add column person_id int not null references person(id) default 1;
alter table portal_user
    alter column person_id drop default;

-- !Downs

alter table portal_user
    drop column person_id;

DROP TABLE employee_role;
DROP TABLE employee;
DROP TABLE person;
DROP TABLE role;
DROP TABLE farm_area;