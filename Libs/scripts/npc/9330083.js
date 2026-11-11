//

function start() {

    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == 0) {

	cm.dispose();

	return;

    }
    status++;

    if (status == 0) {

    if(cm.haveItem(4001337,-1)) {

cm.sendYesNo("#r你想前往 #b鬧鬼宅邸 #r嗎#k ？");

    } else {
	    
	    
cm.dispose();

	}

    }

    if (status == 1) {
	
cm.gainItem(4001337, 0);
	
cm.warp(229010000,0);
	
cm.dispose();

    }

}
