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
	
cm.sendSimple("#d需要什麼服務呢 #r？#d\n\r #L1#夢境解析#k                 (#b#t4001337##i4001337##k X #r10#k)#l #d\n\r #L3#兌換#d綿羊單人床 #r+ #k#d實境沙發#k(#b  #t4032226##i4032226##k X #r10#k)");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#b你昨夜夢境裡的結果是#r...#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(2300001,1) && cm.haveItem(4032226,10)) {
cm.gainItem(4032226, -10);		    
cm.gainItem(3010054, 1);

cm.gainItem(3010086, 1);		    
cm.sendNext("#b您將從此遠離噩夢#r,#k#b且不再感到孤單#r ~ #k");

		} else {
		   
cm.sendOk("您的 #b#t4032226##i4032226##k 數量不足 ！ ");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(2300000,120) && cm.haveItem(4001337,10)) {
		
if (!cm.haveItem(2300000)) {
		    
cm.gainMeso(-0);
		    
cm.gainItem(4001337,-10);
cm.gainItem(4032226, 1);		   
 cm.sendNext("#b恭喜 #r！#b你作了一個美夢#k #r~ ");

		} else {
		    
cm.sendNext("");

		}

	    } else {
		
cm.sendOk("非常抱歉, 由於您的#b#t4001337##i4001337##k 數量不足, \n\r 導致無法幫您分析結果, 請收集足夠的數量再來 #r！#k");

	    }
	    
cm.safeDispose();

	}

    }

}