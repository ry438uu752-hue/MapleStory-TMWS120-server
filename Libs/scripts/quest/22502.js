/*
	Description: 	Quest - A Bite of Hay
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("不知道能不能餵它吃草");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.askAcceptDecline("或許蜥蜴也像牛一樣能吃#t4032452#？週邊有很多乾草拿去餵他吧，不吃再找其他食物不就行了。");
    } else if (status == 1) {
	qm.forceStartQuest();
	qm.evanTutorial("UI/tutorial/evan/12/0", 1);
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
	qm.sendNext("餓餓！找到好吃的嗎，主人？嗯…這個怎麼看也只像是一種草。真的可以吃對吧？唔…是主人說的話，那麼相信一次看看？");
    } else if (status == 1) {
	qm.sendOk("來，啊啊~");
    } else if (status == 2) {
	qm.gainExp(800);
	qm.gainItem(4032452, -3);
	qm.sendOk("…唔唔唔唔~這是什麼？又苦又酸又韌~真的可以吃嗎？主人你也吃吃看！沒辦法吃~我不要吃這種！重新找來食物吧！");
	qm.forceCompleteQuest();
	qm.dispose();
    }
}