/**
	Warrior Job Instructor - Warrior's Rocky Mountain (108000300)
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
	    cm.completeQuest(100004);
	    cm.startQuest(100005);
	    cm.sendOk("恭喜你通過測驗#b！#k拿著我的信物回去找#r武術教練#k吧。");
	} else {
	    cm.sendOk("還沒收集到#r30#k個#b#t4031013##k嗎 #r？#k")
	    cm.dispose();
	}
    } else if (status == 1) {
        cm.gainItem(4031008, -1);
	cm.gainItem(4031012, 1);
	cm.warp(102020300, 0);
	cm.dispose();
    }
}	