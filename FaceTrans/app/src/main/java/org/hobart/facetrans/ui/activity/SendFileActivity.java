package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.event.SocketTransferEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.transfer.TransferDataQueue;
import org.hobart.facetrans.socket.transfer.TransferProtocol;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;
import org.hobart.facetrans.ui.adapter.SenderFileListAdapter;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

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

public class SendFileActivity extends BaseTitleBarActivity {

    private AtomicLong mAutoCreateTransferId;

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

        setCenterText("发送文件");

        init();

        TransferDataQueue.getInstance().sendFTFileList(mSendFileLists);
    }

    private void init() {
        mAutoCreateTransferId = new AtomicLong(2);
        Map<Long, FTFile> map = FTFileManager.getInstance().getFTFiles();
        Iterator<FTFile> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            FTFile ftFile = iterator.next();
            TransferModel model = new TransferModel();
            model.fileName = ftFile.getName();
            model.id = "" + mAutoCreateTransferId.incrementAndGet();
            model.fileSize = ftFile.getSize();
            model.size = "" + model.fileSize;
            model.fileIcon = ftFile.getFilePath();
            model.transferStatus = TransferStatus.WAITING;
            model.filePath = ftFile.getFilePath();
            model.type = TransferModel.convertFileType(ftFile.getFileType());
            mSendFileLists.add(model);
        }
        mAdapter = new SenderFileListAdapter(this, mSendFileLists);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(mAdapter);
    }

    /**
     * socket 数据传输监听
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketEventListener(SocketTransferEvent event) {

        if (null == event) return;

        if (event.connectStatus == SocketTransferEvent.SOCKET_CONNECT_FAILURE) {
            ToastUtils.showLongToast("网络异常!");
            return;
        }
        if (event.type == TransferProtocol.TYPE_DISCONNECT) {
            ToastUtils.showLongToast("已和接收端断开连接!");
            return;
        } else if (event.type == TransferProtocol.TYPE_DATA_TRANSFER) {
            final int type = event.transferData.type;
            switch (type) {
                case TransferModel.TYPE_FILE:
                case TransferModel.TYPE_APK:
                case TransferModel.TYPE_IMAGE:
                case TransferModel.TYPE_MUSIC:
                case TransferModel.TYPE_VIDEO:
                    if (event.transferData.mode == TransferModel.OPERATION_MODE_SEND) {
                        updateAdapter(event);
                        if (event.transferData.transferStatus == TransferStatus.TRANSFER_SUCCESS) {
                            sendSingleFTFile();
                        }
                    }
                    break;
                case TransferModel.TYPE_TRANSFER_DATA_LIST:
                    if (event.transferData.mode == TransferModel.OPERATION_MODE_SEND
                            && event.transferData.transferStatus == TransferStatus.TRANSFER_SUCCESS) {
                        sendSingleFTFile();
                    }
                    break;
                case TransferModel.TYPE_FOLDER:
                    break;
            }
        }
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
        transferModel.transferStatus = TransferStatus.TRANSFERING;
        mAdapter.notifyItemChanged(mSendPointer);
        TransferDataQueue.getInstance().sendTransferData(transferModel);
    }

    private void updateAdapter(SocketTransferEvent event) {
        int position = -1;
        synchronized (mSendFileLists) {
            final int size = mSendFileLists.size();
            for (int i = 0; i < size; i++) {
                TransferModel model = mSendFileLists.get(i);
                if (TextUtils.equals(model.id, event.transferData.id)) {
                    model.transferStatus = event.transferData.transferStatus;
                    model.progress = event.transferData.progress;
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
        clearAll();
        super.onDestroy();
    }

    private void clearAll() {
        FTFileManager.getInstance().clear();
        EventBus.getDefault().post(new FTFilesChangedEvent());
        EventBus.getDefault().unregister(SendFileActivity.this);
        ApWifiHelper.getInstance().disableCurrentNetWork();
        WifiHelper.getInstance().openWifi();
        TransferDataQueue.getInstance().clear();
        IntentUtils.stopSocketSenderService(getApplicationContext());
    }
}
