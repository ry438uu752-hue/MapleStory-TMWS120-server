var status = -1;

var GachItem = new Array([2370000,200,true],
[2370001,199,true],
[2370002,198,true],
[2370003,197,true],
[2370004,196,true]);
	
			
//物品ID,機率,是否上廣
function action(mode, type, selection) {

    if (mode == 1) {

     status++;

    } else {

      cm.dispose();
		
      return ;

    }

    if (status == 0) {

    if (cm.haveItem(5220000, 1)) {

      cm.sendYesNo("您身上有#b#t5220000##i5220000##k可以進行轉蛋。\r\n你確定要使用 #b經驗轉蛋機#k 進行轉蛋嗎?");

     } else {

      cm.sendOk("不好意思!您沒有#b#t5220000##i5220000##k。");

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

var itemList = new Array(2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370012,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370011,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370010,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370009,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370008,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370007,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370006,
2000002, 2000003, 2000004, 2000005, 2000006, 2022000, 2020006, 2020013, 2020014, 2370005);

item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);

      }


    if (item != -1) {

       cm.gainItem(5220000, -1);

       cm.sendOk("您已獲得 #b#t" + item + "##i" + item + "##k");

      } else {

       cm.sendOk("檢查一下背包是否已滿。");

      } 

       cm.safeDispose();

     }

    }