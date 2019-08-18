package com.example.picturepuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Random;

public class GameState {
    public int tilePositions[] = new int[16];
    public ArrayList<Bitmap> tileImages;
    public GameState(Context context) {
        Bitmap bigImage = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.cropped_test);
        int fullW = bigImage.getWidth(), fullH = bigImage.getHeight();
        int tileW = fullW / 4, tileH = fullH / 4;
        tileImages = new ArrayList<>();
        for (int y=0;y<4;y++) {
            for (int x=0;x<4;x++) {
                Bitmap tileBitmap = Bitmap.createBitmap(
                        bigImage, x * tileW, y * tileH, tileW, tileH);
                tileImages.add(tileBitmap);
            }
        }
        newGamePositionSet();
    }

    public void newGamePositionSet() {
        for (int i=0;i<4*4;++i) tilePositions[i] = i;
        RandomizeArray(tilePositions);
    }

    private static void RandomizeArray(int[] array){
        Random rgen = new Random();  // Random number generator
        int randomFactor = rgen.nextInt(1000);
        int zeroIdx = 0, replace, nbCount;
         int[] neighbors = new int[4];
         for (int i=0;i<randomFactor;++i) {
             nbCount = 0;
             if ((zeroIdx % 4) > 0) {
                 neighbors[nbCount++] = zeroIdx - 1;
             }
             if ((zeroIdx % 4) < 3) {
                 neighbors[nbCount++] = zeroIdx + 1;
             }
             if ((zeroIdx / 4) > 0) {
                 neighbors[nbCount++] = zeroIdx - 4;
             }
             if ((zeroIdx / 4) < 3) {
                 neighbors[nbCount++] = zeroIdx + 4;
             }
             replace = neighbors[rgen.nextInt(nbCount)];
             array[zeroIdx] = array[replace];
             array[replace] = 0;
             zeroIdx = replace;
         }
    }

    public int getImageIdx(int tileIdx) {
        for (int i=0;i<tilePositions.length;++i) {
            if (tilePositions[i] == tileIdx) {
                return i;
            }
        }
        return 0;
    }

    public void onImageClicked(int imageId) {
        int imgIdx = getImageIdx(imageId);
        int zeroIdx = getImageIdx(0);
        // check if imgIdx and zeroIdx are on sides
        if ((((imgIdx / 4) == (zeroIdx / 4)) && Math.abs(imgIdx-zeroIdx) == 1) ||
                (((imgIdx % 4) == (zeroIdx % 4)) && Math.abs(imgIdx-zeroIdx) == 4 )) {
            tilePositions[zeroIdx] = imageId;
            tilePositions[imgIdx] = 0;
        }
    }

    public boolean canMoveToEmpty(int imageId) {
        int imgIdx = getImageIdx(0);
        int zeroIdx = getImageIdx(imageId);
        return (((imgIdx / 4) == (zeroIdx / 4)) && Math.abs(imgIdx-zeroIdx) == 1) ||
                (((imgIdx % 4) == (zeroIdx % 4)) && Math.abs(imgIdx-zeroIdx) == 4 );
    }

    public boolean isWon() {
        for (int i=0;i<tilePositions.length;++i) {
            if (tilePositions[i] != i) return false;
        }
        return true;
    }
}
