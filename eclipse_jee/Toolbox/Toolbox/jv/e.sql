select id, fname from hercdb.jv1;

SELECT varchar(CATEGORY,30) "category", 
varchar(SUBCATEGORY,25) "subcat1",
varchar(SUBCATEGORY2,30) "subcat2",
US_CAT, US_CLASS,
varchar(CORE,4) "core",
varchar(CATALOG,10) "catalog",
varchar(DELETED,5) "deltd" 
from hercdb.RENTALEQUIPMENT 
where us_cat=975 
and us_class in (650,651,5502,5503,20,40,60,80,100,6000,6001,6002,6003,6004,6005,6006) 
order by IDNUMBER;

update hercdb.jv1 set id=34645, fname='knjkdbn';
commit;
