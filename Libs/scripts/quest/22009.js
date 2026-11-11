var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendOk("什麼？ 想想這個！ 如果農場失敗，我們要生存下去！ 嗯？ 再次與我說話，然後按ACCEPT!");
            qm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.askAcceptDecline("如果狐狸的數量在農場附近增加，就像它在我們的房子附近，這將與爸爸的農場工作接口。 我們應該調查這個。 你不同意?");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("轉到 #b中央農場#k 並問 #b爸#k 關於情況。 如果狡猾狐狸的數量在那裡增加，我們將不得不進行一個大型狡猾狐狸狩獵.");
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
        qm.sendOk("這是什麼，#r#h ##k？我敢肯定你是不是在這裡用愛來提供另一種便當，我太忙了，不跟你玩了...什麼？這裡有增加北極狐的數量?");
    } else if (status == 1) {
        qm.sendNext("#b豬#k一直在瘋狂，跳過所有的地方。 甚至狐狸似乎逃離了 豬群，#b豬#k一直在瘋狂，跳過所有的地方。");
        qm.gainExp(260);
        qm.forceCompleteQuest();
    } else if (status == 2) {
        qm.sendPrev("啊，也許這就是為什麼狡猾狐狸人口附近的房子增加了。 他們跑到那裡逃離豬群， 嗯...");
        qm.dispose();
    }
}
 