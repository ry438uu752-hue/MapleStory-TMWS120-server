var status = -1;
var skills = Array(21110000, 21110002, 21110003, 21110004, 21110006, 21111001, 21111005);
//polearm booster, combo ability, polearm mastery, final charge, combo smash, combo drain, full swing

function start(mode, type, selection) {
	qm.sendNext("你想證明你的強壯? 很簡單，那就去消滅這附近的 #r#o9001012#s#k 並且收集 #r30#k 個 #b#t4032311##k 回來給我!");
	qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		qm.sendNext("哈！ 你已經證明了自己的價值......你將得到你想要的東西");
	} else if (status == 1) {
	if (qm.getPlayerStat("RSP") > (qm.getPlayerStat("LVL") - 30) * 3) {
	    qm.sendNext("你的技能點數還未使用完");
	    qm.dispose();
	    return;
	}
		qm.sendNextS("我的記憶正在恢復......", 2);
		qm.changeJob(2111);
		qm.gainItem(1142131, 1);
		qm.forceCompleteQuest(21301);
	for (var i = 0; i < skills.length; i++) {
		qm.teachSkill(skills[i], qm.getPlayer().getSkillLevel(skills[i]));
	}
		qm.forceCompleteQuest();
	} else if (status == 2) {
		qm.sendOk("哈哈！ 你已經得到你想要的東西了，你可以離開了！");
		qm.dispose();
	}
}