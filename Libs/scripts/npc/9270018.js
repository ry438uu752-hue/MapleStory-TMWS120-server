// Kerny - Pilot
function start() {
    
if (cm.getMapId() == 540010002) {
	
cm.dispose();

    } else {
	
cm.sendYesNo("飛機很快就會起飛，你現在要離開嗎？ 提醒您, 一但離開後若想返回, 您將需再次購買機票。");

    }

}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(540010000, 0);

    }
    
cm.dispose();

}