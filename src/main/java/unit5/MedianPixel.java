package unit5;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MedianPixel {
    /**
     * Gets the average color of every pixel in an image.
     * @param image The image to average.
     * @return The average color of the image.
     */
    public static Color getAverageColor(Image image) {
        return getAverageColorFromBuffered(toBufferedImage(image));
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private static Color getAverageColorFromBuffered(BufferedImage image) {
        byte[] pixels = getPixelBytes(image);
        boolean hasAlpha = image.getAlphaRaster() != null;

        if(hasAlpha) {
            byte[] averages = getAverageByteArray(pixels, 4);
            int r = Byte.toUnsignedInt(averages[0]);
            int g = Byte.toUnsignedInt(averages[1]);
            int b = Byte.toUnsignedInt(averages[2]);
            int a = Byte.toUnsignedInt(averages[3]);
            return new Color(r, g, b, a);
        } else {
            byte[] averages = getAverageByteArray(pixels, 3);
            int r = Byte.toUnsignedInt(averages[0]);
            int g = Byte.toUnsignedInt(averages[1]);
            int b = Byte.toUnsignedInt(averages[2]);
            return new Color(r, g, b);
        }
    }

    @FunctionalInterface
    public interface ByteSorter {
        void sort(byte[] bytes);
    }

    public static Color getMedianColor(Image image) {
        return getMedianColor(image, Arrays::sort);
    }

    public static Color getMedianColor(Image image, ByteSorter sorter) {
        return getMedianColorFromBuffered(toBufferedImage(image), sorter);
    }

    private static Color getMedianColorFromBuffered(BufferedImage image, ByteSorter sorter) {
        byte[] pixels = getPixelBytes(image);
        boolean hasAlpha = image.getAlphaRaster() != null;

        byte[][] streams = zipperSplit(pixels, hasAlpha ? 4 : 3);
//        System.out.println(Arrays.deepToString(streams));

        for (byte[] channel : streams) {
            sorter.sort(channel);
        }

        int r = Byte.toUnsignedInt(streams[0][streams[0].length / 2]);
        int g = Byte.toUnsignedInt(streams[1][streams[1].length / 2]);
        int b = Byte.toUnsignedInt(streams[2][streams[2].length / 2]);

        if(hasAlpha) {
            int a = Byte.toUnsignedInt(streams[3][streams[3].length / 2]);
            return new Color(r, g, b, a);
        } else {
            return new Color(r, g, b);
        }
    }

    private static byte[] getPixelBytes(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("test flower.jpg"));
        Color col = getMedianColor(image, Arrays::sort);
        Color col2 = getAverageColor(image);
        System.out.println(col);
        System.out.println(col2);
    }

    private static byte[][] zipperSplit(byte[] array, int numSplitInto) {
        int n = array.length;

        if(n % numSplitInto != 0) {
            throw new IllegalArgumentException("Array length (%d) not a multiple of number of split arrays (%d)!".formatted(n, numSplitInto));
        }

        byte[][] ret = new byte[numSplitInto][n / numSplitInto];

        for(int i = 0; i < n; i++) {
            ret[i % numSplitInto][i / numSplitInto] = array[i];
        }

        return ret;
    }

    private static byte[] getAverageByteArray(byte[] bytes, int size) {
        if(bytes.length % size != 0) {
            throw new IllegalArgumentException("Array length (%d) not a multiple of data size (%d)!".formatted(bytes.length, size));
        }

        long[] averages = new long[size];

        for(int i = 0; i < bytes.length; i += size) {
            for(int j = 0; j < size; j++) {
                averages[j] += Byte.toUnsignedLong(bytes[i + j]);
            }
        }

        long n = bytes.length / size;
        byte[] ret = new byte[size];
        for(int i = 0; i < size; i++) {
            ret[i] = (byte) (averages[i] / n);
        }

        return ret;
    }
}
