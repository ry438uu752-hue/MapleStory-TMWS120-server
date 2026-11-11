var cost = 20000;


function start() {
    
status = -1;
    
em = cm.getEventManager("AirPlane");
    
action(1, 0, 0);

}

function action(mode, type, selection) {
    
if (mode == 0 && status == 0) {
	
cm.dispose();
	return;
    }
    
if (mode == 1) {
	
status++;

    }
    if (mode == 0 && menu == 0) {
	
cm.sendNext("我會長時間在此為您服務。 當您改變主意時，請再次與我聯繫。");
	
cm.dispose();

    }
    if (mode == 0 && menu == 1) {
	
cm.sendOk("請確認您要離開的出發時間。 謝謝。");
	
cm.dispose();

    }
    if (status == 0) {
	
cm.sendSimple("你好～我是新加坡航空公司的艾琳, 請問您需要什麼服務　？\r\n#L0##b我想買張到新加坡的機票#k#l\r\n#L1##b讓我進入登機門#k#l");

    } else if (status == 1) {
	
menu = selection;
	
if (menu == 0) {
	    
cm.sendYesNo("機票需花費您20,000楓幣。 您確定要購買嗎？");

	} else if (menu == 1) {
	    
cm.sendYesNo("你想現在進入機場嗎？ 一旦進入，我們將回收您的機票 ！ ");

	}

    } else if (status == 2) {
	
if (menu == 0) {
	    
if (!cm.canHold(4031731) || cm.getMeso() < cost) {
		
cm.sendOk("您的楓幣或背包其他欄位空間不足");

	    } else {
		
cm.gainMeso(-5000);
		
cm.gainItem(4031731, 1);

	    }
	    
cm.dispose();

	} else if(menu == 1) {
	    
if(em == null) {
		
cm.sendNext("發生錯誤, 請聯絡 GM ");
		
cm.dispose();

	    } else if (!cm.haveItem(4031731)) {
		
cm.sendNext("請先購買機票，謝謝 ~");
		
cm.dispose();

	    } else if (em.getProperty("entry").equals("true")) {
		
cm.sendYesNo("看來這個班次有足夠的空位。 請準備好您的機票，這樣我就可以讓您進去，搭乘的時間不會很長，很快就能到達目的地了。 您想登機了嗎？");

	    } else if(em.getProperty("entry").equals("false") && em.getProperty("docked").equals("true")) {
		
cm.sendNext("您錯過了本次的航班，請再等待3分鐘搭下個班次，候機時間為5分鐘，請耐心等待。");
		
cm.dispose();

	    } else {
		
cm.sendNext("很抱歉，登機門在出發前1分鐘關閉, 請搭下一個班次。");
		
cm.dispose();

	    }

	}

    } else if (status == 3) {
	
cm.gainItem(4031731, -1);
	
cm.warp(540010100);
	
cm.dispose();

    }

}