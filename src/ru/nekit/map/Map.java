package ru.nekit.map;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.util.constants.OverlayConstants;

import ru.nekit.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.TextView;

public class Map extends Activity 
{

	protected static GeoPoint gPoint = new GeoPoint(43.0884226,131.9228857);
	public static Resources res;
	protected MapView mOSMap;
	protected MyLocationOverlay mLocationOverlay;
	private NewItemizedOverlaywithFocus<NewOSMOverlayItem> mStationOverlay;
	private ArrayList<NewOSMOverlayItem> stations;
	private ResourceProxy mResourceProxy;
	private TextView notificationTextView;
	private MapType mMapType;
	private boolean enableMyLocation = true;

	public static void setGpoint (IGeoPoint gin){
		GeoPoint g = (GeoPoint)gin;
		System.out.println("new gpoint: " + g.toDoubleString());
		gPoint = g;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mMapType = loadLastUsedMapType();
		setContentView(R.layout.mapview);
		mOSMap = (MapView) findViewById(R.id.mapview);
		mOSMap.setTileSource(mMapType.getMapCode());
		mOSMap.setBuiltInZoomControls(true);
		mOSMap.setMultiTouchControls(true);
		mOSMap.getController().setZoom(16);
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
		mLocationOverlay = new MyLocationOverlay(this, mOSMap, mResourceProxy);
		if (enableMyLocation){
			mLocationOverlay.enableMyLocation();
			mLocationOverlay.enableFollowLocation();
		}
		mOSMap.getOverlays().add(mLocationOverlay);
		readAndDrawStations ();
	}


	class ResourceProxyImpl extends DefaultResourceProxyImpl {

		private final Context mContext;

		public ResourceProxyImpl(final Context pContext) {
			super(pContext);
			mContext = pContext;
		}

		@Override
		public String getString(string pResId) {
			switch(pResId) {
			case vl : return "vl";
			case mapnik : return mContext.getString(R.string.mapnik);
			case cyclemap : return mContext.getString(R.string.cyclemap);
			case base : return mContext.getString(R.string.base);
			case topo : return mContext.getString(R.string.topo);
			case hills : return mContext.getString(R.string.hills);
			case cloudmade_small : return mContext.getString(R.string.cloudmade_small);
			case cloudmade_standard : return mContext.getString(R.string.cloudmade_standard);
			default : return super.getString(pResId);
			}
		}

		@Override
		public Bitmap getBitmap(bitmap pResId) {
			switch(pResId) {
			case center : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.center);
			case direction_arrow : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.direction_arrow);
			case marker_default : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker_default);
			case marker_default_focused_base : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker_default_focused_base);
			case navto_small : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.navto_small);
			case next : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.next);
			case person : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.person);
			case previous : return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.previous);
			default : return super.getBitmap(pResId);
			}
		}

		@Override
		public Drawable getDrawable(bitmap pResId) {
			switch(pResId) {
			case center : return mContext.getResources().getDrawable(R.drawable.center);
			case direction_arrow : return mContext.getResources().getDrawable(R.drawable.direction_arrow);
			case marker_default : return mContext.getResources().getDrawable(R.drawable.marker_default);
			case marker_default_focused_base : return mContext.getResources().getDrawable(R.drawable.marker_default_focused_base);
			case navto_small : return mContext.getResources().getDrawable(R.drawable.navto_small);
			case next : return mContext.getResources().getDrawable(R.drawable.next);
			case person : return mContext.getResources().getDrawable(R.drawable.person);
			case previous : return mContext.getResources().getDrawable(R.drawable.previous);
			default : return super.getDrawable(pResId);
			}
		}
	}

	public void readAndDrawStations (){
		res = this.getResources();
		stations =  new ArrayList<NewOSMOverlayItem>();
		stations.add(new NewOSMOverlayItem("Alert!!", "Аврал!!", gPoint = new GeoPoint(43.09,131.923), false));
		mOSMap.getOverlays().remove(mStationOverlay);
		mStationOverlay = new NewItemizedOverlaywithFocus<NewOSMOverlayItem>(
				stations,
				this.getResources().getDrawable(android.R.drawable.ic_menu_mylocation),
				this.getResources().getDrawable(android.R.drawable.ic_menu_mylocation),
				this.getResources().getDrawable(android.R.drawable.ic_menu_mylocation),
				OverlayConstants.NOT_SET,
				OverlayConstants.NOT_SET,
				null,
				mResourceProxy,
				Align.LEFT);

		mStationOverlay.setFocusItemsOnTap(true);
		mOSMap.getOverlays().add(mStationOverlay);
	}

	public void getUserLocation (){
		setEnableMyLocation(true);
		new Thread(new Runnable() {
			public void run() {
				for (int i = 0 ; i < 30 ; i++){
					GeoPoint loc = mLocationOverlay.getMyLocation();
					if (null!=loc){
						gPoint = loc;
						runOnUiThread(point);
						return ;
					}
					else {
						Log.d("getUserLocation","location was not known, attempt " + i);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
				runOnUiThread(point);
			}
		}).start();

	}

	Runnable point = new Runnable() {
		public void run() {
			setMapCenter(gPoint);
		}
	};

	public void setMapCenter(final GeoPoint geoPoint)
	{
		mOSMap.getController().setCenter(geoPoint);
		mOSMap.getController().setZoom(16);
		setGpoint(geoPoint);
	}

	@Override
	public void onPause() 
	{
		mLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	public void startManagingCursor(Cursor c) {
		if (c == null) {
			throw new IllegalStateException("cannot manage cursor: cursor == null");
		}
		super.startManagingCursor(c);
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
		{
			setMapCenter(gPoint);
		}
	}

	@Override 
	public void onSaveInstanceState(Bundle outState) 
	{
		outState.putString("notificationTextView", (String)notificationTextView.getText());
		super.onSaveInstanceState(outState); 
	}

	int getPixelZoomLevel() {
		return mOSMap.getZoomLevel();
	}

	private MapType loadLastUsedMapType() 
	{
		return   MapType.vl;
	}

	public boolean isEnableMyLocation() 

	{
		return enableMyLocation;
	}

	public void setEnableMyLocation(boolean enableMyLocation) {
		this.enableMyLocation = enableMyLocation;
	} 

	public static enum MapType {
		vl(R.string.choice_mapType_vl, TileSourceFactory.VL);

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