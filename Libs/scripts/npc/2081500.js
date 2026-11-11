/*  NPC : Samuel
	Pirate 4th job advancement
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
	if (!(cm.getJob() == 511 || cm.getJob() == 521)) {
	    cm.sendOk("#r真神#k與你同在!");
	    cm.dispose();
	    return;
	} else if (cm.getPlayerStat("LVL") < 120) {
	    cm.sendOk("等你符合轉職資格後再來找我");
	    cm.dispose();
	    return;
	} else {
	    if (cm.getQuestStatus(6944) == 2) {
		if (cm.getJob() == 511)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為拳霸#l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 521)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為槍神#l\r\n#b#L1#  讓我再考慮看看#l");
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

		if (cm.getJob() == 511) {
		    cm.changeJob(512);
		    cm.teachSkill(5121001,0,10); // 魔龍降臨		    
		    cm.teachSkill(5121002,0,10); // 元氣彈	
		    cm.teachSkill(5121003,0,10); // 鬥神降世	
		    cm.teachSkill(5121004,0,10); // 閃．爆破	
		    cm.teachSkill(5121005,0,10); // 閃．索命		
		    cm.teachSkill(5121007,0,10); // 閃．連殺
		    cm.teachSkill(5121008,0,10); // 楓葉淨化
		    cm.teachSkill(5121009,0,10); // 最終極速
		    cm.teachSkill(5121010,0,10); // 時間置換
		    cm.sendNext("你已經成為最終極的勇者 #b拳霸#k");
		} else if (cm.getJob() == 521) {
                    cm.gainItem(4031348, -1);
                    cm.gainItem(4031860, -1);
                    cm.gainItem(4031861, -1);
		    cm.changeJob(522);
                    cm.teachSkill(5220001,0,10); // 瞬‧冰火連擊		    		    
                    cm.teachSkill(5220002,0,10); // 砲台章魚王	
                    cm.teachSkill(5220011,0,10); // 精準砲擊	
                    cm.teachSkill(5221003,0,10); // 海鷗特戰隊	
                    cm.teachSkill(5221004,0,10); // 瞬‧迅雷	
                    cm.teachSkill(5221006,0,10); // 海盜船
                    cm.teachSkill(5221007,0,10); // 海盜加農炮
                    cm.teachSkill(5221008,0,10); // 海盜魚雷
                    cm.teachSkill(5221009,0,10); // 心靈控制
                    cm.teachSkill(5221000,0,10); // 楓葉祝福
                    cm.teachSkill(5221010,0,10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b槍神#k");
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