package com.example.educationar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.educationar.MainActivity;
import com.example.educationar.R;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelManager {

    private static Logger logger = Logger.getLogger("EduAR-ModelManager");

    private static SharedPreferences sharedPrefs = MainActivity.getContext().getSharedPreferences("Models", Context.MODE_PRIVATE);

    public static void addModel(int id, String name){
        SharedPreferences.Editor modelEditor = sharedPrefs.edit();
        modelEditor.putString(Integer.toString(id), name);
        modelEditor.commit();
    }

    public static void deleteModel(Context context, int id, String name){
        // Delete model and texture
        File dir = context.getFilesDir();
        File file = new File(dir, name);
        file.delete();
        file = new File(dir, name+"-texture");
        file.delete();
        // remove from shared preferences

        SharedPreferences.Editor modelEditor = sharedPrefs.edit();
        modelEditor.remove(Integer.toString(id));
        modelEditor.commit();

        CharSequence text = "Modell erfolgreich entfernt!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static Map getAllModels(){
        return sharedPrefs.getAll();
    }

    public static int getNextID(Context context){
        SharedPreferences prefs = context.getSharedPreferences("Models", Context.MODE_PRIVATE);
        logger.log(Level.INFO, "Starting");

        int i = 0;
        while(prefs.getString(Integer.toString(i), null)!=null){
            i++;
        }
        return i;
    }

}
