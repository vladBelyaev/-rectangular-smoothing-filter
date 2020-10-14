package ua.nure.lab1;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("D:\\university\\4 kurs\\lab1\\test1.png"));
        BufferedImage bufferedImage1 = ImageIO.read(new File("D:\\university\\4 kurs\\lab1\\test2.jpg"));
        BufferedImage bufferedImage2 = ImageIO.read(new File("D:\\university\\4 kurs\\lab1\\test3.png"));
        BufferedImage bufferedImage3 = ImageIO.read(new File("D:\\university\\4 kurs\\lab1\\test4.jpg"));

        alg(bufferedImage, "result1_alg.png", "png");
        alg(bufferedImage1, "result2_alg.jpg", "jpg");
        alg(bufferedImage2, "result3_alg.png", "png");
//        createImageFrom(bufferedImage3, "result4.jpg", "jpg");


    }

    private static void createImageFrom(BufferedImage bufferedImage, String newImageName,  String extension) throws IOException {
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        boolean hasAlphaChannel = bufferedImage.getAlphaRaster() != null;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelLength = 3;
        if (hasAlphaChannel) {
            pixelLength = 4;
        }
        for (int pixel = 0, row = 0, col = 0; pixel + pixelLength - 1 < pixels.length; pixel += pixelLength) {
            int argb = -16777216;
            int count = 0;
            if (hasAlphaChannel) {
                argb = (((int) pixels[pixel] & 0xff) << 24);// alpha
                count = 1;
            }
            argb += ((int) pixels[pixel + count] & 0xff); // blue
            argb += (((int) pixels[pixel + count + 1] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + count + 2] & 0xff) << 16); // red
            image.setRGB(col, row, argb);
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        File outputFile = new File(newImageName);
        ImageIO.write(image, extension, outputFile);
    }

    private static void alg(BufferedImage bufferedImage, String newImageName,  String extension) throws IOException {
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelLength = 3;
        boolean hasAlphaChannel = bufferedImage.getAlphaRaster() != null;
        int[][] alphaMatrix = new int[width][height];
        int[][] blueMatrix = new int[width][height];
        int[][] redMatrix = new int[width][height];
        int[][] greenMatrix = new int[width][height];
        for (int pixel = 0, row = 0, col = 0; pixel + pixelLength - 1 < pixels.length; pixel += pixelLength) {
            int count = 0;
            if (hasAlphaChannel) {
                alphaMatrix[col][row] = (((int) pixels[pixel] & 0xff) << 24);// alpha
                count = 1;
            }
            blueMatrix[col][row] = ((int) pixels[pixel + count] & 0xff); // blue
            greenMatrix[col][row] = (((int) pixels[pixel + count + 1] & 0xff) << 8); // green
            redMatrix[col][row]  = (((int) pixels[pixel + count + 2] & 0xff) << 16); // red
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        int a = 5; //width
        int b = 3; //coll
        for (int i =0; i < width - 2; i++) {
            for (int j =0; j < height - 1; j++) {
                int argb = alphaMatrix[i][j];
                argb += calculateFragment(i, j, a, b, blueMatrix);
                argb += calculateFragment(i, j, a, b, greenMatrix);
                argb += calculateFragment(i, j, a, b, redMatrix);
                image.setRGB(i, j, argb);
            }
        }
        File outputFile = new File(newImageName);
        ImageIO.write(image, extension, outputFile);
    }
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    // 1, 2, 3, 4, 5, 6, 7, 8, 9
    private static int calculateFragment(int indexCol, int indexRow, int fragmentWidth, int fragmentHeight, int[][] canalMatrix) {
        int[][] fragmentMatrix = new int[fragmentWidth][fragmentHeight];
        int fragmentWidthOneSide = (fragmentWidth - 1) / 2;
        int fragmentHeightOneSide = (fragmentHeight - 1) / 2;
        int indexColOrg =  indexCol;
        int indexRowOrg =  indexRow;
        if (indexCol < fragmentWidthOneSide) {
            indexCol = fragmentWidthOneSide; // 2
        }
        if (indexRow < fragmentHeightOneSide) {
            indexRow = fragmentHeightOneSide; // 1
        }
        for (int i = indexCol - fragmentWidthOneSide, iFragment = 0; i < indexCol + fragmentWidthOneSide + 1; i++, iFragment++) {
            for (int j = indexRow - fragmentHeightOneSide, jFragment = 0; j < indexRow + fragmentHeightOneSide + 1; j++, jFragment++) {
                fragmentMatrix[iFragment][jFragment] = canalMatrix[i][j];
            }
        }
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 5; j++) {
//                System.out.print(canalMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
//        for (int i = 0; i < fragmentMatrix.length; i++) {
//            for (int j = 0; j < fragmentMatrix[i].length; j++) {
//                System.out.print(fragmentMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
        double sumD = 0;
        for (int i = 0; i < fragmentMatrix.length; i++) {
            for (int j = 0; j < fragmentMatrix[i].length; j++) {
                double temp = fragmentMatrix[i][j];
                sumD += temp / (fragmentHeight * fragmentWidth);
                fragmentMatrix[i][j] = fragmentMatrix[i][j] / (fragmentHeight * fragmentWidth);
            }
        }
//        System.out.println(sumD);
//        System.out.println(canalMatrix[indexColOrg][indexRowOrg]);
        return (int) sumD;
    }
}
