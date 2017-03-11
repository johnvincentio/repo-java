
-- generated SQL file


create index hercdb.ARIHD2FL on hercdb.ARIHD2FL (AHCMP ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHD2ET1 on hercdb.ARIHD2FL (AHCUS# ASC, AHCMP ASC, AHCOLS ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;

create index hercdb.ARIHDRFL on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF0 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHJOB# ASC, AHITYP ASC, AHSYSD ASC, AHSYST ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRN2 on hercdb.ARIHDRFL (AHCMP ASC, AHCRP# ASC, AHDUED ASC, AHSTTS DESC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRP2 on hercdb.ARIHDRFL (AHCMP ASC, AHCRP# ASC, AHDUED ASC, AHSTTS DESC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRFA on hercdb.ARIHDRFL (AHCMP ASC, AHCRP# ASC, AHINVD ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRH3 on hercdb.ARIHDRFL (AHCMP ASC, AHCRP# ASC, AHCUS# ASC, AHLPDT ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRN3 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHDUED ASC, AHSTTS DESC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRP3 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHDUED ASC, AHSTTS DESC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRFC on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINVD DESC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF2 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINVD ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF5 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINVD ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF7 on hercdb.ARIHDRFL (AHCMP ASC, AHINVD ASC, AHINV# ASC, AHISEQ ASC, AHCUS# ASC) in herc_4k;
create unique index hercdb.ARIHDRF8 on hercdb.ARIHDRFL (AHCMP ASC, AHINVD ASC, AHINV# ASC, AHISEQ ASC, AHCUS# ASC) in herc_4k;
create unique index hercdb.ARIHDRF9 on hercdb.ARIHDRFL (AHCMP ASC, AHSEQ# ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRH1 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHSTTS ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRH2 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHLPDT ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRFB on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF3 on hercdb.ARIHDRFL (AHCMP ASC, AHINV# ASC, AHISEQ ASC, AHCUS# ASC) in herc_4k;
create unique index hercdb.ARIHDRF4 on hercdb.ARIHDRFL (AHCMP ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF6 on hercdb.ARIHDRFL (AHCMP ASC, AHINV# ASC, AHISEQ ASC, AHCUS# ASC) in herc_4k;
create unique index hercdb.ARIHDRF10 on hercdb.ARIHDRFL (AHCMP ASC, AHLOC ASC, AHCUS# ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;
create unique index hercdb.ARIHDRF11 on hercdb.ARIHDRFL (AHCMP ASC, AHCBAL ASC) in herc_4k;
create unique index hercdb.ARIHDRET3 on hercdb.ARIHDRFL (AHCUS# ASC, AHSTTS ASC, AHCMP ASC, AHINVD ASC, AHCBAL ASC, AHTPAY ASC, AHINV# ASC, AHISEQ ASC) in herc_4k;

create index hercdb.CRAHDRFL on hercdb.CRAHDRFL (HCCMP ASC, HCINV# ASC, HCOISQ ASC, HCCASQ ASC) in herc_4k;
create unique index hercdb.CRAHDRH2 on hercdb.CRAHDRFL (HCCMP ASC, HCCUS# ASC, HCSYSD ASC, HCINV# ASC, HCOISQ ASC, HCCASQ ASC) in herc_4k;
create unique index hercdb.CRAHDRH1 on hercdb.CRAHDRFL (HCCMP ASC, HCCUS# ASC, HCINV# ASC, HCOISQ ASC, HCCASQ ASC) in herc_4k;
create unique index hercdb.CRAHDRH3 on hercdb.CRAHDRFL (HCCMP ASC, HCCRSN ASC, HCLOC ASC, HCINV# ASC, HCOISQ ASC) in herc_4k;
create unique index hercdb.CRAHDRL1 on hercdb.CRAHDRFL (HCCMP ASC, HCLOC ASC, HCCRSN ASC) in herc_4k;
create unique index hercdb.CRAHDRL2 on hercdb.CRAHDRFL (HCCMP ASC, HCCRSN ASC, HCLOC ASC) in herc_4k;
create unique index hercdb.CRAHDRL4 on hercdb.CRAHDRFL (HCCMP ASC, HCINV# ASC, HCNISQ ASC) in herc_4k;
create unique index hercdb.CRAHDRL5 on hercdb.CRAHDRFL (HCAORD ASC, HCSYSD ASC, HCCUS# ASC) in herc_4k;
create unique index hercdb.CRAHDRL8 on hercdb.CRAHDRFL (HCCMP ASC, HCINV# ASC, HCCASQ ASC) in herc_4k;
create unique index hercdb.CRAHDRL3 on hercdb.CRAHDRFL (HCCMP ASC, HCINV# ASC, HCOISQ ASC) in herc_4k;
create unique index hercdb.CRAHDRL6 on hercdb.CRAHDRFL (HCINV# ASC, HCOISQ ASC) in herc_4k;
create unique index hercdb.CRAHDRL7 on hercdb.CRAHDRFL (HCMODDT DESC) in herc_4k;

create unique index hercdb.CUSJBLFL on hercdb.CUSJBLFL (CICMP ASC, CICUS# ASC, CIJOB# ASC) in herc_4k;

create index hercdb.CUSJOBFL on hercdb.CUSJOBFL (CJCMP ASC, CJCUS# ASC, CJJOB# ASC) in herc_4k;
create unique index hercdb.CUSJOBF3 on hercdb.CUSJOBFL (CJCMP ASC, CJCUS# ASC, CJJOB# ASC) in herc_4k;
create unique index hercdb.CUSJOBF4 on hercdb.CUSJOBFL (CJCMP ASC, CJCUS# ASC, CJLOC ASC) in herc_4k;
create unique index hercdb.CUSJOBF2 on hercdb.CUSJOBFL (CJCMP ASC, CJCUS# ASC, CJJOB# ASC) in herc_4k;

create index hercdb.CUSMASFL on hercdb.CUSMASFL (CMCMP ASC, CMCUS# ASC) in herc_4k;
create unique index hercdb.CUSMASF4 on hercdb.CUSMASFL (CMCMP ASC, CMCRP# ASC, CMCUS# ASC) in herc_4k;
create unique index hercdb.CUSMASF5 on hercdb.CUSMASFL (CMCMP ASC, CMAREA ASC, CMPHON ASC) in herc_4k;
create unique index hercdb.CUSMASF6 on hercdb.CUSMASFL (CMCMP ASC, CMLOC ASC, CMDWDT ASC) in herc_4k;
create unique index hercdb.CUSMASF7 on hercdb.CUSMASFL (CMCMP ASC, CMGREG ASC, CMDWDT ASC) in herc_4k;
create unique index hercdb.CUSMASF10 on hercdb.CUSMASFL (CMCMP ASC, CMRCDT ASC) in herc_4k;
create unique index hercdb.CUSMASF3 on hercdb.CUSMASFL (CMCMP ASC, CMSRWD ASC) in herc_4k;
create unique index hercdb.CUSMASH1 on hercdb.CUSMASFL (CMCMP ASC, CMNAME ASC) in herc_4k;
create unique index hercdb.CUSMASH2 on hercdb.CUSMASFL (CMCMP ASC, CMPROR ASC) in herc_4k;
create unique index hercdb.CUSMASF2 on hercdb.CUSMASFL (CMCMP ASC, CMCUS# ASC) in herc_4k;
create unique index hercdb.CUSMASF9 on hercdb.CUSMASFL (CMCRDM ASC) in herc_4k;
create unique index hercdb.CUSMASF8 on hercdb.CUSMASFL (CMCMP ASC, CMCUS# ASC) in herc_4k;

create index hercdb.CUSMS2FL on hercdb.CUSMS2FL (CMCMP2 ASC, CMCUS#2 ASC) in herc_4k;
create unique index hercdb.CUSMS2F2 on hercdb.CUSMS2FL (CMCMP2 ASC, CMNATLCON# ASC, CMCUS#2 ASC) in herc_4k;
create unique index hercdb.CUSMS2F3 on hercdb.CUSMS2FL (CMCMP2 ASC, CMDLST ASC, CMDL# ASC) in herc_4k;
create unique index hercdb.CUSMS2F4 on hercdb.CUSMS2FL (CMDB#ID ASC) in herc_4k;

create index hercdb.EQPCCMFL on hercdb.EQPCCMFL (ECCMP ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF8 on hercdb.EQPCCMFL (ECCMP ASC, ECPG# ASC, ECSEQ# ASC, ECSRWD ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF5 on hercdb.EQPCCMFL (ECCMP ASC, ECITEM ASC, ECSTKC ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF7 on hercdb.EQPCCMFL (ECCMP ASC, ECWRKS ASC, ECSRWD ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF2 on hercdb.EQPCCMFL (ECCMP ASC, ECSRWD ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF3 on hercdb.EQPCCMFL (ECCMP ASC, ECWRKS ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF6 on hercdb.EQPCCMFL (ECCMP ASC, ECAGRP ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF1 on hercdb.EQPCCMFL (ECCMP ASC, ECCATG ASC, ECCLAS ASC) in herc_4k;
create unique index hercdb.EQPCCMF4 on hercdb.EQPCCMFL (ECCMP ASC, ECEQP# ASC) in herc_4k;
create unique index hercdb.EQPCCMF9 on hercdb.EQPCCMFL (ECCMP ASC, ECCCR# ASC) in herc_4k;

create index hercdb.EQPCPHFL on hercdb.EQPCPHFL (EYCMP ASC, EYCNT# ASC) in herc_4k;
create unique index hercdb.EQPCPHET2 on hercdb.EQPCPHFL (EYCNT# ASC, EYSTDT ASC, EYENDT ASC, EYCMP ASC) in herc_4k;

create index hercdb.EQPCPRFL on hercdb.EQPCPRFL (EQCMP ASC, EQCNT# ASC, EQCUS# ASC) in herc_4k;
create unique index hercdb.EQPCPRF2 on hercdb.EQPCPRFL (EQCMP ASC, EQCUS# ASC, EQCNT# ASC) in herc_4k;

create index hercdb.EQPMASFL on hercdb.EQPMASFL (EMCMP ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF0 on hercdb.EQPMASFL (EMCMP ASC, EMCLOC ASC, EMCATG ASC, EMCLAS ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF10 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMCLOC ASC) in herc_4k;
create unique index hercdb.EQPMASFB on hercdb.EQPMASFL (EMCMP ASC, EMAEQP ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF1 on hercdb.EQPMASFL (EMCMP ASC, EMCUS# ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF5 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMTYPE DESC, EMMAKE ASC, EMMODL ASC, EMSER# ASC) in herc_4k;
create unique index hercdb.EQPMASF2 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMTYPE DESC, EMTRDT ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF6 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMTYPE DESC, EMAQDT ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF9 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMSUBC ASC, EMCFG# ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF7 on hercdb.EQPMASFL (EMCMP ASC, EMSRCH ASC, EMCATG ASC, EMCLAS ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF8 on hercdb.EQPMASFL (EMCMP ASC, EMMISC ASC, EMCATG ASC, EMCLAS ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASL02 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMCLOC ASC) in herc_4k;
create unique index hercdb.EQPMASF4 on hercdb.EQPMASFL (EMCMP ASC, EMSER# ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF3 on hercdb.EQPMASFL (EMCMP ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASL03 on hercdb.EQPMASFL (EMCLOC ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASL01 on hercdb.EQPMASFL (EMCLOC ASC) in herc_4k;
create unique index hercdb.EQPMASL04 on hercdb.EQPMASFL (EMCMP ASC, EMCATG ASC, EMCLAS ASC, EMCLOC ASC) in herc_4k;
create unique index hercdb.EQPMASF12 on hercdb.EQPMASFL (EMCMP ASC, EMCLOC ASC, EMCATG ASC, EMCLAS ASC, EMTYPE DESC, EMMAKE ASC, EMMODL ASC, EMSER# ASC) in herc_4k;
create unique index hercdb.EQPMASF13 on hercdb.EQPMASFL ( ASC) in herc_4k;
create unique index hercdb.EQPMASF14 on hercdb.EQPMASFL (EMCMP ASC, EMEQP# ASC) in herc_4k;
create unique index hercdb.EQPMASF15 on hercdb.EQPMASFL (EMCMP ASC, EMSER# ASC, EMEQP# ASC) in herc_4k;

create index hercdb.ITMMASFL on hercdb.ITMMASFL (IMCMP ASC, IMITEM ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF7 on hercdb.ITMMASFL (IMCMP ASC, IMRCAT ASC, IMRCLS ASC, IMRSCL ASC, IMITEM ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF5 on hercdb.ITMMASFL (IMCMP ASC, IMSRWD ASC, IMDESC ASC, IMITEM ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF6 on hercdb.ITMMASFL (IMCMP ASC, IMDESC ASC, IMITEM ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF3 on hercdb.ITMMASFL (IMCMP ASC, IMMFI# ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF4 on hercdb.ITMMASFL (IMCMP ASC, IMSTKC ASC, IMITEM ASC) in herc_4k;
create unique index hercdb.ITMMASF8 on hercdb.ITMMASFL (IMCMP ASC, IMSUP# ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASH1 on hercdb.ITMMASFL (IMCMP ASC, IMTPR# ASC, IMITEM ASC) in herc_4k;
create unique index hercdb.ITMMASF2 on hercdb.ITMMASFL (IMCMP ASC, IMITEM ASC, IMSTKC ASC) in herc_4k;
create unique index hercdb.ITMMASF9 on hercdb.ITMMASFL (IMCMP ASC, IMUPC# ASC) in herc_4k;

create index hercdb.MARACDFL on hercdb.MARACDFL (MDCMP ASC, MDLOC ASC, MDCODE ASC) in herc_4k;

create index hercdb.RACDETFL on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDISEQ ASC, RDTYPE ASC, RDSEQ# ASC, RDRSEQ ASC) in herc_4k;
create unique index hercdb.RACDETF11 on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSEQ# ASC, RDISEQ ASC) in herc_4k;
create unique index hercdb.RACDETF10 on hercdb.RACDETFL (RDITEM ASC, RDDATI DESC, RDCON# DESC, RDSEQ# DESC) in herc_4k;
create unique index hercdb.RACDETF13 on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDRTND ASC, RDRTNT ASC) in herc_4k;
create unique index hercdb.RACDETF12 on hercdb.RACDETFL (RDCMP ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RACDETH3 on hercdb.RACDETFL (RDCMP ASC, RDTYPE ASC, RDCUS# ASC, RDCATG ASC, RDCLAS ASC, RDSYSD ASC, RDSYST ASC, RDCON# ASC, RDISEQ ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RACDETF9 on hercdb.RACDETFL (RDCMP ASC, RDCUS# ASC, RDDLST ASC, RDDL# ASC, RDCATG ASC, RDCLAS ASC, RDITEM ASC, RDCON# ASC, RDISEQ ASC) in herc_4k;
create unique index hercdb.RACDETF3 on hercdb.RACDETFL (RDCMP ASC, RDCATG ASC, RDCLAS ASC, RDSYSD ASC, RDSYST ASC, RDCON# ASC, RDISEQ ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RACDETF4 on hercdb.RACDETFL (RDCMP ASC, RDSTTS ASC, RDCATG ASC, RDCLAS ASC, RDSYSD ASC, RDCON# ASC, RDISEQ ASC) in herc_4k;
create unique index hercdb.RACDETF7 on hercdb.RACDETFL (RDCMP ASC, RDCUS# ASC, RDDLST ASC, RDDL# ASC, RDSYSD DESC, RDCATG ASC, RDCLAS ASC) in herc_4k;
create unique index hercdb.RACDETF15 on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSTKC ASC, RDITEM ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RACDETF2 on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDSEQ# ASC, RDISEQ ASC, RDRSEQ ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RACDETF8 on hercdb.RACDETFL (RDCMP ASC, RDPONO ASC, RDCON# ASC, RDISEQ ASC) in herc_4k;
create unique index hercdb.RACDETF17 on hercdb.RACDETFL (RDCMP ASC, RDLOC ASC, RDSYSD ASC) in herc_4k;
create unique index hercdb.RACDETF6 on hercdb.RACDETFL (RDCMP ASC, RDITEM ASC, RDSYSD ASC) in herc_4k;
create unique index hercdb.RACDETF16 on hercdb.RACDETFL (RDCMP ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RACDETF18 on hercdb.RACDETFL (RDCMP ASC, RDLOC ASC, RDITEM ASC, RDSTKC ASC, RDTYPE ASC, RDSTTS ASC) in herc_4k;
create unique index hercdb.RACDETF1 on hercdb.RACDETFL (RDCMP ASC, RDLOC ASC, RDCATG ASC, RDCLAS ASC, RDDATI ASC, RDDATO ASC) in herc_4k;
create unique index hercdb.RACDETET1 on hercdb.RACDETFL (RDCUS# ASC, RDTYPE ASC, RDCMP ASC, RDSYSD ASC, RDCON# ASC, RDISEQ ASC) in herc_4k;
create unique index hercdb.RACDETET4 on hercdb.RACDETFL (RDCMP ASC, RDCON# ASC, RDISEQ ASC, RDTYPE ASC, RDSTTS ASC) in herc_4k;
create unique index hercdb.RACDETET5 on hercdb.RACDETFL (RDCMP ASC, RDCUS# ASC, RDTYPE ASC, RDSYSD ASC) in herc_4k;

create index hercdb.RACHDRFL on hercdb.RACHDRFL (RHCMP ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF4 on hercdb.RACHDRFL (RHCMP ASC, RHCUS# ASC, RHDLST ASC, RHDL# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF6 on hercdb.RACHDRFL (RHCMP ASC, RHPKUB ASC, RHSYSD ASC, RHSYST ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF7 on hercdb.RACHDRFL (RHCMP ASC, RHPKUB ASC, RHDATO ASC, RHSYST ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRH4 on hercdb.RACHDRFL (RHCMP ASC, RHCUS# ASC, RHSYSD ASC, RHCON# ASC, RHISEQ ASC, RHLSEQ ASC) in herc_4k;
create unique index hercdb.RACHDRH9 on hercdb.RACHDRFL (RHCMP ASC, RHCUS# ASC, RHLOC ASC, RHSYSD ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF10 on hercdb.RACHDRFL (RHCMP ASC, RHTYPE ASC, RHISEQ ASC, RHLSEQ ASC, RHSYSD DESC) in herc_4k;
create unique index hercdb.RACHDRF2 on hercdb.RACHDRFL (RHCMP ASC, RHSYSD ASC, RHSYST ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF3 on hercdb.RACHDRFL (RHCMP ASC, RHTYPE ASC, RHLBDT DESC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF5 on hercdb.RACHDRFL (RHCMP ASC, RHCRM# ASC, RHCMS# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF8 on hercdb.RACHDRFL (RHCMP ASC, RHSYSD ASC, RHCUS# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRH1 on hercdb.RACHDRFL (RHCMP ASC, RHJOB# ASC, RHCUS# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRH3 on hercdb.RACHDRFL (RHCMP ASC, RHDL# ASC, RHLOC ASC, RHSYSD ASC, RHAMT$ ASC) in herc_4k;
create unique index hercdb.RACHDRH5 on hercdb.RACHDRFL (RHCMP DESC, RHCUS# ASC, RHJOB# ASC, RHSYSD ASC) in herc_4k;
create unique index hercdb.RACHDRH2 on hercdb.RACHDRFL (RHCMP DESC, RHSYSD ASC, RHCUS# ASC) in herc_4k;
create unique index hercdb.RACHDRF1 on hercdb.RACHDRFL (RHCMP ASC, RHLOC ASC) in herc_4k;
create unique index hercdb.RACHDRF11 on hercdb.RACHDRFL (RHCMP ASC, RHLOC ASC, RHSREP ASC, RHCUS# ASC) in herc_4k;
create unique index hercdb.RACHDRF12 on hercdb.RACHDRFL (RHCMP ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRF13 on hercdb.RACHDRFL (RHCMP ASC, RHISEQ ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RACHDRH6 on hercdb.RACHDRFL (RHCMP ASC, RHMN2$ ASC, RHCUS# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRET1 on hercdb.RACHDRFL (RHCUS# ASC, RHTYPE ASC, RHCMP ASC, RHJOB# ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRET4 on hercdb.RACHDRFL (RHCUS# ASC, RHTYPE ASC, RHCMP ASC, RHSTTS ASC, RHJOB# ASC) in herc_4k;
create unique index hercdb.RACHDRET8 on hercdb.RACHDRFL (RHCUS# ASC, RHTYPE ASC, RHCMP ASC, RHOTYP ASC, RHSTTS ASC, RHCON# ASC, RHISEQ ASC) in herc_4k;
create unique index hercdb.RACHDRET9 on hercdb.RACHDRFL (RHCMP ASC, RHCUS# ASC, RHTYPE ASC, RHOTYP ASC, RHLRDT ASC) in herc_4k;
create unique index hercdb.RACHDRF14 on hercdb.RACHDRFL (RHCMP ASC, RHCON# ASC) in herc_4k;

create index hercdb.RAOADTFL on hercdb.RAOADTFL (ADCMP ASC, ADCON# ASC, ADTYPE ASC, ADSEQ# ASC) in herc_4k;
create unique index hercdb.RAOADTF1 on hercdb.RAOADTFL (ADCMP ASC, ADCON# ASC, ADTYPE ASC) in herc_4k;
create unique index hercdb.RAOADTF2 on hercdb.RAOADTFL (ADLMDDT DESC, ADLMDTM DESC) in herc_4k;
create unique index hercdb.RAOADTF3 on hercdb.RAOADTFL (ADCMP ASC, ADCON# ASC, ADTYPE ASC) in herc_4k;
create unique index hercdb.RAOADTF4 on hercdb.RAOADTFL (ADCMP ASC, ADLVAP ASC, ADCON# ASC, ADTYPE ASC, ADSEQ# ASC) in herc_4k;
create unique index hercdb.RAOADTF5 on hercdb.RAOADTFL (ADCMP ASC, ADCOUPON ASC, ADCPNCAT ASC, ADCPNCLS ASC) in herc_4k;
create unique index hercdb.RAOADTF6 on hercdb.RAOADTFL (ADCMP ASC, ADLVAP ASC, ADCON# ASC, ADTYPE ASC, ADSEQ# ASC) in herc_4k;
create unique index hercdb.RAOADTF7 on hercdb.RAOADTFL (ADCMP ASC, ADLVAP ASC, ADCON# ASC, ADTYPE ASC, ADSEQ# ASC) in herc_4k;

create index hercdb.RAOAMTFL on hercdb.RAOAMTFL (RNCMP ASC, RNCON# ASC, RNSEQ# ASC) in herc_4k;

create index hercdb.RAODETFL on hercdb.RAODETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RAODETF2 on hercdb.RAODETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDCATG ASC, RDCLAS ASC, RDITEM ASC, RDSTKC ASC, RDDATO ASC, RDTIMO ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RAODETF5 on hercdb.RAODETFL (RDCMP ASC, RDCUS# ASC, RDDLST ASC, RDDL# ASC, RDCATG ASC, RDCLAS ASC, RDITEM ASC, RDSTKC ASC, RDCON# ASC) in herc_4k;
create unique index hercdb.RAODETF6 on hercdb.RAODETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSEQ# ASC, RDCATG ASC, RDCLAS ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RAODETFL11 on hercdb.RAODETFL (RDSYSD DESC, RDSYST DESC, RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RAODETF3 on hercdb.RAODETFL (RDCMP ASC, RDLOC ASC, RDCATG ASC, RDCLAS ASC, RDDATI ASC, RDDATO ASC) in herc_4k;
create unique index hercdb.RAODETF4 on hercdb.RAODETFL (RDCMP ASC, RDCATG ASC, RDCLAS ASC, RDDATI ASC, RDDATO ASC) in herc_4k;
create unique index hercdb.RAODETF11 on hercdb.RAODETFL (RDCMP ASC, RDTYPE ASC, RDITEM ASC, RDDATO ASC) in herc_4k;
create unique index hercdb.RAODETF9 on hercdb.RAODETFL (RDCMP ASC, RDCON# ASC, RDTYPE ASC, RDSEQ# ASC) in herc_4k;
create unique index hercdb.RAODETF10 on hercdb.RAODETFL (RDITEM ASC, RDCON# DESC, RDSEQ# DESC) in herc_4k;
create unique index hercdb.RAODETF7 on hercdb.RAODETFL (RDCMP ASC, RDPONO ASC, RDCON# ASC) in herc_4k;
create unique index hercdb.RAODETF12 on hercdb.RAODETFL (RDCMP ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RAODETF13 on hercdb.RAODETFL (RDCMP ASC, RDITEM ASC) in herc_4k;
create unique index hercdb.RAODETF15 on hercdb.RAODETFL (RDCMP ASC, RDLOC ASC, RDITEM ASC, RDSTKC ASC, RDTYPE ASC, RDSTTS ASC) in herc_4k;
create unique index hercdb.RAODETF14 on hercdb.RAODETFL (RDCMP ASC, RDCUS# ASC, RDTYPE ASC) in herc_4k;
create unique index hercdb.RAODETET1 on hercdb.RAODETFL (RDCUS# ASC, RDTYPE ASC, RDCMP ASC, RDCATG ASC, RDCLAS ASC, RDCON# ASC, RDSEQ# ASC) in herc_4k;

create index hercdb.RAOHDRFL on hercdb.RAOHDRFL (RHCMP ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF5 on hercdb.RAOHDRFL (RHCMP ASC, RHCUS# ASC, RHJOB# ASC, RHLBDT ASC, RHDATO ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF2 on hercdb.RAOHDRFL (RHCMP ASC, RHCUS# ASC, RHDLST ASC, RHDL# ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF11 on hercdb.RAOHDRFL (RHCMP ASC, RHTYPE ASC, RHDATO DESC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF6 on hercdb.RAOHDRFL (RHCMP ASC, RHLOC ASC, RHCJOB ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRH12 on hercdb.RAOHDRFL (RHCMP ASC, RHCUS# ASC, RHJOB# ASC, RHSYSD ASC) in herc_4k;
create unique index hercdb.RAOHDRF3 on hercdb.RAOHDRFL (RHCMP ASC, RHLBDT DESC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF4 on hercdb.RAOHDRFL (RHCMP ASC, RHLOC ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF10 on hercdb.RAOHDRFL (RHCMP ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF7 on hercdb.RAOHDRFL (RHCMP ASC, RHLOC ASC) in herc_4k;
create unique index hercdb.RAOHDRF9 on hercdb.RAOHDRFL (RHCMP ASC, RHYT#I ASC) in herc_4k;
create unique index hercdb.RAOHDRF8 on hercdb.RAOHDRFL (RHCMP ASC, RHLOC ASC) in herc_4k;
create unique index hercdb.RAOHDRF16 on hercdb.RAOHDRFL (RHCMP ASC, RHCUS# ASC, RHSREP ASC) in herc_4k;
create unique index hercdb.RAOHDRF17 on hercdb.RAOHDRFL (RHCMP ASC, RHDLST ASC, RHDL# ASC, RHSREP ASC) in herc_4k;
create unique index hercdb.RAOHDRF18 on hercdb.RAOHDRFL (RHCMP ASC, RHSREP ASC) in herc_4k;
create unique index hercdb.RAOHDRF19 on hercdb.RAOHDRFL (RHCMP ASC, RHCUS# ASC, RHTYPE ASC, RHSYSD ASC) in herc_4k;
create unique index hercdb.RAOHDRET1 on hercdb.RAOHDRFL (RHCUS# ASC, RHTYPE ASC, RHCMP ASC, RHJOB# ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRET5 on hercdb.RAOHDRFL (RHCUS# ASC, RHTYPE ASC, RHCMP ASC, RHCON# ASC) in herc_4k;
create unique index hercdb.RAOHDRF20 on hercdb.RAOHDRFL (RHCMP ASC, RHMN2$ ASC) in herc_4k;

create unique index hercdb.RAPKUPFL on hercdb.RAPKUPFL (RUCMP ASC, RUPKU# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF5 on hercdb.RAPKUPFL (RUCMP ASC, RULOC ASC, RUCATG ASC, RUCLAS ASC, RUEQP# ASC, RUPKUD ASC, RUPKUT ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF3 on hercdb.RAPKUPFL (RUCMP ASC, RUCON# ASC, RUCATG ASC, RUCLAS ASC, RUEQP# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF6 on hercdb.RAPKUPFL (RUCMP ASC, RUPKU# ASC, RUCATG ASC, RUCLAS ASC, RUEQP# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF7 on hercdb.RAPKUPFL (RUCMP ASC, RUCUS# ASC, RUJOB# ASC, RUCON# ASC, RUPKU# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF9 on hercdb.RAPKUPFL (RUCMP ASC, RUPKU# ASC, RUCATG ASC, RUCLAS ASC, RUEQP# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUP11 on hercdb.RAPKUPFL (RUCMP ASC, RUCON# ASC, RUSEQ# ASC, RUEQP# ASC, RUSYSD DESC, RUSYST DESC) in herc_4k;
create unique index hercdb.RAPKUPF0 on hercdb.RAPKUPFL (RUCMP ASC, RULOC ASC, RUECAT ASC, RUECLS ASC, RUEEQP ASC) in herc_4k;
create unique index hercdb.RAPKUPF10 on hercdb.RAPKUPFL (RUCMP ASC, RUEQP# ASC, RUCON# ASC, RUMTDT ASC, RUMTTM ASC) in herc_4k;
create unique index hercdb.RAPKUPF4 on hercdb.RAPKUPFL (RUCMP ASC, RUCUS# ASC, RUCON# ASC, RUPKU# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF8 on hercdb.RAPKUPFL (RUCMP ASC, RUCUS# ASC, RUCON# ASC, RUPKU# ASC, RUSEQ# ASC) in herc_4k;
create unique index hercdb.RAPKUPF11 on hercdb.RAPKUPFL (RUCMP ASC, RUEEQP ASC, RUMTDT ASC, RUMTTM ASC) in herc_4k;
create unique index hercdb.RAPKUPF2 on hercdb.RAPKUPFL (RUCMP ASC, RUCON# ASC, RUSEQ# ASC, RUPKU# ASC) in herc_4k;
create unique index hercdb.RAPKUP10 on hercdb.RAPKUPFL (RUCMP ASC, RUCON# ASC, RUSEQ# ASC, RUPKU# ASC) in herc_4k;
create unique index hercdb.RAPKUP12 on hercdb.RAPKUPFL (RUCMP ASC, RUCATG ASC, RUCLAS ASC) in herc_4k;
create unique index hercdb.RAPKUPLF01 on hercdb.RAPKUPFL (RUCMP ASC, RUEQP# ASC) in herc_4k;

create index hercdb.SYSLOCFL on hercdb.SYSLOCFL (ZLCMP ASC, ZLLOC ASC) in herc_4k;
create unique index hercdb.SYSLOCF2 on hercdb.SYSLOCFL (ZLCMP ASC, ZLNAME ASC) in herc_4k;
create unique index hercdb.SYSLOCF3 on hercdb.SYSLOCFL (ZLCMP ASC, ZLOYD# ASC) in herc_4k;
create unique index hercdb.SYSLOCF4 on hercdb.SYSLOCFL (ZLCMP ASC, ZLIRGN ASC) in herc_4k;
create unique index hercdb.SYSLOCF5 on hercdb.SYSLOCFL (ZLCMP ASC, ZLARGN ASC) in herc_4k;
create unique index hercdb.SYSLOCF6 on hercdb.SYSLOCFL (ZLCMP ASC, ZLWHS ASC) in herc_4k;
create unique index hercdb.SYSLOCF7 on hercdb.SYSLOCFL (ZLCMP ASC, ZLDIST ASC) in herc_4k;
create unique index hercdb.SYSLOCFC on hercdb.SYSLOCFL (ZLCMP ASC, ZLOYD# ASC) in herc_4k;
create unique index hercdb.SYSLOCH1 on hercdb.SYSLOCFL (ZLCMP ASC, ZLARGN ASC, ZLLOC ASC) in herc_4k;
create unique index hercdb.SYSLOCH2 on hercdb.SYSLOCFL (ZLCMP ASC, ZLST ASC) in herc_4k;

create index hercdb.SYSPGMFL on hercdb.SYSPGMFL (ZPCMP ASC, ZPLOC ASC, ZPUSER ASC) in herc_4k;
create unique index hercdb.SYSPGMF2 on hercdb.SYSPGMFL (ZPCMP ASC, ZPLOC ASC, ZPPSWD ASC) in herc_4k;
create unique index hercdb.SYSPGMF4 on hercdb.SYSPGMFL (ZPCMP ASC, ZPUSER ASC) in herc_4k;
create unique index hercdb.SYSPGMF3 on hercdb.SYSPGMFL (ZPUSER ASC) in herc_4k;

create index hercdb.WOHEADFL on hercdb.WOHEADFL (VHCMP ASC, VHWO# ASC) in herc_4k;
create unique index hercdb.WOHEADF4 on hercdb.WOHEADFL (VHCMP ASC, VHEQP# ASC, VHSTTS ASC, VHDATE DESC) in herc_4k;
create unique index hercdb.WOHEADF2 on hercdb.WOHEADFL (VHCMP ASC, VHEQP# ASC, VHWO# ASC) in herc_4k;
create unique index hercdb.WOHEADF12 on hercdb.WOHEADFL (VHCMP ASC, VHEQP# ASC) in herc_4k;
create unique index hercdb.WOHEADF3 on hercdb.WOHEADFL (VHCMP ASC, VHRTIX ASC) in herc_4k;
create unique index hercdb.WOHEADF6 on hercdb.WOHEADFL (VHCMP ASC, VHMAKE ASC, VHWNTY ASC, VHLOC ASC, VHWO# ASC) in herc_4k;
create unique index hercdb.WOHEADF7 on hercdb.WOHEADFL (VHCMP ASC, VHEQP# ASC, VHSYSD ASC, VHSYST ASC) in herc_4k;
create unique index hercdb.WOHEADF8 on hercdb.WOHEADFL (VHCMP ASC, VHEQP# ASC, VHMTDT ASC, VHMTTM ASC) in herc_4k;

-- end of generated SQL 

