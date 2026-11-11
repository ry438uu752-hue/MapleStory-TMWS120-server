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
	
cm.sendSimple("#b在兌換怪物卡之前\n\r 請空出10個消耗欄 \n\r 請問您想要兌換什麼卡片 #r？#k#d \n\r #L1#      嫩寶 ~  緞帶肥肥#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L2#  三眼章魚 ~    穆魯昆#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L3#奇怪的肥肥 ~  黑斧木妖#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L4#      海膽 ~  石面怪人#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L5#  耳罩蜥蜴 ~成年仙人掌#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L6#    花鯰魚 ~    火石球#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L7#      烏鴉 ~      山羊#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L8#    小狗仔 ~      鱷魚#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L9#    火肥肥 ~幼黃獨角獅#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L10#幼藍獨角獅 ~  機器章魚#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L11#  小企鵝王 ~      妖魔#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L12#機器章魚王 ~  葛雷族人#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L13#      河豚 ~    食人花#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L14#  鋼之肥肥 ~紅色機器人#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L15#    骷髏犬 ~強化鋼鐵穆太#k ( #r200#b 楓葉 #r。#k)#l #d\n\r #L16#      雲狐 ~小企鵝王玩偶#k ( #r200#b 楓葉 #r。#k)#l #d\n\r #L17#  草莓珍奶 ~親衛隊企鵝王#k ( #r200#b 楓葉 #r。#k)#l #d\n\r #L18#   梅卡堤安~    青螃蟹#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L19#    蔘罈壺 ~    黑鱷魚#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L20#  日光精靈 ~      巫婆#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L21#    石巨人 ~黑曜石巨人#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L22#      白狼 ~      河童#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L23#  街頭混混 ~變身娃娃機#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L24#  小丑柏非 ~    雪吉拉#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L25#  厄運死神 ~      冥鐘#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L26#月牙牛魔王 ~骷髏指揮官#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L27#  黑格里芬 ~    扛壩子#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L28#  甜心香水 ~邪惡侏儒怪#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L29#  特貝爾芬 ~  邪惡綿羊#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L30#  多立百烈 ~  時間之眼#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L31#  人偶蘿絲 ~進化迅猛龍#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L32#致命烏賊怪 ~雙刀龍戰士#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L33#    幼年龍 ~  達納托斯#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L34#    化石龍 ~忘卻的神官#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L35#忘卻守護兵 ~  帝國護衛#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L36#    紅寶王 ~  九尾妖狐#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L37#    肯德熊 ~  格瑞芬多#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L38#    海怒斯 ~  葛雷金剛#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L39#    奇美拉 ~  利里諾斯#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L40#    萊伊卡 ~魔王巴洛古#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L41#葛雷雪吉拉 ~    龍騎士#k   ( #r200#b 楓葉 #r。#k)#l #d\n\r #L42#      找碴#k               ( #r100#b 楓葉 #r。#k)#l");

    } else if (status == 1) {
	
sel = selection;
	
if (sel == 1) {
	    
cm.sendYesNo("#b一組9 ~ 10張的怪物卡#k需要 #r200#b 楓葉 #r。#b您要兌換嗎#r？#k");

	} else if (sel == 2) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2380010,5); //   三眼章魚		    
cm.gainItem(2380011,5); //
cm.gainItem(2380012,5); //
cm.gainItem(2380013,5); //
cm.gainItem(2380014,5); //
cm.gainItem(2380015,5); //
cm.gainItem(2380016,5); //
cm.gainItem(2380017,5); //
cm.gainItem(2380018,5); //
cm.gainItem(2380019,5); //     穆魯昆
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	}
	
cm.sendYesNo("#b一組10張找碴卡#k需要 #r100#b 楓葉 #b。\n\r 一組9 ~ 10張的怪物卡#k需要 #r200#b楓葉 #r#b您要兌換嗎#r？#k");

	} else if (sel == 3) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2380020,5); // 奇怪的肥肥		    
cm.gainItem(2381000,5); //
cm.gainItem(2381001,5); //
cm.gainItem(2381002,5); //
cm.gainItem(2381003,5); //
cm.gainItem(2381004,5); //
cm.gainItem(2381005,5); //
cm.gainItem(2381006,5); //
cm.gainItem(2381007,5); //
cm.gainItem(2381008,5); //   黑斧木妖
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 4) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2381009,5); //       海膽		    
cm.gainItem(2381010,5); //
cm.gainItem(2381011,5); //
cm.gainItem(2381012,5); //
cm.gainItem(2381013,5); //
cm.gainItem(2381014,5); //
cm.gainItem(2381015,5); //
cm.gainItem(2381016,5); //
cm.gainItem(2381017,5); //
cm.gainItem(2381018,5); //   石面怪人
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 5) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2381019,5); //  耳罩蜥蜴	    
cm.gainItem(2381020,5); //
cm.gainItem(2381021,5); //
cm.gainItem(2381022,5); //
cm.gainItem(2381023,5); //
cm.gainItem(2381024,5); //
cm.gainItem(2381025,5); //
cm.gainItem(2381026,5); //
cm.gainItem(2381027,5); //
cm.gainItem(2381028,5); // 成年仙人掌
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	} else if (sel == 6) {
	    		
if(cm.haveItem(4001126,200)) {

cm.gainItem(2381029,5); //     花鯰魚	    
cm.gainItem(2381030,5); //
cm.gainItem(2381031,5); //
cm.gainItem(2381032,5); //
cm.gainItem(2381033,5); //
cm.gainItem(2381034,5); //
cm.gainItem(2381035,5); //
cm.gainItem(2381036,5); //
cm.gainItem(2381037,5); //
cm.gainItem(2381038,5); //     火石球
cm.gainItem(4001126,-200); //  楓葉
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

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
		    
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		   
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

    } else if (status == 2) {
	
if (sel == 1) {
	    
if(cm.haveItem(4001126,200)) {
		    
cm.gainItem(2380000,5); //      嫩寶		    
cm.gainItem(2380001,5); //
cm.gainItem(2380002,5); //
cm.gainItem(2380003,5); //
cm.gainItem(2380004,5); //
cm.gainItem(2380005,5); //
cm.gainItem(2380006,5); //
cm.gainItem(2380007,5); //
cm.gainItem(2380008,5); //
cm.gainItem(2380009,5); //   緞帶肥肥
cm.gainItem(4001126,-200); //  楓葉		   
cm.sendNext("#b兌換成功 #r~ ");

		} else {
		    
cm.sendNext("#b您的楓葉不足 #r。#k");

		}

	    } else {
		
cm.sendOk("#b您的楓葉不足 #r！");

	    }
	    
cm.safeDispose();

	}

    }