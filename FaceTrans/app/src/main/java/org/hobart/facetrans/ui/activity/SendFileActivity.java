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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.event.SocketEvent;
import org.hobart.facetrans.event.SocketFileEvent;
import org.hobart.facetrans.event.SocketTextEvent;
import org.hobart.facetrans.event.ZipFTFileEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketExecutorService;
import org.hobart.facetrans.socket.service.SocketSenderService;
import org.hobart.facetrans.socket.transfer.SocketTransferQueue;
import org.hobart.facetrans.socket.transfer.TransferSender;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.socket.transfer.thread.DeleteTransFileRunnable;
import org.hobart.facetrans.socket.transfer.thread.ZipFTFileRunnable;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.adapter.SenderFileListAdapter;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发送文件界面
 * Created by huzeyin on 2017/11/28.
 */

public class SendFileActivity extends BaseActivity {

    private AtomicLong mAutoCreateTransferId;

    private Socket mSocket;

    private SenderFileListAdapter mAdapter;

    private List<TransferModel> mSendFileLists = Collections.synchronizedList(new ArrayList<TransferModel>());

    @Bind(R.id.recycleView)
    RecyclerView recycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();

        bindReceiveService();
    }

    private void init() {
        mAutoCreateTransferId = new AtomicLong(2);
        Map<String, FTFile> map = FTFileManager.getInstance().getFTFiles();
        Iterator<FTFile> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            FTFile ftFile = iterator.next();
            TransferModel model = new TransferModel();
            model.setFileName(ftFile.getName());
            model.setId("" + mAutoCreateTransferId.incrementAndGet());
            model.setSize("" + ftFile.getSize());
            model.setTransferStatus(TransferStatus.WAITING);
            model.setFilePath(ftFile.getFilePath());
            mSendFileLists.add(model);
        }
        mAdapter = new SenderFileListAdapter(this, mSendFileLists);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(mAdapter);
    }

    private void bindReceiveService() {
        bindService(new Intent(this, SocketSenderService.class), mConnection, SocketSenderService.BIND_AUTO_CREATE);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSocket = ((SocketSenderService.MyBinder) service).getService().getSenderSocket();
            startSendFile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private TransferSender mSender;

    private void startSendFile() {
        mSender = new TransferSender(mSocket);
        mSender.startSend();
        SocketTransferQueue.getInstance().sendFTFileList(mSendFileLists);
    }

    /**
     * socket 数据传输监听
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketEventListener(SocketEvent event) {
        final int type = event.type;
        switch (type) {
            case SocketEvent.TYPE_FILE:
                //接收发送进度
                SocketFileEvent fileEvent = (SocketFileEvent) event;
                if (fileEvent.mode == SocketEvent.OPERATION_MODE_SEND) {
                    updateAdapter(fileEvent);
                    if (fileEvent.status == TransferStatus.TRANSFER_SUCCESS) {
                        sendSingleFTFile();
                        if (fileEvent.isZipFile)
                            SocketExecutorService.getExecute().execute(new DeleteTransFileRunnable(fileEvent.fileSavePath));
                    }
                }
                break;
            case SocketEvent.TYPE_LIST:
                //接收发送list列表完成
                SocketTextEvent textEvent = (SocketTextEvent) event;
                if (textEvent.mode == SocketEvent.OPERATION_MODE_SEND
                        && textEvent.status == TransferStatus.TRANSFER_SUCCESS) {
                    sendSingleFTFile();
                }
                break;
            case SocketEvent.TYPE_HEART_BEAT:
                //接收发送心跳 do nothing
                break;
        }
    }

    /**
     * 文件压缩回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZipFTFileCallback(ZipFTFileEvent event) {

        TransferModel transferModel = mSendFileLists.get(event.index);
        if (event.status == ZipFTFileEvent.ZIP_SUCCESS) {
            transferModel.setTransferStatus(TransferStatus.TRANSFERING);
            SocketTransferQueue.getInstance().sendSingleFTFile(transferModel, event.zipFilePath, true);
        } else {
            transferModel.setTransferStatus(TransferStatus.FAILED);
        }
        mAdapter.notifyItemChanged(event.index);
    }

    private volatile int mSendPointer = -1;

    private void sendSingleFTFile() {
        mSendPointer++;
        final int size = mSendFileLists.size();

        if (mSendPointer >= size) {
            ToastUtils.showLongToast("恭喜你，所有文件全部传输完成!");
            return;
        }
        TransferModel transferModel = mSendFileLists.get(mSendPointer);
        //20M
//        if (Long.parseLong(transferModel.getSize()) > (1024 * 1024 * 20)) {
//            transferModel.setTransferStatus(TransferStatus.ZIP);
//            mAdapter.notifyItemChanged(mSendPointer);
//            SocketExecutorService.getExecute().execute(new ZipFTFileRunnable(mSendPointer, transferModel.getFilePath(), transferModel.getFileName()));
//        } else {
            transferModel.setTransferStatus(TransferStatus.TRANSFERING);
            mAdapter.notifyItemChanged(mSendPointer);
            SocketTransferQueue.getInstance().sendSingleFTFile(transferModel, transferModel.getFilePath(), false);
//        }
    }

    private void updateAdapter(SocketFileEvent event) {
        int position = -1;
        synchronized (mSendFileLists) {
            final int size = mSendFileLists.size();
            for (int i = 0; i < size; i++) {
                TransferModel model = mSendFileLists.get(i);
                if (TextUtils.equals(model.getId(), event.id)) {
                    model.setTransferStatus(event.status);
                    model.setProgress(event.progress);
                    mSendFileLists.set(i, model);
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
    }

    private void clearAll() {
        //发送文件，我方是连接Wi-Fi热点的，
        //断开当前的热点连接
        ApWifiHelper.getInstance().disableCurrentNetWork();
        //打开Wi-Fi
        WifiHelper.getInstance().openWifi();
        FTFileManager.getInstance().clear();
        EventBus.getDefault().post(new FTFilesChangedEvent());
        SocketTransferQueue.getInstance().clear();
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        IntentUtils.stopSocketSenderService(this);
        if (null != mSender) mSender.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteFile(GlobalConfig.getTransferZipDirectory());
            }
        }).start();
    }
}
