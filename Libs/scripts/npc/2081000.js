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
	
cm.sendSimple("歡迎來到神木村! 我能為你做些什麼呢 ?#b \n\r #L1#魔法種子#b X  #r1#k       (   #r5000 #b楓幣#k)#l #b\n\r #L3#魔法種子#b X  #r1#k       ( #r   500 #b楓葉#k)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#r1 #b顆 #t4031346##i4031346##k需要 #r5000#b 楓幣 #r。#b您要購買嗎#r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(4031346,1) && cm.getMeso() >= 0) {
cm.gainMeso(0);
cm.gainItem(4001126,-500);		    
cm.gainItem(4031346,1);
		    
cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		   
cm.sendOk("#r您的楓幣不足 #b！");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(4031346,1) && cm.getMeso() >= 5000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-5000);
		    
cm.gainItem(4031346,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了#k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足 #b！");

	    }
	    
cm.safeDispose();

	}

    }

}