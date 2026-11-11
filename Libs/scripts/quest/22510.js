var status = -1;
//this quest is DELIVER LETTER
function start(mode, type, selection) {
	if (qm.canHold(4032455,1)) {
		qm.sendNext("到弓箭手村找長老斯坦");
		qm.gainItem(4032455,1);
		qm.forceStartQuest();
	} else {
		qm.sendNext("你的背包空間不足");
	}
	qm.dispose();
}

function end(mode, type, selection) {
	if (qm.haveItem(4032455,1)) {
		qm.sendNext("謝謝");
		qm.getPlayer().gainSP(1, 0);
		qm.gainExp(450);
		qm.gainItem(4032455, -1);
		qm.forceCompleteQuest();
	} else {
		qm.sendNext("你沒有克里特的信件");
	}
	qm.dispose();
}