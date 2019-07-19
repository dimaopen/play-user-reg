-- Users

-- !Ups

create table portal_user
(
    id             serial,
    username       varchar(255) not null,
    hashedPassword bytea        not null,
    blocked        boolean      not null,
    constraint portaluser_username_unique unique (username),
    constraint pk_portaluser primary key (id)
);

-- !Downs

DROP TABLE portal_user;