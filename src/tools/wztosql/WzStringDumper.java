/*
 * Decompiled with CFR 0.152.
 */
package tools.wztosql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

public class WzStringDumper {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String name;
        String desc;
        String name2;
        MapleData descData;
        MapleData nameData;
        File stringFile = MapleDataProviderFactory.fileInWZPath("string.wz");
        MapleDataProvider stringProvider = MapleDataProviderFactory.getDataProvider(stringFile);
        MapleData cash = stringProvider.getData("Cash.img");
        MapleData consume = stringProvider.getData("Consume.img");
        MapleData eqp = stringProvider.getData("Eqp.img").getChildByPath("Eqp");
        MapleData etc = stringProvider.getData("Etc.img").getChildByPath("Etc");
        MapleData ins = stringProvider.getData("Ins.img");
        MapleData pet = stringProvider.getData("Pet.img");
        MapleData map = stringProvider.getData("Map.img");
        MapleData mob2 = stringProvider.getData("Mob.img");
        MapleData skill = stringProvider.getData("Skill.img");
        MapleData npc = stringProvider.getData("Npc.img");
        String output = args[0];
        File outputDir = new File(output);
        File cashTxt = new File(output + "\\Cash.txt");
        File useTxt = new File(output + "\\Use.txt");
        File eqpDir = new File(output + "\\Equip");
        File etcTxt = new File(output + "\\Etc.txt");
        File insTxt = new File(output + "\\Setup.txt");
        File petTxt = new File(output + "\\Pet.txt");
        File mapTxt = new File(output + "\\Map.txt");
        File mobTxt = new File(output + "\\Mob.txt");
        File skillTxt = new File(output + "\\Skill.txt");
        File npcTxt = new File(output + "\\NPC.txt");
        outputDir.mkdir();
        cashTxt.createNewFile();
        useTxt.createNewFile();
        eqpDir.mkdir();
        etcTxt.createNewFile();
        insTxt.createNewFile();
        petTxt.createNewFile();
        mapTxt.createNewFile();
        mobTxt.createNewFile();
        skillTxt.createNewFile();
        npcTxt.createNewFile();
        System.out.println("\u63d0\u53d6 Cash.img \u6578\u64da...");
        PrintWriter writer = new PrintWriter(new FileOutputStream(cashTxt));
        for (MapleData child : cash.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            name2 = "";
            desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name2 = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + (String)name2 + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Cash.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Consume.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(useTxt));
        for (MapleData child : consume.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            name2 = "";
            desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name2 = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + (String)name2 + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Consume.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Eqp.img \u6578\u64da...");
        for (MapleData child : eqp.getChildren()) {
            System.out.println("\u63d0\u53d6 " + child.getName() + " \u6578\u64da...");
            File eqpFile = new File(output + "\\Equip\\" + child.getName() + ".txt");
            eqpFile.createNewFile();
            PrintWriter eqpWriter = new PrintWriter(new FileOutputStream(eqpFile));
            for (MapleData child2 : child.getChildren()) {
                MapleData nameData2 = child2.getChildByPath("name");
                MapleData descData2 = child2.getChildByPath("desc");
                String name3 = "";
                String desc2 = "(\u7121\u63cf\u8ff0)";
                if (nameData2 != null) {
                    name3 = (String)nameData2.getData();
                }
                if (descData2 != null) {
                    desc2 = (String)descData2.getData();
                }
                eqpWriter.println(child2.getName() + " - " + name3 + " - " + desc2);
            }
            eqpWriter.flush();
            eqpWriter.close();
            System.out.println(child.getName() + " \u63d0\u53d6\u5b8c\u6210.");
        }
        System.out.println("Eqp.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Etc.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(etcTxt));
        for (MapleData child : etc.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            name2 = "";
            desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name2 = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + (String)name2 + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Etc.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Ins.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(insTxt));
        for (MapleData child : ins.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            name2 = "";
            desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name2 = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + (String)name2 + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Ins.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Pet.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(petTxt));
        for (MapleData child : pet.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            name2 = "";
            desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name2 = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + (String)name2 + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Pet.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Map.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(mapTxt));
        for (MapleData child : map.getChildren()) {
            writer.println(child.getName());
            writer.println();
            for (MapleData child2 : child.getChildren()) {
                MapleData streetData = child2.getChildByPath("streetName");
                MapleData mapData = child2.getChildByPath("mapName");
                String streetName = "(\u7121\u6578\u64da\u540d)";
                String mapName = "(\u7121\u5730\u5716\u540d)";
                if (streetData != null) {
                    streetName = (String)streetData.getData();
                }
                if (mapData != null) {
                    mapName = (String)mapData.getData();
                }
                writer.println(child2.getName() + " - " + streetName + " - " + mapName);
            }
            writer.println();
        }
        writer.flush();
        writer.close();
        System.out.println("Map.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Mob.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(mobTxt));
        for (MapleData child : mob2.getChildren()) {
            nameData = child.getChildByPath("name");
            name = "";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            writer.println(child.getName() + " - " + name);
        }
        writer.flush();
        writer.close();
        System.out.println("Mob.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Skill.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(skillTxt));
        for (MapleData child : skill.getChildren()) {
            nameData = child.getChildByPath("name");
            descData = child.getChildByPath("desc");
            MapleData bookData = child.getChildByPath("bookName");
            String name4 = "";
            String desc3 = "";
            if (nameData != null) {
                name4 = (String)nameData.getData();
            }
            if (descData != null) {
                desc3 = (String)descData.getData();
            }
            if (bookData != null) continue;
            writer.println(child.getName() + " - " + name4 + " - " + desc3);
        }
        writer.flush();
        writer.close();
        System.out.println("Skill.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Npc.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(npcTxt));
        for (MapleData child : npc.getChildren()) {
            nameData = child.getChildByPath("name");
            name = "";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            writer.println(child.getName() + " - " + name);
        }
        writer.flush();
        writer.close();
        System.out.println("Npc.img \u63d0\u53d6\u5b8c\u6210.");
    }
}

