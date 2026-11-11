/*
	Description: 	Quest - A Bite of Pork
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("你怎麼能讓我餓死。我只是一個孩子。這是不對的 !");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("不不不。這不是我所需要的。我需要更多的東西有營養，主人 !");
    } else if (status == 1) {
	qm.sendNextPrevS("#b嗯......所以你不是草食動物。你可能是一個肉食動物。畢竟你是龍，怎麼才能拿到一些豬肉呢...#k", 2);
    } else if (status == 2) {
	qm.askAcceptDecline("什麼是...豬肉？從來沒有聽說過，但如果它是美味的，我可以接受 !");
    } else if (status == 3) {
	qm.forceStartQuest();
	qm.sendOkS("#b( 試著給寶貝龍一些豬肉。你必須在農場打獵幾頭豬。數量應該十分充足...)#k", 2);
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
	qm.sendOk("呵呵，這就是你給我吃？因此，這是你在談論的豬肉？ 讓我嘗試.");
    } else if (status == 1) {
	qm.gainExp(1850);
	qm.gainItem(4032453, -10);
	qm.sendNext("(喀喀，一口吃光...)");
	qm.forceCompleteQuest();
    } else if (status == 2) {
	qm.sendPrev("呃......這不好吃太糟糕了，但我不認為我能消化它。這不適合我...");
	qm.dispose();
    }
}
