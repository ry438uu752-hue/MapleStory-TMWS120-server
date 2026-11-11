/**
	Konpei - Near the Hideout(801040000)
*/

function start() {
    cm.sendYesNo("想前往瑞恩島嗎?");
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.sendOk("還沒準備好嗎 !");
    } else {
	cm.warp(140020300,0);
    }
    cm.dispose();
}