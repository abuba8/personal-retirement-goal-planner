DROP TABLE IF EXISTS contribution_record;
DROP TABLE IF EXISTS funding_source;
DROP TABLE IF EXISTS goal;
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL' CHECK (provider IN ('LOCAL', 'GOOGLE')),
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    verification_code VARCHAR(6),
    verification_code_expires_at TIMESTAMP,
    verification_attempts INT NOT NULL DEFAULT 0
);

CREATE TABLE goal (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    target_retirement_age INT NOT NULL CHECK (target_retirement_age > 0),
    target_amount DECIMAL(15, 2) NOT NULL CHECK (target_amount > 0),
    notes TEXT,

    CONSTRAINT fk_goal_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE funding_source (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    source_type VARCHAR(50) NOT NULL CHECK (
        source_type IN (
            'TRADITIONAL_401K',
            'ROTH_401K',
            'TRADITIONAL_IRA',
            'ROTH_IRA',
            'SEP_IRA',
            'TAXABLE_BROKERAGE'
        )
    ),
    institution VARCHAR(150),
    notes TEXT,

    CONSTRAINT fk_funding_source_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE contribution_record (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NOT NULL,
    funding_source_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    contribution_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (
        category IN (
            'EMPLOYEE_SALARY_DEFERRAL',
            'EMPLOYER_MATCH',
            'CATCH_UP_CONTRIBUTION',
            'ROLLOVER'
        )
    ),
    notes TEXT,

    CONSTRAINT fk_contribution_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_contribution_goal
        FOREIGN KEY (goal_id)
        REFERENCES goal(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,

    CONSTRAINT fk_contribution_funding_source
        FOREIGN KEY (funding_source_id)
        REFERENCES funding_source(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE
);

CREATE INDEX idx_goal_user_id
    ON goal(user_id);

CREATE INDEX idx_funding_source_user_id
    ON funding_source(user_id);

CREATE INDEX idx_contribution_user_id
    ON contribution_record(user_id);

CREATE INDEX idx_contribution_goal_id
    ON contribution_record(goal_id);

CREATE INDEX idx_contribution_funding_source_id
    ON contribution_record(funding_source_id);

CREATE INDEX idx_contribution_date
    ON contribution_record(contribution_date);