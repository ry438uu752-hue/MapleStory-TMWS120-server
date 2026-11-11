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
	
cm.sendSimple("#b想對村莊做點貢獻嗎 #r？#b\n\r 根據您的貢獻我們將給予表揚的勳章 #r~ #k \n\r #L1#弓箭手村捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l  \n\r #L2#魔法森林捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l  \n\r #L3#勇士之村捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L4#墮落城市捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L5#奇幻村捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L6#鯨魚號捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L7#冰原雪域捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L8#水世界捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L9#玩具城捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L10#地球防衛本部捐獻王勳章#k( #r100 ' 000 ' 000 #b楓幣#k)#l  \n\r #L11#童話村捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L12#神木村捐獻王勳章#k      ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L13#桃花仙境捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L14#靈藥幻境捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L15#納希沙漠捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L16#瑪迦提亞城捐獻王勳章#k  ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L17#維多利亞港捐獻王勳章#k  ( #r100 ' 000 ' 000 #b楓幣#k)#l   \n\r #L18#天空之城捐獻王勳章#k    ( #r100 ' 000 ' 000 #b楓幣#k)#l");

    } else if (status == 1) {

sel = selection;
	
if (sel == 1) {
	    
if (cm.canHold(1142014,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142014,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 2) {
	    
if (cm.canHold(1142015,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142015,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 3) {
	    
if (cm.canHold(1142016,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142016,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 4) {
	    
if (cm.canHold(1142017,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142017,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

	    }
	    
cm.safeDispose();

	}
if (sel == 5) {
	    
if (cm.canHold(1142018,1) && cm.getMeso() >= 100000000) {
		
if (!cm.haveItem(0)) {
		    
cm.gainMeso(-100000000);
		    
cm.gainItem(1142018,1);
		   
 cm.sendNext("#b謝謝您的惠顧 #r~ ");

		} else {
		    
cm.sendNext("#b你已經有#r魚餌#b了 #r, 這是一般#r魚餌#b的#r使用限制 #k, #b請使用完畢後再來找我 #r。#k");

		}

	    } else {
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

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
		
cm.sendOk("#r您的楓幣不足或已經擁有勳章 #b！");

	    }
	    
cm.safeDispose();

	}


    }

}