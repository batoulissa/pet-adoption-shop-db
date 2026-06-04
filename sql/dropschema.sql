-- ============================================================
-- Cat Adoption Center - dropschema.sql
-- Drop in reverse FK order to avoid constraint errors
-- ============================================================

DROP VIEW  IF EXISTS v_worker_salary;
DROP VIEW  IF EXISTS v_available_cats;

DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS salary_history;
DROP TABLE IF EXISTS euthanization_records;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS total_adoption_fees;
DROP TABLE IF EXISTS adoption_basket_items;
DROP TABLE IF EXISTS adoption_transaction;
DROP TABLE IF EXISTS adopter_history;
DROP TABLE IF EXISTS workers;
DROP TABLE IF EXISTS fee_schedule;
DROP TABLE IF EXISTS shelter;
DROP TABLE IF EXISTS adopter;
DROP TABLE IF EXISTS cat;
