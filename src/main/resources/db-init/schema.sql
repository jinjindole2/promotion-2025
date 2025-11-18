CREATE DATABASE IF NOT EXISTS jinpro;
USE jinpro;

create table if not exists promotion (
    id bigint not null auto_increment comment '프로모션ID',
    title varchar(100) not null unique comment '프로모션명',
    reward_amount bigint check(reward_amount >= 0) comment '적립금액',
    left_join_count bigint not null check(left_join_count >= 0) comment '남은 참여가능 횟수',
    limit_join_count bigint not null check(limit_join_count >= 0) comment '참여가능 횟수',
    content text comment '프로모션 문구',
    image_url varchar(2083) comment '이미지 url',
    start_date date not null comment '시작일자',
    end_date date not null comment '종료일자',
    created_date datetime not null default current_timestamp comment '생성일자',
    last_modified_date datetime on update current_timestamp comment '수정일자',
    primary key (id),
    KEY idx_promo_01 (start_date, end_date, limit_join_count, reward_amount)
) comment '프로모션' engine=InnoDB;

/*CREATE INDEX idx_promo_01 ON promotion(start_date, end_date, limit_join_count, reward_amount DESC);*/

create table if not exists promotion_join_type (
    id bigint not null auto_increment comment 'ID',
    promo_id bigint not null comment '프로모션ID',
    join_type varchar(20) not null comment '참가자격 타입',
    limit_dup_join_count bigint comment '중복 참여가능 횟수',
    created_date datetime not null default current_timestamp comment '생성일자',
    last_modified_date datetime on update current_timestamp comment '수정일자',
    primary key (id),
    unique key uidx_join_type_01 (promo_id, join_type)
) comment '프로모션 참가 타입' engine=InnoDB;

/*CREATE UNIQUE INDEX uidx_join_type_01 ON promotion_join_type(promo_id, join_type);*/

create table if not exists leading_promotion (
    id bigint not null auto_increment comment 'ID',
    lead_promo_id bigint not null comment '선행 프로모션ID',
    trail_promo_id bigint not null comment '후행 프로모션ID',
    created_date datetime not null default current_timestamp comment '생성일자',
    last_modified_date datetime on update current_timestamp comment '수정일자',
    primary key (id),
    unique key uidx_lead_promo_01 (trail_promo_id, lead_promo_id)
) comment '선행 프로모션' engine=InnoDB;

/*CREATE UNIQUE INDEX uidx_lead_promo_01 ON leading_promotion(trail_promo_id, lead_promo_id);*/

create table if not exists promotion_join_history (
    id bigint not null auto_increment comment '프로모션 참여 이력 ID',
    promo_id bigint not null comment '프로모션ID',
    user_id bigint not null comment '유저ID',
    join_date date not null comment '참여일자',
    reward_amount bigint check(reward_amount >= 0) comment '적립금액',
    created_date datetime not null default current_timestamp comment '생성일자',
    last_modified_date datetime on update current_timestamp comment '수정일자',
    primary key (id),
    unique key uidx_promo_usr_01 (promo_id, user_id, join_date),
    key idx_promo_join_hist_02 (user_id, join_date)
) comment '프로모션 참여 이력' engine=InnoDB;

/*CREATE UNIQUE INDEX uidx_promo_usr_01 ON promotion_users(promo_id, user_id, join_date);
CREATE INDEX idx_promo_usr_02 ON promotion_users(user_id, join_date);*/


