insert into product_categories (name) values('Fruits');
insert into product_categories (name) values('Vegetables');
insert into product_categories (name) values('Empty');

insert into products (name, category, calorific, proteins, fats, carbs, defweight) values('Apple', (select id from product_categories where name = 'Fruits'), 57, 4, 1, 17.5, 23);
insert into products (name, category, proteins, fats, carbs, defweight) values('Banana', (select id from product_categories where name = 'Fruits'), 8.6, 1.4, 6.7, 15);
insert into products (name, category, calorific, fats, carbs, defweight) values('Cherry', (select id from product_categories where name = 'Fruits'), 33.7, 2.4, 9.6, 14 );

insert into products (name, category, calorific, proteins, carbs, defweight) values('Potato', (select id from product_categories where name = 'Vegetables'), 77.1, 9.2, 52.3, 50 );
insert into products (name, category, calorific, proteins, fats, defweight) values('Tomato', (select id from product_categories where name = 'Vegetables'), 45.5, 5.5, 2.6, 21 );
insert into products (name, category, calorific, proteins, fats, carbs) values('Cucamber', (select id from product_categories where name = 'Vegetables'), 21.4, 4.3, 1.1, 8.7);
insert into products (name, category, calorific) values('Carrot', (select id from product_categories where name = 'Vegetables'), 67.8 );
