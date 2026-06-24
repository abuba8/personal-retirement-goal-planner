INSERT INTO app_user (username, email, password_hash) VALUES
    ('kevin', 'kevin@example.com', 'hashed_password_1'),
    ('abdullah', 'abdullah@example.com', 'hashed_password_2'),
    ('john', 'john@example.com', 'hashed_password_3');

INSERT INTO goal (user_id, name, target_retirement_age, target_amount, notes) VALUES
    (1, 'Early Retirement Plan', 55, 1200000.00, 'Main retirement goal focused on retiring early.'),
    (2, 'Standard Retirement Plan', 67, 900000.00, 'Traditional long-term retirement target.'),
    (3, 'Brokerage Growth Goal', 60, 350000.00, 'Extra taxable investment goal for flexibility.');

INSERT INTO funding_source (user_id, name, source_type, institution, notes) VALUES
    (1, 'Work 401k', 'Traditional 401k', 'Fidelity', 'Primary employer retirement account.'),
    (1, 'Roth IRA', 'Roth IRA', 'Vanguard', 'Personal Roth account.'),
    (2, 'Work 401k', 'Traditional 401k', 'Fidelity', 'Employer retirement account for standard plan.'),
    (2, 'Old Job Rollover IRA', 'Traditional IRA', 'Fidelity', 'Rolled over from previous employer.'),
    (3, 'Taxable Investment Account', 'Taxable Brokerage', 'Charles Schwab', 'Flexible brokerage account.');

INSERT INTO contribution_record (
    user_id,
    goal_id,
    funding_source_id,
    amount,
    contribution_date,
    category,
    notes
) VALUES
    (1, 1, 1, 500.00, '2026-01-15', 'Employee Salary Deferral', 'January paycheck contribution.'),
    (1, 1, 1, 150.00, '2026-01-15', 'Employer Match', 'Employer match for January.'),
    (1, 1, 2, 300.00, '2026-02-01', 'Employee Salary Deferral', 'Monthly Roth IRA contribution.'),
    (2, 2, 3, 400.00, '2026-02-15', 'Employee Salary Deferral', 'Regular 401k contribution.'),
    (2, 2, 4, 2500.00, '2026-03-05', 'Rollover', 'Partial rollover from previous account.'),
    (3, 3, 5, 200.00, '2026-03-20', 'Employee Salary Deferral', 'Brokerage investment contribution.'),
    (3, 3, 5, 100.00, '2026-04-20', 'Catch-up Contribution', 'Extra contribution for testing category filtering.');