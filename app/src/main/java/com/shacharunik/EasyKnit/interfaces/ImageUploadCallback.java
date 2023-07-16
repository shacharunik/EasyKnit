package com.shacharunik.EasyKnit.interfaces;

public interface ImageUploadCallback {
    void onImageUploadSuccess(String imageUrl);
    void onImageUploadFailure(Exception exception);
}
