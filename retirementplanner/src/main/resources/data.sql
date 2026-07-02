INSERT INTO  (username, email, password_hash, provider, role) VALUES
    ('kevin', 'kevin@example.com', 'hashed_password_1', 'LOCAL', 'USER'),
    ('abdullah', 'abdullah@example.com', 'hashed_password_2', 'LOCAL', 'USER'),
    ('john', 'john@example.com', 'hashed_password_3', 'LOCAL', 'USER'),
    ('admin', 'admin@example.com','admin.n89','LOCAL', 'ADMIN');

INSERT INTO app_user (username, email, password_hash, provider, role) VALUES
    ('kevin', 'kevin@example.com', '$2y$10$kXQPfkg4vtbpYnnJqLUzh.mLt1C2/TU0FoedJeNLghzCVHYgcpb3a', 'LOCAL', 'USER'),
    ('abdullah', 'abdullah@example.com', '$2y$10$m7MTsdYKpBnMN7V4.dZ/nefdEadRxAg3u2Rmo7eVhM/ZLGroOcG12', 'LOCAL', 'USER'),
    ('john', 'john@example.com', '$2y$10$RSFagnGeDvKZcxb4v2WdT.uRd8DLTTXnvcMLlpTj8pI.rzNAcM3SC', 'LOCAL', 'USER'),
    ('admin', 'admin@example.com','$2y$10$DAHbwDfZyrQEO7blm2zmDekVQlCpiitEO0eKyTSU3vV1xjsvSrnpO','LOCAL', 'ADMIN')


INSERT INTO goal (user_id, name, target_retirement_age, target_amount, notes) VALUES
    (1, 'Early Retirement Plan', 55, 1200000.00, 'Main retirement goal focused on retiring early.'),
    (2, 'Standard Retirement Plan', 67, 900000.00, 'Traditional long-term retirement target.'),
    (3, 'Brokerage Growth Goal', 60, 350000.00, 'Extra taxable investment goal for flexibility.');

INSERT INTO funding_source (user_id, name, source_type, institution, notes) VALUES
    (1, 'Work 401k', 'TRADITIONAL_401K', 'Fidelity', 'Primary employer retirement account.'),
    (1, 'Roth IRA', 'ROTH_IRA', 'Vanguard', 'Personal Roth account.'),
    (2, 'Work 401k', 'TRADITIONAL_401K', 'Fidelity', 'Employer retirement account for standard plan.'),
    (2, 'Old Job Rollover IRA', 'TRADITIONAL_IRA', 'Fidelity', 'Rolled over from previous employer.'),
    (3, 'Taxable Investment Account', 'TAXABLE_BROKERAGE', 'Charles Schwab', 'Flexible brokerage account.');

INSERT INTO contribution_record (
    user_id,
    goal_id,
    funding_source_id,
    amount,
    contribution_date,
    category,
    notes
) VALUES
    (1, 1, 1, 500.00, '2026-01-15', 'EMPLOYEE_SALARY_DEFERRAL', 'January paycheck contribution.'),
    (1, 1, 1, 150.00, '2026-01-15', 'EMPLOYER_MATCH', 'Employer match for January.'),
    (1, 1, 2, 300.00, '2026-02-01', 'EMPLOYEE_SALARY_DEFERRAL', 'Monthly Roth IRA contribution.'),
    (2, 2, 3, 400.00, '2026-02-15', 'EMPLOYEE_SALARY_DEFERRAL', 'Regular 401k contribution.'),
    (2, 2, 4, 2500.00, '2026-03-05', 'ROLLOVER', 'Partial rollover from previous account.'),
    (3, 3, 5, 200.00, '2026-03-20', 'EMPLOYEE_SALARY_DEFERRAL', 'Brokerage investment contribution.'),
    (3, 3, 5, 100.00, '2026-04-20', 'CATCH_UP_CONTRIBUTION', 'Extra contribution for testing category filtering.');