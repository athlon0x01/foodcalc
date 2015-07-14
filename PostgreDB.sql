--PostgeSQL implementation of Food Calculator

--product related tables
CREATE TABLE product_categories (
	id smallserial PRIMARY KEY,
	name varchar(64) NOT NULL
);

CREATE TABLE products (
	id serial PRIMARY KEY,
	name varchar(64) NOT NULL,
	category smallint REFERENCES product_categories,
	calorific real DEFAULT 0.0,
	proteins real DEFAULT 0.0,
	fats real DEFAULT 0.0,
	carbs real DEFAULT 0.0,
	defweight real DEFAULT 0.0
);

--dish related tables
CREATE TABLE dish_categories (
	id smallserial PRIMARY KEY,
	name varchar(64) NOT NULL
);

CREATE TABLE dishes (
	id serial PRIMARY KEY,
	name varchar(64) NOT NULL,
	description varchar(1024),
	category smallint REFERENCES dish_categories
);

CREATE TABLE dish_products (
	id serial PRIMARY KEY,
	dish integer REFERENCES dishes,
	product integer REFERENCES products,
	weight real NOT NULL
);

--product layout related tables
CREATE TABLE meals (
	id smallserial PRIMARY KEY,
	name varchar(64) NOT NULL
);

CREATE TABLE grocery_layouts (
	id serial PRIMARY KEY,
	name varchar(64) NOT NULL,
	members smallint NOT NULL,
	duration smallint,
	description varchar(2048)
);

CREATE TABLE layout_days (
	id serial PRIMARY KEY,
	day_index smallint NOT NULL,
	layout integer REFERENCES grocery_layouts,
	day_date date,
	description varchar(1024)
);

CREATE TABLE grocery_layouts_items (
	id serial PRIMARY KEY,
	layout_day integer REFERENCES layout_days,
	meal smallint REFERENCES meals,
	dish integer REFERENCES dishes,
	product integer REFERENCES products,
	weight real NOT NULL
);