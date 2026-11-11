/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import provider.MapleData;
import provider.MapleDataEntity;
import provider.WzXML.FileStoredPngMapleCanvas;
import provider.WzXML.MapleDataType;
import tools.FileoutputUtil;

public class XMLDomMapleData
implements MapleData,
Serializable {
    private Node node;
    private File imageDataDir;

    private XMLDomMapleData(Node node) {
        this.node = node;
    }

    public XMLDomMapleData(FileInputStream fis, File imageDataDir) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fis);
            this.node = document.getFirstChild();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.imageDataDir = imageDataDir;
    }

    @Override
    public MapleData getChildByPath(String path) {
        String[] segments = path.split("/");
        if (segments[0].equals("..")) {
            return ((MapleData)this.getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
        }
        Node myNode = this.node;
        for (int x = 0; x < segments.length; ++x) {
            NodeList childNodes = myNode.getChildNodes();
            boolean foundChild = false;
            for (int i = 0; i < childNodes.getLength(); ++i) {
                try {
                    Node childNode = childNodes.item(i);
                    if (childNode == null || childNode.getNodeType() != 1 || !childNode.getAttributes().getNamedItem("name").getNodeValue().equals(segments[x])) continue;
                    myNode = childNode;
                    foundChild = true;
                    break;
                }
                catch (NullPointerException e) {
                    FileoutputUtil.outputFileError("Logs/Log_Packet_Except.rtf", e);
                }
            }
            if (foundChild) continue;
            return null;
        }
        XMLDomMapleData ret = new XMLDomMapleData(myNode);
        ret.imageDataDir = new File(this.imageDataDir, this.getName() + "/" + path).getParentFile();
        return ret;
    }

    @Override
    public List<MapleData> getChildren() {
        ArrayList<MapleData> ret = new ArrayList<MapleData>();
        NodeList childNodes = this.node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            if (childNode == null || childNode.getNodeType() != 1) continue;
            XMLDomMapleData child = new XMLDomMapleData(childNode);
            child.imageDataDir = new File(this.imageDataDir, this.getName());
            ret.add(child);
        }
        return ret;
    }

    @Override
    public Object getData() {
        NamedNodeMap attributes = this.node.getAttributes();
        MapleDataType type = this.getType();
        switch (type) {
            case DOUBLE: {
                return Double.parseDouble(attributes.getNamedItem("value").getNodeValue());
            }
            case FLOAT: {
                return Float.valueOf(Float.parseFloat(attributes.getNamedItem("value").getNodeValue()));
            }
            case INT: {
                return Integer.parseInt(attributes.getNamedItem("value").getNodeValue());
            }
            case SHORT: {
                return Short.parseShort(attributes.getNamedItem("value").getNodeValue());
            }
            case STRING: 
            case UOL: {
                return attributes.getNamedItem("value").getNodeValue();
            }
            case VECTOR: {
                return new Point(Integer.parseInt(attributes.getNamedItem("x").getNodeValue()), Integer.parseInt(attributes.getNamedItem("y").getNodeValue()));
            }
            case CANVAS: {
                return new FileStoredPngMapleCanvas(Integer.parseInt(attributes.getNamedItem("width").getNodeValue()), Integer.parseInt(attributes.getNamedItem("height").getNodeValue()), new File(this.imageDataDir, this.getName() + ".png"));
            }
        }
        return null;
    }

    @Override
    public final MapleDataType getType() {
        String nodeName = this.node.getNodeName();
        if (nodeName.equals("imgdir")) {
            return MapleDataType.PROPERTY;
        }
        if (nodeName.equals("canvas")) {
            return MapleDataType.CANVAS;
        }
        if (nodeName.equals("convex")) {
            return MapleDataType.CONVEX;
        }
        if (nodeName.equals("sound")) {
            return MapleDataType.SOUND;
        }
        if (nodeName.equals("uol")) {
            return MapleDataType.UOL;
        }
        if (nodeName.equals("double")) {
            return MapleDataType.DOUBLE;
        }
        if (nodeName.equals("float")) {
            return MapleDataType.FLOAT;
        }
        if (nodeName.equals("int")) {
            return MapleDataType.INT;
        }
        if (nodeName.equals("short")) {
            return MapleDataType.SHORT;
        }
        if (nodeName.equals("string")) {
            return MapleDataType.STRING;
        }
        if (nodeName.equals("vector")) {
            return MapleDataType.VECTOR;
        }
        if (nodeName.equals("null")) {
            return MapleDataType.IMG_0x00;
        }
        return null;
    }

    @Override
    public MapleDataEntity getParent() {
        Node parentNode = this.node.getParentNode();
        if (parentNode.getNodeType() == 9) {
            return null;
        }
        XMLDomMapleData parentData = new XMLDomMapleData(parentNode);
        parentData.imageDataDir = this.imageDataDir.getParentFile();
        return parentData;
    }

    @Override
    public String getName() {
        return this.node.getAttributes().getNamedItem("name").getNodeValue();
    }

    @Override
    public Iterator<MapleData> iterator() {
        return this.getChildren().iterator();
    }
}

