/*
	Mel - Ludibrium Ticketing Place(220000100)

*/

var cost = 6000;


function start() {
    
status = -1;
    
action(1, 0, 0);

}

function action(mode, type, selection) {
    
if(mode == 1) {
	
status++;

    }
    if(mode == 0) {
	
cm.sendNext("您可能還沒準備好要搭乘, 期待您下一次的到來我將再為您服務 ?");
	
cm.dispose();
	
return;

    }
    if(status == 0) {
	
cm.sendYesNo("您好，我負責出售前往天空之城的船票。 行程為每10分鐘起飛一次，這將花費您 #b"+cost+" 楓幣#k. 您確定要購買 #b#t4031045##k嗎 ?");

    } else if(status == 1) {
	
if(cm.getMeso() >= cost && cm.canHold(4031045)) {
	    
cm.gainItem(4031045,1);
	    
cm.gainMeso(-cost);

	} else {
	    
cm.sendOk("您的楓幣或背包其他欄位空間不足");

	}
	
cm.dispose();

    }

}