DROP TYPE IF EXISTS member_role CASCADE;
DROP TYPE IF EXISTS card_connection_status CASCADE;
DROP TYPE IF EXISTS team_certification CASCADE;
DROP TYPE IF EXISTS member_status CASCADE;
DROP TYPE IF EXISTS visible_type CASCADE;
DROP TYPE IF EXISTS oauth_provider CASCADE;
DROP TYPE IF EXISTS role_type CASCADE;

DROP TABLE IF EXISTS link_click_logs CASCADE;
DROP TABLE IF EXISTS links CASCADE;
DROP TABLE IF EXISTS connections CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS introduce_templates CASCADE;
DROP TABLE IF EXISTS gatherings CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS oauth_accounts CASCADE;
DROP TABLE IF EXISTS feedbacks CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;

CREATE TYPE role_type AS ENUM ('ADMIN', 'USER');
CREATE TYPE oauth_provider AS ENUM ('KAKAO', 'GOOGLE');
CREATE TYPE visible_type AS ENUM ('PUBLIC', 'PRIVATE');
CREATE TYPE member_status AS ENUM ('INVITE', 'ACTIVATE', 'DEACTIVATE', 'ETC');
CREATE TYPE team_certification AS ENUM ('NONE', 'CERTIFICATED', 'ETC');
CREATE TYPE card_connection_status AS ENUM ('STAR', 'REGISTRATION');
CREATE TYPE member_role AS ENUM ('ADMIN', 'COMMON', 'ETC');

CREATE TABLE accounts
(
    account_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role       role_type   NOT NULL,
    name       TEXT        NOT NULL,
    email      TEXT        NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE oauth_accounts
(
    provider           oauth_provider NOT NULL,
    subject_identifier TEXT           NOT NULL,
    account_id         BIGINT         NOT NULL,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_oauth_accounts PRIMARY KEY (provider, subject_identifier),
    CONSTRAINT fk_oauth_accounts_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    CONSTRAINT uk_oauth_accounts_policy UNIQUE (account_id, provider)
);

CREATE TABLE teams
(
    team_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    certification team_certification NOT NULL,
    title         TEXT               NOT NULL UNIQUE,
    content       TEXT               NOT NULL,
    activate_flag BOOLEAN            NOT NULL,
    created_at    TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

CREATE TABLE members
(
    member_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    team_id    BIGINT        NOT NULL,
    account_id BIGINT        NOT NULL,
    status     member_status NOT NULL,
    role       member_role   NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_members_team_account UNIQUE (team_id, account_id),
    CONSTRAINT fk_members_team FOREIGN KEY (team_id) REFERENCES teams (team_id),
    CONSTRAINT fk_members_account FOREIGN KEY (account_id) REFERENCES accounts (account_id)
);

CREATE TABLE gatherings
(
    gathering_id      UUID PRIMARY KEY,
    visible           visible_type NOT NULL,
    team_id           BIGINT       NOT NULL,
    title             TEXT         NOT NULL,
    content           TEXT         NOT NULL,
    start_at          TIMESTAMPTZ  NOT NULL,
    end_at            TIMESTAMPTZ  NOT NULL,
    place             TEXT         NOT NULL,
    image_url         TEXT         NULL,
    gathering_url     TEXT         NULL,
    contact           TEXT         NULL,
    deleted_at        TIMESTAMPTZ  NULL,
    register_start_at TIMESTAMPTZ  NOT NULL,
    register_end_at   TIMESTAMPTZ  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_gatherings_team FOREIGN KEY (team_id) REFERENCES teams (team_id),
    CONSTRAINT chk_gatherings_date_valid CHECK (end_at > start_at),
    CONSTRAINT chk_gatherings_register_date_valid CHECK (register_end_at > register_start_at)
);

CREATE TABLE introduce_templates -- 템플릿이 없는 행사라도, 행사 생성 시 기본적으로 하나 만들어지도록 한다. ver0라던지. 그게 cards와의 연산을 더 복잡하지 않게 가져가준다.
(
    introduce_template_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    gathering_id          UUID        NOT NULL,
    version               INT         NOT NULL,
    content               JSONB       NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_introduce_templates_gatherings FOREIGN KEY (gathering_id) REFERENCES gatherings (gathering_id) ON DELETE CASCADE,
    CONSTRAINT uk_introduce_templates_gathering_version UNIQUE (gathering_id, version)
);

CREATE TABLE cards
(
    card_id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    gathering_id      UUID        NOT NULL,
    account_id        BIGINT      NOT NULL,
    pin_number        INT         NULL,
    template_version  INT         NOT NULL,
    introduction_text JSONB       NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_cards_gatherings FOREIGN KEY (gathering_id) REFERENCES gatherings (gathering_id) ON DELETE CASCADE,
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    CONSTRAINT uk_cards_gatherings_account UNIQUE (gathering_id, account_id),
    CONSTRAINT uk_cards_gatherings_pin_number UNIQUE (gathering_id, pin_number),
    CONSTRAINT fk_cards_template_integrity FOREIGN KEY (gathering_id, template_version) REFERENCES introduce_templates (gathering_id, version)
);

CREATE TABLE connections
(
    connection_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    my_card_id    BIGINT                 NOT NULL,
    other_card_id BIGINT                 NOT NULL,
    status        card_connection_status NOT NULL,
    memo          TEXT                   NULL,
    created_at    TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ            NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_card_connections_my_card FOREIGN KEY (my_card_id) REFERENCES cards (card_id) ON DELETE CASCADE,
    CONSTRAINT fk_card_connections_other_card FOREIGN KEY (other_card_id) REFERENCES cards (card_id) ON DELETE CASCADE,
    CONSTRAINT uk_card_connections_mapping UNIQUE (my_card_id, other_card_id),
    CONSTRAINT chk_card_connections_no_self_loop CHECK (my_card_id <> other_card_id)
);

CREATE INDEX idx_card_connections_other_id ON connections (other_card_id);

CREATE TABLE links
(
    link_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id BIGINT NOT NULL,
    link_url   TEXT   NOT NULL,
--     total_click_count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_links_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

-- CREATE TABLE link_click_logs
-- (
--     link_click_log_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
--     link_id           BIGINT      NOT NULL,
--     click_account_id  BIGINT      NULL,
--     clicked_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--
--     CONSTRAINT fk_click_logs_link FOREIGN KEY (link_id) REFERENCES links (link_id) ON DELETE CASCADE
-- );
--
-- CREATE INDEX idx_click_logs_link_id ON link_click_logs (link_id);

CREATE TABLE feedbacks
(
    feedback_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content     TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
