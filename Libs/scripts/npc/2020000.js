/*
	This file is part of the OdinMS Maple Story Server
    
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		      
 Matthias Butz <matze@odinms.de>
		       
Jan Christian Meyer <vimes@odinms.de>

    T
his program is free software: you can redistribute it and/or modify
    
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
/* Vicious
	Ludibrium Village (211000100)
	
	
Refining NPC: 
	* Bows - 10-40
	* Crossbows - 12-50
	* Archer Gloves - 10-60 + upgrades
	
* Processed Wood/Screws
	* Arrows/Bronze Arrows/Steel Arrows

*/

var status = -1;

var selectedType = -1;

var selectedItem = -1;

var item;

var items;

var mats;

var matQty;

var cost;

var qty = 1;

var equip;


function start() {
    
var selStr = "年輕人 ! 在這冰冷的雪地裡能夠遇見我算是你的福氣, 只要有合適的材料沒有什麼是我做不出來的！#b"
    
var options = ["製作礦石","製作寶石","製作稀有寶石","製作水晶","製作箭矢","製作材料"];
    
for (var i = 0; i < options.length; i++)
        
selStr += "\r\n#L" + i + "# " + options[i] + "#l";
    
cm.sendSimple(selStr);

}


function action(mode, type, selection) {
    
status++;

    if (mode != 1){
        
cm.dispose();
        
return;

    }
    if (status == 0) {
        
if (selection == 0) { 
//bow refine
           
 var selStr = "請告訴我你想提煉哪種礦石？#b";
            
items = [4011000,4011001,4011002,4011003,4011004,4011005,4011006];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 1) { 
//xbow refine
            
var selStr = "請告訴我你想提煉哪種寶石？#b";
            
items = [4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 2) { 
//glove refine
            
var selStr = "你想要製作哪一個呢？#b";
            
items = [4011007,4021009];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 3) { 
//glove upgrade
            
var selStr = "你想要製作哪種水晶呢？#b";
            
items = [4005000,4005001,4005002,4005003,4005004];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 8) { 
//material refine
            
var selStr = "XXX你需要做一些材料？這個部分我可以免費幫你製做#b";
            
var materials = ["你想用樹枝做成木材","你想用木柴做成木材","製做螺絲釘 (一組15個)"];
            
for (var i = 0; i < materials.length; i++)
                
selStr += "\r\n#L" + i + "# " + materials[i] + "#l";

        }else if (selection == 4) { 
//arrow refine
            
var selStr = "你想製做哪種箭矢#b";
            
items = [2060000,2061000,2060001,2061001,2060002,2061002];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 5) { 
//glove upgrade
            
var selStr = "你需要做一些材料？這個部分我可以免費幫你製做#b";
            
items = [4003001,4003001,4003000];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }
        selectedType = selection;
        
cm.sendSimple(selStr);
        
if (selection != 8)
            
status++;

    }else if (status == 1) {
        
selectedItem = selection;
        
items = [4003001,4003001,4003000];
        
var matSet = [4000003,4000018, [4011000,4011001]];
        
var matQtySet = [10,5,[1,1]];
        
item = items[selection];
        
mats = matSet[selection];
        
matQty = matQtySet[selection];
        
cost = 0;
        
cm.sendGetNumber("你想讓我製做 #t" + item + "#? 你想要製做幾組呢?",1,1,100)

    }else if (status == 2) {
        
if (selectedType != 8)
            
selectedItem = selection;

        else
            
qty = selection;
        
if (selectedType == 0) { 
//bow refine
            
var matSet = [[4010000,4010000],[4010001,4010001],[4010002,4010002],[4010003,4010003],[4010004,4010004],[4010005,4010005],[4010006,4010006]];
            
var matQtySet = [[5,5],[5,5],[5,5],[5,5],[5,5],[5,5],[5,5]];
            
var costSet = [300,300,300,500,500,500,800];

        }else if (selectedType == 1) { 
//xbow refine
            
var matSet = [[4020000,4020000],[4020001,4020001],[4020002,4020002],[4020003,4020003],[4020004,4020004],[4020005,4020005],[4020006,4020006],[4020007,4020007],[4020008,4020008]];
            
var matQtySet = [[5,5],[5,5],[5,5],[5,5],[5,5],[5,5],[5,5],[5,5],[5,5]];
            
var costSet = [500,500,500,500,500,500,500,1000,3000];

        }else if (selectedType == 2) { 
//glove refine
            
var matSet = [[4011000,4011001,4011002,4011003,4011004,4011005,4011006],[4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008]];
            
var matQtySet = [[1,1,1,1,1,1,1],[1,1,1,1,1,1,1,1,1]];
            
var costSet = [9000,13500];

        }else if (selectedType == 3) { 
//glove upgrade
            
var matSet = [[4004000,4004000],[4004001,4004001],[4004002,4004002],[4004003,4004003],[4004004,4004004]];
            
var matQtySet = [[5,5],[5,5],[5,5],[5,5],[5,5]];
            
var costSet = [5000,5000,5000,5000,100000];

        }else if (selectedType == 4) { 
//arrow refine
            
var matSet = [[4003001,4003004],[4003001,4003004],[4011000,4003001,4003004],[4011000,4003001,4003004],[4011001,4003001,4003005],[4011001,4003001,4003005]];
            
var matQtySet = [[1,1],[1,1],[1,3,10],[1,3,10],[1,5,15],[1,5,15]];
            
var costSet = [0,0,0,0,0,0]

        }else if (selectedType == 5) { 
//glove upgrade
            
var matSet = [[4000003,4031138],[4000018,4031138],[4011000,4011001]];
            
var matQtySet = [[10,0],[5,0],[1,1]];
            
var costSet = [0,0,0];

        }
        if(selectedType != 8){
            
item = items[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        
var prompt = "你想讓我製做 ";
        
if (qty == 1)
            
prompt += " 1 個 #b#t" + item + "##k ?";

        else
            
prompt += qty + " #t" + item + "#?";
        
prompt += " 在製做之前，我需要你準備一些原料，我才能完成作品。請再次確認你的材料!#b";
        
if (mats instanceof Array)
            
for(var i = 0; i < mats.length; i++)
                
prompt += "\r\n#i" + mats[i] + "# " + (matQty[i] * qty) + " #t" + mats[i] + "#";

        else
            
prompt += "\r\n#i" + mats + "# " + (matQty * qty) + " #t" + mats + "#";
        
if (cost > 0)
            
prompt += "\r\n#i4031138# " + (cost * qty) + " 楓幣";
        
cm.sendYesNo(prompt);

    }else if (status == 3) {
        
var complete = true;
        
if (cm.getMeso() < (cost * qty)){
            
cm.sendOk("你的楓幣不足");
			
complete = false;

        }else{
            
if (mats instanceof Array) {
                
for(var i = 0; complete && i < mats.length; i++)
                    
if (!cm.haveItem(mats[i], matQty[i]))
                        
complete = false;

            }else if (!cm.haveItem(mats, matQty))
                
complete = false;

        }	
        
if (!complete){
            
cm.sendOk("您的材料不足, 請再次確認!");

		}
        else {
            
if (cm.canHold(item)) {
                
if (mats instanceof Array) {
                    
for (var i = 0; i < mats.length; i++)
                        
cm.gainItem(mats[i], -(matQty[i] * qty));

                }else
                    
cm.gainItem(mats, -(matQty * qty));
                
cm.gainMeso(-(cost * qty));
                
if (item >= 2060000 && item <= 2060002) 
//bow arrows
                    
cm.gainItem(item, 1000 - (item - 2060000) * 0);
                
else if (item >= 2061000 && item <= 2061002) 
//xbow arrows
                    
cm.gainItem(item, 1000 - (item - 2061000) * 0);

                else if (item == 4003000)
//screws
                    
cm.gainItem(4003000, 15 * qty);

                else
                    
cm.gainItem(item, qty);
                
cm.sendOk("完成了!有需要的話可以再來找我。");

            }else {
                
cm.sendOk("您的背包空間不足, 請再次確認!");

            }

        }
        
cm.dispose();

    }

}