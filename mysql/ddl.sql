USE hackathon;

CREATE TABLE `events`
(
    `event_id`   BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT, -- 이벤트 고유 식별자
    `title`      VARCHAR(255)       NOT NULL,                -- 이벤트 제목
    `content`    TEXT               NOT NULL,                -- 이벤트 내용
    `started_at` DateTime           NOT NULL,                -- 시작 일시
    `ended_at`   DateTime           NOT NULL,                -- 종료 일시
    `place`      VARCHAR(255)       NOT NULL,                -- 장소
    `organizer`  VARCHAR(255)       NOT NULL,                -- 주최자
    `image_url`  VARCHAR(255)       NOT NULL,                -- 이벤트 이미지 URL
    `event_url`  VARCHAR(255)       NOT NULL                 -- 이벤트 페이지 URL
) ENGINE = InnoDB
  CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;

CREATE TABLE `accounts`
(
    `account_id`       BIGINT PRIMARY KEY NOT NULL, -- 계정 고유 식별자
    `name`             VARCHAR(255)       NOT NULL, -- 사용자 이름
    `phone`            VARCHAR(255)       NULL,     -- 전화번호 (선택)
    `github_url`       VARCHAR(255)       NULL,     -- Github 프로필 URL (선택)
    `instagram_url`    VARCHAR(255)       NULL,     -- Instagram 프로필 URL (선택)
    `facebook_url`     VARCHAR(255)       NULL,     -- Facebook 프로필 URL (선택)
    `profile_image_id` INT                NOT NULL  -- 프로필 이미지 ID
) ENGINE = InnoDB
  CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;

CREATE TABLE `participants`
(
    `participant_id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT, -- 이벤트-계정 관계 고유 식별자
    `event_id`       BIGINT             NOT NULL,                -- 이벤트 참조 ID
    `account_id`     BIGINT             NOT NULL,                -- 계정 참조 ID
    `job_group`      VARCHAR(255)       NOT NULL,                -- 직무 그룹
    `team_name`      VARCHAR(255)       NOT NULL,                -- 팀 이름
    `project_info`   VARCHAR(255)       NOT NULL,                -- 프로젝트 정보
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;

CREATE TABLE `found_participants`
(
    `participant_id` BIGINT NOT NULL,
    `account_id`     BIGINT NOT NULL,
    CONSTRAINT pk_found_participants PRIMARY KEY (`participant_id`, `account_id`),
    CONSTRAINT fk_found_participants_participant FOREIGN KEY (`participant_id`) REFERENCES `participants` (`participant_id`),
    CONSTRAINT fk_found_participants_account FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;

-- Events 테이블 Mock 데이터
INSERT INTO `events`
(`event_id`, `title`, `content`, `started_at`, `ended_at`, `place`, `organizer`, `image_url`, `event_url`)
VALUES (1, '삐약톤: 캠퍼스 대항전',
        '친구들과 함께 머리를 맞대고 밤새 새로운 아이디어를 구현하며, 유쾌한 협업을 경험할 수 있는 오프라인 해커톤!\nGDG Campus Korea 에서 캠퍼스 대항전 🐣삐약톤🐣을 주최합니다. 🎉\n대학교의 이름을 걸고 여러분의 반짝이는 아이디어와 열정을 마음껏 펼칠 수 있는 이 자리에 함께 해주세요!\n함께 웃고 도전하며 성장할 수 있는 소중한 시간을 만들어드릴게요.\n여러분의 꿈과 열정을 응원하는 삐약톤에서 만나요! 😊',
        '2025-01-11 11:00:00', '2025-01-12 14:00:00', '동국대학교 서울캠퍼스 혜화관 고순청 세미나실',
        'GDG Campus Korea', 'https://example.com/images/meetup.jpg', 'https://example.com/events/startup-meetup'),
       (2, '오픈소스 컨트리뷰톤', '오픈소스 프로젝트 기여 행사', '2025-05-20 09:00:00', '2025-05-20 18:00:00', '강남 개발자 센터', '오픈소스 커뮤니티',
        'https://example.com/images/contributhon.jpg', 'https://example.com/events/contributhon-2024'),
       (3, '2024 개발자 컨퍼런스', '최신 기술 트렌드와 개발 경험을 공유하는 컨퍼런스', '2024-03-15 10:00:00', '2024-03-15 18:00:00', '코엑스 그랜드볼룸',
        '테크 커뮤니티', 'https://example.com/images/conf2024.jpg', 'https://example.com/events/dev-conf-2024');
