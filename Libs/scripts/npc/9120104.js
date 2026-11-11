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
	
cm.sendSimple("#b歡迎光臨 #r~#b 想試試手氣嗎 #r？#b\n\r 買越多中獎機率越大喲 #r~#b \n\r #L1#小鋼珠轉蛋券#b X  #r1#k       (   #r50000 #b楓幣#k)#l #b\n\r #L3#小鋼珠轉蛋券#b X #r50#k       ( #r2000000 #b楓幣#k)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#r1 #b張 #t4110010##i4110010##k需要 #r50000#b 楓幣 #r。#b您要購買嗎#r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(4110010,50) && cm.getMeso() >= 2000000) {
cm.gainMeso(-2000000);
		    
cm.gainItem(4110010,50);
		    
cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		   
cm.sendOk("#r您的楓幣不足 #b！");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(4110010,1) && cm.getMeso() >= 50000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-50000);
		    
cm.gainItem(4110010,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

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