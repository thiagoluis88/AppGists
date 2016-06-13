package br.com.thiagoluis.appgists.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GistDetail implements Parcelable {
    private String gistId;
    private Map<String, GistFile> files;
    private String description;
    private List forks;
    private int comments;

    public String getGistId() {
        return gistId;
    }

    public void setGistId(String gistId) {
        this.gistId = gistId;
    }

    public Map<String, GistFile> getFiles() {
        return files;
    }

    public void setFiles(Map<String, GistFile> files) {
        this.files = files;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List getForks() {
        return forks;
    }

    public void setForks(List forks) {
        this.forks = forks;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gistId);
        dest.writeInt(this.files.size());
        for (Map.Entry<String, GistFile> entry : this.files.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeString(this.description);
        dest.writeInt(this.comments);
    }

    public GistDetail() {
    }

    protected GistDetail(Parcel in) {
        this.gistId = in.readString();
        int filesSize = in.readInt();
        this.files = new HashMap<String, GistFile>(filesSize);
        for (int i = 0; i < filesSize; i++) {
            String key = in.readString();
            GistFile value = in.readParcelable(GistFile.class.getClassLoader());
            this.files.put(key, value);
        }
        this.description = in.readString();
        this.comments = in.readInt();
    }

    public static final Parcelable.Creator<GistDetail> CREATOR = new Parcelable.Creator<GistDetail>() {
        @Override
        public GistDetail createFromParcel(Parcel source) {
            return new GistDetail(source);
        }

        @Override
        public GistDetail[] newArray(int size) {
            return new GistDetail[size];
        }
    };
}
