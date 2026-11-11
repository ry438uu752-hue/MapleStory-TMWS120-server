/*  NPC : Legor
    Bowman 4th job advancement
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
	if (!(cm.getJob() == 311 || cm.getJob() == 321)) {
	    cm.sendOk("#r真神#k與你同在!");
	    cm.dispose();
	    return;
	} else if (cm.getPlayerStat("LVL") < 120) {
	    cm.sendOk("等你符合轉職資格後再來找我");
	    cm.dispose();
	    return;
	} else {
	    if (cm.getQuestStatus(6924) == 2) {
		if (cm.getJob() == 311)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為箭神 #l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 321)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為神射手#l\r\n#b#L1#  讓我再考慮看看#l");
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

		if (cm.getJob() == 311) {
		    cm.changeJob(312);
                    cm.teachSkill(3120005,0,10); // 弓術精通
                    cm.teachSkill(3121002,0,10); // 會心之眼
                    cm.teachSkill(3121003,0,10); // 龍魂之箭
                    cm.teachSkill(3121004,0,10); // 暴風神射		    
                    cm.teachSkill(3121006,0,10); // 召喚鳳凰
                    cm.teachSkill(3121007,0,10); // 牽制射擊
                    cm.teachSkill(3121008,0,10); // 念力集中
                    cm.teachSkill(3121009,0,10); // 楓葉淨化		    
                    cm.teachSkill(3121000,0,10); // 楓葉祝福
		    cm.sendNext("你已經成為最終極的勇者 #b箭神#k");
		} else if (cm.getJob() == 321) {
                    cm.gainItem(4031348, -1);
                    cm.gainItem(4031514, -1);
                    cm.gainItem(4031515, -1);
		    cm.changeJob(322);
		    cm.teachSkill(3220004,0,10); // 弩術精通
		    cm.teachSkill(3221001,0,10); // 光速神弩
		    cm.teachSkill(3221002,0,10); // 會心之眼
		    cm.teachSkill(3221003,0,10); // 龍魂之箭
		    cm.teachSkill(3221005,0,10); // 召喚銀隼
		    cm.teachSkill(3221006,0,10); // 黑暗狙擊
		    cm.teachSkill(3221007,0,10); // 必殺狙擊
		    cm.teachSkill(3221008,0,10); // 楓葉淨化		    		    
		    cm.teachSkill(3221000,0,10); // 楓葉祝福
		    cm.sendNext("你已經成為最終極的勇者 #b神射手#k");
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