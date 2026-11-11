
function enter(pi) {
	
var map = 749030000;
	
var target = pi.getClient().getChannelServer().getMapFactory().getMap(map);
	
var targetPortal = target.getPortal(0);
	
pi.saveLocation("Gachapon");
	
pi.getPlayer().changeMap(target, targetPortal);
	
return true;

}
