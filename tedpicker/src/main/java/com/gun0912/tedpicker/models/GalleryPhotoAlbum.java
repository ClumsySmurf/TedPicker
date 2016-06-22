package com.gun0912.tedpicker.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mushthak on 22/03/16.
 */
public class GalleryPhotoAlbum {


    private long bucketId;
    private String bucketName;
    private String dateTaken;
    private String data;
    private int totalCount;

    public final ArrayList<GalleryPhoto> imageList = new ArrayList<>();


    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public long getBucketId() {
        return bucketId;
    }
    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    public String getDateTaken() {
        return dateTaken;
    }
    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public GalleryPhoto coverImage;



    public GalleryPhotoAlbum() {

    }
    public GalleryPhotoAlbum(int albumId, String albumName) {
        this.bucketId = albumId;
        this.bucketName = albumName;
    }

    public GalleryPhotoAlbum(int albumId, String albumName, String bucketDate, String bucketData) {
        this.bucketId = albumId;
        this.bucketName = albumName;
        this.dateTaken = bucketDate;
        this.data = bucketData;
    }



    public void addPhoto(GalleryPhoto imageEntry) {
        imageList.add(imageEntry);
    }

    public void sortImagesByTimeDesc() {
        Collections.sort(imageList, new Comparator<GalleryPhoto>() {
            @Override
            public int compare(GalleryPhoto lhs, GalleryPhoto rhs) {
                return (int) (rhs.dateAdded - lhs.dateAdded);
            }
        });

        coverImage = imageList.get(0);
    }
}