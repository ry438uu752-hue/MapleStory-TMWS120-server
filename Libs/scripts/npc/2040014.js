/* Kedrick
	Fishking King 
NPC
*/


/* global cm 
*/

var status = -1;

var sel;


function action(mode, type, selection) {
    
if (mode == 1) {	
status++;

    } else {
		
if (status == 0) {
			
cm.dispose();
			
return;

		}
	
status--;

    }

    
if (status == 0) {
	
cm.sendSimple("#b收集楓葉可以兌換找碴小遊戲卡片 #r!#k #d\n\r #L3#兌換找碴遊戲卡 ( #r100#b個 #b楓葉#k X #r10 #b張#k )#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#r1 #b組魚餌需要 #r1200#b 楓幣 #r。 #b您要購買嗎 #r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(4080100,10) && cm.haveItem(4001126,100)) {
cm.gainItem(4080100,10);
		    
cm.gainItem(4001126,-100);
		    
cm.sendNext("#b兌換成功 #r ！ #b歡迎下次再來 #r~ ");

		} else {
		   
cm.sendOk("#b您的 #r楓葉#k #b不足 #r！#k");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(2300000,120) && cm.getMeso() >= 1200) {
		
if (!cm.haveItem(2300000)) {
		    
cm.gainMeso(-1200);
		    
cm.gainItem(2300000, 120);
		   
 cm.sendNext("#b謝謝您的惠顧 #r？ #b歡迎下次再來 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足 #b！");

	    }
	    
cm.safeDispose();

	}

    }

}