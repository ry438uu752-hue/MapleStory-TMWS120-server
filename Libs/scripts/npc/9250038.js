var status = -1;
//物品ID,機率,是否上廣
var GachItem = new Array([1302143,500,true]);
			

function action(mode, type, selection) {

    if (mode == 1) {

     status++;

    } else {

      cm.dispose();
		
      return ;

    }

    if (status == 0) {

    if (cm.haveItem(4031742, 1) && cm.haveItem(4000312, 50)) {

      cm.sendYesNo("還沒打到邪神分身娃娃嗎 ? 請再接再厲 !");

     } else {

      cm.sendOk("除了#b#i4031742:##k之外還需要50個#b#i4000312:##k。");

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

var itemList = new Array(4031722, 4031722, 4031722, 4031722, 4031722, 4031722, 4031722, 4031722, 4031722, 4031722);

item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);

      }


    if (item != -1) {

       cm.gainItem(4000312, -50);

       cm.sendOk("您已獲得 #b#t" + item + "##i" + item + "##k");

      } else {

       cm.sendOk("檢查一下背包是否已滿。");

      } 

       cm.safeDispose();

     }

    }