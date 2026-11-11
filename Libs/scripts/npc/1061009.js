/* Door of Dimension
	Enter 3rd job event
*/

function start() {
    if (cm.getQuestStatus(100101) == 2 && !cm.haveItem(4031057) && cm.getPlayer().getJob() < 200) {
	var em = cm.getEventManager("3rdjob");
	if (em == null) {
	    cm.sendOk("XXX");
	} else {
	    em.newInstance(cm.getName()).registerPlayer(cm.getChar());
	}
    } else {
	cm.sendOk("這裡是劍士#r第三次轉職#k秘境 !");
    }
    cm.dispose();
}

function action(mode, type, selection) {

}