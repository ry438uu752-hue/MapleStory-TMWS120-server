var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if ((status == 0 && mode == 0) || (status == 5 && mode == 1)) {
	cm.dispose();
	return;
    } else if (status <= 2 && mode == 0) {
	cm.sendNext("您必須要處理一些事務。您必須在旅途和狩獵中感到疲倦。 Go take\r\nsome rest, and if you feel like changing your mind, then\r\ncome talk to me.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendSimple("您是否聽說過離這裡有點遠的名為 #b黃金海岸#k, 的海洋美景？ 我現在可以帶您去只要您支付 #b1200 楓幣#k, 或者如果您有 #b#t4031134##k 的票，將可完全免費。\r\n\r\n#L0##b 支付 1200 楓幣.#l\r\n#L1# 使用 #t4031134##l\r\n#L2# 什麼是 #t4031134##k?#l");
    } else if (status == 1) {
	if (selection == 0) {
	    cm.sendYesNo("您想支付#b 1200 楓幣嗎#k嗎？好的！您可以隨時隨地前往黃金海灘。 但是請注意，您可能會遇到附近的一些怪物，因此請做好戰鬥的準備。那麼，您現在想去黃金海灘嗎？");
	} else if (selection == 1) {
	    status = 2;
	    cm.sendYesNo("那麼，您有#b#t4031134##k嗎？您可以隨時隨地前往黃金海灘。好的！ 但是請注意，您可能會遇到附近的一些怪物，因此請做好隨時戰鬥準備。好的，您現在想去黃金海灘嗎？");
	} else if (selection == 2) {
	    status = 4;
	    cm.sendNext("You must be curious about a #bVIP Ticket to Florina Beach#k. Yeah, I can see that. A VIP Ticket to Florina Beach is an item where as long as you have in possession, you may make your way to Florina Beach for free. It's such a rare item that even we had to buy those, but unfortunately I lost mine a few weeks ago during a long weekend.");
	}
    } else if (status == 2) {
	if (cm.getMeso() < 1200) {
	    cm.sendNext("您的楓幣不足 !");
	    cm.dispose();
	} else {
	    cm.gainMeso(-1200);
	    cm.saveLocation("FLORINA");
	    cm.warp(110000000, 0);
	    cm.dispose();
	}
    } else if (status == 3) {
	if (cm.haveItem(4031134)) {
	    cm.saveLocation("FLORINA");
	    cm.warp(110000000, 0);
	    cm.dispose();
	} else {
	    cm.sendNext("你沒有#b#t4031134##k  !");
	    cm.dispose();
	}
    } else if (status == 4) {
	cm.sendNext("#b#t4031134##k是在商城販售的傳送道具。");
    } else if (status == 5) {
	cm.sendPrev("只要按下快速鍵 B 即可進入進入商城");
    }
}