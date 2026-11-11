var status = -1;
var minLevel = 51; // 35
var maxLevel = 70; // 65

var minPartySize = 6;
var maxPartySize = 6;

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
	if (cm.getMapId() == 920010000) { //inside orbis pq
		cm.sendOk("我們必須拯救張伯倫·埃克！恢復20個雲塊！");
		cm.dispose();
		return;
	}
    if (status == 0) {
	for (var i = 4001044; i < 4001064; i++) {
		cm.removeAll(i); //holy
	}
	if (cm.getParty() == null) { // No Party
	    cm.sendSimple("若想組隊破解任務請由隊長跟我交談\r\n\r\n#rRequirements: " + minPartySize + " 組隊隊員 " + minLevel + " 等到 " + maxLevel + "#b\r\n#L0#我要用40個女神的羽翼兌換女神手鐲#l");
	} else if (!cm.isLeader()) { // Not Party Leader
	    cm.sendSimple("若想組隊破解任務請由隊長跟我交談\r\n\r\n#rRequirements: " + minPartySize + " 組隊隊員 " + minLevel + " 等到 " + maxLevel + "#b\r\n#L0#我要用40個女神的羽翼兌換女神手鐲#l");
	} else {
	    // Check if all party members are within PQ levels
	    var party = cm.getParty().getMembers();
	    var mapId = cm.getMapId();
	    var next = true;
	    var levelValid = 0;
	    var inMap = 0;
	    var it = party.iterator();

	    while (it.hasNext()) {
		var cPlayer = it.next();
		if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
		    levelValid += 1;
		} else {
		    next = false;
		}
		if (cPlayer.getMapid() == mapId) {
		    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
		}
	    }
	    if (party.size() > maxPartySize || inMap < minPartySize) {
		next = false;
	    }
	    if (next) {
		var em = cm.getEventManager("OrbisPQ");
		if (em == null) {
		    cm.sendSimple("系統發生錯誤，請重啟伺服器 #b\r\n#L0#我要用40個女神的羽翼兌換女神手鐲#l");
		} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			em.startInstance(cm.getParty(), cm.getMap());
			cm.dispose();
			return;
		    } else {
			cm.sendSimple("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.#b\r\n#L0#我要用40個女神的羽翼兌換女神手鐲#l");
		    }
		}
	    } else {
		cm.sendSimple("您的隊伍無效。請遵守以下要求:\r\n\r\n#r隊伍人數: " + minPartySize + " 全體隊員等級 " + minLevel + " 等到 " + maxLevel + "#b\r\n#L0#我要用40個女神的羽翼兌換女神手鐲#l");
	    }
	}
    } else { //broken glass
	if (!cm.canHold(1082232,1)) {
	    cm.sendOk("Make room for this Wristband.");
	} else if (cm.haveItem(4001158,40)) {
	    cm.gainItem(1082232, 1, true);
	    cm.gainItem(4001158, -40, true); 
	} else {
	    cm.sendOk("你沒有 40 個 #t4001158#.");
	}
	cm.dispose();

    }
}