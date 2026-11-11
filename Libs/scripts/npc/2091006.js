/*
	Mu Lung Training Center entrance

*/
var status = -1;

var sel;


function action(mode, type, selection) {
    
if (mode == 1) {
	
status++;

    } else {
	
cm.sendNext("( #b這股神祕的氣息好像知道我將要離開#k, #b正在漸漸的消散#r...#k)");
	
cm.safeDispose();
	
return;

    }

    
if (status == 0) {
	
cm.sendSimple("#e< 公告 >#n \r\n凡是有勇氣挑戰武陵道場者, 歡迎你前來武陵道場！- 武公 - \r\n( #r換過腰帶的玩家須將點數歸零後重新累積才能兌換手套#b！#k) \r\n#b#L0#挑戰武陵道場 #l \r\n#b#L1#換取手套 #l")

    } else if (status == 1) {
	
sel = selection;
	

if (sel == 1) {
		
cm.sendSimple("你目前的訓練總分是 #b"+cm.getDojoPoints()+"#k. 我們師父喜歡有挑戰自我能力的人, 所以只要你能累積到達一定的點數, 你就可以根據你的訓練點數, 兌換最高榮譽的英雄手套...\r\n #b#L0##i1082392:# #t1082392##k (#r5000#k)#l\r\n #b#L1##i1082393:# #t1082393##k (#r7000#k)#l\r\n #b#L2##i1082394:#   #t1082394##k (#r9000#k)#l\r\n");
	} else {
	    
cm.sendYesNo("#b(摸在布告欄上，感受到一股神祕的氣息圍繞在身旁。)#k\r\n\r\n你想移動到武陵道場嗎?");

	}

    } else if (status == 2) {
	
if (sel == 1) {		
var record = cm.getDojoRecord();
		
var required = 0;

switch (record) {
		    
case 0:
			required = 5000;
			break;
			
case 1:
			required = 7000;
			break;
case 2:
			required = 9000;
			break;


		}

		
if (record == selection && cm.getDojoPoints() >= required) {
		    
var item = 1082392 + record;

if (cm.canHold(item)) {
			
cm.gainItem(item, 1);
			
cm.setDojoRecord(false);

         } else {
			
cm.sendOk("請確認你的背包是否還有足夠的空間欄位");

		}

	 } else {
		    
cm.sendOk("你可能已經獲得, 或訓練點數不足, 請先嘗試換取其它等級較低的腰帶。");

		}
		
cm.dispose();	    
	} else {
	    
cm.saveLocation("MULUNG_TC");
	    
cm.warp(925020000, 0);
	    
cm.dispose();

	}

    } else if (status == 3) {
	
cm.dispose();

    }

}