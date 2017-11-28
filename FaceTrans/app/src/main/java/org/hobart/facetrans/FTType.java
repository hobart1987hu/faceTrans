package org.hobart.facetrans;

/**
 * 面传类型
 * Created by huzeyin on 2017/11/15.
 */
public enum FTType {
    TEXT(0),
    MUSIC(1),
    VIDEO(2),
    APK(3),
    FILE(4),
    IMAGE(5);

    private int value;

    private FTType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FTType getType(int type) {
        switch (type) {
            case 0:
                return FTType.TEXT;
            case 1:
                return FTType.MUSIC;
            case 2:
                return FTType.VIDEO;
            case 3:
                return FTType.APK;
            case 4:
                return FTType.FILE;
            case 5:
                return FTType.IMAGE;
        }
        return FTType.TEXT;
    }
}
