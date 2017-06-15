package com.mirrorai.app.utils;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CameraPreviewSizeUtils {

    private static final double ASPECT_COMPARSION_TOLERANCE = 0.03;
    private static final double SIZE_COMPARSION_LIGHT_TOLERANCE = 0.1;
    private static final double SIZE_COMPARSION_HARD_TOLERANCE = 0.5;

    /**
     * Selects optimal preview size from array of available sizes. Available sizes are always
     * targeted landscape mode so target width and height should target landscape mode as well.
     * @param cameraParameters Camera parameters.
     * @param targetWidth Target width.
     * @param targetHeight Target height.
     * @return Optimal preview size.
     */
    @Nullable
    public static Camera.Size getPreviewSize(@NonNull Camera.Parameters cameraParameters, int targetWidth, int targetHeight) {
        final Camera.Size defaultPreviewSize = cameraParameters.getPreviewSize();

        final List<Camera.Size> supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();

        if (supportedPreviewSizes == null) {
            return defaultPreviewSize;
        }

        if (supportedPreviewSizes.size() == 0) {
            return defaultPreviewSize;
        }

        sortSupportedPreviewSizesByDescendingWidthAndHeight(supportedPreviewSizes);

        // Let's try to find supported preview size that is equal to the target preview size.
        // This likely does not happen, but sometimes it may be.

        for (Camera.Size previewSize : supportedPreviewSizes) {
            if (previewSize.width == targetWidth && previewSize.height == targetHeight) {
                return previewSize;
            }
        }

        // Now let's check that we have supported preview sizes with the same aspect
        // but a bit larger or a bit smaller (+- 10%).
        // Also aspect may be a varying a bit (+- 3%). This will help to compare aspects with
        // rounding errors and possible strange aspect differences.

        final double targetAspectRatio = targetWidth / (double)targetHeight;

        for (Camera.Size previewSize : supportedPreviewSizes) {
            final double previewSizeAspectRatio = previewSize.width / (double)previewSize.height;

            if (!isEqualWithTolerance(targetAspectRatio, previewSizeAspectRatio, ASPECT_COMPARSION_TOLERANCE)) {
                continue;
            }

            if (!isEqualWithTolerance(previewSize.width, targetWidth, SIZE_COMPARSION_LIGHT_TOLERANCE)) {
                continue;
            }

            if (!isEqualWithTolerance(previewSize.height, targetHeight, SIZE_COMPARSION_LIGHT_TOLERANCE)) {
                continue;
            }

            return previewSize;
        }

        // Next let's check that we have supported preview sizes with the same aspect but with 50% tolerance.
        // Aspect still may be a varying a bit (+- 3%).

        for (Camera.Size previewSize : supportedPreviewSizes) {
            final double previewSizeAspectRatio = previewSize.width / (double)previewSize.height;

            if (!isEqualWithTolerance(targetAspectRatio, previewSizeAspectRatio, ASPECT_COMPARSION_TOLERANCE)) {
                continue;
            }

            if (!isEqualWithTolerance(previewSize.width, targetWidth, SIZE_COMPARSION_HARD_TOLERANCE)) {
                continue;
            }

            if (!isEqualWithTolerance(previewSize.height, targetHeight, SIZE_COMPARSION_HARD_TOLERANCE)) {
                continue;
            }

            return previewSize;
        }

        // At this place we could not find preview size that is equal or almost equal to the target size.
        // Let's just try to find preview that has same almost aspect but lesser than target size.

        for (Camera.Size previewSize : supportedPreviewSizes) {
            final double previewSizeAspectRatio = previewSize.width / (double)previewSize.height;

            if (!isEqualWithTolerance(targetAspectRatio, previewSizeAspectRatio, ASPECT_COMPARSION_TOLERANCE)) {
                continue;
            }

            if (previewSize.width > targetWidth || previewSize.height > targetHeight) {
                continue;
            }

            return previewSize;
        }

        // At this point we failed to find any more or less suitable preview size at all.
        // Let's just find preview size that is required minimum scale transformation to be equal
        // to the target size.

        final SortedMap<Double, Camera.Size> scaleFactors = new TreeMap<>();

        for (Camera.Size previewSize : supportedPreviewSizes) {
            final double scaleFactorWidth = previewSize.width / (double)targetWidth;
            final double scaleFactorHeight = previewSize.width / (double)targetHeight;

            if (scaleFactorWidth > scaleFactorHeight) {
                scaleFactors.put(Math.abs(1.0 - scaleFactorWidth), previewSize);
            } else {
                scaleFactors.put(Math.abs(1.0 - scaleFactorHeight), previewSize);
            }
        }

        return scaleFactors.get(scaleFactors.firstKey());
    }

    private static void sortSupportedPreviewSizesByDescendingWidthAndHeight(List<Camera.Size> supportedPreviewSizes) {
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                if (o1.width == o2.width) {
                    if (o1.height < o2.height) {
                        return 1;
                    } if (o1.height > o2.height) {
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    if (o1.width < o2.width) {
                        return 1;
                    } if (o1.width > o2.width) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });
    }

    /***
     * Check equality with allowed tolerance. Function will count range from first value and
     * second value will be checked if it is withing this range.
     * @param value1 First value.
     * @param value2 Second value.
     * @param tolerance Toleance for the first value.
     * @return Is value1 equal value2 with tolerance.
     */
    private static boolean isEqualWithTolerance(double value1, double value2, double tolerance) {
        double value1Min = value1 - value1 * tolerance;
        double value1Max = value1 + value1 * tolerance;

        return value1Min <= value2 && value2 <= value1Max;
    }
}
