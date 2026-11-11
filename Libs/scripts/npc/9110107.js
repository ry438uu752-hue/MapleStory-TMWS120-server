/*
	NPC Name: 		Palakeen
	Map(s): 		Zipangu - Mushroom Shrine
	Description: 		Kaede Castle teleporter
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    cm.sendNext("等你決定好了再來吧!");
	    cm.dispose();
	    return;
	}
	status--;
    }
    switch (cm.getMapId()) {
	case 800040000: {
	    if (status == 0) {
		cm.sendNext("你想回古代神社了嗎?");
	    } else if (status == 1) {
		cm.sendYesNo("返回古代神社?");
	    } else if (status == 2) {
		cm.warp(800000000);
		cm.dispose();
	    }
	    break;
	}
	default: {
	    if (status == 0) {
		cm.sendNext("這是往楓葉古城的轎子");
	    } else if (status == 1) {
		cm.sendYesNo("前往楓葉古城?");
	    } else if (status == 2) {
		cm.warp(800040000);
		cm.dispose();
	    }
	    break;
	}
    }
}