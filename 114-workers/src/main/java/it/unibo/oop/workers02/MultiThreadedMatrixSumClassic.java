package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedMatrixSumClassic implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedMatrixSumClassic(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startRow;
        private final int nOfRows;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startRow
         *            the initial row for this worker
         * @param nOfRows
         *            the no. of rows to sum up for this worker
         */
        Worker(final double[][] matrix, final int startRow, final int nOfRows) {
            super();
            this.matrix = matrix;
            this.startRow = startRow;
            this.nOfRows = nOfRows;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            for (int iRow = startRow; iRow < matrix.length && iRow < (nOfRows + startRow); iRow++) {
                for (double element: matrix[iRow]) {
                    this.res = this.res + element;
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

}
