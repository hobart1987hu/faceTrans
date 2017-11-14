/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.android.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.android.MessageIDs;
import com.zxing.android.view.ViewfinderResultPointCallback;

import java.util.Vector;

public final class CaptureActivityHandlerForFace extends Handler {

    private static final String TAG = CaptureActivityHandlerForFace.class.getSimpleName();

    private final CameraDecodeProvider mCameraDecodeProvider;
    private final DecodeThreadForFace decodeThread;
    private State state;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureActivityHandlerForFace(CameraDecodeProvider provider, Vector<BarcodeFormat> decodeFormats,
                                         String characterSet) {
        mCameraDecodeProvider = provider;
        decodeThread = new DecodeThreadForFace(mCameraDecodeProvider, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(mCameraDecodeProvider.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;
        mCameraDecodeProvider.getCameraManager().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case MessageIDs.auto_focus:
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (state == State.PREVIEW) {
                    mCameraDecodeProvider.getCameraManager().requestAutoFocus(this, MessageIDs.auto_focus);
                }
                break;
            case MessageIDs.restart_preview:
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            case MessageIDs.decode_succeeded:
                Log.d(TAG, "Got decode succeeded message");
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null :
                        (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
                mCameraDecodeProvider.handleDecode((Result) message.obj, barcode);
                break;
            case MessageIDs.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                //CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), MessageIDs.decode);
                mCameraDecodeProvider.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), MessageIDs.decode);
                break;
            case MessageIDs.return_scan_result:
                Log.d(TAG, "Got return scan result message");
                mCameraDecodeProvider.getView().setResult(Activity.RESULT_OK, (Intent) message.obj);
                mCameraDecodeProvider.getView().finish();
                break;
            case MessageIDs.launch_product_query:
                Log.d(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mCameraDecodeProvider.getView().startActivity(intent);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        mCameraDecodeProvider.getCameraManager().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), MessageIDs.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }
        removeMessages(MessageIDs.decode_succeeded);
        removeMessages(MessageIDs.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            mCameraDecodeProvider.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), MessageIDs.decode);
            mCameraDecodeProvider.getCameraManager().requestAutoFocus(this, MessageIDs.auto_focus);
            mCameraDecodeProvider.getViewfinderView().drawViewfinder();
        }
    }
}
