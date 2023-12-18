package edu.coursera.parallel;

import edu.rice.pcdp.ProcedureInt2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static edu.rice.pcdp.PCDP.forseq2d;

/**
 * Wrapper class for implementing matrix multiply efficiently in parallel.
 */
public final class MatrixMultiply {
    /**
     * Default constructor.
     */
    private MatrixMultiply() {
    }

    /**
     * Perform a two-dimensional matrix multiply (A x B = C) sequentially.
     *
     * @param A An input matrix with dimensions NxN
     * @param B An input matrix with dimensions NxN
     * @param C The output matrix
     * @param N Size of each dimension of the input matrices
     */
    public static void seqMatrixMultiply(final double[][] A, final double[][] B,
                                         final double[][] C, final int N) {
        forseq2d(0, N - 1, 0, N - 1, (i, j) -> {
            C[i][j] = 0.0;
            for (int k = 0; k < N; k++) {
                C[i][j] += A[i][k] * B[k][j];
            }
        });
    }

    /**
     * Perform a two-dimensional matrix multiply (A x B = C) in parallel.
     *
     * @param A An input matrix with dimensions NxN
     * @param B An input matrix with dimensions NxN
     * @param C The output matrix
     * @param N Size of each dimension of the input matrices
     */
    public static void parMatrixMultiply(final double[][] A, final double[][] B,
                                         final double[][] C, final int N) {
        /*
         * TODO Parallelize this outermost two-dimension sequential loop to
         * achieve performance improvement.
         */
        forpar2d(0, N - 1, 0, N - 1, (i, j) -> {
            C[i][j] = 0.0;
            for (int k = 0; k < N; k++) {
                C[i][j] += A[i][k] * B[k][j];
            }
        });
    }

    private static void forpar2d(int start0, int endInclusive0, int start1, int endInclusive1, ProcedureInt2D body) {
        assert start0 <= endInclusive0;

        assert start1 <= endInclusive1;

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        List<ForkJoinTask<?>> forkJoinTaskList = new ArrayList<>();

        for (int i = start0; i <= endInclusive0; ++i) {
            int finalI = i;
            ForkJoinTask<?> task = forkJoinPool.submit(() -> {
                        for (int j = start1; j <= endInclusive1; ++j) {
                            body.apply(finalI, j);
                        }
                    }
            );
            forkJoinTaskList.add(task);
        }

        forkJoinTaskList.forEach(ForkJoinTask::join);
    }
}
