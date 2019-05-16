package com.wow.carlauncher.view.activity.set.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import com.wow.carlauncher.R;
import com.wow.carlauncher.common.CommonData;
import com.wow.carlauncher.common.LogEx;
import com.wow.carlauncher.common.TaskExecutor;
import com.wow.carlauncher.common.util.AppUtil;
import com.wow.carlauncher.common.util.SharedPreUtil;
import com.wow.carlauncher.common.view.SetView;
import com.wow.carlauncher.ex.manage.appInfo.AppInfoManage;
import com.wow.carlauncher.ex.manage.okHttp.ProgressResponseListener;
import com.wow.carlauncher.ex.manage.toast.ToastManage;
import com.wow.carlauncher.repertory.server.CommonService;
import com.wow.carlauncher.view.activity.AboutActivity;
import com.wow.carlauncher.view.activity.launcher.event.LDockLabelShowChangeEvent;
import com.wow.carlauncher.view.activity.set.SetActivity;
import com.wow.carlauncher.view.activity.set.SetBaseView;
import com.wow.carlauncher.view.activity.set.listener.SetAppMultipleSelectOnClickListener;
import com.wow.carlauncher.view.activity.set.listener.SetAppSingleSelectOnClickListener;
import com.wow.carlauncher.view.activity.set.listener.SetSwitchOnClickListener;
import com.wow.carlauncher.view.dialog.ProgressDialog;
import com.wow.carlauncher.view.event.CEventShowUsbMount;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 10124 on 2018/4/22.
 */

@SuppressLint("ViewConstructor")
public class SSystemView extends SetBaseView {
    public SSystemView(SetActivity activity) {
        super(activity);
    }

    @Override
    protected int getContent() {
        return R.layout.content_set_system;
    }

    @BindView(R.id.sv_sys_anquan)
    SetView sv_sys_anquan;

    @BindView(R.id.sv_sys_overlay)
    SetView sv_sys_overlay;

    @BindView(R.id.sv_sys_sdk)
    SetView sv_sys_sdk;

    @BindView(R.id.sv_about)
    SetView sv_about;

    @BindView(R.id.sv_apps_hides)
    SetView sv_apps_hides;

    @BindView(R.id.sv_launcher_show_dock_label)
    SetView sv_launcher_show_dock_label;

    @BindView(R.id.sv_key_listener)
    SetView sv_key_listener;

    @BindView(R.id.sv_open_log)
    SetView sv_open_log;

    @BindView(R.id.sv_update)
    SetView sv_update;

    @BindView(R.id.sv_show_mount)
    SetView sv_show_mount;


    private boolean showKey;
    private BroadcastReceiver nwdKeyTestReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if ("com.nwd.action.ACTION_KEY_VALUE".equals(paramIntent.getAction())) {
                int key = paramIntent.getByteExtra("extra_key_value", (byte) 0);
                ToastManage.self().show("NWD按键测试触发:" + key);
            }
        }
    };

    @Override
    protected void initView() {
        sv_show_mount.setOnValueChangeListener(new SetSwitchOnClickListener(CommonData.SDATA_SHOW_USB_MOUNT) {
            @Override
            public void newValue(boolean value) {
                SharedPreUtil.saveBoolean(CommonData.SDATA_SHOW_USB_MOUNT, value);
                EventBus.getDefault().post(new CEventShowUsbMount());
            }
        });
        sv_show_mount.setChecked(SharedPreUtil.getBoolean(CommonData.SDATA_SHOW_USB_MOUNT, false));

        sv_update.setOnClickListener(v -> {
            getActivity().showLoading("请求中");
            CommonService.getUpdate(2, (success, msg, appUpdate) -> {
                getActivity().hideLoading();
                if (success == 0) {
                    if (AppUtil.getLocalVersion(getContext()) < appUpdate.getVersion()) {
                        TaskExecutor.self().autoPost(() -> new AlertDialog.Builder(getContext()).setTitle("发现新版本")
                                .setNegativeButton("忽略", null)
                                .setPositiveButton("下载新版本", (dialog12, which) -> {
                                    loadDownloadApk(appUpdate.getUrl(), appUpdate.getVersion());
                                }).setMessage(appUpdate.getAbout()).show());
                    } else {
                        ToastManage.self().show("没有新版本");
                    }
                }
            });
        });

        sv_open_log.setOnValueChangeListener(new SetSwitchOnClickListener(CommonData.SDATA_LOG_OPEN) {
            @Override
            public void newValue(boolean value) {
                SharedPreUtil.saveBoolean(CommonData.SDATA_LOG_OPEN, value);
                LogEx.setSaveFile(value);
            }
        });
        sv_open_log.setChecked(SharedPreUtil.getBoolean(CommonData.SDATA_LOG_OPEN, true));

        sv_key_listener.setOnClickListener(v -> {
            showKey = true;
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("com.nwd.action.ACTION_KEY_VALUE");
            getContext().registerReceiver(nwdKeyTestReceiver, intentFilter2);

            new AlertDialog.Builder(getContext()).setTitle("开启").setOnDismissListener(dialog -> {
                showKey = false;
                getContext().unregisterReceiver(nwdKeyTestReceiver);
            }).setPositiveButton("关闭", null).show();
        });

        sv_apps_hides.setOnClickListener(new SetAppMultipleSelectOnClickListener(getContext()) {
            @Override
            public String getCurr() {
                return SharedPreUtil.getString(CommonData.SDATA_HIDE_APPS);
            }

            @Override
            public void onSelect(String t) {
                SharedPreUtil.saveString(CommonData.SDATA_HIDE_APPS, t);
                AppInfoManage.self().refreshShowApp();
            }
        });

        sv_launcher_show_dock_label.setOnValueChangeListener(new SetSwitchOnClickListener(CommonData.SDATA_LAUNCHER_DOCK_LABEL_SHOW) {
            @Override
            public void newValue(boolean value) {
                EventBus.getDefault().post(new LDockLabelShowChangeEvent(value));
            }
        });
        sv_launcher_show_dock_label.setChecked(SharedPreUtil.getBoolean(CommonData.SDATA_LAUNCHER_DOCK_LABEL_SHOW, true));

        sv_sys_overlay.setOnClickListener(new SetAppSingleSelectOnClickListener(getContext()) {
            @Override
            public String getCurr() {
                return null;
            }

            @Override
            public void onSelect(String t) {
                if (Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + t));
                    getActivity().startActivity(intent);
                } else {
                    ToastManage.self().show("这个功能是安卓6.0以上才有的");
                }
            }
        });

        sv_sys_anquan.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            getActivity().startActivity(intent);
        });

        sv_sys_sdk.setOnClickListener(v -> ToastManage.self().show("当前SDK版本是" + Build.VERSION.SDK_INT));

        sv_about.setOnClickListener(v -> getActivity().startActivity(new Intent(getContext(), AboutActivity.class)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (showKey) {
            ToastManage.self().show("系统按键触发 KEY_CODE:" + keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadDownloadApk(String url, int version) {
        final String filePath = Environment.getExternalStorageDirectory() + "/ddlauncher-V" + version + ".apk";
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("正在下载嘟嘟桌面新版本");
        Call call = CommonService.downFile(url, new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                progressDialog.setProgress((float) ((double) bytesRead / (double) contentLength));
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastManage.self().show("更新下载失败!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    InputStream inputStream = responseBody.byteStream();
                    /**
                     * 写入本地文件
                     */
                    File file = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri value = FileProvider.getUriForFile(getActivity(), "com.satsoftec.risense.fileprovider", new File(filePath));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(value,
                                "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(new File(filePath)),
                                "application/vnd.android.package-archive");
                    }
                    getActivity().startActivity(intent);
                } else {
                    ToastManage.self().show("更新下载失败!");
                }
                TaskExecutor.self().autoPost(progressDialog::dismiss);
            }
        });
        progressDialog.setOnDismissListener(dialog -> {
            if (!call.isCanceled()) {
                call.cancel();
            }
        });
        TaskExecutor.self().autoPost(progressDialog::show);
    }
}
