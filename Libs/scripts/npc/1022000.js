/* Dances with Balrog
	Warrior Job Advancement
	Victoria Road : Warriors' Sanctuary (102000003)

	Custom Quest 100003, 100005
*/

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 2) {
	cm.sendOk("成為劍士是你正確的選擇"); // 非本職
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	if (cm.getJob() == 0) {
	    if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() == 0) {
		cm.sendNext("你想成為一位 #r劍士#k 嗎 ?");
	    } else {
		cm.sendOk("等級達到# #r10 #k就有資格成為劍士");
		cm.dispose();
	    }
	} else {
	    if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() == 100) { // 劍士
		if (cm.getQuestStatus(100003) >= 1) {
		    cm.completeQuest(100005);
		    if (cm.getQuestStatus(100005) == 2) {
			status = 20;
			cm.sendNext("我看到你做得很好。 我將允許你在漫長的道路上邁出下一步。");
		    } else {
			if (!cm.haveItem(4031008)) {
			    cm.gainItem(4031008, 1);
			}
			cm.sendOk("你還沒去找 #r劍士轉職教官#k 嗎？")
			cm.dispose();
		    }
		} else {
		    status = 10;
		    cm.sendNext("你的進步真是神速 !");
		}
	    } else if (cm.getQuestStatus(100100) == 1) {
		cm.completeQuest(100101);
		if (cm.getQuestStatus(100101) == 2) {
		    cm.sendOk("嘿，#r#h0##k，再次拜訪#r泰勒斯#k，他會告訴你#b力量項鍊#k的下落。");
		} else {
		    cm.sendOk("第三次轉職不是#r泰勒斯#k的職責嗎 ? 片刻後我再回覆你...");
		    cm.startQuest(100101);
		}
		cm.dispose();
	    } else {
		cm.sendOk("成為劍士是你正確的選擇"); // 本職
		cm.dispose();
	    }
	}
    } else if (status == 1) {
	cm.sendNextPrev("這是一個重要的決定, 一但選擇了就無法再改變。");
    } else if (status == 2) {
	cm.sendYesNo("你確定要成為一位 #r劍士#k ?");
    } else if (status == 3) {
	if (cm.getJob() == 0) {
	    cm.resetStats(35, 4, 4, 4);
	    cm.expandInventory(1, 4);
	    cm.expandInventory(4, 4);
	    cm.changeJob(100); // 劍士
	}
	cm.gainItem(1402001, 1);
	cm.sendOk("你現在已經是一位 #b劍士#k 了");
	cm.dispose();
    } else if (status == 11) {
	cm.sendNextPrev("想必你已經準備好轉職成為 #r狂戰士#k , #r見習騎士#k 或 #r槍騎兵#k")
    } else if (status == 12) {
	cm.askAcceptDecline("但在這之前，我仍要考驗你是否有這個資格, 你準備好要接受測試了嗎 ?");
    } else if (status == 13) {
	cm.gainItem(4031008, 1);
	cm.startQuest(100003);
	cm.sendOk("你帶著我的信件到 #b西部岩山Ⅳ#k 去找 #r劍士轉職教官#k, 他會告訴你該怎麼做");
	cm.dispose();
    } else if (status == 21) {
	cm.sendYesNo("選擇你要二轉的職業 #b\r\n#L0#狂戰士#l\r\n#L1#見習騎士#l\r\n#L2#槍騎兵#l#k");
    } else if (status == 22) {
	var jobName;
	if (selection == 0) {
	    jobName = "狂戰士";
	    job = 110;
	} else if (selection == 1) {
	    jobName = "見習騎士";
	    job = 120; 
	} else if (selection == 2) {
	    jobName = "槍騎兵";
	    job = 130;
	}
	cm.sendYesNo("你想要成為一位 #r" + jobName + "#k?");
    } else if (status == 23) {
	cm.changeJob(job);
	cm.gainItem(4031012, -1);
	cm.sendOk("你將繼續強大, 邁入終極的境界！");
	cm.dispose();
    }
}	
