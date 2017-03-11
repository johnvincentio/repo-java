function validateData()
{

	var count = 0;

	if (document.eqpRelFrm.num_count.value > 1)
	{
	
		for(var i=0 ; i< document.eqpRelFrm.num_count.value ;i++)
		{
	
			if (document.eqpRelFrm.EqpChkd[i].checked)
			{	
				count = count + 1;	
				document.eqpRelFrm.eqpSel[i].value = "Y";
			}	
			else
				document.eqpRelFrm.eqpSel[i].value = "N";
		}	
	}
	else
	{
	
			if (document.eqpRelFrm.EqpChkd.checked)
			{	
				count = count + 1;	
				document.eqpRelFrm.eqpSel.value = "Y";
			}	
			else
				document.eqpRelFrm.eqpSel.value = "N";
	
	
	}
	
	if (count == 0)
	{	
		alert("Please select at least one item to release");
		return false ;
	}	

	if(!document.eqpRelFrm.contactName.value)
	{
		alert("Please enter a contact name");
		document.eqpRelFrm.contactName.focus();
		return false;
	}
	
	if(!document.eqpRelFrm.contactPhone1.value)
	{
		alert("Please enter an area code");
		document.eqpRelFrm.contactPhone1.focus();
		return false;
	}

	if(IsNumeric(document.eqpRelFrm.contactPhone1.value) == false)
	{
		alert("Please enter only numeric values for the area code");
		//document.eqpRelFrm.contactPhone1.value = "000";
		document.eqpRelFrm.contactPhone1.focus();
		return false;
	}

	if(!document.eqpRelFrm.contactPhone2.value)
	{
		alert("Please enter a valid contact phone number");
		document.eqpRelFrm.contactPhone2.focus();
		return false;
	}

	if(IsNumeric(document.eqpRelFrm.contactPhone2.value) == false)
	{
		alert("Please enter a valid contact phone number");
		//document.eqpRelFrm.contactPhone2.value = "000";		
		document.eqpRelFrm.contactPhone2.focus();
		return false;
	}

	if(!document.eqpRelFrm.contactPhone3.value)
	{
		alert("Please enter a valid contact phone number");
		document.eqpRelFrm.contactPhone3.focus();
		return false;
	}

	if(IsNumeric(document.eqpRelFrm.contactPhone3.value) == false)
	{
		alert("Please enter a valid contact phone number");
		//document.eqpRelFrm.contactPhone3.value = "0000";		
		document.eqpRelFrm.contactPhone3.focus();
		return false;
	}

	if(!document.eqpRelFrm.userComments.value)
	{
		alert("Please enter detailed information regarding access");
		document.eqpRelFrm.userComments.focus();
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


