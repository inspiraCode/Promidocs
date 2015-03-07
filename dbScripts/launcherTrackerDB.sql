


CREATE TABLE tracker 
(
     id integer not null primary key identity,
     file_name varchar(70) not null,
     status varchar(15),
     uploadedOn date,
     sourcheLocation varchar(300)
)