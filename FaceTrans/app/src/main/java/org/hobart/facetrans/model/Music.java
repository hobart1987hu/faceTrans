package org.hobart.facetrans.model;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class Music extends FTModel {

    /**
     * 歌曲名称
     */
    private String musicName;

    /**
     * 是否已经选择该首音乐
     */
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    @Override
    public String toString() {
        return "Music{" +
                "musicName='" + musicName + '\'' +
                '}';
    }
}
