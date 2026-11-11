function action(mode, type, selection) {
    if (cm.isQuestActive(2166)) {
	cm.forceCompleteQuest(2166);
	cm.sendOk("這是一塊美麗、發光石，它的周圍具有神秘力量。");
    }
    cm.dispose();
}