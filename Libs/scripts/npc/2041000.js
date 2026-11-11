/* Author: Xterminator
	NPC Name: 		Tian
	Map(s): 		Ludibrium: Station<Orbis> (220000110)
	Description: 		Ludibrium Ticketing Usher
*/
var status = 0;

function start() {
    status = -1;
    train = cm.getEventManager("Trains");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if(mode == 0) {
	cm.sendNext("You must have some business to take care of here, right?");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if(train == null) {
	    cm.sendNext("發生錯誤，請輸入@EA解卡或重新開啟伺服器。");
	    cm.dispose();
	} else if(train.getProperty("entry").equals("true")) {
	    cm.sendYesNo("飛船已進站，想搭乘的旅客請盡快登船。");
	} else if(train.getProperty("entry").equals("false") && train.getProperty("docked").equals("true")) {
	    cm.sendNext("抱歉，飛船已經離開，請耐心等候改搭下一班，並且注意時間，飛船準時起飛。");
	    cm.dispose();
	} else {
	    cm.sendNext("我們將在起飛前1分鐘開始登船。請耐心等待幾分鐘。請注意，飛船隨時會進站，在出發前1分鐘我們將停止收票。");
	    cm.dispose();
	}
    } else if(status == 1) {
	if(!cm.haveItem(4031045)) {
	    cm.sendNext("Oh no ... I don't think you have the ticket with you. I can't let you in without it. Please buy the ticket at the ticketing booth.");
	} else {
	    cm.gainItem(4031045, -1);
	    cm.warp(220000111, 0);
	}
	cm.dispose();
    }
}