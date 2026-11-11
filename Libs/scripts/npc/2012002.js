/*
	Erin - Before Takeoff To Ellinia(200000112)
*/

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if(mode == 0) {
	cm.sendOk("飛船就要出發了，您很快就會到達目的地。");
	cm.dispose();
	return;
    }
    if(status == 0) {
	cm.sendYesNo("你現在要離開嗎？提醒您已收取的票不能退還。您確定要離開嗎？");
    } else if(status == 1) {
	cm.warp(200000111, 0);
	cm.dispose();
    }
}