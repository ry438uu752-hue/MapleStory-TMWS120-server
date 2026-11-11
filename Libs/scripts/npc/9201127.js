/*
 
*/


function start() {
    
cm.sendYesNo("#r你想前往 #b航海室 #r嗎#k ？");

}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(120000101);

    }
    
cm.dispose();

}