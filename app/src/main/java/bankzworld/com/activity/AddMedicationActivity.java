package bankzworld.com.activity;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import bankzworld.com.R;
import bankzworld.com.data.AppDatabase;
import bankzworld.com.data.Medication;
import bankzworld.com.util.NotificationUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMedicationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddMedicationActivity";

    @BindView(R.id.medication_name)
    TextInputEditText mMedicationName;
    @BindView(R.id.medication_description)
    TextInputEditText mMedicationDescription;
    @BindView(R.id.number_of_doze)
    TextInputEditText mNumberOfDoze;
    @BindView(R.id.number_of_times_daily)
    TextInputEditText mNumberOfTimes;
    @BindView(R.id.date)
    EditText mEditDate;
    @BindView(R.id.alarm_time)
    TextInputEditText mSetAlarm;
    @BindView(R.id.end_date)
    TextInputEditText mEndDate;
    @BindView(R.id.button_add)
    Button mButtonAdd;
    @BindView(R.id.btn_date)
    Button mButtonDate;
    public AppDatabase db;
    Medication medication;
    // declaration of variables
    private String mName, mDescription, mDozeNumber, mNumberOfTimesDaily, mNumberOfDays, mCurrentDate, mMonth, mAlarm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_medication);

        // binds views
        ButterKnife.bind(this);

        // initialises the db
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production").build();

        // sets the click listeners
        mButtonDate.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_add:

                /**Receives inputs and store in a String variable**/
                mName = mMedicationName.getText().toString();
                mDescription = mMedicationDescription.getText().toString();
                mDozeNumber = mNumberOfDoze.getText().toString();
                mNumberOfTimesDaily = mNumberOfTimes.getText().toString();
                mCurrentDate = mEditDate.getText().toString();
                mNumberOfDays = mEndDate.getText().toString();
                mAlarm = mSetAlarm.getText().toString();


                // calls validation method
                if (!dataValidation(mName, mDescription, mDozeNumber, mNumberOfTimesDaily, mCurrentDate, mNumberOfDays, mMonth, mAlarm)) {
                    return;
                }

                break;
            case R.id.btn_date:
                getCurrentDateAndtTime();
                break;
            default:
                break;
        }
    }

    /**
     * Gets the current date
     **/
    private void getCurrentDateAndtTime() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy" + " , " + "HH:mm:ss");

        // gets just the month format to store in the database
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM");

        // gets the format for month and year alone to use in querying the database
        mMonth = simpleDateFormat1.format(calendar.getTime());

        // gets dates and store in a String variable
        mEditDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    /**
     * validate the inputs
     **/
    public boolean dataValidation(String medName, String medDescription,
                                  String numOfDoze, String numOfTimes, String date, String endDate, String month, String alarm) {

        if (medName.isEmpty()) {
            mMedicationName.setError(getString(R.string.error_message));
        } else if (medDescription.isEmpty()) {
            mMedicationDescription.setError(getString(R.string.error_message));
        } else if (numOfDoze.isEmpty()) {
            mNumberOfDoze.setError(getString(R.string.error_message));
        } else if (numOfTimes.isEmpty()) {
            mNumberOfTimes.setError(getString(R.string.error_message));
        } else if (date.isEmpty() || month.isEmpty()) {
            mEditDate.setText(getString(R.string.error_message));
        } else if (endDate.isEmpty()) {
            mEndDate.setError(getString(R.string.error_message));
        } else {
            // insert to database
            /**WHEN TESTING, REMEMBER TO COMMENT ON THIS LINE WHEN PERFORMING A JUNIT4 TEST, AS ASYNC TASK IS NOT MOCKED**/
            new PerformInsertion().execute();
        }
        return true;
    }

    /**
     * Performs insertion into the room
     **/
    class PerformInsertion extends AsyncTask<Medication, Void, Void> {

        private static final String TAG = "PerformInsertion";

        @Override
        protected Void doInBackground(Medication... medications) {
            Log.d(TAG, "doInBackground: called");
            medication = new Medication(mName, mDescription, mDozeNumber, mNumberOfTimesDaily, mCurrentDate, mNumberOfDays, mMonth);
            db.medicationDao().insertAll(medication);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /**Note: REMEMBER TO COMMENT THIS INTENT LINE IN OTHER TO HAVE AN ACCURATE ESPRESSO TEST RESULT OF THIS ACTIVITY/FILE**/
            // sets reminder and calls next activity
            NotificationUtil.setAlarm(AddMedicationActivity.this, mAlarm);
            startActivity(new Intent(AddMedicationActivity.this, MainActivity.class));
            finish();
        }
    }
}
