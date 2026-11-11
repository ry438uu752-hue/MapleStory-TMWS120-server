/*
	This file is part of the OdinMS Maple Story Server
    
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
					   
Matthias Butz <matze@odinms.de>
					   
Jan Christian Meyer <vimes@odinms.de>

    
This program is free software: you can redistribute it and/or modify
    
it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    
the Free Software Foundation. You may not use, modify or distribute
    
this program under any other version of the GNU Affero General Public
    License.

    
This program is distributed in the hope that it will be useful,
    
but WITHOUT ANY WARRANTY; without even the implied warranty of
    
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    
GNU Affero General Public License for more details.

    
You should have received a copy of the GNU Affero General Public License
    
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/


/* Pi
	Ludibrium Village (220000300)
	
	
Refining NPC: 
	
* Minerals
	
* Jewels
	
* Moon/Star Rocks
	
* Crystals (minus Dark)
	
* Processed Wood/Screws
	
* Arrows/Bronze Arrows/Steel Arrows

*/

var status = 0;

var selectedType = -1;

var selectedItem = -1;

var item;

var mats;

var matQty;

var cost;

var qty;

var equip;


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
        
var selStr = "什麼? 你不知道我是誰? 我可是玩具城技術最好的匠工呢 ! 只要有適合的材料, 我都能製作出你想要的東西!#b"
        
var options = new Array("製作礦石","製作寶石","製作稀有寶石","製作水晶","製作箭矢");
	for (var i = 0; i < options.length; i++){
	    selStr += "\r\n#L" + i + "# " + options[i] + "#l";
	}	
	cm.sendSimple(selStr);
    } else if (status == 1) {
	selectedType = selection;
	if (selectedType == 0){ //mineral refine
	    var selStr = "你想要提煉哪種礦石呢？#b";
	    var minerals = new Array ("青銅","鋼鐵","鋰礦石","朱礦石","銀","紫礦石","黃金");
	    for (var i = 0; i < minerals.length; i++){
		selStr += "\r\n#L" + i + "# " + minerals[i] + "#l";
	    }
	    equip = false;
	    cm.sendSimple(selStr);
	} else if (selectedType == 1){ //jewel refine
	    var selStr = "你想要提煉哪種寶石呢？#b";
	    var jewels = new Array ("石榴石","紫水晶","海藍寶石","祖母綠","蛋白石","藍寶石","黃晶","鑽石","黑水晶");
	    for (var i = 0; i < jewels.length; i++){
		selStr += "\r\n#L" + i + "# " + jewels[i] + "#l";
	    }
	    equip = false;
	    cm.sendSimple(selStr);
	} else if (selectedType == 2){ //rock refine
	    var selStr = "你想要製作哪一個呢？#b";
	    var items = new Array ("月石","星石");
	    for (var i = 0; i < items.length; i++){
		selStr += "\r\n#L" + i + "# " + items[i] + "#l";
	    }
	    equip = false;
	    cm.sendSimple(selStr);
	} else if (selectedType == 3){ //crystal refine
	    var selStr = "你想要製作哪種水晶呢？#b";
	    var crystals = new Array ("力量水晶","智慧水晶","敏捷水晶","幸運水晶");
	    for (var i = 0; i < crystals.length; i++){
		selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";
	    }
	    equip = false;
	    cm.sendSimple(selStr);
	} else if (selectedType == 99){ //material refine
	    var selStr = "你需要做一些材料？這個部分我可以免費幫你製做#b";
	    var materials = new Array ("你想用樹枝做成木材","你想用木柴做成木材","製做螺絲釘 (一組15個)");
	    for (var i = 0; i < materials.length; i++){
		selStr += "\r\n#L" + i + "# " + materials[i] + "#l";
	    }
	    equip = false;
	    cm.sendSimple(selStr);
	} else if (selectedType == 4){ //arrow refine
	    var selStr = "你想製做哪種箭矢#b";
	    var arrows = new Array ("箭矢","弩箭矢","青銅箭矢","青銅弩箭矢","鋼鐵箭矢","鋼鐵弩箭矢");
	    for (var i = 0; i < arrows.length; i++){
		selStr += "\r\n#L" + i + "# " + arrows[i] + "#l";
	    }
	    equip = true;
	    cm.sendSimple(selStr);
	}
	if (equip)
	    status++;
    } else if (status == 2) {
	selectedItem = selection;
	if (selectedType == 0){ //mineral refine
	    var itemSet = new Array(4011000,4011001,4011002,4011003,4011004,4011005,4011006);
	    var matSet = new Array(4010000,4010001,4010002,4010003,4010004,4010005,4010006);
	    var matQtySet = new Array(10,10,10,10,10,10,10);
	    var costSet = new Array(270,270,270,450,450,450,720);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
	else if (selectedType == 1){ //jewel refine
	    var itemSet = new Array(4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008);
	    var matSet = new Array(4020000,4020001,4020002,4020003,4020004,4020005,4020006,4020007,4020008);
	    var matQtySet = new Array(10,10,10,10,10,10,10,10,10);
	    var costSet = new Array (450,450,450,450,450,450,450,900,2700);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
	else if (selectedType == 2){ //rock refine
	    var itemSet = new Array(4011007,4021009);
	    var matSet = new Array(new Array(4011000,4011001,4011002,4011003,4011004,4011005,4011006), new Array(4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008));
	    var matQtySet = new Array(new Array(1,1,1,1,1,1,1),new Array(1,1,1,1,1,1,1,1,1));
	    var costSet = new Array(9000,13500);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
	else if (selectedType == 3){ //crystal refine
	    var itemSet = new Array (4005000,4005001,4005002,4005003);
	    var matSet = new Array(4004000,4004001,4004002,4004003);
	    var matQtySet = new Array (10,10,10,10);
	    var costSet = new Array (4500,4500,4500,4500);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
	else if (selectedType == 99){ //material refine
	    var itemSet = new Array (4003001,4003001,4003000);
	    var matSet = new Array(4000003,4000018,new Array (4011000,4011001));
	    var matQtySet = new Array (10,5,new Array (1,1));
	    var costSet = new Array (0,0,0);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
		
	var prompt = "你是說你想要讓我製做 #b#t" + item + "##k ? 你想要製做幾個呢 ?";
		
	cm.sendGetNumber(prompt,1,1,100)
    } else if (status == 3) {
	if (equip) {
	    selectedItem = selection;
	    qty = 1;
	} else
	    qty = selection;

	if (selectedType == 4){ //arrow refine
	    var itemSet = new Array(2060000,2061000,2060001,2061001,2060002,2061002);
	    var matSet = new Array(new Array (4003001,4003004),new Array (4003001,4003004),new Array (4011000,4003001,4003004),new Array (4011000,4003001,4003004),
		new Array (4011001,4003001,4003005),new Array (4011001,4003001,4003005));
	    var matQtySet = new Array (new Array (1,1),new Array (1,1),new Array (1,3,10),new Array (1,3,10),new Array (1,5,15),new Array (1,5,15));
	    var costSet = new Array (0,0,0,0,0,0);
	    item = itemSet[selectedItem];
	    mats = matSet[selectedItem];
	    matQty = matQtySet[selectedItem];
	    cost = costSet[selectedItem];
	}
		
	var prompt = "你想讓我製做 ";
	if (qty == 1) {
            prompt += " 1 個 #b#t" + item + "##k ?";
	} else {
	    prompt += qty + " #t" + item + "#?";
	}
	prompt += " 在製做之前，我需要你準備一些原料，我才能完成作品。請再次確認你的材料!#b";
		
	if (mats instanceof Array){
	    for (var i = 0; i < mats.length; i++) {
		prompt += "\r\n#i"+mats[i]+"# " + matQty[i] * qty + " #t" + mats[i] + "#";
	    }
	} else {
	    prompt += "\r\n#i"+mats+"# " + matQty * qty + " #t" + mats + "#";
	}
	if (cost > 0) {
	    prompt += "\r\n#i4031138# " + cost * qty + " 楓幣";
	}
	cm.sendYesNo(prompt);
    } else if (status == 99) {
	var complete = false;
		
	if (cm.getMeso() < cost * qty) {
	    cm.sendOk("你的楓幣不足")
	    cm.dispose();
	    return;
	} else {
	    if (mats instanceof Array) {
		for (var i = 0; i < mats.length; i++) {
		    complete = cm.haveItem(mats[i], matQty[i] * qty);
		    if (!complete) {
			break;
		    }
		}
	    } else {
		complete = cm.haveItem(mats, matQty * qty);
	    }	
        }
			
	if (!complete)
	    cm.sendOk("您的材料不足, 請再次確認!");
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
				
	    if (item >= 2060000 && item <= 2060002) //bow arrows
		cm.gainItem(item, 1000 - (item - 2060000) * 0);
	    else if (item >= 2061000 && item <= 2061002) //xbow arrows
		cm.gainItem(item, 1000 - (item - 2061000) * 0);
	    else if (item == 4003000)//screws
		cm.gainItem(4003000, 15 * qty);
	    else
		cm.gainItem(item, qty);
	    cm.sendOk("完成了!有需要的話可以再來找我。");
            }else {
                
            cm.sendOk("您的背包空間不足, 請再次確認!");
	}
  }
	cm.safeDispose();
    }
}