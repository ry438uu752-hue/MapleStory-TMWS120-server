/* Bowman Job Instructor
	Hunter Job Advancement
	Warning Street : The Road to the Dungeon (106010000)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	if (cm.getQuestStatus(100001) == 1) {
	    status = 3;
	} else {
	    if (cm.getQuestStatus(100001) == 2) {
		cm.sendOk("成為弓箭手是你正確的選擇");
		cm.safeDispose();
	    } else if (cm.getQuestStatus(100000) >= 1) {
		cm.completeQuest(100000);
		if (cm.getQuestStatus(100000) == 2) {
		    cm.sendNext("哦，這不就是#r赫麗娜#k的親筆信函嗎？");
		}
	    } else {
		cm.sendOk("等你準備好了，我再告訴你下一步該怎麼做。");
		cm.safeDispose();
	    }
	}
    } else if (status == 1) {
	cm.sendNextPrev("所以你想證明你的實力？很好...")
    } else if (status == 2) {
	cm.askAcceptDecline("如果你準備好了，我會給你一個機會。");
    } else if (status == 3) {
	cm.startQuest(100001);
	cm.sendOk("你必須收集 #r30#k個#b#t4031013##k後交給我，才能算是通過測驗，祝你好運 #r！#k")
    } else if (status == 4) {
	cm.warp(108000100, 0);
	cm.dispose();
    }
}	