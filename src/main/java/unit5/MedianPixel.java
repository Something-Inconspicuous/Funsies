package unit5;

import unit2.Time;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        if(img == null) throw new NullPointerException("Image cannot be null");
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

    @FunctionalInterface
    public interface IntSorter {
        void sort(int[] ints);
    }

    public static Color getMedianColor(Image image) {
        return getMedianColor(image, (IntSorter) Arrays::sort);
    }

    public static Color getMedianColor(Image image, ByteSorter sorter) {
        return getMedianColorFromBuffered(toBufferedImage(image), sorter);
    }

    public static Color getMedianColor(Image[] images, ByteSorter sorter) {
        BufferedImage[] bimages = new BufferedImage[images.length];
        for(int i = 0; i < images.length; i++) {
            bimages[i] = toBufferedImage(images[i]);
        }
        return getMedianColorFromBuffered(bimages, sorter);
    }
    public static Color getMedianColor(Image image, IntSorter sorter) {
        return getMedianColorFromBuffered(toBufferedImage(image), sorter);
    }

    public static Color[] getMedianColors(Image[] images, IntSorter sorter) {
        BufferedImage[] bimages = new BufferedImage[images.length];
        for(int i = 0; i < images.length; i++) {
            bimages[i] = toBufferedImage(images[i]);
        }
        return getMedianColorsFromBuffered(bimages, sorter);
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

    private static Color getMedianColorFromBuffered(BufferedImage[] images, ByteSorter sorter) {
        byte[] rs = new byte[images.length];
        byte[] gs = new byte[images.length];
        byte[] bs = new byte[images.length];
        byte[] as = new byte[images.length];
        for (int i = 0; i < images.length; i++) {
            byte[] pixels = getPixelBytes(images[i]);
            boolean hasAlpha = images[i].getAlphaRaster() != null;
    
            byte[][] streams = zipperSplit(pixels, hasAlpha ? 4 : 3);
    //        System.out.println(Arrays.deepToString(streams));
    
            for (byte[] channel : streams) {
                sorter.sort(channel);
            }
    
            rs[i] = streams[0][streams[0].length / 2];
            gs[i] = streams[1][streams[1].length / 2];
            bs[i] = streams[2][streams[2].length / 2];
            
            if(hasAlpha) {
                as[i] = streams[3][streams[3].length / 2];
            } else {
                as[i] = -1; // 255 in unsigned
            }
        }

        sorter.sort(rs);
        sorter.sort(gs);
        sorter.sort(bs);
        sorter.sort(as);

        int r = Byte.toUnsignedInt(rs[rs.length / 2]);
        int g = Byte.toUnsignedInt(gs[gs.length / 2]);
        int b = Byte.toUnsignedInt(bs[bs.length / 2]);
        int a = Byte.toUnsignedInt(as[as.length / 2]);

        return new Color(r, g, b, a);

    }

    private static Color[] getMedianColorsFromBuffered(BufferedImage[] images, IntSorter sorter) {
        int[] rs = new int[images.length];
        int[] gs = new int[images.length];
        int[] bs = new int[images.length];
        for (int i = 0; i < images.length; i++) {
            int[][] streams = getRGBs(images[i]);
            for(int[] channel : streams) {
                sorter.sort(channel);
            }

            rs[i] = streams[0][streams[0].length / 2];
            gs[i] = streams[1][streams[1].length / 2];
            bs[i] = streams[2][streams[2].length / 2];
        }

        Color[] colors = new Color[images.length];

        for(int i = 0; i < images.length; i++) {
            colors[i] = new Color(rs[i], gs[i], bs[i]);
        }

        return colors;
    }

    private static Color getMedianColorFromBuffered(BufferedImage image, IntSorter sorter) {
        int[][] streams = getRGBs(image);
        for(int[] channel : streams) {
            sorter.sort(channel);
        }

        int r = streams[0][streams[0].length / 2];
        int g = streams[1][streams[1].length / 2];
        int b = streams[2][streams[2].length / 2];

        return new Color(r, g, b);
    }

    private static int[][] getRGBs(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] data = new int[w * h];
        image.getRGB(0, 0, w, h, data, 0, w);
        int[][] colors = new int[3][w * h];
        for(int i = 0; i < w * h; i++) {
            colors[0][i] = data[i] & 0xFF;
            colors[1][i] = (data[i] >> 8) & 0xFF;
            colors[2][i] = (data[i] >> 16) & 0xFF;
        }
        return colors;
    }

    private static byte[] getPixelBytes(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
//        BufferedImage flower = ImageIO.read(new File("test flower.jpg"));
//        BufferedImage chichen = ImageIO.read(new File("Chichen_Itza_Raymond_Ostertag.JPG"));
        BufferedImage vanGogh = ImageIO.read(new URI("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/1513px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg").toURL());
        BufferedImage wood = ImageIO.read(new URI("https://upload.wikimedia.org/wikipedia/commons/thumb/c/cc/Grant_Wood_-_American_Gothic_-_Google_Art_Project.jpg/994px-Grant_Wood_-_American_Gothic_-_Google_Art_Project.jpg").toURL());
        BufferedImage hopper = ImageIO.read(new URI("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Nighthawks_by_Edward_Hopper_1942.jpg/1600px-Nighthawks_by_Edward_Hopper_1942.jpg").toURL());
        BufferedImage daVinci = ImageIO.read(new URI("https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/The_Last_Supper_-_Leonardo_Da_Vinci_-_High_Resolution_32x16.jpg/880px-The_Last_Supper_-_Leonardo_Da_Vinci_-_High_Resolution_32x16.jpg").toURL());

        int pxs = vanGogh.getWidth() * vanGogh.getHeight()
                + wood.getWidth() * wood.getHeight()
                + hopper.getWidth() * hopper.getHeight()
                + daVinci.getHeight() * daVinci.getHeight()
                ;

        System.out.println("Number of pixels = " + pxs);
//        SwingUtilities.invokeLater(() -> gui(flower));

        timeDifferentSortingAlgorithms(vanGogh, wood, hopper, daVinci);
    }

    private static void gui(BufferedImage image) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        frame.setContentPane(panel);

        JLabel averageColorLabel = new JLabel("Average Color:");
        panel.add(averageColorLabel);

        JPanel averageColorSquare = new JPanel();
        averageColorSquare.setPreferredSize(new Dimension(50, 50));
        averageColorSquare.setBackground(MedianPixel.getAverageColor(image));
        panel.add(averageColorSquare);

        JLabel medianColorLabel = new JLabel("Median Color:");
        panel.add(medianColorLabel);

        JPanel medianColorSquare = new JPanel();
        medianColorSquare.setPreferredSize(new Dimension(50, 50));
        medianColorSquare.setBackground(MedianPixel.getMedianColor(image));
        panel.add(medianColorSquare);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public static void timeDifferentSortingAlgorithms(Image... testImages) {
        if(testImages.length == 1) {
            timeDifferentSortingAlgorithms1(testImages[0]);
        } else {
            testSorting(testImages, Arrays::sort,                    "Arrays.sort");
            testSorting(testImages, Arrays::parallelSort,            "Arrays.parallelSort");
            testSorting(testImages, MedianPixel::topDownMergeSort,   "Merge Sort, Top-down");
            testSorting(testImages, MedianPixel::radixSort,          "Radix Sort");
        }
    }

    public static void timeDifferentSortingAlgorithms1(Image testImage) {
        testSorting(testImage, Arrays::sort,                    "Arrays.sort");
        testSorting(testImage, Arrays::parallelSort,            "Arrays.parallelSort");
        testSorting(testImage, MedianPixel::topDownMergeSort,   "Merge Sort, Top-down");
        testSorting(testImage, MedianPixel::radixSort,          "Radix Sort");
    }

    private static void testSorting(Image image, IntSorter sorter, String name) {
        final Color[] col = new Color[1];
        final long millis = Time.millis(() -> col[0] = getMedianColor(image, sorter));
        System.out.println("=========================================");
        System.out.println("Sorting Algorithm: " + name);
        System.out.println("Time: " + millis + "ms");
        System.out.println("Color: " + col[0]);
        System.out.println("=========================================");
    }

    static Color[] col;
    private static void testSorting(Image[] image, IntSorter sorter, String name) {
        final long millis = Time.millis(() -> col = getMedianColors(image, sorter));
        System.out.println("=========================================");
        System.out.println("Sorting Algorithm: " + name);
        System.out.println("Time: " + millis + "ms");
        System.out.println("Colors: " + toHex(col));
        System.out.println("=========================================");
    }

    private static String toHex(Color[] colors) {
        if(colors.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < colors.length - 1; i++) {
            sb.append("#").append(Integer.toHexString(colors[i].getRGB() & 0xFFFFFF).toUpperCase()).append(", ");
        }
        sb.append("#").append(Integer.toHexString(colors[colors.length - 1].getRGB() & 0xFFFFFF).toUpperCase()).append("]");
        return sb.toString();

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
    private static int[][] zipperSplit(int[] array, int numSplitInto) {
        int n = array.length;

        if(n % numSplitInto != 0) {
            throw new IllegalArgumentException("Array length (%d) not a multiple of number of split arrays (%d)!".formatted(n, numSplitInto));
        }

        int[][] ret = new int[numSplitInto][n / numSplitInto];

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

    // Sorting algorithms

    // ==========================
    // Merge Sort, Top-down
    // Source: https://en.wikipedia.org/wiki/Merge_sort
    // Time:  O(n * log(n))
    // Space: O(n)
    // ===========================

    // Array A[] has the items to sort; array B[] is a work array.
    public static void topDownMergeSort(int[] A) {
        int n = A.length;
        int[] B = new int[n];
        System.arraycopy(A, 0, B, 0, n); // one time copy of A[] to B[]
        topDownSplitMerge(A, 0, n, B);           // sort data from B[] into A[]
    }

    // Split A[] into 2 runs, sort both runs into B[], merge both runs from B[] to A[]
    // iBegin is inclusive; iEnd is exclusive (A[iEnd] is not in the set).
    private static void topDownSplitMerge(int[] B, int iBegin, int iEnd, int[] A) {
        if (iEnd - iBegin <= 1)                     // if run size == 1
            return;                                 //   consider it sorted
        // split the run longer than 1 item into halves
        int iMiddle = (iEnd + iBegin) / 2;              // iMiddle = mid point
        // recursively sort both runs from array A[] into B[]
        topDownSplitMerge(A, iBegin,  iMiddle, B);  // sort the left  run
        topDownSplitMerge(A, iMiddle,    iEnd, B);  // sort the right run
        // merge the resulting runs from array B[] into A[]
        topDownMerge(B, iBegin, iMiddle, iEnd, A);
    }

    //  Left source half is A[ iBegin:iMiddle-1].
    // Right source half is A[iMiddle:iEnd-1   ].
    // Result is            B[ iBegin:iEnd-1   ].
    private static void topDownMerge(int[] B, int iBegin, int iMiddle, int iEnd, int[] A)  {
        int i = iBegin, j = iMiddle;

        // While there are elements in the left or right runs...
        for (int k = iBegin; k < iEnd; k++) {
            // If left run head exists and is <= existing right run head.
            if (i < iMiddle && (j >= iEnd || A[i] <= A[j])) {
                B[k] = A[i];
                i = i + 1;
            } else {
                B[k] = A[j];
                j = j + 1;
            }
        }
    }

    // A utility function to get maximum value in arr[]
    private static int getMax(int[] arr, int n) {
        int mx = arr[0];
        for (int i = 1; i < n; i++)
            if (arr[i] > mx)
                mx = arr[i];
        return mx;
    }

    // A function to do counting sort of arr[] according to
    // the digit represented by exp.
    private static void countSort(int[] arr, int n, int exp) {
        int[] output = new int[n]; // output array
        int i;
        int[] count = new int[10];
        Arrays.fill(count, 0);

        // Store count of occurrences in count[]
        for (i = 0; i < n; i++)
            count[ (arr[i]/exp) % 10 ]++;

        // Change count[i] so that count[i] now contains
        // actual position of this digit in output[]
        for (i = 1; i < 10; i++)
            count[i] += count[i - 1];

        // Build the output array
        for (i = n - 1; i >= 0; i--)
        {
            output[count[ (arr[i]/exp)%10 ] - 1] = arr[i];
            count[ (arr[i]/exp)%10 ]--;
        }

        // Copy the output array to arr[], so that arr[] now
        // contains sorted numbers according to current digit
        for (i = 0; i < n; i++)
            arr[i] = output[i];
    }

    // The main function to that sorts arr[] of size n using
    // Radix Sort
    public static void radixSort(int[] arr) {
        int n = arr.length;
        // Find the maximum number to know number of digits
        int m = getMax(arr, n);

        // Do counting sort for every digit. Note that instead
        // of passing digit number, exp is passed. exp is 10^i
        // where i is current digit number
        for (int exp = 1; m/exp > 0; exp *= 10)
            countSort(arr, n, exp);
    }
}
