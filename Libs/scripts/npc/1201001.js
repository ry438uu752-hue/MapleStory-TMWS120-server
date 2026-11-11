/* Dances with Balrog
	Warrior Job Advancement
	
Victoria Road : Warriors' Sanctuary (140000000)

	
Custom Quest 100003, 100005

*/

var status = 0;

var job;



function start() {
    
status = -1;
    
action(1, 0, 0);
}


function action(mode, type, selection) {
    
if (mode == 0 && status == 2) {
	
cm.sendOk("等你提升到足夠的等級後再來找我");
	
cm.dispose();
	
return;
    }
    
if (mode == 1)
	status++;

    else
	status--;
    
if (status == 0) {
	
if (cm.getJob() == 2000) {
	    
if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() == 2000) {
		
cm.sendNext("要進行全面的修煉，首先要搞清楚我是過去英雄中的哪一位。可是什麼也想不起來啊...");

	    } else {
		
cm.sendOk("等你提升到足夠的等級後再來找我");
		
cm.dispose();

	    }
	} else {
	    
if (cm.getPlayerStat("LVL") >= 500 && cm.getJob() == 100) { // Warrior
		
if (cm.getQuestStatus(100003) >= 1) {
		    
cm.completeQuest(100005);
		    
if (cm.getQuestStatus(100005) == 2) {
			
status = 20;
			
cm.sendNext("？");

		    } else {
			
if (!cm.haveItem(4031008)) {
			    
cm.gainItem(4031008, 1);

			}
			
cm.sendOk("？")
			
cm.dispose();

		    }

		} else {
		    
status = 10;
		    
cm.sendNext("？");

		}

	    } else if (cm.getQuestStatus(100100) == 1) {
		
cm.completeQuest(100101);
		
if (cm.getQuestStatus(100101) == 2) {
		    
cm.sendOk("你不是狂狼勇士#r！#k");

		} else {
		    
cm.sendOk("");
		    
cm.startQuest(100101);

		}
		
cm.dispose();

	    } else {
		
cm.sendOk("狂狼勇士是我們的英雄");
		
cm.dispose();

	    }

	}

    } else if (status == 1) {
	
cm.sendNextPrev("聽說英雄的武器能夠認出主人。如果我就是那個使用矛的英雄，只要摸到矛就會出現某種反應…");

    } else if (status == 2) {
	
cm.sendYesNo("一碰到#p1201001#，迷迷糊糊想起了一些東西，但是不知是不是錯覺。");

    } else if (status == 3) {
	
if (cm.getJob() == 2000) {
	    
cm.resetStats(35, 4, 4, 4);
cm.teachSkill(20001004, 1); // 怪物騎乘
cm.teachSkill(21000000, 0, 10); // 矛之鬥氣
cm.teachSkill(21000002, 0, 20); // 雙重攻擊
cm.teachSkill(21001001, 0, 15); // 戰鬥衝刺
cm.teachSkill(21001003, 0, 20); // 神速之矛	    
cm.changeJob(2100); // 狂狼勇士
	}
	
cm.gainItem(1142129, 1);
	
cm.sendOk("你現在已經是 #b狂狼勇士#k 了");
	
cm.dispose();

    } else if (status == 11) {
	
cm.sendNextPrev("？")

    } else if (status == 12) {
	
cm.askAcceptDecline("？");

    } else if (status == 13) {
	
cm.gainItem(1142129, 1);
	
cm.startQuest(100003);
	
cm.sendOk("？");
	
cm.dispose();

    } else if (status == 21) {
	
cm.sendSimple("？");

    } else if (status == 22) {
	
var jobName;
	
if (selection == 0) {
	    
jobName = "？";
	    
job = 110; // ？
	} else if (selection == 1) {
	    
jobName = "？";
	    
job = 120; // ？
	} else if (selection == 2) {
	    
jobName = "？";
	    
job = 130; // ？
	}
	
cm.sendYesNo("你想要成為一位 #r" + jobName + "#k?");

    } else if (status == 23) {
	
cm.changeJob(job);
	
cm.gainItem(4031012, -1);
	
cm.sendOk("你將繼續強大, 到達最終極的境界！");
	
cm.dispose();

    }

}	
