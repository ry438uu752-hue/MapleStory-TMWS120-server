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
importPackage(Packages.client);
importPackage(Packages.server.quest);

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 1) {
            cm.sendOk("等你下定決心後再來找我");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (cm.getPlayer().getJob() <= 200 || cm.getPlayer().getJob() >= 300 || cm.getPlayer().getJob() % 10 != 0) {
                cm.sendOk("#r真神#k與你同在!"); //非本職
                cm.dispose();
                return;
            }
            if (cm.getQuestStatus(100102) == 2 ) { //完成
                cm.sendNext("恭喜你通過考驗，願#r真神#k與你同在!");
            } else if (cm.getQuestStatus(100102) == 1 ) { //開始
                cm.sendOk("探索冰原雪域裡的一處隱地(#r雪原聖地#k)，裡面有一顆#b神聖之石#k");
                cm.dispose();
            } else if (cm.getQuestStatus(100101) == 2) { //完成
                cm.sendNext("#r#h0##k，進入#b巫婆森林Ⅱ#k找到次元之門，並且打敗裡面的#b漢斯的分身#k。");
            } else if (cm.getQuestStatus(100100) == 1 ) { // 開始
                cm.sendOk("還不快回去找#b漢斯#k。");
                cm.dispose();
            } else if ((cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230) && cm.getPlayer().getJob() % 10 == 0 && cm.getPlayer().getLevel() >= 70) {
                cm.sendNext("長期歷練使你的能力都得到了相當大的提昇");
            } else {
                cm.sendOk("請將2轉技能使用完畢,才能進行第三次轉職");
                cm.dispose();
            }
        } else if (status == 1) {
			if(cm.getPlayer().getJob() % 10 != 0){
				cm.dispose();
			}
            if (cm.getQuestStatus(100102) == 2 ) { //完成
                cm.changeJob(cm.getPlayer().getJob() + 1);
                cm.gainItem(4031057, -1);
	        cm.gainItem(4031058, -1);
                cm.sendOk("你變得更加強大了!");
                cm.dispose();
                } else if (cm.getQuestStatus(100101) == 2) { //完成
				if(cm.haveItem(4031057)){ //力量項鍊
				cm.sendAcceptDecline("你符合資格了，接下來將進行最終的測驗。");
			} else {
				cm.sendOk("將#b力量項鍊#k拿來，證明你的價值 !");
				cm.dispose();
			}
                        } else {
                                cm.sendAcceptDecline("#r#h0##k，你仍有進步的空間，願意接受挑戰嗎?");
                        }
                } else if (status == 2) {
                       if (cm.getQuestStatus(100101) == 2) { //完成
				if(cm.haveItem(4031057)){
				cm.startQuest(100102);
				cm.sendOk("尋找隱藏在#r雪原聖地#k裡的#b神聖之石#k，並且帶回#b智慧項鍊#k");
				cm.dispose();
		} else {
				cm.sendOk("你沒有#b智慧項鍊#k");
				cm.dispose();
		}
                } else {
                                cm.startQuest(100100);
                                cm.sendOk("回去找#b漢斯#k吧，他能讓你變得更強");
                                cm.dispose();
                                }
                      }
           }
}