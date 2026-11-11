/**
-- Odin JavaScript --------------------------------------------------------------------------------
	
Faito(Showa Exchange Quest) - Showa Town(801000300)
-- By ---------------------------------------------------------------------------------------------
	
Information
-- Version Info -----------------------------------------------------------------------------------
	
1.0 - First Version by Information
	    - Base from Sean360 script, thanks
---------------------------------------------------------------------------------------------------

**/


var status = -1;

var eQuestChoices = new Array ();


var requiredItemArr = new Array();

var requiredItemNumArr = new Array();

var requiredMoneyArr = new Array();

var Allscroll = new Array();
								

var requiredItem  = 0;


var requiredItemNum = 0;


var lastSelection = -1;


var remoney = 0;


var reward;


var itemSet;



function action(mode, type, selection) {
	
if (status == 2 && mode != 1 ) {
		
cm.sendOk("好好慶祝吧!有問題歡迎隨時找我～");
		
cm.safeDispose();
		
return;

	}
	
status++;

    
if (status == 0) {
		
var eQuestChoice = makeChoices(eQuestChoices);
		
cm.sendSimple(eQuestChoice);

    } else if (status == 1){
		
if(selection == 38){
			
cm.sendYesNo("你確定要兌換#b週年慶卷軸#k嗎?\r\n需要#b#v4001126##t4001126#100個#k\r\n" );
			
status++;

		}else{
			
requiredItem = requiredItemArr[selection];
			
requiredItemNum = requiredItemNumArr[selection];
			
reward = eQuestChoices[selection];
			
remoney = requiredMoneyArr[selection];
			
var eRequired = makeRequire(requiredItem,requiredItemNum,reward,remoney);
			
cm.sendSimple(eRequired);

		}
		
lastSelection = selection;

	}else if(status == 2){	
		
cm.sendYesNo("你確定你要製作#b#v"+ reward + "##t" + reward +"##k嗎?\r\n" );

    }else if(status == 3){
		
if(lastSelection == 38){
			
itemSet = (Math.floor(Math.random() * Allscroll.length));
			
reward = Allscroll[itemSet];
			
if(!cm.haveItem(4001126,100)){
					
cm.sendOk("你的楓葉不足！\r\n" );
					
cm.dispose();
					
return ;

			}
			
if(!cm.canHold(reward)){
				
cm.sendOk("你的物品欄已經滿了！\r\n" );
				
cm.dispose();
				
return ;

			}
			
cm.gainItem(4001126,-100);
			
cm.gainItem(reward,1);
			
cm.sendOk("還滿意兌換的獎勵嗎？若不滿意可以再接再勵喲！\r\n" );
			
cm.dispose();

			
		}else{
			
for(var i = 0 ; i < requiredItem.length ; i++){
				
if(!cm.haveItem(requiredItem[i],requiredItemNum[i])){
					
cm.sendOk("你缺少楓葉武器或楓葉不足！\r\n" );
					
cm.dispose();
					
return ;

				}

			}
			
if(cm.getMeso() < remoney){
				
cm.sendOk("楓幣不足！\r\n" );
				
cm.dispose();
				
return ;

			}
			
if(!cm.canHold(reward)){
				
cm.sendOk("你的背包空間不足！\r\n" );
				
cm.dispose();
				
return ;

			}
			
cm.gainMeso(-remoney);
			
for(var i=0 ; i < requiredItem.length ; i++ ){
				
cm.gainItem(requiredItem[i] , -requiredItemNum[i]);

			}
			
cm.gainItem(reward,1,true);
			
cm.sendOk("完成囉！繼續欣賞美麗的楓葉吧！\r\n" );
			
cm.dispose();

		}

	}

}

function makeChoices(a){
    
var result  = "#d           楓葉武器怪物掉落資訊 \r\n#r 劍士裝備 \r\n  \r\n #b#i1302020:# #o2130100:# #o3210205:# #o4230110# \r\n 可兌換 #i1302064:# 或 #i1402039:# \r\n  \r\n #b#i1432012:# #o3230306:# #o4230106:# \r\n 可兌換 #i1432040:# \r\n  \r\n #b#i1442024:# #o3230305:# \r\n 可兌換 #i1442051:#\r\n  \r\n #i1412011:# #o1120100:# #o5150001# \r\n 可兌換 #i1312032:# 或 #i1412027:#\r\n  \r\n #i1422014:# #o1110101:# #o3000000:# #o4230109# \r\n 可兌換 #i1322054:# 或 #i1422029:# \r\n\r\n #r(不可兌換) \r\n \r\n #b#i1302030:# #o4230126:# #o3110102:# #o5120001#  \r\n\r\n\r\n#r 法師裝備 \r\n  \r\n #b#i1382009:# #o2230102:# \r\n 可兌換 #i1372034:# 或 #i1382039:#\r\n\r\n #r(不可兌換) \r\n \r\n #b#i1382012:# #o3210208:# #o5120000:# \r\n\r\n\r\n#r 弓箭手裝備 \r\n  \r\n #b#i1452016:# #o2230111:# #o5200002# \r\n 可兌換 #i1452045:# \r\n\r\n #r(不可兌換) \r\n \r\n #b#i1452022:# #o3000005:# #o5120002:#  \r\n \r\n \r\n \r\n  #i1462014:# #o1140100:# #o2230100:# \r\n 可兌換 #i1462040:#\r\n\r\n #r(不可兌換) \r\n \r\n #b#i1462019:# #o2230101:# \r\n\r\n\r\n#r 盜賊裝備 \r\n  \r\n #b#i1332025:# #o3210100:# #o3210200:# \r\n 可兌換 #i1332055:# 或 #i1332056:#\r\n  \r\n #i1472030:# #o2110200:# #o4230111# \r\n 可兌換 #i1472055:#\r\n\r\n #r(不可兌換) \r\n \r\n #b#i1472032:# #o4230125:# #o4230115:# \r\n\r\n\r\n#r 海盜裝備 \r\n  \r\n #b#i1482020:# #o3210202:# #o3230100:# \r\n 可兌換 #i1482022:# \r\n\r\n #r(不可兌換) \r\n \r\n #b#i1482021:# #o4230102:# #o5120003:# \r\n \r\n \r\n \r\n #i1492020:# #o1210103:# #o2220100# \r\n 可兌換 #i1492022:#\n\r\n #r(不可兌換) \r\n \r\n #b#i1492021:# #o3210201:# #o5140000:# \r\n\r\n\r\n#r  影武者裝備 \r\n  \r\n #b#i1342025:# #o3110101:# #o3110100:# #o3230101# \r\n 可兌換 #i1342027:# \r\n\r\n #r(不可兌換) \r\n \r\n #b#i1342026:# #o4230126:#  \r\n \r\n#r 防具 \r\n  \r\n #b#i1092030:# #o1110100:# #o4230114:# \r\n 可兌換 #i1092045:# 或 #i1092046:# 或 #i1092047:# \r\n \r\n \r\n #b#i1102167:# #o2230110:# #o4230112:# #o5130104:# \r\n 可兌換 #i1102168:#";
    
for (var x = 0; x< a.length; x++){
		
result += " #L" + x + "##v" + a[x] + "##t" + a[x] + "##l\r\n";

    }
	
result += "";
    
return result;

}

function makeRequire(a,b,re,m){
    
var result  = "注意！做出來的物品#b素質都是隨機#k的唷~\r\n製作#b#v"+re+"##t"+re+"##k需要以下物品：\r\n\r\n";
    
for (var x = 0; x< a.length; x++){
		
result += "#v" + a[x] + "##t" + a[x] + "# " + b[x]+ "個#l\r\n";

    }
	result +="#fUI/UIWindow.img/QuestIcon/7/0##b"+m+"#k\r\n";
    
return result;

}
