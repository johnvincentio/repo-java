/*
create table hercdb.CompanyAccounts (
	accountid bigint not null Constraint pk_CompanyAccounts primary key,
	companyid bigint not null, 

	approved varchar(1) not null,
	active varchar(1) not null,

	account varchar(20) not null,
	countryCode int not null,
	accountType int not null,
	narpAccountId bigint,
	narpManager varchar(100),

) in herc_4k;

create table Hercdb.Members (
	memberid bigint not null Constraint pk_Members primary key,
	username varchar(128) not null, 
	companyid bigint not null,
) in herc_4k;

create table hercdb.MemberAccounts (
	memberid bigint not null,
	accountid bigint not null,
	approved varchar(1) not null,
	approverid bigint not null,
	Constraint pk_MemberAccounts primary key (memberid, accountid)
) in herc_4k;

*/

/*
cannot handle a narp:
select m.USERNAME, m.MEMBERID, ca.accountid, ca.ACCOUNT, ca.countryCode, ca.ACCOUNTTYPE 
FROM HERCDB.MEMBERS as m left join HERCDB.MEMBERACCOUNTS as mm on m.MEMBERID = mm.MEMBERID 
join HERCDB.COMPANYACCOUNTS as ca on mm.ACCOUNTID = ca.ACCOUNTID 
where m.username = 'aaaa';

Execute SQL on any one Production center

Please provide 4 different CSV data sets, one for each SQL statement:


Good:
select narp.accountid, narp.ACCOUNT, narp.countryCode, narp.ACCOUNTTYPE, narp.narpAccountId from hercdb.CompanyAccounts narp where narp.ACCOUNTTYPE = 2;

Good:
select narp.accountid, narp.ACCOUNT, narp.countryCode, narp.ACCOUNTTYPE, 
ca.accountid, ca.ACCOUNT, ca.countryCode, ca.ACCOUNTTYPE, ca.narpAccountId 
from hercdb.CompanyAccounts narp, hercdb.CompanyAccounts ca 
where narp.ACCOUNTTYPE = 2 
and ca.narpAccountId = narp.accountid 
order by narp.accountid, ca.accountid;

Good:
select m.username, m.memberid, ma.accountid 
from HERCDB.MEMBERS m, HERCDB.MEMBERACCOUNTS ma 
where m.memberid = ma.memberid 
order by m.username;

Good:
select accountid, approved, active, account, countryCode, accountType, narpAccountId 
from hercdb.CompanyAccounts 
order by accountid;



	


*/
