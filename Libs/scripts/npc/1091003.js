/*
	Serryl (1091003)
	Location: The Nautilus

*/

function start() {
    
status = -1;
    
action(1, 0, 0);

}

function action(mode, type, selection) {
    
if (mode == 1)
	status++;
    else
	cm.dispose();
    
if (status == 0 && mode == 1) {
	
var selStr = "什麼 ？你想製造自己的武器和手套 ？20年來我一直是個海盜，我為這裡的船員做了許許多多各式各樣的物品。所以基本上以我的經驗來說, 只要有充足的材料, 我都能製作出讓你滿意的物品 ！#b";
	
var options = new Array("製作指虎","製作手槍","製作手套");
	
for (var i = 0; i < options.length; i++){
	    
selStr += "\r\n#L" + i + "# " + options[i] + "#l";

	}
			
	
cm.sendSimple(selStr);

    }
    else if (status == 1 && mode == 1) {
	
selectedType = selection;
	
if (selectedType == 0){ //Making a Knuckler
	    
var selStr = "請告訴我你想製作哪種指虎 ？#b";
	    
var knucklers = new Array("加固皮護手#k       - 海盜 Lv. 15#b","雙棘指虎#k         - 海盜 Lv. 20#b","諾曼指虎#k         - 海盜 Lv. 25#b","精鋼拳套#k         - 海盜 Lv. 30#b","利刃拳套#k         - 海盜 Lv. 35#b","尼奧札德毀滅拳套#k - 海盜 Lv. 40#b","惡魔之爪#k         - 海盜 Lv. 50#b");
	    
for (var i = 0; i < knucklers.length; i++){
		
selStr += "\r\n#L" + i + "# " + knucklers[i] + "#l";

	    }
	    
equip = true;
	    
cm.sendSimple(selStr);

	}
	else if (selectedType == 1){ //Making a Gun
	    
var selStr = "請告訴我你想製作哪種手槍 ？#b";
	    
var guns = new Array("迪林格火槍#k   - 海盜 Lv. 15#b","格蘭德火槍#k   - 海盜 Lv. 20#b","黃金火槍#k     - 海盜 Lv. 25#b","冷酷之心#k     - 海盜 Lv. 30#b","流星#k         - 海盜 Lv. 35#b","銀月神射#k     - 海盜 Lv. 40#b","拉斯菲特之槍#k - 海盜 Lv. 50#b");
	    
for (var i = 0; i < guns.length; i++){
		
selStr += "\r\n#L" + i + "# " + guns[i] + "#l";

	    }
	    
equip = true;
	    
cm.sendSimple(selStr);

	}
	else if (selectedType == 2){ //Making a pair of pirate gloves
	    
var selStr = "請告訴我你想製作哪種手套 ？#b";
	    
var gloves = new Array ("綠色皮手套#k     - 海盜 Lv. 15#b","褐色皮革手腕#k   - 海盜 Lv. 20#b","褐色堅固皮手腕#k - 海盜 Lv. 25#b","金色護手#k       - 海盜 Lv. 30#b","褐色寶石手套#k   - 海盜 Lv. 35#b","褐色瑞恩手腕#k   - 海盜 Lv. 40#b","褐色羅伊斯手套#k - 海盜 Lv. 50#b","黑色舒爾特手套#k - 海盜 Lv. 60#b");
	    
for (var i = 0; i < gloves.length; i++){
		
selStr += "\r\n#L" + i + "# " + gloves[i] + "#l";

	    }
	    
equip = true;
	    
cm.sendSimple(selStr);

	}
	
if (equip)
	    
status++;

    }
    else if (status == 3 && mode == 1) {
	if (equip)
	{
	    
selectedItem = selection;
	    
qty = 1;

	}
	
else
	    
qty = selection;

	
if (selectedType == 0){ //Making a Knuckler
	    
var itemSet = new Array(1482001, 1482002, 1482003, 1482004, 1482005, 1482006, 1482007);
	    
var matSet = new Array(4000021, new Array(4011001,4011000,4000021,4003000), new Array(4011000,4011001,4003000), new Array(4011000,4011001,4000021,4003000), new Array(4011000,4011001,4000021,4003000), new Array(4011000,4011001,4021000,4000021,4003000), new Array(4000039,4011000,4011001,4000030,4000021,4003000));
	    
var matQtySet = new Array(20, new Array(1,1,10,5), new Array(2,1,10), new Array(1,1,30,10), new Array(2,2,30,20), new Array(1,1,2,50,20), new Array(150,1,2,20,20,20));
	    
var costSet = new Array(1000,2000,5000,15000,30000,50000,100000);
	    
var levelLimitSet = new Array(15,20,25,30,35,40,50);
	    
item = itemSet[selectedItem];
	    
mats = matSet[selectedItem];
	    
matQty = matQtySet[selectedItem];
	    
cost = costSet[selectedItem];
	    
levelLimit = levelLimitSet[selectedItem];

	}
	else if (selectedType == 1){ //Making a Gun
	    
var itemSet = new Array(1492001, 1492002, 1492003, 1492004, 1492005, 1492006, 1492007);
	    
var matSet = new Array(new Array(4011000,4003000,4003001), new Array(4011000,4003000,4003001,4000021), new Array(4011000,4003000), new Array(4011001,4000021,4003000), new Array(4011006,4011001,4000021,4003000), new Array(4011004,4011001,4000021,4003000), new Array(4011006,4011004,4011001,4000030,4003000));
	    
var matQtySet = new Array(new Array(1,5,1), new Array(1,10,5,10), new Array(2,10), new Array(2,10,10), new Array(10,2,5,10), new Array(1,2,10,20), new Array(1,2,4,30,30));
	    
var costSet = new Array (1000,2000,5000,15000,30000,50000,100000);
	    
var levelLimitSet = new Array(15,20,25,30,35,40,50);
	    
item = itemSet[selectedItem];
	    
mats = matSet[selectedItem];
	    
matQty = matQtySet[selectedItem];
	    
cost = costSet[selectedItem];
	    
levelLimit = levelLimitSet[selectedItem];

	}
	else if (selectedType == 2){ //Making a pair of pirate gloves
	    
var itemSet = new Array(1082180, 1082183, 1082186, 1082189, 1082192, 1082195, 1082198, 1082201);
	    
var matSet = new Array(new Array(4000021,4021003),4000021,new Array(4011000,4000021),new Array(4021006,4000021,4003000),new Array(4011000,4000021,4003000),new Array(4000021,4011000,4011001,4003000),new Array(4011000,4000021,4000030,4003000),new Array(4011007,4021008,4021007,4000030,4003000));
	    
var matQtySet = new Array(new Array(15,1),35,new Array(2,20),new Array(2,50,10),new Array(3,60,15),new Array(80,3,3,25),new Array(3,20,40,30),new Array(1,1,1,50,50));
	    
var costSet = new Array(1000,8000,15000,25000,30000,40000,50000,70000);
	    
var levelLimitSet = new Array(15,20,25,30,35,40,50,60);
	    
item = itemSet[selectedItem];
	    
mats = matSet[selectedItem];
	    
matQty = matQtySet[selectedItem];
	    
cost = costSet[selectedItem];
	    
levelLimit = levelLimitSet[selectedItem];

	}
			
	
var prompt = "你想讓我製作";
        
if (qty == 1)
            
prompt += " 1 個 #b#t" + item + "##k ?";

        else
            
prompt += qty + " #t" + item + "#?";
			
       
prompt += " 在製作之前，我需要你準備一些原料，我才能完成作品。請再次確認你的材料!#b";

if (mats instanceof Array){
	    
for(var i = 0; i < mats.length; i++){
		
prompt += "\r\n#i"+mats[i]+"# " + matQty[i] * qty + " #t" + mats[i] + "#";

	    }

	}
	else {
	    prompt += "\r\n#i"+mats+"# " + matQty * qty + " #t" + mats + "#";

	}
		
	
if (cost > 0)
	    
prompt += "\r\n#i4031138# " + cost * qty + " 楓幣";
		
	
cm.sendYesNo(prompt);

    }
    else if (status == 4 && mode == 1) {
	var pass = false;
		
	
if (cm.getMeso() < cost * qty) {
	    
cm.sendOk("你的楓幣不足")
	    
cm.dispose();
	    
return;

	} else {
	    
if (mats instanceof Array) {
		
for (var i = 0; i < mats.length; i++) {
		    
pass = cm.haveItem(mats[i], matQty[i] * qty);
		    
if (!pass) {
			
break;

		    }

		}

	    } else {
		
pass = cm.haveItem(mats, matQty * qty);

	    }
	
        }
			
	
if (pass == false)
	    
cm.sendNext("您的材料不足, 請再次確認!");

	else {
	    
if (cm.canHold(item)) {
if (mats instanceof Array) {
		
for (var i = 0; i < mats.length; i++){
		    
cm.gainItem(mats[i], -matQty[i] * qty);

		}

	    }
	    else
		
cm.gainItem(mats, -matQty * qty);
					
	    
if (cost > 0)
		
cm.gainMeso(-cost * qty);
				
	    
if (item == 4003000)//screws
		
cm.gainItem(4003000, 15 * qty);

	    else
		
cm.gainItem(item, qty);
	    
cm.sendOk("完成了! 如果你還要的話, 可以再來找我。");

}else {
                
cm.sendOk("您的背包空間不足, 請再次確認!");
          }

     }
        
cm.dispose();

    }

}