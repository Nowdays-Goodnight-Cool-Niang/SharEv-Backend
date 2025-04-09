DROP DATABASE IF EXISTS share_me;

CREATE DATABASE share_me;

USE share_me;

CREATE TABLE `accounts`
(
    `account_id`        BINARY(16) PRIMARY KEY NOT NULL,
    `kakao_oauth_id`    BIGINT UNIQUE          NOT NULL,
    `name`              VARCHAR(255)           NULL,
    `email`             VARCHAR(320)           NULL,
    `linkedin_url`      VARCHAR(255)           NULL,
    `github_url`        VARCHAR(255)           NULL,
    `instagram_url`     VARCHAR(255)           NULL,
    `team_name`         VARCHAR(255)           NULL,
    `position`          VARCHAR(255)           NULL,
    `introduction_text` VARCHAR(255)           NULL,
    `created_at`        timestamp              not NULL,
    `updated_at`        timestamp              not NULL
);

CREATE TABLE `social_dex`
(
    `first_account_id`  BINARY(16) NOT NULL,
    `second_account_id` BINARY(16) NOT NULL,
    `created_at`        timestamp  not NULL,
    `updated_at`        timestamp  not NULL,
    PRIMARY KEY (first_account_id, second_account_id),
    CONSTRAINT fk_first_account FOREIGN KEY (`first_account_id`) REFERENCES `accounts` (`account_id`),
    CONSTRAINT fk_second_account FOREIGN KEY (`second_account_id`) REFERENCES `accounts` (`account_id`)
);
