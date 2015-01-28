package com.noprom.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noprom .
 */
public class ImageSplitterUtil {

    /**
     * 传入Bitmap，并且切成pieces*pieces块
     *
     * @param bitmap 图
     * @param pieces 块数
     * @return List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int pieces) {
        List<ImagePiece> imagePieces = new ArrayList<>();

        // 获得bitmap宽度和高度
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 得到每一块的宽度
        int pieceWidth = Math.min(width, height) / pieces;

        // 切图
        for (int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * pieces);

                // 获得要截图的图片的坐标
                int x = j * pieceWidth;
                int y = i * pieceWidth;

                // 创建每一个小图
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));

                // 加入到整个集合
                imagePieces.add(imagePiece);
            }

        }

        return imagePieces;
    }


}
