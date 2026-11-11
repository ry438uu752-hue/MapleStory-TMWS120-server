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
	
cm.sendSimple("#b請問您想要什麼騎寵 #r~ #k \n\r #L1##d馬鞍+野豬#k    ( #r20,000,000 #b楓幣#k)  \n\r #L2##d銀色鬃毛#k     ( #r50,000,000 #b楓幣#k)#l  \n\r #L3##d馬鞍+1階段龍#k ( #r10,000,000 #b楓幣#k)#l   \n\r #L4##d2階段龍#k      ( #r30,000,000 #b楓幣#k)#l   \n\r #L5##d3階段龍#k      ( #r50,000,000 #b楓幣#k)#l");

    } else if (status == 1) {

sel = selection;
	
if (sel == 1) {
	    
if (cm.canHold(1902000,1) && cm.getMeso() >= 20000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-20000000);
		    
cm.gainItem(1902000,1); //野豬 70等
cm.gainItem(1912000,1); //馬鞍
cm.teachSkill(1004,1,1); //怪物騎乘
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 2) {
	    
if (cm.canHold(1902001,1) && cm.getMeso() >= 50000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-50000000);
		    
cm.gainItem(1902000,-1); //野豬 70等
cm.gainItem(1902001,1); //銀色鬃毛 120等
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 3) {
	    
if (cm.canHold(1902040,1) && cm.getMeso() >= 10000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-10000000);
		    		    
cm.gainItem(1902040,1); //1階段龍 50等
cm.gainItem(1912033,1); //馬鞍
cm.teachSkill(20011004,1,1); //怪物騎乘
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 4) {
	    
if (cm.canHold(1902041,1) && cm.getMeso() >= 30000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-30000000);
		    
cm.gainItem(1902040,-1); //1階段龍 50等
cm.gainItem(1912033,-1); //馬鞍		    
cm.gainItem(1902041,1); //2階段龍 70等
cm.gainItem(1912034,1); //馬鞍
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 5) {
	    
if (cm.canHold(1902042,1) && cm.getMeso() >= 50000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-50000000);
		    
cm.gainItem(1902041,-1); //2階段龍 70等
cm.gainItem(1912034,-1); //馬鞍		    
cm.gainItem(1902042,1); //3階段龍 120等
cm.gainItem(1912035,1); //馬鞍
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 6) {
	    
if (cm.canHold(1142019,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142019,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 7) {
	    
if (cm.canHold(1142020,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142020,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 8) {
	    
if (cm.canHold(1142021,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142021,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 9) {
	    
if (cm.canHold(1142022,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142022,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 10) {
	    
if (cm.canHold(1142023,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142023,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 11) {
	    
if (cm.canHold(1142024,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142024,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 12) {
	    
if (cm.canHold(1142025,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142025,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 13) {
	    
if (cm.canHold(1142026,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142026,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 14) {
	    
if (cm.canHold(1142027,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142027,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 15) {
	    
if (cm.canHold(1142028,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142028,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 16) {
	    
if (cm.canHold(1142029,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142029,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 17) {
	    
if (cm.canHold(1142030,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142030,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 18) {
	    
if (cm.canHold(1142031,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142031,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 99) {
	    
if (cm.canHold(2000005,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(2000005,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有座騎 #b！");

	    }
	    
cm.safeDispose();

	}


    }

}