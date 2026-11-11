/*
	Bowman Job Instructor - Ant Tunnel For Bowman (108000100)
*/

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
	    cm.completeQuest(100001);
	    cm.startQuest(100002);
	    cm.sendOk("恭喜你通過測驗#b！#k拿著我的信物回去找#r赫麗娜#k吧 。");
	} else {
	    cm.sendOk("還沒收集到#r30#k個#b#t4031013##k嗎 #r？#k")
	    cm.dispose();
	}
    } else if (status == 1) {
        cm.gainItem(4031010, -1);
	cm.gainItem(4031012, 1);
	cm.warp(106010000, 1);
	cm.dispose();
    }
}	