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
    along with this program.  
If not, see <http://www.gnu.org/licenses/>.
*/

/* Mr. Smith
	Victoria Road: Perion (102000000)
	
	
Refining NPC: 
	* Warrior Gloves - 10-60 + upgrades
	* Processed Wood/Screws
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
        
var selStr = "呃...嗨，我是辛德先生的徒弟。 他年紀大了，所以一些簡易的工作都交給我處理。 我能為你做些什麼嗎？#b"
        
var options = new Array("製作手套","合成手套","製作材料");
        
for (var i = 0; i < options.length; i++){
            selStr += "\r\n#L" + i + "# " + options[i] + "#l";

        }
			
        
cm.sendSimple(selStr);

    }
    else if (status == 1 && mode == 1) {
        
selectedType = selection;
        
if (selectedType == 0){ //glove refine
            
var selStr = "請告訴我你想製作哪種手套#b";
            
var items = new Array ("腕甲#k         - 劍士 Lv. 10#b","鋼製短手套#k   - 劍士 Lv. 15#b","皮手套#k       - 劍士 Lv. 20#b","白紋短手套#k   - 劍士 Lv. 25#b",

                "青銅機器手套#k - 劍士 Lv. 30#b","鐵製輕便手套#k - 劍士 Lv. 35#b","鋼鐵長手套#k   - 劍士 Lv. 40#b","鋼鐵合金手套#k - 劍士 Lv. 50#b","青銅戰鬥手套#k - 劍士 Lv. 60#b");
            
for (var i = 0; i < items.length; i++){
                
selStr += "\r\n#L" + i + "# " + items[i] + "#l";

            }
            
cm.sendSimple(selStr);
            
equip = true;

        }
        else if (selectedType == 1){ //glove upgrade
            
var selStr = "合成手套？ 這應該不會太難。 你想要合成哪種手套#b";
            
var crystals = new Array ("鋼製機器手套#k - 劍士 Lv. 30#b","紫礦機器手套#k - 劍士 Lv. 30#b","黃輕便手套#k   - 劍士 Lv. 35#b","黑輕便手套#k   - 劍士 Lv. 35#b",

                "朱礦長手套#k   - 劍士 Lv. 40#b","黑色長手套#k   - 劍士 Lv. 40#b","鋰礦合金手套#k - 劍士 Lv. 50#b","黃金合金手套#k - 劍士 Lv. 50#b",

                "藍戰鬥手套#k   - 劍士 Lv. 60#b","黑戰鬥手套#k   - 劍士 Lv. 60#b");
            
for (var i = 0; i < crystals.length; i++){
                
selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";

            }
            
cm.sendSimple(selStr);
            
equip = true;

        }
		else if (selectedType == 2){
 //shield refine

var selStr = "你需要做一些材料？這個部分我可以免費幫你製做？#b";

var shields = new Array ("你想用樹枝做成木材","你想用木柴做成木材","製做螺絲釘 (一組15個)");

            for (var i = 0;
 i < shields.length; i++){

                selStr += "\r\n#L" + i + "# " + shields[i] + "#l";

            }
            
        cm.sendSimple(selStr);

            equip = true;

        }
        if (equip)
            status++;

    }
    else if (status == 2 && mode == 1) {
        
selectedItem = selection;
        
if (selectedType == 5){ 
//material refine
            
var itemSet = new Array (4003001,4003001,4003000);
            
var matSet = new Array(4000003,4000018,new Array (4011000,4011001));
            
var matQtySet = new Array (10,5,new Array (1,1));
            
var costSet = new Array (0,0,0)
            
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

        
if (selectedType == 0){ 
//glove refine
            
var itemSet = new Array(1082003,1082000,1082004,1082001,1082007,1082008,1082023,1082009,1082059);
            
var matSet = new Array(new Array(4000021,4011001),4011001,new Array(4000021,4011000),4011001,new Array(4011000,4011001,4003000),new Array(4000021,4011001,4003000),new Array(4000021,4011001,4003000),

                new Array(4011001,4021007,4000030,4003000),new Array(4011007,4011000,4011006,4000030,4003000));
            
var matQtySet = new Array(new Array(15,1),2,new Array(40,2),2,new Array(3,2,15),new Array(30,4,15),new Array(50,5,40),new Array(3,2,30,45),new Array(1,8,2,50,50));
            
var costSet = new Array(1000,2000,5000,10000,20000,30000,40000,50000,70000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        else if (selectedType == 1){ 
//glove upgrade
            
var itemSet = new Array(1082005,1082006,1082035,1082036,1082024,1082025,1082010,1082011,1082060,1082061);
            
var matSet = new Array(new Array(1082007,4011001),new Array(1082007,4011005),new Array(1082008,4021006),new Array(1082008,4021008),new Array(1082023,4011003),new Array(1082023,4021008),

                new Array(1082009,4011002),new Array(1082009,4011006),new Array(1082059,4011002,4021005),new Array(1082059,4021007,4021008));
            
var matQtySet = new Array (new Array(1,1),new Array(1,2),new Array(1,3),new Array(1,1),new Array(1,4),new Array(1,2),new Array(1,5),new Array(1,4),new Array(1,3,5),new Array(1,2,2));       
var costSet = new Array (20000,25000,30000,40000,45000,50000,55000,60000,70000,80000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
		else if (selectedType == 2){
 //shield refine
            
var itemSet = new Array (4003001,4003001,4003000);
            
var matSet = new Array(new Array (4000003,4031138),
                       new Array (4000018,4031138),
                       new Array (4011000,4011001));
            
var matQtySet = new Array (new Array (10,0),new Array (5,0),new Array (1,1));
            
var costSet = new Array (0,0,0);
            
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

        }
        else {
            
prompt += "\r\n#i"+mats+"# " + matQty * qty + " #t" + mats + "#";

        }
		
        
if (cost > 0)
            
prompt += "\r\n#i4031138# " + cost * qty + " 楓幣";
		
        
cm.sendYesNo(prompt);

    }
    else if (status == 4 && mode == 1) {
        
var complete = true;
		
if(qty<0){
			
cm.dispose();

		}
        
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
                    
if (matQty[i] * qty == 1)	{
                        
if (!cm.haveItem(mats[i]))
                        {
                            
complete = false;

                        }

                    } else {

                        
if (!cm.haveItem(mats[i],matQty[i]*qty)){
							
complete=false;

						}

                    }

                }

            }
            else {

                
if (!cm.haveItem(mats,matQty)) complete=false;

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
            
cm.sendOk("完成了! 如果你還要的話, 可以再來找我。");
}else {
                
cm.sendOk("您的背包空間不足, 請再次確認!");
          }

     }
        
cm.dispose();

    }

}