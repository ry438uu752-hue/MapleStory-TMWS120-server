importPackage(Packages.tools);


var rewards = Array(2000005, 2100001, 2100002, 2100003, 2100004, 2100005, 2100006, 2100007, 2100008,2100009);

var expires = Array(-1, 30, 30, 30, 30, 30, 30, 60, 60);

var quantity = Array(3, 1, 1, 1, 1, 1, 1, 1, 1,1);

var needed = Array(10, 20, 20, 20, 20, 35, 35, 50, 50);

var gender = Array(2, 2, 2, 2, 2, 2, 2, 0, 1);

var status = -1;



function action(mode, type, selection) {
    
if (mode == 1) {
	
status++;

    } else {
	if (status == 0) {
	    
cm.dispose();

	}
	
status--;
    }
    
if (status == 0) {
        
for (var i = 3994059; i < 3994085; i++) {
	    
cm.givePartyItems(i, 0, true);

	}

    }
    
switch(cm.getPlayer().getMapId()) {
	
case 749050300:
    	    
if (status == 0) {
	        
cm.sendSimple("#b你準備好要挑戰了嗎#k #r!#k#d\r\n\r\n#L0#進入恰吉的房間#l\r\n#L4#回到大廳裡#l#k");

    	    } else if (status == 1) {
				
if (selection == 0 || selection == 1 || selection == 2) {
				
var em = cm.getEventManager("English");
					
if (em == null) {
				
cm.sendOk("Please try again later.");
				
cm.dispose();
				
return;

					}
				
if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
				
cm.sendOk("你不是隊長喔, 請隊長帶領進入 !");

				} else {
				
var party = cm.getPlayer().getParty().getMembers();
				
var mapId = cm.getPlayer().getMapId();
				
var next = true;
				
var size = 0;
				
var it = party.iterator();
				
while (it.hasNext()) {
					
var cPlayer = it.next();
					
var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
					
if (ccPlayer == null) {
						
next = false;
						
break;

					}
					
size++;

				}	
				
if (next && size >= 1) {
						
if (em.getInstance("English" + selection) == null) {
						
em.startInstance_Party("" + selection, cm.getPlayer());

						} else {
						
cm.sendOk("Another party quest has already entered this channel.");

						}

				} else {
					
cm.sendOk("All members of your party must be here.");

				}

				}
				
cm.dispose();

			} else if (selection == 3 ){
				
if(cm.haveItem(4001137,15)){
					
cm.sendYesNo("你有乖寶寶印章15個阿!你想要交換獎品嗎？");

				}else{
					
cm.sendOk("有乖寶寶印章15個就可以和我換獎品喔!");

				}


			} else if (selection == 4) {
				
map = cm.getSavedLocation("EVENT");
				
cm.warp(map,0);
				
cm.dispose();

			}

		}else if(status == 2){
			
if(cm.haveItem(4001137,15)){
				
var ee = java.lang.Math.floor(java.lang.Math.random() * rewards.length);
				
cm.gainItem(4001137,-15);
				
cm.gainItem(rewards[ee],quantity[ee]);

			}
			
cm.dispose();

		}
	    
break;
	
default:
		
if (status == 0) {
	        
cm.sendYesNo(" #b進入墳墓區#k ");

    	    } else if (status == 1) {
				
cm.saveLocation("EVENT");
				
cm.warp(229010100,0); //exit map lobby
				
cm.dispose();

		}

    }

}



