-- ============================================
-- Accounts (독립적 테이블)
-- ============================================
INSERT INTO "accounts" ("account_id", "role", "name", "email", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 'ADMIN'::role_type, '관리자', 'admin@example.com', NOW(), NOW()),
       (2, 'USER'::role_type, '홍길동', 'hong@example.com', NOW(), NOW()),
       (3, 'USER'::role_type, '김철수', 'kim@example.com', NOW(), NOW()),
       (4, 'USER'::role_type, '이영희', 'lee@example.com', NOW(), NOW()),
       (5, 'USER'::role_type, '박민수', 'park@example.com', NOW(), NOW());

-- ============================================
-- Teams (독립적 테이블)
-- ============================================
INSERT INTO "teams" ("team_id", "certification", "title", "content", "activate_flag", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 'CERTIFICATED'::team_certification, '개발팀', '백엔드 개발을 담당하는 팀입니다.', true, NOW(), NOW()),
       (2, 'NONE'::team_certification, '디자인팀', 'UI/UX 디자인을 담당하는 팀입니다.', true, NOW(), NOW()),
       (3, 'CERTIFICATED'::team_certification, '기획팀', '서비스 기획을 담당하는 팀입니다.', true, NOW(), NOW()),
       (4, 'NONE'::team_certification, '마케팅팀', '마케팅을 담당하는 팀입니다.', false, NOW(), NOW());

-- ============================================
-- OAuth Accounts (accounts 참조)
-- ============================================
INSERT INTO "oauth_accounts" ("provider", "subject_identifier", "account_id", "created_at", "updated_at")
VALUES ('KAKAO'::oauth_provider, '123456789', 1, NOW(), NOW()),
       ('KAKAO'::oauth_provider, '987654321', 2, NOW(), NOW()),
       ('GOOGLE'::oauth_provider, '111222333', 3, NOW(), NOW()),
       ('KAKAO'::oauth_provider, '444555666', 4, NOW(), NOW()),
       ('GOOGLE'::oauth_provider, '777888999', 5, NOW(), NOW());

-- ============================================
-- Members (teams, accounts 참조)
-- ============================================
INSERT INTO "members" ("member_id", "team_id", "account_id", "status", "role", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 1, 'ACTIVATE'::member_status, 'ADMIN'::member_role, NOW(), NOW()),
       (2, 1, 2, 'ACTIVATE'::member_status, 'COMMON'::member_role, NOW(), NOW()),
       (3, 1, 3, 'ACTIVATE'::member_status, 'COMMON'::member_role, NOW(), NOW()),
       (4, 2, 2, 'ACTIVATE'::member_status, 'ADMIN'::member_role, NOW(), NOW()),
       (5, 2, 4, 'INVITE'::member_status, 'COMMON'::member_role, NOW(), NOW()),
       (6, 3, 1, 'ACTIVATE'::member_status, 'ADMIN'::member_role, NOW(), NOW()),
       (7, 3, 5, 'ACTIVATE'::member_status, 'COMMON'::member_role, NOW(), NOW());

-- ============================================
-- Gatherings (teams 참조)
-- ============================================
INSERT INTO "gatherings" ("gathering_id",
                          "visible",
                          "team_id",
                          "title",
                          "content",
                          "start_at",
                          "end_at",
                          "place",
                          "image_url",
                          "gathering_url",
                          "contact",
                          "deleted_at",
                          "register_start_at",
                          "register_end_at",
                          "created_at",
                          "updated_at")
VALUES ('a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        'PUBLIC'::visible_type,
        1,
        'Spring Boot 워크샵',
        'Spring Boot를 활용한 백엔드 개발 워크샵입니다. 실무 경험을 공유합니다.',
        NOW() + INTERVAL '7 days',
        NOW() + INTERVAL '8 days',
        '서울 강남구',
        'https://example.com/images/event1.jpg',
        'https://example.com/events/spring-boot-workshop',
        'contact@example.com',
        NULL,
        NOW(),
        NOW() + INTERVAL '6 days',
        NOW(),
        NOW()),
       ('45b2e9d2-5a21-4d1a-8c9e-5f8e5b4e3f17'::UUID,
        'PRIVATE'::visible_type,
        1,
        '팀 내부 미팅',
        '개발팀 내부 미팅입니다.',
        NOW() + INTERVAL '14 days',
        NOW() + INTERVAL '14 days' + INTERVAL '2 hours',
        '서울 본사',
        NULL,
        NULL,
        NULL,
        NULL,
        NOW(),
        NOW() + INTERVAL '13 days',
        NOW(),
        NOW()),
       ('d8f1e6c3-9a7b-4d4f-b6e1-5c8e3b7d2e0a'::UUID,
        'PUBLIC'::visible_type,
        2,
        'UI/UX 디자인 컨퍼런스',
        '최신 UI/UX 디자인 트렌드를 공유하는 컨퍼런스입니다.',
        NOW() + INTERVAL '21 days',
        NOW() + INTERVAL '22 days',
        '부산 해운대',
        'https://example.com/images/event2.jpg',
        'https://example.com/events/ux-conference',
        'design@example.com',
        NULL,
        NOW(),
        NOW() + INTERVAL '20 days',
        NOW(),
        NOW()),
       ('e9f2e7c4-0b8c-5e5f-c7f2-6d9f4e8c3f1b'::UUID,
        'PUBLIC'::visible_type,
        3,
        '프로젝트 기획 세미나',
        '효과적인 프로젝트 기획 방법론을 다루는 세미나입니다.',
        NOW() + INTERVAL '30 days',
        NOW() + INTERVAL '31 days',
        '인천 송도',
        NULL,
        'https://example.com/events/planning-seminar',
        'planning@example.com',
        NULL,
        NOW(),
        NOW() + INTERVAL '29 days',
        NOW(),
        NOW());

-- ============================================
-- Introduce Templates (gatherings 참조)
-- ============================================
INSERT INTO "introduce_templates" ("introduce_template_id",
                                   "gathering_id",
                                   "version",
                                   "content",
                                   "created_at",
                                   "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        0,
        '{
          "sections": [
            {
              "title": "자기소개",
              "fields": [
                "이름",
                "회사",
                "직책"
              ]
            },
            {
              "title": "경력",
              "fields": [
                "경력년수",
                "주요기술"
              ]
            }
          ]
        }'::JSONB,
        NOW(),
        NOW()),
       (2,
        'd8f1e6c3-9a7b-4d4f-b6e1-5c8e3b7d2e0a'::UUID,
        0,
        '{
          "sections": [
            {
              "title": "포트폴리오",
              "fields": [
                "작품링크",
                "설명"
              ]
            },
            {
              "title": "연락처",
              "fields": [
                "이메일",
                "전화번호"
              ]
            }
          ]
        }'::JSONB,
        NOW(),
        NOW()),
       (3,
        'e9f2e7c4-0b8c-5e5f-c7f2-6d9f4e8c3f1b'::UUID,
        0,
        '{
          "sections": [
            {
              "title": "기획 경험",
              "fields": [
                "프로젝트명",
                "역할"
              ]
            },
            {
              "title": "관심사",
              "fields": [
                "도메인",
                "기술"
              ]
            }
          ]
        }'::JSONB,
        NOW(),
        NOW());

-- ============================================
-- Cards (gatherings, accounts 참조)
-- ============================================
INSERT INTO "cards" ("card_id", "gathering_id", "account_id", "pin_number", "template_version", "introduction_text")
    OVERRIDING SYSTEM VALUE
VALUES (1,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        2,
        1234,
        0,
        '{
          "name": "홍길동",
          "company": "ABC 회사",
          "position": "시니어 개발자",
          "bio": "10년차 백엔드 개발자입니다."
        }'::JSONB),
       (2,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        3,
        5678,
        0,
        '{
          "name": "김철수",
          "company": "XYZ 회사",
          "position": "주니어 개발자",
          "bio": "Spring Boot를 배우고 싶습니다."
        }'::JSONB),
       (3,
        'd8f1e6c3-9a7b-4d4f-b6e1-5c8e3b7d2e0a'::UUID,
        4,
        9012,
        0,
        '{
          "name": "이영희",
          "company": "디자인 스튜디오",
          "position": "UI 디자이너",
          "bio": "사용자 경험을 중시하는 디자이너입니다."
        }'::JSONB),
       (4,
        'e9f2e7c4-0b8c-5e5f-c7f2-6d9f4e8c3f1b'::UUID,
        5,
        3456,
        0,
        '{
          "name": "박민수",
          "company": "기획 회사",
          "position": "프로덕트 매니저",
          "bio": "사용자 중심의 기획을 추구합니다."
        }'::JSONB),
       (5,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        1,
        NULL,
        0,
        '{
          "name": "관리자",
          "company": "시스템",
          "position": "시스템 관리자",
          "bio": "시스템을 관리합니다."
        }'::JSONB);

-- ============================================
-- Connections (cards 참조)
-- ============================================
INSERT INTO "connections" ("connection_id",
                           "my_card_id",
                           "other_card_id",
                           "status",
                           "memo",
                           "created_at",
                           "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1,
        1,
        2,
        'REGISTRATION'::card_connection_status,
        '좋은 만남이었습니다!',
        NOW(),
        NOW()),
       (2,
        1,
        3,
        'STAR'::card_connection_status,
        '나중에 연락하겠습니다.',
        NOW(),
        NOW()),
       (3,
        2,
        1,
        'REGISTRATION'::card_connection_status,
        NULL,
        NOW(),
        NOW()),
       (4,
        3,
        4,
        'STAR'::card_connection_status,
        '디자인 관련 협업 가능합니다.',
        NOW(),
        NOW());

-- ============================================
-- Links (accounts 참조)
-- ============================================
INSERT INTO "links" ("link_id", "account_id", "link_url")
    OVERRIDING SYSTEM VALUE
VALUES (DEFAULT, 1, 'https://github.com/admin'),
       (DEFAULT, 1, 'https://linkedin.com/in/admin'),
       (DEFAULT, 2, 'https://github.com/hong'),
       (DEFAULT, 2, 'https://blog.example.com/hong'),
       (DEFAULT, 3, 'https://github.com/kim'),
       (DEFAULT, 4, 'https://behance.net/lee'),
       (DEFAULT, 4, 'https://dribbble.com/lee'),
       (DEFAULT, 5, 'https://linkedin.com/in/park');

-- ============================================
-- Feedbacks (독립적 테이블)
-- ============================================
INSERT INTO "feedbacks" ("feedback_id", "content", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, '서비스가 매우 유용합니다. 계속 발전시켜 주세요!', NOW(), NOW()),
       (2, 'UI/UX 개선이 필요해 보입니다. 더 직관적인 인터페이스를 원합니다.', NOW(), NOW()),
       (3, '카드 교환 기능이 편리합니다. 감사합니다!', NOW(), NOW()),
       (4, '이벤트 등록 프로세스를 더 간단하게 만들어 주세요.', NOW(), NOW()),
       (5, '전반적으로 만족스럽습니다. 좋은 서비스입니다!', NOW(), NOW());

-- ============================================
-- IDENTITY 시퀀스 재설정
-- ============================================
SELECT setval(pg_get_serial_sequence('accounts', 'account_id'), (SELECT MAX(account_id) FROM accounts));
SELECT setval(pg_get_serial_sequence('teams', 'team_id'), (SELECT MAX(team_id) FROM teams));
SELECT setval(pg_get_serial_sequence('members', 'member_id'), (SELECT MAX(member_id) FROM members));
SELECT setval(pg_get_serial_sequence('introduce_templates', 'introduce_template_id'), (SELECT MAX(introduce_template_id) FROM introduce_templates));
SELECT setval(pg_get_serial_sequence('cards', 'card_id'), (SELECT MAX(card_id) FROM cards));
SELECT setval(pg_get_serial_sequence('connections', 'connection_id'), (SELECT MAX(connection_id) FROM connections));
SELECT setval(pg_get_serial_sequence('feedbacks', 'feedback_id'), (SELECT MAX(feedback_id) FROM feedbacks));
