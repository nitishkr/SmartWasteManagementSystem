package app.iiitd.swms;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class InfoActivity extends AppCompatActivity {

    Location bin1, bin2, iiitd;
    int radius;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setiitd();
        processData();
/*
      //  android.R.layout.simple_list_item_2
        lv = (ListView) findViewById(R.id.listView);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        List<String> your_array_list = new ArrayList<String>();
        your_array_list.add("Bin 1");
        your_array_list.add("Bin 2");

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );


        lv.setAdapter(arrayAdapter);*/

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(bin1.getLatitude(), bin1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.v("Latitude ", address+" ,"+city+","+state);

            TextView text1 = (TextView) findViewById(R.id.textView3);
            text1.setText(address);
            TextView text2 = (TextView) findViewById(R.id.textView5);
            text2.setText(city+", "+state);

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Exception","caught it" );
        }


        try {
            addresses = geocoder.getFromLocation(bin2.getLatitude(), bin2.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.v("Latitude ", address+" ,"+city+","+state);

            TextView text1 = (TextView) findViewById(R.id.textView9);
            text1.setText(address);
            TextView text2 = (TextView) findViewById(R.id.textView11);
            text2.setText(city+", "+state);

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Exception","caught it" );
        }



        Button buttonOne = (Button) findViewById(R.id.button);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                Intent intent = new Intent(InfoActivity.this, MapsActivity.class);
                intent.putExtra("Lat1", bin1.getLatitude());
                intent.putExtra("Long1", bin1.getLongitude());
                intent.putExtra("Lat2", bin2.getLatitude());
                intent.putExtra("Long2", bin2.getLongitude());
                startActivity(intent);
            }
        });


    }

    private void processData() {
        double x0, y0;
        y0 = iiitd.getLatitude();
        x0 = iiitd.getLongitude();

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
       // System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude );

     bin1 = new Location("dummyprovider");
     bin1.setLatitude(foundLatitude);
        bin1.setLongitude(foundLongitude);
        Log.v("Bin1", foundLatitude + "," + foundLatitude);
        u = random.nextDouble();
        v = random.nextDouble();
        w = radiusInDegrees * Math.sqrt(u);
        t = 2 * Math.PI * v;
        x = w * Math.cos(t);
        y = w * Math.sin(t);
// Adjust the x-coordinate for the shrinking of the east-west distances
        new_x = x / Math.cos(y0);
        foundLongitude = new_x + x0;
        foundLatitude = y + y0;
        Log.v("Bin2", foundLatitude+","+foundLatitude);
        bin2 = new Location("dummyprovider");
        bin2.setLatitude(foundLatitude);
        bin2.setLongitude(foundLongitude);



    }


    private void setiitd() {
        iiitd = new Location("dummyprovider");
       iiitd.setLatitude(28.5650);
        iiitd.setLongitude(77.2100);
        radius = 5000;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
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
