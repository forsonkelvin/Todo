package com.example.forso.to_do;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button addBtn;
    EditText addItem;
    RecyclerView rvItems;
    itemsAdapter ItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Associates each view with the appropriate member name.
        addBtn = findViewById(R.id.addItem);
        addItem = findViewById(R.id.newItem);
        rvItems = findViewById(R.id.listItem);



        //Creates an array to store the list of to dos.
        loadItems();

        itemsAdapter.OnLongClickListener onLongClickListener = new itemsAdapter.OnLongClickListener(){

            @Override
            public void onItemLongClicked(int position) {
                //Delete item from view.
                items.remove(position);
                // Notify the adapter;
                ItemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item is removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        itemsAdapter.OnClickListener onClickListener = new itemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at " + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //Display next activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        ItemsAdapter = new itemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(ItemsAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = addItem.getText().toString();
                items.add(todoItem);
                ItemsAdapter.notifyItemInserted(items.size()-1);
                addItem.setText("");
                Toast.makeText(getApplicationContext(), "New item is added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    // handle the result of the edit Activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            // Retrieve the new text in the edit Activity.
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Stores the current position of the edited item.
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model to store the new text
            items.set(position, itemText);
            // notify the adapter
            ItemsAdapter.notifyItemChanged(position);
            // persist the changes made to the library.
            saveItems();
            Toast.makeText(getApplicationContext(), "Item is updated", Toast.LENGTH_SHORT).show();
        } else{
            Log.w("MainActivity", "Unable to log data to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }
    private  void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
         Log.e("MainActivity", "Error saving items");
        }
    }
}
