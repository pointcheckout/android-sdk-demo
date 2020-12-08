package com.pc.android.sdk.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pc.android.sdk.Environment;
import com.pc.android.sdk.PointCheckoutClient;
import com.pc.android.sdk.PointCheckoutEventListener;
import com.pc.android.sdk.PointCheckoutException;

public class MainActivity extends AppCompatActivity {

    private PointCheckoutClient pcClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabAction(view);
            }
        });

        try {
            pcClient = new PointCheckoutClient(true);
            pcClient.initialize(this);

        } catch (PointCheckoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "There was an error with PC", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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


    private void onFabAction(View view){
        EditText txtCheckoutKey = findViewById(R.id.checkoutKey);

        if(txtCheckoutKey.getText().toString().replace(" ", "").equals(""))
        {
            Snackbar.make(view, "Checkout key is required", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        try {
            pcClient.pay(this, txtCheckoutKey.getText().toString(), "test://test/redirect", new PointCheckoutEventListener() {
                @Override
                public void onPaymentCancel() {
                    System.out.println("!!PAYMENT CANCELLED");
                }

                @Override
                public void onPaymentUpdate() {
                    System.out.println("!!PAYMENT UPDATED");
                }
            });

        } catch (PointCheckoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "There was an error with PC", Toast.LENGTH_SHORT).show();
        }

    }
}