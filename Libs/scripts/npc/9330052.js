/* Dr. Feeble
	Henesys Random Eye Change.

*/

var status = 0;

var beauty = 0;


function start() {
    
status = -1;
    
action(1, 0, 0);
}


function action(mode, type, selection) {
    
if (mode == 0 && status == 0) {
	
cm.dispose();
	
return;

    }
    if (mode == 1)
	
status++;
    
else
	
status--;
    
if (status == 0) {
	
cm.sendNext("你好 ～　是不是厭倦了你現在的臉龐呢? 你只需要一張 #b#t5152031##k, 就可以變更一張不一樣的臉龐");

    } else if (status == 1) {
	
cm.sendYesNo("想變換成其他臉型嗎 ? 只要有 #b#t5152031##k 我就可以隨機幫你換一張不錯的臉型喔 ！");

    } else if (status == 2){
	
var face = cm.getPlayerStat("FACE");
	
var facetype;

	
if (cm.getPlayerStat("GENDER") == 0) {
	    
facetype = [20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014];

	} else {
	    
facetype = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21014];

	}
	
for (var i = 0; i < facetype.length; i++) {
	    
facetype[i] = facetype[i] + face % 1000 - (face % 100);

	}

	
if (cm.setRandomAvatar(5152031, facetype) == 1) {
	    
cm.sendOk("如何？有沒有符合你的理想呢！");

	} else {
	    
cm.sendOk("你沒有我們的會員卡喔");

	}
	
cm.dispose();

    }

}
