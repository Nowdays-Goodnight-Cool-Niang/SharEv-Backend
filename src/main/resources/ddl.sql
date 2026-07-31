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

CREATE TABLE accounts
(
    account_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role       VARCHAR(20) NOT NULL, -- 'ADMIN', 'USER'
    name       TEXT        NOT NULL,
    email      TEXT        NOT NULL UNIQUE,
    handle     TEXT        NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE oauth_accounts
(
    provider           VARCHAR(20) NOT NULL, -- 'KAKAO', 'GOOGLE'
    subject_identifier TEXT        NOT NULL,
    account_id         BIGINT      NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_oauth_accounts PRIMARY KEY (provider, subject_identifier),
    CONSTRAINT fk_oauth_accounts_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    CONSTRAINT uk_oauth_accounts_policy UNIQUE (account_id, provider)
);

CREATE TABLE teams
(
    team_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    certification VARCHAR(20) NOT NULL, -- 'NONE', 'CERTIFICATED'
    title         TEXT        NOT NULL,
    content       TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX uk_teams_title_active ON teams (title) WHERE deleted_at IS NULL;

CREATE TABLE members
(
    member_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    team_id    BIGINT      NOT NULL,
    account_id BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL, -- 'INVITE', 'ACTIVATE', 'DEACTIVATE'
    role       VARCHAR(20) NOT NULL, -- 'ADMIN', 'COMMON'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_members_team_account UNIQUE (team_id, account_id),
    CONSTRAINT fk_members_team FOREIGN KEY (team_id) REFERENCES teams (team_id),
    CONSTRAINT fk_members_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE INDEX idx_members_account_id ON members (account_id);
CREATE INDEX idx_members_team_id ON members (team_id);

CREATE TABLE gatherings
(
    gathering_id      UUID PRIMARY KEY,
    visible           VARCHAR(20) NOT NULL, -- 'PUBLIC', 'PRIVATE'
    team_id           BIGINT      NOT NULL,
    title             TEXT        NOT NULL,
    content           TEXT        NOT NULL,
    start_at          TIMESTAMPTZ NOT NULL,
    end_at            TIMESTAMPTZ NOT NULL,
    place             TEXT        NOT NULL,
    image_url         TEXT        NULL,
    gathering_url     TEXT        NULL,
    contact           TEXT        NULL,
    deleted_at        TIMESTAMPTZ NULL,
    register_start_at TIMESTAMPTZ NOT NULL,
    register_end_at   TIMESTAMPTZ NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_gatherings_team FOREIGN KEY (team_id) REFERENCES teams (team_id),
    CONSTRAINT chk_gatherings_date_valid CHECK (end_at > start_at),
    CONSTRAINT chk_gatherings_register_date_valid CHECK (register_end_at > register_start_at)
);

CREATE INDEX idx_gatherings_team_id ON gatherings (team_id);

CREATE TABLE introduce_templates
(
    introduce_template_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    gathering_id          UUID        NOT NULL,
    version               INT         NOT NULL,
    content               JSONB       NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_introduce_templates_gatherings FOREIGN KEY (gathering_id) REFERENCES gatherings (gathering_id),
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

    CONSTRAINT fk_cards_gatherings FOREIGN KEY (gathering_id) REFERENCES gatherings (gathering_id),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    CONSTRAINT uk_cards_gatherings_account UNIQUE (gathering_id, account_id),
    CONSTRAINT uk_cards_gatherings_pin_number UNIQUE (gathering_id, pin_number),
    CONSTRAINT fk_cards_template_integrity FOREIGN KEY (gathering_id, template_version) REFERENCES introduce_templates (gathering_id, version)
);

CREATE INDEX idx_cards_account_id ON cards (account_id);
CREATE INDEX idx_cards_gathering_id ON cards (gathering_id);

CREATE TABLE connections
(
    connection_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    my_card_id    BIGINT      NOT NULL,
    other_card_id BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL, -- 'STAR', 'REGISTRATION'
    memo          TEXT        NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_card_connections_my_card FOREIGN KEY (my_card_id) REFERENCES cards (card_id) ON DELETE CASCADE,
    CONSTRAINT fk_card_connections_other_card FOREIGN KEY (other_card_id) REFERENCES cards (card_id) ON DELETE CASCADE,
    CONSTRAINT uk_card_connections_mapping UNIQUE (my_card_id, other_card_id),
    CONSTRAINT chk_card_connections_no_self_loop CHECK (my_card_id <> other_card_id)
);

CREATE INDEX idx_connections_my_card_id ON connections (my_card_id);
CREATE INDEX idx_connections_other_card_id ON connections (other_card_id);

CREATE TABLE links
(
    link_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id BIGINT      NOT NULL,
    link_url   TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_links_account FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE feedbacks
(
    feedback_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content     TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
