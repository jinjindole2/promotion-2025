CREATE DATABASE IF NOT EXISTS daily_promotion;
USE daily_promotion;

create table if not exists promotion (
    id bigint not null auto_increment,
    title varchar(100) not null unique,
    reward_amount bigint check(reward_amount >= 0),
    left_join_count bigint not null check(left_join_count >= 0),
    limit_join_count bigint not null check(limit_join_count >= 0),
    content text,
    image_url varchar(2083),
    start_date date not null,
    end_date date not null,
    created_date datetime not null default current_timestamp,
    last_modified_date datetime on update current_timestamp,
    primary key (id),
    KEY idx_promo_01 (start_date, end_date, limit_join_count, reward_amount)
);

create table if not exists promotion_join_type (
    id bigint not null auto_increment,
    promo_id bigint not null,
    join_type varchar(20) not null,
    limit_dup_join_count bigint,
    created_date datetime not null default current_timestamp,
    last_modified_date datetime on update current_timestamp,
    primary key (id),
    unique key uidx_join_type_01 (promo_id, join_type)
);


create table if not exists leading_promotion (
    id bigint not null auto_increment,
    lead_promo_id bigint not null,
    trail_promo_id bigint not null,
    created_date datetime not null default current_timestamp,
    last_modified_date datetime on update current_timestamp,
    primary key (id),
    unique key uidx_lead_promo_01 (trail_promo_id, lead_promo_id)
);


create table if not exists promotion_users (
    id bigint not null auto_increment,
    promo_id bigint not null,
    user_id bigint not null,
    join_date date not null,
    created_date datetime not null default current_timestamp,
    last_modified_date datetime on update current_timestamp,
    primary key (id),
    unique key uidx_promo_usr_01 (promo_id, user_id, join_date),
    key idx_promo_usr_02 (user_id, join_date)
);


