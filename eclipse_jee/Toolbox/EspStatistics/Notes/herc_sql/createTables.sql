
-- this script is to create the basic application database objects

-- going into LDAP

-- LDAPMembers.companyAdmin - Y is true

create table Hercdb.LDAPMembers (
	username varchar(128) not null Constraint pk_LDAPMembers primary key, 
	active varchar(1) not null,
	approval varchar(1) not null,
	role varchar(50) not null, 
	reports varchar(200),
	password varchar(254) not null, 
	question varchar(254), 
	answer varchar(254), 
	temporarypassword varchar(1), 
	failurecounter int,
	dialect varchar(4),
	prefix varchar(10),
	fname varchar(15), 
	mname varchar(1), 
	lname varchar(25), 
	suffix varchar(10), 
	title varchar(25), 
	bname varchar(240), 
	address1 varchar(50), 
	address2 varchar(50), 
	city varchar(50),
	country varchar(50), 
	zip varchar(50), 
	email varchar(128), 
	phone varchar(50), 
	fax varchar(50),
	ponumber varchar(250), 
	pcardtype varchar(250), 
	pcardnumber varchar(250), 
	expmonth varchar(250),
	expyear varchar(250), 
	seccode varchar(250),
	release varchar(1),
	extend varchar(1),
	fleet varchar(1),
	updateDate date,
	approver varchar(240),
	optIn varchar(1)
) in herc_4k;

create table Hercdb.LDAPMembers_TEMP (
	username varchar(128) not null Constraint pk_LDAPMemberT primary key, 
	active varchar(1) not null,
	approval varchar(1) not null,
	role varchar(50) not null, 
	reports varchar(200),
	password varchar(254) not null, 
	question varchar(254), 
	answer varchar(254), 
	temporarypassword varchar(1), 
	failurecounter int,
	dialect varchar(4),
	prefix varchar(10),
	fname varchar(15), 
	mname varchar(1), 
	lname varchar(25), 
	suffix varchar(10), 
	title varchar(25), 
	bname varchar(240), 
	address1 varchar(50), 
	address2 varchar(50), 
	city varchar(50),
	country varchar(50), 
	zip varchar(50), 
	email varchar(128), 
	phone varchar(50), 
	fax varchar(50),
	ponumber varchar(250), 
	pcardtype varchar(250), 
	pcardnumber varchar(250), 
	expmonth varchar(250),
	expyear varchar(250), 
	seccode varchar(250),
	release varchar(1),
	extend varchar(1),
	fleet varchar(1),
	updateDate date,
	approver varchar(240),
	optIn varchar(1)
) in herc_4k;



-- Company Database objects

-- Companies.companyid is hercdb.Companies_seq
-- Companies.active 'A', 'D', 'L' Active, Disabled, Locked
-- Companies.type 1,2,3 Construction, Industrial, Fragmented
-- Companies.demo 'Y' = demo company, else not demo

create table hercdb.Companies (
	companyid bigint not null Constraint pk_Companies primary key, 
	companyname varchar(255) not null,
	active varchar(1) not null,
	type int not null,
	annualVolume varchar(50),
	ytdVolume varchar(50),
	potentialVolume varchar(50),
	comments varchar(255),
	reason_denial varchar(255),
	demo varchar(1)
) in herc_4k;

create table hercdb.Companies_TEMP (
	companyid bigint not null Constraint pk_CompanieT primary key, 
	companyname varchar(255) not null,
	active varchar(1) not null,
	type int not null,
	annualVolume varchar(50),
	ytdVolume varchar(50),
	potentialVolume varchar(50),
	comments varchar(255),
	reason_denial varchar(255),
	demo varchar(1)
) in herc_4k;

commit;

-- Company Accounts

-- CompanyAccounts.approved - 'A', 'P', 'D' Approved, Pending, Denied
-- CompanyAccounts.active - 'A', 'D' Active, Disabled
-- CompanyAccounts.account - From RentalMan
-- CompanyAccounts.countryCode - Get from RentalMan, 1 US, 2 CA
-- CompanyAccounts.accountType - 1,2,3 Account, Narp, Narp Member
-- CompanyAccounts.narpAccountId - if is a member, this is the Narp account id
-- CompanyAccounts.creatorFirst and creatorLast - memberId of creator of the record	

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

	creatorFirst varchar(50) not null,
	creatorLast varchar(50) not null,

	displayRates varchar(1),
	release varchar(1),
	extend varchar(1),
	fleet varchar(1),
	kpi varchar(1)
) in herc_4k;

create table hercdb.CompanyAccounts_TEMP (
	accountid bigint not null Constraint pk_CompanyAccountT primary key,
	companyid bigint not null, 

	approved varchar(1) not null,
	active varchar(1) not null,

	account varchar(20) not null,
	countryCode int not null,
	accountType int not null,
	narpAccountId bigint,
	narpManager varchar(100),

	creatorFirst varchar(50) not null,
	creatorLast varchar(50) not null,

	displayRates varchar(1),
	release varchar(1),
	extend varchar(1),
	fleet varchar(1),
	kpi varchar(1)
) in herc_4k;


-- Members
-- username is 128 because it matches LDAP username, for which the irac standard is 128 chars.

create table Hercdb.Members (
	memberid bigint not null Constraint pk_Members primary key,
	username varchar(128) not null, 
	companyid bigint not null,
	defaultlocation varchar(50), 
	locked_by_memberid bigint,
	lock_expiry_time bigint,
	reports_accountid bigint
) in herc_4k;

create table Hercdb.Members_TEMP (
	memberid bigint not null Constraint pk_MemberT primary key,
	username varchar(128) not null, 
	companyid bigint not null,
	defaultlocation varchar(50), 
	locked_by_memberid bigint,
	lock_expiry_time bigint,
	reports_accountid bigint
) in herc_4k;


	
-- MemberAccounts

-- MemberAccounts.approved - 'A', 'P', 'D' Approved, Pending, Denied

create table hercdb.MemberAccounts (
	memberid bigint not null,
	accountid bigint not null,
	approved varchar(1) not null,
	approverid bigint not null,
	Constraint pk_MemberAccounts primary key (memberid, accountid)
) in herc_4k;

create table hercdb.MemberAccounts_TEMP (
	memberid bigint not null,
	accountid bigint not null,
	approved varchar(1) not null,
	approverid bigint not null,
	Constraint pk_MemberAccountT primary key (memberid, accountid)
) in herc_4k;



-- Member Jobsite Locations
-- countrystate, example USNJ

create table Hercdb.MemberLocations (
	memberid bigint not null,
	locationname varchar(50) not null,
	accountid bigint not null,
	branch varchar(50) not null,
	address1 varchar(255), 
	address2 varchar(50), 
	city varchar(50), 
	countrystate varchar(4), 
	zip varchar(50),
	Constraint pk_MemberLocations primary key (memberid, locationname)
) in herc_4k;

create table Hercdb.MemberLocations_TEMP (
	memberid bigint not null,
	locationname varchar(50) not null,
	accountid bigint not null,
	branch varchar(50) not null,
	address1 varchar(255), 
	address2 varchar(50), 
	city varchar(50), 
	countrystate varchar(4), 
	zip varchar(50),
	Constraint pk_MemberLocationT primary key (memberid, locationname)
) in herc_4k;


-- Member Preferences

create table hercdb.MemberPreferences (
	memberid bigint not null, 
	preference varchar(50) not null, 
	category int not null, 
	classification int not null,
	countryCode int not null,
	quantity int not null,
	Constraint pk_MemberPrefs primary key (memberid, preference, category, classification, countryCode)
) in herc_4k;

create table hercdb.MemberPreferences_TEMP (
	memberid bigint not null, 
	preference varchar(50) not null, 
	category int not null, 
	classification int not null,
	countryCode int not null,
	quantity int not null,
	Constraint pk_MemberPrefT primary key (memberid, preference, category, classification, countryCode)
) in herc_4k;



-- Member Requests

create table Hercdb.MemberRequests (
	requestid bigint not null Constraint pk_MemberRequests primary key,
	memberid bigint not null, 
	accountid bigint not null,
	idate date not null,
	jobsite varchar(50),
	local_first_name varchar(50),
	local_last_name varchar(50),
	local_phone varchar(50),
	status varchar(1) not null,
	approver_memberid bigint, 
	denial_reason varchar(250),
	approver_comments varchar(250),
	requestor_comments varchar(250),
	po_number varchar(50),
	work_order_number varchar(50),
	refr_number varchar(50),
	additional_information varchar(250)
) in herc_4k;

create table Hercdb.MemberRequests_TEMP (
	requestid bigint not null Constraint pk_MemberRequests primary key,
	memberid bigint not null, 
	accountid bigint not null,
	idate date not null,
	jobsite varchar(50),
	local_first_name varchar(50),
	local_last_name varchar(50),
	local_phone varchar(50),
	status varchar(1) not null,
	approver_memberid bigint, 
	denial_reason varchar(250),
	approver_comments varchar(250),
	requestor_comments varchar(250),
	po_number varchar(50),
	work_order_number varchar(50),
	refr_number varchar(50),
	additional_information varchar(250)
) in herc_4k;


create table Hercdb.MemberRequestsItems (
	requestid bigint not null,
	category int not null, 
	classification int not null,
	countryCode int not null,
	from_date date not null,
	to_date date not null,
	quantity int not null,
	rate varchar(50),
	Constraint pk_MemberReqItems primary key (requestid, category, classification, countryCode, from_date, to_date)
) in herc_4k;

create table Hercdb.MemberRequestsItems_TEMP (
	requestid bigint not null,
	category int not null, 
	classification int not null,
	countryCode int not null,
	from_date date not null,
	to_date date not null,
	quantity int not null,
	rate varchar(50),
	Constraint pk_MemberReqItemT primary key (requestid, category, classification, countryCode, from_date, to_date)
) in herc_4k;



-- MemberHistory

create table Hercdb.MemberHistory (
	memberid bigint not null,
	requestorid varchar(50) not null,
	requestorName varchar(50),
	requestorBusiness varchar(50),
	action varchar(50) not null,
	action_date timestamp not null,
	requestorPhone varchar(50),
	requestorEmail varchar(128),
	Constraint pk_MemberHistory primary key (memberid, requestorid, action, action_date)
) in herc_4k;

create table Hercdb.MemberHistory_TEMP (
	memberid bigint not null,
	requestorid varchar(50) not null,
	requestorName varchar(50),
	requestorBusiness varchar(50),
	action varchar(50) not null,
	action_date timestamp not null,
	requestorPhone varchar(50),
	requestorEmail varchar(128),
	Constraint pk_MemberHistoryT primary key (memberid, requestorid, action, action_date)
) in herc_4k;

commit;

create table hercdb.TaskProfile (
	taskid bigint not null, 
	memberid bigint not null,
	profile varchar(50) not null,
	countryCode int not null,
	frequency_type int not null,
	frequency_subtype1 int not null,
	frequency_subtype2 int not null,
	duration_type int not null,
	duration_subtype int not null,
	last_run_time bigint not null,
	next_run_time bigint not null,
	expiry_time bigint not null,
	status int not null,
	tries int not null, 
	report_type int not null,
	standard varchar(1),
	Constraint pk_TaskProfile primary key (taskid)
) in herc_4k;

create table hercdb.TaskProfileAccounts (
	taskid bigint not null,
	accountid bigint not null,
	Constraint pk_TaskProfileA primary key (taskid, accountid)
) in herc_4k;

create table hercdb.TaskProfileReports (
	taskid bigint not null,
	reportid int not null, 
	Constraint pk_TaskProfileR primary key (taskid, reportid)
) in herc_4k;

commit;
