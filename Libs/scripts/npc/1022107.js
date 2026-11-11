function action(mode, type, selection) {
	if (cm.isQuestActive(22530)) {
		if (!cm.canHold(1952000,1)) {
			cm.sendOk("You need inventory space..");
		} else {
			cm.forceCompleteQuest(22530);
			cm.gainExp(710);
			cm.gainItem(1952000,1);
			cm.getPlayer().gainSP(1, 1);
			cm.sendOk("您已檢查過標誌，完成了警衛的要求。");
		}
	} else {
		cm.sendOk("這裡是一處遺跡。");
	}
	cm.dispose();
}