/**
	Magician Job Instructor - Magician's Tree Dungeon (108000200)
**/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	if (cm.haveItem(4031013,30)) {
	    cm.removeAll(4031013);
	    cm.completeQuest(100007);
	    cm.startQuest(100008);
	    cm.sendOk("恭喜你通過測驗#b！#k拿著我的信物回去找#r漢斯#k吧。");
	} else {
	    cm.sendOk("還沒收集到#r30#k個#b#t4031013##k嗎#r？#k")
	    cm.dispose();
	}
    } else if (status == 1) {
        cm.gainItem(4031009, -1);
	cm.gainItem(4031012, 1);
	cm.warp(101020000, 0);
	cm.dispose();
    }
}	