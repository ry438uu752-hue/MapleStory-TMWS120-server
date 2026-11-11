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
	Ludibrium Village (101000000)
	
	
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
        
var selStr = "歡迎來到魔法森林，我是易德。有什麼需要我為您服務的嗎？#b"
        
var options = new Array("製作手套","合成手套","合成帽子","製作短杖","製作長杖");
        
for (var i = 0; i < options.length; i++){
            
selStr += "\r\n#L" + i + "# " + options[i] + "#l";

        }
			
        
cm.sendSimple(selStr);

    }
    else if (status == 1 && mode == 1) {
        
selectedType = selection;
        
if (selectedType == 0){ //製作手套
            
var selStr = "請告訴我你想製作哪種手套？#b";
            
var items = new Array ("魔法手套#k     - 法師 Lv. 15#b","藍色梅林手套#k - 法師 Lv. 20#b","藍賢者手套#k   - 法師 Lv. 25#b","紅守護手套#k   - 法師 Lv. 30#b","紅神聖手套#k   - 法師 Lv. 35#b","紅精靈手套#k   - 法師 Lv. 40#b","紅元素手套#k   - 法師 Lv. 50#b","鋼鐵封印手套#k - 法師 Lv. 60#b");
            
for (var i = 0; i < items.length; i++){
                
selStr += "\r\n#L" + i + "# " + items[i] + "#l";

            }
            
cm.sendSimple(selStr);
            
equip = true;

        }
        else if (selectedType == 1){ //合成手套
            
var selStr = "請告訴我你想合成哪種手套？#b";
            
var crystals = new Array ("綠色梅林手套#k - 法師 Lv. 20#b","紫色梅林手套#k - 法師 Lv. 20#b","紅賢者手套#k   - 法師 Lv. 25#b","黑賢者手套#k   - 法師 Lv. 25#b","藍守護手套#k   - 法師 Lv. 30#b","黑守護手套#k   - 法師 Lv. 30#b","藍神聖手套#k   - 法師 Lv. 35#b","黑神聖手套#k   - 法師 Lv. 35#b","藍精靈手套#k   - 法師 Lv. 40#b","黑精靈手套#k   - 法師 Lv. 40#b","藍元素手套#k   - 法師 Lv. 50#b","黑元素手套#k   - 法師 Lv. 50#b","黃金封印手套#k - 法師 Lv. 60#b","黑封印手套#k   - 法師 Lv. 60#b");
            
for (var i = 0; i < crystals.length; i++){
                
selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";

            }
            
cm.sendSimple(selStr);
            
equip = true;
        }
        else if (selectedType == 2){ //合成帽子
            
var selStr = "請告訴我你想合成哪種帽子？#b";
            
var crystals = new Array ("孫悟空鋼製頭箍#k - 法師 Lv. 30#b","孫悟空頭箍#k     - 法師 Lv. 30#b");
            
for (var i = 0; i < crystals.length; i++){
                
selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";
            }
            
cm.sendSimple(selStr);
            
equip = true;
        }
        else if (selectedType == 3){ //製作短杖
            
var selStr = "請告訴我你想製作哪種短杖？#b";
            
var crystals = new Array ("木製短杖#k     - 法師 Lv. 8#b","高級木製短杖#k - 法師 Lv. 13#b","金屬短杖#k     - 法師 Lv. 18#b","冰精短杖#k     - 法師 Lv. 23#b","鋰礦短杖#k     - 法師 Lv. 28#b","法師短杖#k     - 法師 Lv. 33#b","妖精短杖#k     - 法師 Lv. 38#b","大魔法師短杖#k - 法師 Lv. 48#b");
            
for (var i = 0; i < crystals.length; i++){
                
selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";
            }
            
cm.sendSimple(selStr);
            
equip = true;
        }
        else if (selectedType == 4){ //製作長杖
            
var selStr = "請告訴我你想製作哪種長杖？#b";
            
var crystals = new Array ("木製長杖#k   - 法師 Lv. 10#b","藍寶石長杖#k - 法師 Lv. 15#b","祖母綠長杖#k - 法師 Lv. 15#b","古樹長杖#k   - 法師 Lv. 20#b","法師長杖#k   - 法師 Lv. 25#b","精靈長杖#k   - 法師 Lv. 45#b");
            
for (var i = 0; i < crystals.length; i++){
                
selStr += "\r\n#L" + i + "# " + crystals[i] + "#l";
            }
            
cm.sendSimple(selStr);
            
equip = true;

        }
        else if (selectedType == 8){ 
//material refine
            
var selStr = "你需要做一些材料？這個部分我可以免費幫你製做#b";
            
var materials = new Array ("你想用樹枝做成木材","你想用木柴做成木材","製做螺絲釘 (一組15個)");
            
for (var i = 0; i < materials.length; i++){
                
selStr += "\r\n#L" + i + "# " + materials[i] + "#l";

            }
            
cm.sendSimple(selStr);
            
equip = false;

        }
        if (equip)
            status++;

    }
    else if (status == 2 && mode == 1) {
        
selectedItem = selection;
        
if (selectedType == 8){ 
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
            
var itemSet = new Array(1082019,1082020,1082026,1082051,1082054,1082062,1082081,1082086);
var matSet = new Array(4000021,new Array(4000021,4011001),new Array(4000021,4011006),new Array(4000021,4021006,4021000),new Array(4000021,4011006,4011001,4021000),new Array(4000021,4021000,4021006,4003000),new Array(4021000,4011006,4000030,4003000),new Array(4011007,4011001,4021007,4000030,4003000));
var matQtySet = new Array(15,new Array(30,1),new Array(50,2),new Array(60,1,2),new Array(70,1,3,2),new Array(80,3,3,30),new Array(3,2,35,40),new Array(1,8,1,50,50));
var costSet = new Array(7000,15000,20000,25000,30000,40000,50000,70000);
item = itemSet[selectedItem];
mats = matSet[selectedItem];
matQty = matQtySet[selectedItem];
cost = costSet[selectedItem];
	}
else if (selectedType == 1){ //glove upgrade
var itemSet = new Array(1082021,1082022,1082027,1082028,1082052,1082053,1082055,1082056,1082063,1082064,1082082,1082080,1082087,1082088);
var matSet = new Array(new Array(1082020,4011001),new Array(1082020,4021001),new Array(1082026,4021000),new Array(1082026,4021008),new Array(1082051,4021005),new Array(1082051,4021008),new Array(1082054,4021005),new Array(1082054,4021008),new Array(1082062,4021002),new Array(1082062,4021008),new Array(1082081,4021002),new Array(1082081,4021008),new Array(1082086,4011004,4011006),new Array(1082086,4021008,4011006));
var matQtySet = new Array(new Array(1,1),new Array(1,2),new Array(1,3),new Array(1,1),new Array(1,3),new Array(1,1),new Array(1,3),new Array(1,1),new Array(1,4),new Array(1,2),new Array(1,5),new Array(1,3),new Array(1,3,5),new Array(1,2,3));
var costSet = new Array (20000,25000,30000,40000,35000,40000,40000,45000,45000,50000,55000,60000,70000,80000);
item = itemSet[selectedItem];
mats = matSet[selectedItem];
matQty = matQtySet[selectedItem];
cost = costSet[selectedItem];

	}
else if (selectedType == 2){ //hat upgrade
var itemSet = new Array(1002065,1002013);
var matSet = new Array(new Array(1002064,4011001),new Array(1002064,4011006));
var matQtySet = new Array(new Array(1,3),new Array(1,3));
var costSet = new Array(40000,50000);
item = itemSet[selectedItem];
mats = matSet[selectedItem];
matQty = matQtySet[selectedItem];
cost = costSet[selectedItem];
	}
else if (selectedType == 3){ //wand refine
var itemSet = new Array (1372005,1372006,1372002,1372004,1372003,1372001,1372000,1372007);
var matSet = new Array(4003001,new Array(4003001,4000001),new Array(4011001,4000009,4003000),new Array(4011002,4003002,4003000),new Array(4011002,4021002,4003000),new Array(4021006,4011002,4011001,4003000),new Array(4021006,4021005,4021007,4003003,4003000),new Array(4011006,4021003,4021007,4021002,4003002,4003000));
var matQtySet = new Array (5,new Array(10,50),new Array(1,30,5),new Array(2,1,10),new Array(3,1,10),new Array(5,3,1,15),new Array(5,5,1,1,20),new Array(4,3,2,1,1,30));
var costSet = new Array (1000,3000,5000,12000,30000,60000,120000,200000);
item = itemSet[selectedItem];
mats = matSet[selectedItem];
matQty = matQtySet[selectedItem];
cost = costSet[selectedItem];
	}
else if (selectedType == 4){ //staff refine
var itemSet = new Array (1382000,1382003,1382005,1382004,1382002,1382001);
var matSet = new Array(4003001,new Array(4021005,4011001,4003000),new Array(4021003,4011001,4003000),new Array(4003001,4011001,4003000),new Array(4021006,4021001,4011001,4003000),new Array(4011001,4021006,4021001,4021005,4003000,4000010,4003003));
var matQtySet = new Array (5,new Array(1,1,5),new Array(1,1,5),new Array(50,1,10),new Array(2,1,1,15),new Array(8,5,5,5,30,50,1));
var costSet = new Array (2000,2000,2000,5000,12000,180000);
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