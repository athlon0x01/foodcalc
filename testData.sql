insert into product_category (name) values('Fruits');
insert into product_category (name) values('Vegetables');
insert into product_category (name) values('Meat');

insert into product (name, category, calorific, proteins, fats, carbs, defweight) values('Apple', (select id from product_category where name = 'Fruits'), 57, 4, 1, 17.5, 23);
insert into product (name, category, proteins, fats, carbs, defweight) values('Banana', (select id from product_category where name = 'Fruits'), 8.6, 1.4, 6.7, 15);
insert into product (name, category, calorific, fats, carbs, defweight) values('Cherry', (select id from product_category where name = 'Fruits'), 33.7, 2.4, 9.6, 14 );

insert into product (name, category, calorific, proteins, carbs, defweight) values('Potato', (select id from product_category where name = 'Vegetables'), 77.1, 9.2, 52.3, 50 );
insert into product (name, category, calorific, proteins, fats, defweight) values('Tomato', (select id from product_category where name = 'Vegetables'), 45.5, 5.5, 2.6, 21 );
insert into product (name, category, calorific, proteins, fats, carbs) values('Cucumber', (select id from product_category where name = 'Vegetables'), 21.4, 4.3, 1.1, 8.7);
insert into product (name, category, calorific) values('Carrot', (select id from product_category where name = 'Vegetables'), 67.8 );

insert into dish_category (name) values('Soups');
insert into dish_category (name) values('Garnish');
insert into dish_category (name) values('Appetizers');
insert into dish_category (name) values('Snacks');
insert into dish_category (name) values('Meat');
insert into dish_category (name) values('Desserts');

insert into meal_type (name) values('breakfast');
insert into meal_type (name) values('lunch');
insert into meal_type (name) values('dinner');
insert into meal_type (name) values('snack');

insert into dish (name, description, category, template) values('borsch', 'soup with tomatos', (select id from dish_category where name = 'Soups'), true);
insert into dish (name, description, category, template) values('buckwheat', 'buckwheat porridge', (select id from dish_category where name = 'Garnish'), true);
insert into dish (name, description, category, template) values('stew', 'stew meat', (select id from dish_category where name = 'Meat'), true);
insert into dish (name, description, category, template) values('borsch day1', 'soup with tomatos day1 lunch', (select id from dish_category where name = 'Soups'), false);


insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch'), (select id from product where name = 'Potato'), 2, 300);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch'), (select id from product where name = 'Tomato'), 0, 100);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch'), (select id from product where name = 'Carrot'), 1, 30);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'buckwheat'), (select id from product where name = 'Carrot'), 0, 60);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch day1'), (select id from product where name = 'Potato'), 2, 111);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch day1'), (select id from product where name = 'Tomato'), 0, 222);
insert into dish_product (dish, product, "ndx", weight) values ((select id from dish where name = 'borsch day1'), (select id from product where name = 'Carrot'), 1, 333);

