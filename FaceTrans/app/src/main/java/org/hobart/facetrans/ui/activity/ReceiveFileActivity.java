package org.hobart.facetrans.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.SocketEvent;
import org.hobart.facetrans.event.SocketFileEvent;
import org.hobart.facetrans.event.SocketTextEvent;
import org.hobart.facetrans.event.UnZipFTFileEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketExecutorService;
import org.hobart.facetrans.socket.service.ServerReceiverService;
import org.hobart.facetrans.socket.transfer.TransferReceiver;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.socket.transfer.thread.UnZipFTFileRunnable;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.adapter.ReceiveFileListAdapter;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 接收文件界面
 * Created by huzeyin on 2017/11/28.
 */

public class ReceiveFileActivity extends BaseActivity {

    //TODO:等解压缩结束了，回调给发送端，开始发送下一个文件了

    private Socket mSocket;

    private List<TransferModel> mReceiveFileLists;

    private ReceiveFileListAdapter mAdapter;


    @Bind(R.id.recycleView)
    RecyclerView recycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        mReceiveFileLists = new ArrayList<>();
        mAdapter = new ReceiveFileListAdapter(this, mReceiveFileLists);
        recycleView.setAdapter(mAdapter);

        bindReceiveService();
    }

    private void bindReceiveService() {
        bindService(new Intent(this, ServerReceiverService.class), mConnection, ServerReceiverService.BIND_AUTO_CREATE);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSocket = ((ServerReceiverService.MyBinder) service).getService().getReceiverSocket();
            ToastUtils.showLongToast("接收文件Socket服务绑定 成功");
            readyReceiveFile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private TransferReceiver mReceiver;

    private void readyReceiveFile() {
        mReceiver = new TransferReceiver(mSocket);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketEventListener(SocketEvent event) {
        final int type = event.type;
        switch (type) {
            case SocketEvent.TYPE_FILE:
                //接收发送进度
                SocketFileEvent fileEvent = (SocketFileEvent) event;
                if (fileEvent.mode == SocketEvent.OPERATION_MODE_RECEIVER) {
                    updateAdapter(fileEvent);
                    if (fileEvent.status == TransferStatus.TRANSFER_SUCCESS) {
                        //解压缩文件
                        if (fileEvent.isZipFile) {
                            unZipFTFile(fileEvent);
                        } else {
                            int position = -1;
                            synchronized (mReceiveFileLists) {
                                final int size = mReceiveFileLists.size();
                                for (int i = 0; i < size; i++) {
                                    TransferModel model = mReceiveFileLists.get(i);
                                    if (TextUtils.equals(model.getId(), fileEvent.id)) {
                                        model.setTransferStatus(TransferStatus.UNZIP);
                                        mReceiveFileLists.set(i, model);
                                        position = i;
                                        break;
                                    }
                                }
                            }
                            if (position == -1) return;
                            TransferModel model = mReceiveFileLists.get(position);
                            model.setTransferStatus(TransferStatus.FINISH);
                            mReceiveFileLists.set(position, model);
                            mAdapter.notifyItemChanged(position);
                        }
                    }
                }
                break;
            case SocketEvent.TYPE_LIST:
                //接收发送list列表完成
                SocketTextEvent textEvent = (SocketTextEvent) event;
                if (textEvent.mode == SocketEvent.OPERATION_MODE_RECEIVER
                        && textEvent.status == TransferStatus.TRANSFER_SUCCESS) {
                    setAdapterData(textEvent.content);
                }
                break;
            case SocketEvent.TYPE_HEART_BEAT:
                //接收发送心跳 do nothing
                break;
        }
    }

    /**
     * 解压回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void unZipFTFileCallback(UnZipFTFileEvent event) {

        final int index = event.index;
        TransferModel model = mReceiveFileLists.get(index);
        if (event.status == UnZipFTFileEvent.UNZIP_SUCCESS) {
            model.setTransferStatus(TransferStatus.FINISH);
        } else {
            model.setTransferStatus(TransferStatus.FAILED);
        }
        mReceiveFileLists.set(index, model);
        mAdapter.notifyItemChanged(index);
    }

    private void unZipFTFile(SocketFileEvent event) {
        int position = -1;
        synchronized (mReceiveFileLists) {
            final int size = mReceiveFileLists.size();
            for (int i = 0; i < size; i++) {
                TransferModel model = mReceiveFileLists.get(i);
                if (TextUtils.equals(model.getId(), event.id)) {
                    model.setTransferStatus(TransferStatus.UNZIP);
                    mReceiveFileLists.set(i, model);
                    position = i;
                    break;
                }
            }
        }
        if (position == -1) return;
        mAdapter.notifyItemChanged(position);
        SocketExecutorService.getExecute().execute(new UnZipFTFileRunnable(position, event.fileSavePath));
    }

    private void updateAdapter(SocketFileEvent event) {
        int position = -1;
        synchronized (mReceiveFileLists) {
            final int size = mReceiveFileLists.size();
            for (int i = 0; i < size; i++) {
                TransferModel model = mReceiveFileLists.get(i);
                if (TextUtils.equals(model.getId(), event.id)) {
                    model.setTransferStatus(event.status);
                    model.setProgress(event.progress);
                    mReceiveFileLists.set(i, model);
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            mAdapter.notifyItemChanged(position);
        }
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
        super.onDestroy();
        ApWifiHelper.getInstance().closeWifiAp();
        WifiHelper.getInstance().openWifi();
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        if (null != mReceiver) mReceiver.release();
    }
}
