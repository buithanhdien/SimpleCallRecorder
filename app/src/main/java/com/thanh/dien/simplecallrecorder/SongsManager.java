package com.thanh.dien.simplecallrecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SongsManager {

    public ArrayList<HashMap<String, String>> getPlayList(String rootPath){
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();

        try{
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    if(getPlayList(file.getAbsolutePath()) != null){
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    }else{
                        break;
                    }
                }else if(file.getName().endsWith(".mp3")){
                    long lastmodified = file.lastModified();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                    String lastmod = format.format(new Date(lastmodified));

                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_size", Integer.toString((int)file.length()/1024)+" Kb");
                    song.put("file_name", file.getName());
                    song.put("file_date", lastmod);
                    fileList.add(song);
                }
            }
            return fileList;
        }catch(Exception e){
            return null;
        }
    }

}
