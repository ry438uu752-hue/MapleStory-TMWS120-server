/*  NPC : Gritto
	Magician 4th job advancement
	Forest of the priest (240010501)
*/

var status = -1;

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
	if (!(cm.getJob() == 211 || cm.getJob() == 221 || cm.getJob() == 231)) {
	    cm.sendOk("#r真神#k與你同在!");
	    cm.dispose();
	    return;
	} else if (cm.getPlayerStat("LVL") < 120) {
	    cm.sendOk("等你符合轉職資格後再來找我");
	    cm.dispose();
	    return;
	} else {
	    if (cm.getQuestStatus(6914) == 2) {
		if (cm.getJob() == 211)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為大魔導士(火,毒)#l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 221)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為大魔導士(冰,雷)#l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 231)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為主教#l\r\n#b#L1#  讓我再考慮看看#l");
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

		if (cm.getJob() == 211) {
          	    cm.changeJob(212);
          	    cm.teachSkill(2121000, 0, 10); // 楓葉祝福
          	    cm.teachSkill(2121001, 0, 10); // 核爆術
          	    cm.teachSkill(2121002, 0, 10); // 魔法反射
          	    cm.teachSkill(2121003, 0, 10); // 炎靈地獄
          	    cm.teachSkill(2121004, 0, 10); // 魔力無限
          	    cm.teachSkill(2121005, 0, 10); // 召喚火炎神
          	    cm.teachSkill(2121006, 0, 10); // 劇毒麻痺
          	    cm.teachSkill(2121007, 0, 10); // 火流星
          	    cm.teachSkill(2121008, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b大魔導士(火,毒)#k");
		} else if (cm.getJob() == 221) {
         	    cm.changeJob(222);
         	    cm.teachSkill(2221000, 0, 10); // 楓葉祝福
         	    cm.teachSkill(2221001, 0, 10); // 核爆術
         	    cm.teachSkill(2221002, 0, 10); // 魔法反射
         	    cm.teachSkill(2221003, 0, 10); // 寒冰地獄
         	    cm.teachSkill(2221004, 0, 10); // 魔力無限
         	    cm.teachSkill(2221005, 0, 10); // 召喚冰魔
         	    cm.teachSkill(2221006, 0, 10); // 閃電連擊
         	    cm.teachSkill(2221007, 0, 10); // 暴風雪
         	    cm.teachSkill(2221008, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b大魔導士(冰,雷)#k");
		} else if (cm.getJob() == 231) {
		    cm.gainItem(4031348, -1);
		    cm.gainItem(4031511, -1);
		    cm.gainItem(4031512, -1);
		    cm.changeJob(232);
		    cm.teachSkill(2321000, 0, 10); // 楓葉祝福
		    cm.teachSkill(2321001, 0, 10); // 核爆術
		    cm.teachSkill(2321002, 0, 10); // 魔法反射
		    cm.teachSkill(2321003, 0, 10); // 召喚神龍
		    cm.teachSkill(2321004, 0, 10); // 魔力無限
		    cm.teachSkill(2321005, 0, 10); // 聖盾護鎧
		    cm.teachSkill(2321006, 0, 10); // 復甦之光
		    cm.teachSkill(2321007, 0, 10); // 天使之箭
		    cm.teachSkill(2321008, 0, 10); // 天怒
		    cm.teachSkill(2321009, 0, 10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b主教#k");
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