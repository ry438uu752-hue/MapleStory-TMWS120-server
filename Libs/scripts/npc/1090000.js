/* Grendel the Really Old
	Magician Job Advancement
	Victoria Road : Magic Library (101000003)

	Custom Quest 100006, 100008, 100100, 100101
*/

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 2) {
	cm.sendOk("成為海盜是你正確的選擇"); // 非本職
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
		cm.sendNext("你想成為一位 #r海盜#k 嗎 ?");
	    } else {
		cm.sendOk("等級達到 #r10 #k就有資格成為海盜")
		cm.dispose();
	    }
	} else {
	    if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() == 500) { // 海盜
		if (cm.getQuestStatus(100006) >= 1) {
		    cm.completeQuest(100008);
		    if (cm.getQuestStatus(100008) == 2) {
			status = 20;
			cm.sendNext("我看到你做得很好，我將允許你在漫長的道路上邁出下一步。");
		    } else {
			if (!cm.haveItem(4031607)) {
			    cm.gainItem(4031607, 1);
			}
			cm.sendOk("你還沒去找#r萬人迷#k嗎？")
			cm.dispose();
		    }
		} else {
		    status = 10;
		    cm.sendNext("你的進步真是神速 !");
		}
	    } else if (cm.getQuestStatus(100100) == 1) {
		cm.completeQuest(100101);
		if (cm.getQuestStatus(100101) == 2) {
		    cm.sendOk("嘿，#r#h0##k，再次拜訪#r佩特勞#k，他會告訴你#b力量項鍊#k的下落。");
		} else {
		    cm.sendOk("第三次轉職不是#r佩特勞#k的職責嗎 ? 片刻後我再回覆你...");
		    cm.startQuest(100101);
		}
		cm.dispose();
	    } else {
		cm.sendOk("成為海盜是你正確的選擇"); // 本職
		cm.dispose();
	    }
	}
    } else if (status == 1) {
	cm.sendNextPrev("這是一個重要的決定, 一但選擇了就無法再改變。");
    } else if (status == 2) {
	cm.sendYesNo("你確定要成為一位 #b海盜#k ?");
    } else if (status == 3) {
	if (cm.getJob() == 0) {
	    cm.resetStats(4, 20, 4, 4);
	    cm.expandInventory(1, 4);
	    cm.expandInventory(4, 4);
	    cm.changeJob(500); // 海盜
	}
	    cm.gainItem(1482000, 1);
	    cm.gainItem(1492000, 1);
	    cm.gainItem(2330000, 500);
	    cm.gainItem(2330000, 500);
	    cm.gainItem(2330000, 500);
	cm.sendOk("你現在已經是一位 #b海盜#k 了");
	cm.dispose();
    } else if (status == 11) {
	cm.sendNextPrev("想必你已經準備好轉職成為 #r打手#k 或 #r槍手#k");
    } else if (status == 12) {
	cm.askAcceptDecline("但在這之前，我仍要考驗你是否有這個資格，你準備好要接受測試了嗎 ?");
    } else if (status == 13) {
	cm.gainItem(4031607, 1);
	cm.startQuest(100006);
	cm.sendOk("你帶著我的信件到鯨魚號的#b訓練場#k，去找#r萬人迷#k，她會告訴你該怎麼做");
	cm.dispose();
    } else if (status == 21) {
	cm.sendSimple("選擇你要二轉的職業 #b\r\n#L0#打手#l\r\n#L1#槍手#l#k");
    } else if (status == 22) {
	var jobName;
	if (selection == 0) {
	    jobName = "打手";
	    job = 510;
	} else if (selection == 1) {
	    jobName = "槍手";
	    job = 520;
	}
	cm.sendYesNo("你想要成為一位 #r" + jobName + "#k?");
    } else if (status == 23) {
	cm.changeJob(job);
	cm.gainItem(4031012, -1);
	cm.sendOk("你將繼續強大，邁入終極的境界！");
	cm.dispose();
    }
}	
