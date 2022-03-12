--PostgeSQL implementation of Food Calculator

--product related tables
CREATE TABLE product_category
(
    id   bigserial PRIMARY KEY,
    name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE product
(
    id          bigserial PRIMARY KEY,
    name        varchar(64) NOT NULL,
    description varchar(265),
    category    bigint REFERENCES product_category,
    calorific   real    DEFAULT 0.0,
    proteins    real    DEFAULT 0.0,
    fats        real    DEFAULT 0.0,
    carbs       real    DEFAULT 0.0,
--product default weight in grams
    defweight   integer DEFAULT 0
);

--dish related tables
CREATE TABLE dish_category
(
    id   bigserial PRIMARY KEY,
    name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE dish
(
    id          bigserial PRIMARY KEY,
    name        varchar(64) NOT NULL,
    description varchar(1024),
    category    bigint REFERENCES dish_category
);

CREATE TABLE dish_product
(
    id      bigserial PRIMARY KEY,
    dish    bigint REFERENCES dish,
    product bigint REFERENCES product,
--product weight in 0.1 grams
    weight  integer NOT NULL
);

--meal related tables
CREATE TABLE meal_type
(
    id   serial PRIMARY KEY,
    name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE meal
(
    id          bigserial PRIMARY KEY,
    description varchar(265),
    type        integer REFERENCES meal_type
);

CREATE TABLE meal_product
(
    id      bigserial PRIMARY KEY,
    meal    bigint REFERENCES meal,
    product bigint REFERENCES product,
--product weight in 0.1 grams
    weight  integer NOT NULL
);

CREATE TABLE meal_dish
(
    id   bigserial PRIMARY KEY,
    meal bigint REFERENCES meal,
    dish bigint REFERENCES dish
);

--layout items
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
	meal bigint REFERENCES meal,
	dish bigint REFERENCES dish,
	product bigint REFERENCES product,
--product weight in 0.1 grams
	weight integer NOT NULL
);