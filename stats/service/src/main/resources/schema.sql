CREATE TABLE if not exists endpoint_hit (
	id SERIAL PRIMARY KEY,
	app varchar NOT NULL,
	"timestamp" timestamp  WITHOUT TIME ZONE NOT NULL,
	uri varchar NOT NULL,
	ip varchar NOT NULL
);
