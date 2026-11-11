var status = -1;

var GachItem = new Array([1372074,200,true], [1372075,200,true], [1372076,200,true], [1372077,200,true], [1372078,200,true], 
                         [1382095,200,true], [1382096,200,true], [1382097,200,true], [1382098,200,true], [1382099,200,true]);
			
//物品ID,機率,是否上廣
function action(mode, type, selection) {

    if (mode == 1) {

     status++;

    } else {

      cm.dispose();
		
      return ;

    }

    if (status == 0) {

    if (cm.haveItem(4110010, 1)) {

      cm.sendYesNo("您確定要使用 #r2號#b小鋼珠轉蛋機#k 進行轉蛋嗎 ? \r\n有一定的機率能獲得#r法師#bVIP#k武器, #b祝您中大獎 #r~#k");

     } else {

      cm.sendOk("不好意思!您沒有#b#t4110010##i4110010##k。");

      cm.safeDispose();

     }
    
     } else if (status == 1) {
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

var itemList = new Array(2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 
                         2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014);

item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);

      }


    if (item != -1) {

       cm.gainItem(4110010, -1);

       cm.sendOk("您已獲得 #b#t" + item + "##i" + item + "##k");

      } else {

       cm.sendOk("檢查一下背包是否已滿。");

      } 

       cm.safeDispose();

     }

    }