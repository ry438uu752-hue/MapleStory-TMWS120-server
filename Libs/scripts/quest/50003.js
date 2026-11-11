/* ==================
 腳本類型:  任務	    
 腳本版權：ZeroMS
 =====================
 */
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.sendNext("...這是什麼？啊，我看到他越來越接近了!");
        qm.dispose();
        return;
    }
    if (status == 0) {
        qm.askAcceptDecline("當心，他似乎......比以前更加強大了。不要太小看他!");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.forceCompleteQuest();
    qm.dispose();
}
