package com.app.kindergarten;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ForecastService forecastService = new ForecastService();
    TextView textView;
    DatabaseHelper myDb;

    Button button_get_temperature;
    Button button_add_data;
    Button button_update;
    Button button_view_all;
    Button button_delete;

    EditText editText_name;
    EditText editText_temperature;
    EditText editText_clothes;
    EditText editText_id;

    EditText editText_latitude;
    EditText editText_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        button_get_temperature = (Button) findViewById(R.id.button_get_temperature);
        button_add_data = (Button) findViewById(R.id.button_add);
        button_update = (Button) findViewById(R.id.button_update);
        button_view_all = (Button) findViewById(R.id.button_view_all);
        button_delete = (Button) findViewById(R.id.button_delete);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_temperature = (EditText) findViewById(R.id.editText_temperature);
        editText_clothes = (EditText) findViewById(R.id.editText_clothes);
        editText_id = (EditText) findViewById(R.id.editText_id);

        editText_latitude = (EditText) findViewById(R.id.editText_latitude);
        editText_longitude = (EditText) findViewById(R.id.editText_longitude);

        button_get_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTemperature();
            }
        });


        addData();
        updateData();
        viewAll();
        deleteData();
    }

    private void getTemperature() {
        Callback<Forecast> callback = new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                double result = response.body().getCurrently().getTemperature();
                result = ((result - 32) * 5) / 9;
                textView = (TextView) findViewById(R.id.text_view);
                textView.setText(String.format("%.2f deg C", result));
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {

            }
        };

        String latitude = String.valueOf(editText_latitude.getText());
        String longitude = String.valueOf(editText_longitude.getText());


        forecastService.loadForecastData(callback, latitude, longitude);


    }

    public void addData() {
        button_add_data.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(editText_name.getText().toString(), editText_temperature.getText().toString(), editText_clothes.getText().toString());


                        if (isInserted) {
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void viewAll() {
        button_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    showMessage("Error", "Nothing found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Id: " + res.getString(0) + "\n");
                    buffer.append("Name: " + res.getString(1) + "\n");
                    buffer.append("Temperature: " + res.getString(2) + "\n");
                    buffer.append("Clothes: " + res.getString(3) + "\n");

                }

                showMessage("Data", buffer.toString());
            }
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void updateData() {
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUpdate = myDb.updateData(editText_id.getText().toString(), editText_name.getText().toString(), editText_temperature.getText().toString(), editText_clothes.getText().toString());

                if (isUpdate) {
                    Toast.makeText(MainActivity.this, "Data Updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteData() {
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deletedRows = myDb.deleteData(editText_id.getText().toString());

                if (deletedRows > 0) {
                    Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Data not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
