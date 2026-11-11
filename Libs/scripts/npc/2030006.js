/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
var status = 0;
var qChars = new Array ("Q1: 楓之谷中，從等級1到等級2需要多少經驗值？#10#12#15#20#3",
    "Q1: 根據不同職業為了第1次轉職所要求的能力，被不正確敘述的是哪一個？#  劍士 35 力量#  盜賊 20 幸運#  法師 20 智力#弓箭手 25 敏捷#2",
    "Q1: 被怪物攻擊時特別的異常狀態沒有被正確說明的是哪一個？#虛弱 - 移動速度降低#封印 - 不能使用技能#黑暗 - 命中下降#詛咒 - 減少經驗#1",
    "Q1: 根據不同職業的第1次轉職需要有25敏捷的是哪一個？#劍士#弓箭手#法師#盜賊#2");
var qItems = new Array( "Q2: 下列怪物中，哪組怪物與打倒它所能得到的戰利品是正確對應關係的？#大幽靈 - 幽靈頭帶#蝙蝠 - 蝙蝠翅膀#木妖 - 木柴#肥肥 - 葉子#2",
    "Q2: 下列怪物中，哪組怪物與打倒它所能得到的戰利品是不正確對應關係的？#緞帶肥肥 - 蝴蝶結#殭屍菇菇 - 道符#嫩寶 - 綠寶殼#食人花 - 食人花的葉子#4",
    "Q2: 楓之谷中下列藥品中，哪組藥品與功效是正確的對應關係？#白色藥水 - 恢復HP約250#特殊藥水 - 恢復HP、MP全滿#紅色藥水 - 恢復HP約100#人蔘汽水 - 恢復40%的HP和的MP#4",
    "Q2: 楓之谷中下列藥品中，哪組藥水可以恢復HP50%MP50%？#特殊藥水#超級藥水#西瓜#礦泉水#1",
    "Q2: 楓之谷中下列藥品中，哪組藥品與功效是不正確的對應關係？#藍色藥水 - 恢復MP約100#活力藥水 - MP可恢復300#清晨之露 - 恢復MP約3000#紅色藥水 - 恢復HP約50#3");
var qMobs = new Array(  "Q3: 綠蘑菇、藍水靈、斧木妖、三眼章魚，哪個是等級最高的怪物？#綠蘑菇#三眼章魚#藍水靈#斧木妖#4",
    "Q3: 楓葉村不存在哪個怪物？#小石球#嫩寶#藍寶#蘑菇仔#1",
    "Q3: 在墮落城市裡見不到的NPC是誰？#洪先生#後街吉姆#休咪#魯克#4",
    "Q3: 楓之谷最初遇見的NPC是誰？#麗娜#瑞恩#皮奧#希娜#4",
    "Q3: 在畫面中會到處亂飛的怪物是誰？#巫婆#小幽靈#赤龍#貪吃鬼#1",
    "Q3: 能夠反覆執行的任務是哪一個？#秀茲的興趣#尋找《上古魔書》#克林的記憶#艾溫的玻璃鞋#4",
    "Q3: 為了進行2次轉職收集好30個黑珠後轉職教官會給你的物品是什麼?#轉職教官的信#英雄證書#英雄的勳章#介紹信#2");
var qQuests = new Array("Q4:要求級別最高的任務是哪一個？#蝸牛的傳說#蓓蒂博士的化石研究#亞凱斯特和黑暗水晶#柰洛的項鏈#3",
    "Q4: 下面哪個職業不是二轉中出現的職業? #格鬥家#刺客#槍手#弩弓手#1",
    "Q4: 在維多利亞港看不到哪個NPC？#賽恩#特奧#坤#約翰#1",
    "Q4: 弓箭手村的瑪雅為了治好自己的病讓你給她的物品是哪一個?#補藥#奇怪的藥#克里夫的特殊藥水#應急治療藥#2",
    "Q4: 維多利亞島中沒有的村落？#奇幻村#彩虹村#魔法森林#墮落城市#2",
    "Q4: 在天空之城不存在哪個怪物？#黑鱷魚#小石球#艾利傑#藍獨角獸#1");
var qTowns = new Array( "Q5:在維多利亞島的勇士部落不能看到的NPC是誰？ #蓓蒂博士#酋長#辛德#義安#1",
    "Q5: 在墮落城市能夠見到一個離家的少年阿勒斯，他的父親是誰？#麥吉#小攤老板#斯坦長老#伊卡路斯#3",
    "Q5: 在維多利亞島和螞蟻洞看不到的怪物是哪一個？#石球#刺菇菇#殭屍菇菇#蝙蝠#1",
    "Q5: 在天空之城見不到的NPC是誰？#管家艾瑪#煉金術師#吉妮#尤莉亞#3",
    "Q5: 在艾納斯島的冰原雪域看不見的NPC是誰?#杰德#高登#亞凱斯特#露妮#4",
    "Q5: 尋找《上古魔書》任務完成後,找誰換取獎勵?#煉金術師#索菲亞#史卡圖勒#亞凱斯特#4");
var correctAnswer = 0;

function start() {
	if (cm.haveItem(4031058, 1)) {
		cm.sendOk("#h #,你已經有了 #t4031058#!");
		cm.dispose();
	}
    if (!cm.haveItem(4031058) || cm.getQuestStatus(100102) != 1) {
        cm.sendNext("我是#b神聖的石頭#k\r\n看你一路走來，實屬不易\r\n請好好把握這次挑戰的機會。");
    }
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0) {
            cm.sendOk("我想\r\n你應該有不少的#b黑暗水晶#k吧．．．");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 1)
            cm.sendNextPrev("獻上#v4005004##b黑暗水晶#k，我會隨機抽#r５#k個問題來考你\r\n若能全部答對，你將獲得#v4031058##b智慧項鏈#k。");
        else if (status == 2) {
            if (!cm.haveItem(4005004)) {
                cm.sendOk("#r#h ##k，你沒有#b黑暗水晶#k");
                cm.dispose();
            } else {
                cm.gainItem(4005004, -1);
                cm.sendSimple("#b測驗開始 #k!\r\n\r\n" + getQuestion(qChars[Math.floor(Math.random() * qChars.length)]));
                status = 2;
            }
        } else if (status == 3) {
            if (selection == correctAnswer)
                cm.sendOk("答對了，繼續下一題。");
            else {
                cm.sendOk("答錯了!\r\n很抱歉，你必須再給我一個 #b黑暗水晶#k 才能繼續挑戰!");
                cm.dispose();
            }
        } else if (status == 4)
            cm.sendSimple("#b測驗開始 #k!\r\n\r\n" + getQuestion(qItems[Math.floor(Math.random() * qItems.length)]));
        else if (status == 5) {
            if (selection == correctAnswer)
                cm.sendOk("答對了，繼續下一題。");
            else {
                cm.sendOk("答錯了!\r\n很抱歉，你必須再給我一個 #b黑暗水晶#k 才能繼續挑戰!");
                cm.dispose();
            }
        } else if (status == 6) {
            cm.sendSimple("#b測驗開始 #k!\r\n\r\n" + getQuestion(qMobs[Math.floor(Math.random() * qMobs.length)]));
            status = 6;
        } else if (status == 7) {
            if (selection == correctAnswer)
                cm.sendOk("答對了，繼續下一題。");
            else {
                cm.sendOk("答錯了!\r\n很抱歉，你必須再給我一個 #b黑暗水晶#k 才能繼續挑戰!");
                cm.dispose();
            }
        } else if (status == 8)
            cm.sendSimple("#b測驗開始 #k!\r\n\r\n" + getQuestion(qQuests[Math.floor(Math.random() * qQuests.length)]));
        else if (status == 9) {
            if (selection == correctAnswer) {
                cm.sendOk("答對了，繼續下一題。");
                status = 9;
            } else {
                cm.sendOk("答錯了!\r\n很抱歉，你必須再給我一個 #b黑暗水晶#k 才能繼續挑戰!");
                cm.dispose();
            }
        } else if (status == 10) {
            cm.sendSimple("#r#h ##k，這是最後一個問題，若能成功通關\r\n你將獲得#b智慧項鍊#k，並且送你回到公館。 \r\n\r\n" + getQuestion(qTowns[Math.floor(Math.random() * qTowns.length)]));
            status = 10;
        } else if (status == 11) {
            if (selection == correctAnswer) {
                cm.completeQuest(100102);
                cm.gainItem(4031058, 1);
                cm.sendOk("直接傳送");
                cm.warp(211000001, 0);
                cm.dispose();
            } else {
                cm.sendOk("真可惜...就差一題了，請再接再厲 。\r\n很抱歉，你必須再給我一個 #b黑暗水晶#k 才能繼續挑戰!");
                cm.dispose();
            }
        }
    }
}
function getQuestion(qSet){
    var q = qSet.split("#");
    var qLine = q[0] + "\r\n\r\n#L0#" + q[1] + "#l\r\n#L1#" + q[2] + "#l\r\n#L2#" + q[3] + "#l\r\n#L3#" + q[4] + "#l";
    correctAnswer = parseInt(q[5],10);
    correctAnswer--;
    return qLine;
}