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
	Victoria Road : Henesys Market (100000100)
	
	
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
    
var selStr = "【#d迷你地城活動地點#k】\r\n#b愛奧斯塔 #r76 #b樓#k\r\n#b弓箭手村 #r肥肥農場#k\r\n只要收集從怪物身上掉落的楓葉水晶, 就能跟我兌換武器強化卷軸喲 ~#b"
    
var options = ["兌換武器強化卷軸","兌換釣魚用椅子","兌換通用整形券"];
    
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
        
if (selection == 3) { 
//bow refine
           
 var selStr = "請告訴我你想兌換哪件楓葉武器？#b";
            
items = [1302064,1402039,1472055,1332055,1332056,1372034,1382039,1312032,
         1412027,1422029,1322054,1432040,1442051,1452045,1462040,1482022,
		 1492022,1342027];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 1) { 
//xbow refine
            
var selStr = "聽說釣魚場裡的獎勵非常豐富 #r!#b";
            
items = [3010004];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 2) { 
//glove refine
            
var selStr = "兌換之後恕不退還，你確定要兌換#t5152049#？#b";
            
items = [5152049];
            
for (var i = 0; i < items.length; i++)
                
selStr += "\r\n#L" + i + "##t" + items[i] + "##l";

        }else if (selection == 0) { 
//glove upgrade
            
var selStr = "你想要兌換哪一種卷軸呢？#b";
            
items = [2043003,2044003,2043103,2044103,2043203,2044203,2044303,2044403,2044503,2044603,2043303,2044703,2043406,2043703,2043803,2040807,2040806,2040506,2040709,2040710,2040711,2040303];           
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
        
cm.sendGetNumber("你想兌換 #t" + item + "#? 你想要製做幾組呢?",1,1,100)

    }else if (status == 2) {
        
if (selectedType != 8)
            
selectedItem = selection;

        else
            
qty = selection;
        
if (selectedType == 3) { 
//bow refine
            
var matSet = [[1302020,4001126],[1302020,4001126],[1472030,4001126],
              [1332025,4001126],[1332025,4001126],[1382009,4001126],
	      [1382012,4001126],[1412011,4001126],[1412011,4001126],
              [1422014,4001126],[1422014,4001126],[1432012,4001126],
	      [1442024,4001126],[1452022,4001126],[1462019,4001126],
              [1482020,4001126],[1492020,4001126],[1342025,4001126]];
var matQtySet = [[1,2000],[1,2000],[1,2000],[1,2000],[1,2000],[1,2000],
                 [1,1500],[1,1500],[1,1500],[1,1500],[1,1500],[1,1500],
				 [1,1500],[1,2000],[1,2000],[1,2000],[1,2000],[1,2000]];
            
var costSet = [50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000,50000];

        }else if (selectedType == 1) { 
//xbow refine
            
var matSet = [[4032056,4001126],[1092030,4001126],[1092030,4001126],
              [1092030,4001126],[4001126],[1002508,4001126],[1002509,4001126],
	      [1002510,4001126],[1002511,4001126],[4001126],[1122201,4001126],
              [1122202,4001126],[4001126],[4001126],[4001126],[4001126]];
var matQtySet = [[100,100],[1,2000],[1,2000],[1,2000],[100],[1,200],[1,300],[1,400],
                 [1,500],[200],[1,300],[1,400],[200],[300],[500],[1000]];
            
var costSet = [3000,50000,50000,50000,500,1000,50000,50000,500000,50000,50000,500000,5000,10000,50000,50000];

        }else if (selectedType == 2) { 
//glove refine
            
var matSet = [[4032056]];
            
var matQtySet = [[500]];
            
var costSet = [0];

        }else if (selectedType == 0) { 
//glove upgrade
            
var matSet = [[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056],[4032056]];
            
var matQtySet = [[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000],[1000]];
            
var costSet = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];

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
        
var prompt = "你想兌換 ";
        
if (qty == 1)
            
prompt += " 1 張 #b#t" + item + "##k 是嗎 ?";

        else
            
prompt += qty + " #t" + item + "#?";
        
prompt += " 在兌換之前，請再次確認你的背包空間跟所需的材料!#b";
        
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
                    
cm.gainItem(item, 1000 - (item - 2060000) * 100);
                
else if (item >= 2061000 && item <= 2061002) 
//xbow arrows
                    
cm.gainItem(item, 1000 - (item - 2061000) * 100);

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