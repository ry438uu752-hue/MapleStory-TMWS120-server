var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("你準備好去#o0100132#s了嗎？ 當且僅當您完全準備好時，始終繼續。 沒有充分準備就沒有比參與戰鬥更糟的了。");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.askAcceptDecline("我們可以開始你的基礎訓練了嗎？ 在這之前，請準備好你的劍，並且熟練您的技能和備妥您的藥水。");
    } else if (status == 1) {
	qm.forceStartQuest();
	qm.AranTutInstructionalBubble("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
