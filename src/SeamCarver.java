import edu.princeton.cs.algs4.*;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private double[][] matrix;
    final private double weightBorder = 10000;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.picture = picture;
        this.matrix = new double[picture.width()][picture.height()];
        for(int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                this.matrix[i][j] = cachingEnergy(i,j);
            }
        }
    }

    public Picture picture() {
        return picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x > picture.width() - 1 || y > picture.height() - 1) {
            throw new IllegalArgumentException();
        }
        return matrix[x][y];
    }

    public int[] findHorizontalSeam() {
        if (picture.height() <= 1) {
            throw new IllegalArgumentException();
        }
        int[] result = new int[width()];
        double minDistance = Double.POSITIVE_INFINITY;
        int lastPoint = -1;

        int[] edgeTo = new int[width() * height()];
        double[] distTo = new double[width() * height()];

        for (int row = 0; row < height(); row++){
            for(int col = 0; col < width(); col++) {
                int point = indexPoint(row, col);
                edgeTo[point] = -1;
                distTo[point] = Double.POSITIVE_INFINITY;
            }
        }

        for (int row = 1; row < height() - 1; row++){
            for (int col = 0; col < width() - 1; col++) {
                relax(row, col, edgeTo, distTo);
            }
        }

        for (int row = 1; row < height() - 1; row++) {
            if (minDistance > distTo[indexPoint(row, width() - 1)]) {
                minDistance = distTo[indexPoint(row, width() - 1)];
                lastPoint = indexPoint(row, width() - 1);
            }
        }

        for(int i = result.length - 1; i > 0; i--) {
            result[i] = edgeTo[lastPoint];
            lastPoint = edgeTo[lastPoint];
        }

        return result;
    }



    public int[] findVerticalSeam() {
        int[] result = new int[picture.width()];
        if (picture.width() <= 1) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        checkSeem(seam, true);
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        checkSeem(seam, false);
    }

    private void checkSeem(int[] seem, boolean horizontal) {
        if ( (horizontal && (seem.length != picture.width())) || !horizontal && (seem.length != picture.height())){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < seem.length - 1; i++) {
            if (Math.abs(seem[i] - seem[i+1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }


    private double cachingEnergy(int x, int y) {
        if (x < 0 || y < 0 || x > picture.width() - 1 || y > picture.height() - 1) {
            throw new IllegalArgumentException();
        }
        if (x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1) {
            return weightBorder;
        }
        return Math.sqrt(gradX(x,y) + gradY(x,y));
    }

    private double gradX(int x, int y) {
        double result = 0;

        Color colorLeft = picture.get(x-1, y);
        Color colorRight = picture.get(x+1, y);
        int Rx = colorRight.getRed() - colorLeft.getRed();
        int Gx = colorRight.getGreen() - colorLeft.getGreen();
        int Bx = colorRight.getBlue() - colorLeft.getBlue();

        result = Math.pow(Rx,2) + Math.pow(Gx,2) + Math.pow(Bx,2);
        return result;
    }

    private double gradY(int x, int y) {
        double result = 0;

        Color colorTop = picture.get(x, y + 1);
        Color colorBottom = picture.get(x, y - 1);
        int Rx = colorTop.getRed() - colorBottom.getRed();
        int Gx = colorTop.getGreen() - colorBottom.getGreen();
        int Bx = colorTop.getBlue() - colorBottom.getBlue();

        result = Math.pow(Rx,2) + Math.pow(Gx,2) + Math.pow(Bx,2);
        return result;
    }

    private void relax(int row, int col, int[] edgeTo, double[] distTo) {
        int point = indexPoint(row, col);
        if (distTo[point] < distTo[point] + matrix[row][col]) {
            distTo[point] = distTo[point] + matrix[row][col];
            edgeTo[point] = point;
        }
    }

    private int indexPoint(int row, int col) {
        return row * width() + col;
    }

    public static void main(String[] args) {
//        In in = new In(args[0]);
        Picture pic = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(pic);
        sc.findHorizontalSeam();
//        Digraph G = new Digraph(in);
//        Topological top = new Topological(G);
//        Out out = new Out();
//        for(int i : top.order()) {
//            out.print(i);
//            out.print(" ");
//        }
//
//        int i = 1;
//        for (int j = 0; j < 1000; j++) {
//            i = i + 2;
//        }
//        out.print(i);
    }

}