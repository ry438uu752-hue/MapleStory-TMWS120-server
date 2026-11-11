function enter(pi) {
    if (!pi.haveMonster(9300216)) {
	pi.playerMessage("消滅所有怪物後才能過關.");
    } else {
	pi.dojo_getUp();
	pi.getMap().setReactorState();
    }
}