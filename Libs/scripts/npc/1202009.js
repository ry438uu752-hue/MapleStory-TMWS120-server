/* Kedrick
	Fishking King 
NPC
*/


/* global cm 
*/

var status = -1;

var sel;


function action(mode, type, selection) {
    
if (mode == 1) {	
status++;

    } else {
		
if (status == 0) {
			
cm.dispose();
			
return;

		}
	
status--;

    }

    
if (status == 0) {
	
cm.sendSimple("#b這裡有適合初學者使用的座騎 \n\r 但僅限於我們的族人才能使用 #k#d\n\r #L1# 野狼馬鞍 + 50等柳虎(500W)#k#l #d\n\r #L4# 100等柳虎(2000W)#k#l #d\n\r #L5# 150等柳虎(5000W)#k#l #d\n\r #L6# 200等柳虎(8000W)#k#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#b在購買之前請空出#r 2 #b個裝備欄#k");

	} else if (sel == 2) {
	    		
if(cm.haveItem(4001126,-1)&& cm.getMeso() >= 5000000) {
		    
cm.gainItem(1912011,1); // 野狼馬鞍	(無用)    
cm.gainItem(1902015,1); // 50等柳虎
cm.gainMeso(-5000000);  // 楓幣
		    
cm.sendNext("#b謝謝惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b需要準備 #r2000#bW 楓幣和#r50#b等#r柳虎#b才能交換 #r！");

	    }
	    
cm.safeDispose();

	}
	
cm.sendYesNo("#b兌換後將無法退還#r，#b你確定要兌換嗎#r？#k");

	} else if (sel == 3) {
	    		
if(cm.haveItem(4001126,-1)&& cm.getMeso() >= 5000000) {
		    
cm.gainItem(1912011,1); // 野狼馬鞍    
cm.gainItem(1902015,1); // 50等柳虎
cm.gainMeso(-5000000);  // 楓幣
		    
cm.sendNext("#b謝謝惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b你的#r楓幣#b不足#k");

	    }
	    
cm.safeDispose();

	} else if (sel == 4) {
	    		
if(cm.haveItem(1902015,1)&& cm.getMeso() >= 20000000) {

cm.gainItem(1902015,-1); //  50等柳虎
cm.gainItem(1902016,1);  // 100等柳虎
cm.gainMeso(-20000000);  // 楓幣
	    
cm.sendNext("#b謝謝惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b需要準備 #r2000#bW 楓幣和#r 50 #b等柳虎才能交換 #r！#k");

	    }
	    
cm.safeDispose();

	} else if (sel == 5) {
	    		
if(cm.haveItem(1902016,1)&& cm.getMeso() >= 50000000) {

cm.gainItem(1902016,-1); // 100等柳虎
cm.gainItem(1902017,1);  // 150等柳虎
cm.gainMeso(-50000000);  // 楓幣
	    
cm.sendNext("#b謝謝惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b需要準備 #r5000#bW 楓幣和#r 100 #b等柳虎才能交換 #r！#k");

	    }
	    
cm.safeDispose();

	} else if (sel == 6) {
	    		
if(cm.haveItem(1902017,1)&& cm.getMeso() >= 80000000) {

cm.gainItem(1902017,-1); // 150等柳虎
cm.gainItem(1902018,1);  // 200等柳虎
cm.gainMeso(-80000000);  // 楓幣
	    
cm.sendNext("#b謝謝惠顧 #r~ ");

		} else {
		   
cm.sendOk("#b需要準備 #r8000#bW 楓幣和#r 150 #b等柳虎才能交換 #r！#k");

	    }
	    
cm.safeDispose();

	} else if (sel == 7) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2381039,5); //      烏鴉	    
cm.gainItem(2381040,5); //
cm.gainItem(2381041,5); //
cm.gainItem(2381042,5); //
cm.gainItem(2381043,5); //
cm.gainItem(2381044,5); //
cm.gainItem(2381045,5); //
cm.gainItem(2381046,5); //
cm.gainItem(2381047,5); //       山羊
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 8) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2381074,5); //    小狗仔	    
cm.gainItem(2381075,5); //
cm.gainItem(2381076,5); //
cm.gainItem(2381082,5); //
cm.gainItem(2381083,5); //
cm.gainItem(2381084,5); //
cm.gainItem(2381085,5); //
cm.gainItem(2382000,5); //
cm.gainItem(2382001,5); //
cm.gainItem(2382002,5); //    鱷魚
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 9) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382003,5); //  火肥肥	    
cm.gainItem(2382004,5); //
cm.gainItem(2382005,5); //
cm.gainItem(2382006,5); //
cm.gainItem(2382007,5); //
cm.gainItem(2382008,5); //
cm.gainItem(2382009,5); //
cm.gainItem(2382010,5); //
cm.gainItem(2382011,5); //
cm.gainItem(2382012,5); //幼黃獨角獅
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 10) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382013,5); //幼藍獨角獅	    
cm.gainItem(2382014,5); //
cm.gainItem(2382015,5); //
cm.gainItem(2382016,5); //
cm.gainItem(2382017,5); //
cm.gainItem(2382018,5); //
cm.gainItem(2382019,5); //
cm.gainItem(2382020,5); //
cm.gainItem(2382021,5); //
cm.gainItem(2382022,5); // 機器章魚
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 11) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382023,5); //   小企鵝王	    
cm.gainItem(2382024,5); //
cm.gainItem(2382025,5); //
cm.gainItem(2382026,5); //
cm.gainItem(2382027,5); //
cm.gainItem(2382028,5); //
cm.gainItem(2382029,5); //
cm.gainItem(2382030,5); //
cm.gainItem(2382031,5); //
cm.gainItem(2382032,5); //       妖魔
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 12) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382033,5); // 機器章魚王	    
cm.gainItem(2382034,5); //
cm.gainItem(2382035,5); //
cm.gainItem(2382036,5); //
cm.gainItem(2382037,5); //
cm.gainItem(2382038,5); //
cm.gainItem(2382039,5); //
cm.gainItem(2382040,5); //
cm.gainItem(2382041,5); //
cm.gainItem(2382042,5); //   葛雷族人
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 13) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382043,5); //       河豚	    
cm.gainItem(2382044,5); //
cm.gainItem(2382045,5); //
cm.gainItem(2382046,5); //
cm.gainItem(2382047,5); //
cm.gainItem(2382048,5); //
cm.gainItem(2382049,5); //
cm.gainItem(2382050,5); //
cm.gainItem(2382051,5); //
cm.gainItem(2382052,5); //     食人花
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 14) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382053,5); //   鋼之肥肥	    
cm.gainItem(2382054,5); //
cm.gainItem(2382055,5); //
cm.gainItem(2382056,5); //
cm.gainItem(2382057,5); //
cm.gainItem(2382058,5); //
cm.gainItem(2382059,5); //
cm.gainItem(2382060,5); //
cm.gainItem(2382061,5); //
cm.gainItem(2382062,5); // 紅色機器人
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 15) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382063,5); //     骷髏犬	    
cm.gainItem(2382064,5); //
cm.gainItem(2382065,5); //
cm.gainItem(2382066,5); //
cm.gainItem(2382067,5); //
cm.gainItem(2382068,5); //
cm.gainItem(2382069,5); //
cm.gainItem(2382070,5); //
cm.gainItem(2382071,5); //
cm.gainItem(2382072,5); //強化鋼鐵穆太
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 16) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382073,5); //       雲狐	    
cm.gainItem(2382074,5); //
cm.gainItem(2382075,5); //
cm.gainItem(2382076,5); //
cm.gainItem(2382077,5); //
cm.gainItem(2382078,5); //
cm.gainItem(2382079,5); //
cm.gainItem(2382080,5); //
cm.gainItem(2382081,5); //
cm.gainItem(2382082,5); //小企鵝王玩偶
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 17) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2382083,5); //   草莓珍奶	    
cm.gainItem(2382084,5); //
cm.gainItem(2382085,5); //
cm.gainItem(2382086,5); //
cm.gainItem(2382087,5); //
cm.gainItem(2382092,5); //
cm.gainItem(2382093,5); //
cm.gainItem(2382094,5); //
cm.gainItem(2382095,5); //
cm.gainItem(2382096,5); //親衛隊企鵝王
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 18) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383000,5); //   梅卡堤安	    
cm.gainItem(2383001,5); //
cm.gainItem(2383002,5); //
cm.gainItem(2383003,5); //
cm.gainItem(2383004,5); //
cm.gainItem(2383005,5); //
cm.gainItem(2383006,5); //
cm.gainItem(2383007,5); //
cm.gainItem(2383008,5); //
cm.gainItem(2383009,5); //     青螃蟹
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 19) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383010,5); //     蔘罈壺	    
cm.gainItem(2383011,5); //
cm.gainItem(2383012,5); //
cm.gainItem(2383013,5); //
cm.gainItem(2383014,5); //
cm.gainItem(2383015,5); //
cm.gainItem(2383016,5); //
cm.gainItem(2383017,5); //
cm.gainItem(2383018,5); //
cm.gainItem(2383019,5); //     黑鱷魚
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 20) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383020,5); //   日光精靈	    
cm.gainItem(2383021,5); //
cm.gainItem(2383022,5); //
cm.gainItem(2383023,5); //
cm.gainItem(2383024,5); //
cm.gainItem(2383025,5); //
cm.gainItem(2383026,5); //
cm.gainItem(2383027,5); //
cm.gainItem(2383028,5); //
cm.gainItem(2383029,5); //       巫婆
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 21) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383030,5); //     石巨人	    
cm.gainItem(2383031,5); //
cm.gainItem(2383032,5); //
cm.gainItem(2383033,5); //
cm.gainItem(2383034,5); //
cm.gainItem(2383035,5); //
cm.gainItem(2383036,5); //
cm.gainItem(2383037,5); //
cm.gainItem(2383038,5); //
cm.gainItem(2383039,5); // 黑曜石巨人
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 22) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383040,5); //       白狼	    
cm.gainItem(2383041,5); //
cm.gainItem(2383042,5); //
cm.gainItem(2383043,5); //
cm.gainItem(2383044,5); //
cm.gainItem(2383045,5); //
cm.gainItem(2383046,5); //
cm.gainItem(2383047,5); //
cm.gainItem(2383050,5); //
cm.gainItem(2383051,5); //       河童
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 23) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2383052,5); //   街頭混混	    
cm.gainItem(2383053,5); //
cm.gainItem(2383054,5); //
cm.gainItem(2383055,5); //
cm.gainItem(2383056,5); //
cm.gainItem(2383057,5); //
cm.gainItem(2383058,5); //
cm.gainItem(2383059,5); //
cm.gainItem(2383060,5); //
cm.gainItem(2383063,5); //變身娃娃機(變身後)
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 24) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2384000,5); //   小丑柏非	    
cm.gainItem(2384001,5); //
cm.gainItem(2384002,5); //
cm.gainItem(2384003,5); //
cm.gainItem(2384004,5); //
cm.gainItem(2384005,5); //
cm.gainItem(2384006,5); //
cm.gainItem(2384007,5); //
cm.gainItem(2384008,5); //
cm.gainItem(2384009,5); //     雪吉拉
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 25) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2384010,5); //   厄運死神	    
cm.gainItem(2384011,5); //
cm.gainItem(2384012,5); //
cm.gainItem(2384013,5); //
cm.gainItem(2384014,5); //
cm.gainItem(2384015,5); //
cm.gainItem(2384016,5); //
cm.gainItem(2384017,5); //
cm.gainItem(2384018,5); //
cm.gainItem(2384019,5); //       冥鐘
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 26) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2384020,5); // 月牙牛魔王	    
cm.gainItem(2384021,5); //
cm.gainItem(2384022,5); //
cm.gainItem(2384023,5); //
cm.gainItem(2384024,5); //
cm.gainItem(2384025,5); //
cm.gainItem(2384026,5); //
cm.gainItem(2384027,5); //
cm.gainItem(2384028,5); //
cm.gainItem(2384029,5); // 骷髏指揮官
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 27) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2384030,5); //   黑格里芬	    
cm.gainItem(2384031,5); //
cm.gainItem(2384032,5); //
cm.gainItem(2384033,5); //
cm.gainItem(2384034,5); //
cm.gainItem(2384035,5); //
cm.gainItem(2384036,5); //
cm.gainItem(2384041,5); //
cm.gainItem(2384042,5); //
cm.gainItem(2384043,5); //     扛壩子
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 28) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2384044,5); //   甜心香水	    
cm.gainItem(2384045,5); //
cm.gainItem(2384046,5); //
cm.gainItem(2384047,5); //
cm.gainItem(2384048,5); //
cm.gainItem(2384049,5); //
cm.gainItem(2384050,5); //
cm.gainItem(2385000,5); //
cm.gainItem(2385001,5); //
cm.gainItem(2385002,5); // 邪惡侏儒怪
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 29) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2385003,5); //   特貝爾芬	    
cm.gainItem(2385005,5); //
cm.gainItem(2385006,5); //
cm.gainItem(2385007,5); //
cm.gainItem(2385008,5); //
cm.gainItem(2385010,5); //
cm.gainItem(2385011,5); //
cm.gainItem(2385012,5); //
cm.gainItem(2385013,5); //
cm.gainItem(2385014,5); //   邪惡綿羊
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 30) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2385015,5); //     刺鰭魚	    
cm.gainItem(2385016,5); //
cm.gainItem(2385017,5); //
cm.gainItem(2385018,5); //
cm.gainItem(2385019,5); //
cm.gainItem(2385020,5); //
cm.gainItem(2385021,5); //
cm.gainItem(2385022,5); //
cm.gainItem(2385024,5); //
cm.gainItem(2385025,5); //   時間之眼
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 31) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2385029,5); //   人偶蘿絲	    
cm.gainItem(2385030,5); //
cm.gainItem(2385031,5); //
cm.gainItem(2386000,5); //
cm.gainItem(2386001,5); //
cm.gainItem(2386002,5); //
cm.gainItem(2386003,5); //
cm.gainItem(2386004,5); //
cm.gainItem(2386005,5); //
cm.gainItem(2386006,5); // 進化迅猛龍
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 32) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2386007,5); // 致命烏賊怪	    
cm.gainItem(2386008,5); //
cm.gainItem(2386009,5); //
cm.gainItem(2386010,5); //
cm.gainItem(2386011,5); //
cm.gainItem(2386012,5); //
cm.gainItem(2386013,5); //
cm.gainItem(2386014,5); //
cm.gainItem(2386015,5); //
cm.gainItem(2386016,5); // 雙刀龍戰士
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 33) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2386017,5); //     幼年龍	    
cm.gainItem(2386018,5); //
cm.gainItem(2386019,5); //
cm.gainItem(2386020,5); //
cm.gainItem(2386021,5); //
cm.gainItem(2386022,5); //
cm.gainItem(2386023,5); //
cm.gainItem(2386024,5); //
cm.gainItem(2387000,5); //
cm.gainItem(2387001,5); //   達納托斯
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 34) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2387002,5); //     化石龍	    
cm.gainItem(2387003,5); //
cm.gainItem(2387004,5); //
cm.gainItem(2387005,5); //
cm.gainItem(2387006,5); //
cm.gainItem(2387007,5); //
cm.gainItem(2387008,5); //
cm.gainItem(2387009,5); //
cm.gainItem(2387010,5); //
cm.gainItem(2387011,5); // 忘卻的神官
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 35) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2387012,5); // 忘卻守護兵	    
cm.gainItem(2387013,5); //
cm.gainItem(2387021,5); //
cm.gainItem(2387022,5); //
cm.gainItem(2387023,5); //
cm.gainItem(2387024,5); //
cm.gainItem(2387025,5); //
cm.gainItem(2387026,5); //
cm.gainItem(2387032,5); //   帝國護衛
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 36) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388000,5); //     紅寶王	    
cm.gainItem(2388001,5); //
cm.gainItem(2388002,5); //
cm.gainItem(2388003,5); //
cm.gainItem(2388004,5); //
cm.gainItem(2388005,5); //
cm.gainItem(2388006,5); //
cm.gainItem(2388007,5); //
cm.gainItem(2388008,5); //
cm.gainItem(2388009,5); //   九尾妖狐
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 37) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388010,5); //     肯德熊	    
cm.gainItem(2388011,5); //
cm.gainItem(2388012,5); //
cm.gainItem(2388013,5); //
cm.gainItem(2388014,5); //
cm.gainItem(2388015,5); //
cm.gainItem(2388016,5); //
cm.gainItem(2388017,5); //
cm.gainItem(2388018,5); //
cm.gainItem(2388019,5); //   格瑞芬多
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 38) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388020,5); //     海怒斯	    
cm.gainItem(2388021,5); //
cm.gainItem(2388022,5); //
cm.gainItem(2388023,5); //
cm.gainItem(2388024,5); //
cm.gainItem(2388025,5); //
cm.gainItem(2388026,5); //
cm.gainItem(2388029,5); //
cm.gainItem(2388030,5); //
cm.gainItem(2388031,5); //   葛雷金剛
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 39) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388032,5); //     奇美拉	    
cm.gainItem(2388033,5); //
cm.gainItem(2388034,5); //
cm.gainItem(2388035,5); //
cm.gainItem(2388036,5); //
cm.gainItem(2388037,5); //
cm.gainItem(2388038,5); //
cm.gainItem(2388039,5); //
cm.gainItem(2388040,5); //
cm.gainItem(2388041,5); //   利里諾斯
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 40) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388042,5); //     萊伊卡	    
cm.gainItem(2388043,5); //
cm.gainItem(2388044,5); //
cm.gainItem(2388046,5); //
cm.gainItem(2388047,5); //
cm.gainItem(2388048,5); //
cm.gainItem(2388049,5); //
cm.gainItem(2388050,5); //
cm.gainItem(2388051,5); //
cm.gainItem(2388055,5); // 魔王巴洛古
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 41) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2388067,5); // 葛雷雪吉拉	    
cm.gainItem(2388068,5); //
cm.gainItem(2388069,5); //
cm.gainItem(2388070,5); //
cm.gainItem(2388071,5); //
cm.gainItem(2388072,5); //
cm.gainItem(2388073,5); //
cm.gainItem(2388074,5); //
cm.gainItem(2388081,5); //
cm.gainItem(2388082,5); //     龍騎士
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 42) {
	    		
if(cm.haveItem(4001126,100)) {

cm.gainItem(4080100,10); //    找碴	    
cm.gainItem(4001126,-100); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ #k");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

    } else if (status == 2) {
	
if (sel == 1) {
	    
if(cm.haveItem(4001126,-1)&& cm.getMeso() >= 5000000) {
		    
cm.gainItem(1912011,1); // 野狼馬鞍	    
cm.gainItem(1902015,1); // 50等柳虎
cm.gainMeso(-5000000); // 楓幣
	   
cm.sendNext("#b謝謝惠顧 #r~ #k");

		} else {
		    
cm.sendNext("#b你的#r楓幣#b不足#k");

		}

	    } else {
		
cm.sendOk("#b您的楓葉不足 #r！#k");

	    }
	    
cm.safeDispose();

	}

    }