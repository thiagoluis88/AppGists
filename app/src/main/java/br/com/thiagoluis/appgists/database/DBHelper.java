package br.com.thiagoluis.appgists.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.model.GistDetail;
import br.com.thiagoluis.appgists.model.GistFile;
import br.com.thiagoluis.appgists.model.Owner;

/**
 * http://www.tutorialspoint.com/android/android_sqlite_database.htm
 * http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * https://developer.android.com/training/basics/data-storage/databases.html
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AppGists.db";
    public static final String GIST_COLUMN_GIST_ID = "gistId";
    public static final String GIST_COLUMN_DESCRIPTION = "description";
    public static final String GIST_COLUMN_COMMENTS = "comments";
    public static final String GIST_COLUMN_URL = "url";
    public static final String GIST_COLUMN_CREATED_AT = "createdAt";
    public static final String GIST_COLUMN_LANGUAGE = "language";
    public static final String GIST_COLUMN_GIST_TYPE = "gistType";
    public static final String GIST_COLUMN_IS_PUBLIC = "isPublic";
    public static final String GIST_TYPE = "gistType";
    public static final String GIST_FILE_DETAIL_ID = "gistDetailsId";
    public static final String GIST_FILE_NAME = "fileName";
    public static final String GIST_FILE_SIZE = "size";
    public static final String GIST_FILE_TYPE = "type";
    public static final String GIST_FILE_CONTENT = "content";
    public static final String GIST_FILE_LANGUAGE = "language";
    public static final String OWNER_COLUMN_LOGIN = "login";
    public static final String OWNER_COLUMN_AVATAR_URL = "avatarUrl";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table Gist " +
                        "(gistId text primary key," +
                        "url text," +
                        "createdAt long," +
                        "language text," +
                        "gistType text, " +
                        "isPublic boolean)");

        db.execSQL(
                "create table Owner " +
                        "(id integer primary key autoincrement, " +
                        "gistId text," +
                        "login text," +
                        "avatarUrl text)");

        db.execSQL(
                "create table GistDetail " +
                        "(id integer primary key, " +
                        "gistId text," +
                        "description text," +
                        "comments integer)");

        db.execSQL(
                "create table GistFile " +
                        "(id integer primary key, " +
                        "gistDetailsId text," +
                        "fileName text," +
                        "size long," +
                        "type text," +
                        "content text," +
                        "language text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Gist");
        db.execSQL("DROP TABLE IF EXISTS GistDetail");
        db.execSQL("DROP TABLE IF EXISTS GistFile");
        db.execSQL("DROP TABLE IF EXISTS Owner");
        onCreate(db);
    }

    public boolean insertGists(List<Gist> gists) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Gist gist : gists) {
            insertGist(gist, db);
        }
        return true;
    }

    public boolean insertGistDetail(String gistId, GistDetail gistDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GIST_COLUMN_GIST_ID, gistId);
        contentValues.put(GIST_COLUMN_DESCRIPTION, gistDetail.getDescription());
        contentValues.put(GIST_COLUMN_COMMENTS, gistDetail.getComments());

        db.insert("GistDetail", null, contentValues);
        return true;
    }

    public void insertGistFiles(String gistDetailId, List<GistFile> gistFiles) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (GistFile gistFile : gistFiles) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GIST_FILE_DETAIL_ID, gistDetailId);
            contentValues.put(GIST_FILE_NAME, gistFile.getFileName());
            contentValues.put(GIST_FILE_CONTENT, gistFile.getContent());
            contentValues.put(GIST_FILE_SIZE, gistFile.getSize());
            contentValues.put(GIST_FILE_LANGUAGE, gistFile.getLanguage());
            contentValues.put(GIST_FILE_TYPE, gistFile.getType());

            db.insert("GistFile", null, contentValues);
        }
    }

    private void insertGist(Gist gist, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GIST_COLUMN_GIST_ID, gist.getGistId());
        contentValues.put(GIST_COLUMN_URL, gist.getUrl());
        contentValues.put(GIST_COLUMN_IS_PUBLIC, gist.isPublic());
        contentValues.put(GIST_COLUMN_CREATED_AT, gist.getCreatedAt().getTime());

        insertOwner(gist, db);
        insertLanguageAndType(gist, contentValues);
        insertGistFiles(gist.getGistId(), new ArrayList<>(gist.getFiles().values()));

        db.insert("Gist", null, contentValues);
    }

    private void insertOwner(Gist gist, SQLiteDatabase db) {
        if (gist.getOwner() != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GIST_COLUMN_GIST_ID, gist.getGistId());
            contentValues.put(OWNER_COLUMN_LOGIN, gist.getOwner().getLogin());
            contentValues.put(OWNER_COLUMN_AVATAR_URL, gist.getOwner().getAvatarUrl());

            db.insert("Owner", null, contentValues);
        }
    }

    public List<Gist> getAllGists(int page) {
        page = page * 30;

        List<Gist> gists = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Gist g\n" +
                " WHERE g.oid NOT IN ( SELECT oid FROM Gist\n" +
                "                   ORDER BY createdAt DESC LIMIT ?)\n" +
                " ORDER BY g.createdAt DESC LIMIT ?";
        Cursor res = db.rawQuery(sql, new String[]{String.valueOf(page), String.valueOf(30)});

        fillGist(res, gists);

        return gists;
    }

    public GistDetail getGistDetail(String gistId) {
        GistDetail gistDetail = new GistDetail();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * from GistDetail WHERE gistId = ?";
        Cursor res = db.rawQuery(sql, new String[]{gistId});

        fillGistDetail(res, gistDetail);

        return gistDetail;
    }

    private Owner getOwner(String gistId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Owner where gistId = ?", new String[]{gistId});
        if (res.moveToFirst()) {
            return fillOwner(res);
        }
        return null;
    }

    private Map<String, GistFile> getGistFiles(String gistDetailId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from GistFile where gistDetailsId = ?", new String[]{gistDetailId});
        if (res.moveToFirst()) {
            return fillGistsFiles(res);
        }
        return null;
    }

    private void fillGistDetail(Cursor res, GistDetail gistDetail) {
        res.moveToFirst();
        gistDetail.setDescription(res.getString(res.getColumnIndex(GIST_COLUMN_DESCRIPTION)));
        gistDetail.setComments(res.getInt(res.getColumnIndex(GIST_COLUMN_COMMENTS)));
        gistDetail.setGistId(res.getString(res.getColumnIndex(GIST_COLUMN_GIST_ID)));
        gistDetail.setFiles(getGistFiles(gistDetail.getGistId()));
    }

    private Map<String, GistFile> fillGistsFiles(Cursor res) {
        Map<String, GistFile> gistFiles = new HashMap<>();
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            GistFile gistFile = new GistFile();
            gistFile.setContent(res.getString(res.getColumnIndex(GIST_FILE_CONTENT)));
            gistFile.setFileName(res.getString(res.getColumnIndex(GIST_FILE_NAME)));
            gistFile.setLanguage(res.getString(res.getColumnIndex(GIST_COLUMN_LANGUAGE)));
            gistFile.setSize(res.getLong(res.getColumnIndex(GIST_FILE_SIZE)));
            gistFile.setType(res.getString(res.getColumnIndex(GIST_FILE_TYPE )));

            gistFiles.put(gistFile.getFileName(), gistFile);

            res.moveToNext();
        }

        return gistFiles;
    }

    private void fillGist(Cursor res, List<Gist> gists) {
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Gist gist = new Gist();
            gist.setGistId(res.getString(res.getColumnIndex(GIST_COLUMN_GIST_ID)));
            gist.setUrl(res.getString(res.getColumnIndex(GIST_COLUMN_URL)));
            gist.setLanguage(res.getString(res.getColumnIndex(GIST_COLUMN_LANGUAGE)));
            gist.setGistType(res.getString(res.getColumnIndex(GIST_TYPE)));
            gist.setCreatedAt(new Date(res.getLong(res.getColumnIndex(GIST_COLUMN_CREATED_AT))));
            gist.setOwner(getOwner(gist.getGistId()));
            gist.setFiles(getGistFiles(gist.getGistId()));

            gists.add(gist);

            res.moveToNext();
        }
    }

    private Owner fillOwner(Cursor res) {
        Owner owner = new Owner();
        owner.setLogin(res.getString(res.getColumnIndex(OWNER_COLUMN_LOGIN)));
        owner.setAvatarUrl(res.getString(res.getColumnIndex(OWNER_COLUMN_AVATAR_URL)));

        return owner;
    }

    private void insertLanguageAndType(Gist gist, ContentValues contentValues) {
        Map<String, GistFile> files = gist.getFiles();

        if (files != null) {
            for (String key : files.keySet()) {
                String language = files.get(key).getLanguage();
                String type = files.get(key).getType();

                if (type != null && !type.isEmpty()) {
                    contentValues.put(GIST_COLUMN_GIST_TYPE, type);
                } else {
                    contentValues.put(GIST_COLUMN_GIST_TYPE, "unknown type");
                }

                if (language != null && !language.isEmpty()) {
                    contentValues.put(GIST_COLUMN_LANGUAGE, language);
                } else {
                    contentValues.put(GIST_COLUMN_LANGUAGE, "unknown language");
                }

                break;
            }
        }
    }
}