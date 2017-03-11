//<!--
function popup(href)
{
    if (screen.height > 0 && screen.width > 0 &&
	screen.height < 600 && screen.width < 800)
    {
	location.replace("/help/screensize.html");
	return false;
    }

    var popwin;
    popwin = window.open(href, 'game', 'width=790,height=545,resizable=0,toolbar=0,directories=0,scrollbars=0,status=0,screenX=0,screenY=0,top=0,left=0');

    if (popwin != null) {
	popwin.focus();
    } else {
	alert("Not enough memory available to open game window.  Close some other applications and try again.");
    }

    return false;
}
//-->
