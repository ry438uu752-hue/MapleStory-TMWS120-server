/*
 Map : Mu Lung Training Center
 Npc : So Gong
 Desc : Training Center Start
 */
var status = -1;
var sel;
var mapid;

function start() {
    mapid = cm.getMapId();

    if (mapid == 925020001) {
        cm.sendSimple("我們主人是武陵道場的師傅。你想要挑戰我們師傅？不要說我沒提醒你，他是最強的。 \r #b#L0#個人挑戰#l \n\r #L1#組隊挑戰#l \n\r #L2#兌換腰帶#l \n\r #L3#重置點數#l \n\r #L5#什麼是武陵道場?#l");
    } else if (isRestingSpot(mapid)) {
        cm.sendSimple("我很驚訝，你竟然能走到這一步，但我仍要提醒你，接下來的關卡可沒你想像中的簡單，你想繼續挑戰嗎？#b \n\r #L0# 我想繼續#l \n\r #L1# 我想離開#l \n\r #L2# 紀錄我目前所在的樓層#l");
    } else {
        cm.sendYesNo("你要離開這裡嗎？");
    }
}

function action(mode, type, selection) {
    if (mapid == 925020001) {
        if (mode == 1) {
            status++;
        } else {
            cm.dispose();
            return;
        }
        if (status == 0) {
            sel = selection;

            if (sel == 5) {
                cm.sendNext("武陵道場是一個由三十八個樓層所組成的大型培訓設施。 每一個樓層皆有不同屬性的怪物把關。我師父是整個道場最強大的人，他創造了這驚人的訓練塔並鎮守於頂樓。 當然，你想到達最頂端的樓層並不是一件容易的事...");
                cm.dispose();
            } else if (sel == 3) {
                cm.sendYesNo("要知道如果你重置了你的訓練點數，那麼點數將會歸零。 一旦你清除了訓練點數並重新開始，那麼你將可以再次重新接收腰帶跟稀有裝備。 你真的要重置你的訓練點數嗎？");
            } else if (sel == 2) {
                cm.sendSimple("現在你的道場點數有 #b" + cm.getDojoPoints() + "#k. 我們的主人喜歡有才華的人，所以如果你有了足夠的道場點數，你就可以根據你的道場點數依序換取腰帶...\n\r #L0##i1132000:# #t1132000#(100)#l \n\r #L1##i1132001:# #t1132001#(300)#l \n\r #L2##i1132002:# #t1132002#(600)#l \n\r #L3##i1132003:# #t1132003#(1200)#l \n\r #L4##i1132004:# #t1132004#(2400)#l");
            } else if (sel == 1) {
                if (cm.getParty() != null) {
                    if (cm.isLeader()) {
                        cm.sendOk("走吧");
                    } else {
                        cm.sendOk("你不是隊長");
                    }
                } else {
                    cm.sendOk("你沒有組隊");
                    cm.dispose();
                    return;
                }
            } else if (sel == 0) {
                if (cm.getParty() != null) {
                    cm.sendOk("請退出你的組隊。");
                    cm.dispose();
                    return;
                }
                var record = cm.getQuestRecord(150000);
                var data = record.getCustomData();

                if (data != null) {
                    cm.warp(get_restinFieldID(parseInt(data)), 0);
                    record.setCustomData(null);
                } else {
                    cm.start_DojoAgent(true, false);
                }
                cm.dispose();
                // cm.sendYesNo("The last time you took the challenge yourself, you were able to reach Floor #18. I can take you straight to that floor, if you want. Are you interested?");
            }
        } else if (status == 1) {
            if (sel == 3) {
                cm.setDojoRecord(true);
                cm.sendOk("我已經將你的點數歸零");
            } else if (sel == 2) {
                var record = cm.getDojoRecord();
                var required = 0;

                switch (record) {
                    case 0:
                        required = 100;
                        break;
                    case 1:
                        required = 300;
                        break;
                    case 2:
                        required = 600;
                        break;
                    case 3:
                        required = 1200;
                        break;
                    case 4:
                        required = 2400;
                        break;
                }

                if (record == selection && cm.getDojoPoints() >= required) {
                    var item = 1132000 + record;
                    if (cm.canHold(item)) {
                        cm.gainItem(item, 1);
                        cm.setDojoRecord(false);
                        cm.sendOk("恭喜兌換成功！！");
                    } else {
                        cm.sendOk("你的背包空間不足");
                    }
                } else if (record != selection) {
                    cm.sendOk("請依照順序兌換腰帶");
                } else {
                    cm.sendOk("你的訓練點數不足");
                }
                cm.dispose();
            } else if (sel == 1) {
                cm.start_DojoAgent(true, true);
                cm.dispose();
            }
        }
    } else if (isRestingSpot(mapid)) {
        if (mode == 1) {
            status++;
        } else {
            cm.dispose();
            return;
        }

        if (status == 0) {
            sel = selection;

            if (sel == 0) {
                cm.dojoAgent_NextMap(true, true);
                //cm.getQuestRecord(150000).setCustomData(null);
                cm.dispose();
            } else if (sel == 1) {
                cm.askAcceptDecline("你要離開這裡嗎？");
            } else if (sel == 2) {
                if (cm.getParty() == null) {
                    var stage = get_stageId(cm.getMapId());

                    cm.getQuestRecord(150000).setCustomData(stage);
                    cm.sendOk("已保存你的紀錄");
                    cm.dispose();
                } else {
                    cm.sendOk("組隊挑戰模式無法記錄樓層");
                    cm.dispose();
                }
            }
        } else if (status == 1) {
            if (sel == 1) {
                if (cm.isLeader()) {
                    cm.warpParty(925020002);
                } else {
                    cm.warp(925020002);
                }
            }
            cm.dispose();
        }
    } else {
        if (mode == 1) {
            if (cm.isLeader()) {
                cm.warpParty(925020002);
            } else {
                cm.warp(925020002);
            }
        }
        cm.dispose();
    }
}

function get_restinFieldID(id) {
    var idd = 925020002;
    switch (id) {
        case 1:
            idd = 925020600;
            break;
        case 2:
            idd = 925021200;
            break;
        case 3:
            idd = 925021800;
            break;
        case 4:
            idd = 925022400;
            break;
        case 5:
            idd = 925023000;
            break;
        case 6:
            idd = 925023600;
            break;
    }
    for (var i = 0; i < 10; i++) {
        var canenterr = true;
        for (var x = 1; x < 39; x++) {
            var map = cm.getMap(925020000 + 100 * x + i);
            if (map.getCharactersSize() > 0) {
                canenterr = false;
                break;
            }
        }
        if (canenterr) {
            idd += i;
            break;
        }
    }
    return idd;
}

function get_stageId(mapid) {
    if (mapid >= 925020600 && mapid <= 925020614) {
        return 1;
    } else if (mapid >= 925021200 && mapid <= 925021214) {
        return 2;
    } else if (mapid >= 925021800 && mapid <= 925021814) {
        return 3;
    } else if (mapid >= 925022400 && mapid <= 925022414) {
        return 4;
    } else if (mapid >= 925023000 && mapid <= 925023014) {
        return 5;
    } else if (mapid >= 925023600 && mapid <= 925023614) {
        return 6;
    }
    return 0;
}

function isRestingSpot(id) {
    return (get_stageId(id) > 0);
}
