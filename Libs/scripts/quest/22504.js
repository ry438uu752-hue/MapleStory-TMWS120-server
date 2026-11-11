/*
	Description: 	Quest - Tasty Milk 1
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("比主人#b年齡大，經歷世界比你多一點的人#k。沒有那樣的人嗎？#k");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("我想，應該有更適合我的食物，不是乾草也不是豬肉。什麼 ! 你不知道 ? 你比我年長，這些事你應該比我懂的，主人 !");
    } else if (status == 1) {
	qm.sendNextPrevS("#但我是真的不知道你能吃什麼，這跟年紀沒有關係...", 2);
    } else if (status == 2) {
	qm.askAcceptDecline("你比我年長，自然知道的事就會比我多，不是嗎 ?  或許你也可以去請教一些比你年長的人，相信他們的經驗比你多，主人 !");
    } else if (status == 3) {
	qm.forceStartQuest();
	qm.sendOkS("#b(你已經問過老爸一次了，但你沒有任何更好的想法。找時間再請教他 !)#k", 2);
	qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendOk("嗯？還沒找到蜥蜴吃什麼嗎？#t4032452#也不吃#t4032453#也不吃？真是挑剔啊。他有沒有獨特的特徵啊？嗯？那蜥蜴還是嬰兒？");
    } else if (status == 1) {
	qm.gainExp(260);
	qm.forceCompleteQuest();
	qm.dispose();
    }
}