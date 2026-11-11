var itemid = new Array(4031036,4031037,4031038,4031711);
var mapid = new Array(103000900,103000903,103000906,600010004);
var menu;
var status;
var sw;

function start() {
    status = 0;
    sw = cm.getEventManager("Subway");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status ==1) {
	cm.dispose();
    } else {
	if (mode == 0) {
	    cm.sendNext("列車馬上就要出發了請儘快回來搭乘");
	    cm.dispose();
	    return;
	}
	if (mode == 1)
	    status++;
	if (status == 1) {
	    menu = "這是票證閱讀器。你會立刻被帶進來。想要使用哪張票？\r\n";
	    for (i=0; i < itemid.length; i++) {
		if(cm.haveItem(itemid[i])) {
		    menu += "#L"+i+"##b#m"+mapid[i]+"##k#l\r\n";
		}
	    }
		menu += "#L" + (itemid.length) + "##b地鐵 1#k#l\r\n#L" + (itemid.length + 1) + "##b#l\r\n";
	    cm.sendSimple(menu);
	} if (status == 2) {
	    section = selection;
	    if(section < (itemid.length - 1)) {
		cm.gainItem(itemid[selection],-1);
		cm.warp(mapid[selection]);
		cm.dispose();
	    }
	    else if (section == (itemid.length - 1)){
		if(sw == null) {
		    cm.sendNext("事件錯誤，請重啟服務器以獲得解決方案");
		    cm.dispose();
		} else if(sw.getProperty("entry").equals("true")) {
		    cm.sendYesNo("這班列車已經到站，請準備好你的票，馬上就要出發了。你想乘坐這班列車嗎？");
		} else if(sw.getProperty("entry").equals("false") && sw.getProperty("docked").equals("true")) {
		cm.sendNext("地鐵已經出發了。抱歉，你必須等下一班列車。");
		    cm.dispose();
		} else {
		cm.sendNext("我們將在開車前1分鐘開始登車。請耐心等待幾分鐘。請注意，地鐵將準時出發，我們會在1分鐘前停止接收車票，所以請務必準時到達。");
		    cm.dispose();
		}
	    } else {
		if (section == itemid.length) { //subway line 1
			cm.warp(103000101, 0);
		} else if (section == (itemid.length + 1)) { //kerning square
			cm.warp(103000310, 0);
		}
		cm.dispose();
	}
	} if (status == 3) {
	    cm.gainItem(itemid[section],-1);
	    cm.warp(mapid[section]);
	    cm.dispose();
	}
    }
}