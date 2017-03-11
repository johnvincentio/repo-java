
-- this script adds test data for the application


-- hercdb.LDAPMembers
-- NNNNNNNNNNNNNNNNNNNN
-- 01234567890123456789

-- YNNNNNNNNNNNNNNNNNNN admin
-- NYNNNNNNNNNNNNNNNNNN approver
-- NNYNNNNNNNNNNNNNNNNN member
-- NNNYNNNNNNNNNNNNNNNN requestor

-- position 0; Y/N Admin
-- position 1; Y/N Approver
-- position 2; Y/N Member
-- position 3; Y/N Requestor

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('aaaa', 'A', 'A', 'YNNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'aaaa99', 'personalquestion aaaa', 'personalanswer aaaa','',0,'enUS',
	'MR','Winston','E','Churchhill','SR','Prime Minister, Knight',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-02-01'),'');

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('bbbb', 'A', 'A', 'NNYNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'bbbb99', 'personalquestion bbbb', 'personalanswer bbbb','',0,'enUS',
	'MR','Benjamin','M','Disraeli','SR','First Minister, Earl',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('cccc', 'A', 'A', 'NNNYNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'cccc99', 'personalquestion cccc', 'personalanswer cccc','',0,'enUS',
	'MR','Sigmund','A','Freud','SR','Brain Doctor',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'N',date('2006-08-01'),'aaaa');

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('dddd', 'A', 'A', 'NYNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'dddd99', 'personalquestion dddd', 'personalanswer dddd','',0,'enUS',
	'MR','Rudyard','S','Kipling','SR','Author',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- test report flags, temporary user

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('eeee', 'A', 'A', 'NNNYNNNNNNNNNNNNNNNN', 'YNYNYNNYYNNYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'eeee99', 'personalquestion eeee', 'personalanswer eeee','Y',0,'enUS',
	'MR','John','L','Bunyan','SR','Author',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- test report flags; approve users, only allow access to this member's reports

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('aaa100', 'A', 'A', 'YNNNNNNNNNNNNNNNNNNN', 'YNYNYNNYYNNYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'aaa100', 'personalquestion aaa100', 'personalanswer aaa100','',0,'enUS',
	'MR','Charles','A','Dickens','SR','Author',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');


-- test login, no default job location

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('501000', 'A', 'A', 'NNNYNNNNNNNNNNNNNNNN', 'YNYNYNNYYNNYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'501000', 'personalquestion 501000', 'personalanswer 501000','',0,'enUS',
	'MR','Albert','C','Einstein','SR','Scientist',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- test logon, not active, should not logon

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('901000', 'D', 'A', 'NYNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'901000', 'personalquestion 901000', 'personalanswer 901000','',0,'enUS',
	'DR','Isaac','K','Newton','SR','Scientist, Knight',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- awaiting approval

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('902000', 'D', 'P', 'NNNYNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'902000', 'personalquestion 902000', 'personalanswer 902000','',0,'enUS',
	'MR','John','V','Harrison','SR','Inventor',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('903000', 'D', 'P', 'NNNYNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'903000', 'personalquestion 903000', 'personalanswer 903000','',0,'enUS',
	'MR','Charles','F','Darwin','SR','Scientist',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- account locked

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('904000', 'L', 'A', 'NNNYNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'904000', 'personalquestion 904000', 'personalanswer 904000','',0,'enUS',
	'MR','Isambard','K','Brunel','SR','Engineer',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- French

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('905000', 'A', 'A', 'NNNYNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'905000', 'personalquestion 905000', 'personalanswer 905000','',0,'frFR',
	'MR','Charles','E','de Gaulle','SR','General, President',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-08-01'),'aaaa');

-- Invited Member; Note that updateDate needs to be within two days of your run date,
--                    else the credentials check will fail.

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect,
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('InvitationKey101', 'D', 'I', 'NNNNNNNNNNNNNNNNNNNN', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'InvitationKey101', '', '','',0,'enUS',
	'MR','Leonardo','C','DiVinci','SR','Genius',
	'Pontvale Cleaners','','','','','',
	'WTemp1@hertz.com','','','','','','','','',
	'N',date('2007-11-28'),'aaaa');




-- Member for Company 100000000000002

-- Test Company has been created but not approved

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('fredsimple', 'A', 'P', 'YNNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'fred99', '', '','Y',0,'enUS',
	'DR','Edward','W','Teller','SR','Scientist',
	'Simple Manufacturing','120 Main Street','','Millville','USNJ','07624',
	'WTemp1@hertz.com','','','','','','','','',
	'Y',date('2006-02-01'),'');

-- focus group testing

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('fgt100', 'A', 'A', 'YNNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'fgt100', 'personalquestion', 'personalanswer','',0,'enUS',
	'MR','Michael','E','Moricco','SR',' ',
	'FGT Corporation','225 Brae Boulevard','','Park Ridge','USNJ','07656',
	'hercsales@hertz.com','201-307-5555','8667775555','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-02-01'),'');

-- demo100 account

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('demo100', 'A', 'A', 'YNNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'demo100', 'personalquestion', 'personalanswer','',0,'enUS',
	'MR','Michael','E','Moricco','SR',' ',
	'ABC Corporation','225 Brae Boulevard','','Park Ridge','USNJ','07656',
	'hercsales@hertz.com','201-307-5555','8667775555','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-02-01'),'');

-- test Member without an account

insert into hercdb.LDAPMembers (username, active, approval, role, reports,
	password, question, answer, temporarypassword, failurecounter, dialect, 
	prefix, fname, mname, lname, suffix, title,
	bname, address1, address2, city, country, zip, email, phone, fax,
	ponumber, pcardtype, pcardnumber, expmonth, expyear, seccode,
	releaseExtend, updateDate, approver)
values ('pppp', 'A', 'A', 'YNNNNNNNNNNNNNNNNNNN', 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY',
	'pppp99', 'personalquestion pppp', 'personalanswer pppp','',0,'enUS',
	'DR','Max','E','Planck','SR','Physicist',
	'Pontvale Cleaners','413 First Ave','','Pontvale','USNJ','03124',
	'WTemp1@hertz.com','912-344-5643','9435256545','PO3928','VSA','4012888888881881','12','09','5432',
	'Y',date('2006-02-01'),'');

commit;

-- hercdb.Companies
-- 1000000000
-- 100000000000000

insert into hercdb.Companies (companyid, companyname, active, type, annualVolume, ytdVolume, potentialVolume,
	comments, reason_denial, releaseExtend, releaseExtendEmail) 
values (1000000000000000001,'BE & K INC.','A',1,'12543','5478347','56457657','','','Y','jlvincent@hertz.com');

insert into hercdb.Companies (companyid, companyname, active, type, annualVolume, ytdVolume, potentialVolume,
	comments, reason_denial)
values (1000000000000000002,'Morrow - Meadows Corp.','A',2,'12554543','134244','32532543','','');

insert into hercdb.Companies (companyid, companyname, active, type, annualVolume, ytdVolume, potentialVolume,
	comments, reason_denial)
values (1000000000000000003,'Kellogg/Brown & Root','A',3,'3453412543','47657','478768768','','');

-- focus group testing

insert into hercdb.Companies (companyid, companyname, active, type, annualVolume, ytdVolume, potentialVolume,
	comments, reason_denial, releaseExtend, releaseExtendEmail) 
values (1000000000000000004,'FGT Corporation','A',1,'5476567','67567868','54786845','','','Y','hercsales@hertz.com');

-- demo100 account

insert into hercdb.Companies (companyid, companyname, active, type, annualVolume, ytdVolume, potentialVolume,
	comments, reason_denial, demo, releaseExtend, releaseExtendEmail) 
values (1000000000000000005,'ABC Corporation','A',1,'1254305','623144','326234870','','','Y','Y','hercsales@hertz.com');

commit;

-- hercdb.CompanyAccounts
-- 2000000000
-- 200000000000000
--
-- CA Narp# 2391505 and the accounts are as follows: 
-- 2389487   
-- 2389737   
-- 2392022 

-- US Narp# 3740	 and the accounts are as follows: 
-- 279355
-- 318824
-- 678436
-- 1858080
-- 2649522
-- 2779474
-- 2779517
-- 2779518
-- 2779519
-- 2779520
-- 2800195
-- 7484573

-- RM CA NON-NARP
-- 2367615; has Contract Rental Items
-- 2373521; has Contract Rental Items
-- 2373792; has Contract Rental Items

-- RM US NON-NARP 
-- 2791221; has Contract Rental Items, Contract Locations
-- 8513422; has Contract Rental Items, Contract Locations
-- 2801270; has Contract Rental Items, Contract Locations

-- Robert Iacobuc...	US accounts:
-- Robert Iacobuc...	2799460
--2799484
-- 2799571
-- 2799642
-- 2799682
-- 2799796
-- 2800287
-- 2800322
-- 2800513
-- 2800546
-- 2800634
-- 2800776
-- Robert Iacobuc...	Canada accounts...
-- Robert Iacobuc...	9181004
--9185704
--9189004
--9191004
--9192604
--9193104
--9202504
--9233204
--9660005
--9660031
--9660062

--
-- End of current
--

--
-- OLD
--
-- 517425, account, corporate, contract
-- 635596, account, corporate, non-contract
-- 5840, Narp, corporate, non-contract
-- has accounts 2825852, 2826655, 2827367, 2827368, 2827369, corporate, non-contract
-- 5247, Narp, corporate
-- has accounts 2658154 contract, 2794329 non-contract

-- 2787163, has contract rental items (used to).
-- 269415
-- 2801270, has contract locations and contract rental items

--
-- Prod accounts with no contract location or contract rental items
--
-- 9901006
-- 9902001
-- 9902023
-- 9902034
-- 9902045
-- 9902056
-- 9902060
-- 9999990

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000001,1000000000000000001,'A','A','2801270',1,1,0,'','Bill','Day',
		'Y','Y','WTemp1@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000005,1000000000000000001,'A','A','1110',2,1,0,'','Bill','Day',
		'Y','Y','WTemp1@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000002,1000000000000000001,'A','A','5247',1,2,0,'My Narp Manager','Bill','Day',
		'Y','Y','WTemp1@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000003,1000000000000000001,'A','A','2658154',1,3,2000000000000000002,'','Bill','Day',
		'Y','Y','WTemp1@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000004,1000000000000000001,'A','A','7010302',2,1,0,'','Bill','Day',
		'N','N','');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000010,1000000000000000002,'P','A','517425',1,1,0,'','Fred','Simple',
		'Y','Y','WTemp1@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000020,1000000000000000004,'A','A','1182020',1,1,0,'','Michael','Moricco',
		'Y','Y','hercsales@hertz.com');
-- 1182020
-- 2821736; not enough data on prod

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000021,1000000000000000004,'A','A','7030002',2,1,0,'','Michael','Moricco',
		'Y','Y','hercsales@hertz.com');

insert into hercdb.CompanyAccounts (accountid,companyid,approved,active,account,countryCode,accountType,
	narpAccountId,narpManager,creatorFirst,creatorLast,
	displayRates, releaseExtend, releaseExtendEmail)
values (2000000000000000030,1000000000000000005,'A','A','2821736',1,1,0,'','Michael','Moricco',
		'Y','Y','hercsales@hertz.com');

commit;

-- Members
-- 300000000000000

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000001,'aaaa',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000002,'bbbb',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000003,'cccc',1000000000000000001,'Sun Microsystems TX');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000004,'dddd',1000000000000000001,'Sun Microsystems TX');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000005,'eeee',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000006,'901000',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000007,'902000',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000008,'903000',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000011,'501000',1000000000000000001,null);

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000012,'aaa100',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000013,'904000',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000014,'905000',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000020,'fredsimple',1000000000000000002,null);

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000030,'fgt100',1000000000000000004,'New Jersey Central School District - Test Building');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000040,'demo100',1000000000000000005,'New Jersey Central School District - Test Building');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000015,'pppp',1000000000000000001,'Sun Microsystems NJ');

insert into hercdb.Members (memberid, username, companyid, defaultlocation)
values (3000000000000000016,'InvitationKey101',1000000000000000001,null);

commit;

-- Member Accounts

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000001,2000000000000000001,'A', 0);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000001,2000000000000000003,'A', 0);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000001,2000000000000000004,'A', 0);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000001,2000000000000000005,'A', 0);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000002,2000000000000000001,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000002,2000000000000000003,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000002,2000000000000000004,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000002,2000000000000000005,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000003,2000000000000000001,'A', 3000000000000000004);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000003,2000000000000000003,'A', 3000000000000000004);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000003,2000000000000000004,'A', 3000000000000000004);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000003,2000000000000000005,'A', 3000000000000000004);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000004,2000000000000000001,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000004,2000000000000000003,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000004,2000000000000000004,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000004,2000000000000000005,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000005,2000000000000000001,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000005,2000000000000000003,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000005,2000000000000000004,'A', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000005,2000000000000000005,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000006,2000000000000000001,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000007,2000000000000000001,'P', 3000000000000000001);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000007,2000000000000000003,'P', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000008,2000000000000000002,'P', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000011,2000000000000000001,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000012,2000000000000000001,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000014,2000000000000000001,'A', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000016,2000000000000000001,'P', 3000000000000000001);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000020,2000000000000000010,'P', 0);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000030,2000000000000000020,'A', 0);
insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000030,2000000000000000021,'A', 0);

insert into hercdb.MemberAccounts (memberid, accountid, approved, approverid) values (3000000000000000040,2000000000000000030,'A', 0);

commit;

-- Member Jobsite Locations

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000001,'Sun Microsystems NJ','9165','400 Atrium Dr','','somerset','USNJ','08873');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000001,'Sun Microsystems CA','9736','29 AVANZARE','','Irvine','USCA','92606');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000001,'Hertz NJ','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000003,'Hertz NJ 2','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000004,'Sun Microsystems ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000001,2000000000000000005,'Sun Microsystems 2 ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000002,2000000000000000001,'Sun Microsystems TX','9256','16000 DALLAS PKWY #700','','Dallas','USTX','75248');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000002,2000000000000000003,'Hertz NJ','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000002,2000000000000000004,'Sun Microsystems ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000002,2000000000000000005,'Sun Microsystems 2 ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000003,2000000000000000001,'Sun Microsystems TX','9256','16000 DALLAS PKWY #700','','Dallas','USTX','75248');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000003,2000000000000000003,'Hertz NJ','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000003,2000000000000000004,'Sun Microsystems ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000003,2000000000000000005,'Sun Microsystems 2 ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000004,2000000000000000001,'Sun Microsystems TX','9256','16000 DALLAS PKWY #700','','Dallas','USTX','75248');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000004,2000000000000000003,'Hertz NJ','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000004,2000000000000000004,'Sun Microsystems ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000004,2000000000000000005,'Sun Microsystems 2 ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000005,2000000000000000001,'Sun Microsystems TX','9256','16000 DALLAS PKWY #700','','Dallas','USTX','75248');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000005,2000000000000000003,'Hertz NJ','9165','225 Brae Boulevard','','Park Ridge','USNJ','07656');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000005,2000000000000000004,'Sun Microsystems ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000005,2000000000000000005,'Sun Microsystems 2 ONT','8202','27 Allstate Parkway','9th Floor','Markham','CAON','L3R 5L7');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000006,2000000000000000001,'Sun Microsystems TX','9256','16000 DALLAS PKWY #700','','Dallas','USTX','75248');


insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000012,2000000000000000001,'Sun Microsystems NJ','9165','400 Atrium Dr','','somerset','USNJ','08873');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000014,2000000000000000001,'Sun Microsystems NJ','9165','400 Atrium Dr','','somerset','USNJ','08873');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000030,2000000000000000020,'New Jersey Central School District - Test Building','9175','49 Wesley Street','','South Hackensack','USNJ','07606');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000030,2000000000000000021,'Canada - Test Building','8208','15 Terry Fox Drive','','Kingston','CAON','K7M 7K5');

insert into hercdb.MemberLocations (memberid, accountid, locationname, branch, address1, address2, city, countrystate, zip) 
values (3000000000000000040,2000000000000000030,'New Jersey Central School District - Test Building','9175','49 Wesley Street','','South Hackensack','USNJ','07606');

commit;

-- Member Preferences

insert into hercdb.MemberPreferences values (3000000000000000001, 'One Preference', 975, 6008, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000001, 'One Preference', 975, 9502, 1, 2);
insert into hercdb.MemberPreferences values (3000000000000000001, 'One Preference', 469, 0200, 1, 4);
insert into hercdb.MemberPreferences values (3000000000000000001, 'One Preference', 518, 0070, 1, 7);
insert into hercdb.MemberPreferences values (3000000000000000001, 'Two Preference', 465, 0410, 1, 2);
insert into hercdb.MemberPreferences values (3000000000000000001, 'Two Preference', 459, 0250, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000001, 'Three Preference', 451, 0300, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000001, 'CA Preference', 38,2500, 2, 1);
insert into hercdb.MemberPreferences values (3000000000000000001, 'CA Preference', 38,1700, 2, 1);
insert into hercdb.MemberPreferences values (3000000000000000001, 'CA Preference', 38,500, 2, 1);

insert into hercdb.MemberPreferences values (3000000000000000002, 'B Preference', 452, 0140, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000002, 'B Preference', 452, 0300, 1, 1);

insert into hercdb.MemberPreferences values (3000000000000000030, 'Spring Clean-up 2007 - Earthmoving', 200, 70, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000030, 'Spring Clean-up 2007 - Earthmoving', 222, 50, 1, 1);
insert into hercdb.MemberPreferences values (3000000000000000030, 'Snow Removal 2007 - Earthmoving', 259, 30, 1, 1);

commit;

-- Member Requests
-- 400000000000000


-- 400000000000002
-- 4000000000000000002
-- 1234567890123456789
-- 4000000000000000002
-- 30000000000000000001
-- 20000000000000000001
-- 30000000000000000001
-- 10000000000000000001

-- aaaa

insert into hercdb.MemberRequests values (4000000000000000002, 3000000000000000001, 2000000000000000001, date('2005-06-02'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000002, 775, 0960, 1, date('2005-02-02'), date('2005-04-24'), 3, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000002, 716, 0150, 1, date('2005-03-03'), date('2005-08-28'), 1, '25');

insert into hercdb.MemberRequests values (4000000000000000006, 3000000000000000001, 2000000000000000001, date('2005-09-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000006, 465, 0410, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000010, 3000000000000000001, 2000000000000000001, date('2005-03-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000010, 003, 6846, 1, date('2005-04-02'), date('2005-06-22'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000013, 3000000000000000001, 2000000000000000004, date('2005-04-01'), 'Sun Microsystems ONT', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000013, 38, 2500, 2, date('2005-04-04'), date('2005-06-27'), 2, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000013, 38, 1700, 2, date('2005-05-05'), date('2005-07-27'), 1, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000013, 38, 0500, 2, date('2005-06-06'), date('2005-08-28'), 1, '25');

insert into hercdb.MemberRequests values (4000000000000000014, 3000000000000000001, 2000000000000000001, date('2005-03-08'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000014, 999, 9999, 1, date('2005-10-02'), date('2005-11-22'), 1, '252');

insert into hercdb.MemberRequests values (4000000000000000015, 3000000000000000001, 2000000000000000001, date('2005-10-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000015, 999, 9999, 1, date('2005-10-06'), date('2005-11-26'), 1, '251');
insert into hercdb.MemberRequestsItems values (4000000000000000015, 003, 6846, 1, date('2005-10-07'), date('2005-11-27'), 2, '250');

insert into hercdb.MemberRequests values (4000000000000000016, 3000000000000000001, 2000000000000000001, date('2005-11-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000016, 465, 0410, 1, date('2005-12-01'), date('2005-12-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000017, 3000000000000000001, 2000000000000000001, date('2005-11-21'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000017, 999, 9999, 1, date('2005-12-02'), date('2005-12-22'), 2, '25');

-- test with bbb, account user role, can approve own rental requests

insert into hercdb.MemberRequests values (4000000000000000032, 3000000000000000002, 2000000000000000001, date('2005-06-02'), 'Sun Microsystems TX', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000032, 775, 0960, 1, date('2005-04-06'), date('2005-06-21'), 3, '66');
insert into hercdb.MemberRequestsItems values (4000000000000000032, 716, 0150, 1, date('2005-03-03'), date('2005-08-21'), 1, '77');

insert into hercdb.MemberRequests values (4000000000000000033, 3000000000000000002, 2000000000000000001, date('2005-09-01'), 'Sun Microsystems TX', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000033, 465, 0410, 1, date('2005-04-01'), date('2005-06-20'), 2, '88');

insert into hercdb.MemberRequests values (4000000000000000034, 3000000000000000002, 2000000000000000001, date('2005-03-01'), 'Sun Microsystems TX', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000034, 003, 6846, 1, date('2005-04-04'), date('2005-06-26'), 2, '99');

insert into hercdb.MemberRequests values (4000000000000000035, 3000000000000000002, 2000000000000000001, date('2005-10-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000035, 999, 9999, 1, date('2005-10-06'), date('2005-11-26'), 1, '251');
insert into hercdb.MemberRequestsItems values (4000000000000000035, 003, 6846, 1, date('2005-10-07'), date('2005-11-27'), 2, '250');

insert into hercdb.MemberRequests values (4000000000000000036, 3000000000000000002, 2000000000000000001, date('2005-11-01'), 'Hertz NJ', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000036, 9999, 9999, 1, date('2005-12-01'), date('2005-12-20'), 2, '159');

-- cccc, account requestor, cannot approve own requests

-- 1234567890123456789
-- 4000000000000000002
-- 4000000000000000041
-- 4000000000000000041
-- 3000000000000000003
-- 2000000000000000001
-- 30000000000000000004

insert into hercdb.MemberRequests values (4000000000000000041, 3000000000000000003, 2000000000000000001, date('2005-04-01'), 'Sun Microsystems TX', 'C', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000041, 851, 0010, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000041, 409, 0080, 1, date('2005-05-01'), date('2005-07-20'), 1, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000041, 466, 0450, 1, date('2005-06-01'), date('2005-08-20'), 1, '25');

insert into hercdb.MemberRequests values (4000000000000000042, 3000000000000000003, 2000000000000000001, date('2005-06-02'), 'Sun Microsystems TX', 'A', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000042, 775, 0960, 1, date('2005-04-01'), date('2005-06-20'), 3, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000042, 716, 0150, 1, date('2005-03-01'), date('2005-08-20'), 1, '25');

insert into hercdb.MemberRequests values (4000000000000000043, 3000000000000000003, 2000000000000000001, date('2005-05-04'), 'Sun Microsystems TX', 'P', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000043, 003, 5635, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000044, 3000000000000000003, 2000000000000000001, date('2005-05-03'), 'Sun Microsystems TX', 'D', 3000000000000000001,'too just so, like','no excuse');
insert into hercdb.MemberRequestsItems values (4000000000000000044, 412, 0050, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');
insert into hercdb.MemberRequestsItems values (4000000000000000044, 025, 0150, 1, date('2005-02-01'), date('2005-06-20'), 1, '25');

insert into hercdb.MemberRequests values (4000000000000000045, 3000000000000000003, 2000000000000000001, date('2005-08-01'), 'Sun Microsystems TX', 'C', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000045, 658, 0140, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000046, 3000000000000000003, 2000000000000000001, date('2005-09-01'), 'Sun Microsystems TX', 'A', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000046, 465, 0410, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000047, 3000000000000000003, 2000000000000000001, date('2005-10-01'), 'Sun Microsystems TX', 'P', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000047, 487, 0480, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000048, 3000000000000000003, 2000000000000000001, date('2005-01-01'), 'Sun Microsystems TX', 'D', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000048, 468, 0400, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000049, 3000000000000000003, 2000000000000000001, date('2005-02-01'), 'Sun Microsystems TX', 'C', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000049, 003, 2090, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000050, 3000000000000000003, 2000000000000000001, date('2005-03-01'), 'Sun Microsystems TX', 'A', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000050, 003, 6846, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000051, 3000000000000000003, 2000000000000000001, date('2005-04-01'), 'Sun Microsystems TX', 'P', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000051, 409, 0060, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');

insert into hercdb.MemberRequests values (4000000000000000052, 3000000000000000003, 2000000000000000001, date('2005-05-01'), 'Sun Microsystems TX', 'D', 3000000000000000004,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000052, 452, 0330, 1, date('2005-04-01'), date('2005-06-20'), 2, '25');





insert into hercdb.MemberRequests values (4000000000000000083, 3000000000000000030, 2000000000000000020, date('2006-11-15'), 'New Jersey Central School District - Test Building', 'A', null,'','');
insert into hercdb.MemberRequestsItems values (4000000000000000083, 217, 90, 1, date('2006-11-15'), date('2007-03-15'), 2, 'xxx');
insert into hercdb.MemberRequestsItems values (4000000000000000083, 238, 120, 1, date('2006-11-18'), date('2007-03-18'), 1, 'xxx');
insert into hercdb.MemberRequestsItems values (4000000000000000083, 220, 70, 1, date('2006-11-19'), date('2007-03-19'), 1, 'xxx');

commit;

-- MemberHistory

insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000001,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000001,'aaaa','Bill Smith','Hertz','approved','2006-09-28 18:21:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000002,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000003,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000004,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000005,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000006,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000007,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000008,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000011,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000012,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000013,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');
insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000014,'aaaa','Bill Smith','Hertz','created','2006-09-25 14:45:00');

insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000020,'hertz95012','Linda Smith','Hertz','created','2007-01-25 16:05:00');

insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000030,'fgt100','Michael Moricco','Hertz','created','2007-03-15 16:05:00');

insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000040,'demo100','Michael Moricco','Hertz','created','2007-05-15 12:15:00');

insert into Hercdb.MemberHistory (memberid,requestorid,requestorName,requestorBusiness,action,action_date)
values (3000000000000000015,'pppp','Bill Smith','Hertz','created','2006-09-25 14:45:00');

commit;
