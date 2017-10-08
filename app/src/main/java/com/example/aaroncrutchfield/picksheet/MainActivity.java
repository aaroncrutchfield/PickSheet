package com.example.aaroncrutchfield.picksheet;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int SPEECH_INPUT_REQUEST = 65461;
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
                promptForNewPartnumber("");
            }
        });


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                requestSpeechInput();
                return false;
            }
        });
    }

    /**
     * Convenience method for adding a part number to the database via speech recognition
     */
    private void requestSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say the part number");
        try{
            startActivityForResult(intent, SPEECH_INPUT_REQUEST);
        } catch (ActivityNotFoundException e){

        }
    }

    /**
     * Returns data from the speech RecognizerIntent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_INPUT_REQUEST:
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //ADD PART TO DATABASE
                    String partnumber = result.get(0).replaceAll("\\s", "").toUpperCase();
                    if (validateSpeechInput(partnumber))
                        processInput(partnumber);
                    else
                        promptForNewPartnumber(partnumber);
                }
        }
    }

    private boolean validateSpeechInput(String partnumber) {
        //ex: 26666-A-000 -or- 308330-H-030
        Pattern digitsWithSuffix = Pattern.compile("\\d{5,6}-\\w-\\d{3}");

        //ex: 6776 -or- 26580 -or- 305456
        Pattern digitsWithoutSuffix = Pattern.compile("\\d{4,6}");

        //ex: box -or- misc parts
        Pattern word = Pattern.compile("\\D{3,}");

        if (digitsWithSuffix.matcher(partnumber).matches()) {
            return true;
        } else if (digitsWithoutSuffix.matcher(partnumber).matches()){
            return true;
        } else if ( word.matcher(partnumber).matches()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates an AlertDialog to prompt for a part number that will be entered into the database
     */
    private void promptForNewPartnumber(String partnumber) {
        final EditText input = new EditText(this);
        // TODO: 10/4/17 Create autocomplete input editText
        formatInputField(input);
        AlertDialog.Builder newPartPrompt = new AlertDialog.Builder(this);
        String title;
        if (!partnumber.equals("")){
            title = "Did you mean...?";
            //append moves the cursor to the end of the text
            input.append(partnumber);
        } else {
            title = "Enter a new part number";
        }

        newPartPrompt.setTitle(title)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String partnumber = input.getText().toString();
                                processInput(partnumber);
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
     * @param partnumber
     */
    private void processInput(String partnumber) {
        long rowId;
        String message = "";

        if (validateSpeechInput(partnumber)) {
            rowId = PickListDbUtility.addDatabaseEntry(partnumber, mDb);
        } else {
            rowId = -2;
        }

        if (rowId > 0) {
            mPickListAdapter.swapCursor(PickListDbUtility.getPartnumbersForPickListAdapter(mDb));
            message = partnumber + " was added";
        } else if (rowId == -2) {
            message = "Please enter a valid input";
            promptForNewPartnumber(partnumber);
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
                        String partnumber = input.getText().toString();
                        processInput(partnumber);
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
