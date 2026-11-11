
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 3) {
	    qm.sendNext("*喘氣*你怎麼能拒絕養活你的龍？這是虐待動物 !");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("呦，我已經向你展示了我的力量，輪到你了。找些我能吃的食物！ 我餓了，您可以使用我的力量，但相對的回報你要照顧我.");
    } else if (status == 1) {
	qm.forceStartQuest();
	qm.sendNextPrevS("嗯，我還是不明白這是怎麼回事，但我不能讓一個可憐的小動物餓死吧？你餓了？ 你想吃什麼呢 ?", 2);
    } else if (status == 2) {
	qm.sendNextPrev("唉...我才剛出生。我怎會知道我是吃什麼的？我所知道的是，我是龍......我是你的龍。而你是我的主人。你要對我好!");
    } else if (status == 3) {
	qm.askAcceptDecline("我想我們應該共同學習。只是，我餓了。主人，我要食物。請記住，我還是嬰兒！沒餵食我可能會哭！");
    } else if (status == 4) {
	qm.forceStartQuest();
	qm.sendOkS("#b(米爾龍出現在是寶寶餓極了。你需要喂他吃點東西。或許你爸爸會知道該喂什麼給你的龍吃。)", 2);
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("這是什麼，即使是？你想知道龍吃什麼？你為什麼......咦？你發現了一隻龍?");
    } else if (status == 1) {
	qm.sendNextS("#b(爸爸，告訴你一個秘密.)#k", 2);
    } else if (status == 2) {
	qm.sendNextPrev("嗯......這是一隻龍？你確定它不只是一隻大蜥蜴？好了，所有的生命是寶貴的，所以我想你可以把它...");
    } else if (status == 3) {
	qm.sendNextS("#b(爸爸似乎並不相信先生是一 隻龍。嗯，他是非常小的。將爸爸相信，如果他聽到米爾談話?)", 2);
    } else if (status == 4) {
	qm.sendNextPrev("如果它是一隻真正的龍，如果它會吐火，那就太危險了。我真的不認為這是一隻龍，但也許我們應該問問冒險家是否要消滅它。");
    } else if (status == 5) {
	qm.sendNextS("#b(什麼？！殺了米爾？但他沒有做錯什麼啊！!!)", 2);
    } else if (status == 6) {
	qm.sendNextPrev("當然，我敢肯定它不是一隻龍。");
    } else if (status == 7) {
	qm.sendNextS("#b哈...哈...你是絕對正確的！我懷疑他是一隻龍。他可能只是一隻蜥蜴！無疑!#k", 2);
    } else if (status == 8) {
	qm.sendNextPrev("是啊，我敢肯定。這是一隻奇怪的稀有蜥蜴，但它看起來並不危險。猜猜你可以把它...");
    } else if (status == 9) {
	qm.sendNextS("#b(對於他自己的安全，你最好不要讓任何人知道，米拉是一隻龍.)#k", 2);
    } else if (status == 10) {
	qm.sendOk("哦，你說你要找些東西喂蜥蜴？我不知道...讓我想想。");
    } else if (status == 11) {
	qm.gainExp(180);
	qm.forceCompleteQuest();
	qm.dispose();
    }
}