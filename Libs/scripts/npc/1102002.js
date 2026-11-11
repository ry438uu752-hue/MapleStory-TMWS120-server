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
	
cm.sendSimple("#b請問您想要什麼騎寵 #r？#k#d \n\r #L1#馬鞍+提提阿那#k   ( #r  500,000 #b楓幣#k)#l #d\n\r #L3#提提歐#k          ( #r1,000,000 #b楓幣#k)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#b#t1902005##i1902005##k需要 #r500,000#b 楓幣 #r。#b您要購買嗎#r？#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(1902006,1) && cm.getMeso() >= 1000000) {
cm.gainMeso(-1000000);

cm.gainItem(1902005,-1); //提提阿那 50等		    
cm.gainItem(1902006,1); //提提歐 100等
		    
cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b您已經擁有或楓幣不足 #r！");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(1902005,1) && cm.getMeso() >= 500000) {
if (!cm.haveItem(0)) {

				    
cm.gainMeso(-500000);
		    
cm.gainItem(1902005,1); //提提阿那 50等
cm.gainItem(1912005,1); //馬鞍
cm.teachSkill(10001004,1,1); //怪物騎乘(貴族)		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b您已經擁有 #r。#k");

		}

	    } else {
		
cm.sendOk("#b您已經擁有或楓幣不足 #r！");

	    }
	    
cm.safeDispose();

	}

    }

}