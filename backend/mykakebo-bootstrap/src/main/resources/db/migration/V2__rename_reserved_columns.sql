ALTER TABLE monthly_budgets RENAME COLUMN month TO year_month;

ALTER TABLE monthly_reflections RENAME COLUMN month TO year_month;

ALTER TABLE expenses RENAME COLUMN date TO expense_date;