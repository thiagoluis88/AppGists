package br.com.thiagoluis.appgists.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GistFile implements Parcelable {
    @SerializedName("filename")
    private String fileName;
    private String type;
    private String content;
    private String language;
    private long size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.type);
        dest.writeString(this.content);
        dest.writeString(this.language);
        dest.writeLong(this.size);
    }

    public GistFile() {
    }

    protected GistFile(Parcel in) {
        this.fileName = in.readString();
        this.type = in.readString();
        this.content = in.readString();
        this.language = in.readString();
        this.size = in.readLong();
    }

    public static final Parcelable.Creator<GistFile> CREATOR = new Parcelable.Creator<GistFile>() {
        @Override
        public GistFile createFromParcel(Parcel source) {
            return new GistFile(source);
        }

        @Override
        public GistFile[] newArray(int size) {
            return new GistFile[size];
        }
    };
}
