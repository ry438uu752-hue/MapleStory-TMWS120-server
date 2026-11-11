/*
	First Eos Rock - Ludibrium : Eos Tower 100th Floor (221024400)

**/

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
	    
cm.sendYesNo("你可以使用 #i4001020# #b#t4001020##k 瞬間移動到71樓的 #b愛奧斯之石II#k, 你要移動到愛奧斯塔 71 樓嗎 ?");

	} else {
	    
cm.sendOk("你身上沒有  #b#i4001020# #t4001020##k ！");
	    
cm.dispose();

	}

    } else if (status == 1) {
	
cm.gainItem(4001020, -1);
	
cm.warp(221022900, 0);
	
cm.dispose();

    }

}