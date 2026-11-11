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
	
cm.sendSimple("#b需要什麼服務呢 #r？#b\n\r #L1#購買釣魚餌       (1組#r120#b個)#l #b\n\r #L3#使用高級魚餌罐頭 (1組#r120#b個)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#r1 #b組魚餌需要 #r1200#b 楓幣 #r。 #b您要購買嗎 #r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(2300001,1) && cm.haveItem(5350000,1)) {
cm.gainItem(2300001, 120);
		    
cm.gainItem(5350000,-1);
		    
cm.sendNext("#b謝謝您的惠顧 #r？ #b歡迎下次再來 #r~ ");

		} else {
		   
cm.sendOk("#b您沒有 #r高級魚餌罐頭#k #b！#k");

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
		
cm.sendOk("#r你的背包空間不足 #b！");

	    }
	    
cm.safeDispose();

	}

    }

}