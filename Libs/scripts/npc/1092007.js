/*
Muirhat - Nautilus' Port
*/


function start() {
	if (cm.getQuestStatus(2175) == 1) {
		cm.sendYesNo("你準備好了嗎？現在我就送你過去找#o9300156#。");
	} else {
		cm.sendOk("沒看到我很盲嗎？");
		cm.dispose();
}
}

function action(mode, type, selection) {
	if (mode == 1)
		cm.warp(912000000, 0);
		cm.dispose();
}