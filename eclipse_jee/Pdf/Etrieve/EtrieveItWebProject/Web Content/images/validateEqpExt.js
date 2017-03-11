function validateExtData()
{

	var currdate = new Date();

	var entDate = document.eqpExtFrm.revEstDate.value ;
	
	if(!document.eqpExtFrm.revEstDate.value)
	{
		alert("Please enter a valid date");
		document.eqpExtFrm.revEstDate.focus();
		return false;
	}
	
	
	var month = entDate.substring(0,2);
	var day = entDate.substring(3,5);
	var year = entDate.substring(6,10);
	
	var userDate = new Date();
	userDate.setDate(day);
	userDate.setMonth(month-1);
	userDate.setFullYear(year); 

	if (userDate <= currdate)
	{
		alert("New date must after today");
		document.eqpExtFrm.revEstDate.focus();
		return false;
	}

	var nxtyr = currdate.getYear();
	nxtyr = nxtyr + 1 ;
	var nextYrDate = new Date();
	nextYrDate.setFullYear(nxtyr); 

	if (userDate > nextYrDate)
	{
		alert("Date must be within 12 months from today");
		document.eqpExtFrm.revEstDate.focus();
		return false;
	}
	
	if(!document.eqpExtFrm.contactName.value)
	{
		alert("Please enter a contact name");
		document.eqpExtFrm.contactName.focus();
		return false;
	}
	
	if(!document.eqpExtFrm.contactPhone1.value)
	{
		alert("Please enter an area code");
		document.eqpExtFrm.contactPhone1.focus();
		return false;
	}

	if(IsNumeric(document.eqpExtFrm.contactPhone1.value) == false)
	{
		alert("Please enter only numeric values for the area code");
		//document.eqpExtFrm.contactPhone1.value = "000";
		document.eqpExtFrm.contactPhone1.focus();
		return false;
	}

	if(!document.eqpExtFrm.contactPhone2.value)
	{
		alert("Please enter a valid contact phone number");
		document.eqpExtFrm.contactPhone2.focus();
		return false;
	}

	if(IsNumeric(document.eqpExtFrm.contactPhone2.value) == false)
	{
		alert("Please enter a valid contact phone number");
		//document.eqpExtFrm.contactPhone2.value = "000";		
		document.eqpExtFrm.contactPhone2.focus();
		return false;
	}

	if(!document.eqpExtFrm.contactPhone3.value)
	{
		alert("Please enter a valid contact phone number");
		document.eqpExtFrm.contactPhone3.focus();
		return false;
	}

	if(IsNumeric(document.eqpExtFrm.contactPhone3.value) == false)
	{
		alert("Please enter only numeric values for the contact phone number");
		//document.eqpExtFrm.contactPhone3.value = "0000";		
		document.eqpExtFrm.contactPhone3.focus();
		return false;
	}

	if(!document.eqpExtFrm.userComments.value)
	{
		alert("Please enter detailed information");
		document.eqpExtFrm.userComments.focus();
		return false;
	}

	return true;	
}

function IsNumeric(strString)
{

   var strValidChars = "0123456789";
   var strChar;
   var blnResult = true;

   if (strString.length == 0) return false;
   for (i = 0; i < strString.length && blnResult == true; i++)
   {
      strChar = strString.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
         blnResult = false;
   }
   return blnResult;
   
}

