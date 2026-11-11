/*
 
*/


function start() {
    
cm.sendYesNo("#r你想前往 #b勇士聖殿 #r嗎#k ？");

}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(102000003);

    }
    
cm.dispose();

}