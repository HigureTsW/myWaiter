package it.sii.mywaiter;

import com.google.android.gms.maps.MapView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;

public class MapActivity extends FragmentActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		
	}

}



/*public class MapActivity extends Activity {

	MapView mMapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
	
		mMapView = (MapView) findViewById(R.layout.map_layout);
    	mMapView.onCreate(savedInstanceState);
    
	}

	@Override
	public void onPause() {
    	super.onPause();
    	mMapView.onPause();
	}

	@Override
	public void onResume() {
   		super.onResume();
   		mMapView.onResume();
	}

	@Override
	public void onDestroy() {
    	super.onDestroy();
    	mMapView.onDestroy();
	}	

}*/