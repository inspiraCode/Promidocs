


CREATE TABLE tracker 
(
     id integer not null primary key identity,
     file_name varchar(70) not null,
     status varchar(15),
     uploaded_on date,
     source_location varchar(300)
)