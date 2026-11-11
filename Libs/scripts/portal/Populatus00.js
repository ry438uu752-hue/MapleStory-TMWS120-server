function enter(pi) {
	
if (pi.getPlayer().getClient().getChannel() != 1 && 
    pi.getPlayer().getClient().getChannel() != 2 && 
    pi.getPlayer().getClient().getChannel() != 3 && 
    pi.getPlayer().getClient().getChannel() != 4 && 
    pi.getPlayer().getClient().getChannel() != 5 && 
    pi.getPlayer().getClient().getChannel() != 6 && 
    pi.getPlayer().getClient().getChannel() != 7 && 
    pi.getPlayer().getClient().getChannel() != 8 && 
    pi.getPlayer().getClient().getChannel() != 9 && 
    pi.getPlayer().getClient().getChannel() != 10 && 
    pi.getPlayer().getClient().getChannel() != 11 && 
    pi.getPlayer().getClient().getChannel() != 12 && 
    pi.getPlayer().getClient().getChannel() != 13 && 
    pi.getPlayer().getClient().getChannel() != 14 && 
    pi.getPlayer().getClient().getChannel() != 15 && 
    pi.getPlayer().getClient().getChannel() != 16 && 
    pi.getPlayer().getClient().getChannel() != 17 && 
    pi.getPlayer().getClient().getChannel() != 18 && 
    pi.getPlayer().getClient().getChannel() != 19 && 
    pi.getPlayer().getClient().getChannel() != 20) {
		
pi.playerMessage(5, "完成20個頻道");
		
return false;

	}
    
if (pi.haveItem(4031179)) {
	
pi.warp(220080001);
	
return true;

    }
    
if (!pi.haveItem(4031172)) {
	
return false;

    }
    
if (pi.getPlayerCount(220080001) <= 0) { 
// Papu Map
	
var papuMap = pi.getMap(220080001);

	
papuMap.resetFully();

	
pi.playPortalSE();
	
pi.warp(220080001, "st00");
	
return true;

    } else {
	
if (pi.getMap(220080001).getSpeedRunStart() == 0 && (pi.getMonsterCount(220080001) <= 0 || pi.getMap(220080001).isDisconnected(pi.getPlayer().getId()))) {
	    
pi.playPortalSE();
	    
pi.warp(220080001, "st00");
	    
return true;
	} else {
	    
pi.playerMessage(5, "The battle against Papulatus has already begun, so you may not enter this place.");
	    
return false;

	}

    }


/*    
var canEnter = false;

    
if (pi.haveItem(4031172)) { 
// Ludibrium Medal
	
var currenttime = new Date().getTime();
	
var record = pi.getQuestRecord(7200);
	
var diff = currenttime - record.getCompletionTime();
	
	
// After 1 day
	if (diff >= 86400000) { 
// 24 hours
	    
record.setCompletionTime(currenttime);
	    
record.setCustomData("1");
	    
canEnter = true;

	} else {
	    
var recordData = record.getCustomData();
	    
if (recordData == null || recordData.equals("")) {
		
record.setCompletionTime(currenttime);
		
record.setCustomData("1");
		
canEnter = true;

	    } else if (recordData.equals("1")) {
		
record.setCustomData("2");
		
canEnter = true;

	    }

	}

    } else {
	
return false;

    }
    
if (!canEnter) {
	
pi.playerMessage(5, "You may only enter the Origin of Clocktower twice per day.");
	
return false;

    }
*/

}