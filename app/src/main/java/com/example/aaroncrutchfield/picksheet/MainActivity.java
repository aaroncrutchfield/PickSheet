package com.example.aaroncrutchfield.picksheet;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aaroncrutchfield.picksheet.data.PickListDbHelper;
import com.example.aaroncrutchfield.picksheet.data.PickListDbUtility;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private PickListAdapter mPickListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rvPickList = (RecyclerView) findViewById(R.id.rv_pickList);


        PickListDbHelper dbHelper = new PickListDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        formatRvPickList(rvPickList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForNewPartnumber();
            }
        });
    }

    /**
     * Creates an AlertDialog to prompt for a part number that will be entered into the database
     */
    private void promptForNewPartnumber() {
        final EditText input = new EditText(this);
        // TODO: 10/4/17 Create autocomplete input editText
        formatInputField(input);
        AlertDialog.Builder newPartPrompt =
                new AlertDialog.Builder(this)
                        .setTitle("Enter New Partnumber")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                processInput(input);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });

        newPartPrompt.setView(input);
        newPartPrompt.show();
    }

    /**
     * Convenience method to add part number into the database or notify if the part number already
     * exists
     * @param input the EditText containing the input String
     */
    private void processInput(EditText input) {
        String partnumber = input.getText().toString();
        long rowId = PickListDbUtility.addDatabaseEntry(partnumber, mDb);
        String message = "";

        if (rowId != -1) {
            mPickListAdapter.swapCursor(PickListDbUtility.getPartnumbersForPickListAdapter(mDb));
            message = partnumber + " was added";
        } else {
            message = partnumber + " already exists";
        }
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Convenience method for setting an onKeyListener for the Enter key on the input EditText
     * @param input the EditText to set the onKeyListener on
     */
    private void formatInputField(final EditText input) {
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        processInput(input);
                        input.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Convinience method for setting up the RecyclerView and its Adapter
     * @param rvPickList
     */
    private void formatRvPickList(RecyclerView rvPickList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPickList.setLayoutManager(layoutManager);

        mPickListAdapter = new PickListAdapter(this, mDb);

        rvPickList.setAdapter(mPickListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
