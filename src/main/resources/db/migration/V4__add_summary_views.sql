CREATE VIEW account_totals AS
SELECT
	r.user_id,
	e."account_id",
	SUM(e."amount") AS "amount",
	e."currency_id"
FROM
	"record" r
JOIN "entry" e ON
	r."id" = e."record_id"
GROUP BY
	r.user_id,
	e."currency_id",
	e."account_id";

CREATE VIEW monthly_expense_income AS
SELECT
	r.user_id,
	SUM(e."amount") AS "amount",
	e."currency_id",
	r."type",
	EXTRACT (YEAR FROM r."date") AS "year",
	EXTRACT (MONTH FROM r."date") AS "month"
FROM
	"record" r
JOIN "entry" e ON
	r."id" = e."record_id"
WHERE
	r."type" IN ('EXPENSE', 'INCOME')
GROUP BY
	r.user_id,
	e."currency_id",
	r."type",
	"year",
	"month"
ORDER BY
	r.user_id,
	"year" DESC,
	"month" DESC;