package ru.nekit.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class NewOSMOverlayItem extends OverlayItem {

	private boolean disabled;

	public NewOSMOverlayItem(String aTitle, String aDescription,
			GeoPoint aGeoPoint, boolean isDisabled) {
		super(aTitle, aDescription, aGeoPoint);
		disabled = isDisabled;
	}

	public boolean isDisabled(){
		return disabled;
	}



}
