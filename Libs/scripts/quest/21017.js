var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 5) {
	    qm.sendNextS("#b(你因害怕而退縮了，但這並不像你可以這樣逃跑。 大口氣，冷靜下來，再試一次。)#k", 2);
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNextS("你現在好像已經熱完身了。 這是嚴格的培訓可以真正幫助您建立堅實的基礎。 讓我們繼續進行基礎訓練，好嗎？", 8);
    } else if (status == 1) {
	qm.sendNextPrevS("這次要到 #b#m140020200 ##k去擊敗了一些#r#o0100133##k。 我想#r20#k個應該就夠了。 繼續前進吧......嗯？ 你有話要說嗎？", 8);
    } else if (status == 2) {
	qm.sendNextPrevS("怎麼狩獵的數量好像越來越大....", 2);
    } else if (status == 3) {
	qm.sendNextPrevS("什麼，你對20不滿意嗎？ 還是你想消滅100個？ 或者是說需要增加到999個，如何？ 陷入困境的人可以輕鬆地做到這一點。 畢竟，我們正在訓練......", 8);
    } else if (status == 4) {
	qm.sendNextPrevS("不，不，不。 我想20個就夠了", 2);
    } else if (status == 5) {
	qm.askAcceptDecline("你不必那麼謙虛。 我理解你渴望迅速成為曾經的英雄。 這種態度是讓你成為英雄的原因。");
    } else if (status == 6) {
	qm.forceStartQuest();
	qm.sendNextS('#b(還是快點答應她的要求...，如果再繼續講下去，恐怕是要增加到999個了。)#k', 2);
    } else if (status == 7) {
	qm.sendNextPrevS('請去消滅20隻 #r#o0100133##k.', 8);
    } else if (status == 8) {
	qm.AranTutInstructionalBubble("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
