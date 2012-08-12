package com.pd.map;

import java.util.ArrayList;

import microsoft.mappoint.TileSystem;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.util.constants.OverlayConstants;

import com.pd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class Map extends Activity 
{

	/*
	 * STATIC
	 */
	protected static GeoPoint gPoint = new GeoPoint(32.08117,34.780172); //default location: kikar rabin, TA
	public static void setGpoint (GeoPoint g){
		System.out.println("new gpoint: " + g.toDoubleString());
		gPoint = g;
	}
	
	public static Resources res;

	
	private static final String TAG = Map.class.getName();
	private static final int DIALOG_ABOUT = 2;
	private static final int DIALOG_MAP_CHOOSE = 3;
	
	
	/*
	 * Fields 
	 */
	protected MapView mOSMap;
	protected MyLocationOverlay mLocationOverlay;
	private NewItemizedOverlaywithFocus<NewOSMOverlayItem> mStationOverlay;
//	public newItemizedOverlay<OpenStreetMapViewOverlayItem> mTempOverlay;
	private ArrayList<NewOSMOverlayItem> stations;
	private ResourceProxy mResourceProxy;
	
    private TextView notificationTextView;
    
    private MapType mMapType;

    private Handler toastMakerHandler=new Handler(){
		public void handleMessage(Message m){
		   Toast.makeText(Map.this,  getString(m.arg1), Toast.LENGTH_SHORT).show();
		}
    };
	private boolean enableMyLocation = true;
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Log.i (TAG,"===================== MAP Activity START =====================");
        
        mMapType = loadLastUsedMapType();
        
        
/*
 * INIT MAPVIEW
 */    
    setContentView(R.layout.mapview);
    //getting the view
    mOSMap = (MapView) findViewById(R.id.mapview);
    //create the map view that you will use. 
    mOSMap.setTileSource(mMapType.getMapCode());
    //set built in zoom handles.   
    mOSMap.setBuiltInZoomControls(true);
    //set multi-touch to true.
    mOSMap.setMultiTouchControls(true);
    //set zoom (the number was chosen randomly to get you acquainted with this feature).
    mOSMap.getController().setZoom(16);
    //init your resource proxy (the file you created earlier).
    this.mResourceProxy = new ResourceProxyImpl(getApplicationContext());
    mOSMap.setOnTouchListener(new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			setGpoint(mOSMap.getMapCenter());
			return true;
		}
	});
    mOSMap.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			setGpoint(mOSMap.getMapCenter());
		}
	});
   
/*
 * USER LOCATION
 */        
      //create a layer - i created a location layer so that it will track the device's location.
      mLocationOverlay = new MyLocationOverlay(this, mOSMap, mResourceProxy);
        //getting current location
      if (enableMyLocation){
     	 mLocationOverlay.enableMyLocation();
     	 mLocationOverlay.enableFollowLocation();
      }
      //add the layer you created to the map's layers.
      mOSMap.getOverlays().add(mLocationOverlay);



/*
 * Station load and draw
 */
      readAndDrawStations ();


      
          
        
    }
    
	/**
	 * 
	 */
    public void readAndDrawStations (){
        //grab your application's resources to use your drawables (in this case).
        res = this.getResources();
        //create an ArrayList of stations.
        stations =  new ArrayList<NewOSMOverlayItem>();
        stations.add(new NewOSMOverlayItem("No place like 127.0.0.1", "functioning point", gPoint = new GeoPoint(32.08157,34.780192), false));
        stations.add(new NewOSMOverlayItem("No place like 127.0.0.1", "disabled point", gPoint = new GeoPoint(32.08107,34.780132), true));
        //create the overlay with the stations you created earlier and a marker base for the stations (i chose the android icon - very original, i know :)
        mOSMap.getOverlays().remove(mStationOverlay);
        //creating the overlay, 
        mStationOverlay = new NewItemizedOverlaywithFocus<NewOSMOverlayItem>(
    		  stations,
    		  this.getResources().getDrawable(R.drawable.marker_default),
    		  this.getResources().getDrawable(R.drawable.marker_default_yellow),
    		  this.getResources().getDrawable(R.drawable.marker_default_red),
    		  OverlayConstants.NOT_SET,
    		  OverlayConstants.NOT_SET,
    		  null,
    		  mResourceProxy,
    		  Align.LEFT);//alignment of text 
          
        mStationOverlay.setFocusItemsOnTap(true);
        mOSMap.getOverlays().add(mStationOverlay);
    }

    /**
     * finds the user locationg , will try for several seconds. does not run in user thread.
     */
    public void getUserLocation (){
       Toast.makeText(Map.this,  getString(R.string.location_try_fix), Toast.LENGTH_SHORT).show();
       setEnableMyLocation(true);
       setMapCenter(gPoint);
       new Thread(new Runnable() {
		 public void run() {
			for (int i = 0 ; i < 3 ; i++){//num of trial =3 
			   GeoPoint loc = mLocationOverlay.getMyLocation();
			   if (null!=loc){
		        	 setMapCenter(loc);
		        	 return ;
			   }
			   else {
				   Log.d("getUserLocation","location was not known, attempt " + i);
				   try {
					 Thread.sleep(3000); //sleep time 3 sec
				   } catch (InterruptedException e) {e.printStackTrace();}
				   sendIntMessage (R.string.location_try_fix_still,Map.this.returnHandler());
				}
			}
			sendIntMessage (R.string.location_try_fix_no_success,Map.this.returnHandler());
			setMapCenter(gPoint);
		}
	}).start();
       
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        return true;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
        	case DIALOG_MAP_CHOOSE:{
                MapType mapType = loadLastUsedMapType();
        		int i = 0;
        		String[] arr = new String[MapType.values().length];
        		for (MapType value: MapType.values()) {
        			arr[i++] =getString(value.preertyName());
        		}
        		builder.setTitle(this.getString(R.string.choice_mapType)).
        		setSingleChoiceItems(arr, mapType.ordinal(), new  DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
        		        Toast.makeText(getApplicationContext(), getString(R.string.choice_mapType_toast)+getString(MapType.values()[item].preertyName()), Toast.LENGTH_SHORT).show();        		        
        		        MapType mapType = MapType.values()[item];
        		        SharedPreferences settings = getSharedPreferences(getString(R.string.propertyFileName), 0);
        		        SharedPreferences.Editor editor = settings.edit();
        		        editor.putString(getString(R.string.property_curr_maptype), mapType.name());
        		        editor.commit();
        		        mMapType = MapType.values()[item];
        		        mOSMap.setTileSource(mMapType.getMapCode());
        		        dialog.cancel();
        		    }
        		});
        	}break;
	        case DIALOG_ABOUT:
	        	
	        	builder.setMessage(R.string.aboutApp)
	        	       .setCancelable(false)
	        	       .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	           }
	        	       })
	        	       .setPositiveButton(R.string.aboutApp_gotoWebsite, new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( getString(R.string.aboutApp_facebook_link )));
          	        	       startActivity( browse );
	        	           }
	        	       });
	        	
	        	break;
	        default:
	            dialog = null;
        }
        dialog = builder.create();
        return dialog;
    }
    
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.about:
        	showDialog(DIALOG_ABOUT);
            return true;
        case R.id.mapSelection:
        	showDialog(DIALOG_MAP_CHOOSE);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * centers the map to the given point
     * @param geoPoint
     */
	public void setMapCenter(final GeoPoint geoPoint)
    {
    	double aLatitudeE6 = geoPoint.getLatitudeE6();
    	double aLongitudeE6 = geoPoint.getLongitudeE6();
		final Point coords = TileSystem.LatLongToPixelXY(aLatitudeE6 / 1E6, aLongitudeE6 / 1E6,
				mOSMap.getZoomLevel(), null);
		final int worldSize_2 = TileSystem.MapSize(mOSMap.getZoomLevel()) / 2;
		if (mOSMap.getAnimation() == null || mOSMap.getAnimation().hasEnded()) {
			mOSMap.getScroller().startScroll(mOSMap.getScrollX(), mOSMap.getScrollY(),
					coords.x - worldSize_2 - mOSMap.getScrollX(), coords.y - worldSize_2 - mOSMap.getScrollY(),
					500);
			mOSMap.postInvalidate();
		}
		setGpoint(geoPoint);
    }
    
    
    @Override
    public void onPause() 
    {
    	//this feature is turned off when the activity is paused to save battery life.
    	mLocationOverlay.disableMyLocation();
    	super.onPause();
    }
    
    @Override
    public void onResume() 
    {
    	super.onResume();
    	if (enableMyLocation){
	    	mLocationOverlay.enableMyLocation();
	    	mLocationOverlay.enableFollowLocation();
	    	getUserLocation ();
    	}
    	else
            setMapCenter(gPoint);
            
    }
     
	
    @Override 
    public void onSaveInstanceState(Bundle outState) 
    {
        //---save whatever you need to persist—
        outState.putString("notificationTextView", (String)notificationTextView.getText());
        super.onSaveInstanceState(outState); 
    }

/* **************
 * PRIVATE AND SERVICE METHODS
 */
	


	private void sendIntMessage (int arg,Handler hm){
    	Message msg = new Message();
    	msg.arg1=arg;
    	hm.sendMessage(msg);
    }
	
	/**
	 * Get the equivalent zoom level on pixel scale
	 */
	int getPixelZoomLevel() {
		return mOSMap.getZoomLevel();// + mOSMap.getRenderer().MAPTILE_ZOOM;
	}
	
    private MapType loadLastUsedMapType() {
   	 	SharedPreferences settings = this.getSharedPreferences(this.getString(R.string.propertyFileName), 0);
        return  MapType.valueOf(settings.getString(
        		this.getString(R.string.property_curr_maptype), MapType.regular.name()));
		
	}


/* **************
 * GETTERS AND SETTERS
 */
	private Handler returnHandler(){return toastMakerHandler;}
	
	public boolean isEnableMyLocation() {return enableMyLocation;}
	public void setEnableMyLocation(boolean enableMyLocation) {this.enableMyLocation = enableMyLocation;} 
	
	
	public static enum MapType {cycle(R.string.choice_mapType_cycle,TileSourceFactory.CYCLEMAP),regular(R.string.choice_mapType_road,TileSourceFactory.MAPNIK);
		private int pname;
		private OnlineTileSourceBase  tsf;
		private MapType(int s, OnlineTileSourceBase  _tsf){ 
			pname = s ;
			tsf = _tsf;
		}
		public int preertyName() { return pname;};
		public OnlineTileSourceBase getMapCode() { return tsf;};
	}; 

}