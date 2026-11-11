/* */
var status = 0;
var beauty = 0;
var regprice = 1000000;
var vipprice = 1000000;
var skin = Array(0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11);
hair_Colo_new = [];
var facetype;
var colors = Array();

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("只要你有 #b#t5152049##k, 就可以透過我們的造型服務系統，打造成任何你想要的風格。 \r\n#L1##b膚色#k#l \r\n#L2##b髮型#k#l \r\n#L5##b髮色#k#l \r\n#L6##b臉型(眼睛)#k#l \r\n#L8##b臉型(瞳色)#k#l");						
		        } else if (status == 1) {
			       if (selection == 0) {
				beauty = 0;
				cm.sendSimple("Which coupon would you like to buy?\r\n#L0#Cosmetic Lenses for " + regprice + " mesos: #i5152042##t5152042##l\r\n#L1#Cosmetic Lenses for " + vipprice + " mesos: #i5152041##t5152041##l");
			            } else if (selection == 1) {
                                beauty = 1;
                                cm.askAvatar("#b你喜歡哪個顏色呢#r ?#k", skin);
                        } else if (selection == 2) {
                                var hair = cm.getPlayerStat("HAIR");
                                hair_Colo_new = [];
                                beauty = 2;
                               if (cm.getPlayerStat("GENDER") == 0) {
                                hair_Colo_new = [30020, 30120, 30200, 30210, 30220, 30330, 30370, 30440, 30490, 30510, 36700, 36710, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 36720, 36730, 36740, 36750, 30660, 36760, 36770, 36780, 36800, 36810, 30730, 30740, 30750, 30760, 30770, 30790, 30800, 30810, 36820, 36830, 36840, 36850, 36920, 30870, 38880, 30890, 30900, 30910, 30920, 30930, 30940, 30950, 30990, 33000, 33030, 33080, 36930, 33100, 36950, 36980, 33130, 33140, 33150, 33170, 33180, 33190, 33200, 33210, 33220, 33230, 33240, 33250, 33260, 33270, 33280, 33290, 36990, 33310, 33320, 33330, 33340, 33350, 33360, 33370, 33380, 33390, 33400, 33410, 33420, 33430, 33440, 33450, 33460, 33470, 33480, 33490, 33500, 33510, 36320, 33530, 33540, 33550, 33580, 33590, 33600, 33610, 33620, 33630, 33640, 33650, 33660, 33670, 33680, 33690, 33700, 33710, 33720, 33730, 33740, 33750, 33760, 33770, 33780, 33790, 36690];
	                        } else  {		
                                hair_Colo_new = [31000, 31150, 31160, 31230, 31240, 31260, 31270, 31320, 31330, 31420, 31440, 31490, 31520, 31530, 31540, 31550, 31570, 31590, 31610, 31640, 31650, 31670, 31680, 31690, 31710, 31720, 31740, 31750, 31760, 32140, 32560, 32580, 32650, 32660, 34120, 34150, 34160, 34170, 34190, 34210, 34240, 34260, 34300, 34310, 34330, 34870, 34490, 34510, 34590, 34600, 34610, 34670, 34680, 34690, 34700, 34710, 34720, 34730, 34740, 34750, 34770, 34780, 34790, 34800, 34810, 34830, 34840, 34850, 34860, 34880, 34900, 34910, 34940, 34950, 34960, 34970, 37000, 37010, 37020, 37030, 37040, 37050, 37060, 37070, 37080, 37090, 37100, 37110, 37120, 37130, 37140, 37190, 37210, 37220, 37230, 37260, 37280, 37300, 37310, 37320, 37340, 37350, 37370, 37380, 37400, 37450, 37490, 37500, 37510, 37520, 37530, 37560, 37570, 37580, 37610, 37630, 37640, 37650, 37660, 37670, 37680, 37700, 37710, 37720, 37730];
	                        }	    
                                for (var i = 0; i < hair_Colo_new.length; i++) {
                                hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	                        }
                                cm.askAvatar("#b你喜歡哪個髮型呢#r ?#k", hair_Colo_new);
                        } else if (selection == 3) {
				beauty = 3;
				if (cm.getChar().getGender() == 0) {
					var current = cm.getChar().getFace()% 100 + 20000;
				}
				if (cm.getChar().getGender() == 1) {
					var current = cm.getChar().getFace()% 100 + 21000;
				}
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("#b你喜歡哪個顏色呢#r ?#k", colors);
                        } else if (selection == 4) {
				beauty = 4;
				if (cm.getChar().getGender() == 0) {
					var current = cm.getChar().getFace()% 100 + 20000;
				}
				if (cm.getChar().getGender() == 1) {
					var current = cm.getChar().getFace()% 100 + 21000;
				}
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("#b你喜歡哪個顏色呢#r ?#k", colors);
                        } else if (selection == 5) {
                                var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
                                hair_Colo_new = [];
                                beauty = 5;
                                for (var i = 0; i < 8; i++) {
                                hair_Colo_new[i] = currenthaircolo + i;
	                        }
                                cm.askAvatar("#b你喜歡哪個顏色呢#r ?#k", hair_Colo_new);
                        } else if (selection == 6) {
				beauty = 6;
                                var face = cm.getPlayerStat("FACE");
                                if (cm.getPlayerStat("GENDER") == 0) {  
                                facetype = [20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025, 20027, 20028, 20029, 20031, 20032, 20036, 20037, 20038, 20040, 20043, 20044, 20045, 20046, 20047, 20048, 20049, 20050, 20052, 20053, 20055, 20056, 20057, 20058, 20059, 20060, 20061, 20062, 20063, 20064, 20065, 20066, 20067, 20068, 20069, 20070, 20073, 20074, 20075, 20076, 20077, 20078, 20080, 20081, 20082, 20083, 20084, 20085, 20086, 20087, 20088, 20089, 20090, 20093, 20094, 20095, 20097, 20098, 20099, 23000, 23001, 23002, 23003, 23004, 23005, 23006, 23008, 23009, 23010, 23011, 23012, 23013, 23014, 23016, 23020, 23023, 23024, 23025, 23028, 23031, 23032, 23035, 23036, 23037, 23040, 23043, 23045, 23046, 23047, 23048, 23051, 23057, 23065, 23066, 23073, 23080, 23081, 23082, 23098, 23099];
                    	        } else {	    
                                facetype = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21015, 21016, 21017, 21018, 21019, 21020, 21021, 21022, 21023, 21024, 21026, 21027, 21028, 21029, 21031, 21035, 21036, 21038, 21041, 21042, 21043, 21044, 21045, 21046, 21047, 21048, 21049, 21050, 21052, 21053, 21054, 21055, 21056, 21057, 21058, 21059, 21060, 21061, 21062, 21063, 21064, 21065, 21068, 21069, 21070, 21071, 21072, 21073, 21074, 21075, 21076, 21077, 21078, 21079, 21080, 21081, 21082, 21083, 21084, 21085, 21086, 21087, 21088, 21089, 21090, 21091, 21092, 21093, 21094, 21095, 21096, 21097, 21098, 24001, 24002, 24003, 24004, 24005, 24006, 24007, 24008, 24009, 24010, 24012, 24014, 24015, 24016, 24019, 24020, 24022, 24024, 24026, 24027, 24031, 24033, 24034, 24035, 24037, 24040, 24042, 24043, 24044, 24045, 24048, 24055, 24063, 24064, 24071, 24073, 24078, 24080, 24096, 24097];
                     	        }	
                                for (var i = 0; i < facetype.length; i++) {   
                                facetype[i] = facetype[i] + face % 1000 - (face % 100);
                    	        }
                                cm.askAvatar("#b你喜歡哪個樣式呢#r ?#k", facetype);
                        } else if (selection == 7) {
				beauty = 7;
				if (cm.getChar().getGender() == 0) {
					var current = cm.getChar().getFace()% 100 + 20000;
				}
				if (cm.getChar().getGender() == 1) {
					var current = cm.getChar().getFace()% 100 + 21000;
				}
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("#b你喜歡哪個顏色呢#r ?#k", colors);
			} else if (selection == 8) {
				beauty = 8;
				if (cm.getChar().getGender() == 0) {
					var current = cm.getChar().getFace()% 100 + 20000;
				}
				if (cm.getChar().getGender() == 1) {
					var current = cm.getChar().getFace()% 100 + 21000;
				}
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("#b你喜歡哪個顏色呢#r ?#k", colors);
			                }
		                }
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				if (cm.setAvatar(5152049, skin[selection]) == 1){
					cm.sendOk("是不是覺得煥然一新了呢！");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				        }
			        }
			if (beauty == 2){
				if (cm.setAvatar(5152049, hair_Colo_new[selection]) == 1) {
					cm.sendOk("新髮型是不是顯得更加亮麗呢 !");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
                        if (beauty == 3){
				if (cm.haveItem(5152049) == true){
					cm.gainItem(5152049, -1);
					cm.setFace(colors[Math.floor(Math.random() * colors.length)]);
					cm.sendOk("完成了!");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
			if (beauty == 4){
				if (cm.haveItem(5152049) == true){
					cm.gainItem(5152049, -1);
					cm.setFace(colors[selection]);
					cm.sendOk("完成了!");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
                        if (beauty == 5){
                                if (cm.setAvatar(5152049, hair_Colo_new[selection]) == 1) {
                                        cm.sendOk("新顏色是不是顯得更加亮麗呢 !");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
			if (beauty == 6){
                                if (cm.setAvatar(5152049, facetype[selection]) == 1) {
                                        cm.sendOk("如何！是不是覺得煥然一新呢！");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
                        if (beauty == 7){
				if (cm.haveItem(5152049) == true){
					cm.gainItem(5152049, -1);
					cm.setFace(colors[Math.floor(Math.random() * colors.length)]);
					cm.sendOk("#b謝謝您的惠顧#r ！#k");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
			if (beauty == 8){
				if (cm.haveItem(5152049) == true){
					cm.gainItem(5152049, -1);
					cm.setFace(colors[selection]);
					cm.sendOk("你的眼神更迷人了!");
				} else {
					cm.sendOk("#b你沒有#b#t5152049##k#r ！#k");
				}
			}
			if (beauty == 0){
				if (selection == 0 && cm.getMeso() >= regprice) {
					cm.gainMeso(-regprice);
					cm.gainItem(5152049, 1);
					cm.sendOk("Enjoy!");
				} else if (selection == 1 && cm.getMeso() >= vipprice) {
					cm.gainMeso(-vipprice);
					cm.gainItem(5152049, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
			}
		}
	}
}