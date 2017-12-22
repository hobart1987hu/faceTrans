package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.SocketConnectEvent;
import org.hobart.facetrans.event.SocketTransferEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.transfer.TransferProtocol;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;
import org.hobart.facetrans.ui.adapter.ReceiveFileListAdapter;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 接收文件界面
 * Created by huzeyin on 2017/11/28.
 */

public class ReceiveFileActivity extends BaseTitleBarActivity {

    private List<TransferModel> mReceiveFileLists = Collections.synchronizedList(new ArrayList<TransferModel>());

    private ReceiveFileListAdapter mAdapter;

    @Bind(R.id.recycleView)
    RecyclerView recycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setCenterText("接收文件");

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReceiveFileListAdapter(this, mReceiveFileLists, new OnRecyclerViewClickListener.SimpleOnRecyclerViewClickListener() {
            @Override
            public void onItemClick(View container, View view, int position) {
                synchronized (mReceiveFileLists) {
                    TransferModel model = mReceiveFileLists.get(position);
                    if (model.transferStatus == TransferStatus.FINISH) {
                        switch (model.type) {
                            case TransferModel.TYPE_APK:
                                FileUtils.install(model.savePath);
                                break;
                            case TransferModel.TYPE_MUSIC:
                                FileUtils.playMusic(model.savePath);
                                break;
                            case TransferModel.TYPE_VIDEO:
                                FileUtils.playVideo(model.savePath);
                                break;
                            case TransferModel.TYPE_IMAGE:
                                FileUtils.showImage(model.savePath);
                                break;
                        }
                    }
                }
            }
        });
        recycleView.setAdapter(mAdapter);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketDisconnect(SocketConnectEvent event) {
        if (null == event) return;
        if (event.status == SocketConnectEvent.DIS_CONNECTED) {
            ToastUtils.showLongToast("已和发送端断开连接!");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketEventListener(SocketTransferEvent event) {

        if (null == event) {
            return;
        }
        if (event.connectStatus == SocketTransferEvent.SOCKET_CONNECT_FAILURE) {
            ToastUtils.showLongToast("网络异常!");
            return;
        }

        if (event.type == TransferProtocol.TYPE_DISCONNECT) {
            ToastUtils.showLongToast("已和发送端断开连接!");
            return;
        } else if (event.type == TransferProtocol.TYPE_DATA_TRANSFER) {
            final int type = event.transferData.type;
            switch (type) {
                case TransferModel.TYPE_FILE:
                case TransferModel.TYPE_APK:
                case TransferModel.TYPE_IMAGE:
                case TransferModel.TYPE_MUSIC:
                case TransferModel.TYPE_VIDEO:
                    if (event.transferData.mode == TransferModel.OPERATION_MODE_RECEIVER) {
                        updateAdapter(event);
                        if (event.transferData.transferStatus == TransferStatus.TRANSFER_SUCCESS) {
                            updateAdapterByStatus(event, TransferStatus.FINISH);
                        }
                    }
                    break;
                case TransferModel.TYPE_TRANSFER_DATA_LIST:
                    if (event.transferData.mode == TransferModel.OPERATION_MODE_RECEIVER
                            && event.transferData.transferStatus == TransferStatus.TRANSFER_SUCCESS) {
                        setAdapterData(event.transferData.content);
                    }
                    break;
            }
        }
    }

    private void updateAdapter(SocketTransferEvent event) {

        updateAdapterByStatus(event, event.transferData.transferStatus);
    }

    private int updateAdapterByStatus(SocketTransferEvent event, int status) {
        int position = -1;
        synchronized (mReceiveFileLists) {
            final int size = mReceiveFileLists.size();
            for (int i = 0; i < size; i++) {
                TransferModel model = mReceiveFileLists.get(i);
                if (TextUtils.equals(model.id, event.transferData.id)) {
                    model.transferStatus = status;
                    model.progress = event.transferData.progress;
                    model.savePath = model.fileIcon = event.transferData.savePath;
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            mAdapter.notifyItemChanged(position);
        }
        return position;
    }

    private void setAdapterData(String json) {
        Gson gson = new Gson();
        List<TransferModel> retList = gson.fromJson(json,
                new TypeToken<List<TransferModel>>() {
                }.getType());
        mReceiveFileLists.addAll(retList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        clearAll();
        super.onDestroy();
    }

    private void clearAll() {
        EventBus.getDefault().unregister(ReceiveFileActivity.this);
        ApWifiHelper.getInstance().closeWifiAp();
        ApWifiHelper.getInstance().disableCurrentNetWork();
        WifiHelper.getInstance().openWifi();
        IntentUtils.stopServerReceiverService(getApplicationContext());
    }
}
