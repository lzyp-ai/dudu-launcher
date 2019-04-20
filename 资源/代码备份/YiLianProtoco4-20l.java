package com.wow.carlauncher.ex.plugin.fk.protocol;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wow.carlauncher.base.Shorts;
import com.wow.carlauncher.ex.plugin.console.ConsolePlugin;
import com.wow.carlauncher.ex.plugin.console.event.PConsoleEventCallState;
import com.wow.carlauncher.ex.plugin.fk.FKSimulateDoubleClick;
import com.wow.carlauncher.ex.plugin.fk.FangkongProtocol;
import com.wow.carlauncher.ex.plugin.fk.FangkongProtocolListener;
import com.wow.carlauncher.ex.plugin.music.MusicPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import static com.wow.carlauncher.common.CommonData.TAG;

/**
 * Created by 10124 on 2018/3/28.
 */

public class YiLianProtocol extends FangkongProtocol {
    public static final short LEFT_TOP_CLICK = -24316;//左上
    public static final short RIGHT_TOP_CLICK = -24319;//右上
    public static final short LEFT_BOTTOM_CLICK = -24312;//左下
    public static final short RIGHT_BOTTOM_CLICK = -24318;//右下
    public static final short CENTER_CLICK = -24304;//中间
    public static final short LEFT_TOP_LONG_CLICK = -23804;
    public static final short RIGHT_TOP_LONG_CLICK = -23807;
    public static final short LEFT_BOTTOM_LONG_CLICK = -23800;
    public static final short RIGHT_BOTTOM_LONG_CLICK = -23806;
    public static final short CENTER_LONG_CLICK = -23792;



    private int moshi = 1;

    //标记是否是在打电话
    private boolean isCalling = false;

    private FKSimulateDoubleClick<Short> doubleClick;

    public YiLianProtocol(String address, Context context, FangkongProtocolListener changeModelCallBack) {
        super(address, context, changeModelCallBack);
        doubleClick = new FKSimulateDoubleClick<>();
        EventBus.getDefault().register(this);

        Log.d(TAG, "yilian protocol init");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(final PConsoleEventCallState event) {
        isCalling = event.isCalling();
    }

    @Override
    public void receiveMessage(byte[] message) {
        if (message != null && message.length == 2) {
            short cmd = Shorts.fromByteArray(message);
            if (isCalling) {
                switch (cmd) {
                    case LEFT_TOP_CLICK:
                        ConsolePlugin.self().decVolume();
                        break;
                    case RIGHT_TOP_CLICK:
                        ConsolePlugin.self().incVolume();
                        break;
                    case LEFT_BOTTOM_CLICK:
                        ConsolePlugin.self().callAnswer();
                        break;
                    case RIGHT_BOTTOM_CLICK:
                        ConsolePlugin.self().callHangup();
                        break;
                    case CENTER_CLICK:
                        Intent home = new Intent(Intent.ACTION_MAIN);
                        home.addCategory(Intent.CATEGORY_HOME);
                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(home);
                        break;
                    case LEFT_TOP_LONG_CLICK:
                        MusicPlugin.self().pre();
                        break;
                    case RIGHT_TOP_LONG_CLICK:
                        MusicPlugin.self().next();
                        break;
                    case LEFT_BOTTOM_LONG_CLICK:
                        ConsolePlugin.self().callAnswer();
                        break;
                    case RIGHT_BOTTOM_LONG_CLICK:
                        ConsolePlugin.self().callHangup();
                        break;
                    case CENTER_LONG_CLICK:
                        isCalling = false;
                        break;
                }
            } else {
                if (moshi == 1) {
                    switch (cmd) {
                        case LEFT_TOP_CLICK:
                            ConsolePlugin.self().decVolume();
                            break;
                        case RIGHT_TOP_CLICK:
                            ConsolePlugin.self().incVolume();
                            break;
                        case LEFT_BOTTOM_CLICK:
                            ConsolePlugin.self().mute();
                            break;
                        case RIGHT_BOTTOM_CLICK:
                            setBtnRightBottomClick();
                            break;
                        case CENTER_CLICK:
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.addCategory(Intent.CATEGORY_HOME);
                            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(home);
                            break;
                        case LEFT_TOP_LONG_CLICK:
                            MusicPlugin.self().pre();
                            break;
                        case RIGHT_TOP_LONG_CLICK:
                            MusicPlugin.self().next();
                            break;
                        case LEFT_BOTTOM_LONG_CLICK:

                            break;
                        case RIGHT_BOTTOM_LONG_CLICK:
                            ConsolePlugin.self().callHangup();
                            break;
                        case CENTER_LONG_CLICK:

                            break;
                    }
                } else {
                    switch (cmd) {
                        case LEFT_TOP_CLICK:
                            MusicPlugin.self().pre();
                            break;
                        case RIGHT_TOP_CLICK:
                            MusicPlugin.self().next();
                            break;
                        case LEFT_BOTTOM_CLICK:
                            ConsolePlugin.self().mute();
                            break;
                        case RIGHT_BOTTOM_CLICK:
                            setBtnRightBottomClick();
                            break;
                        case CENTER_CLICK:
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.addCategory(Intent.CATEGORY_HOME);
                            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(home);
                            break;
                        case LEFT_TOP_LONG_CLICK:
                            break;
                        case RIGHT_TOP_LONG_CLICK:
                            break;
                        case LEFT_BOTTOM_LONG_CLICK:
                            break;
                        case RIGHT_BOTTOM_LONG_CLICK:
                            break;
                        case CENTER_LONG_CLICK:
                            break;
                    }
                }
            }
        }
    }

    private void setBtnRightBottomClick() {
        doubleClick.action(RIGHT_BOTTOM_CLICK, new Runnable() {
            @Override
            public void run() {
                modelSwitch(false);
            }
        }, new Runnable() {
            @Override
            public void run() {
                modelSwitch(true);
            }
        });
    }


    private int oldmodel = -1;

    private void modelSwitch(boolean dclick) {
        Log.d(TAG, "modelSwitch: " + dclick);
        if (dclick) {
            if (moshi == 2 || moshi == 1) {
                oldmodel = moshi;
                moshi = 3;
            } else {
                moshi = oldmodel;
            }
        } else {
            Log.d(TAG, "modelSwitch: " + moshi);
            if (moshi == 3) {
                moshi = oldmodel;
            } else {
                if (moshi == 2) {
                    moshi = 1;
                } else {
                    moshi = 2;
                }
            }
        }
    }

    public UUID getService() {
        return UUID.fromString("0000474d-0000-1000-8000-00805f9b34fb");
    }

    public UUID getCharacter() {
        return UUID.fromString("00004b59-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "yilian protocol destroy");
    }

    public String getModelName() {
        return "模式" + moshi;
    }
}
