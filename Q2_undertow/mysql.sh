#!/usr/bin/env bash 

sudo apt-get install mysql-server

mysql -u root -p
create user ''@'localhost' identified by password '';
grant all privileges on *.* to ''@'localhost';

create database data;

USE data;
CREATE TABLE q2table(hashtag VARCHAR(200), text TEXT);

LOAD DATA LOCAL INFILE '~/data.csv' INTO TABLE q2table FIELDS TERMINATED BY '\t' ENCLOSED BY '"' LINES TERMINATED BY '\n';

CREATE INDEX tag ON q2table(hashtag);
