-- Tokens

-- !Ups

create table token
(
    token_value varchar(200) not null,
    issued      timestamp with time zone not null,
    valid_until timestamp with time zone not null,
    user_id     int not null,
    constraint pk_token primary key (token_value)
);

alter table token
    add constraint fk_token_user_id
        foreign key (user_id) references portal_user (id) on delete restrict on update restrict;


-- !Downs

alter table if exists token
    drop constraint if exists fk_token_user_id;

DROP TABLE token;