/*
Third Eos Rock - Ludibrium : Eos Tower 41st Floor (221021700)

*/

var status = 0;

var map;

var portal;


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
	    
cm.sendSimple("你可以使用 #i4001020# #b#t4001020##k 瞬間移動到 71樓的 #b愛奧斯之石Ⅱ#k和  1樓的 #b愛奧斯之石Ⅳ#k, 你要移動到哪一層 ?#b\r\n#L0#愛奧斯之石Ⅱ (愛奧斯塔 71樓)#l\r\n#L1#愛奧斯之石Ⅳ (愛奧斯塔  1樓)#l");

	} else {
	    
cm.sendOk("你身上沒有  #b#i4001020# #t4001020##k ！");
	    
cm.dispose();

	}

    } else if (status == 1) {
	
if (selection == 0) {
	    
cm.sendYesNo("你確定你要移動到 71樓 ？");
	    
map = 221022900;
	    

	} else {
	    
cm.sendYesNo("你確定你要移動到  1樓 ？");
	    
map = 221020000;
	    

	}

    } else if (status == 2) {
	
cm.gainItem(4001020, -1);
	
cm.warp(map, 0);
	
cm.dispose();

    }

}