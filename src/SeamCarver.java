import edu.princeton.cs.algs4.Picture;


import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private double[][] matrix;
    final private double weightBorder = 1000;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.picture = picture;
        this.matrix = new double[picture.width()][picture.height()];
        for(int col = 0; col < picture.width(); col++) {
            for (int row = 0; row < picture.height(); row++) {
                this.matrix[col][row] = cachingEnergy(col,row);
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
        int[] result = new int[width()];

        if(height() == 1) {
            for (int i = width() - 1; i >= 0; i--) {
                result[i] = 0;
            }
            return result;
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int lastPoint = -1;

        int[] edgeTo = new int[width() * height()];
        double[] distTo = new double[width() * height()];

        //init
        for(int col = 0; col < width(); col++){
            for (int row = 0; row < height(); row++){
                int point = indexPoint(row, col);
                if (col == 0) {
                    distTo[point] = 0;
                } else {
                    distTo[point] = Double.POSITIVE_INFINITY;
                }
                edgeTo[point] = -1;
            }
        }

        //relax
        for (int col = 0; col < width() - 1; col++){
            for(int row = 0; row < height(); row++) {
                int pointFrom = indexPoint(row, col);
                int pointTo = 0;
                if (row + 1 < height()){
                    pointTo = indexPoint(row + 1, col + 1);
                    relax(pointFrom, pointTo, edgeTo, distTo);
                }
                if (row - 1 >= 0) {
                    pointTo = indexPoint(row - 1, col + 1);
                    relax(pointFrom, pointTo, edgeTo, distTo);
                }
                pointTo = indexPoint(row, col + 1);
                relax(pointFrom, pointTo, edgeTo, distTo);
            }
        }

        // the shortest path
        for (int row = 1; row < height() - 1; row++) {
            if (minDistance > distTo[indexPoint(row, width() - 1)]) {
                minDistance = distTo[indexPoint(row, width() - 1)];
                lastPoint = indexPoint(row, width() - 1);
            }
        }

        // seem
        for(int i = result.length-1; i >= 0; i--) {
            result[i] = rowPoint(lastPoint);
            lastPoint = edgeTo[lastPoint];
        }

        return result;
    }

    public int[] findVerticalSeam() {
        int[] result = new int[picture.height()];
        if(width() == 1) {
            for (int i = height() - 1; i >= 0; i--) {
                result[i] = 0;
            }
            return result;
        }

        int[] edgeTo = new int[width() * height()];
        double[] distTo = new double[width() * height()];

        for(int col = 0; col < width(); col++){
            for (int row = 0; row < height(); row++){
                int point = indexPoint(row, col);
                if (row == 0) {
                    distTo[point] = 0;
                } else {
                    distTo[point] = Double.POSITIVE_INFINITY;
                }
                edgeTo[point] = -1;
            }
        }

        for (int row = 0; row < height() - 1; row++){
            for(int col = 0; col < width() - 1; col++) {
                int pointFrom = indexPoint(row, col);
                int pointTo = 0;
                if (col + 1 < width() - 1){
                    pointTo = indexPoint(row + 1, col + 1);
                    relax(pointFrom, pointTo, edgeTo, distTo);
                }
                if (col - 1 >= 0) {
                    pointTo = indexPoint(row + 1, col - 1);
                    relax(pointFrom, pointTo, edgeTo, distTo);
                }
                pointTo = indexPoint(row + 1, col);
                relax(pointFrom, pointTo, edgeTo, distTo);
            }
        }

        double minDistance = distTo[indexPoint(height() - 1, 0)];
        int lastPoint = indexPoint(height() - 1, 0);

        // the shortest path
        for (int col = 0; col <= width() - 1; col++) {
            if (minDistance > distTo[indexPoint(height() - 1, col)]) {
                minDistance = distTo[indexPoint(height() - 1, col)];
                lastPoint = indexPoint(height() - 1, col);
            }
        }

        // seem
        for(int i = result.length-1; i >= 0; i--) {
            result[i] = colPoint(lastPoint, rowPoint(lastPoint));
            lastPoint = edgeTo[lastPoint];
        }

        return result;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        checkSeem(seam, true);

        Picture updatedPicture = new Picture(width(), height() - 1);
        double[][] updatedMatrix = new double[width()][height() - 1];

        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < seam[col]; row++) {
                updatedPicture.set(col, row, picture.get(col, row));
                updatedMatrix[col][row] = matrix[col][row];
            }
            for (int row = seam[col]; row < height() - 1; row++) {
                updatedPicture.set(col, row, picture.get(col, row + 1));
                updatedMatrix[col][row] = matrix[col][row + 1];
            }
        }

        picture = updatedPicture;
        matrix = updatedMatrix;
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        checkSeem(seam, false);

        Picture updatedPicture = new Picture(width() - 1, height());
        double[][] updatedMatrix = new double[width() - 1][height()];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < seam[row]; col++) {
                updatedPicture.set(col, row, picture.get(col, row));
                updatedMatrix[col][row] = matrix[col][row];
            }
            for (int col = seam[row]; col < width() - 1; col++) {
                updatedPicture.set(col, row, picture.get(col + 1, row));
                updatedMatrix[col][row] = matrix[col + 1][row];
            }
        }

        picture = updatedPicture;
        matrix = updatedMatrix;
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

    private void relax(int pointFrom, int pointTo, int[] edgeTo, double[] distTo) {
        int row = rowPoint(pointTo);
        int col = colPoint(pointTo, row);
        if (distTo[pointFrom] + matrix[col][row] < distTo[pointTo]) {
            distTo[pointTo] = distTo[pointFrom] + matrix[col][row];
            edgeTo[pointTo] = pointFrom;
        }
    }

    private int indexPoint(int row, int col) {
        return row * width() + col;
    }

    private int colPoint(int point, int row) {
        return point - row * width();
    }

    private int rowPoint(int point) {
        return point / width();
    }

    public static void main(String[] args) {
        Picture pic = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(pic);
        int[] arr = { 0, 1, 1, 1, 1, 1, 0 };
        sc.removeVerticalSeam(arr);
    }

}