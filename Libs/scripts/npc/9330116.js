var status = -1;

var GachItem = new Array([1432101,200,true], [1482080,200,true], [1122038,150,true], [1492012,150,true], [1492013,150,true], 
                         [1482012,150,true], [1482013,150,true], [1482023,150,true], [2330003,150,true], [2330004,150,true], 
                         [2330005,150,true], [2330007,150,true], [2331000,150,true], [2332000,150,true], [1002646,150,true], 
                         [1002649,150,true], [1052134,150,true], [1082213,150,true], [1082216,150,true], [1072318,150,true], 
                         [1072321,150,true], [2290097,150,true], [2290098,150,true], [2290099,150,true], [2290100,150,true], 
                         [2290101,150,true], [2290102,150,true], [2290103,150,true], [2290104,150,true], [2290105,150,true], 
                         [2290106,150,true], [2290107,150,true], [2290108,150,true], [2290109,150,true], [2290110,150,true], 
                         [2290111,150,true], [2290112,150,true], [2290113,150,true], [2290114,150,true], [2290115,150,true], 
                         [2290116,150,true], [2290117,150,true], [2290118,150,true], [2290119,150,true], [2290120,150,true], 
                         [2290121,150,true], [2290122,150,true], [2290123,150,true], [2290124,150,true], [1112405,150,true],

                         [1112413,150,true],
[1112414,150,true]);
	
			
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

      cm.sendYesNo("您身上有#b#t5220000##i5220000##k可以進行轉蛋。\r\n你確定要使用 #b海盜轉蛋機#k 進行轉蛋嗎?");

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

var itemList = new Array(1492007, 1492008, 1492009, 1492010, 1492011, 1482007, 1482008, 1482009, 1482010, 1482011, 2330001, 2330002, 1002631, 1002634, 1002637, 
                         1002640, 1002643, 1052116, 1052119, 1052122, 1052125, 1052128, 1052131, 1082180, 1082183, 1082186, 1082189, 1082192, 1082195, 1082198, 
                         1082201, 1082204, 1082207, 1082210, 1072300, 1072303, 1072306, 1072309, 1072312, 1072315);

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