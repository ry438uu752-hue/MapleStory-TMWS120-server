/*
 
*/


function start() {
    
cm.sendYesNo("#r你想前往 #b弓箭手培訓中心 #r嗎#k ？");

}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(100000201);

    }
    
cm.dispose();

}