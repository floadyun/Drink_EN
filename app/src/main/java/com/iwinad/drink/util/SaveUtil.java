package com.iwinad.drink.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;

import com.base.lib.util.DeviceUtils;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.log.ViseLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/10
 * @description:
 */
public class SaveUtil {
    /**
     * 保存拍照的图片
     *
     * @param data
     */
    public static String saveImage(Context context, CameraPreview mFace_detector_preview,DetectorData mDetectorData, byte[] data) {
        File pictureFile = getOutputFile(context, "face", "photo.jpg");//拍照图片
        File avatarFile = getOutputFile(context, "face", "avatar.jpg");//截取人脸图片
        if (pictureFile == null || avatarFile == null) {
            return "";
        }
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
        if (avatarFile.exists()) {
            avatarFile.delete();
        }
        Rect rect = new Rect();
        if (mDetectorData != null && mDetectorData.getFaceRectList() != null
                && mDetectorData.getFaceRectList().length > 0
                && mDetectorData.getFaceRectList()[0].right > 0) {
            rect = mDetectorData.getFaceRectList()[0];
        }
        ViseLog.i("save picture start!");
        Bitmap bitmap = getImage(context,mFace_detector_preview,data, rect, 150, 200, pictureFile, avatarFile);
        ViseLog.i("save picture complete!");
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return pictureFile.getAbsolutePath();
    }
    private static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = "";
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context != null && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            if (context != null && context.getCacheDir() != null) {
                cachePath = context.getCacheDir().getPath();
            }
        }
        return cachePath + File.separator + dirName;
    }
    private static File getOutputFile(Context context, String dirName, String fileName) {
        File dirFile = new File(getDiskCacheDir(context, dirName));
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                ViseLog.d("failed to create directory");
                return null;
            }
        }
        File file = new File(dirFile.getPath() + File.separator + fileName);
        return file;
    }
    private static Bitmap getImage(Context context,CameraPreview mFace_detector_preview,byte[] data, Rect rect, float ww, float hh, File pictureFile, File avatarFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int w = options.outWidth;
        int h = options.outHeight;
        float scale = 1.0F;
        if (w < h) {
            scale = ww / (float) w;
            if (hh / (float) h > scale) {
                scale = hh / (float) h;
            }
        } else {
            scale = ww / (float) h;
            if (hh / (float) w > scale) {
                scale = hh / (float) w;
            }
        }
        Bitmap scaleBitmap = scaleImage(bitmap, (int) ((float) w * scale), (int) ((float) h * scale));
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return compressImage(context,mFace_detector_preview,scaleBitmap, rect, 1536, pictureFile, avatarFile);
    }
    private static Bitmap compressImage(Context context,CameraPreview mFace_detector_preview,Bitmap image, Rect rect, int size, File pictureFile, File avatarFile) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = image;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {
            baos.reset();
            options -= 5;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pictureFile);
            if (mFace_detector_preview != null) {
                if (mFace_detector_preview.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    bitmap = rotaingImageView(360 - mFace_detector_preview.getDisplayOrientation(), bitmap);
                } else {
                    bitmap = rotaingImageView(mFace_detector_preview.getDisplayOrientation(), bitmap);
                }
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, fileOutputStream);
        } catch (FileNotFoundException var18) {
            ViseLog.e("File not found: " + var18.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException var16) {
                ViseLog.e("Error accessing file: " + var16.getMessage());
            }
        }
        float scale = (float) bitmap.getHeight() / DeviceUtils.getScreenHeight(context);
        if (rect.right > 0) {
            int top = (int) (rect.top * scale);
            int bottom = (int) (rect.bottom * scale);
            rect.left = 0;
            rect.right = bitmap.getWidth();
            rect.top = top - (bitmap.getWidth() - (bottom - top)) / 2;
            rect.bottom = bottom + (bitmap.getWidth() - (bottom - top)) / 2;
        } else {
            rect.left = 0;
            rect.right = bitmap.getWidth();
            rect.top = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            rect.bottom = (bitmap.getHeight() - bitmap.getWidth()) / 2 + bitmap.getWidth();
        }
//        Bitmap avatarBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
//        try {
//            fileOutputStream = new FileOutputStream(avatarFile);
//            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
//        } catch (FileNotFoundException var18) {
//            ViseLog.e("File not found: " + var18.getMessage());
//        } finally {
//            if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
//                avatarBitmap.recycle();
//                avatarBitmap = null;
//            }
//            try {
//                if (fileOutputStream != null) {
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                }
//            } catch (IOException var16) {
//                ViseLog.e("Error accessing file: " + var16.getMessage());
//            }
//
//        }

        if (baos != null) {
            try {
                baos.close();
            } catch (IOException var17) {
                var17.printStackTrace();
            }
        }

        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }

        return bitmap;
    }
    private static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        } else {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = (float) newWidth / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            if (bm != null & !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }

            return newbm;
        }
    }
    private static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        return resizedBitmap;
    }
}
