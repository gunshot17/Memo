package com.block.memo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.block.memo.model.Memo;
import com.block.memo.util.Util;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 테이블 생성문 SQLite 문법에 맞게 작성해야 한다.
        String CREATE_MEMO_TABLE = "create table " +
                Util.TABLE_NAME + "(" +
                Util.KEY_ID + " integer not null primary key autoincrement," +
                Util.KEY_TITLE + " text, " +
                Util.KEY_CONTENT + " text )";

        // 2. 쿼리 실행
        db.execSQL(CREATE_MEMO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "drop table " + Util.TABLE_NAME;
        db.execSQL(DROP_TABLE);

        // 테이블 새로 다시 생성.
        onCreate(db);
    }

    // 여기서부터는 기획에 맞게 데이터베이스에 넣고, 업데이트, 가져오고, 지우고
    // 메소드 만들기.

    public void addMemo(Memo memo){
        // 1. 저장하기 위해서, writable db 를 가져온다.
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. db에 저장하기 위해서는, ContentValues를 이용한다.
        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, memo.getTitle());
        values.put(Util.KEY_CONTENT, memo.getContent());
        // 3. db에 실제로 저장한다.
        db.insert(Util.TABLE_NAME, null, values);
        db.close();
        Log.i("myDB", "inserted.");
    }

    // 메모1개 불러오는 메소드
    public Memo getMemo(int id){
        // 1. 데이터베이스 가져온다. 조회니까, readable 한 db로 가져온다.
        SQLiteDatabase db = this.getReadableDatabase();

        // select id, title, content from memo where id = 2;
        // 2. 데이터를 셀렉트(조회) 할때는, Cursor 를 이용해야 한다.
        Cursor cursor = db.query(Util.TABLE_NAME,
                new String[] {"id", "title", "content"},
                Util.KEY_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        int selectedId = Integer.parseInt(cursor.getString(0));
        String selectedTitle = cursor.getString(1);
        String selectedContent = cursor.getString(2);

        // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
        Memo memo = new Memo();
        memo.setId(selectedId);
        memo.setTitle(selectedTitle);
        memo.setContent(selectedContent);

        return memo;
    }

    // 전체 저장된 데이터 모두 가져오기.
    public ArrayList<Memo> getAllMemo(){
        // 1. 비어 있는 어레이 리스트를 먼저 한개 만든다.
        ArrayList<Memo> memoList = new ArrayList<>();

        // 2. 데이터베이스에 select (조회) 해서,
        String selectAll = "select * from " + Util.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);

        // 3. 여러개의 데이터를 루프 돌면서, Contact 클래스에 정보를 하나씩 담고
        if(cursor.moveToFirst()){
            do {

                int selectedId = Integer.parseInt(cursor.getString(0));
                String selectedTitle = cursor.getString(1);
                String selectedContent = cursor.getString(2);
                Log.i("myDB", "do while : " + selectedTitle);
                // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
                Memo memo = new Memo();
                memo.setId(selectedId);
                memo.setTitle(selectedTitle);
                memo.setContent(selectedContent);

                // 4. 위의 빈 어레이리스트에 하나씩 추가를 시킨다.
                memoList.add(memo);

            }while(cursor.moveToNext());
        }
        return memoList;
    }

    // 데이터를 업데이트 하는 메서드.
    public int updateMemo(Memo memo){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, memo.getTitle());
        values.put(Util.KEY_CONTENT, memo.getContent());

        // 데이터베이스 테이블 업데이트.
        // update memo set title="홍길동", content="asdfasdf" where id = 3;
        int ret = db.update(Util.TABLE_NAME,    // 테이블명
                values,     // 업데이트할 값
                Util.KEY_ID + " = ? ",   // where
                new String[]{String.valueOf(memo.getId())}); // ? 에 들어갈 값
        db.close();
        return ret;
    }

    // 데이터 삭제 메서드
    public void deleteMemo(Memo memo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_NAME,  // 테이블 명
                Util.KEY_ID + " = ?",   // where id = ?
                new String[]{String.valueOf(memo.getId())});  // ? 에 해당하는 값.
        db.close();
    }

    // 테이블에 저장된 데이터의 전체 갯수를 리턴하는 메소드.
    public int getCount(){
        // select count(*) from memo;
        String countQuery = "select * from " + Util.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    // 검색용 메소드 추가
    public ArrayList<Memo> search(String keyword){
        String searchQuery = "select id, title, content from "+Util.TABLE_NAME+
                " where content like ? or title like ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(searchQuery, new String[]{"%"+keyword+"%", "%"+keyword+"%"} );
        ArrayList<Memo> memoList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                int selectedId = Integer.parseInt(cursor.getString(0));
                String selectedTitle = cursor.getString(1);
                String selectedContent = cursor.getString(2);
                Log.i("myDB", "do while : " + selectedTitle);
                // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
                Memo memo = new Memo();
                memo.setId(selectedId);
                memo.setTitle(selectedTitle);
                memo.setContent(selectedContent);

                // 4. 위의 빈 어레이리스트에 하나씩 추가를 시킨다.
                memoList.add(memo);

            }while(cursor.moveToNext());
        }
        return memoList;
    }


}
