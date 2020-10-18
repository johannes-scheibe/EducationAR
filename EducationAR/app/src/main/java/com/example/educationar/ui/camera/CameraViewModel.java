package com.example.educationar.ui.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private MutableLiveData<Integer> fps;

    public CameraViewModel() {
        fps = new MutableLiveData<Integer>();

    }

    public void setFPS(int fps){
        this.fps.setValue(fps);
    }

    public LiveData<Integer> getFPS(){
        return fps;
    }
}