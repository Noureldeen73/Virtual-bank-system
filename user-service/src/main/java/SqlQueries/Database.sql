CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists users(
    id  uuid not null default uuid_generate_v4(),
    username varchar(50) not null unique,
    email varchar(100) not null unique,
    password_hash varchar(255) not null,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    created_at timestamptz DEFAULT current_timestamp,
    constraint users_pk primary key (id)
);