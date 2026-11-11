/**
	Orbis Magic Spot - Orbis Tower <1st Floor>(229010000)

**/

function start() {

    status = -1;

    action(1, 0, 0);
}


function action(mode, type, selection) {

    if (mode == 0) {

	cm.dispose();

	return;

    }
    status++;

    if (status == 0) {

    if(cm.haveItem(4001337)) {

cm.sendYesNo("你好! 如果你有#b#t4001337##i4001337##k, 就可以進入 #b睡覺的南瓜#k地圖");

    } else {
	    
cm.sendOk("你身上沒有 #b#t4001337##i4001337##k  ！");
	    
cm.dispose();

	}

    }

    if (status == 1) {
	
cm.gainItem(4001337, -1);
	
cm.warp(749050300,0);
	
cm.dispose();

    }

}
