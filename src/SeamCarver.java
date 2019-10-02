import edu.princeton.cs.algs4.*;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private double[][] matrix;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.picture = picture;
        this.matrix = new double[picture.width()][picture.height()];
        for(int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                this.matrix[i][j] = energy(i,j);
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
        if (x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1) {
            return 1000;
        }
        return Math.sqrt(gradX(x,y) + gradY(x,y));
    }

    public int[] findHorizontalSeam() {
        if (picture.height() <= 1) {
            throw new IllegalArgumentException();
        }
        int height = picture.height();
        int width = picture.width();
        boolean[][] marked = new boolean[height][width];
        int[] result = new int[width];
        double minWeight = Double.POSITIVE_INFINITY;
        ST<Integer,int[]> indexes = new ST<Integer,int[]>();
        int[][] keys = new int[height][width];
        int maxV = V();
        Bag<Integer>[] adj = (Bag<Integer>[]) new Bag[maxV];
        for (int v = 0; v < maxV; v++) {
            adj[v] = new Bag<Integer>();
        }
        Queue<Integer> pixels = new Queue<Integer>();
        int currentIndex = 0;
        for (int row = 1; row < height; row+=2) {
            int[] point = new int[2];
            point[0] = row;
            point[1] = 0;
            marked[row][0] = true;
            keys[0][1] = currentIndex;
            indexes.put(currentIndex, point);
            pixels.enqueue(currentIndex);
            currentIndex++;
            while (!pixels.isEmpty()) {
                point = indexes.get(pixels.dequeue());
                currentIndex = addAdj(point[0], point[1], indexes, adj, pixels, currentIndex, marked, keys);
            }
            int count = indexes.size();
            Digraph G = new Digraph(count);
            for (int i = 0; i < count; i++) {
                for(Integer e : adj[i]) {
                    G.addEdge(i, e);
                }
            }
            Topological top = new Topological(G);
            Iterable<Integer> order = top.order();

            int[] edgeTo = new int[count*3];
            double[] distTo = new double[count*3];
            for (int i = 0; i < distTo.length; i++){
                distTo[i] = Double.POSITIVE_INFINITY;
            }

//            for (int i : order){
//                relax(edgeTo, distTo, adj[i], i);
//            };
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

    private int addAdj(int row, int col, ST<Integer,int[]> indexes, Bag<Integer>[] adj, Queue<Integer> pixels, int currentIndex, boolean[][] marked, int[][] keys ) {
        int height = picture.height();
        int width = picture.width();
        int parentIndex = keys[row][col];
        if (col + 1 < width) {
            if (row - 1 > 0) {
                int[] point = new int[2];
                point[0] = row + 1;
                point[1] = col + 1;
                if (!marked[row - 1][col + 1]) {
                    indexes.put(currentIndex, point);
                    adj[parentIndex].add(currentIndex);
                    pixels.enqueue(currentIndex);
                    keys[row - 1][col + 1] = currentIndex;
                    marked[row - 1][col + 1] = true;
                    currentIndex++;
                } else {
                    adj[parentIndex].add(keys[row - 1][col + 1]);
                }
            }
            int[] point = new int[2];
            point[0] = row;
            point[1] = col + 1;
            if (!marked[row][col + 1]) {
                indexes.put(currentIndex, point);
                adj[parentIndex].add(currentIndex);
                pixels.enqueue(currentIndex);
                keys[row][col + 1] = currentIndex;
                marked[row][col + 1] = true;
                currentIndex++;
            } else {
                adj[parentIndex].add(keys[row][col + 1]);
            }
            if(row + 1 < height && !marked[row + 1][col + 1]) {
                point = new int[2];
                point[0] = row + 1;
                point[1] = col + 1;
                if (!marked[row + 1][col + 1]) {
                    indexes.put(currentIndex, point);
                    adj[parentIndex].add(currentIndex);
                    pixels.enqueue(currentIndex);
                    keys[row + 1][col + 1] = currentIndex;
                    marked[row + 1][col + 1] = true;
                    currentIndex++;
                } else {
                    adj[parentIndex].add(keys[row + 1][col + 1]);
                }
            }
        }
        return currentIndex;
    }

    private int V(){
        int v = 1;
        int prev = 1;
        for (int i = 0; i < 1000 - 1; i++) {
            if (prev < 1000) {
                prev = prev + 2;
                v += prev;
            } else {
                v += 1000;
            }
        }
        return v;
    }

    private void relax(int[] edgeTo, double[] distTo, Bag<Integer> adj, int e) {
        for (int i : adj) {

        }
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