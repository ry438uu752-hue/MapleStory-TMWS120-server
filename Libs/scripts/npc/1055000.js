/** 
	Tylus: Thief 3rd job advancement
	
El Nath: Chief's Residence (211000001)

	
Custom Quest 100100, 100102

*/

var status = 0;
var job;
var skills = Array(21001003, 21000000, 21100002, 21100004, 21100005, 21110002);

//polearm booster, combo ability, polearm mastery, final charge, combo smash, combo drain, full swing


function start() {
    
status = -1;
    
action(1, 0, 0);

}


function action(mode, type, selection) {
    
if (mode == 0 && status == 1) {
	
cm.sendOk("等你提升到足夠的等級後再來找我");
	
cm.dispose();
	
return;
    
}
    if (mode == 1)
	
status++;
    
else
	
status--;
    
if (status == 0) {
	
if (!(cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432)) {
	    
if (cm.getQuestStatus(6192) == 1) {
		
if (cm.getParty() != null) {
		    
var ddz = cm.getEventManager("ProtectTylus");
		    
if (ddz == null) {
			
cm.sendOk("#r真神#k與你同在!");

		    } else {
			
var prop = ddz.getProperty("state");
			
if (prop == null || prop.equals("0")) {
			    
ddz.startInstance(cm.getParty(), cm.getMap());

			} else {
			    
cm.sendOk("Someone else is already trying to protect Tylus, please try again in a bit.");

			}

		    }

		} else {
		    
cm.sendOk("Please form a party in order to protect Tylus!");

		}

	    } else if (cm.getQuestStatus(6192) == 2) {
		
cm.sendOk("You have protected me. Thank you. I will teach you stance skill.");
		
if (cm.getJob() == 412) {
			
if (cm.getPlayer().getMasterLevel(4120002) <= 0) {
				
cm.teachSkill(4120002, 0, 10);

			}

		} else 
if (cm.getJob() == 422) {
			
if (cm.getPlayer().getMasterLevel(4120002) <= 0) {
				
cm.teachSkill(4120002, 0, 10);


			}

		}

	    } else {
		
cm.sendOk("#r真神#k與你同在!");

	    }
	    
cm.dispose();
	    
return;

	}
	
if ((cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {

cm.sendNext("你已茁壯成為了一位強者");

	} else {
	    
cm.sendOk("請將2轉技能使用完畢後, 再來找我3轉");
	    
cm.safeDispose();

	}

    } else if (status == 1) {

if (cm.haveItem(4032619)){	    
if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    
if (cm.getJob() == 410) { // 刺客
		
cm.changeJob(411); // 暗殺者
		
cm.gainAp(5);
		
cm.sendOk("你將繼續強大, 願#r真神#k與你同在!");
cm.gainItem(4031057, -1);		
cm.dispose();

	    } else if 
(cm.getJob() == 420) { // 俠盜
		
cm.changeJob(421); // 神偷
		
cm.gainAp(5);
		
cm.sendOk("你將繼續強大, 願#r真神#k與你同在!");
cm.gainItem(4031057, -1);		
cm.dispose();

	    } else if 
(cm.getJob() == 432) { // 上忍
		
cm.changeJob(433); // 隱忍
cm.gainAp(5);
		
cm.sendOk("你將繼續強大, 願#r真神#k與你同在!");
cm.gainItem(4032619, -1);
cm.dispose();

}		

		
for (var i = 0; i < skills.length; i++) {
			
cm.teachSkill(skills[i], cm.getPlayer().getSkillLevel(skills[i]));

		}
		
cm.sendOk("你將繼續強大, 願#r真神#k與你同在!");		
cm.dispose();

	    }

	    } else {
		
cm.sendOk("到達70等，並且使用完技能點數後帶著阿里可給雪姬小姐的密函來找我 !");		
cm.dispose();

	    }

    }

}
