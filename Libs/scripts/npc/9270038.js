/*
	Shalon - Ticketing Usher

*/

var cost = 20000;

var ap;

function start() {

    ap = cm.getEventManager("AirPlane");

    status = -1;
    action(1, 0, 0);

}


function action(mode, type, selection) {

    if(mode == 0 && status == 0) {

	cm.dispose();

	return;

    }
    if(mode == 1) {

	status++;

    }
    if(mode == 0 && menu == 0) {

	cm.sendNext("我會長時間在此為您服務。 當您改變主意時，請再次與我聯繫。");

	cm.dispose();

    }
    if(mode == 0 && menu == 1) {
	
cm.sendOk("請確認您要離開的出發時間。 謝謝。");

	cm.dispose();

    }
    if(status == 0) {

	cm.sendSimple("你好～我是新加坡機場的 #p"+cm.getNpc()+"# 我會幫助你立刻回到 #m103000000#。 請問需樣什麼服務呢？\r\n#L0##b我想買張去#m103000000##k#b的機票 \r\n#L1##b讓我進入登機門.#k#l");

    } else if(status == 1) {

	menu = selection;

	if(menu == 0) {

	cm.sendYesNo("機票需花費您20,000楓幣。 您確定要購買嗎？");

	} else if(menu == 1) {

        cm.sendYesNo("你想現在進入機場嗎？ 一旦進入，我們將回收您的機票 ！  ");

	}

    } else if(status == 2) {

	if(menu == 0) {

	   if(!cm.canHold(4031732) || cm.getMeso() < cost) {

	cm.sendOk("看來這個班次有足夠的空位。 請準備好您的機票，這樣我就可以讓您進去，搭乘的時間不會很長，很快就能到達目的地了。 您想登機了嗎？");

    } else {

	cm.gainMeso(-cost);

	cm.gainItem(4031732, 1);

    }
	cm.dispose();

    } else if(menu == 1) {

	   if(!cm.haveItem(4031732)) {

	cm.sendNext("請先購買機票。 謝謝～");

    } else if(ap.getProperty("entry").equals("true")) {

	cm.sendNext("很抱歉，登機門在出發前1分鐘已關閉, 請搭下一個班次。");

    } else {

	cm.gainItem(4031732,-1);

	cm.warp(540010001);
    }
  cm.dispose();

   }

  }

 }
