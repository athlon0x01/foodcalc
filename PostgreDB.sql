﻿--PostgeSQL implementation of Food Calculator

--product related tables
CREATE TABLE product_category (
	id bigserial PRIMARY KEY,
	name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE product(
	id bigserial PRIMARY KEY,
	name varchar(64) NOT NULL,
    description varchar(265),
	category bigint REFERENCES product_category,
	calorific real DEFAULT 0.0,
	proteins real DEFAULT 0.0,
	fats real DEFAULT 0.0,
	carbs real DEFAULT 0.0,
--product default weight in grams
	defweight integer DEFAULT 0
);

--dish related tables
CREATE TABLE dish_categories (
	id bigserial PRIMARY KEY,
	name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE dishes (
	id bigserial PRIMARY KEY,
	name varchar(64) NOT NULL,
	description varchar(1024),
	category integer REFERENCES dish_categories
);

CREATE TABLE dish_products (
	id bigserial PRIMARY KEY,
	dish bigint REFERENCES dishes,
	product bigint REFERENCES product,
--product weight in 0.1 grams
	weight integer NOT NULL
);

--product layout related tables
CREATE TABLE meals (
	id bigserial PRIMARY KEY,
	name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE grocery_layouts (
	id bigserial PRIMARY KEY,
	name varchar(64) NOT NULL,
	members smallint NOT NULL,
	duration smallint,
	description varchar(2048)
);

CREATE TABLE layout_days (
	id bigserial PRIMARY KEY,
	day_index smallint NOT NULL,
	layout bigint REFERENCES grocery_layouts,
	day_date date,
	description varchar(1024)
);

CREATE TABLE grocery_layouts_items (
	id bigserial PRIMARY KEY,
	layout_day bigint REFERENCES layout_days,
	meal bigint REFERENCES meals,
	dish bigint REFERENCES dishes,
	product bigint REFERENCES product,
--product weight in 0.1 grams
	weight integer NOT NULL
);