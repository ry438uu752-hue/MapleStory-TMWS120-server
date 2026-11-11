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
/* JM from tha Streetz
	Victoria Road: Kerning City (211000100)
	
	
Refining NPC: 
	* Gloves
	* Glove Upgrade
	* Claw
	* Claw Upgrade
	* Processed Wood/Screws

	
* Note: JM by default is used as a Megaphone shop. To move this shop to Frederick in the FM,
	
* following MySQL command:
	* UPDATE `shops` SET `npcid`='9030000' WHERE (`shopid`='0')

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
        
var selStr = "你好, 冰原雪域是個非常寒冷的地方，我想, 你可能需要一雙溫暖的鞋子才能在這裡生存。#b"
        
var options = new Array("製作劍士鞋子","製作弓手鞋子","製作法師鞋子","製作盜賊鞋子");
        
for (var i = 0; i < options.length; i++)
            
selStr += "\r\n#L" + i + "# " + options[i] + "#l";
        
cm.sendSimple(selStr);

    }
    else if (status == 1 && mode == 1) {
        
selectedType = selection;
        
if (selectedType == 0){ //warrior shoes
            
var selStr = "請告訴我你想製作哪種劍士鞋子？#b";
            
var gloves = new Array ("藍十字鞋#k - 劍士 Lv. 60#b","紫十字鞋#k - 劍士 Lv. 60#b","紅十字鞋#k - 劍士 Lv. 60#b","藍飛魂鞋#k - 劍士 Lv. 70#b","紫飛魂鞋#k - 劍士 Lv. 70#b","黑飛魂鞋#k - 劍士 Lv. 70#b","紅水晶靴#k - 劍士 Lv. 80#b","藍水晶靴#k - 劍士 Lv. 80#b","黑水晶靴#k - 劍士 Lv. 80#b");
            
for (var i = 0; i < gloves.length; i++)
                
selStr += "\r\n#L" + i + "# " + gloves[i] + "#l";
            
equip = true;
            
cm.sendSimple(selStr);

        }
        else if (selectedType == 1){ 
//bowman shoes
            
var selStr = "請告訴我你想製作哪種弓手鞋子？#b";
            
var gloves = new Array ("紫花精鞋#k - 弓手 Lv. 60#b","藍花精鞋#k - 弓手 Lv. 60#b","綠花精鞋#k - 弓手 Lv. 60#b","藍箭魂鞋#k - 弓手 Lv. 70#b","黃箭魂鞋#k - 弓手 Lv. 70#b","綠箭魂鞋#k - 弓手 Lv. 70#b","黑箭魂鞋#k - 弓手 Lv. 70#b","藍飛翼鞋#k - 弓手 Lv. 80#b","紅飛翼鞋#k - 弓手 Lv. 80#b","綠飛翼鞋#k - 弓手 Lv. 80#b","黑飛翼鞋#k - 弓手 Lv. 80#b");
            
for (var i = 0; i < gloves.length; i++)
                
selStr += "\r\n#L" + i + "# " + gloves[i] + "#l";
            
equip = true;
            
cm.sendSimple(selStr);

        }
        else if (selectedType == 2){ 
//mage shoes
            
var selStr = "請告訴我你想製作哪種法師鞋子？#b";
            
var claws = new Array ("粉紅天使鞋#k - 法師 Lv. 60#b","綠天使鞋#k   - 法師 Lv. 60#b","黃天使鞋#k   - 法師 Lv. 60#b","藍天使鞋#k   - 法師 Lv. 60#b","藍水晶涼鞋#k - 法師 Lv. 70#b","紅水晶涼鞋#k - 法師 Lv. 70#b","褐水晶涼鞋#k - 法師 Lv. 70#b","金水晶涼鞋#k - 法師 Lv. 70#b","綠密林之鞋#k - 法師 Lv. 80#b","紫密林之鞋#k - 法師 Lv. 80#b","黑密林之鞋#k - 法師 Lv. 80#b");
            
for (var i = 0; i < claws.length; i++){
                
selStr += "\r\n#L" + i + "# " + claws[i] + "#l";

            }
            
equip = true;
            
cm.sendSimple(selStr);

        }
        else if (selectedType == 3){ 
//thief shoes
            
var selStr = "請告訴我你想製作哪種盜賊鞋子？#b";
            
var claws = new Array ("紅無痕之鞋#k - 盜賊 Lv. 60#b","金無痕之鞋#k - 盜賊 Lv. 60#b","黑無痕之鞋#k - 盜賊 Lv. 60#b","紫龍皮鞋#k   - 盜賊 Lv. 70#b","藍龍皮鞋#k   - 盜賊 Lv. 70#b","紅龍皮鞋#k   - 盜賊 Lv. 70#b","綠飛影鞋#k   - 盜賊 Lv. 80#b","紅飛影鞋#k   - 盜賊 Lv. 80#b","黑飛影鞋#k   - 盜賊 Lv. 80#b");
            
for (var i = 0; i < claws.length; i++){
                
selStr += "\r\n#L" + i + "# " + claws[i] + "#l";

            }
            
equip = true;
            
cm.sendSimple(selStr);

        }
        else if (selectedType == 4){ 
//material refine
            
var selStr = "你需要做一些材料？這個部分我可以免費幫你製做#b";
            
var materials = new Array ("你想用樹枝做成木材","你想用木柴做成木材","製做螺絲釘 (一組15個)");
            
for (var i = 0; i < materials.length; i++){
                
selStr += "\r\n#L" + i + "# " + materials[i] + "#l";

            }
            
equip = false;
            
cm.sendSimple(selStr);

        }
        
if (equip)
            
status++;

    }
    else if (status == 2 && mode == 1) {
        
selectedItem = selection;
        
if (selectedType == 4){ 
//material refine
            
var itemSet = new Array (4003001,4003001,4003000);
            
var matSet = new Array(4000003,4000018,new Array (4011000,4011001));
            
var matQtySet = new Array (10,5,new Array (1,1));
            
var costSet = new Array (0,0,0);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
		
        
var prompt = "你想讓我製做 #t" + item + "#? 你想要製做幾組呢?";
		
        
cm.sendGetNumber(prompt,1,1,100)

    }
    else if (status == 3 && mode == 1) {
        
if (equip)

        {
            selectedItem = selection;
            
qty = 1;

        }
        else
            
qty = selection;
        
if (selectedType == 0){ //warrior shoes
            
var itemSet = new Array(1072147,1072148,1072149,1072154,1072155,1072156,1072210,1072211,1072212);
            
var matSet = new Array(new Array(4021008,4011007,4021005,4000030,4003000),new Array(4021008,4011007,4011005,4000030,4003000),new Array(4021008,4011007,4021000,4000030,4003000),new Array(4005000,4005002,4011002,4000048,4003000),new Array(4005000,4005002,4011005,4000048,4003000),new Array(4005000,4005002,4021008,4000048,4003000),new Array(4005000,4005002,4021000,4000030,4003000),new Array(4005000,4005002,4021002,4000030,4003000),new Array(4005000,4005002,4021008,4000030,4003000));
            
var matQtySet = new Array(new Array(1,1,8,80,55),new Array(1,1,8,80,55),new Array(1,1,8,80,55),new Array(1,3,5,100,55),new Array(2,2,5,100,55),new Array(3,1,1,100,55),new Array(2,3,7,90,65),new Array(3,2,7,90,65),new Array(4,1,2,90,65));
            
var costSet = new Array(60000,60000,60000,70000,70000,70000,80000,80000,80000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        else if (selectedType == 1){ //bowman shoes
            
var itemSet = new Array(1072144,1072145,1072146,1072164,1072165,1072166,1072167,1072182,1072183,1072184,1072185);
            
var matSet = new Array(new Array(4011006,4021000,4021007,4000030,4003000),new Array(4011006,4021005,4021007,4000030,4003000),new Array(4011006,4021003,4021007,4000030,4003000),new Array(4005002,4005000,4021005,4000055,4003000),new Array(4005002,4005000,4021004,4000055,4003000),new Array(4005002,4005000,4021003,4000055,4003000),new Array(4005002,4005000,4021008,4000055,4003000),new Array(4005002,4005000,4021002,4000030,4003000),new Array(4005002,4005000,4021000,4000030,4003000),new Array(4005002,4005000,4021003,4000030,4003000),new Array(4005002,4021008,4000030,4003000));
            
var matQtySet = new Array(new Array(5,8,1,75,50),new Array(5,8,1,75,50),new Array(5,8,1,75,50),new Array(1,3,5,100,55),new Array(2,2,5,100,55),new Array(2,2,5,100,55),new Array(3,1,1,100,55),new Array(2,3,7,90,60),new Array(3,2,7,90,60),new Array(4,1,7,90,60),new Array(5,2,90,60));var costSet = new Array(60000,60000,60000,70000,70000,70000,70000,80000,80000,80000,80000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        else if (selectedType == 2){ //mage shoes
            
var itemSet = new Array(1072136,1072137,1072138,1072139,1072157,1072158,1072159,1072160,1072177,1072178,1072179);
            
var matSet = new Array(new Array(4021009,4011006,4011005,4000030,4003000),new Array(4021009,4011006,4021003,4000030,4003000),new Array(4021009,4011006,4011003,4000030,4003000),new Array(4021009,4011006,4021002,4000030,4003000),new Array(4005001,4005003,4021002,4000051,4003000),new Array(4005001,4005003,4021000,4000051,4003000),new Array(4005001,4005003,4011003,4000051,4003000),new Array(4005001,4005003,4011006,4000051,4003000),new Array(4005001,4005003,4021003,4000030,4003000),new Array(4005001,4005003,4021001,4000030,4003000),new Array(4005001,4005003,4021008,4000030,4003000));
            
var matQtySet = new Array(new Array(1,4,5,70,50),new Array(1,4,5,70,50),new Array(1,4,5,70,50),new Array(1,4,5,70,50),new Array(1,3,5,100,55),new Array(2,2,5,100,55),new Array(2,2,5,100,55),new Array(3,1,3,100,55),new Array(2,3,7,85,60),new Array(3,2,7,85,60),new Array(4,1,2,85,60));
            
var costSet = new Array(60000,60000,60000,60000,70000,70000,70000,70000,80000,80000,80000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        else if (selectedType == 3){ //thief shoes
            
var itemSet = new Array (1072150,1072151,1072152,1072161,1072162,1072163,1072172,1072173,1072174);
            
var matSet = new Array(new Array(4021007,4011007,4021000,4000030,4003000),new Array(4021007,4011007,4011006,4000030,4003000),new Array(4021007,4011007,4021008,4000030,4003000),new Array(4005003,4005000,4021001,4000051,4003000),new Array(4005003,4005002,4021005,4000051,4003000),new Array(4005002,4005003,4021000,4000051,4003000),new Array(4005000,4005003,4021003,4000030,4003000),new Array(4005002,4005003,4021000,4000030,4003000),new Array(4005003,4005002,4021008,4000030,4003000));
            
var matQtySet = new Array(new Array(1,1,8,75,50),new Array(1,1,5,75,50),new Array(1,1,1,75,50),new Array(1,3,5,100,55),new Array(1,3,5,100,55),new Array(1,3,5,100,55),new Array(3,2,7,90,60),new Array(3,2,7,90,60),new Array(3,2,7,90,60));var costSet = new Array(60000,60000,60000,70000,70000,70000,80000,80000,80000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

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

        }        else {
            
prompt += "\r\n#i"+mats+"# " + matQty * qty + " #t" + mats + "#";

        }
		
        
if (cost > 0)
            
prompt += "\r\n#i4031138# " + cost * qty + " 楓幣";
		
        
cm.sendYesNo(prompt);

    }
    else if (status == 4 && mode == 1) {
        
var complete = true;
        
if (cm.getMeso() < cost * qty)
        {
            
cm.sendOk("你的楓幣不足");
			
complete = false ;

        }
        else
        {
            
if (mats instanceof Array) {
                
for(var i = 0; complete && i < mats.length; i++)

                {
                    
if (matQty[i] * qty == 1){
                        
if (!cm.haveItem(mats[i])){
                            
complete = false;

                        }

					}else{
						
if (!cm.haveItem(mats[i],matQty[i] * qty)){
							
complete = false;

						}

					}

				}
            }else{
				
if (!cm.haveItem(mats,matQty * qty))
					
complete=false;

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
            
if (item == 4003000)//screws
                
cm.gainItem(4003000, 15 * qty);

            else
                
cm.gainItem(item, qty);
            
//cm.sendOk("完成了!有需要的話可以再來找我。");

}else {
                
cm.sendOk("您的背包空間不足, 請再次確認!");
          }

     }
        
cm.dispose();

    }

}