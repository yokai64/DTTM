package com.yokai64.dttm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //List of class global variables
    Button btnDatePicker,
            btnTimePicker,
            btnCopyTag;
    TextView txtDate,
            txtTime,
            txtFormattedOutput,
            txtResultTag;
    DateTimeFormatter myFormatObj;
    Spinner spinner;
    long unixTime;

    //When the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up variables
        //Buttons
        btnDatePicker = findViewById(R.id.setDateButton);
        btnTimePicker =findViewById(R.id.setTimeButton);
        btnCopyTag = findViewById(R.id.copyTagButton);
        //TextViews
        txtDate = findViewById(R.id.dateLine);
        txtTime = findViewById(R.id.timeLine);
        txtFormattedOutput = findViewById(R.id.formattedOutputText);
        txtResultTag = findViewById(R.id.resultantTagText);
        //Date Formatter
        myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");

        //onClick Listeners
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnCopyTag.setOnClickListener(this);

        spinner = findViewById(R.id.displayModeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.displayModesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateTag((String) txtDate.getText(), (String) txtTime.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void updateTag(String date, String time) {
        //if the time and date has not been set yet
        if(date.equals("-") || time.equals("-")) {
            return;
        } else {
            //parse the time and date in
            ZonedDateTime zonedDateTime = parseDate(date, time).toInstant().atZone(ZoneId.systemDefault());

            //format the input date
            String formattedDate = zonedDateTime.format(myFormatObj);

            unixTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toEpochSecond();

            String tag = "<t:" + unixTime;

            char spinnerSelected = spinner.getSelectedItem().toString().charAt(spinner.getSelectedItem().toString().length() - 2);

            switch (spinnerSelected){
                case ('l'):
                    tag = tag + ">";
                    break;
                case ('d'):
                    tag = tag + ":d>";
                    break;
                case ('D'):
                    tag = tag + ":D>";
                    break;
                case ('f'):
                    tag = tag + ":f>";
                    break;
                case ('F'):
                    tag = tag + ":F>";
                    break;
                case ('t'):
                    tag = tag + ":t>";
                    break;
                case ('T'):
                    tag = tag + ":T>";
                    break;
                case ('R'):
                    tag = tag + ":R>";
                    break;
            }

            txtFormattedOutput.setText("Local Time: " + formattedDate + "\nUTC: " + zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).format(myFormatObj) + "");
            txtResultTag.setText(tag);
        }
    }

    public static Date parseDate(String date, String time) {
        try {
            return new SimpleDateFormat("MMM dd, yyyy HH:mm:ss").parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        int mMinute,
                mHour,
                mDay,
                mMonth,
                mYear;

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth)
                        {
                            String sDate1=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            Date date1= null;
                            try {
                                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            assert date1 != null;
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
                            txtDate.setText(dateFormatter.format(date1));
                            updateTag((String) txtDate.getText(), (String) txtTime.getText());

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
                            String sDate1=hourOfDay + ":" + minute + ":00";
                            txtTime.setText(Time.valueOf(sDate1).toString());

                            updateTag((String) txtDate.getText(), (String) txtTime.getText());
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == btnCopyTag) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Discord Timezone Tag", txtResultTag.getText());
            clipboard.setPrimaryClip(clip);
        }
    }
}