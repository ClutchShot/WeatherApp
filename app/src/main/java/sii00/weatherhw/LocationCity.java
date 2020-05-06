package sii00.weatherhw;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class LocationCity implements LocationListener {

    Context context;


    LocationCity (Context context){
        this.context = context;

    }

    public Location getLocation(){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Permission not garanted", Toast.LENGTH_LONG).show();
            return null;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSenabeld = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkenabeld = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSenabeld){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,1000,this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if ( location!= null){
                return  location;
            }
        }
//        else  {
//            Toast.makeText(context,"Please enable GPS",Toast.LENGTH_LONG).show();
//        }
        if (isNetworkenabeld){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,6000,1000,this);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return location;
        }
        return null;
    }




    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
