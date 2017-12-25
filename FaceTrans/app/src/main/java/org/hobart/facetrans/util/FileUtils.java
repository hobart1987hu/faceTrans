package org.hobart.facetrans.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import org.hobart.facetrans.BuildConfig;
import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.model.Video;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");

    public static boolean isApkFile(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return false;
        }
        if (filePath.lastIndexOf(GlobalConfig.EXTEND_APK) > 0) {
            return true;
        }
        return false;
    }

    public static boolean isImageFile(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return false;
        }
        if (filePath.lastIndexOf(GlobalConfig.EXTEND_JPG) > 0 || filePath.lastIndexOf(GlobalConfig.EXTEND_JPEG) > 0) {
            return true;
        }
        return false;
    }


    public static boolean isMusicFile(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return false;
        }
        if (filePath.lastIndexOf(GlobalConfig.EXTEND_MP3) > 0) {
            return true;
        }
        return false;
    }

    public static boolean isVideoFile(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return false;
        }
        if (filePath.lastIndexOf(GlobalConfig.EXTEND_MP4) > 0) {
            return true;
        }
        return false;
    }

    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static String getFileSize(long size) {
        if (size < 0) {
            return "0B";
        }
        double value = 0f;
        if ((size / 1024) < 1) {
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {
            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {
            value = (size * 100l / (1024l * 1024l * 1024l)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    public static String formatVideoTime(long timeMs) {

        StringBuilder mFormatBuilder = new StringBuilder();

        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;


        long minutes = (totalSeconds / 60) % 60;


        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public synchronized static String getWebTransferImage(String imageUrl, FTType ftType) {

        FileOutputStream outputStream = null;

        File file = null;
        try {
            long ftFileId = 0;
            if (!TextUtils.isEmpty(imageUrl)) {
                try {
                    ftFileId = Long.parseLong(imageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FTFile ftFile = FTFileManager.getInstance().getFTFile(ftFileId);

            if (null == ftFile) {
                if (ftType == FTType.APK) {
                    file = getCurrentApkIcon(file, outputStream);
                } else if (ftType == FTType.MUSIC) {
                    file = getDefaultMusicPath(file, outputStream);
                } else if (ftType == FTType.VIDEO) {
                    file = getDefaultVideoPath(file, outputStream);
                }
            } else {

                if (ftType == FTType.APK) {

                    String savePath = GlobalConfig.getApkIconDirectory() + File.separator + AndroidUtils.getApkPkgName(ftFile.getFilePath()) + ".png";

                    if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

                    file = new File(savePath);

                    if (!file.exists()) {

                        file.createNewFile();

                        Drawable drawable = AndroidUtils.getApkIcon(ftFile.getFilePath());

                        if (null == drawable) {
                            getCurrentApkIcon(file, outputStream);
                        } else {
                            Bitmap bitmap = drawableToBitmap(drawable);
                            outputStream = new FileOutputStream(file);
                            compressBitmapToSdcard(bitmap, outputStream);
                        }
                    }
                } else if (ftType == FTType.MUSIC) {

                    Music music = (Music) ftFile;

                    String savePath = GlobalConfig.getMusicIconDirectory() + File.separator + music.getName() + ".png";

                    if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

                    file = new File(savePath);

                    if (!file.exists()) {

                        file.createNewFile();

                        Bitmap bitmap = getMusicThumbnail(music.getFilePath());

                        if (null == bitmap) {
                            getCurrentApkIcon(file, outputStream);
                        } else {
                            outputStream = new FileOutputStream(file);
                            compressBitmapToSdcard(bitmap, outputStream);
                        }
                    }
                } else if (ftType == FTType.VIDEO) {

                    Video video = (Video) ftFile;

                    String savePath = GlobalConfig.getVideoIconDirectory() + File.separator + video.getName() + ".png";

                    if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

                    file = new File(savePath);

                    if (!file.exists()) {

                        file.createNewFile();

                        Bitmap bitmap = null;
                        try {
                            bitmap = ScreenshotUtils.createVideoThumbnail(ftFile.getFilePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (null == bitmap) {
                            getCurrentApkIcon(file, outputStream);
                        } else {
                            outputStream = new FileOutputStream(file);
                            compressBitmapToSdcard(bitmap, outputStream);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    private static Bitmap getMusicThumbnail(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] embedPic = retriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }

    private synchronized static File getCurrentApkIcon(File file, FileOutputStream outputStream) {

        try {
            Bitmap appIconBitmap = null;

            Context context = FaceTransApplication.getFaceTransApplicationContext();

            String savePath = GlobalConfig.getApkIconDirectory() + File.separator + context.getApplicationInfo().packageName + ".png";

            if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

            file = new File(savePath);

            if (file.exists()) return file;

            file.createNewFile();

            Drawable appIconDrawable = context.getApplicationInfo().loadIcon(context.getPackageManager());

            appIconBitmap = drawableToBitmap(appIconDrawable);

            outputStream = new FileOutputStream(file);

            compressBitmapToSdcard(appIconBitmap, outputStream);

            if (appIconBitmap != null) {

                appIconBitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private synchronized static File getDefaultMusicPath(File file, FileOutputStream outputStream) {

        try {
            Bitmap musicBitmap = null;

            Context context = FaceTransApplication.getFaceTransApplicationContext();

            String savePath = GlobalConfig.getMusicIconDirectory() + File.separator + "icon_music_default" + ".png";

            if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

            file = new File(savePath);

            if (file.exists()) return file;

            file.createNewFile();

            Drawable musicDrawable = context.getResources().getDrawable(R.mipmap.icon_music_default);

            musicBitmap = drawableToBitmap(musicDrawable);

            outputStream = new FileOutputStream(file);

            compressBitmapToSdcard(musicBitmap, outputStream);

            if (musicBitmap != null) {

                musicBitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private synchronized static File getDefaultVideoPath(File file, FileOutputStream outputStream) {
        try {
            Bitmap videoBitmap = null;

            Context context = FaceTransApplication.getFaceTransApplicationContext();

            String savePath = GlobalConfig.getMusicIconDirectory() + File.separator + "icon_video_default" + ".png";

            if (!FileUtils.isFolderExist(savePath)) FileUtils.makeDirs(savePath);

            file = new File(savePath);

            if (file.exists()) return file;

            file.createNewFile();

            Drawable videoDrawable = context.getResources().getDrawable(R.mipmap.icon_video_default);

            videoBitmap = drawableToBitmap(videoDrawable);

            outputStream = new FileOutputStream(file);

            compressBitmapToSdcard(videoBitmap, outputStream);

            if (videoBitmap != null) {

                videoBitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private static void compressBitmapToSdcard(Bitmap bitmap, FileOutputStream outputStream) {
        if (null == bitmap || null == outputStream) return;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    }


    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }


    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return delete(file);
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                delete(f);
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return delete(file);
    }

    public static boolean delete(File file) {
        if (file.isFile()) {
            File f = new File(file.getParent() + "/" + System.currentTimeMillis());
            file.renameTo(f);
            try {
                return f.delete();
//                FileUtils.deleteQuietly(f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                try {
                    File f = new File(file.getParent() + File.separator + System.currentTimeMillis());
                    file.renameTo(f);
                    return f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            File f = new File(file.getParent() + File.separator + System.currentTimeMillis());
            file.renameTo(f);
            try {
                return f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    private static final int BUFF_SIZE = 1024 * 10; // 10K

    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE * 100));
        for (File resFile : resFileList) {
            zipFile(resFile, zipOutputStream, "");
        }
        zipOutputStream.close();
    }

    private static void zipFile(File resFile, ZipOutputStream zipOutputStream, String rootPath)
            throws IOException {
        rootPath = rootPath + (rootPath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
        rootPath = new String(rootPath.getBytes("8859_1"), "utf-8");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipOutputStream, rootPath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE * 100);
            zipOutputStream.putNextEntry(new ZipEntry(rootPath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, realLength);
            }
            in.close();
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        }
    }

    public static void upZipFile(File zipFile, String folderPath) throws IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            if (entry.isDirectory()) {
                String tmpStr = folderPath + File.separator + entry.getName();
                tmpStr = new String(tmpStr.getBytes("8859_1"), "UTF-8");
                File folder = new File(tmpStr);
                folder.mkdirs();
            } else {
                InputStream is = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("8859_1"), "UTF-8");
                File desFile = new File(str);
                if (desFile.exists()) {
                    File f = new File(desFile.getParent() + "/" + System.currentTimeMillis());
                    desFile.renameTo(f);
                    f.delete();
                }
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream os = new FileOutputStream(desFile);
                byte[] buffer = new byte[BUFF_SIZE];
                int realLength;

                while ((realLength = is.read(buffer)) > 0) {
                    os.write(buffer, 0, realLength);
                    os.flush();
                }
                is.close();
                os.close();
            }
        }
        zf.close();
    }

    public static void install(String apkFilePath) {
        try {
            Context context = FaceTransApplication.getFaceTransApplicationContext();
            File file = new File(apkFilePath);

            final boolean isExists  = file.exists();

            if (!isExists) {
                return;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLongToast("无法安装apk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playMusic(String musicPath) {

        try {
            File file = new File(musicPath);
            if (!file.exists()) {
                return;
            }
            Context context = FaceTransApplication.getFaceTransApplicationContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(contentUri, "audio/*");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playVideo(String videoPath) {

        try {
            File file = new File(videoPath);
            if (!file.exists()) {
                return;
            }
            Context context = FaceTransApplication.getFaceTransApplicationContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(contentUri, "video/*");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showImage(String imagePath) {

        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                return;
            }
            Context context = FaceTransApplication.getFaceTransApplicationContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(contentUri, "image/*");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
