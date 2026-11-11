/*  NPC : Harmonia
	Warrior 4th job advancement
	Forest of the priest (240010501)
*/

var status = -1;
var skills = Array(21001003, 21000000, 21100002, 21100004, 21100005, 21110002);
//神速之矛、矛之鬥氣、突刺之矛、猛擲之矛、連環吸血、伺機攻擊

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	if (!(cm.getJob() == 111 || cm.getJob() == 121 || cm.getJob() == 131 || cm.getJob() == 2111)) {
	    cm.sendOk("#r真神#k與你同在!");
	    cm.dispose();
	    return;
	} else if (cm.getPlayerStat("LVL") < 120) {
	    cm.sendOk("等你符合轉職資格後再來找我");
	    cm.dispose();
	    return;
	} else {
	    if (cm.getQuestStatus(6904) == 2 || cm.getJob() == 2111) {
		if (cm.getJob() == 111)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為英雄#l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 121)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為聖騎士#l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 131)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為黑騎士#l\r\n#b#L1#  讓我再考慮看看#l");
		else {
		    if (cm.haveItem(4031348)) {
		        cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想進行第四次轉職#l\r\n#b#L1#  讓我再考慮看看#l");
		    } else {
			cm.sendNext("你需要#r1000#k萬楓幣才能獲得#b秘咒#k。");
			cm.dispose();
			return;
		    }
		}
	    } else {
		cm.sendOk("只要你有足夠的財力，神木村的村長能幫你完成第4次轉職任務 !");
		cm.dispose();
		return;
	    }
	}
    } else if (status == 1) {
	if (selection == 1) {
	    cm.sendOk("如果你準備好了, 我將為你進行第四次轉職 ! ");
	    cm.dispose();
	    return;
	}
	if (cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 120) * 3) {
	    cm.sendOk("嗯... 你的技能點數尚未使用完畢, 請使用完技能點數後再來找我 !");
	    cm.dispose();
	    return;
	} else {
	    if (cm.canHold(2280003)) {
		cm.gainAp(5);
		cm.gainItem(2280003, 1);

		if (cm.getJob() == 111) {
		    cm.changeJob(112);
	            cm.teachSkill(1120003, 0, 10); // 進階鬥氣
	            cm.teachSkill(1120004, 0, 10); // 武神防禦
	            cm.teachSkill(1121001, 0, 10); // 絕對引力
	            cm.teachSkill(1121002, 0, 10); // 格擋
	            cm.teachSkill(1121006, 0, 10); // 究極突刺
	            cm.teachSkill(1121008, 0, 10); // 無雙劍舞
	            cm.teachSkill(1121000, 0, 10); // 楓葉祝福
	            cm.teachSkill(1121010, 0, 10); // 鬥氣爆發
	            cm.teachSkill(1221011, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b英雄#k");
		} else if (cm.getJob() == 121) {
		    cm.changeJob(122);
		    cm.teachSkill(1220005, 0, 10); // 武神防禦
		    cm.teachSkill(1220010, 0, 10); // 屬性強化
		    cm.teachSkill(1221001, 0, 10); // 絕對引力
		    cm.teachSkill(1221002, 0, 10); // 格擋
		    cm.teachSkill(1221003, 0, 10); // 聖靈之劍
		    cm.teachSkill(1221004, 0, 10); // 聖靈之棍
		    cm.teachSkill(1221007, 0, 10); // 究極突刺
		    cm.teachSkill(1221009, 0, 10); // 騎士衝擊波
		    cm.teachSkill(1221000, 0, 10); // 楓葉祝福
		    cm.teachSkill(1221011, 0, 10); // 鬼神之擊
		    cm.teachSkill(1221012, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b聖騎士#k");
		} else if (cm.getJob() == 131) {
		    cm.changeJob(132);
		    cm.teachSkill(1320005, 0, 10); // 武神防禦
		    cm.teachSkill(1320006, 0, 10); // 黑暗力量
		    cm.teachSkill(1320008, 0, 10); // 闇靈治癒
		    cm.teachSkill(1320009, 0, 10); // 黑暗守護
		    cm.teachSkill(1321001, 0, 10); // 絕對引力
		    cm.teachSkill(1321002, 0, 10); // 格擋
		    cm.teachSkill(1321003, 0, 10); // 究極突刺
		    cm.teachSkill(1321007, 0, 10); // 暗之靈魂
		    cm.teachSkill(1321000, 0, 10); // 楓葉祝福
		    cm.teachSkill(1321010, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b黑騎士#k");
		} else {
		    cm.gainItem(4031348, -1);
		    cm.gainItem(4031343, -1);
		    cm.gainItem(4031344, -1);
		    cm.changeJob(2112);
		for (var i = 0; i < skills.length; i++) {
			cm.teachSkill(skills[i], cm.getPlayer().getSkillLevel(skills[i]));
		}
		    cm.teachSkill(21120001, 0, 10); // 攻擊戰術
		    cm.teachSkill(21120002, 0, 10); // 終極攻擊
		    cm.teachSkill(21120004, 0, 10); // 防禦戰術
		    cm.teachSkill(21120005, 0, 10); // 終極之矛
		    cm.teachSkill(21120006, 0, 10); // 極冰暴風
		    cm.teachSkill(21120007, 0, 10); // 宙斯之盾
		    cm.teachSkill(21120009, 0, 10); // 終極攻擊-雙重攻擊
		    cm.teachSkill(21120010, 0, 10); // 終極攻擊-三重攻擊
		    cm.teachSkill(21121000, 0, 10); // 楓葉祝福
		    cm.teachSkill(21121003, 0, 10); // 靈魂戰鬥
		    cm.teachSkill(21121008, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b狂狼勇士#k");
		}
	    } else {
		cm.sendOk("你的消耗欄沒有多餘的空間 !");
		cm.dispose();
		return;
	    }
	}
    } else if (status == 2) {
	cm.sendNextPrev("透過擊敗#rBOSS#k後取得的技能書，有機會使你的技能得到進階 !");
	cm.dispose();
    }
}