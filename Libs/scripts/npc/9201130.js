var status = -1;

function action(mode, type, selection) {
    
if (mode == 1) {
	
status++;
    } else {
	
cm.dispose();
	
return;
    }
    if (status == 0) {
		
if (cm.getPlayer().getLevel() < 40 && cm.haveItem(4032485)) {
			
cm.sendYesNo("你想到華利弗漫步道嗎?");

		} else {
			
cm.sendOk("你需要擁有#b#t4032485##i4032485##k，並且低於40等才能進入。");
			
cm.dispose();

		}

} else {
	
cm.warp(677000008,0);
	
cm.dispose();

    }

}