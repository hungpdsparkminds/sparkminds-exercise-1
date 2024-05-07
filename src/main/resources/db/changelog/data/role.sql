-- liquibase formatted sql

-- changeset hungpd:data-1
insert into role (name, status) values ('Admin', true);
insert into role (name, status) values ('User', true);
