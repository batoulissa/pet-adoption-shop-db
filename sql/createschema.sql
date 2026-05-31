-- ============================================================
-- Cat Adoption Center - createschema.sql
-- Table creation order is FK-safe (referenced tables first)
-- REQ13: fee_schedule + unit_price_at_sale snapshot in adoption_basket_items
-- REQ14: adopter_history tracks demographic changes
-- ============================================================
CREATE DATABASE cat_adoption_center;
USE cat_adoption_center;
-- 1. cat
CREATE TABLE cat (
    cat_id              INT           PRIMARY KEY AUTO_INCREMENT,
    cat_name            VARCHAR(100)  NOT NULL,
    breed               VARCHAR(100),
    age_months          INT,
    cat_type            ENUM('kitten','adult','senior','special_needs') NOT NULL,
    gender              ENUM('male','female') NOT NULL,
    color               VARCHAR(50),
    description         TEXT,
    photo_url           VARCHAR(255),
    status              ENUM('available','adopted','foster',
                             'medical_hold','euthanized','deceased')
                        DEFAULT 'available',
    intake_date         DATE          NOT NULL,
    euthanized_date     DATE,
    euthanized_reason   ENUM('terminal_illness','severe_injury',
                             'behavioral','overcapacity','owner_request'),
    natural_death_date  DATE,
    death_notes         TEXT
);

CREATE INDEX idx_cat_status ON cat(status);
CREATE INDEX idx_cat_type   ON cat(cat_type);

-- 2. adopter
CREATE TABLE adopter (
    adopter_id  INT           PRIMARY KEY AUTO_INCREMENT,
    first_name  VARCHAR(50)   NOT NULL,
    last_name   VARCHAR(50)   NOT NULL,
    email       VARCHAR(100)  UNIQUE,
    phone       VARCHAR(20),
    city        VARCHAR(50),
    address     TEXT,
    birth_year  INT,
    age         INT,
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_adopter_city ON adopter(city);
CREATE INDEX idx_adopter_name ON adopter(last_name);

-- 3. shelter
CREATE TABLE shelter (
    shelter_id    INT          PRIMARY KEY AUTO_INCREMENT,
    shelter_name  VARCHAR(100) NOT NULL,
    city          VARCHAR(50),
    manager_name  VARCHAR(100)
);

-- 4. fee_schedule  [REQ13]
--    Each row represents a price period for a cat_type.
--    effective_to IS NULL means this is the CURRENT active price.
--    When price changes: set old row's effective_to = today, insert new row.
CREATE TABLE fee_schedule (
    fee_id          INT           PRIMARY KEY AUTO_INCREMENT,
    cat_type        ENUM('kitten','adult','senior','special_needs') NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    effective_from  DATE          NOT NULL,
    effective_to    DATE,
    changed_by      VARCHAR(100),
    change_reason   VARCHAR(255)
);

CREATE INDEX idx_fee_cattype ON fee_schedule(cat_type);

-- 5. workers
CREATE TABLE workers (
    worker_id       INT           PRIMARY KEY AUTO_INCREMENT,
    first_name      VARCHAR(100)  NOT NULL,
    last_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  UNIQUE NOT NULL,
    phone           VARCHAR(20),
    role            ENUM('volunteer','vet','coordinator','admin','caretaker') NOT NULL,
    employment_type ENUM('full_time','part_time','volunteer') DEFAULT 'full_time',
    shelter_id      INT,
    hire_date       DATE          NOT NULL,
    salary          DECIMAL(10,2),
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shelter_id) REFERENCES shelter(shelter_id)
);

CREATE INDEX idx_workers_shelter ON workers(shelter_id);
CREATE INDEX idx_workers_role    ON workers(role);

-- 6. adoption_transaction
--    One transaction = one adoption visit by an adopter at a shelter.
--    basket_id has been removed — use transaction_id directly as the basket key.
CREATE TABLE adoption_transaction (
    transaction_id        INT       PRIMARY KEY AUTO_INCREMENT,
    transaction_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shelter_id            INT       NOT NULL,
    adopter_id            INT       NOT NULL,
    adopter_history_id    INT,
    FOREIGN KEY (shelter_id) REFERENCES shelter(shelter_id),
    FOREIGN KEY (adopter_id) REFERENCES adopter(adopter_id)
);

CREATE INDEX idx_txn_adopter ON adoption_transaction(adopter_id);
CREATE INDEX idx_txn_shelter ON adoption_transaction(shelter_id);

-- 7. adoption_basket_items
--    Each row = one cat in a transaction.
--    unit_price_at_sale is a SNAPSHOT of the fee at the time of adoption (REQ13).
--    Even if fee_schedule changes later, past records remain correct.
CREATE TABLE adoption_basket_items (
    basket_item_id      INT           PRIMARY KEY AUTO_INCREMENT,
    transaction_id      INT           NOT NULL,         -- FK to adoption_transaction
    cat_id              INT           NOT NULL,
    fee_id              INT           NOT NULL,         -- NOT NULL: REQ13 requires fee reference
    quantity            INT           NOT NULL DEFAULT 1,
    unit_price_at_sale  DECIMAL(10,2) NOT NULL,         -- REQ13: price snapshot
    CONSTRAINT chk_quantity CHECK (quantity > 0),
    FOREIGN KEY (transaction_id) REFERENCES adoption_transaction(transaction_id),
    FOREIGN KEY (cat_id)         REFERENCES cat(cat_id),
    FOREIGN KEY (fee_id)         REFERENCES fee_schedule(fee_id)
);

CREATE INDEX idx_basket_cat ON adoption_basket_items(cat_id);
CREATE INDEX idx_basket_txn ON adoption_basket_items(transaction_id);

-- 8. total_adoption_fees
--    Stores the computed total per transaction (denormalized for performance).
CREATE TABLE total_adoption_fees (
    total_id       INT           PRIMARY KEY AUTO_INCREMENT,
    transaction_id INT           NOT NULL UNIQUE,
    total_amount   DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES adoption_transaction(transaction_id)
);

-- 9. medical_records
CREATE TABLE medical_records (
    medical_id  INT   PRIMARY KEY AUTO_INCREMENT,
    cat_id      INT   NOT NULL,
    worker_id   INT   NOT NULL,
    record_date DATE  NOT NULL,
    `type`      ENUM('vaccine','checkup','surgery','treatment') NOT NULL,
    notes       TEXT,
    FOREIGN KEY (cat_id)    REFERENCES cat(cat_id),
    FOREIGN KEY (worker_id) REFERENCES workers(worker_id)
);

CREATE INDEX idx_medical_cat    ON medical_records(cat_id);
CREATE INDEX idx_medical_date   ON medical_records(record_date);

-- 10. euthanization_records
CREATE TABLE euthanization_records (
    euth_id        INT      PRIMARY KEY AUTO_INCREMENT,
    cat_id         INT      NOT NULL,
    worker_id      INT      NOT NULL,
    authorized_by  INT,
    scheduled_date DATE,
    performed_date DATE     NOT NULL,
    reason         ENUM('terminal_illness','severe_injury','behavioral',
                        'overcapacity','owner_request') NOT NULL,
    method         VARCHAR(100),
    second_opinion BOOLEAN  DEFAULT FALSE,
    consent_form   BOOLEAN  DEFAULT FALSE,
    notes          TEXT,
    FOREIGN KEY (cat_id)        REFERENCES cat(cat_id),
    FOREIGN KEY (worker_id)     REFERENCES workers(worker_id),
    FOREIGN KEY (authorized_by) REFERENCES workers(worker_id)
);

-- 11. salary_history
CREATE TABLE salary_history (
    history_id  INT           PRIMARY KEY AUTO_INCREMENT,
    worker_id   INT           NOT NULL,
    old_salary  DECIMAL(10,2),
    new_salary  DECIMAL(10,2) NOT NULL,
    change_date DATE          NOT NULL,
    reason      VARCHAR(255),
    FOREIGN KEY (worker_id) REFERENCES workers(worker_id)
);

-- 12. schedules
CREATE TABLE schedules (
    schedule_id INT  PRIMARY KEY AUTO_INCREMENT,
    worker_id   INT  NOT NULL,
    work_date   DATE NOT NULL,
    shift_start TIME NOT NULL,
    shift_end   TIME NOT NULL,
    FOREIGN KEY (worker_id) REFERENCES workers(worker_id)
);

CREATE INDEX idx_schedule_worker ON schedules(worker_id);
CREATE INDEX idx_schedule_date   ON schedules(work_date);

-- 13. adopter_history  [REQ14]
--    Snapshots of adopter demographics over time, including old and current versions.
--    Allows comparison of adoptions before/after address or age change.
CREATE TABLE adopter_history (
    history_id    INT      PRIMARY KEY AUTO_INCREMENT,
    adopter_id    INT      NOT NULL,
    snapshot_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    city          VARCHAR(50),
    address       TEXT,
    birth_year    INT,
    age           INT,
    change_reason VARCHAR(255),
    FOREIGN KEY (adopter_id) REFERENCES adopter(adopter_id)
);

CREATE INDEX idx_adopterhistory_adopter ON adopter_history(adopter_id);

-- Link each adoption transaction to the adopter demographic snapshot used at adoption time.
-- This makes REQ14 direct: old adoptions can be analyzed with old city/address/age values.
ALTER TABLE adoption_transaction
    ADD CONSTRAINT fk_txn_adopter_history
    FOREIGN KEY (adopter_history_id) REFERENCES adopter_history(history_id);

CREATE INDEX idx_txn_adopter_history ON adoption_transaction(adopter_history_id);

-- ============================================================
-- VIEWS  (REQ2, REQ11)
-- ============================================================

-- View 1: Available cats joined with current adoption fee and medical visit count
--         Used in REQ6 SELECT menus (join + view + user input)
CREATE OR REPLACE VIEW v_available_cats AS
SELECT
    c.cat_id,
    c.cat_name,
    c.breed,
    c.age_months,
    c.cat_type,
    c.gender,
    c.color,
    c.description,
    c.intake_date,
    f.unit_price      AS current_adoption_fee,
    f.effective_from  AS fee_effective_from,
    COUNT(m.medical_id) AS medical_visits
FROM cat c
LEFT JOIN fee_schedule f
       ON f.cat_type = c.cat_type AND f.effective_to IS NULL
LEFT JOIN medical_records m ON c.cat_id = m.cat_id
WHERE c.status = 'available'
GROUP BY c.cat_id, c.cat_name, c.breed, c.age_months, c.cat_type,
         c.gender, c.color, c.description, c.intake_date,
         f.unit_price, f.effective_from;

-- View 2: Workers with salary info joined with shelter (excludes volunteers)
--         Used in REQ6 SELECT menus (join + view + user input)
CREATE OR REPLACE VIEW v_worker_salary AS
SELECT
    w.worker_id,
    w.first_name,
    w.last_name,
    w.role,
    w.employment_type,
    w.salary,
    w.hire_date,
    s.shelter_name,
    s.city AS shelter_city
FROM workers w
LEFT JOIN shelter s ON w.shelter_id = s.shelter_id
WHERE w.employment_type != 'volunteer';
