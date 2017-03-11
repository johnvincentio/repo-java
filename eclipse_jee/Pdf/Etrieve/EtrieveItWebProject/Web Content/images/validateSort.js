
function validate()
{
	s1 =  document.sortFrm.sortFld0.selectedIndex;
	s2 =  document.sortFrm.sortFld1.selectedIndex;
	s3 =  document.sortFrm.sortFld2.selectedIndex;
	

	if ( s1 != 0 &&  ( (s1 == s2) || (s1 == s3) ) )
	{ 		
		alert("Duplicate sort selections are not valid. Please try again.");
		return false ;
	} 
	
	if ( s2 != 0 && ( (s2 == s1) || (s2 == s3) ) )
	{ 		
		alert("Duplicate sort selections are not valid. Please try again.");
		return false ;
	}		
			
	if ( s3 != 0 && ( (s3 == s1) || (s3 == s2) ) )
	{ 		
		alert("Duplicate sort selections are not valid. Please try again.");
		return false ;
	}		
		
	return true ;
	
}