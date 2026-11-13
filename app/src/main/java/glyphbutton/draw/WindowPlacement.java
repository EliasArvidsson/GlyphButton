package glyphbutton.draw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Window placement / alignment algorithm.
 * Produces top-left coordinates so windows form a cohesive left-to-right block
 * inside maxWidth x maxHeight, respecting margin and input sequence.
 * <p>
 * Designed as a single reusable instance. Cache is automatic: identical inputs
 * return the previous result without recomputation.
 * <p>
 * Failure rules:
 * <ul>
 *   <li>Only failed windows receive null coordinates; successful ones keep theirs</li>
 *   <li>All failed indices are reported in windowNotFitIndices</li>
 *   <li>If failure is caused by adding a window (terminal size unchanged), placement
 *       stops at the first failure</li>
 *   <li>If failure is caused by terminal resizing, placement continues</li>
 * </ul>
 */
public final class WindowPlacement {

    /** Aspect ratio above which a window is considered "wide-short". */
    private static final double WIDE_ASPECT = 2.5;

    /**
     * Result of a placement computation. Coordinates are top-left corners.
     * A {@code null} entry in {@code x} or {@code y} means that particular
     * window could not be placed.
     *
     * @param windowNotFitIndices indices of every window that failed to place
     *                            (empty when all succeeded)
     */
    public record PlacementResult(
            Integer[] x,
            Integer[] y,
            boolean calcIncompTermResize,
            boolean calcIncompWindowNotFit,
            int[] windowNotFitIndices
    ) {
        public static PlacementResult success(Integer[] x, Integer[] y) {
            return new PlacementResult(x, y, false, false, new int[0]);
        }

        public static PlacementResult terminalResized(Integer[] x, Integer[] y, int[] failed) {
            return new PlacementResult(x, y, true, false, failed);
        }

        public static PlacementResult windowDidNotFit(Integer[] x, Integer[] y, int[] failed) {
            return new PlacementResult(x, y, false, true, failed);
        }
    }

    // Cache of previous call
    private int[] lastWidths;
    private int[] lastHeights;
    private int lastMaxWidth = -1;
    private int lastMaxHeight = -1;
    private int lastMargin = -1;
    private boolean lastOptimizeSpace;
    private PlacementResult lastResult;

    /**
     * Compute placement for the given windows.
     *
     * @param widths        window widths (must match heights length)
     * @param heights       window heights
     * @param maxWidth      workspace width
     * @param maxHeight     workspace height
     * @param margin        gap between windows
     * @param optimizeSpace allow breaking strict sequence to pack better
     * @return placement result; never null
     */
    public PlacementResult place(int[] widths, int[] heights,
                                 int maxWidth, int maxHeight,
                                 int margin, boolean optimizeSpace) {
        Objects.requireNonNull(widths, "widths");
        Objects.requireNonNull(heights, "heights");
        if (widths.length != heights.length) {
            throw new IllegalArgumentException("widths and heights must have same length");
        }
        if (maxWidth <= 0 || maxHeight <= 0 || margin < 0) {
            throw new IllegalArgumentException("maxWidth/maxHeight must be > 0 and margin >= 0");
        }

        if (lastResult != null
                && lastMaxWidth == maxWidth
                && lastMaxHeight == maxHeight
                && lastMargin == margin
                && lastOptimizeSpace == optimizeSpace
                && Arrays.equals(lastWidths, widths)
                && Arrays.equals(lastHeights, heights)) {
            return lastResult;
        }

        boolean terminalChanged = (lastMaxWidth != -1)
                && (lastMaxWidth != maxWidth || lastMaxHeight != maxHeight);

        PlacementResult result = compute(widths, heights, maxWidth, maxHeight, margin, optimizeSpace, terminalChanged);

        lastWidths = Arrays.copyOf(widths, widths.length);
        lastHeights = Arrays.copyOf(heights, heights.length);
        lastMaxWidth = maxWidth;
        lastMaxHeight = maxHeight;
        lastMargin = margin;
        lastOptimizeSpace = optimizeSpace;
        lastResult = result;

        return result;
    }

    private PlacementResult compute(int[] widths, int[] heights,
                                    int maxWidth, int maxHeight,
                                    int margin, boolean optimizeSpace,
                                    boolean terminalChanged) {
        final int n = widths.length;
        if (n == 0) {
            return PlacementResult.success(new Integer[0], new Integer[0]);
        }

        boolean stopOnFirstFailure = !terminalChanged;

        // --- Pass 1: strict sequential ---
        Integer[] x1 = new Integer[n];
        Integer[] y1 = new Integer[n];
        int[][] placed1 = new int[n][4];
        int[] failed1 = placeInOrder(widths, heights, maxWidth, maxHeight, margin,
                sequentialOrder(n), x1, y1, placed1, 0, stopOnFirstFailure);
        compact(x1, y1, widths, heights, maxWidth, maxHeight, margin);

        Integer[] bestX = x1;
        Integer[] bestY = y1;
        int[] bestFailed = failed1;
        long bestMetric = packingMetric(x1, y1, widths, heights, maxWidth, maxHeight);

        // --- Pass 2 (only when optimizeSpace): aspect-reordered ---
        if (optimizeSpace) {
            Integer[] order = aspectOrder(widths, heights);
            Integer[] x2 = new Integer[n];
            Integer[] y2 = new Integer[n];
            int[][] placed2 = new int[n][4];
            int[] failed2 = placeInOrder(widths, heights, maxWidth, maxHeight, margin,
                    order, x2, y2, placed2, 0, stopOnFirstFailure);
            compact(x2, y2, widths, heights, maxWidth, maxHeight, margin);

            long metric2 = packingMetric(x2, y2, widths, heights, maxWidth, maxHeight);
            // Prefer fewer failures, then better (lower) metric
            if (failed2.length < bestFailed.length
                    || (failed2.length == bestFailed.length && metric2 < bestMetric)) {
                bestX = x2;
                bestY = y2;
                bestFailed = failed2;
                bestMetric = metric2;
            }
        }

        if (bestFailed.length == 0) {
            return PlacementResult.success(bestX, bestY);
        }
        return terminalChanged
                ? PlacementResult.terminalResized(bestX, bestY, bestFailed)
                : PlacementResult.windowDidNotFit(bestX, bestY, bestFailed);
    }

    private static Integer[] sequentialOrder(int n) {
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        return order;
    }

    /** Order by decreasing aspect ratio (wide-short first), stable on ties. */
    private static Integer[] aspectOrder(int[] widths, int[] heights) {
        Integer[] order = sequentialOrder(widths.length);
        Arrays.sort(order, (a, b) -> {
            double aspectA = (double) widths[a] / Math.max(1, heights[a]);
            double aspectB = (double) widths[b] / Math.max(1, heights[b]);
            int cmp = Double.compare(aspectB, aspectA);
            return cmp != 0 ? cmp : Integer.compare(a, b);
        });
        return order;
    }

    /**
     * Place windows in the given order.
     * Returns indices of every window that could not be placed.
     * When stopOnFirstFailure is true, aborts after the first failure
     * (later windows stay null and are reported as failed).
     */
    private int[] placeInOrder(int[] widths, int[] heights,
                               int maxWidth, int maxHeight, int margin,
                               Integer[] order,
                               Integer[] x, Integer[] y,
                               int[][] placed, int placedCount,
                               boolean stopOnFirstFailure) {

        List<Integer> failed = new ArrayList<>();
        boolean aborted = false;

        for (int oi = 0; oi < order.length; oi++) {
            int i = order[oi];

            if (aborted) {
                failed.add(i);
                continue;
            }

            int w = widths[i];
            int h = heights[i];

            if (w > maxWidth || h > maxHeight) {
                failed.add(i);
                if (stopOnFirstFailure) aborted = true;
                continue;
            }

            boolean wide = ((double) w / Math.max(1, h)) >= WIDE_ASPECT;

            int bestX = -1, bestY = -1;
            long bestScore = Long.MAX_VALUE;

            int[] candidateYs = buildCandidateYs(placed, placedCount, maxHeight, h, margin);
            int[] candidateXsBase = buildCandidateXs(placed, placedCount, maxWidth, w, margin);

            for (int cy : candidateYs) {
                if (cy < 0 || cy + h > maxHeight) continue;

                for (int cx : candidateXsBase) {
                    if (cx < 0 || cx + w > maxWidth) continue;
                    if (overlapsAny(cx, cy, w, h, placed, placedCount, margin)) continue;

                    long score = scoreCandidate(cx, cy, w, h, wide);
                    if (score < bestScore) {
                        bestScore = score;
                        bestX = cx;
                        bestY = cy;
                    }
                }
            }

            if (bestX < 0) {
                failed.add(i);
                if (stopOnFirstFailure) aborted = true;
                continue;
            }

            x[i] = bestX;
            y[i] = bestY;
            placed[placedCount][0] = bestX;
            placed[placedCount][1] = bestY;
            placed[placedCount][2] = w;
            placed[placedCount][3] = h;
            placedCount++;
        }

        return failed.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Score a candidate position.
     * Primary: top-most (small y). Secondary: left-most (small x).
     * Wide windows get an extra preference for smaller y.
     */
    private static long scoreCandidate(int cx, int cy, int w, int h, boolean wide) {

        long score = (long) cy * 1_000_000L + (long) cx * 1_000L + (cx + w) + (cy + h);

        if (wide) {
            // Stronger pull toward the top for wide-short windows
            score += (long) cy * 500_000L;
        }

        return score;
    }

    /** Richer Y candidates: 0, below/above every placed window, and bottom of current bbox. */
    private int[] buildCandidateYs(int[][] placed, int placedCount, int maxHeight, int h, int margin) {

        Set<Integer> set = new HashSet<>();
        set.add(0);
        int bboxBottom = 0;

        for (int i = 0; i < placedCount; i++) {

            int py = placed[i][1];
            int ph = placed[i][3];
            int bottom = py + ph + margin;

            if (bottom + h <= maxHeight) set.add(bottom);
            int above = py - h - margin;

            if (above >= 0) set.add(above);
            bboxBottom = Math.max(bboxBottom, py + ph);
        }

        if (bboxBottom > 0) {
            int belowBbox = bboxBottom + margin;
            if (belowBbox + h <= maxHeight) set.add(belowBbox);
        }

        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    /** Richer X candidates: 0, right/left of every placed window, and right edge of current bbox. */
    private int[] buildCandidateXs(int[][] placed, int placedCount, int maxWidth, int w, int margin) {

        Set<Integer> set = new HashSet<>();
        set.add(0);
        int bboxRight = 0;

        for (int i = 0; i < placedCount; i++) {

            int px = placed[i][0];
            int pw = placed[i][2];
            int right = px + pw + margin;

            if (right + w <= maxWidth) set.add(right);
            int left = px - w - margin;

            if (left >= 0) set.add(left);
            bboxRight = Math.max(bboxRight, px + pw);
        }

        if (bboxRight > 0) {
            int rightOfBbox = bboxRight + margin;
            if (rightOfBbox + w <= maxWidth) set.add(rightOfBbox);
        }

        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    private boolean overlapsAny(int cx, int cy, int w, int h,
                                int[][] placed, int placedCount, int margin) {

        for (int i = 0; i < placedCount; i++) {

            int px = placed[i][0];
            int py = placed[i][1];
            int pw = placed[i][2];
            int ph = placed[i][3];

            if (cx < px + pw + margin
                    && cx + w + margin > px
                    && cy < py + ph + margin
                    && cy + h + margin > py) {
                return true;
            }
        }

        return false;
    }

    /**
     * Lightweight post-placement compaction: repeatedly slide every placed window
     * left and up as far as possible without introducing overlaps.
     */
    private void compact(Integer[] x, Integer[] y, int[] widths, int[] heights,
                         int maxWidth, int maxHeight, int margin) {
        final int n = x.length;
        boolean moved;

        do {
            moved = false;
            for (int i = 0; i < n; i++) {
                if (x[i] == null) continue;
                int w = widths[i];
                int h = heights[i];

                // Slide left
                int newX = x[i];
                while (newX > 0) {
                    int tryX = newX - 1;
                    if (overlapsAnyPlaced(tryX, y[i], w, h, x, y, widths, heights, i, margin)) break;
                    newX = tryX;
                }
                if (newX != x[i]) {
                    x[i] = newX;
                    moved = true;
                }

                // Slide up
                int newY = y[i];
                while (newY > 0) {
                    int tryY = newY - 1;
                    if (overlapsAnyPlaced(x[i], tryY, w, h, x, y, widths, heights, i, margin)) break;
                    newY = tryY;
                }
                if (newY != y[i]) {
                    y[i] = newY;
                    moved = true;
                }
            }
        } while (moved);
    }

    private boolean overlapsAnyPlaced(int cx, int cy, int w, int h,
                                      Integer[] x, Integer[] y,
                                      int[] widths, int[] heights,
                                      int self, int margin) {
                                        
        for (int j = 0; j < x.length; j++) {

            if (j == self || x[j] == null) continue;

            int px = x[j], py = y[j], pw = widths[j], ph = heights[j];

            if (cx < px + pw + margin
                    && cx + w + margin > px
                    && cy < py + ph + margin
                    && cy + h + margin > py) {
                return true;
            }
        }

        return false;
    }

    /**
     * Packing quality metric (lower is better).
     * Combines bounding-box area with a penalty for empty space inside the box.
     * Windows that failed (null) are ignored.
     */
    private static long packingMetric(Integer[] x, Integer[] y,
                                      int[] widths, int[] heights,
                                      int maxWidth, int maxHeight) {
        
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxR = 0, maxB = 0;
        long usedArea = 0;
        int placed = 0;
        for (int i = 0; i < x.length; i++) {

            if (x[i] == null) continue;

            placed++;
            minX = Math.min(minX, x[i]);
            minY = Math.min(minY, y[i]);
            maxR = Math.max(maxR, x[i] + widths[i]);
            maxB = Math.max(maxB, y[i] + heights[i]);
            usedArea += (long) widths[i] * heights[i];
        }

        if (placed == 0) return Long.MAX_VALUE / 2;

        long bboxArea = (long) (maxR - minX) * (maxB - minY);
        long waste = bboxArea - usedArea;

        // Prefer smaller bounding box, then less internal waste, then more placed
        return bboxArea * 1000L + waste * 10L - placed;
    }

    /** Clears the internal cache. */
    public void clearCache() {
        lastWidths = null;
        lastHeights = null;
        lastMaxWidth = -1;
        lastMaxHeight = -1;
        lastMargin = -1;
        lastResult = null;
    }
}
