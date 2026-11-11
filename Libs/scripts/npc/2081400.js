/*  NPC : Hellin
	Thief 4th job advancement
	Forest of the priest (240010501)
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }

    if (status == 0) {
	if (!(cm.getJob() == 411 || cm.getJob() == 421 || cm.getJob() == 433)) {
	    cm.sendOk("#r真神#k與你同在!");
	    cm.safeDispose();
	    return;
	} else if (cm.getPlayerStat("LVL") < 120) {
	    cm.sendOk("等你符合轉職資格後再來找我");
	    cm.safeDispose();
	    return;
	} else {
	    if (cm.getQuestStatus(6934) == 2 || cm.getJob() == 433) {
		if (cm.getJob() == 411)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為夜使者 #l\r\n#b#L1#  讓我再考慮看看#l");
		else if (cm.getJob() == 421)
		    cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想成為暗影神偷 #l\r\n#b#L1#  讓我再考慮看看#l");
		else {
		    if (cm.haveItem(4031348) || cm.getQuestStatus(6934) == 2) {
		        cm.sendSimple("你已具備成為一名勇者的資格。 \r\n你是否要晉級下一個階段?\r\n#b#L0# 我想進行第四次轉職#l\r\n#b#L1#  讓我再考慮看看#l");
		    } else {
			cm.sendNext("你需要#r1000#k萬楓幣才能獲得#b秘咒#k。");
			cm.dispose();
			return;
		    }
		    
		}
	    } else {
		cm.sendOk("只要你有足夠的財力，神木村的村長能幫你完成第4次轉職任務 !");
		cm.safeDispose();
		return;
	    }
	}
    } else if (status == 1) {
	if (selection == 1) {
	    cm.sendOk("如果你準備好了, 我將為你進行第四次轉職 ! ");
	    cm.safeDispose();
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

		if (cm.getJob() == 411) {
		    cm.changeJob(412);
                    cm.teachSkill(4120002,0,10); // 瞬身迴避	
                    cm.teachSkill(4121003,0,10); // 挑釁	
                    cm.teachSkill(4121004,0,10); // 忍影瞬殺
                    cm.teachSkill(4120005,0,10); // 飛毒殺	
                    cm.teachSkill(4121006,0,10); // 無形鏢	
                    cm.teachSkill(4121007,0,10); // 三飛閃	
                    cm.teachSkill(4121008,0,10); // 忍術風影
                    cm.teachSkill(4121009,0,10); // 楓葉淨化
                    cm.teachSkill(4121000,0,10); // 楓葉祝福
		    cm.sendNext("你已經成為最終極的勇者 #b夜使者#k");
		} else if (cm.getJob() == 421) {
		    cm.changeJob(422);
                    cm.teachSkill(4220002,0,10); // 瞬身迴避		    		    
                    cm.teachSkill(4220005,0,10); // 飛毒殺	
                    cm.teachSkill(4221001,0,10); // 致命暗殺	
                    cm.teachSkill(4221003,0,10); // 挑釁	
                    cm.teachSkill(4221004,0,10); // 忍影瞬殺	
                    cm.teachSkill(4221006,0,10); // 煙幕彈
                    cm.teachSkill(4221007,0,10); // 瞬步連擊
                    cm.teachSkill(4221008,0,10); // 楓葉淨化
                    cm.teachSkill(4221000,0,10); // 楓葉祝福
		    cm.sendNext("你已經成為最終極的勇者 #b暗影神偷#k");
		} else if (cm.getJob() == 433) {
		    if (cm.getQuestStatus(6934) != 2) {
		    	cm.gainItem(4031348, -1);
		    	cm.gainItem(4031517, -1);
		    	cm.gainItem(4031518, -1);
		    }
		    cm.changeJob(434);
		    cm.teachSkill(4340001,0,10); // 飛毒殺-改
		    cm.teachSkill(4341000,0,10); // 楓葉祝福		    		    
		    cm.teachSkill(4341002,0,30); // 絕殺刃 (無升級書)
		    cm.teachSkill(4341003,1,1);  // 怪物大爆炸 (無法升級只有1級的效果)
		    cm.teachSkill(4341004,0,10); // 穢土轉生	
		    cm.teachSkill(4341005,0,30); // 隱．鎖煉地獄 (無升級書)	
		    cm.teachSkill(4341006,0,10); // 幻影替身
		    cm.teachSkill(4341007,0,10); // 荊棘特效
		    cm.teachSkill(4341008,0,10); // 楓葉淨化
		    cm.sendNext("你已經成為最終極的勇者 #b影武者#k");
		}
	    } else {
		cm.sendOk("你的消耗欄沒有多餘的空間 !");
		cm.safeDispose();
		return;
	    }
	}
    } else if (status == 2) {
	cm.sendNextPrev("透過擊敗#rBOSS#k後取得的技能書，有機會使你的技能得到進階 !");
    } else if (status == 3) {
	cm.dispose();
    }
}