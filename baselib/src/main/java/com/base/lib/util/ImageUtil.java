package com.base.lib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.orhanobut.logger.Logger;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2018/7/18
 * @description:图片处理类
 */
public class ImageUtil {
    /**
     * 切割图片
     * @param bitmap
     * @param xPiece
     * @param yPiece
     * @return
     */
    public static List<ImagePiece> split(Bitmap bitmap, int xPiece, int yPiece) {
        List<ImagePiece> pieces = new ArrayList<>(xPiece * yPiece);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xPiece;
        int pieceHeight = height / yPiece;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                ImagePiece piece = new ImagePiece();
                piece.index = j + i * xPiece;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth, pieceHeight);
                pieces.add(piece);
            }
        }
        return pieces;
    }
    /**
     * 图片Base64编码
     * @param path
     * @return
     */
    public static String imageToBase64(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Logger.d("bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] bytes = baos.toByteArray();
       return Base64.encodeToString(bytes,Base64.NO_WRAP);
    }
    /**
     *base64编码字符集转化成图片文件。
     * @param base64Str
     * @param path 文件存储路径
     * @return 是否成功
     */
    public static boolean base64ToFile(String base64Str,String path){
        FileUtil.createfile(path);
        byte[] data = Base64.decode(base64Str, Base64.NO_WRAP);
        for (int i = 0; i < data.length; i++) {
            if(data[i] < 0){
                //调整异常数据
                data[i] += 256;
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            os.write(data);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
