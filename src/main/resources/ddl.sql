DROP DATABASE IF EXISTS share_me;

CREATE DATABASE share_me;

USE share_me;

CREATE TABLE `accounts`
(
    `account_id`                BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `kakao_oauth_id`            BIGINT UNIQUE      NOT NULL,
    `name`                      VARCHAR(255)       NULL,
    `email`                     VARCHAR(320)       NULL,
    `initial_role_granted_flag` boolean            NOT NULL,
    `linkedin_url`              VARCHAR(255)       NULL,
    `github_url`                VARCHAR(255)       NULL,
    `instagram_url`             VARCHAR(255)       NULL,
    `created_at`                timestamp          NOT NULL,
    `updated_at`                timestamp          NOT NULL
);

CREATE TABLE `events`
(
    `event_id`   BINARY(16) PRIMARY KEY NOT NULL,
    `created_at` timestamp              NOT NULL,
    `updated_at` timestamp              NOT NULL
);

CREATE TABLE `profiles`
(
    `profile_id`          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `event_id`            BINARY(16)         NOT NULL,
    `account_id`          BIGINT             NOT NULL,
    `pin_number`          INT                NOT NULL,
    `icon_number`         INT                NOT NULL,
    `introduce`           VARCHAR(255)       NULL,
    `proudest_experience` VARCHAR(255)       NULL,
    `tough_experience`    VARCHAR(255)       NULL,
    `created_at`          timestamp          NOT NULL,
    `updated_at`          timestamp          NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
    CONSTRAINT fk_account FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    UNIQUE unq_event_account (`event_id`, `account_id`),
    INDEX idx_event_pin_number (`event_id`, `pin_number`),
    INDEX idx_event_account (`event_id`, `account_id`)
);

CREATE TABLE `relations`
(
    `first_profile_id`  BIGINT    NOT NULL,
    `second_profile_id` BIGINT    NOT NULL,
    `created_at`        timestamp NOT NULL,
    `updated_at`        timestamp NOT NULL,
    PRIMARY KEY (first_profile_id, second_profile_id),
    CONSTRAINT fk_first_profile FOREIGN KEY (`first_profile_id`) REFERENCES `profiles` (`profile_id`),
    CONSTRAINT fk_second_profile FOREIGN KEY (`second_profile_id`) REFERENCES `profiles` (`profile_id`)
);

CREATE TABLE `feedbacks`
(
    `feedback_id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `feedback`    VARCHAR(255)       NULL,
    `created_at`  timestamp          NOT NULL,
    `updated_at`  timestamp          NOT NULL
);
