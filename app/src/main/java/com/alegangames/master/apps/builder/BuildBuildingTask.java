package com.alegangames.master.apps.builder;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.alegangames.master.apps.builder.utils.ChunkManager;
import com.alegangames.master.apps.builder.utils.CuboidClipboard;
import com.alegangames.master.apps.builder.utils.MinecraftWorldUtil;
import com.alegangames.master.apps.builder.utils.SchematicIO;
import com.alegangames.master.apps.builder.utils.Vector3f;
import com.alegangames.master.apps.builder.utils.leveldb.LevelDataConverter;
import com.alegangames.master.util.files.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuildBuildingTask implements Runnable {

    private static final String TAG = BuildBuildingTask.class.getSimpleName();
    private ChunkManager mChunkManager;
    private CuboidClipboard mCuboidClipboard;
    private static String sWorldPath;
    private Context mContext;
    private InterfaceBuilding mInterfaceResult;
    private String valueX;
    private String valueY;
    private String valueZ;
    private String mWorldFolder;

    private volatile boolean isCancelled;

    public interface InterfaceBuilding {
        void onBuildingResultListener(boolean result);
    }

    public BuildBuildingTask(Context context, String worldFolder, InterfaceBuilding interfaceResult) {
        this.mContext = context;
        this.mInterfaceResult = interfaceResult;
        this.mWorldFolder = worldFolder;
        sWorldPath = Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftWorlds/" + mWorldFolder + "/";
        fillData();
        backupWorld();
    }

    @Override
    public void run() {
        boolean result = buildBuilding();
        onResult(result);
    }


    protected Boolean buildBuilding() {
        try {
            if (MinecraftWorldUtil.level.getDimension() == 0) {
                String pathDB = Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftWorlds/" + mWorldFolder + "/db/";
                String pathSchematic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/plan.schematic";

                MinecraftWorldUtil.buildY = valueY;
                Log.d(TAG, "buildBuilding: new ChunkManager");
                mChunkManager = new ChunkManager(new File((pathDB)), (Activity) mContext);
                Log.d(TAG, "buildBuilding: created");
                Vector3f vector3f = new Vector3f(Float.parseFloat(valueX) + 10.0F, Float.parseFloat(valueY), Float.parseFloat(valueZ) + 10.0F);
                Log.d(TAG, "buildBuilding: IO.read");

                //создаем объект
                if (isCancelled) return false;
                mCuboidClipboard = SchematicIO.read(new File(pathSchematic));
                mCuboidClipboard.place(mChunkManager, vector3f, false);

                System.out.println("Saving chunks...");

                if (isCancelled) return false;
                mChunkManager.saveAll();


                if (isCancelled) return false;
                Log.d(TAG, "buildBuilding: unload chunks");
                mChunkManager.unloadChunks(false);


                try {
                    mChunkManager.close();
                } catch (IOException localIOException) {
                    localIOException.printStackTrace();
                    return false;
                }

                return true;

            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancel() {
        isCancelled = true;
        if (mCuboidClipboard != null) {
            mCuboidClipboard.isCanceled = true;
        }
    }

    private void onResult(Boolean result) {
        if (!result) {
            //если задача не выполненна, то закрываем ресурсы
            if (mCuboidClipboard != null) {
                mCuboidClipboard.isCanceled = true;
            }

            if (mChunkManager != null) {
                try {
                    mChunkManager.unloadChunks(false);
                    mChunkManager.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            if (mChunkManager != null) {
                try {
                    mChunkManager.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (mInterfaceResult != null) {
            Handler mainHandler = new Handler(mContext.getMainLooper());
            mainHandler.post(() -> mInterfaceResult.onBuildingResultListener(result));
        }
    }

    private void fillData() {
        try {
            MinecraftWorldUtil.level = LevelDataConverter.read(new File(sWorldPath, "level.dat"));
            valueX = ((int) MinecraftWorldUtil.level.getPlayer().getLocation().getX() + "");
            valueY = ((int) MinecraftWorldUtil.level.getPlayer().getLocation().getY() + "");
            valueZ = ((int) MinecraftWorldUtil.level.getPlayer().getLocation().getZ() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void backupWorld() {
        new Thread(new BackupTask(sWorldPath, new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(new Date()) + "_backup/")).start();
    }

    private class BackupTask implements Runnable {
        private String _backupFolder;
        private String _worldPath;

        BackupTask(final String worldPath, final String backupFolder) {
            this._worldPath = worldPath;
            this._backupFolder = backupFolder;
        }

        @Override
        public void run() {
            try {
                File file = new File(this._worldPath + "db/");
                File file2 = new File(this._worldPath + "level.dat");
                File file3 = new File(this._worldPath + "levelname.txt");
                File file4 = new File(this._backupFolder + "db/");
                File file5 = new File(this._backupFolder + "level.dat");
                File file6 = new File(this._backupFolder + "levelname.txt");
                FileUtil.copyDirectory(file, file4);
                FileUtil.copyDirectory(file2, file5);
                FileUtil.copyDirectory(file3, file6);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

