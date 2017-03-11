
select * from `jv_schema_1`.`KNIGHT_TOTALS`;
select * from `jv_schema_1`.`KNIGHT_SUBTOTALS`;
select * from `jv_schema_1`.`KNIGHT_MOVES`;

select * from `jv_schema_1`.`KNIGHT_TOTALS` where SIZE_X_POS = 7 and SIZE_Y_POS = 3 order by id;

delete from `jv_schema_1`.`KNIGHT_TOTALS` where SIZE_X_POS = 7 and SIZE_Y_POS = 3 order by id;

select count(*) from `jv_schema_1`.`KNIGHT_TOTALS`;
select count(*) from `jv_schema_1`.`KNIGHT_SUBTOTALS`;
select count(*) from `jv_schema_1`.`KNIGHT_MOVES`;

select * from `jv_schema_1`.`KNIGHT_TOTALS` where id = 1403300797891;
select * from `jv_schema_1`.`KNIGHT_SUBTOTALS` where id = 1403300797891;
select * from `jv_schema_1`.`KNIGHT_MOVES` where id = 1403300797891;

select count(*) from `jv_schema_1`.`KNIGHT_SUBTOTALS` where id = 1403300797891;

delete from `jv_schema_1`.`KNIGHT_TOTALS` where id = 1403300797891;
delete from `jv_schema_1`.`KNIGHT_SUBTOTALS` where id = 1403300797891;
delete from `jv_schema_1`.`KNIGHT_MOVES` where id = 1403300797891;