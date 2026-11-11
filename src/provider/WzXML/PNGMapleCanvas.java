/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import provider.MapleCanvas;

public class PNGMapleCanvas
implements MapleCanvas {
    private static final int[] ZAHLEN = new int[]{2, 1, 0, 3};
    private int height;
    private int width;
    private int dataLength;
    private int format;
    private byte[] data;

    public PNGMapleCanvas(int width, int height, int dataLength, int format, byte[] data) {
        this.height = height;
        this.width = width;
        this.dataLength = dataLength;
        this.format = format;
        this.data = data;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public int getFormat() {
        return this.format;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public BufferedImage getImage() {
        int i;
        int sizeUncompressed = 0;
        int size8888 = 0;
        int maxWriteBuf = 2;
        int maxHeight = 3;
        byte[] writeBuf = new byte[maxWriteBuf];
        switch (this.getFormat()) {
            case 1: 
            case 513: {
                sizeUncompressed = this.getHeight() * this.getWidth() * 4;
                break;
            }
            case 2: {
                sizeUncompressed = this.getHeight() * this.getWidth() * 8;
                break;
            }
            case 517: {
                sizeUncompressed = this.getHeight() * this.getWidth() / 128;
            }
        }
        size8888 = this.getHeight() * this.getWidth() * 8;
        if (size8888 > maxWriteBuf) {
            maxWriteBuf = size8888;
            writeBuf = new byte[maxWriteBuf];
        }
        if (this.getHeight() > maxHeight) {
            maxHeight = this.getHeight();
        }
        Inflater dec = new Inflater();
        dec.setInput(this.getData(), 0, this.dataLength);
        int declen = 0;
        byte[] uc = new byte[sizeUncompressed];
        try {
            declen = dec.inflate(uc);
        }
        catch (DataFormatException ex) {
            throw new RuntimeException("zlib fucks", ex);
        }
        dec.end();
        if (this.getFormat() == 1) {
            for (i = 0; i < sizeUncompressed; ++i) {
                byte low = (byte)(uc[i] & 0xF);
                byte high = (byte)(uc[i] & 0xF0);
                writeBuf[i << 1] = (byte)((low << 4 | low) & 0xFF);
                writeBuf[(i << 1) + 1] = (byte)(high | high >>> 4 & 0xF);
            }
        } else if (this.getFormat() == 2) {
            writeBuf = uc;
        } else if (this.getFormat() == 513) {
            for (i = 0; i < declen; i += 2) {
                byte bBits = (byte)((uc[i] & 0x1F) << 3);
                byte gBits = (byte)((uc[i + 1] & 7) << 5 | (uc[i] & 0xE0) >> 3);
                byte rBits = (byte)(uc[i + 1] & 0xF8);
                writeBuf[i << 1] = (byte)(bBits | bBits >> 5);
                writeBuf[(i << 1) + 1] = (byte)(gBits | gBits >> 6);
                writeBuf[(i << 1) + 2] = (byte)(rBits | rBits >> 5);
                writeBuf[(i << 1) + 3] = -1;
            }
        } else if (this.getFormat() == 517) {
            byte b = 0;
            int pixelIndex = 0;
            for (int i2 = 0; i2 < declen; ++i2) {
                for (int j = 0; j < 8; ++j) {
                    b = (byte)(((uc[i2] & 1 << 7 - j) >> 7 - j) * 255);
                    for (int k = 0; k < 16; ++k) {
                        pixelIndex = (i2 << 9) + (j << 6) + k * 2;
                        writeBuf[pixelIndex] = b;
                        writeBuf[pixelIndex + 1] = b;
                        writeBuf[pixelIndex + 2] = b;
                        writeBuf[pixelIndex + 3] = -1;
                    }
                }
            }
        }
        DataBufferByte imgData = new DataBufferByte(writeBuf, sizeUncompressed);
        PixelInterleavedSampleModel sm = new PixelInterleavedSampleModel(0, this.getWidth(), this.getHeight(), 4, this.getWidth() * 4, ZAHLEN);
        WritableRaster imgRaster = Raster.createWritableRaster(sm, imgData, new Point(0, 0));
        BufferedImage aa = new BufferedImage(this.getWidth(), this.getHeight(), 2);
        aa.setData(imgRaster);
        return aa;
    }
}

