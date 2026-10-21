CREATE TABLE monthly_budgets (
    id              BIGSERIAL PRIMARY KEY,
    month           VARCHAR(7)     NOT NULL,
    income          NUMERIC(19,2)  NOT NULL,
    fixed_expenses  NUMERIC(19,2)  NOT NULL,
    savings_goal    NUMERIC(19,2)  NOT NULL,
    CONSTRAINT uq_monthly_budgets_month UNIQUE (month)
);

CREATE TABLE expenses (
    id          BIGSERIAL PRIMARY KEY,
    category    VARCHAR(20)    NOT NULL,
    amount      NUMERIC(19,2)  NOT NULL,
    date        DATE           NOT NULL,
    note        VARCHAR(500)
);

CREATE TABLE monthly_reflections (
    id                  BIGSERIAL PRIMARY KEY,
    month               VARCHAR(7)     NOT NULL,
    money_had           NUMERIC(19,2)  NOT NULL,
    money_saved         NUMERIC(19,2)  NOT NULL,
    money_spent         NUMERIC(19,2)  NOT NULL,
    improvement_note    TEXT,
    CONSTRAINT uq_monthly_reflections_month UNIQUE (month)
);

CREATE INDEX idx_expenses_date ON expenses (date);
CREATE INDEX idx_expenses_category ON expenses (category);