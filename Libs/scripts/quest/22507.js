
 var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 14) {
	    qm.sendNext("呃，你在跟我開玩笑吧？告訴我你的手指滑落！來吧，接受任務.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("我就知道我們是相連的，主人！當你變得強壯，我也會變得更強。這時你便可以使用我的力量！這是我們的契約條件。我選對了一個好主人 !");
    } else if (status == 1) {
	qm.sendNextPrevS("#b原來是這麼一回事。我們是在何時簽訂的契約 ?#k", 2);
    } else if (status == 2) {
	qm.sendNextPrev("我不知道。我當時只是一顆蛋。我真的不記得......雖然我依稀記得你，主人，我看到在一個有霧的森林中你朝我走來。");
    } else if (status == 3) {
	qm.sendNextPrevS("#b(等等 ! 這聽起來就像是在做夢......難道我們兩個在夢中見面了？有沒有可能是我在夢中見到的那頭巨龍是......米爾？#k", 2);
    } else if (status == 4) {
	qm.sendNextPrev("主人，你和我的精神是一體的，從我看到你的那一刻起我就認定你了。這就是為什麼我想跟你簽訂契約。");
    } else if (status == 5) {
	qm.sendNextPrevS("#b跟你簽訂了契約 ?#k", 2);
    } else if (status == 6) {
	qm.sendNextPrev("難道你不記得了嗎？你我本就是一體，當你認出了我，在你觸碰蛋的那一刻起，你和我的精神就成為了共同體.");
    } else if (status == 7) {
	qm.sendNextPrevS("#b精神共同體... ?#k", 2);
    } else if (status == 8) {
	qm.sendNextPrev("是的 !是精神契約！你和我各有獨自的個體，但我們共享一個精神。這就是為什麼你變得更強壯，我也會變得更強，反之亦然！很棒對吧？至少，我是這麼認為的.");
    } else if (status == 9) {
	qm.sendNextPrevS("#b雖然我不知道你在說什麼，但它聽起來像是一個相當大的交易。#k", 2);
    } else if (status == 10) {
	qm.sendNextPrev("當然，這是一個大問題，傻傻的主人！你再也不必擔心任何怪物。現在有我保護你！來吧，現在就來測試一下我的能力 !");
    } else if (status == 11) {
	qm.sendNextPrevS("#b這裡相當安全，周圍並沒有危險的怪物.#k", 2);
    } else if (status == 12) {
	qm.sendNextPrev("什麼，你說這不好玩 ? 你不喜歡冒險 ? 主人 ! 難道你忘了你與強大的怪物搏鬥，並擊敗邪惡的存在，和拯救無數無辜的人，這些事難道你都忘了 ?");
    } else if (status == 13) {
	qm.sendNextPrevS("#b你是在開玩笑吧，我只是一個農民的兒子...#k", 2);
    } else if (status == 14) {
	qm.askAcceptDecline("主人，龍魔導士是無法過著平靜的生活的。將來還有很多的機會來證明我的技能。相信我，我們將會一起迎接更大的冒險。答應我，讓我們共同努力，好嗎?");
    } else if (status == 15) {
	qm.forceStartQuest();
	qm.sendNextS("呵...，好吧，讓我們開始吧 !", 1);
    } else if (status == 16) {
	qm.sendNextPrevS("#b(雖然你感到有些困惑，但或許跟寶貝龍一同去冒險，能改變我現在乏味的生活，就像他說的那樣。)#k", 3);
    } else if (status == 17) {
	qm.sendPrevS("#b(還有一件事，你需要和你爸爸談談，去看看他吧。)#k", 2);
	qm.dispose();
    }
}

function end(mode, type, selection) {
}