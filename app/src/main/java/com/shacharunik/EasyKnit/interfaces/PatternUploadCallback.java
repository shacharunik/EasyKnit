package com.shacharunik.EasyKnit.interfaces;

public interface PatternUploadCallback {
    void onPatternUploadSuccess();
    void onPatternUploadFailure(Exception exception);
}
