
select oag_code, ISO_STATEPROVINCE_CODE, STATEPROVINCE_CODE, address2, address3, address4, city, country_code
 from DB2INST1.FTS_MATERIALIZED_TABLE_ENUS where country_code = 'CN';
 
 select oag_code, ISO_STATEPROVINCE_CODE, STATEPROVINCE_CODE, address2, address3, address4, city, country_code
 from DB2INST1.FTS_MATERIALIZED_TABLE_ENUS where country_code = 'SA';
 
 
 select * from db2inst1.navigation_zhcn where web_context = 'AIR_PARTNERS' ;
 
select description from db2inst1.navigation_zhcn where web_context = 'AIR_PARTNERS';