-- Cat Adoption Center - initdata.sql
-- Initial data for all 13 tables


-- 1. cat (22 rows)
INSERT INTO cat (cat_name, breed, age_months, cat_type, gender, color, description, status, intake_date) VALUES
-- id=1
('Gomungi',   'Scottish Fold',      8,  'kitten',        'female', 'white',         'Gets scared easily but warms up fast. Likes hiding under blankets.',   'available',    '2026-01-10'),
-- id=2
('Dubu',      'British Shorthair',  24, 'adult',         'male',   'gray',          'Big and chill. Sits in one spot all day and judges everyone.',          'available',    '2026-01-15'),
-- id=3
('Nabi',      'American Shorthair', 36, 'adult',         'male',   'orange',        'Got into everything when he first arrived. Calmed down after neuter.',  'adopted',      '2025-11-20'),
-- id=4
('Harang',    'Siamese',            12, 'kitten',        'female', 'cream/brown',   'Cries if you leave the room. Very attached.',                           'available',    '2026-02-01'),
-- id=5
('Kkomi',     'Maine Coon',         60, 'senior',        'male',   'black',         'Old but still acts like a kitten sometimes. Eats a lot.',               'available',    '2025-12-05'),
-- id=6
('Soltting',  'Persian',            18, 'adult',         'female', 'cream',         'Hates baths. Gets matted fur easily. Needs regular grooming.',          'foster',       '2026-01-22'),
-- id=7
('Tantan',    'Domestic Shorthair',  4, 'kitten',        'male',   'black/white',   'Bites a lot but its playful not mean. Very fast.',                      'available',    '2026-03-01'),
-- id=8
('Boong',     'Ragdoll',            30, 'adult',         'female', 'brown tabby',   'Falls asleep on you if you stay still long enough.',                    'available',    '2026-02-14'),
-- id=9
('Gureum',    'Norwegian Forest',   48, 'adult',         'male',   'gray/white',    'Upper respiratory infection on intake. On medication. Recovering ok.',  'medical_hold', '2025-10-30'),
-- id=10
('Tteok',     'Munchkin',           15, 'kitten',        'male',   'orange tabby',  'Runs into walls sometimes. Vet says hes fine, just clumsy.',            'available',    '2026-02-20'),
-- id=11
('Somi',      'Turkish Angora',     22, 'adult',         'female', 'white',         'Used to be an outdoor cat. Still wants to look out the window all day.','adopted',      '2025-09-15'),
-- id=12
('Jjang',     'Russian Blue',       84, 'senior',        'male',   'blue-gray',     'Took 3 weeks before he stopped hiding. Now follows staff around.',      'available',    '2026-01-28'),
-- id=13
('Hobak',     'Domestic Longhair',   6, 'kitten',        'female', 'orange',        'Sneezes constantly. Vet cleared her, probably just dust allergy.',       'available',    '2026-03-10'),
-- id=14
('Jommi',     'Tuxedo',             36, 'special_needs', 'male',   'black/white',   'Missing left eye from before intake. Gets around fine, very affectionate.','euthanized', '2025-08-01'),
-- id=15
('Chamchi',   'Bengal',             20, 'adult',         'female', 'spotted brown', 'Jumps on top of every cabinet. Needs a home without small kids.',        'available',    '2026-02-25'),
-- id=16
('Nori',      'Domestic Shorthair', 96, 'senior',        'male',   'gray',          'Found near Mapo station. Stage 4 kidney failure on arrival.',            'euthanized',   '2024-06-01'),
-- id=17
('Bori',      'Persian',            48, 'special_needs', 'female', 'white',         'Breathing problems since intake. Did not improve with treatment.',       'euthanized',   '2024-09-15'),
-- id=18
('Mustang',   'Maine Coon',         72, 'senior',        'male',   'brown tabby',   'Surrendered by owner. Lymphoma diagnosis at intake exam.',              'euthanized',   '2024-11-20'),
-- id=19
('Garang',    'Domestic Longhair',  60, 'senior',        'female', 'gray/white',    'FIV positive. Secondary infections did not respond to antibiotics.',     'euthanized',   '2025-02-10'),
-- id=20
('Dotori',    'Siamese',            36, 'special_needs', 'female', 'cream',         'Neurological symptoms from birth. Deteriorated over 2 months in care.', 'euthanized',   '2025-04-05'),
-- id=21
('Baram',     'Norwegian Forest',   84, 'senior',        'male',   'black',         'Liver values critical on last bloodwork. Owner could not afford surgery.','euthanized',  '2025-06-18'),
-- id=22
('Pudding',   'British Shorthair',  48, 'adult',         'female', 'cream',         'Hit by a car near shelter. Brought in by a passerby. Injuries severe.', 'euthanized',   '2025-10-22');


-- 2. adopter (12 rows)
INSERT INTO adopter (first_name, last_name, email, phone, city, address, birth_year, age) VALUES
('Jiwon',    'Kim',   'jiwon.kim83@naver.com',      '010-3821-4756', 'Seoul',   '123 Gangnam-daero, Gangnam-gu',   1990, 36),
('Minseo',   'Park',  'ms_park85@kakao.com',         '010-9042-3318', 'Busan',   '45 Haeundae-ro, Haeundae-gu',    1985, 41),
('Hyunjun',  'Lee',   'hyunjun.lee@gmail.com',       '010-5573-8820', 'Seoul',   '78 Mapo-daero, Mapo-gu',          1995, 31),
('Sooji',    'Choi',  'sooji_c@daum.net',            '010-2294-6637', 'Incheon', '22 Bupyeong-daero, Bupyeong-gu', 1992, 34),
('Daehyun',  'Jung',  'daehyunj@naver.com',          '010-8841-2295', 'Daegu',   '90 Dongseong-ro, Jung-gu',        1988, 38),
('Yuna',     'Han',   'yuna97han@gmail.com',          '010-4419-5563', 'Seoul',   '5 Itaewon-ro, Yongsan-gu',        1997, 29),
('Seungho',  'Yoon',  'ysh_83@naver.com',            '010-7762-1148', 'Suwon',   '33 Ingye-ro, Paldal-gu',          1983, 43),
('Minji',    'Shin',  'miniminji99@kakao.com',       '010-3305-9974', 'Seoul',   '66 Sinchon-ro, Seodaemun-gu',     1999, 27),
('Junho',    'Lim',   'junho.lim91@gmail.com',        '010-6687-4423', 'Ulsan',   '14 Samsan-ro, Nam-gu',            1991, 35),
('Chaeyeon', 'Oh',    'chaeyeon_oh@naver.com',        '010-1158-7736', 'Seoul',   '200 Songpa-daero, Songpa-gu',     1994, 32),
('Taehoon',  'Bae',   'th.bae87@daum.net',           '010-9923-3381', 'Gwangju', '77 Chungjang-ro, Dong-gu',        1987, 39),
('Eunji',    'Kwon',  'eunjikwon96@gmail.com',       '010-4470-8812', 'Seoul',   '11 Apgujeong-ro, Gangnam-gu',     1996, 30);


-- 3. shelter (10 rows)
INSERT INTO shelter (shelter_name, city, manager_name) VALUES
('Mapo Cat Care Center',       'Seoul',   'Kim Sangwon'),
('Haeundae Animal Shelter',    'Busan',   'Lee Jinyoung'),
('Incheon City Cat Rescue',    'Incheon', 'Park Sooyeon'),
('Daegu Stray Cat Foundation', 'Daegu',   'Choi Minho'),
('Suwon Pet Welfare Center',   'Suwon',   'Jung Haerin'),
('Gwangju Animal Care Assoc',  'Gwangju', 'Oh Seungmin'),
('Ulsan Cat Rescue Network',   'Ulsan',   'Yoon Jisoo'),
('Jeju Island Animal Shelter', 'Jeju',    'Han Mirae'),
('Sejong City Pet Center',     'Sejong',  'Lim Dohyun'),
('Jeonju Animal Welfare Org',  'Jeonju',  'Shin Areum');


-- 4. fee_schedule (12 rows)
--
--   fee_id  cat_type      period              price
--   1       kitten        2025-01-01~06-30    120.00
--   2       kitten        2025-07-01~12-31    135.00
--   3       kitten        2026-01-01~now      150.00
--   4       adult         2025-01-01~06-30     90.00
--   5       adult         2025-07-01~12-31    100.00
--   6       adult         2026-01-01~now      110.00
--   7       senior        2025-01-01~06-30     50.00
--   8       senior        2025-07-01~12-31     60.00
--   9       senior        2026-01-01~now       70.00
--   10      special_needs 2025-01-01~06-30     30.00
--   11      special_needs 2025-07-01~12-31     40.00
--   12      special_needs 2026-01-01~now       50.00
INSERT INTO fee_schedule (cat_type, unit_price, effective_from, effective_to, changed_by, change_reason) VALUES
('kitten',        120.00, '2025-01-01', '2025-06-30', 'Kim Sangwon',  'Opening fee set at launch'),
('kitten',        135.00, '2025-07-01', '2025-12-31', 'Lee Jinyoung', 'Raised due to higher demand in summer'),
('kitten',        150.00, '2026-01-01', NULL,          'Kim Sangwon',  'New year fee update'),
('adult',          90.00, '2025-01-01', '2025-06-30', 'Kim Sangwon',  'Opening fee set at launch'),
('adult',         100.00, '2025-07-01', '2025-12-31', 'Lee Jinyoung', 'Adjusted after vet cost review'),
('adult',         110.00, '2026-01-01', NULL,          'Kim Sangwon',  'New year fee update'),
('senior',         50.00, '2025-01-01', '2025-06-30', 'Kim Sangwon',  'Opening fee set at launch'),
('senior',         60.00, '2025-07-01', '2025-12-31', 'Lee Jinyoung', 'Small increase to cover care costs'),
('senior',         70.00, '2026-01-01', NULL,          'Kim Sangwon',  'New year fee update'),
('special_needs',  30.00, '2025-01-01', '2025-06-30', 'Kim Sangwon',  'Opening fee set at launch'),
('special_needs',  40.00, '2025-07-01', '2025-12-31', 'Lee Jinyoung', 'Raised slightly, still subsidized'),
('special_needs',  50.00, '2026-01-01', NULL,          'Kim Sangwon',  'New year fee update');


-- 5. workers (12 rows)
INSERT INTO workers (first_name, last_name, email, phone, role, employment_type, shelter_id, hire_date, salary) VALUES
('Sangwon', 'Kim',    'sangwon.k@mapocat.org',      '010-3847-2291', 'admin',       'full_time', 1, '2020-03-01', 3500000.00),
('Jinyoung','Lee',    'jy.lee@haeundaeshelter.org', '010-6612-5548', 'admin',       'full_time', 2, '2019-07-15', 3400000.00),
('Sooyeon', 'Park',   'sypark@incheonrescue.org',   '010-9923-7714', 'coordinator', 'full_time', 1, '2021-01-10', 2800000.00),
('Minho',   'Choi',   'dr.choi@mapocat.org',        '010-2238-4409', 'vet',         'full_time', 1, '2020-06-01', 4500000.00),
('Haerin',  'Jung',   'haerin.j@suwonpet.org',      '010-5571-8836', 'admin',       'full_time', 5, '2022-02-20', 3200000.00),
('Yoojin',  'Seo',    'vet.yoojin@haeundaeshelter.org','010-7784-3325','vet',        'full_time', 2, '2021-09-05', 4300000.00),
('Donghun', 'Kang',   'kang.dh@mapocat.org',        '010-4456-9901', 'caretaker',   'part_time', 3, '2023-03-15', 1800000.00),
('Nayeon',  'Im',     'nayeon.im@incheonrescue.org','010-8813-6672', 'caretaker',   'full_time', 4, '2022-08-01', 2200000.00),
('Beomjun', 'Hwang',  'bj.hwang@volunteer.net',     '010-3390-5547', 'volunteer',   'volunteer', 6, '2024-01-01', NULL),
('Seoyeon', 'Cho',    'sy.cho@volunteer.net',        '010-6625-1183', 'volunteer',   'volunteer', 7, '2024-03-10', NULL),
('Jihoon',  'Ryu',    'jihoon.r@jejushelter.org',   '010-1147-8890', 'coordinator', 'full_time', 8, '2021-11-20', 2700000.00),
('Areum',   'Son',    'areum.vet@sejongpet.org',    '010-9982-4416', 'vet',         'part_time', 9, '2023-05-01', 3100000.00);


-- 6. adoption_transaction (20 rows)
INSERT INTO adoption_transaction (transaction_timestamp, shelter_id, adopter_id, basket_id) VALUES
('2025-02-15 10:30:00', 1, 1,  1001),
('2025-03-18 14:00:00', 2, 2,  1002),
('2025-04-25 11:15:00', 1, 3,  1003),
('2025-05-02 09:45:00', 3, 4,  1004),
('2025-05-20 15:30:00', 1, 5,  1005),
('2025-06-10 13:00:00', 4, 6,  1006),
('2025-08-14 10:00:00', 2, 7,  1007),
('2025-09-20 16:30:00', 1, 8,  1008),
('2025-10-25 11:00:00', 5, 9,  1009),
('2025-11-01 14:30:00', 1, 10, 1010),
('2025-11-05 09:00:00', 2, 11, 1011),
('2025-12-08 15:00:00', 3, 12, 1012),
('2026-01-10 10:30:00', 1, 1,  1013),
('2026-01-12 13:45:00', 4, 2,  1014),
('2026-02-15 11:00:00', 5, 3,  1015),
('2026-02-18 14:00:00', 1, 4,  1016),
('2026-03-20 10:00:00', 2, 5,  1017),
('2026-03-22 15:30:00', 3, 6,  1018),
('2026-04-25 09:30:00', 1, 7,  1019),
('2026-04-28 16:00:00', 4, 8,  1020);


-- 7. adoption_basket_items (25 rows)
--    basket_id refs adoption_transaction.basket_id
--    cat_id refs cat (1-15 only, euthanized cats not in baskets)
--    fee_id refs fee_schedule:
--      Jan-Jun 2025: kitten=1, adult=4, senior=7, special=10
--      Jul-Dec 2025: kitten=2, adult=5, senior=8, special=11
--      2026+:        kitten=3, adult=6, senior=9, special=12
INSERT INTO adoption_basket_items (basket_id, cat_id, fee_id, quantity, unit_price_at_sale) VALUES
(1001, 3,  4, 1,  90.00),
(1002, 11, 4, 1,  90.00),
(1003, 1,  1, 1, 120.00),
(1004, 4,  1, 1, 120.00),
(1005, 2,  4, 1,  90.00),
(1006, 6,  4, 1,  90.00),
(1007, 5,  8, 1,  60.00),
(1008, 8,  5, 1, 100.00),
(1009, 10, 2, 1, 135.00),
(1010, 12, 8, 1,  60.00),
(1011, 7,  2, 1, 135.00),
(1012, 13, 2, 1, 135.00),
(1013, 15, 6, 1, 110.00),
(1014, 9,  6, 1, 110.00),
(1015, 4,  3, 1, 150.00),
(1016, 1,  3, 1, 150.00),
(1017, 2,  6, 1, 110.00),
(1018, 8,  6, 1, 110.00),
(1019, 10, 3, 1, 150.00),
(1020, 12, 9, 1,  70.00),
(1003, 7,  1, 1, 120.00),
(1005, 13, 1, 1, 120.00),
(1010, 15, 5, 1, 100.00),
(1015, 5,  9, 1,  70.00),
(1019, 6,  6, 1, 110.00);


-- 8. total_adoption_fees (20 rows)
INSERT INTO total_adoption_fees (basket_id, total_amount) VALUES
(1001,  90.00),
(1002,  90.00),
(1003, 240.00),
(1004, 120.00),
(1005, 210.00),
(1006,  90.00),
(1007,  60.00),
(1008, 100.00),
(1009, 135.00),
(1010, 160.00),
(1011, 135.00),
(1012, 135.00),
(1013, 110.00),
(1014, 110.00),
(1015, 220.00),
(1016, 150.00),
(1017, 110.00),
(1018, 110.00),
(1019, 260.00),
(1020,  70.00);


-- 9. medical_records (15 rows)
INSERT INTO medical_records (cat_id, worker_id, record_date, type, notes) VALUES
(1,  4, '2026-01-12', 'checkup',   'Weight normal. Ears clean. No issues found.'),
(2,  4, '2026-01-16', 'vaccine',   'FVRCP booster done. Cat was calm during procedure.'),
(3,  6, '2025-11-22', 'checkup',   'Pre-adoption check. Passed. Ready for adoption.'),
(4,  4, '2026-02-03', 'vaccine',   'Rabies shot administered. Mild reaction, monitored 30min.'),
(5,  6, '2025-12-08', 'treatment', 'Small bald patch on back. Antifungal applied. Check in 2 weeks.'),
(6,  4, '2026-01-24', 'checkup',   'Coat heavily matted on intake. Groomed. Overall healthy.'),
(7,  4, '2026-03-02', 'vaccine',   'First FVRCP shot. Bit the vet. Normal for age.'),
(8,  6, '2026-02-16', 'checkup',   'Routine check. Nothing notable. Good weight.'),
(9,  4, '2025-11-01', 'surgery',   'Neutered. Recovery normal. Released back to pen same day.'),
(9,  4, '2026-01-05', 'checkup',   'Follow-up post-neuter. Healed well. Still on respiratory meds.'),
(10, 12,'2026-02-22', 'vaccine',   'FVRCP administered. Cat sneezed the whole time.'),
(11, 6, '2025-09-17', 'checkup',   'Pre-adoption exam. Healthy. Slight tartar on molars noted.'),
(12, 4, '2026-01-30', 'treatment', 'Ear mites confirmed. Treated with drops. Recheck in 10 days.'),
(13, 12,'2026-03-11', 'vaccine',   'First vaccine in series. Very small for age. Will monitor weight.'),
(15, 4, '2026-02-27', 'checkup',   'Scratched staff during exam. Healthy otherwise. High energy.');


-- 10. euthanization_records (10 rows)
INSERT INTO euthanization_records
    (cat_id, worker_id, authorized_by, scheduled_date, performed_date,
     reason, method, second_opinion, consent_form, notes) VALUES
(14, 4, 1, '2025-07-30', '2025-08-01',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'FIP confirmed. Consulted with external vet. No treatment options at this stage.'),
(16, 4, 1, '2024-05-28', '2024-06-01',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Stage 4 CKD. Cat stopped eating. Agreed by both vets on site.'),
(17, 6, 2, '2024-09-14', '2024-09-15',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Respiratory failure. Cat showed no improvement after 3 weeks of care.'),
(18, 4, 1, '2024-11-18', '2024-11-20',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Lymphoma. Owner surrendered knowing diagnosis. Second vet agreed.'),
(19, 6, 1, '2025-02-08', '2025-02-10',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'FIV with severe infections. Three rounds of antibiotics failed.'),
(20, 4, 2, '2025-04-03', '2025-04-05',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Neurological deterioration. Cat could not eat or walk unassisted.'),
(21, 4, 1, '2025-06-16', '2025-06-18',
    'terminal_illness', 'Pentobarbital injection', FALSE, TRUE,
    'Liver failure. Owner could not pay for surgery. Single vet decision.'),
(22, 6, 2, '2025-10-20', '2025-10-22',
    'severe_injury',    'Pentobarbital injection', TRUE,  TRUE,
    'Hit by car. Both hind legs fractured. Internal bleeding confirmed.'),
(14, 6, 2, '2025-07-28', '2025-08-01',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Second vet review for Jommi before procedure. Confirmed FIP findings.'),
(20, 6, 1, '2025-04-02', '2025-04-05',
    'terminal_illness', 'Pentobarbital injection', TRUE,  TRUE,
    'Second opinion for Dotori. Neurological damage confirmed as irreversible.');


-- 11. salary_history (10 rows)
INSERT INTO salary_history (worker_id, old_salary, new_salary, change_date, reason) VALUES
(1,  3200000.00, 3500000.00, '2024-01-01', 'Yearly review increase'),
(2,  3100000.00, 3400000.00, '2024-01-01', 'Yearly review increase'),
(3,  2500000.00, 2800000.00, '2024-06-01', 'Role change to coordinator'),
(4,  4000000.00, 4500000.00, '2024-01-01', 'Yearly review increase'),
(5,  3000000.00, 3200000.00, '2025-01-01', 'Yearly review increase'),
(6,  4000000.00, 4300000.00, '2024-06-01', 'Extended contract + performance'),
(7,  1600000.00, 1800000.00, '2025-03-01', 'Minimum wage adjustment'),
(8,  2000000.00, 2200000.00, '2024-09-01', 'Yearly review increase'),
(11, 2400000.00, 2700000.00, '2025-01-01', 'Yearly review increase'),
(12, 2800000.00, 3100000.00, '2025-06-01', 'Upgraded from intern to part-time vet');


-- 12. schedules (15 rows)
INSERT INTO schedules (worker_id, work_date, shift_start, shift_end) VALUES
(1,  '2026-05-26', '09:00:00', '18:00:00'),
(3,  '2026-05-26', '09:30:00', '18:30:00'),
(4,  '2026-05-26', '10:00:00', '19:00:00'),
(7,  '2026-05-26', '12:00:00', '17:00:00'),
(9,  '2026-05-26', '10:00:00', '15:00:00'),
(1,  '2026-05-27', '09:00:00', '18:00:00'),
(4,  '2026-05-27', '10:00:00', '19:00:00'),
(8,  '2026-05-27', '08:30:00', '17:30:00'),
(10, '2026-05-27', '13:00:00', '17:00:00'),
(3,  '2026-05-28', '09:30:00', '18:30:00'),
(6,  '2026-05-28', '10:00:00', '19:00:00'),
(7,  '2026-05-28', '12:00:00', '17:00:00'),
(11, '2026-05-28', '09:00:00', '18:00:00'),
(12, '2026-05-29', '10:00:00', '15:30:00'),
(9,  '2026-05-29', '09:00:00', '13:00:00');


-- 13. adopter_history (10 rows)
INSERT INTO adopter_history (adopter_id, snapshot_date, city, address, birth_year, age, change_reason) VALUES
(1,  '2025-06-01', 'Busan',   '10 Centum-ro, Haeundae-gu',       1990, 35, 'Relocated to Seoul for work'),
(3,  '2025-09-15', 'Incheon', '5 Guwol-ro, Namdong-gu',          1995, 30, 'Was living in Incheon before moving to Seoul'),
(5,  '2025-03-01', 'Seoul',   '30 Teheran-ro, Gangnam-gu',        1988, 37, 'Left Seoul, moved to Daegu with family'),
(6,  '2024-12-01', 'Busan',   '77 Gwangbok-ro, Jung-gu',          1997, 27, 'Previous address before moving to Seoul'),
(7,  '2024-07-10', 'Seoul',   '100 Dobong-ro, Dobong-gu',         1983, 41, 'Moved out of Seoul to Suwon'),
(2,  '2025-01-20', 'Daegu',   '15 Dongdaegu-ro, Dong-gu',         1985, 40, 'Moved from Daegu to Busan for new job'),
(8,  '2026-01-05', 'Incheon', '3 Sinpo-ro, Jung-gu',              1999, 26, 'Old Incheon address, now in Seoul'),
(10, '2025-11-10', 'Suwon',   '88 Gwonseon-ro, Gwonseon-gu',      1994, 31, 'Moved from Suwon to Seoul for school'),
(4,  '2025-08-22', 'Seoul',   '40 Bupyeong-daero, Bupyeong-gu',  1992, 33, 'Transferred to Incheon branch at work'),
(11, '2025-04-15', 'Busan',   '22 Nampo-ro, Jung-gu',             1987, 38, 'Left Busan, settling in Gwangju');
