package com.gun0912.tedpicker.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;

import com.gun0912.tedpicker.R;
import com.gun0912.tedpicker.models.GalleryPhoto;
import com.gun0912.tedpicker.models.GalleryPhotoAlbum;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by johnhamilton on 6/22/16.
 */
public class GalleryUtil {

    public static final TypedValue TYPED_VALUE = new TypedValue();
    public static final String CAMERA_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "Camera/";



    public static ArrayList<GalleryPhotoAlbum> getAlbums(final Context context) {
        final ArrayList<GalleryPhotoAlbum> albumsSorted = new ArrayList<>();

        final HashMap<Integer, GalleryPhotoAlbum> albums = new HashMap<Integer, GalleryPhotoAlbum>();


        GalleryPhotoAlbum allPhotosAlbum = null;


        Cursor imagesCursor = null;

        try {

            imagesCursor = queryImages(context);
            allPhotosAlbum = iterateCursor(context, imagesCursor, allPhotosAlbum, albumsSorted, albums, false);


        } catch (Exception ex) {
            Log.e("getAlbums", ex.getMessage());
        } finally {

            closeCursors(imagesCursor);

        }

        setPickedFlagForPickedImages(albumsSorted);

        for (final GalleryPhotoAlbum album : albumsSorted) {
            album.sortImagesByTimeDesc();
        }


        return albumsSorted;

    }


    private static Cursor queryImages(final Context context) {

        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.ORIENTATION
        };

        return MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projectionPhotos, "", null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
    }



    private static void setPickedFlagForPickedImages(final ArrayList<GalleryPhotoAlbum> albumsSorted) {
        //Check all photos album
        final GalleryPhotoAlbum allPhotosAlbum = getAllPhotosAlbum(albumsSorted);

        if (allPhotosAlbum == null) {
            return;
        }

       /* if (!PickerActivity.sCheckedImages.isEmpty() && !allPhotosAlbum.imageList.isEmpty()) {

            for (final ImageEntry checkedImage : PickerActivity.sCheckedImages) {
                for (final ImageEntry imageEntry : allPhotosAlbum.imageList) {
                    imageEntry.isPicked = imageEntry.equals(checkedImage);
                }
            }
        }*/
    }

    public static GalleryPhotoAlbum getAllPhotosAlbum(final ArrayList<GalleryPhotoAlbum> albumEntries) {
        for (final GalleryPhotoAlbum albumEntry : albumEntries) {
            if (albumEntry.getBucketId() == 0) {
                return albumEntry;
            }
        }
        return null;
    }

    private static GalleryPhotoAlbum iterateCursor(final Context context, final Cursor cursor, GalleryPhotoAlbum allPhotosAlbum, final ArrayList<GalleryPhotoAlbum> albumsSorted, final HashMap<Integer, GalleryPhotoAlbum> albums, final boolean isVideoCursor) {

        if (cursor == null) return null;

        final int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);


        while (cursor.moveToNext()) {

            final GalleryPhoto imageEntry = getImageFromCursor(cursor, isVideoCursor);

            if (imageEntry.path == null || imageEntry.path.length() == 0) {
                continue;

            }

            allPhotosAlbum = createAllPhotosAlbumIfDoesntExist(context, allPhotosAlbum, albumsSorted);
            allPhotosAlbum.addPhoto(imageEntry);

            final int bucketId = cursor.getInt(bucketIdColumn);
            final String bucketName = cursor.getString(bucketNameColumn);

            GalleryPhotoAlbum albumEntry = albums.get(bucketId);
            if (albumEntry == null) {
                albumEntry = createNewAlbumAndAddItToArray(albums, bucketId, bucketName);

                if (shouldCreateCameraAlbum(imageEntry)) {
                    addCameraAlbumToArray(albumsSorted, albumEntry);

                } else {
                    albumsSorted.add(albumEntry);
                }
            }


            albumEntry.addPhoto(imageEntry);
        }

        return allPhotosAlbum;


    }

    private static void addCameraAlbumToArray(final ArrayList<GalleryPhotoAlbum> albumsSorted, final GalleryPhotoAlbum albumEntry) {
        albumsSorted.add(0, albumEntry);
    }

    private static boolean shouldCreateCameraAlbum(final GalleryPhoto imageEntry) {
        return imageEntry.path.startsWith(CAMERA_FOLDER);
    }

    @NonNull
    private static GalleryPhotoAlbum createNewAlbumAndAddItToArray(final HashMap<Integer, GalleryPhotoAlbum> albums, final int bucketId, final String bucketName) {

        final GalleryPhotoAlbum albumEntry = new GalleryPhotoAlbum(bucketId, bucketName);
        albums.put(bucketId, albumEntry);
        return albumEntry;
    }

    @NonNull
    private static GalleryPhoto getImageFromCursor(final Cursor cursor, final boolean isVideoCursor) {
        final GalleryPhoto imageEntry = GalleryPhoto.from(cursor);
        imageEntry.isVideo = isVideoCursor;
        return imageEntry;
    }

    @NonNull
    private static GalleryPhotoAlbum createAllPhotosAlbumIfDoesntExist(Context context, GalleryPhotoAlbum allPhotosAlbum, ArrayList<GalleryPhotoAlbum> albumsSorted) {
        if (allPhotosAlbum == null) {
            allPhotosAlbum = new GalleryPhotoAlbum(0, context.getResources().getString(R.string.all_photos));
            addCameraAlbumToArray(albumsSorted, allPhotosAlbum);
        }
        return allPhotosAlbum;
    }


    private static void closeCursors(final Cursor imagesCursor) {

        if (imagesCursor != null) {
            imagesCursor.close();
        }
    }


}
