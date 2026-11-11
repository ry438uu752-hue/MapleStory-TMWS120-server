var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("不能嗎，真希望你能幫幫我...");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("在過去的幾天裡，農場裡的#b#o1210100##k一直表現得很奇怪。他們無緣無故地生氣和煩躁。我很擔心，所以今天一大早我來到農場，果然，其中一些#b#o1210100##k似乎越過了柵欄。");
    else if (status == 1)
        qm.sendAcceptDecline("在我去尋找#b#o1210100##k之前，我應該修補破損的柵欄。幸運的是，它並沒有被嚴重損壞。我只需要一些#b#t4032498##k來修復它。#r#h ##k，你能給我帶來#b3#k根#b#t4032498##k嗎?");
    else if (status == 2) {
        if (!qm.canHold(4032498, 3)) {
            qm.sendOk("你的背包空間不足");
            qm.dispose();
        }
        qm.gainItem(4032498, 0);
        qm.forceStartQuest();
        qm.sendNext("哦，太感謝你了，你可以從附近的 #b#o0130100##k 找到 #r#t4032498##k. 它們並沒有很強，相信你可以輕易的消滅它們。");
    } else if (status == 3) {
        qm.evanTutorial("UI/tutorial/evan/6/0", 1);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("哦，#t4032498#搜集到了嗎？真了不起。我應該給你什麼作為獎賞呢……對了，我有那個東西。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i3010097# 1 #t3010097# \r\n#i2022621# 15 #t2022621#s \r\n#i2022622# 15 #t2022622#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 210 exp");
    if (status == 1) {
        qm.forceCompleteQuest();
        qm.gainItem(4032498, -3);
        qm.gainItem(3010097, 1);
        qm.gainItem(2022621, 15);
        qm.gainItem(2022622, 15);
        qm.gainExp(210);
        qm.sendNextPrev("好了，我用修理籬笆剩下的木條做了一把椅子。雖然不太好看，但卻很結實。就給你用吧。");
    }
    if (status == 2) {
        qm.evanTutorial("UI/tutorial/evan/7/0", 1);
        qm.dispose();
    }
}