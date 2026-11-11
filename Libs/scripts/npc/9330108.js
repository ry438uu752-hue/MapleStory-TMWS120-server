/* Kedrick
	Fishking King NPC
*/

/* global cm */

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
	if(cm.haveItem(3010004)) {
	cm.sendSimple("請自備釣竿，場內均有販售魚餌\n\r #b#L0#進入釣魚場#l");
	} else {
	    
cm.sendOk("你身上沒有 #b#t3010004##i3010004##k  ！");
	    
cm.dispose();

	}
    } else if (status == 1) {
	sel = selection;
	if (sel == 0) {
	    cm.sendSimple("請選擇喜歡的場地？\r\n#b#L0#外太空釣魚場#l\r\n#L1#夢幻王國釣魚場#l\r\n#L2#精靈釣魚場#l#k");
	} else if (sel == 2) {
	    var returnMap = cm.getSavedLocation("FISHING");
	    if (returnMap < 0 || cm.getMap(returnMap) == null) {
		returnMap = 910000000; // to fix people who entered the fm trough an unconventional way
	    }
	    cm.clearSavedLocation("FISHING");
	    cm.warp(returnMap,0);
	    cm.dispose();
	}
    } else if (status == 2) {
	if (sel == 0 && selection <= 2 && selection >= 0) {
	    if (cm.getPlayer().getMapId() < 749050500 || cm.getPlayer().getMapId() > 749050502) {
	    	cm.saveLocation("FISHING");
	    }
	    cm.warp(749050500 + selection);
	    cm.dispose();
	} else {
	    cm.dispose();
	}
    }
}