/* Kedrick
	Fishking King 
NPC
*/


/* global cm 
*/
var status = -1;
var sel;


function action(mode, type, selection) { 
if (mode == 1) {	
status++;
    } else {	
if (status == 0) {		
    cm.dispose();
return;
	} status--;
    } if (status == 0) {
    cm.sendSimple("#b想學習額外技能嗎 #r？#b\n\r在學習的過程中請勿點擊#rAP#b或#rSP#b點數\n\r 因為會導致#r轉職後無法正常加技能#b\n\r 在獲得技能後請#r設置鍵盤並且保存#k，#r切記 ! \n\r #b[#r警告#b] \n\r #r學習額外技能會先將三轉的技能點數\n\r 拿來消耗你所選的額外學習技能\n\r 可使用#b技能重配卷軸#r補回你所需要的點數。 #b \n\r #L1#恢復成初心者 (#r獲得技能後必點#k)#b\n\r #L3#學習劍士技能  #r神聖之火#b\n\r #L4#學習法師技能  #r群體治癒#b\n\r #L5#學習弓手技能  #r會心之眼#b\n\r #L6#學習盜賊技能  #r二段跳#b\n\r #L7#學習海盜技能  #r章魚砲台#k#l");
    } else if (status == 1) {
sel = selection;
if (sel == 1) {
    cm.sendYesNo("你想恢復成#r初心者#k嗎 ?");
	} else if (sel == 3) {
if (cm.canHold(4110010,50) && cm.getMeso() >= 0) {
    cm.gainMeso(1);
    cm.gainMeso(-1);   
    cm.changeJob(130); // 劍士_槍騎兵
	cm.gainItem(2000005, 1);
	cm.gainItem(2000005, -1);
    cm.teachSkill(1301007, 30, 30); // 神聖之火		    
    cm.sendNext("你已習得技能#r神聖之火#k \r\n(#b三轉技能#r已預先透支30點#k)");
	} else {	   
    cm.sendOk("#r您的楓幣不足 #b！");
	}    
    cm.safeDispose();
	} else if (sel == 4) {
if (cm.canHold(4110010,50) && cm.getMeso() >= 0) {
    cm.gainMeso(1);
    cm.gainMeso(-1);   
    cm.changeJob(230); // 法師_僧侶
	cm.gainItem(2000005, 1);
	cm.gainItem(2000005, -1);
    cm.teachSkill(2301002, 30, 30); // 群體治癒		    
    cm.sendNext("你已習得技能#r群體治癒#k \r\n(#b三轉技能#r已預先透支30點#k)");
	} else {	   
    cm.sendOk("#r您的楓幣不足 #b！");
	}    
    cm.safeDispose();
	} else if (sel == 5) {
if (cm.canHold(4110010,50) && cm.getMeso() >= 0) {
    cm.gainMeso(1); 
    cm.gainMeso(-1); 
    cm.changeJob(312); // 弓手_箭神
	cm.gainItem(2000005, 1);
	cm.gainItem(2000005, -1);
    cm.teachSkill(3121002, 30, 30); // 會心之眼		    
    cm.sendNext("你已習得技能#r會心之眼#k \r\n(#b三轉技能#r已預先透支30點#k)");
	} else {	   
    cm.sendOk("#r您的楓幣不足 #b！");
	}    
    cm.safeDispose();
	} else if (sel == 6) {
if (cm.canHold(4110010,50) && cm.getMeso() >= 0) {
    cm.gainMeso(1);
    cm.gainMeso(-1);  
    cm.changeJob(411); // 盜賊_暗殺者
	cm.gainItem(2000005, 1);
	cm.gainItem(2000005, -1);
    cm.teachSkill(4111006, 20, 20); // 二段跳		    
    cm.sendNext("你已習得技能#r二段跳#k+#r20#k \r\n(#b三轉技能#r已預先透支20點#k)");
	} else {	   
    cm.sendOk("#r您的楓幣不足 #b！");
	}    
    cm.safeDispose();
	} else if (sel == 7) {
if (cm.canHold(4110010,50) && cm.getMeso() >= 0) {
    cm.gainMeso(1);
    cm.gainMeso(-1);  
    cm.changeJob(521); // 海盜_神槍手
	cm.gainItem(2000005, 1);
	cm.gainItem(2000005, -1);
    cm.teachSkill(5211001, 30, 30); // 章魚砲台		    
    cm.sendNext("你已習得技能#r章魚砲台#k \r\n(#b三轉技能#r已預先透支30點#k)");
	} else {	   
    cm.sendOk("#r您的楓幣不足 #b！");
	}    
    cm.safeDispose();
	}
    } else if (status == 2) {
if (sel == 1) {
if (cm.canHold(4110010,1) && cm.getMeso() >= 0) {
if (!cm.haveItem(0)) {
    cm.gainMeso(1);
    cm.gainMeso(-1);
    cm.changeJob(0); // 初心者   
    cm.sendNext("現在，一切將從零開始");
	} else {
    cm.sendNext("這裡是你已擁有道具的限制提醒");
    }
	} else {	
    cm.sendOk("#r您的楓幣不足 #b！");
	}
    cm.safeDispose();
	}
    }
    }