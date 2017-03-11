
drop table `jv_schema_1`.`KNIGHT_TOTALS`;
drop table `jv_schema_1`.`KNIGHT_SUBTOTALS`;
drop table `jv_schema_1`.`KNIGHT_MOVES`;

--- One record for each {size(x, y) , start (x, y)}

create table `jv_schema_1`.`KNIGHT_TOTALS` (
	`ID` bigint not null,
	`SIZE_X_POS` integer not null,
	`SIZE_Y_POS` integer not null,
	`START_X_POS` integer not null,
	`START_Y_POS` integer not null,
	`SOLUTIONS` bigint not null,
	`TOTAL_MOVES` bigint not null,
	`START_DATE` timestamp not null,
	`TIMING` bigint not null,
	primary key (`id`)
);

--- one record for each solution for a given scenario

create table `jv_schema_1`.`KNIGHT_SUBTOTALS` (
	`ID` bigint not null,
	`SOLUTION_NUMBER` bigint not null,
	`TOTAL_MOVES` bigint not null,
	`TIMING` bigint not null,
	primary key (`id`, `solution_number`)
);

--- one record for each move for each solution found

create table `jv_schema_1`.`KNIGHT_MOVES` (
	`ID` bigint not null,
	`SOLUTION_NUMBER` bigint not null,
	`MOVE_COUNTER` integer not null,
	`X_POS` integer not null,
	`Y_POS` integer not null,
	`MOVE_AWAY_TYPE` integer not null,
	`FROM_X_POS` integer not null,
	`FROM_Y_POS` integer not null,
	primary key (`id`, `solution_number`, `move_counter`)
);

---

describe table `jv_schema_1`.`KNIGHT_TOTALS`;
describe table `jv_schema_1`.`KNIGHT_MOVES`;
describe table `jv_schema_1`.`KNIGHT_SUBTOTALS`;

--- 

delete from `jv_schema_1`.`KNIGHT_TOTALS`;
delete from `jv_schema_1`.`KNIGHT_MOVES`;
delete from `jv_schema_1`.`KNIGHT_SUBTOTALS`;

---
