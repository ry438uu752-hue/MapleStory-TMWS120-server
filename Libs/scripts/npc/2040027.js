/*
	Fourth Eos Rock - Ludibrium : Eos Tower 1st Floor (221020000)

*/

var status = 0; 
function start() {
    
status = -1;
    
action(1, 0, 0);

}


function action(mode, type, selection) {
    
if (status >= 0 && mode == 0) {
	
cm.dispose();

	return;

    }
    if (mode == 1)

	status++;

    else

	status--;
    
if (status == 0) {
	
if (cm.haveItem(4001020)) {
	    
cm.sendYesNo("你可以使用 #i4001020# #b#t4001020##k 瞬間移動到 41樓的 #b愛奧斯之石Ⅲ#k, 你要移動到 41樓 ?");

	} else {
	    
cm.sendOk("你身上沒有  #b#i4001020# #t4001020##k ！");
	    
cm.dispose();

	}

    } else if (status == 1) {
	
cm.gainItem(4001020, -1);
	
cm.warp(221021700, 0);
	
cm.dispose();

    }

}