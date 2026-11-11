/*
 
*/


function start() {
    
cm.sendYesNo("#r你想前往 #b魔法森林圖書館 #r嗎#k ？");

}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(101000003);

    }
    
cm.dispose();

}