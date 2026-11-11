function start() {
    
cm.sendYesNo("你可以使用閃閃發光的水晶回到現實世界。 你確定要回去嗎？");

}

function action(mode, type, selection) {
    
if (mode == 1) {
	
var map = cm.getMapId();
	
var tomap;

	
               if (map == 108010101) { // 弓箭手
	    
tomap = 211000001;

	} else if (map == 108010201) { // 法師
	    
tomap = 211000001;

	} else if (map == 108010301) { // 劍士
	    
tomap = 211000001;

	} else if (map == 108010401) { // 盜賊
	    
tomap = 211000001;

	} else if (map == 108010501) { // 海盜
	    
tomap = 211000001;

	}
	
cm.warp(tomap);

    }
    
cm.dispose();

}
