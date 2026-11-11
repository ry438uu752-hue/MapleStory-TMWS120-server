/* ==================
 腳本類型:  任務	    
 腳本版權：ZeroMS
 =====================
 */
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1) {
	    status++;
	}
	if (status == 0) {
	    qm.sendNext("好吧，所以你也要去戰鬥. 謝謝... 只是讓你知道，敵人可能比任何你所面對的更強大，你準備好了?");
	} else if (status == 1) {
	    qm.warp(802000800, 0);
	    qm.dispose();
	}
    }
}

function end(mode, type, selection) {
}