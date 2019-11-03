package com.trinhbk.lecturelivestream.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.Surface;
import android.view.TextureView;

public class DeviceUtil {
    private DeviceUtil() {

    }

    private static DeviceUtil instance;

    public static DeviceUtil getInstance() {
        if (instance == null) {
            instance = new DeviceUtil();
        }
        return instance;
    }

    public void transformImage(Activity activity, TextureView textureView, int width, int height) {

        if (textureView == null) {
            return;
        } else try {
            {
                Matrix matrix = new Matrix();
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                RectF textureRectF = new RectF(0, 0, width, height);
                RectF previewRectF = new RectF(0, 0, textureView.getHeight(), textureView.getWidth());
                float centerX = textureRectF.centerX();
                float centerY = textureRectF.centerY();
                if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                    previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
                    matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
                    float scale = Math.max((float) width / width, (float) height / width);
                    matrix.postScale(scale, scale, centerX, centerY);
                    matrix.postRotate(90 * (rotation - 2), centerX, centerY);
                }
                textureView.setTransform(matrix);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
