var status = -1;
var skills = Array(21120001, 21120002, 2112004, 21120005, 21120006, 21120007, 21121000, 21121003, 21121008);
//polearm booster, combo ability, polearm mastery, final charge, combo smash, combo drain, full swing

function start(mode, type, selection) {
	qm.sendNext("到企鵝港口水底下打倒我的分身後再回來找我!");
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
		qm.sendNextS("帶著秘咒去祭司之林找劍士轉職官，完成最終的轉職", 2);
		qm.changeJob(2111);
		qm.gainItem(1142132, 1);
		qm.forceCompleteQuest(21401);
	for (var i = 0; i < skills.length; i++) {
		qm.teachSkill(skills[i], qm.getPlayer().getSkillLevel(skills[i]));
	}
		qm.forceCompleteQuest();
	} else if (status == 2) {
		qm.sendOk(" 繼續邁向終極之路吧！");
		qm.dispose();
	}
}