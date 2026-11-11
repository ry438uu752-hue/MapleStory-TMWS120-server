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
	
cm.sendSimple("#b請問您想要什麼騎寵 #r？#k#d \n\r #L1#馬鞍+野豬#k   ( #r20,000,000 #b楓幣#k)#l #d\n\r #L3#銀色鬃毛#k    ( #r50,000,000 #b楓幣#k)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#b#t1902000##i1902000##k需要 #r20000,000#b 楓幣 #r。#b您要購買嗎#r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(1902001,1) && cm.getMeso() >= 50000000) {
cm.gainMeso(-50000000);

cm.gainItem(1902000,-1); //野豬 70等		    
cm.gainItem(1902001,1); //銀色鬃毛 120等
		    
cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b您已經擁有或楓幣不足 #r！");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(1902000,1) && cm.getMeso() >= 20000000) {
if (!cm.haveItem(0)) {

				    
cm.gainMeso(-20000000);
		    
cm.gainItem(1902000,1); //野豬 70等
cm.gainItem(1912000,1); //馬鞍
cm.teachSkill(1004,1,1); //怪物騎乘		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b2您已經擁有 #r。#k");

		}

	    } else {
		
cm.sendOk("#b2您已經擁有或楓幣不足 #r！");

	    }
	    
cm.safeDispose();

	}

    }

}