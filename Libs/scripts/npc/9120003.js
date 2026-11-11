/*
	Hikari - Showa Town(801000000)
*/

var status = -1;

function start() {
    action(1,0,0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	cm.sendOk(" 不好意思，已客滿，請待回再來 !");
	cm.dispose();
	return;
    }
    if (status == 0) {
	cm.sendYesNo("只要有300楓幣，就能進入澡堂 !");
    } else if (status == 1) {
	if (cm.getMeso() < 300) {
	    cm.sendOk("客人，不好意思，您的楓幣不足 !");
	} else {
	    cm.gainMeso(-300);
	    if (cm.getPlayerStat("GENDER") == 0) {
		cm.warp(801000100);
	    } else {
		cm.warp(801000200);
	    }
	}
	cm.dispose();
    }
}
