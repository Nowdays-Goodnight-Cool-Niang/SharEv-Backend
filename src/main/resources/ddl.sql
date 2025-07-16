DROP DATABASE IF EXISTS share_me;

CREATE DATABASE share_me;

USE share_me;

CREATE TABLE `accounts`
(
    `account_id`     BIGINT PRIMARY KEY NOT NULL,
    `kakao_oauth_id` BIGINT UNIQUE      NOT NULL,
    `name`           VARCHAR(255)       NULL,
    `email`          VARCHAR(320)       NULL,
    `linkedin_url`   VARCHAR(255)       NULL,
    `github_url`     VARCHAR(255)       NULL,
    `instagram_url`  VARCHAR(255)       NULL,
    `created_at`     timestamp          NOT NULL,
    `updated_at`     timestamp          NOT NULL
);

CREATE TABLE `events`
(
    `event_id`   BINARY(16) PRIMARY KEY NOT NULL,
    `created_at` timestamp              NOT NULL,
    `updated_at` timestamp              NOT NULL
);

CREATE TABLE `participants`
(
    `participant_id`        BIGINT PRIMARY KEY NOT NULL,
    `event_id`              BINARY(16)         NOT NULL,
    `account_id`            BIGINT             NOT NULL,
    `pin_number`            INT                NOT NULL,
    `icon_number`           INT                NOT NULL,
    `introduce`             VARCHAR(255)       NULL,
    `reminder_experience`   VARCHAR(255)       NULL,
    `want_again_experience` VARCHAR(255)       NULL,
    `created_at`            timestamp          NOT NULL,
    `updated_at`            timestamp          NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
    CONSTRAINT fk_account FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    INDEX idx_event_pin_number (`event_id`, `pin_number`)
);

CREATE TABLE `social_dex`
(
    `first_participant_id`  BIGINT    NOT NULL,
    `second_participant_id` BIGINT    NOT NULL,
    `created_at`            timestamp NOT NULL,
    `updated_at`            timestamp NOT NULL,
    PRIMARY KEY (first_participant_id, second_participant_id),
    CONSTRAINT fk_first_participant FOREIGN KEY (`first_participant_id`) REFERENCES `participants` (`participant_id`),
    CONSTRAINT fk_second_participant FOREIGN KEY (`second_participant_id`) REFERENCES `participants` (`participant_id`)
);
