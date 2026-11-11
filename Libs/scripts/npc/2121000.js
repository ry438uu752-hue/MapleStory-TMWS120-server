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

    if(cm.haveItem(4001337,-1)) {

cm.sendYesNo("你想回 #b旅館#k 休息 #r?#k 好的, 我這就送您回去 #r!#k \n\r 期待您下次的光臨 #r~#k");

    } else {
	    
cm.sendOk("12345");
	    
cm.dispose();

	}

    }

    if (status == 1) {
	
cm.gainItem(4001337, 0);
	
cm.warp(749030000,0);
	
cm.dispose();

    }

}
