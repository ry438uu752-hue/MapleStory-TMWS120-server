/**
-- Odin JavaScript --------------------------------------------------------------------------------
	Mark the Toy Soldier - Doll's House(922000010)
-- By ---------------------------------------------------------------------------------------------
	Information
-- Version Info -----------------------------------------------------------------------------------
	1.0 - First Version by Information
---------------------------------------------------------------------------------------------------
**/

var havePendulum = false;
var complete = false;
var inQuest = false;

function start() {
    if(cm.getQuestStatus(3230) == 1) {
	inQuest = true;
    } else {
	inQuest = false;
    }
    dh = cm.getEventManager("DollHouse");
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == 0 && status == 0) {
	cm.dispose();
	return;
    } else if(mode == 0 && status == 1) {
	cm.sendNext("I knew you'd stay. It's important that you finish what you've started! Now please go locate the different-looking dollhouse, break it, and bring #b#t4031094##k to me!");
	cm.dispose();
	return;
    }
    if(mode == 1) {
	status++;
    } else {
	status--;
    }
    if(inQuest == true) {
	if(status == 0) {
	    if(cm.haveItem(4031094)) {
		cm.sendNext("哦，哇，你確實找到了看起來不同的娃娃屋，找到了#b#t4031094##k! 那太不可思議!! 有了這個，玩具鐘樓將能再次運行！ 感謝您的幫忙，這是您的努力的一點獎勵。 在此之前，請確認您的背包是否有足夠的空間。");
		havePendulum = true;
	    } else {
		cm.sendSimple("你好。 我是#b#p2040028##k，負責保護這個房間。 在裡面，你會看到一堆玩具屋，你可能會發現一個看起來與其他玩家不同的玩具屋。 你的工作是找到它，打破它的門，找到#b#t4031094##k，這是Ludibrium Clocktower的一個組成部分。 你有一個時間限制，如果你打破了錯誤的玩具屋，你將被迫回到外面，所以請小心。\r\n#L0##b我想離開這裡。");
	    }
	} else if(status == 1) {
	    if(havePendulum == true) {
		if(!cm.canHold(2000010)) {
		    cm.sendNext("你的背包空間不足");
		}
		cm.sendNextPrev("我給你的 #b100 #t2000010#s#k 你還滿意嗎？ 非常感謝你幫助我們。 由於你的鼎力相助，鐘樓將再次運行，而來自其他維度的怪物似乎也已經消失了。 我這就送你出去。");
		if(complete == false) {
		    cm.completeQuest(3230);
		    cm.gainExp(2400);
		    cm.gainItem(4031094, -1);
		    cm.gainItem(2000010, 100);
		    complete = true;
		}
	    } else {
		cm.sendYesNo("你確定你要離開了嗎? 好的，我將送你回到原先的地圖。");
	    }
	} else if(status == 2) {
	    cm.getPlayer().getEventInstance().removePlayer(cm.getChar());
	    cm.dispose();
	}
    } else {
	if(status == 0) {
	    cm.sendNext("再次感謝你的幫忙，我將送你回到原先的地圖。");
	} else if(status == 1) {
	    cm.warp(221024400, 4);
	    cm.dispose();
	}
    }
}