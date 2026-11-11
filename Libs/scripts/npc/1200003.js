/**
	Konpei - Near the Hideout(801040000)
*/

function start() {
    cm.sendYesNo("想前往維多利亞港嗎?");
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.sendOk("還沒準備好嗎 !");
    } else {
	cm.warp(104000000,0);
    }
    cm.dispose();
}