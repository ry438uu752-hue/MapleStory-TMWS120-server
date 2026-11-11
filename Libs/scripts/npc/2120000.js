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
	
cm.sendSimple("#b稱传ぐ或㎡ #r#d\r\n #L1#环弘艶ブ籋#l\r\n\r\n #L3#环弘艶Чブ籋#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("传 #b#t1152055##i1152055##k 惠璶 #r500 #k#b 番腑#k");

	} else if (sel == 3) {
	    		
if (cm.canHold(1152054,1) && cm.haveItem(4001337,15) && cm.haveItem(1152055,1)) {
cm.gainItem(4001337,-15);

cm.gainItem(1152055,-1);		    
cm.gainItem(1152054,1);
		    
cm.sendNext("#bЧΘ#r~ #k");



		} else {
		   
cm.sendOk("#b眤竒局Τ┪#r秆冠芲巴#b计秖ぃì#r15#b #k");

	    }
	    
cm.safeDispose();

	}

    } else if (status == 2) {
	
if (sel == 1) {
	    
if (cm.canHold(1152055,1) && cm.haveItem(4001126,500)) {
if (!cm.haveItem(0)) {

				    
cm.gainItem(4001126,-500);
		    
cm.gainItem(1152055,1);
		   
 cm.sendNext("#bЧΘ#r~ #k");

		} else {
		    
cm.sendNext("#b眤竒局Τ环弘艶ブ籋 #r#k");

		}

	    } else {
		
cm.sendOk("#b眤竒局Τ┪番腑ぃì #r");

	    }
	    
cm.safeDispose();

	}

    }

}