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
	if (cm.haveItem(4031856,30)) {
	    cm.completeQuest(100007);
	    cm.startQuest(100008);
	    cm.sendOk("恭喜你通過測驗#b！#k給你的證明別搞丟了，我回航海室等你。");
	} else {
	    cm.sendOk("等級達到#r10#k就有資格成為海盜")
	    cm.dispose();
	}
    } else if (status == 1) {
        cm.gainItem(4031607, -1);
	cm.gainItem(4031012, 1);
	cm.removeAll(4031856);
	cm.warp(120000104, 0);
	cm.dispose();
    }
}	