var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendOk("咦？你害怕豬？他們跳來跳去像瘋了似的，但你不應該害怕他們...");
            qm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.askAcceptDecline("忘掉狡猾的狐狸。既然你在這里，想再次幫助我嗎？我想平息豬的唯一途徑是通過管教他們。你為什麽不走花幾分鐘的護理#r豬#k?");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("瘋狂的豬，可以發現在#b巨大的路徑。#k. 頭以上，並采取只關心#r20#k 它們的。嘿，老兄，你真的幫了我一個大忙");
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
        qm.sendOk("哦，你的紀律豬。 做得好！ 謝謝.\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2022621# Declicious Milk 30\r\n#i2022622#Declicious Juice 30\r\n#fUI/UIWindow.img/QuestIcon/8/0#980 exp");
    } else if (status == 1) {
        qm.gainExp(980);
        qm.gainItem(2022621, 30);
        qm.gainItem(2022622, 30);
        qm.sendOk("我該回去工作了");
        qm.forceCompleteQuest();
        qm.dispose();
    }
}
