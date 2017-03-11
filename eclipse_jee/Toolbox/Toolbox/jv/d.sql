
drop table hercdb.jv1;
create table hercdb.jv1 (
	id bigint not null Constraint pk_jv1 primary key, 
	fname varchar(50)
) in herc_4k;

insert into hercdb.jv1 values (29345, 'jv1 title');
commit;

select id, fname from hercdb.jv1;

