var status = -1;

function start(mode, type, selection) {
	qm.sendNext("來找我吧，我在長老公館的地下室");
    	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	qm.dispose();
}