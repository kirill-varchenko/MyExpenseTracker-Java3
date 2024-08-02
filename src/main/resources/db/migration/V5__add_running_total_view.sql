CREATE VIEW monthly_balance AS
SELECT
	r.user_id,
	SUM(e."amount") AS "amount",
	e."currency_id",
	EXTRACT (YEAR FROM r."date") :: int AS "year",
	EXTRACT (MONTH FROM r."date") :: int AS "month"
FROM
	"record" r
JOIN "entry" e ON
	r."id" = e."record_id"
GROUP BY
	r.user_id,
	e."currency_id",
	"year",
	"month"
ORDER BY
	r.user_id,
	"year" DESC,
	"month" DESC;

CREATE VIEW running_total AS
SELECT
    user_id,
    currency_id,
    year,
    month,
    SUM(amount) OVER (
        PARTITION BY user_id, currency_id
        ORDER BY year, month
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS amount
FROM
    monthly_balance;