package com.idc.rm;

public class D1 {

}

/*
select OCCMNT
from RMHCQDATA.ORDCOMFL 
OCCMP='company'
and OCREF#= " +  contract +
and OCISEQ=  " +  seqno +
and OCTYPE= 'R' " +
and OCASEQ= " + lineno;
*/

/*
select OCCMP, OCREF#, OCISEQ, OCTYPE, OCASEQ, OCCMNT from RMHCQDATA.ORDCOMFL;


looks like OCCMP = 'HG'

*/
