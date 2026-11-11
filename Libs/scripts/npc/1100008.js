// Xinga - Pilot
function start() {
    
if (cm.getMapId() == 200000160) {
	
cm.dispose();

    } else {
	
cm.sendYesNo("這是開往耶雷弗的飛船, 搭乘費用是 #b5000#k 楓幣 !");

    }

}


function action(mode, type, selection) {
    
if (mode == 1) {

if (cm.getMeso() >= 5000) {
		
		    
cm.gainMeso(-5000);
		    	
cm.warp(200090020, 0);
      } else {
		
cm.sendOk("#r您的楓幣不足 #b！");

	    }   
cm.dispose();

      }
   }