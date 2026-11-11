var status = -1;

function action(mode, type, selection) {
    
if (mode == 1) {
	
status++;
    } else {
	
cm.dispose();
	
return;
    }
    if (status == 0) {
		
if (cm.getPlayer().getLevel() < 40 && cm.haveItem(4032492)) {
			
cm.sendYesNo("你想到安督西亞漫步道嗎?");

		} else {
			
cm.sendOk("你需要擁有#b#t4032492##i4032492##k，並且低於40等才能進入。");
			
cm.dispose();

		}

} else {
	
cm.warp(677000002,0);
	
cm.dispose();

    }

}