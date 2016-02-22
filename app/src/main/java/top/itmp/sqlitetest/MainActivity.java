package top.itmp.sqlitetest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaRouter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.sql.SQLDataException;

public class MainActivity extends AppCompatActivity {

    private EditText id01;
    private EditText id02;
    private Button button;
    private ListView listView;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id01 = (EditText)findViewById(R.id.id_01);
        id02 = (EditText)findViewById(R.id.id_02);
        button = (Button)findViewById(R.id.button);
        listView = (ListView)findViewById(R.id.listview);

        Log.v("files", this.getFilesDir().toString());
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + File.separator + "sql.db", null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = id01.getText().toString();
                String content = id02.getText().toString();
                if(title != null && content != null && title.length() != 0) {
                    try {
                        insertDb(sqLiteDatabase, title, content);

                        Cursor cursor = sqLiteDatabase.rawQuery("select * from news_inf", null);
                        inflateList(cursor);
                    }catch(SQLiteException se){
                        sqLiteDatabase.execSQL("create table news_inf(_id integer "
                                + " primary key autoincrement,"
                                + " news_title varchar(50),"
                                + " news_content varchar(255))");
                        insertDb(sqLiteDatabase, title, content);
                        Cursor cursor = sqLiteDatabase.rawQuery("select * from news_inf", null);
                        inflateList(cursor);
                    }
                } else {
                    Toast.makeText(getApplication(), "plz input something.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void insertDb(SQLiteDatabase db, String title, String content){
        db.execSQL("insert into news_inf values(null, ?, ?)",
                new String[]{title, content});
    }
    private void inflateList(Cursor cursor){
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.line,
                cursor,
                new String[]{ "_id", "news_title", "news_content"},
                new int[]{R.id._id, R.id.title, R.id.content},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        listView.setAdapter(simpleCursorAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sqLiteDatabase != null && sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }
    }
}
