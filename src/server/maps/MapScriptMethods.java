/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import java.awt.Point;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;

public class MapScriptMethods {
    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects = new String[]{"\u6211\u7b49\u8457\u4f60\uff0c\u53ea\u8981\u4f60\u6301\u4e4b\u4ee5\u6046\uff0c\u5fc5\u80fd\u8d70\u5411\u6b63\u78ba\u7684\u9053\u8def", "\u4f55\u4f86\u7684\u52c7\u6c23\u6311\u6230\u6b66\u9675!", "\u6211\u6703\u8b93\u4f60\u5f8c\u6094\u6311\u6230\u6b66\u9675\u9053\u9928!", "\u6211\u559c\u6b61\u4f60\u7684\u76f4\u6027\u683c! \u4f46\u4e0d\u662f\u4f60\u7684\u9b6f\u83bd\u885d\u52d5!", "\u5982\u679c\u4f60\u771f\u7684\u90a3\u9ebc\u60f3\u8981\u5931\u6557\u7684\u8a71\uff0c\u5c31\u7e7c\u7e8c\u5f80\u524d\u5427!"};

    public static void startScript_FirstUser(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (onFirstUserEnter.fromString(scriptName)) {
            case dojang_Eff: {
                int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                int stage = temp - temp / 100 * 100;
                MapScriptMethods.sendDojoClock(c, MapScriptMethods.getTiming(stage) * 60);
                MapScriptMethods.sendDojoStart(c, stage - MapScriptMethods.getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                MapScriptMethods.handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                MapScriptMethods.reloadWitchTower(c);
                break;
            }
            case GhostF: {
                c.getPlayer().getMap().startMapEffect("\u9019\u500b\u5730\u5716\u611f\u89ba\u9670\u68ee\u68ee\u7684..\u6709\u7a2e\u83ab\u540d\u7684\u5947\u602a\u611f\u89ba..", 5120025);
                break;
            }
            case moonrabbit_mapEnter: {
                c.getPlayer().getMap().startMapEffect("\u7ca5\u74b0\u7e5e\u6708\u7403\u7684\u6708\u898b\u8349\u7a2e\u5b50\u548c\u4fdd\u8b77\u6708\u7403\u5154\u5b50\uff01", 5120016);
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926100000: {
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120021);
                        break;
                    }
                    case 926100001: {
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120021);
                        break;
                    }
                    case 926100100: {
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120021);
                        break;
                    }
                    case 926100200: {
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120021);
                        break;
                    }
                    case 926100203: {
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120021);
                        break;
                    }
                    case 926100300: {
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120021);
                        break;
                    }
                    case 926100401: {
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120021);
                    }
                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926110000: {
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120022);
                        break;
                    }
                    case 926110001: {
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120022);
                        break;
                    }
                    case 926110100: {
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120022);
                        break;
                    }
                    case 926110200: {
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120022);
                        break;
                    }
                    case 926110203: {
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120022);
                        break;
                    }
                    case 926110300: {
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120022);
                        break;
                    }
                    case 926110401: {
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120022);
                    }
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930000000: {
                        c.getPlayer().getMap().startMapEffect("\u9032\u5165\u50b3\u9001\u9ede\uff0c\u6211\u8981\u5c0d\u4f60\u5011\u65bd\u653e\u8b8a\u8eab\u9b54\u6cd5\u4e86\uff01", 5120023);
                        break;
                    }
                    case 930000100: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u6ec5\u6240\u6709\u602a\u7269\uff01", 5120023);
                        break;
                    }
                    case 930000200: {
                        c.getPlayer().getMap().startMapEffect("\u5c0d\u834a\u68d8\u65bd\u653e\u7a00\u91cb\u7684\u6bd2\u6db24\u500b\uff01", 5120023);
                        break;
                    }
                    case 930000300: {
                        c.getPlayer().getMap().startMapEffect("\u6211\u8ff7\u8def\u4e86...", 5120023);
                        break;
                    }
                    case 930000400: {
                        c.getPlayer().getMap().startMapEffect("\u62ff\u6de8\u5316\u4e4b\u73e0\u5f8c\u62ff10\u500b\u602a\u7269\u682a\u7d66\u6211\uff01", 5120023);
                        break;
                    }
                    case 930000500: {
                        c.getPlayer().getMap().startMapEffect("\u5f9e\u602a\u4eba\u66f8\u684c\u4e2d\u5c0b\u627e\u7d2b\u8272\u9b54\u529b\u77f3\uff01", 5120023);
                        break;
                    }
                    case 930000600: {
                        c.getPlayer().getMap().startMapEffect("\u5c07\u7d2b\u8272\u9b54\u529b\u77f3\u653e\u5728\u796d\u58c7\u4e0a\uff01", 5120023);
                    }
                }
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925100000: {
                        c.getPlayer().getMap().startMapEffect("Defeat the monsters outside of the ship to advance!", 5120020);
                        break;
                    }
                    case 925100100: {
                        c.getPlayer().getMap().startMapEffect("We must prove ourselves! Get me Pirate Medals!", 5120020);
                        break;
                    }
                    case 925100200: {
                        c.getPlayer().getMap().startMapEffect("Defeat the guards here to pass!", 5120020);
                        break;
                    }
                    case 925100300: {
                        c.getPlayer().getMap().startMapEffect("Eliminate the guards here to pass!", 5120020);
                        break;
                    }
                    case 925100400: {
                        c.getPlayer().getMap().startMapEffect("Lock the doors! Seal the root of the Ship's power!", 5120020);
                        break;
                    }
                    case 925100500: {
                        c.getPlayer().getMap().startMapEffect("Destroy the Lord Pirate!", 5120020);
                    }
                }
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() != 925100500 || em == null || em.getProperty("stage5") == null) break;
                int mobId = Randomizer.nextBoolean() ? 9300119 : 9300119;
                int st = Integer.parseInt(em.getProperty("stage5"));
                switch (st) {
                    case 1: {
                        mobId = 9300105;
                        break;
                    }
                    case 2: {
                        mobId = 9300106;
                    }
                }
                MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                if (c.getPlayer().getEventInstance() != null) {
                    c.getPlayer().getEventInstance().registerMonster(shammos);
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26));
                break;
            }
            case boss_Ravana: {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "Ravana has appeared!"));
                break;
            }
            case killing_BonusSetting: {
                c.getPlayer().getMap().resetFully();
                c.getSession().write((Object)MaplePacketCreator.showEffect("killing/bonus/bonus"));
                c.getSession().write((Object)MaplePacketCreator.showEffect("killing/bonus/stage"));
                Point pos1 = null;
                Point pos2 = null;
                Point pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else {
                    if (c.getPlayer().getMapId() < 926010070 || c.getPlayer().getMapId() > 926010089) break;
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                }
                for (int i = 0; i < spawnPer; ++i) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() < 921120100 || c.getPlayer().getMapId() >= 921120500) break;
                MapleMonster shammos = MapleLifeFactory.getMonster(9300275);
                if (c.getPlayer().getEventInstance() != null) {
                    c.getPlayer().getEventInstance().registerMonster(shammos);
                    if (c.getPlayer().getEventInstance().getProperty("HP") != null) {
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    } else {
                        c.getPlayer().getEventInstance().setProperty("HP", "50000");
                    }
                }
                c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                shammos.switchController(c.getPlayer(), false);
                c.getSession().write((Object)MaplePacketCreator.getNodeProperties(shammos, c.getPlayer().getMap()));
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0: {
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    }
                    case 1: {
                        c.getPlayer().getMap().startMapEffect("Break the boxes and eliminate the monsters!", 5120033);
                        break;
                    }
                    case 2: {
                        c.getPlayer().getMap().startMapEffect("Eliminate the Officer!", 5120033);
                        break;
                    }
                    case 3: {
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    }
                    case 4: {
                        c.getPlayer().getMap().startMapEffect("Find the way to the other side!", 5120033);
                    }
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().startMapEffect("Defeat the Ghost Ship Captain!", 5120033);
                break;
            }
            case balog_summon: 
            case easy_balog_summon: {
                break;
            }
            case metro_firstSetting: 
            case killing_MapSetting: 
            case Sky_TrapFEnter: 
            case balog_bonusSetting: {
                c.getPlayer().getMap().resetFully();
                break;
            }
            default: {
                System.out.println("\u672a\u8655\u7406\u7684\u8173\u672c : " + scriptName + ", \u578b\u614b : onUserEnter - \u5730\u5716ID " + c.getPlayer().getMapId());
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "\u672a\u8655\u7406\u7684\u8173\u672c : " + scriptName + ", \u578b\u614b : onUserEnter - \u5730\u5716ID " + c.getPlayer().getMapId());
            }
        }
    }

    public static void startScript_User(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        String data = "";
        switch (onUserEnter.fromString(scriptName)) {
            case cygnusTest: 
            case cygnusJobTutorial: {
                MapScriptMethods.showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913040100));
                break;
            }
            case shammos_Enter: {
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("shammos_LastStage", String.valueOf(c.getPlayer().getMapId() % 1000 / 100)));
                if (c.getPlayer().getEventInstance() == null || c.getPlayer().getMapId() != 921120500) break;
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 2022006);
                break;
            }
            case start_itemTake: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em == null || !em.getProperty("pre").equals("0")) break;
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 2013001);
                break;
            }
            case PRaid_W_Enter: {
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_expPenalty", "0"));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_Point", "-1"));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_Bonus", "-1"));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_Total", "-1"));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_Team", ""));
                c.getSession().write((Object)MaplePacketCreator.sendPyramidEnergy("PRaid_IsRevive", "0"));
                c.getPlayer().writePoint("PRaid_Point", "-1");
                c.getPlayer().writeStatus("Red_Stage", "1");
                c.getPlayer().writeStatus("Blue_Stage", "1");
                c.getPlayer().writeStatus("redTeamDamage", "0");
                c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case PRaid_D_Enter: 
            case PRaid_B_Enter: 
            case PRaid_WinEnter: 
            case PRaid_FailEnter: 
            case PRaid_Revive: 
            case metro_firstSetting: 
            case blackSDI: 
            case summonIceWall: 
            case onSDI: 
            case enterBlackfrog: 
            case Sky_Quest: 
            case dollCave00: 
            case dollCave01: 
            case shammos_Base: 
            case shammos_Result: 
            case Sky_BossEnter: 
            case Sky_GateMapEnter: 
            case balog_dateSet: 
            case balog_buff: 
            case outCase: 
            case Sky_StageEnter: 
            case dojang_QcheckSet: 
            case evanTogether: 
            case aranTutorAlone: 
            case Ghost: {
                break;
            }
            case evanAlone: {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case startEreb: 
            case mirrorCave: 
            case babyPigMap: 
            case evanleaveD: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case dojang_Msg: {
                c.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco: 
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), false, -1L);
                break;
            }
            case goAdventure: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case crash_Dragon: {
                MapScriptMethods.showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case getDragonEgg: {
                MapScriptMethods.showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case meetWithDragon: {
                MapScriptMethods.showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case PromiseDragon: {
                MapScriptMethods.showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case evanPromotion: {
                switch (c.getPlayer().getMapId()) {
                    case 900090000: {
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    }
                    case 900090001: {
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    }
                    case 900090002: {
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    }
                    case 900090003: {
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    }
                    case 900090004: {
                        c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                        c.getSession().write((Object)UIPacket.IntroLock(false));
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                    }
                }
                MapScriptMethods.showIntro(c, data);
                break;
            }
            case TD_MC_title: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                c.getSession().write((Object)UIPacket.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104000000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    c.getSession().write((Object)UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                }
                MapleQuest.MedalQuest m = null;
                block52: for (MapleQuest.MedalQuest mq : MapleQuest.MedalQuest.values()) {
                    for (int i : mq.maps) {
                        if (c.getPlayer().getMapId() != i) continue;
                        m = mq;
                        continue block52;
                    }
                }
                if (m == null || c.getPlayer().getLevel() < m.level || c.getPlayer().getQuestStatus(m.questid) == 2) break;
                if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                    MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                }
                if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                    MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                    StringBuilder sb = new StringBuilder("enter=");
                    for (int i = 0; i < m.maps.length; ++i) {
                        sb.append("0");
                    }
                    c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                    MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                }
                String quest = c.getPlayer().getInfoQuest(m.questid - 2005);
                MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(m.questid - 1995));
                if (stat.getCustomData() == null) {
                    stat.setCustomData("0");
                }
                int number = Integer.parseInt(stat.getCustomData());
                StringBuilder sb = new StringBuilder("enter=");
                boolean changedd = false;
                for (int i = 0; i < m.maps.length; ++i) {
                    boolean changed = false;
                    if (c.getPlayer().getMapId() == m.maps[i] && quest.substring(i + 6, i + 7).equals("0")) {
                        sb.append("1");
                        changed = true;
                        changedd = true;
                    }
                    if (changed) continue;
                    sb.append(quest.substring(i + 6, i + 7));
                }
                if (!changedd) break;
                c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(++number));
                c.getPlayer().dropMessage(-1, "\u8a2a\u554f " + number + "/" + m.maps.length + " \u500b\u5730\u5340.");
                c.getPlayer().dropMessage(-1, "\u7a31\u865f " + String.valueOf((Object)m) + " \u5df2\u5b8c\u6210\u4e86");
                c.getSession().write((Object)MaplePacketCreator.showQuestMsg("\u7a31\u865f " + String.valueOf((Object)m) + " \u5df2\u5b8c\u6210\u8a2a\u554f " + number + "/" + m.maps.length + " \u500b\u5730\u5340"));
                break;
            }
            case go10000: 
            case go1020000: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
            case go20000: 
            case go30000: 
            case go40000: 
            case go50000: 
            case go1000000: 
            case go2000000: 
            case go1010000: 
            case go1010100: 
            case go1010200: 
            case go1010300: 
            case go1010400: {
                c.getSession().write((Object)UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                break;
            }
            case goArcher: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                MapScriptMethods.showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                MapScriptMethods.showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: {
                MapScriptMethods.showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914090010: {
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    }
                    case 914090011: {
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    }
                    case 914090012: {
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    }
                    case 914090013: {
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    }
                    case 914090100: {
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    }
                    case 914090200: {
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                    }
                }
                MapScriptMethods.showIntro(c, data);
                break;
            }
            case iceCave: {
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000014), (byte)-1, (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000015), (byte)-1, (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000016), (byte)-1, (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000017), (byte)-1, (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000018), (byte)-1, (byte)0);
                c.getSession().write((Object)UIPacket.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin"));
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case rienArrow: {
                if (!c.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) break;
                c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                c.getSession().write((Object)UIPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21101) == 2 && c.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                break;
            }
            case check_count: {
                if (c.getPlayer().getMapId() != 950101010 || c.getPlayer().haveItem(4001433, 20) && c.getPlayer().getLevel() >= 50) break;
                MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                break;
            }
            case Massacre_first: {
                if (c.getPlayer().getPyramidSubway() != null) break;
                c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                break;
            }
            case Massacre_result: {
                c.getSession().write((Object)MaplePacketCreator.showEffect("killing/fail"));
                break;
            }
            default: {
                System.out.println("\u672a\u8655\u7406\u7684\u8173\u672c : " + scriptName + ", \u578b\u614b : onUserEnter - \u5730\u5716ID " + c.getPlayer().getMapId());
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "\u672a\u8655\u7406\u7684\u8173\u672c : " + scriptName + ", \u578b\u614b : onUserEnter - \u5730\u5716ID " + c.getPlayer().getMapId());
            }
        }
    }

    private static final int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        }
        if (ids >= 7 && ids <= 11) {
            return 6;
        }
        if (ids >= 13 && ids <= 17) {
            return 7;
        }
        if (ids >= 19 && ids <= 23) {
            return 8;
        }
        if (ids >= 25 && ids <= 29) {
            return 9;
        }
        if (ids >= 31 && ids <= 35) {
            return 10;
        }
        if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static final int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        }
        if (ids >= 7 && ids <= 11) {
            return 1;
        }
        if (ids >= 13 && ids <= 17) {
            return 2;
        }
        if (ids >= 19 && ids <= 23) {
            return 3;
        }
        if (ids >= 25 && ids <= 29) {
            return 4;
        }
        if (ids >= 31 && ids <= 35) {
            return 5;
        }
        if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(MapleClient c, String data) {
        c.getSession().write((Object)UIPacket.IntroDisableUI(true));
        c.getSession().write((Object)UIPacket.IntroLock(true));
        c.getSession().write((Object)UIPacket.ShowWZEffect(data));
    }

    private static void sendDojoClock(MapleClient c, int time) {
        c.getSession().write((Object)MaplePacketCreator.getClock(time));
    }

    private static void sendDojoStart(MapleClient c, int stage) {
        c.getSession().write((Object)MaplePacketCreator.environmentChange("Dojang/start", 4));
        c.getSession().write((Object)MaplePacketCreator.environmentChange("dojang/start/stage", 3));
        c.getSession().write((Object)MaplePacketCreator.environmentChange("dojang/start/number/" + stage, 3));
        c.getSession().write((Object)MaplePacketCreator.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(MapleClient c) {
        MapleMap map = c.getPlayer().getMap();
        map.resetFully();
        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(MapleClient c) {
        MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);
        short level = c.getPlayer().getLevel();
        int mob2 = level <= 10 ? 9300367 : (level <= 20 ? 9300368 : (level <= 30 ? 9300369 : (level <= 40 ? 9300370 : (level <= 50 ? 9300371 : (level <= 60 ? 9300372 : (level <= 70 ? 9300373 : (level <= 80 ? 9300374 : (level <= 90 ? 9300375 : (level <= 100 ? 9300376 : 9300377)))))))));
        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob2), witchTowerPos);
    }

    private static enum onUserEnter {
        babyPigMap,
        crash_Dragon,
        evanleaveD,
        getDragonEgg,
        meetWithDragon,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        TD_MC_Openning,
        TD_MC_gasi,
        TD_MC_title,
        cygnusJobTutorial,
        cygnusTest,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        Ghost,
        NULL;


        private static onUserEnter fromString(String Str) {
            try {
                return onUserEnter.valueOf(Str);
            }
            catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    }

    private static enum onFirstUserEnter {
        dojang_Eff,
        PinkBeen_before,
        onRewordMap,
        StageMsg_together,
        StageMsg_davy,
        party6weatherMsg,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        killing_BonusSetting,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        GhostF,
        NULL;


        private static onFirstUserEnter fromString(String Str) {
            try {
                return onFirstUserEnter.valueOf(Str);
            }
            catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    }
}

