package com.example.educationar.ui.marker;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.educationar.utils.MarkerGenerator;

public class MarkerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MarkerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}