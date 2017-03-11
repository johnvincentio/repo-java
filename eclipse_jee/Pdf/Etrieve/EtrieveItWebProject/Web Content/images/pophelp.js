if (document.layers) {n=1;ie=0}
if (document.all) {n=0;ie=1}

function init() {
        if (n) tab = document.tabDiv
                if (n) poptext = document.poptextDiv
                if (ie) tab = tabDiv.style
        if (ie) poptext = poptextDiv.style
}

var tabShow=0;

//Hide-Show Layer
function hidepoptext() {

        if (tabShow == 1) {
                if (n) {
                tab.visibility = "hide";
                tab.left = 0;
                tab.visibility = "show";
                poptext.visibility = "hide";
                tabShow = 0;
                return;
                }
                        if (ie) {
                tab.visibility = "hidden";
                tab.left = 0;
                tab.visibility = "visible";
                poptext.visibility = "hidden";
                tabShow = 0;
                return;
           }
  }
                
        if (tabShow == 0) {
                if (n) {
                tab.visibility = "hide";
                tab.left = 200;
                tab.visibility = "show";
                poptext.visibility = "show";
                tabShow = 1;
                           }
                        if (ie) {
                                tab.visibility = "hidden";
                tab.left = 200;
                tab.visibility = "visible";
                poptext.visibility = "visible";
                tabShow = 1;
                                }
}
}
