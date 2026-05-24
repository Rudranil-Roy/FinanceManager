MERGE INTO categories (name, type, is_custom, user_id)
    KEY(name)
    VALUES
    ('Salary', 'INCOME', false, NULL),
    ('Food', 'EXPENSE', false, NULL),
    ('Rent', 'EXPENSE', false, NULL),
    ('Transportation', 'EXPENSE', false, NULL),
    ('Entertainment', 'EXPENSE', false, NULL),
    ('Healthcare', 'EXPENSE', false, NULL),
    ('Utilities', 'EXPENSE', false, NULL);