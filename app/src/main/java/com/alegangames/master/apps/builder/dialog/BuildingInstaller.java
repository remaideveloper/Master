package com.alegangames.master.apps.builder.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alegangames.master.R;
import com.alegangames.master.apps.builder.BuildBuildingTask;
import com.alegangames.master.apps.builder.entities.World;
import com.alegangames.master.apps.builder.utils.MinecraftWorldUtil;
import com.alegangames.master.events.BuildingProgressEvent;
import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class BuildingInstaller {

    public static final int CANCELED = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;
    private static final String TAG = BuildingInstaller.class.getSimpleName();
    private Context mContext;
    private ArrayList<World> worldList;
    private BuildingListener mInterfaceBuilding;

    private BuildBuildingTask buildingTask;

    public BuildingInstaller(Context context) {
        this.mContext = context;
        worldList = MinecraftWorldUtil.getMinecraftWorld(Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftWorlds");
        if (worldList != null && !worldList.isEmpty()) {
            Collections.reverse(this.worldList);
        }
    }

    public void initList(final BuildingListener interfaceResult) {
        mInterfaceBuilding = interfaceResult;
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = view.findViewById(android.R.id.text1);
                    text1.setTextColor(Color.BLACK);
                    return view;
                }
            };

            if (worldList != null && !worldList.isEmpty()) {
                for (int i = 0; i < worldList.size(); i++) {
                    adapter.add(worldList.get(i).getName());
                }
            } else {
                //Если карт нету, сообщить об этом
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.no_maps_found)
                        .setMessage(R.string.create_one_map)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
                return;
            }

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.select_map)
                    .setCancelable(true)
                    .setAdapter(adapter, (dialog, position) -> runBuilding(worldList.get(position).getFolder_name())).create().show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    /**
     * Запускает построение карты
     *
     * @param worldFolder
     */
    private void runBuilding(String worldFolder) {
        buildingTask = new BuildBuildingTask(mContext, worldFolder, BuildingInstaller.this::onBuildingResult);
        Thread buildThread = new Thread(buildingTask);
        buildThread.start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadProgress(BuildingProgressEvent event) {
        if (mInterfaceBuilding != null) {
            mInterfaceBuilding.onBuildingProgress(event.getProgress());
        }
    }

    /**
     * Вызывается при завершении задачи
     *
     * @param result успешно ли выполнение
     */
    private void onBuildingResult(boolean result) {
        int resultCode = result ? SUCCESS : ERROR;
        if (mInterfaceBuilding != null) {
            mInterfaceBuilding.onBuildingResult(resultCode);
        }

        //Удаляем файл постройки
        File buildingFile = new File(Environment.getExternalStorageDirectory(), "/download/plan.schematic");
//        FileUtils.deleteQuietly(buildingFile);

        buildingFile.delete();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void cancel() {
        if(buildingTask != null) {
            buildingTask.cancel();
        }

        if (mInterfaceBuilding != null) {
            mInterfaceBuilding.onBuildingResult(CANCELED);
            mInterfaceBuilding = null;
        }
    }

    public void setBuildingListener(BuildingListener listener) {
        mInterfaceBuilding = listener;
    }

    public interface BuildingListener {
        void onBuildingResult(int result);

        void onBuildingProgress(int progress);
    }
}

