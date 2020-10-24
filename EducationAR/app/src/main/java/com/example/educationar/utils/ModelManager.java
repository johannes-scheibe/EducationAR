package com.example.educationar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.educationar.MainActivity;
import com.example.educationar.R;
import com.example.educationar.artoolkitx.rendering.ShaderProgram;
import com.example.educationar.shader_impl.MyFragmentShader;
import com.example.educationar.shader_impl.MyShaderProgram;
import com.example.educationar.shader_impl.MyVertexShader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelManager {

    private static Logger logger = Logger.getLogger("EduAR-ModelManager");

    private static SharedPreferences sharedPrefs = MainActivity.getContext().getSharedPreferences("Models", Context.MODE_PRIVATE);

    private static ModelManager instance;

    public static  ModelManager getInstance(){
        if(instance == null){
            instance = new ModelManager();
        }
        return instance;
    }

    private static Context mContext;

    private Map<Integer, Model> models;

    private ModelManager(){
        mContext =  MainActivity.getContext();
        loadModels();
    }

    public static String getFileName(int id){
        return getFileName(Integer.toString(id));
    }
    public static String getFileName(String id){
        return "model" + id;
    }

    private void loadModels(){
        models = new HashMap<>();
        File dir = mContext.getFilesDir();

        Map<String,?> modelRefs = sharedPrefs.getAll();

        for(String key : modelRefs.keySet()){
            File modelFile = new File(dir, getFileName(key));
            File textureFile = new File(dir, getFileName(key)+"-texture");
            Model model = new Model(mContext, modelFile, textureFile);

            this.models.put(Integer.parseInt(key.trim()), model);
        }

    }

    public void addModel(int id, String name){
        File dir = mContext.getFilesDir();

        File modelFile = new File(dir, getFileName(id));
        File textureFile = new File(dir, getFileName(id) + "-texture");
        Model model = new Model(mContext, modelFile, textureFile);

        // added to map
        models.put(id, model);

        // add to shared preferences
        SharedPreferences.Editor modelEditor = sharedPrefs.edit();
        modelEditor.putString(Integer.toString(id), name);
        modelEditor.commit();
    }

    public void deleteModel(Context context, int id){
        // Delete model and texture
        File dir = context.getFilesDir();
        File file = new File(dir, getFileName(id));
        file.delete();
        file = new File(dir, getFileName(id)+"-texture");
        file.delete();

        // remove from map
        models.remove(id);

        // remove from shared preferences
        SharedPreferences.Editor modelEditor = sharedPrefs.edit();
        modelEditor.remove(Integer.toString(id));
        modelEditor.commit();

        CharSequence text = "Modell erfolgreich entfernt!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public Map<Integer, Model> getModels(){
        ShaderProgram shaderProgram = new MyShaderProgram(new MyVertexShader(), new MyFragmentShader());
        for (Model model: models.values())
            model.initialise(shaderProgram);
        return models;
    }
    public Map getModelReferences(){
        return sharedPrefs.getAll();
    }

    public static int getNextID(Context context){
        SharedPreferences prefs = context.getSharedPreferences("Models", Context.MODE_PRIVATE);
        int i = 0;
        while(prefs.getString(Integer.toString(i), null)!=null){
            i++;
        }
        return i;
    }

}
