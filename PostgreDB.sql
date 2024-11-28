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
    category    bigint REFERENCES dish_category,
    template    boolean NOT NULL
);

CREATE TABLE dish_product
(
    id      bigserial PRIMARY KEY,
    dish    bigint REFERENCES dish,
    product bigint REFERENCES product,
    ndx     smallint NOT NULL,
--product weight in 0.1 grams
    weight  integer  NOT NULL
);

--meal related tables
CREATE TABLE meal_type
(
    id   bigserial PRIMARY KEY,
    name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE meal
(
    id          bigserial PRIMARY KEY,
    description varchar(265),
    type        bigserial REFERENCES meal_type
);

CREATE TABLE meal_product
(
    id      bigserial PRIMARY KEY,
    meal    bigint REFERENCES meal,
    product bigint REFERENCES product,
    ndx     smallint NOT NULL,
--product weight in 0.1 grams
    weight  integer  NOT NULL
);

CREATE TABLE meal_dish
(
    id   bigserial PRIMARY KEY,
    meal bigint REFERENCES meal,
    dish bigint REFERENCES dish,
    ndx  smallint NOT NULL
);

--layout items
CREATE TABLE food_plan
(
    id          bigserial PRIMARY KEY,
    name        varchar(64)              NOT NULL,
    createdOn   TIMESTAMP WITH TIME ZONE NOT NULL,
    lastUpdated TIMESTAMP WITH TIME ZONE NOT NULL,
    description varchar(2048)
);

CREATE TABLE day_plan
(
    id          bigserial PRIMARY KEY,
    day         date,
    ndx         smallint NOT NULL,
    description varchar(1024),
    plan        bigint REFERENCES food_plan
);

CREATE TABLE day_meal
(
    id   bigserial PRIMARY KEY,
    day  bigint REFERENCES day_plan,
    meal bigint REFERENCES meal,
    ndx  smallint NOT NULL
);

CREATE TABLE day_dish
(
    id   bigserial PRIMARY KEY,
    day  bigint REFERENCES day_plan,
    dish bigint REFERENCES dish,
    ndx  smallint NOT NULL
);

CREATE TABLE day_product
(
    id      bigserial PRIMARY KEY,
    day     bigint REFERENCES day_plan,
    product bigint REFERENCES product,
    ndx     smallint NOT NULL,
--product weight in 0.1 grams
    weight  integer  NOT NULL
);

CREATE TABLE hiker
(
    id          bigserial PRIMARY KEY,
    name        varchar(128),
    description varchar(1024),
    weight_coef real    DEFAULT 1.0,
    plan        bigint REFERENCES food_plan
);