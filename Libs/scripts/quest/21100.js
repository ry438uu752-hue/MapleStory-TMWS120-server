var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 6) {
	    qm.sendNext("哦，還不夠嗎？ 如果您覺得需要進一步訓練，請隨意殺死更多。 如果你殺了他們所有人，即使它傷害了我的心，我也只能看看另一種方式，因為他們會為了一個好的理由而犧牲...");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNextS("這裡沒有太多記錄與黑魔法師作戰的英雄。 即使在預言書中，唯一可用的信息是其中有五個。 沒有關於他們是誰或他們看起來像什麼。 你還記得些什麼嗎？ 什麼都沒有嗎？", 8);
    } else if (status == 1) {
	qm.sendNextPrevS("我不記得了......", 2);
    } else if (status == 2) {
	qm.sendNextPrevS("正如我所料。 當然，黑魔法師的詛咒足以摧毀你所有的記憶。 但即使是這樣的話，也必須有一個過去會發現的點，特別是現在我們確定你是英雄之一。 我知道你在戰鬥中失去了盔甲和武器但是......哦，是的，是的。 我差點忘了！ 你的#b武器#k！", 8);
    } else if (status == 3) {
	qm.sendNextPrevS("我的武器?", 2);
    } else if (status == 4) {
	qm.sendNextPrevS("我一會兒挖了一塊冰塊，發現了一把令人難以置信的武器。 我認為這個武器屬於一個英雄，所以我把它帶到了城裡並把它放在鎮中心的某個地方。 你沒見過嗎？ #b#p1201001##k ... \r\n #i4032372# \r\n 它看起來像這樣......", 8);
    } else if (status == 5) {
	qm.sendNextPrevS("我想想，我確實有在城裡看到過#p1201001#。", 2);
    } else if (status == 6) {
	qm.askAcceptDecline("對，就是那樣。 根據記錄的內容，英雄的武器會認主人，如果你是使用#p1201001#的英雄，那#p1201001#會在你抓住#p1201001#時作出反應。 請找到#b#p1201001#並點擊它。#k");
    } else if (status == 7) {
	if (qm.getQuestStatus(21100) == 0) {
	    qm.forceCompleteQuest();
	}
	qm.sendOkS("如果#p1201001#對你做出反應，那麼我們就會知道你是#b狂狼勇士#k，是一個揮舞著#p1201001#的英雄。", 8);
	qm.showWZEffect("Effect/Direction1.img/aranTutorial/ClickPoleArm");
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
