package app.and.ainsofttestapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        dbHelper = new DBHelper(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpAdapter();
    }

    private void setUpAdapter(){
        mAdapter = new RecyclerAdapter(getDataFromBD());
        mRecyclerView.setAdapter(mAdapter);
    }

    private boolean updateDB(int id, double price){
        boolean result = false;
        SQLiteDatabase dbase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("price",price);
        int upd = dbase.update(DBHelper.getTableName(),contentValues,"id = ?",new String[]{String.valueOf(id)});
        if(upd>0) result = true;
        dbase.close();
        return result;
    }

    private List<DataModel> getDataFromBD(){
        List<DataModel> datalist = new ArrayList<>();
        SQLiteDatabase dbase = dbHelper.getReadableDatabase();
        Cursor cursor = dbase.query(DBHelper.getTableName(),null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                datalist.add(new DataModel(cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("price"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbase.close();
        return datalist;
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
        List<DataModel> datalist;

        public RecyclerAdapter(List<DataModel> datalist) {
            this.datalist = datalist;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DataModel dm = datalist.get(position);
            holder.pName.setText(dm.name);
            holder.pPrice.setText(String.valueOf(dm.price)+" UAH");
            final int id = dm.id;
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText input = new EditText(view.getContext());
                    input.setHint("Новая цена");
                    input.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                    alertDialog.setTitle("Изменить цену " + ((TextView)view.findViewById(R.id.card_name)).getText().toString());
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Double newPrice = Double.valueOf(input.getText().toString());
                            if(newPrice!=null){
                                if(updateDB(id,newPrice)){
                                    Toast.makeText(input.getContext(),"Данные обновлены", Toast.LENGTH_LONG).show();
                                    setUpAdapter();
                                } else {
                                    Toast.makeText(input.getContext(),"Ошибка записи в БД", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(input.getContext(),"Неправильный формат данных", Toast.LENGTH_LONG).show();
                                dialogInterface.cancel();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card,parent,false);
            return new ViewHolder(v);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView pName,pPrice;
            public CardView card;

            public ViewHolder(View view) {
                super(view);
                pName = (TextView)view.findViewById(R.id.card_name);
                pPrice = (TextView)view.findViewById(R.id.card_price);
                card = (CardView)view.findViewById(R.id.card);
            }
        }
    }
}
