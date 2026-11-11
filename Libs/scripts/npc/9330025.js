/* 
Gina
	Ludibrium Skin Change.

*/

var status = -1;

var skin = Array(0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11);


function action(mode, type, selection) {
    
if (mode == 0) {
	
cm.dispose();
	
return;

    } else {
	
status++;

    }
    
if (status == 0) {
	
cm.sendNext("你好！ 歡迎來到不夜城整形中心，這裡是護膚部！ 你有興趣曬黑和看起來性感嗎？ 或是讓皮膚看起來美麗雪白？ 如果你有 #b#t5153004##k, 就能讓我們為你重朔你的外在美，擁有你夢寐以求的皮膚！");

    } else if (status == 1) {
	
cm.askAvatar("使用我們的專業機器，您可以在手術前看到治療方式。 你在尋找什麼樣的膚色呢？ 來吧，選擇你喜歡的風格～！", skin);

    } else if (status == 2){
	
if (cm.setAvatar(5153004, skin[selection]) == 1) {
	    
cm.sendOk("怎麼樣！是不是覺得煥然一新了呢！");

	} else {
	    
cm.sendOk("你沒有護膚券喔！");

	}
	
cm.safeDispose();

    }

}