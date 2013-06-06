package com.example.getcurrentlocation; 
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	TextView textView2,textView3,textView1;
	Button button1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView2=(TextView)findViewById(R.id.textView2);
		textView3=(TextView)findViewById(R.id.textView3);
		textView1=(TextView)findViewById(R.id.textView1);
		button1=(Button)findViewById(R.id.button1);
		
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				myLocation=new MyLocation();
				turnGPSOn();
				getMyCurrentLocation();
				
			}
		});
		
		
	}

	public void turnGPSOn(){
    	try
    	{
    	
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            sendBroadcast(poke);
        }
    	}
    	catch (Exception e) {
			
		}
    }

	public void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            sendBroadcast(poke);
        }
    }
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		turnGPSOff();
	}
	
	/**
	 * Check the type of GPS Provider available at that instance and 
	 * collect the location informations
	 * 
	 * @Output Latitude and Longitude
	 */
	void getMyCurrentLocation() {
		
		
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new MyLocationListener();
		
		
		 try{gps_enabled=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
	       try{network_enabled=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

	        //don't start listeners if no provider is enabled
	        //if(!gps_enabled && !network_enabled)
	            //return false;

	        if(gps_enabled){
	        	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
	        	
	        }
	        
	        
	        if(gps_enabled){
	        	location=locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        	
	        	
	        }
	        
 
	        if(network_enabled && location==null){
	        	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
	        	
	        }
		
		
	        if(network_enabled && location==null)	{
            	location=locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
            
	        }
		
		if (location != null) {
			
			MyLat = location.getLatitude();
			MyLong = location.getLongitude();

		
		} else {
			Location loc= getLastKnownLocation(this);
			if (loc != null) {
				
				MyLat = loc.getLatitude();
				MyLong = loc.getLongitude();
				

			} 
		}
		locManager.removeUpdates(locListener);
		
		try
		{
		Geocoder geocoder;
		
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		//MyLat=	37.3004186;	//marcel locations
		//MyLong=-121.8867312;//marcel locations
		addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

		StateName= addresses.get(0).getAdminArea();
		CityName = addresses.get(0).getLocality();
		CountryName = addresses.get(0).getCountryName();
		
		
		
		System.out.println(" StateName " + StateName);
		System.out.println(" CityName " + CityName);
		System.out.println(" CountryName " + CountryName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		textView2.setText(""+MyLat);
		textView3.setText(""+MyLong);
		textView1.setText(" StateName " + StateName +" CityName " + CityName +" CountryName " + CountryName);
	}
	
	
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (location != null) {
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}
	
	private boolean gps_enabled=false;
	private boolean network_enabled=false;
	Location location;
	MyLocation myLocation;	
	Double MyLat, MyLong; 
	String CityName="";
	String StateName="";
	String CountryName="";
	

	public static Location getLastKnownLocation(Context context)
    {
        Location location = null;
        LocationManager locationmanager = (LocationManager)context.getSystemService("location");
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do
        {
        	//System.out.println("---------------------------------------------------------------------");
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if(i != false && !locationmanager.isProviderEnabled(s))
                continue;
           // System.out.println("provider ===> "+s);
            Location location1 = locationmanager.getLastKnownLocation(s);
            if(location1 == null)
                continue;
            if(location != null)
            {
            	//System.out.println("location ===> "+location);
            	//System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if(f >= f1)
                {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if(l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
           // System.out.println("location  out ===> "+location);
        	//System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);
           // System.out.println("---------------------------------------------------------------------");
        } while(true);
        return location;
    }
	
}
