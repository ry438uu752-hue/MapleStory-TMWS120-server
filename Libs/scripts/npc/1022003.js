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
    but WITHOUT ANY WARRANTY;
 without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the
    GNU Affero General Public License for more details.


    You should have received a copy of the GNU Affero General Public License
    along with this program.
  If not, see <http://www.gnu.org/licenses/>.

*/
/* Mr. Thunder

	Victoria Road: Perion (102000000)

	
	Refining NPC: 
	* Minerals
	* Jewels
	* Shields
	* Helmets
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

var selStr = "嗯？你說你是誰？呃，你有聽說過我的鍛造技術嗎？在有材料情況下，我很樂意幫你提煉原石。#b"

var options = new Array("提煉礦石","提煉寶石","合成頭盔","合成盾牌");

        for (var i = 0; i < options.length; i++){
            
selStr += "\r\n#L" + i + "# " + options[i] + "#l";

        }
			
        
cm.sendSimple(selStr);

    }
    else if (status == 1 && mode == 1) {

selectedType = selection;

        if (selectedType == 0){
 //helmet refine

var selStr = "請告訴我你想提煉哪種礦石?#b";

var helmets = new Array ("青銅#k#b","鋼鐵#k#b","鋰礦石#k#b","朱礦石#k#b","銀#k#b","紫礦石#k#b","黃金#k#b");

            for (var i = 0;
 i < helmets.length; i++){

                selStr += "\r\n#L" + i + "# " + helmets[i] + "#l";

            }
            
        cm.sendSimple(selStr);

            equip = true;

        }
        else if (selectedType == 1){
 //shield refine

var selStr = "請告訴我你想提煉哪種寶石?#b";

var shields = new Array ("石榴石#k#b","紫水晶#k#b","海藍石#k#b","祖母綠#k#b","蛋白母石#k#b","藍寶石#k#b","黃晶#k#b","鑽石#k#b","黑水晶#k#b");

            for (var i = 0;
 i < shields.length; i++){

                selStr += "\r\n#L" + i + "# " + shields[i] + "#l";

            }
            
        cm.sendSimple(selStr);

            equip = true;

        }
        
else if (selectedType == 2){
 //helmet refine

var selStr = "請告訴我你想合成哪種頭盔?#b";

var helmets = new Array ("藍色金屬頭箍#k   - 全職 Lv. 15#b","黃色金屬頭箍#k   - 全職 Lv. 15#b","鋼鐵頭盔#k       - 劍士 Lv. 10#b","鋰礦頭盔#k       - 劍士 Lv. 10#b","鋼鐵帽#k         - 劍士 Lv. 12#b","鋰礦帽#k         - 劍士 Lv. 12#b","鐵製鋼盔#k       - 劍士 Lv. 15#b",

             "鋰礦鋼盔#k       - 劍士 Lv. 15#b","鐵製海盜頭盔#k   - 劍士 Lv. 20#b","鋰礦海盜頭盔#k   - 劍士 Lv. 20#b","鋼鐵橄欖球帽#k   - 劍士 Lv. 20#b","鋰礦橄欖球帽#k   - 劍士 Lv. 20#b","鋰礦尖頭盔#k     - 劍士 Lv. 22#b","黃金尖頭盔#k     - 劍士 Lv. 22#b",

             "黃金騎士頭盔#k   - 劍士 Lv. 25#b","紫礦騎士頭盔#k   - 劍士 Lv. 25#b","紅色戰鬥頭盔#k   - 劍士 Lv. 35#b","藍色戰鬥頭盔#k   - 劍士 Lv. 35#b","鋰礦諾曼頭盔#k   - 劍士 Lv. 40#b","黃金諾曼頭盔#k   - 劍士 Lv. 40#b","鋰礦十字軍帽#k   - 劍士 Lv. 50#b",

             "銀製十字軍帽#k   - 劍士 Lv. 50#b","舊諾曼頭盔#k     - 劍士 Lv. 55#b","舊鋰礦諾曼頭盔#k - 劍士 Lv. 55#b");

            for (var i = 0;
 i < helmets.length; i++){

                selStr += "\r\n#L" + i + "# " + helmets[i] + "#l";

            }
            
        cm.sendSimple(selStr);

            equip = true;

        }
        
     else if (selectedType == 3){
 //shield refine

var selStr = "請告訴我你想合成哪種盾牌?#b";

var shields = new Array ("朱礦方盾#k   - 劍士 Lv. 40#b","鋰礦方盾#k   - 劍士 Lv. 40#b","古老銀盾#k   - 劍士 Lv. 60#b","古老朱礦盾#k - 劍士 Lv. 60#b");

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
 //mineral refine
            
var itemSet = new Array(4011000,4011001,4011002,4011003,4011004,4011005,4011006);
            
var matSet = new Array(4010000,4010001,4010002,4010003,4010004,4010005,4010006);
            
var matQtySet = new Array(10,10,10,10,10,10,10);
            
var costSet = new Array(300,300,300,500,500,500,800);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];
        
     }

     else if (selectedType == 6){
 //jewel refine
            
var itemSet = new Array(4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008);
           
 var matSet = new Array(4020000,4020001,4020002,4020003,4020004,4020005,4020006,4020007,4020008);
           
 var matQtySet = new Array(10,10,10,10,10,10,10,10,10);
            
var costSet = new Array (500,500,500,500,500,500,500,1000,3000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
		
        
var prompt = "你是說你想要讓我製做 #b#t" + item + "##k ? 你想要製做幾個呢 ?";
		
        
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
 //helmet refine
            
var itemSet = new Array(4011000,4011001,4011002,4011003,4011004,4011005,4011006);
            
var matSet = new Array(new Array(4010000,4010000),new Array(4010001,4010001),new Array(4010002,4010002),
                       new Array(4010003,4010003),new Array(4010004,4010004),new Array(4010005,4010005),
					   new Array(4010006,4010006));
            
var matQtySet = new Array(new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0));
            
var costSet = new Array(300,300,300,500,500,500,800);
            
   item = itemSet[selectedItem];
           
   mats = matSet[selectedItem];
            
 matQty = matQtySet[selectedItem];
            
   cost = costSet[selectedItem];

        }
     
        else if (selectedType == 1){
 //helmet refine
            
var itemSet = new Array(4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008);
            
var matSet = new Array(new Array(4020000,4020000),new Array(4020001,4020001),new Array(4020002,4010002),
                       new Array(4020003,4020003),new Array(4020004,4020004),new Array(4020005,4020005),
					   new Array(4020006,4020006),new Array(4020007,4020007),new Array(4020008,4020008));
            
var matQtySet = new Array(new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0),new Array(10,0));
            
var costSet = new Array(500,500,500,500,500,500,500,1000,3000);
            
   item = itemSet[selectedItem];
           
   mats = matSet[selectedItem];
            
 matQty = matQtySet[selectedItem];
            
   cost = costSet[selectedItem];

        }
     
        else if (selectedType == 2){
 //helmet refine
            
var itemSet = new Array(1002042,1002041,1002002,1002044,1002003,1002040,1002007,1002052,1002011,1002058,1002009,
                        1002056,1002087,1002088,1002049,1002050,1002047,1002048,1002099,1002098,1002085,1002028,1002022,1002101);
            
var matSet = new Array(new Array(1002001,4011002),new Array(1002001,4021006),new Array(1002043,4011001),new Array(1002043,4011002),
             new Array(1002039,4011001),new Array(1002039,4011002),new Array(1002051,4011001),new Array(1002051,4011002),
             new Array(1002059,4011001),new Array(1002059,4011002),

             new Array(1002055,4011001),new Array(1002055,4011002),new Array(1002027,4011002),new Array(1002027,4011006),
             new Array(1002005,4011006),new Array(1002005,4011005),new Array(1002004,4021000),new Array(1002004,4021005),
             new Array(1002021,4011002),new Array(1002021,4011006),new Array(1002086,4011002),

             new Array(1002086,4011004),new Array(1002100,4011007,4011001),new Array(1002100,4011007,4011002));
            
var matQtySet = new Array(new Array(1,1),new Array(1,1),new Array(1,1),new Array(1,1),new Array(1,1),new Array(1,1),new Array(1,2),
             new Array(1,2),new Array(1,3),new Array(1,3),new Array(1,3),new Array(1,3),new Array(1,4),new Array(1,4),new Array(1,5),
             new Array(1,5),new Array(1,3),new Array(1,3),

             new Array(1,5),new Array(1,6),new Array(1,5),new Array(1,4),new Array(1,1,7),new Array(1,1,7));
            
var costSet = new Array(500,300,500,800,500,800,1000,1500,1500,2000,1500,2000,2000,4000,4000,5000,8000,10000,12000,15000,20000,25000,30000,30000);
            
   item = itemSet[selectedItem];
           
   mats = matSet[selectedItem];
            
 matQty = matQtySet[selectedItem];
            
   cost = costSet[selectedItem];

        }
     
        else if (selectedType == 3){
 //shield refine
            
var itemSet = new Array (1092014,1092013,1092010,1092011);
            
var matSet = new Array(new Array (1092012,4011003),new Array (1092012,4011002),new Array (1092009,4011007,4011004),new Array (1092009,4011007,4011003));
            
var matQtySet = new Array (new Array (1,10),new Array (1,10),new Array (1,1,15),new Array (1,1,15));
            
var costSet = new Array (100000,100000,120000,120000);
            
item = itemSet[selectedItem];
            
mats = matSet[selectedItem];
            
matQty = matQtySet[selectedItem];
            
cost = costSet[selectedItem];

        }
        
var prompt = "你想讓我製做";

        if (qty == 1)
            
prompt += " 1 個 #b#t" + item + "##k ?";

        else
            
prompt += qty + "#t" + item + "#?";
        
prompt += " 在製做之前，我需要你準備一些原料，我才能完成作品。請再次確認你的材料!#b";
        
        if (mats instanceof Array)
            
for(var i = 0; i < mats.length; i++)
                
prompt += "\r\n#i"+mats[i]+"# " + matQty[i] * qty + " #t" + mats[i] + "#";

        else {
            
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
			
complete = false;

        }

        else
        {

        if (mats instanceof Array) {
                
for(var i = 0; complete && i < mats.length; i++) {

                    if (matQty[i] * qty == 1)	{

                    if (!cm.haveItem(mats[i]))
 {
                            
complete = false;

        }
                    
        }  else {

        if (!cm.haveItem(mats[i],matQty[i] * qty)){

complete=false;
						
   }
                    
  }
                
 }
           
 }else {

       if (!cm.haveItem(mats,matQty * qty)){

complete=false;
				
    }
           
   }
       
  }
       if (!complete)
            
cm.sendOk("您的材料或背包空間不足, 請再次確認!");

            else {

        if (mats instanceof Array)
                
for (var i = 0; i < mats.length; i++)
                    
cm.gainItem(mats[i], -matQty[i] * qty);

            else
                
cm.gainItem(mats, -matQty * qty);
            
cm.gainMeso(-cost * qty);
            
cm.gainItem(item,qty);
            
cm.sendOk("完成了!有需要的話可以再來找我。");

        }
        
cm.dispose();

    }

  }