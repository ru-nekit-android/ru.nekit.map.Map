package ru.nekit.map;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

public class NewLocationOverlLay extends MyLocationOverlay 
{
	public Bitmap man;

	public NewLocationOverlLay(Context ctx, MapView mapView) 
	{
		super(ctx, mapView);
	}

	public NewLocationOverlLay(Context ctx, MapView mapView, ResourceProxy pResourceProxy) 
	{
		super(ctx, mapView, pResourceProxy);
		man = pResourceProxy.getBitmap(ResourceProxy.bitmap.person);
	}


	@Override
	public void draw(Canvas c, MapView osmv, boolean shadow) {
		if(Map.gPoint != null)
		{
			int center_x = man.getWidth() / 2;
			int center_y = man.getHeight() / 2;

			float[] values = new float[3];
			float[] mtx = new float[9];
			Matrix direction = new Matrix();
			Point MapCoords = new Point();

			c.getMatrix().getValues(mtx);

			final Projection pj = osmv.getProjection();
			pj.toMapPixels(Map.gPoint, MapCoords);

			direction.setRotate(values[0], center_x, center_y);
			direction.setTranslate(center_x, center_y);
			direction.postScale(1/mtx[Matrix.MSCALE_X], 1/mtx[Matrix.MSCALE_Y]);
			direction.postTranslate(MapCoords.x, MapCoords.y);

			Paint person_paint = new Paint();
			c.drawBitmap(man, direction, person_paint);
		}
		else
			super.draw(c, osmv, shadow);
	}

}
