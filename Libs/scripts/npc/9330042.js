/* */
var status = 0;
var beauty = 0;
var GachItem = new Array([2049300,500,true]);


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
		if (cm.haveItem(4032056, 3)) {
			cm.sendSimple("只要準備#r64#k等#b頂級楓武#k和足夠的#b楓葉水晶#k,\r\n我就能幫你#b優化#k武器素質。\r\n(#r請將楓武放置裝備欄第一格#k)。 \r\n#L1##b#t1402039:##k#l \r\n#L2##b#t1302064:##k#l \r\n#L3##b#t1432040:##k#l \r\n#L4##b#t1382039:##k#l \r\n#L5##b#t1372034:##k#l \r\n#L6##b#t1452045:##k#l \r\n#L7##b#t1462040:##k#l \r\n#L8##b#t1472055:##k#l \r\n#L9##b#t1332055:##k#l \r\n#L10##b#t1332056:##k#l \r\n#L11##b#t1492022:##k#l \r\n#L12##b#t1482022:##k#l \r\n#L13##b#t1342027:##k#l ");
                                   } else {
                                                    cm.sendOk("你沒有 #b#i4032056:##k(#r2#b顆) 和 #b頂級楓武#k。");
                                                    cm.safeDispose();
                                   }						
	       } else if (status == 1) {
			       if (selection == 0) {
				beauty = 0;
				cm.sendSimple("Which coupon would you like to buy?\r\n#L0#Cosmetic Lenses for " + regprice + " mesos: #i5152042##t5152042##l\r\n#L1#Cosmetic Lenses for " + vipprice + " mesos: #i5152041##t5152041##l");
	       } else if (selection == 1) {
                                beauty = 1;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 2) {
                                beauty = 2;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 3) {
                                beauty = 3;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 4) {
                                beauty = 4;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 5) {
                                beauty = 5;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 6) {
                                beauty = 6;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 7) {
                                beauty = 7;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
	       } else if (selection == 8) {
                                beauty = 8;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 9) {
                                beauty = 9;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 10) {
                                beauty = 10;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 11) {
                                beauty = 11;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        } else if (selection == 12) {
                                beauty = 12;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
	       } else if (selection == 13) {
                                beauty = 13;
                                cm.sendYesNo("每次優化需要#r2#k顆#b#t4032056:##k。\r\n但也有一定的機率會因#r失敗#k導致#r劣化#k。\r\n你確定要使用#b混沌屬性#k進行轉化嗎?");
                        }
	       } else if (status == 2){
	               cm.dispose();
		if (beauty == 1){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1402039, 1402039, 1402039, 1402039, 1402039, 1402039, 1402039, 1402039, 1402039, 1402039);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1402039, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 2){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1302064, 1302064, 1302064, 1302064, 1302064, 1302064, 1302064, 1302064, 1302064, 1302064);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1302064, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
                                  if (beauty == 3){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1432040, 1432040, 1432040, 1432040, 1432040, 1432040, 1432040, 1432040, 1432040, 1432040);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1432040, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 4){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1382039, 1382039, 1382039, 1382039, 1382039, 1382039, 1382039, 1382039, 1382039, 1382039);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1382039, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
                                   if (beauty == 5){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1372034, 1372034, 1372034, 1372034, 1372034, 1372034, 1372034, 1372034, 1372034, 1372034);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1372034, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 6){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1452045, 1452045, 1452045, 1452045, 1452045, 1452045, 1452045, 1452045, 1452045, 1452045);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1452045, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
                                  if (beauty == 7){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1462040, 1462040, 1462040, 1462040, 1462040, 1462040, 1462040, 1462040, 1462040, 1462040);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1462040, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 8){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1472055, 1472055, 1472055, 1472055, 1472055, 1472055, 1472055, 1472055, 1472055, 1472055);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1472055, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 9){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1332055, 1332055, 1332055, 1332055, 1332055, 1332055, 1332055, 1332055, 1332055, 1332055);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1332055, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
                                   if (beauty == 10){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1332056, 1332056, 1332056, 1332056, 1332056, 1332056, 1332056, 1332056, 1332056, 1332056);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1332056, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 11){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1492022, 1492022, 1492022, 1492022, 1492022, 1492022, 1492022, 1492022, 1492022, 1492022);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1492022, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
                                  if (beauty == 12){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1482022, 1482022, 1482022, 1482022, 1482022, 1482022, 1482022, 1482022, 1482022, 1482022);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1482022, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 13){
var item;		
var gain = false;		
for(var i = 0 ; i < GachItem.length ; i++){			
var GItem = GachItem[i];
	
if (Math.floor(Math.random() * GItem[1]) == 0) {
item = cm.gainGachaponItem(GItem[0], 1 , GItem[2]);
gain = true;
break;
      }
      }
      if(!gain){
var itemList = new Array(1342027, 1342027, 1342027, 1342027, 1342027, 1342027, 1342027, 1342027, 1342027, 1342027);
item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
      }
      if (item != -1) {
       cm.gainItem(4032056, -2);
       cm.gainItem(1342027, -1);
       cm.sendOk("#b#t" + item + "##i" + item + ":##r 完成屬性轉化#k");
      } else {
       cm.sendOk("你的背包空間不足。");
      }
      }
		if (beauty == 0){
				if (selection == 0 && cm.getMeso() >= regprice) {
					cm.gainMeso(-regprice);
					cm.gainItem(5152049, 1);
					cm.sendOk("Enjoy!");
				} else if (selection == 1 && cm.getMeso() >= vipprice) {
					cm.gainMeso(-vipprice);
					cm.gainItem(5152049, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
			}
		}
	}
}