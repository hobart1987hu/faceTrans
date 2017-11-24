package org.hobart.facetrans.model;

/**
 * Created by huzeyin on 2017/11/15.
 */

public class FTModel {

    /**
     * 文件的大小
     */
    private long size;

    /**
     * 文件的名字
     */
    private String name;

    /**
     * 文件的路径
     */
    private String path;


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FTModel{" +
                "size=" + size +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
