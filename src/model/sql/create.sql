CREATE TABLE IF NOT EXISTS USERS
(
    login       TEXT       PRIMARY KEY,
    password    CHAR(32)
);

CREATE TABLE IF NOT EXISTS ACCOUNTS
(
    owner       TEXT,
    date        TEXT,
    account_id  TEXT,
    description TEXT,
    balance     REAL,
    PRIMARY KEY (owner, account_id)
);

CREATE TABLE IF NOT EXISTS  RECORDS
(
    account_id  TEXT     NOT NULL,
    record_id   INTEGER     PRIMARY KEY AUTOINCREMENT,
    date        TEXT,
    operation   TEXT,
    amount      REAL,
    description TEXT,
    category    TEXT
);

