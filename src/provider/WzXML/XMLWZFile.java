/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;
import provider.WzXML.WZDirectoryEntry;
import provider.WzXML.WZFileEntry;
import provider.WzXML.XMLDomMapleData;

public class XMLWZFile
implements MapleDataProvider {
    private File root;
    private WZDirectoryEntry rootForNavigation;

    public XMLWZFile(File fileIn) {
        this.root = fileIn;
        this.rootForNavigation = new WZDirectoryEntry(fileIn.getName(), 0, 0, null);
        this.fillMapleDataEntitys(this.root, this.rootForNavigation);
    }

    private void fillMapleDataEntitys(File lroot, WZDirectoryEntry wzdir) {
        for (File file : lroot.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory() && !fileName.endsWith(".img")) {
                WZDirectoryEntry newDir = new WZDirectoryEntry(fileName, 0, 0, wzdir);
                wzdir.addDirectory(newDir);
                this.fillMapleDataEntitys(file, newDir);
                continue;
            }
            if (!fileName.endsWith(".xml")) continue;
            wzdir.addFile(new WZFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, wzdir));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MapleData getData(String path) {
        XMLDomMapleData domMapleData;
        FileInputStream fis;
        File dataFile = new File(this.root, path + ".xml");
        File imageDataDir = new File(this.root, path);
        try {
            fis = new FileInputStream(dataFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Datafile " + path + " does not exist in " + this.root.getAbsolutePath());
        }
        try {
            domMapleData = new XMLDomMapleData(fis, imageDataDir.getParentFile());
        }
        finally {
            try {
                fis.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return domMapleData;
    }

    @Override
    public MapleDataDirectoryEntry getRoot() {
        return this.rootForNavigation;
    }
}

