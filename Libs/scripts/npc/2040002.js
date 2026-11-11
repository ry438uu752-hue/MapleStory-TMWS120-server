var dh;
var entry = true;

function start() {
    dh = cm.getEventManager("DollHouse");
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == 0 && status == 0) {
	cm.sendNext("I see. It's very understandable, considering the fact that you'll be facing a very dangerous monster inside. If you ever feel a change of heart, then please come talk to me. I sure can use help from someone like you.");
	cm.dispose();
	return;
    } else if(mode == 0 && status == 2) {
	cm.sendNext("I see. Please talk to me when you're ready to take on this task. I advise you not to take too much time, through, for the monster may turn into something totally different. We have to act like we don't know anything.");
	cm.dispose();
	return;
    }
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if(cm.getQuestStatus(3230) == 1) {
	if(status == 0) {
	    cm.sendYesNo("嗯......我通過 #b#p2040001##k 聽到了很多關於你的事。 你給了他一堆 #b#t4031093##k 讓他可以在工作中擺脫無聊。想請你幫我找個東西，就在我身旁的這個房間裡，因為我無法離開工作崗位，你能幫幫我嗎？");
	} else if(status == 1) {
	    cm.sendNext("非常的感謝您。 事實上，#b#p2040001##k 會請你收集 #b#t4031093##k 是在測試你能力的一種方法，看看你是否能夠處理諸如此類的問題，所以請你不要介意。 我認為像你這樣的人，在面對任何逆境的時候都能迎刃而解。");
	} else if(status == 2) {
	    cm.sendYesNo("不久之前，由於玩具城空間的裂縫，一個怪物從另一個維度來到這裡，它偷走了時鐘的鐘擺。 並且隱藏在房間裡，偽裝成一個玩具屋。 在我看來它們每個都長的一樣，所以我無法分辨到底哪一個才是真的。 你能幫我們找到它嗎？");
	    if (dh != null && dh.getProperty("noEntry").equals("true")) {
		entry = false;
	    }
	} else if(status == 3) {
	    cm.sendNext("好的！ 入口就在我右手邊的這扇門，你在那裡會看到許多長的相同的玩具屋。 當中的一個與其他略有不同。 找到它之後使用普攻破壞就能發現鐘擺。 但是，如果你打破了錯誤的玩具屋，你將毫無預警的被送回到這裡，所以請特別注意這一點。");
	} else if(status == 4) {
	    cm.sendNextPrev("你也會在那裡找到怪物，它們已經變得非常強大，這全都是因為它們來自維度空間的關係，你無法將它們擊倒，所以請不要白費工夫。 找到 #b#t4031094##k 後，通知裡面的 #b#p2040028##k ，就可以了！");
	} else if(status == 5) {
	    if(dh == null || entry == false) {
		cm.sendPrev("由我右手邊的這扇門進入玩偶之家，祝你好運 ! ");
	    } else {
		cm.removeAll(4031093);
		dh.startInstance(cm.getChar());
	    }
	    cm.dispose();
	}
    } else if(cm.getQuestStatus(3230) == 2) {
	cm.sendNext("#r#h ##k 謝謝你，我們已經拿到 #b#t4031094##k 並且讓玩具鐘塔又再一次正常的運作了，希望你能在 #b#m220000000##k 多待幾天!");
	cm.dispose();
    } else {
	cm.sendOk("我們是守衛這個房間的玩具士兵，禁止閒雜人等進入。");
	cm.dispose();
    }
}