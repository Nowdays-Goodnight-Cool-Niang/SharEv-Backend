-- ============================================
-- Accounts (독립적 테이블)
-- ============================================
-- handle: PUBLIC 팀 생성/초대에 필요한 VERIFIED 권한의 전제 (^[a-zA-Z0-9_]{4,20}$, UNIQUE)
INSERT INTO "accounts" ("account_id", "role", "name", "email", "handle", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 'ADMIN', '관리자', 'admin@example.com', 'admin', NOW(), NOW()),
       (2, 'USER', '홍길동', 'hong@example.com', 'honggildong', NOW(), NOW()),
       (3, 'USER', '김철수', 'kim@example.com', 'kimchulsoo', NOW(), NOW()),
       (4, 'USER', '이영희', 'lee@example.com', 'leeyounghee', NOW(), NOW()),
       (5, 'USER', '박민수', 'park@example.com', 'parkminsoo', NOW(), NOW());

-- ============================================
-- Teams (독립적 테이블)
--   - PUBLIC  : 사용자가 웹에서 생성한 공개 팀 (title/content 필수)
--   - PERSONAL: 회원가입 시 계정당 1개씩 자동 생성되는 개인 팀 (title NULL, content '')
-- ============================================
INSERT INTO "teams" ("team_id", "certification", "type", "title", "content", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 'CERTIFICATED', 'PUBLIC', '개발팀', '백엔드 개발을 담당하는 팀입니다.', NOW(), NOW()),
       (2, 'NONE', 'PUBLIC', '디자인팀', 'UI/UX 디자인을 담당하는 팀입니다.', NOW(), NOW()),
       (3, 'CERTIFICATED', 'PUBLIC', '기획팀', '서비스 기획을 담당하는 팀입니다.', NOW(), NOW()),
       (4, 'NONE', 'PUBLIC', '마케팅팀', '마케팅을 담당하는 팀입니다.', NOW(), NOW()),
       -- 개인 팀: 각 계정(1~5)의 회원가입 시 자동 생성된 팀 (title NULL, content '')
       (5, 'NONE', 'PERSONAL', NULL, '', NOW(), NOW()),
       (6, 'NONE', 'PERSONAL', NULL, '', NOW(), NOW()),
       (7, 'NONE', 'PERSONAL', NULL, '', NOW(), NOW()),
       (8, 'NONE', 'PERSONAL', NULL, '', NOW(), NOW()),
       (9, 'NONE', 'PERSONAL', NULL, '', NOW(), NOW());

-- ============================================
-- OAuth Accounts (accounts 참조)
-- ============================================
INSERT INTO "oauth_accounts" ("provider", "subject_identifier", "account_id", "created_at", "updated_at")
VALUES ('KAKAO', '123456789', 1, NOW(), NOW()),
       ('KAKAO', '987654321', 2, NOW(), NOW()),
       ('GOOGLE', '111222333', 3, NOW(), NOW()),
       ('KAKAO', '444555666', 4, NOW(), NOW()),
       ('GOOGLE', '777888999', 5, NOW(), NOW());

-- ============================================
-- Members (teams, accounts 참조)
-- ============================================
INSERT INTO "members" ("member_id", "team_id", "account_id", "status", "role", "created_at", "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 1, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (2, 1, 2, 'ACTIVATE', 'COMMON', NOW(), NOW()),
       (3, 1, 3, 'ACTIVATE', 'COMMON', NOW(), NOW()),
       (4, 2, 2, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (5, 2, 4, 'INVITE', 'COMMON', NOW(), NOW()),
       (6, 3, 1, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (7, 3, 5, 'ACTIVATE', 'COMMON', NOW(), NOW()),
       (8, 4, 4, 'ACTIVATE', 'ADMIN', NOW(), NOW()), -- 마케팅팀 생성자(모든 PUBLIC 팀은 생성 시 ADMIN 멤버가 생김)
       -- 개인 팀 소유자: 본인 개인 팀(team 5~9)의 유일한 멤버, 항상 ACTIVATE/ADMIN
       (9, 5, 1, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (10, 6, 2, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (11, 7, 3, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (12, 8, 4, 'ACTIVATE', 'ADMIN', NOW(), NOW()),
       (13, 9, 5, 'ACTIVATE', 'ADMIN', NOW(), NOW());

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
        'PUBLIC',
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
        'PRIVATE',
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
        'PUBLIC',
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
        'PUBLIC',
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
-- Introductions (gatherings 참조)
--   흐름: 관리자 첫 작성 시 introduction 생성(version 1) → fields(키) 변경 시 새 버전 행 append
--   - 구성 완료 PUBLIC 행사만 introduction 보유 (version 1)
--   - 미구성 행사(PRIVATE 내부 미팅 45b2 등)는 introduction 행 없음
-- ============================================
INSERT INTO "introductions" ("introduction_id",
                             "gathering_id",
                             "version",
                             "source",
                             "fields",
                             "created_at",
                             "updated_at")
    OVERRIDING SYSTEM VALUE
VALUES (1,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        1,
        '안녕하세요, ${company}에서 ${role}로 일하는 ${name}입니다. 주력 기술은 ${techStack}이며, 개발 경력은 ${career}년입니다.',
        '{
          "name": {"placeholder": "이름을 입력해주세요"},
          "company": {"placeholder": "소속 회사를 입력해주세요"},
          "role": {"placeholder": "직무를 입력해주세요 (예: 백엔드 개발자)"},
          "techStack": {"placeholder": "주력 기술 스택을 입력해주세요"},
          "career": {"placeholder": "개발 경력(년)을 입력해주세요"}
        }'::JSONB,
        NOW(),
        NOW()),
       (2,
        'd8f1e6c3-9a7b-4d4f-b6e1-5c8e3b7d2e0a'::UUID,
        1,
        '${company}의 ${name}입니다. ${specialty} 디자인을 전문으로 하며, 주로 ${tool}을 사용합니다.',
        '{
          "name": {"placeholder": "이름을 입력해주세요"},
          "company": {"placeholder": "소속을 입력해주세요"},
          "specialty": {"placeholder": "전문 분야를 입력해주세요 (예: 모바일 UX)"},
          "tool": {"placeholder": "주로 사용하는 디자인 툴을 입력해주세요"}
        }'::JSONB,
        NOW(),
        NOW()),
       (3,
        'e9f2e7c4-0b8c-5e5f-c7f2-6d9f4e8c3f1b'::UUID,
        1,
        '안녕하세요, ${organization} 소속 ${name}입니다. ${interest} 분야에 관심이 있으며, 이번 세미나에서 ${goal}을 얻어가고 싶습니다.',
        '{
          "name": {"placeholder": "이름을 입력해주세요"},
          "organization": {"placeholder": "소속을 입력해주세요"},
          "interest": {"placeholder": "관심 분야를 입력해주세요"},
          "goal": {"placeholder": "세미나 참여 목표를 입력해주세요"}
        }'::JSONB,
        NOW(),
        NOW());

-- ============================================
-- Cards (gatherings, accounts 참조)
--   흐름: join(pin 부여, 아직 미작성) → updateIntroduce(introduction_version + field_values 작성)
--   모든 카드는 구성된 introduction(version 1)에 맞춰 작성 완료 상태 (field_values 키 = 해당 행사 fields)
-- ============================================
INSERT INTO "cards" ("card_id", "gathering_id", "account_id", "pin_number", "introduction_version", "field_values")
    OVERRIDING SYSTEM VALUE
VALUES (1,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        2,
        1234,
        1,
        '{
          "name": "홍길동",
          "company": "ABC 소프트웨어",
          "role": "시니어 백엔드 개발자",
          "techStack": "Spring Boot, JPA, Kotlin",
          "career": "10"
        }'::JSONB),
       (2,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        3,
        5678,
        1,
        '{
          "name": "김철수",
          "company": "XYZ 스타트업",
          "role": "주니어 백엔드 개발자",
          "techStack": "Java, Spring MVC",
          "career": "2"
        }'::JSONB),
       (3,
        'd8f1e6c3-9a7b-4d4f-b6e1-5c8e3b7d2e0a'::UUID,
        4,
        9012,
        1,
        '{
          "name": "이영희",
          "company": "디자인 스튜디오",
          "specialty": "모바일 UX/UI",
          "tool": "Figma"
        }'::JSONB),
       (4,
        'e9f2e7c4-0b8c-5e5f-c7f2-6d9f4e8c3f1b'::UUID,
        5,
        3456,
        1,
        '{
          "name": "박민수",
          "organization": "기획 컴퍼니",
          "interest": "애자일 프로덕트 기획",
          "goal": "실전 기획 방법론"
        }'::JSONB),
       (5,
        'a81bc81b-dead-4e5d-abff-90865d1e13b1'::UUID,
        1,
        2468,
        1,
        '{
          "name": "관리자",
          "company": "쿨냥이",
          "role": "인프라 엔지니어",
          "techStack": "Kubernetes, PostgreSQL",
          "career": "8"
        }'::JSONB);

-- ============================================
-- Connections (cards 참조)
--   흐름: 같은 행사 내 카드 열람 시 양방향(A→B, B→A) 쌍으로 생성, 상태 REGISTRATION, memo 없음
--   (Spring Boot 워크샵 a81bc 참가자 카드 1·2·5 간 연결)
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
        'REGISTRATION',
        NULL,
        NOW(),
        NOW()),
       (2,
        2,
        1,
        'REGISTRATION',
        NULL,
        NOW(),
        NOW()),
       (3,
        1,
        5,
        'REGISTRATION',
        NULL,
        NOW(),
        NOW()),
       (4,
        5,
        1,
        'REGISTRATION',
        NULL,
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
SELECT setval(pg_get_serial_sequence('introductions', 'introduction_id'),
              (SELECT MAX(introduction_id) FROM introductions));
SELECT setval(pg_get_serial_sequence('cards', 'card_id'), (SELECT MAX(card_id) FROM cards));
SELECT setval(pg_get_serial_sequence('connections', 'connection_id'), (SELECT MAX(connection_id) FROM connections));
SELECT setval(pg_get_serial_sequence('feedbacks', 'feedback_id'), (SELECT MAX(feedback_id) FROM feedbacks));
