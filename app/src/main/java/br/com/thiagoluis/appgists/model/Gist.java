package br.com.thiagoluis.appgists.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Gist implements Parcelable {
    @SerializedName("id")
    private String gistId;
    private String url;
    @SerializedName("public")
    private boolean isPublic;
    @SerializedName("created_at")
    private Date createdAt;
    private Owner owner;
    private Map<String, GistFile> files;

    //Usado quando populado pelo SQLite
    @Expose(deserialize = false, serialize = false)
    private String language;
    @Expose(deserialize = false, serialize = false)
    private String gistType;

    public String getGistId() {
        return gistId;
    }

    public void setGistId(String gistId) {
        this.gistId = gistId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
//
    public Map<String, GistFile> getFiles() {
        return files;
    }

    public void setFiles(Map<String, GistFile> files) {
        this.files = files;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGistType() {
        return gistType;
    }

    public void setGistType(String gistType) {
        this.gistType = gistType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gistId);
        dest.writeString(this.url);
        dest.writeByte(this.isPublic ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeParcelable(this.owner, flags);
        dest.writeInt(this.files.size());
        for (Map.Entry<String, GistFile> entry : this.files.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeString(this.language);
        dest.writeString(this.gistType);
    }

    public Gist() {
    }

    protected Gist(Parcel in) {
        this.gistId = in.readString();
        this.url = in.readString();
        this.isPublic = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.owner = in.readParcelable(Owner.class.getClassLoader());
        int filesSize = in.readInt();
        this.files = new HashMap<String, GistFile>(filesSize);
        for (int i = 0; i < filesSize; i++) {
            String key = in.readString();
            GistFile value = in.readParcelable(GistFile.class.getClassLoader());
            this.files.put(key, value);
        }
        this.language = in.readString();
        this.gistType = in.readString();
    }

    public static final Parcelable.Creator<Gist> CREATOR = new Parcelable.Creator<Gist>() {
        @Override
        public Gist createFromParcel(Parcel source) {
            return new Gist(source);
        }

        @Override
        public Gist[] newArray(int size) {
            return new Gist[size];
        }
    };
}
