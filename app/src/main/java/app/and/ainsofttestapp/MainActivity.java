package app.and.ainsofttestapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private Button mLoadXmlButton, mShowDbButton;
    private ProgressBar mProgressBar;
    private DBHelper dbHelper;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadXmlButton = (Button)findViewById(R.id.load_xml_button);
        mShowDbButton = (Button)findViewById(R.id.show_db_button);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        deleteDatabase(DBHelper.getDbName());
        dbHelper = new DBHelper(this);

        mLoadXmlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDataLoaded) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    LoadXmlTask load = new LoadXmlTask();
                    load.execute();
                } else
                    Toast.makeText(getApplicationContext(),"Данные уже загружены",Toast.LENGTH_LONG).show();
            }
        });
        mShowDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DatabaseActivity.class);
                startActivity(intent);
            }
        });
    }

    private Boolean loadXmlAndWriteToDb(){
        Boolean result = false;
        try {
            URL url = new URL("http://ainsoft.pro/test/test.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("product");
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            for(int i=0; i<nodeList.getLength(); i++){
                Node node = nodeList.item(i);

                int id = Integer.parseInt(((Element)node).getElementsByTagName("id").item(0).getTextContent());
                String name = ((Element)node).getElementsByTagName("name").item(0).getTextContent();
                double price = Double.parseDouble(((Element)node).getElementsByTagName("price").item(0).getTextContent());

                ContentValues contentValues = new ContentValues();
                contentValues.put("id",id);
                contentValues.put("name",name);
                contentValues.put("price",price);
                dbase.insert(DBHelper.getTableName(),null,contentValues);
            }
            dbase.close();
            result = true;
        } catch (Exception ex){
            Log.e("XML_DB_ERROR",ex.getMessage());
        }
        return result;
    }

    class LoadXmlTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            return loadXmlAndWriteToDb();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(getApplicationContext(), "XML файл загружен в БД", Toast.LENGTH_LONG).show();
                isDataLoaded = true;
            }
            else
                Toast.makeText(getApplicationContext(),"Ошибка при загрузке XML в БД",Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
