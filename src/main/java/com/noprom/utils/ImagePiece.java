package com.noprom.utils;

import android.graphics.Bitmap;

/**
 * 将一张大图切成多张小图片
 *
 * @author noprom
 *         Created by noprom.
 */
public class ImagePiece {
    // 当前图片的索引
    private int mIndex;

    // 当前关的图片
    private Bitmap mBitmap;

    public ImagePiece() {

    }

    public ImagePiece(int index, Bitmap bitmap) {
        mIndex = index;
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "mIndex=" + mIndex +
                ", mBitmap=" + mBitmap +
                '}';
    }
}
