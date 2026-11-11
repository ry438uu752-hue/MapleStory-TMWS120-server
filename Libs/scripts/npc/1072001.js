/* Magician Job Instructor
	Magician 2nd Job Advancement
	Victoria Road : The Forest North of Ellinia (101020000)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0 && cm.getQuestStatus(100007) == 1) {
	status = 3;
    }
    if (status == 0) {
	if (cm.getQuestStatus(100007) == 2) {
	    cm.sendOk("成為法師是你正確的選擇");
	    cm.safeDispose();
	} else if (cm.getQuestStatus(100006) >= 1) {
	    cm.completeQuest(100006);
	    if (cm.getQuestStatus(100006) == 2) {
		cm.sendNext("哦，這不就是#r漢斯#k的親筆信函嗎？");
	    }
	} else {
	    cm.sendOk("等你準備好了，我再告訴你下一步該怎麼做。");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	cm.sendNextPrev("所以你想證明你的實力？很好...")
    } else if (status == 2) {
	cm.askAcceptDecline("如果你準備好了，我會給你一個機會。");
    } else if (status == 3) {
	cm.startQuest(100007);
	cm.sendOk("你必須收集 #r30#k個#b#t4031013##k後交給我，才能算是通過測驗，祝你好運 #r！#k")
    } else if (status == 4) {
	cm.warp(108000200, 0);
	cm.dispose();
    }
}	