var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendNext("你有什麼猶豫不決的？ 你是個英雄！ 鐵炙熱的時候你要罷工！ 來吧，讓我們這樣做吧!");
            qm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("好吧，我現在已經做了足夠的解釋。 讓我們進入下一階段。 你問，下一階段是什麼？ 我剛跟你說了。 盡可能努力訓練，直到你變得強壯到足以擊敗黑魔法師。");
    } else if (status == 1) {
        qm.sendNextPrev("你可能在過去曾是個英雄，但那是幾百年前的事了。 即使它不是黑魔法師的詛咒，所以那些你及時凍結的歲月已經使你的身體僵硬。 你必須放鬆一點，慢慢恢復敏捷的身手。 你問，你是怎麼做到的？");
    } else if (status == 2) {
        qm.askAcceptDecline("難道你不知道你必須先掌握基礎知識嗎？ 所以明智的做法是從瞭解#b黑魔法師#k開始。 哦，當然，我忘了你失去了記憶。 嗯，這就是我在這裡的原因。 你只需要親自體驗一下。 要開始了嗎？");
    } else if (status == 3) {
        qm.forceStartQuest();
        qm.AranTutInstructionalBubble("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
        qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
