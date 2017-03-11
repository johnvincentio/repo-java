
-- comment

drop table hercdb.RMContractLocationsCA;
create table hercdb.RMContractLocationsCA (
	account varchar(20) not null,
	branch varchar(50) not null,
	Constraint pk_RMLocatioCA primary key (account, branch)
) in herc_4k;

drop table hercdb.RMContractLocationsCA_TEMP;
create table hercdb.RMContractLocationsCA_TEMP (
	account varchar(20) not null,
	branch varchar(50) not null,
	Constraint pk_tRMLocatioCA primary key (account, branch)
) in herc_4k;

-- comment

drop table hercdb.RMContractRentalEquipmentUS;
create table hercdb.RMContractRentalEquipmentUS (
	account varchar(20) not null,
	category int not null, 
	classification int not null,
	Constraint pk_RMRentalEquUS primary key (account, category, classification)
) in herc_4k;

drop table hercdb.RMContractRentalEquipmentUS_TEMP;
create table hercdb.RMContractRentalEquipmentUS_TEMP (
	account varchar(20) not null,
	category int not null, 
	classification int not null,
	Constraint pk_tRMRentalEquUS primary key (account, category, classification)
) in herc_4k;
