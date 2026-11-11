// Xinga - Pilot
function start() {
    
if (cm.getMapId() == 200090031) {
	
cm.dispose();

    } else {
	
cm.sendYesNo("這是開往魔法森林的飛船, 搭乘費用是 #b2000#k 楓幣 !");

    }

}


function action(mode, type, selection) {
    
if (mode == 1) {

if (cm.getMeso() >= 2000) {
		
		    
cm.gainMeso(-2000);	
cm.warp(200090031, 0);
      } else {
		
cm.sendOk("#r您的楓幣不足 #b！");

	    }   
cm.dispose();

      }
   }
