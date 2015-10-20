package app.iiitd.swms;


import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MapsActivity extends FragmentActivity {

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    Location bin1, bin2;
    double distance1, distance2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


       Bundle b = getIntent().getExtras();
        bin1 = new Location("dummyprovider");
        bin1.setLatitude(b.getDouble("Lat1"));
        bin1.setLongitude(b.getDouble("Long1"));

        bin2 = new Location("dummyprovider");
        bin2.setLatitude(b.getDouble("Lat2"));
        bin2.setLongitude(b.getDouble("Long2"));

        Log.v("Bin1", String.valueOf(bin1.getLatitude()) + "," + String.valueOf(bin1.getLongitude()));
        Log.v("Bin2", String.valueOf(bin2.getLatitude()) + "," + String.valueOf(bin2.getLongitude()));


        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if(map!=null){

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);

            LatLng point = new LatLng(bin1.getLatitude(),bin1.getLongitude());

                    markerPoints.add(point);

                    MarkerOptions options = new MarkerOptions();
                    options.position(point).title("Bin 1");
                     map.addMarker(options);
                    point = new LatLng(bin2.getLatitude(),bin2.getLongitude());
                    markerPoints.add(point);
                    options.position(point).title("Bin 2 ");
                     map.addMarker(options);
                     point = new LatLng(28.5444, 77.2725);
                     markerPoints.add(point);
                     options.position(point).title("User Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    map.addMarker(options);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(28.5444, 77.2725)).zoom(12.0f)           .bearing(90).tilt(30).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    // Checks, whether start and end locations are captured
            LatLng origin,dest;
            if ( getDistanceFromLatLonInKm(bin1.getLatitude(),bin1.getLongitude(),28.5444, 77.2725) > getDistanceFromLatLonInKm(bin2.getLatitude(),bin2.getLongitude(),28.5444, 77.2725)) {
                origin = markerPoints.get(1);
                dest = markerPoints.get(0);
            }
            else
            {
                origin = markerPoints.get(0);
                dest = markerPoints.get(1);
            }

                      //  Getting URL to the Google Directions API
                       String url = getDirectionsUrl(origin, dest);


                       DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API

                        downloadTask.execute(url);

                      if ( getDistanceFromLatLonInKm(bin1.getLatitude(),bin1.getLongitude(),28.5444, 77.2725) > getDistanceFromLatLonInKm(bin2.getLatitude(),bin2.getLongitude(),28.5444, 77.2725))
                          origin = markerPoints.get(1);
                      else
                          origin = markerPoints.get(0);
                        dest = markerPoints.get(2);
                         url = getDirectionsUrl(origin, dest);
                        DownloadTask downloadTas = new DownloadTask();
                        downloadTas.execute(url);
            Toast.makeText(getApplicationContext(), "Sketching Optimized Routes !!.......",
                    Toast.LENGTH_LONG).show();

            }
        }



    double getDistanceFromLatLonInKm(double lat1,double lon1, double lat2,double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.v("Query", url);

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }
}