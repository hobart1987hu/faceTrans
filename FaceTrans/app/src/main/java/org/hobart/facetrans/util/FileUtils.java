package org.hobart.facetrans.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");





    public static <T extends FTFile> List<T> getSpecificTypeFiles(String[] extension) {

        List<T> fileInfoList = new ArrayList<>();

        Uri fileUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.PARENT,
        };

        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }

        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = FaceTransApplication.getApp().getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    T fileInfo = (T) new FTFile();
                    fileInfo.setFilePath(data);
                    long size = 0;
                    try {
                        File file = new File(data);
                        size = file.length();
                        fileInfo.setSize(size);
                    } catch (Exception e) {

                    }
                    fileInfoList.add(fileInfo);
                } catch (Exception e) {
                }
            }
        }
        return fileInfoList;
    }


    public static <T extends FTFile> List<T> getDetailFTFiles(List<T> fileInfoList, FTType type) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }

        for (FTFile fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setName(getFileName(fileInfo.getFilePath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getSize()));
                if (type == FTType.APK) {
                    fileInfo.setBitmap(drawableToBitmap(getApkThumbnail(fileInfo.getFilePath())));
                } else if (type == FTType.VIDEO) {
                    fileInfo.setBitmap(getScreenshotBitmap(fileInfo.getFilePath(), FTType.VIDEO));
                } else if (type == FTType.MUSIC) {

                } else if (type == FTType.IMAGE) {

                }
                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }

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

    private static String getFileName(String filePath) {
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

    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    private static Bitmap getScreenshotBitmap(String filePath, FTType type) {
        Bitmap bitmap = null;
        if (type == FTType.APK) {
            Drawable drawable = getApkThumbnail(filePath);
            if (drawable != null) {
                bitmap = drawableToBitmap(drawable);
            } else {
                bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.ic_launcher);
            }
            return bitmap;
        } else if (type == FTType.IMAGE) {
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)));
            } catch (FileNotFoundException e) {
                bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.ic_launcher);
            }
            bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
            return bitmap;
        } else if (type == FTType.MUSIC) {
            bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.ic_launcher);
            bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
            return bitmap;
        } else if (type == FTType.VIDEO) {
            try {
                bitmap = ScreenshotUtils.createVideoThumbnail(filePath);
            } catch (Exception e) {
                bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.ic_launcher);
            }
            bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
            return bitmap;
        }
        return bitmap;
    }

    private static Drawable getApkThumbnail(String apk_path) {

        try {
            PackageManager pm = FaceTransApplication.getApp().getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = apk_path;
            appInfo.publicSourceDir = apk_path;
            if (appInfo != null) {
                Drawable apk_icon = appInfo.loadIcon(pm);
                return apk_icon;
            }
        } catch (Exception e) {

        }

        return null;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
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
}
