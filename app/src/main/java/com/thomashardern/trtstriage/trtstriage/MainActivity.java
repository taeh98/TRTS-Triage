package com.thomashardern.trtstriage.trtstriage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    private SeekBar eyeOpeningSeekBar;
    private SeekBar motorResponseSeekBar;
    private SeekBar verbalResponseSeekBar;

    private NumberPicker respRateNumberPicker;
    private NumberPicker systolicBPNumberPicker;

    private Button feedbackButton;

    private TextView outputTextView;

    private TextView eyeOpeningSeekBarLabel;
    private TextView motorResponseSeekBarLabel;
    private TextView verbalResponseSeekBarLabel;

    String disclaimerPreferecesName = "legalDisclaimer";

    protected Dialog onCreateDialog(int id){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Legal Notice\n\nCopyright © 2018 Thomas Hardern and Richard Hardern. All Rights Reserved. By using any of the contents herein, you agree that you are solely responsible for any consequences of their usage. The authors of this app in no way guarantee its ability to function correctly, robustness, and that it will not fail.")
                .setCancelable(false)
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences settings = getSharedPreferences(disclaimerPreferecesName, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(disclaimerPreferecesName, true);

                        editor.commit(); // commits the changes in settings to persistent storage
                    }
                })
                .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        return alert;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eyeOpeningSeekBar = (SeekBar) findViewById(R.id.eyeOpeningSeekBar);
        motorResponseSeekBar = (SeekBar) findViewById(R.id.motorResponseSeekBar);
        verbalResponseSeekBar = (SeekBar) findViewById(R.id.verbalResponseSeekBar);

        respRateNumberPicker = (NumberPicker) findViewById(R.id.respRateNumberPicker);
        systolicBPNumberPicker = (NumberPicker) findViewById(R.id.systolicBPNumberPicker);

        feedbackButton = (Button) findViewById(R.id.feedbackButton);

        outputTextView = (TextView) findViewById(R.id.resultTextView);

        eyeOpeningSeekBarLabel = (TextView) findViewById(R.id.eyeOpeningCurrentValue);
        motorResponseSeekBarLabel = (TextView) findViewById(R.id.motorResponseCurrentValue);
        verbalResponseSeekBarLabel = (TextView) findViewById(R.id.verbalResponseCurrentValue);

        SharedPreferences settings = getSharedPreferences(disclaimerPreferecesName, 0);
        boolean acceptedAlready = settings.getBoolean(disclaimerPreferecesName,false);
        if (!acceptedAlready) showDialog(0);

        eyeOpeningSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBarLabels();
                writeOutput();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        verbalResponseSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBarLabels();
                writeOutput();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        motorResponseSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBarLabels();
                writeOutput();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        respRateNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                writeOutput();
            }
        });

        systolicBPNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                writeOutput();
            }
        });


        feedbackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFeedbackForm();
            }
        });

        systolicBPNumberPicker.setMaxValue(300);
        systolicBPNumberPicker.setMinValue(0);
        systolicBPNumberPicker.setValue(130);

        respRateNumberPicker.setMaxValue(100);
        respRateNumberPicker.setMinValue(0);
        respRateNumberPicker.setValue(25);
    }

    private void updateSeekBarLabels() {
        int eyeOpening = eyeOpeningSeekBar.getProgress() + 1;
        int verbalResponse = verbalResponseSeekBar.getProgress() + 1;
        int motorResponse = motorResponseSeekBar.getProgress() + 1;

        eyeOpeningSeekBarLabel.setText(String.valueOf(eyeOpening));
        verbalResponseSeekBarLabel.setText(String.valueOf(verbalResponse));
        motorResponseSeekBarLabel.setText(String.valueOf(motorResponse));
    }

    private void openFeedbackForm() {
        String url = "https://docs.google.com/forms/d/1o4EzO5tozXb5PmjAyEZKsYgb2uAzrbaijWtOGsS8iaU";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void writeOutput() {
        try {
            int eyeOpening = eyeOpeningSeekBar.getProgress() + 1;
            int verbalResponse = verbalResponseSeekBar.getProgress() + 1;
            int motorResponse = motorResponseSeekBar.getProgress() + 1;
            int respRate = respRateNumberPicker.getValue();
            int systolicBP = systolicBPNumberPicker.getValue();
            String output = convertValues(eyeOpening, verbalResponse, motorResponse, respRate, systolicBP);

            if (output.length() > 25) outputTextView.setTextSize(14);
            else outputTextView.setTextSize(36);
            outputTextView.setText(output);
        } catch (NumberFormatException e) {
            String output = "An Exception (error) occurred formatting the input/s for the respiratory rate and/or systolic blood pressure into integer values.";
            output += "\nPlease make sure you enter valid positive integer values for both.";
            if (output.length() > 25) outputTextView.setTextSize(16);
            else outputTextView.setTextSize(36);
            outputTextView.setText(output);
        }
        catch (Exception e) {
            String output = "An Exception occurred processing the input values to find and print the output.";
            output += "\nThe message from the Exception was: " + e.getMessage();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();

            output += "\nThe stackTrace from the Exception was:\n" + stackTraceString;

            if (output.length() > 25) outputTextView.setTextSize(8);
            else outputTextView.setTextSize(36);
            outputTextView.setText(output);
        }
    }

    private static String convertValues(int eyeOpening, int verbalResponse, int motorResponse, int respiratoryRate, int systolicBP) {
        int glasgowComaScaleTotal = eyeOpening + verbalResponse + motorResponse;
        int fourScaleGlasgowComaScale = convertGlasgowComaScale(glasgowComaScaleTotal);
        int fourScaleRespiratoryRate = convertRespiratoryRate(respiratoryRate);
        int fourScaleSystolicBP = convertSystolicBP(systolicBP);
        int trts = fourScaleGlasgowComaScale + fourScaleRespiratoryRate + fourScaleSystolicBP;
        return getOutput(trts, fourScaleGlasgowComaScale, fourScaleRespiratoryRate, fourScaleSystolicBP, glasgowComaScaleTotal, respiratoryRate, systolicBP);
    }

    private static int convertGlasgowComaScale(int rawGCSIn) {
        if (rawGCSIn > 15 || rawGCSIn < 3) {
            return -1;
        }

        else if (rawGCSIn >= 13 && rawGCSIn <= 15) {
            return 4;
        }
        else if (rawGCSIn >= 9 && rawGCSIn <= 12) {
            return 3;
        }
        else if (rawGCSIn >= 6 && rawGCSIn <= 8) {
            return 2;
        }
        else if (rawGCSIn >= 4 && rawGCSIn <= 5) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private static int convertRespiratoryRate(int rawRRIn) {
        if (rawRRIn < 0 || rawRRIn > 100) {
            return -1;
        }
        else if (rawRRIn > 29) {
            return 3;
        }
        else if (rawRRIn > 9 && rawRRIn < 30) {
            return 4;
        }
        else if (rawRRIn > 5 && rawRRIn < 10) {
            return 2;
        }
        else if (rawRRIn > 0 && rawRRIn < 6) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private static int convertSystolicBP(int rawSBPIn) {
        if (rawSBPIn < 0 || rawSBPIn > 300) {
            return -1;
        }
        else if (rawSBPIn > 89) {
            return 4;
        }
        else if (rawSBPIn > 75 && rawSBPIn < 90) {
            return 3;
        }
        else if (rawSBPIn > 49 && rawSBPIn < 76) {
            return 2;
        }
        else if (rawSBPIn > 0 && rawSBPIn < 50) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private static String getOutput(int trts, int fourScaleGlasgowComaScale, int fourScaleRespiratoryRate, int fourScaleSystolicBP, int glasgowComaScaleTotal, int respiratoryRate, int systolicBP) {
        if (fourScaleGlasgowComaScale == -1 || fourScaleRespiratoryRate == -1 || fourScaleSystolicBP == -1) {
            String returnString = "An error occurred converting the raw values of the GCS total, respiratory rate, or systolic BP, or a combination of the three, into four-point scales.";
            if (fourScaleGlasgowComaScale == -1) returnString += " This occurred for the GCS total when converting a raw value of " + glasgowComaScaleTotal + ".";
            if (fourScaleRespiratoryRate == -1) returnString += " This occurred for the respiratory rate when converting a raw value of " + respiratoryRate + ".";
            if (fourScaleSystolicBP == -1) returnString += " This occurred for the systolic BP when converting a raw value of " + systolicBP + ".";
            return returnString;
        }

        int priority = 1; // immediate priority
        if (trts == 12) priority = 3; // delayed priority
        else if (trts == 11) priority = 2; // urgent priority
        return "RTS VALUE: " + trts + "\nPRIORITY: " + priority;
    }
}
