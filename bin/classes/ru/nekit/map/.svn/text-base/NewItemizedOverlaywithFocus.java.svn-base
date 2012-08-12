package com.pd.map;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
/**
 * Itemized overlay that can accept 2 types of object: enabled and disabled. each has it's own drawable.
 * 
 * @author pd
 *
 * @param <T>
 */
public class NewItemizedOverlaywithFocus<T extends NewOSMOverlayItem> extends ItemizedIconOverlay<T> {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int DESCRIPTION_BOX_PADDING = 3;
	public static final int DESCRIPTION_BOX_CORNERWIDTH = 3;

	public static final int DESCRIPTION_LINE_HEIGHT = 12;
	/** Additional to <code>DESCRIPTION_LINE_HEIGHT</code>. */
	public static final int DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT = 2;

	// protected static final Point DEFAULTMARKER_FOCUSED_HOTSPOT = new Point(10, 19);
	protected static final int DEFAULTMARKER_BACKGROUNDCOLOR = Color.rgb(101, 185, 74);
	protected static final int DEFAULTMARKER_DISABLED_BACKGROUNDCOLOR = Color.rgb(185, 0, 0);

	protected static final int DESCRIPTION_MAXWIDTH = 200;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mMarkerFocusedBackgroundColor;
	protected final int mMarkerFocusedDisabledBackgroundColor;
	
	protected final Paint mMarkerBackgroundPaint, mDescriptionPaint, mTitlePaint;

	protected Drawable mMarkerFocusedBase;
	protected Drawable markerDisabled;
	protected int mFocusedItemIndex;
	protected boolean mBoxOpen;
	protected boolean mFocusItemsOnTap;
	private final Point mFocusedScreenCoords = new Point();

	private final String UNKNOWN;
	
	protected Align textAlign;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	public NewItemizedOverlaywithFocus(final Context ctx, final List<T> aList,
			final OnItemGestureListenerImpl<T> aOnItemTapListener) {
		this(aList, aOnItemTapListener, new DefaultResourceProxyImpl(ctx));
	}

	public NewItemizedOverlaywithFocus(final List<T> aList,
			final OnItemGestureListenerImpl<T> aOnItemTapListener, final ResourceProxy pResourceProxy) {
		this(aList, pResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default),null, null,NOT_SET, NOT_SET,
				aOnItemTapListener, pResourceProxy,Align.LEFT);
	}

	public NewItemizedOverlaywithFocus(final List<T> aList, 
			final Drawable pMarker,
			final Drawable pMarkerFocused,
			final Drawable pMarkerDisabled,
			final int pFocusedBackgroundColor,
			final int pFocusedDisabledBackgroundColor,
			final OnItemGestureListenerImpl<T> aOnItemTapListener, 
			final ResourceProxy pResourceProxy,
			final Align ptextAlign) {

		super(aList, pMarker, null, pResourceProxy);
		
		if (null==aOnItemTapListener)
			this.mOnItemGestureListener=new OnItemGestureListenerImpl<T>();
		else
			this.mOnItemGestureListener=aOnItemTapListener;
		
		this.markerDisabled = (pMarkerDisabled != null) ? pMarkerDisabled : mResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default_focused_base);
		
		UNKNOWN = mResourceProxy.getString(ResourceProxy.string.unknown);

		if (pMarkerFocused == null) {
			this.mMarkerFocusedBase = boundToHotspot(
					mResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default_focused_base),
					HotspotPlace.BOTTOM_CENTER);
		} else
			this.mMarkerFocusedBase = pMarkerFocused;

		if (pFocusedBackgroundColor != NOT_SET)
			this.mMarkerFocusedBackgroundColor=pFocusedBackgroundColor;
		else
			this.mMarkerFocusedBackgroundColor = DEFAULTMARKER_BACKGROUNDCOLOR;
		
		if (pFocusedBackgroundColor != NOT_SET)
			this.mMarkerFocusedDisabledBackgroundColor=pFocusedDisabledBackgroundColor;
		else
			this.mMarkerFocusedDisabledBackgroundColor = DEFAULTMARKER_DISABLED_BACKGROUNDCOLOR;

		this.mMarkerBackgroundPaint = new Paint(); // Color is set in onDraw(...)

		this.mDescriptionPaint = new Paint();
		this.mDescriptionPaint.setAntiAlias(true);
		this.mTitlePaint = new Paint();
		this.mTitlePaint.setFakeBoldText(true);
		this.mTitlePaint.setAntiAlias(true);
		this.unSetFocusedItem();
		
		textAlign = ptextAlign;
		this.mMarkerBackgroundPaint.setTextAlign(textAlign);
		this.mDescriptionPaint.setTextAlign(textAlign);
		this.mTitlePaint.setTextAlign(textAlign);
		this.mBoxOpen = false;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public T getFocusedItem() {
		if (this.mFocusedItemIndex == NOT_SET) {
			return null;
		}
		return (T) this.mItemList.get(this.mFocusedItemIndex);
	}

	public void setFocusedItem(final int pIndex) {
		this.mFocusedItemIndex = pIndex;
	}

	public void unSetFocusedItem() {
		this.mFocusedItemIndex = NOT_SET;
	}

	public void setFocusedItem(final T pItem) {
		final int indexFound = super.mItemList.indexOf(pItem);
		if (indexFound < 0) {
			throw new IllegalArgumentException();
		}

		this.setFocusedItem(indexFound);
	}

	public void setFocusItemsOnTap(final boolean doit) {
		this.mFocusItemsOnTap = doit;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	
	
	@Override
	protected boolean onSingleTapUpHelper(final int index, final T item, final MapView mapView) {
		if (this.mFocusItemsOnTap) {
			this.mFocusedItemIndex = index;
			mapView.postInvalidate();
		}
		return this.mOnItemGestureListener.onItemSingleTapUp(index, item);
	}

	private final Rect mRect = new Rect();

	@Override
	public void draw(final Canvas c, final MapView osmv, final boolean shadow) {

		super.draw(c, osmv, shadow);

		if (shadow) {
			return;
		}
		
		if (this.mFocusedItemIndex == NOT_SET) {
			return;
		}

		// get focused item's preferred marker & hotspot
		final T focusedItem = (T) super.mItemList.get(this.mFocusedItemIndex);
		Drawable markerFocusedBase = focusedItem.getMarker(OverlayItem.ITEM_STATE_FOCUSED_MASK);
		if (markerFocusedBase == null) {
			markerFocusedBase = this.mMarkerFocusedBase;
		}

		/* Calculate and set the bounds of the marker. */
		osmv.getProjection().toMapPixels(focusedItem.mGeoPoint, mFocusedScreenCoords);

		markerFocusedBase.copyBounds(mRect);
		mRect.offset(mFocusedScreenCoords.x, mFocusedScreenCoords.y);
		
		/* Strings of the OverlayItem, we need. */
		final String itemTitle = (focusedItem.mTitle == null) ? UNKNOWN : focusedItem.mTitle;
		final String itemDescription = (focusedItem.mDescription == null) ? UNKNOWN
				: focusedItem.mDescription;

		/*
		 * Store the width needed for each char in the description to a float array. This is pretty
		 * efficient.
		 */
		final float[] widths = new float[itemDescription.length()];
		this.mDescriptionPaint.getTextWidths(itemDescription, widths);

		final StringBuilder sb = new StringBuilder();
		int maxWidth = 0;
		int curLineWidth = 0;
		int lastStop = 0;
		int i;
		int lastwhitespace = 0;
		
		/* Loop through the charwidth array and harshly insert a linebreak,
         * when the width gets bigger than DESCRIPTION_MAXWIDTH. */
        for (i = 0; i < widths.length; i++) {
                if(!Character.isLetter(itemDescription.charAt(i)))
                        lastwhitespace = i;

                float charwidth = widths[i];

                if(curLineWidth + charwidth> DESCRIPTION_MAXWIDTH || itemDescription.charAt(i)=='\n'){
                        if(lastStop == lastwhitespace)
                                i--;
                        else
                                i = lastwhitespace;


                        sb.append(itemDescription.subSequence(lastStop, i));
                        sb.append('\n');

                        lastStop = i;
                        maxWidth = Math.max(maxWidth, curLineWidth);
                        curLineWidth = 0;
                }
                else 
                	curLineWidth += charwidth;
        }
		
		
		
		/* Add the last line to the rest to the buffer. */
		if (i != lastStop) {
			final String rest = itemDescription.substring(lastStop, i);
			maxWidth = Math.max(maxWidth, (int) this.mDescriptionPaint.measureText(rest));
			sb.append(rest);
		}
		final String[] lines = sb.toString().split("\n");

		/*
		 * The title also needs to be taken into consideration for the width calculation.
		 */
		final int titleWidth = (int) this.mDescriptionPaint.measureText(itemTitle);

		maxWidth = Math.max(maxWidth, titleWidth);
		final int descWidth = Math.min(maxWidth, DESCRIPTION_MAXWIDTH);

		/* Calculate the bounds of the Description box that needs to be drawn. */
		final int descBoxLeft = mRect.left - descWidth / 2 - DESCRIPTION_BOX_PADDING
				+ mRect.width() / 2;
		final int descBoxRight = descBoxLeft + descWidth + 2 * DESCRIPTION_BOX_PADDING;
		//compensating for icon's height
		final int descBoxBottom = mRect.bottom - markerFocusedBase.getIntrinsicHeight() - 10;
		final int descBoxTop = descBoxBottom - DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT
				- (lines.length + 1) * DESCRIPTION_LINE_HEIGHT /* +1 because of the title. */
				- 2 * DESCRIPTION_BOX_PADDING;

		/* Twice draw a RoundRect, once in black with 1px as a small border. */
		this.mMarkerBackgroundPaint.setColor(Color.BLACK);
		c.drawRoundRect(new RectF(descBoxLeft - 1, descBoxTop - 1, descBoxRight + 1,
				descBoxBottom + 1), DESCRIPTION_BOX_CORNERWIDTH, DESCRIPTION_BOX_CORNERWIDTH,
				this.mDescriptionPaint);
		//setting background color to disabled color if item is disabled
		this.mMarkerBackgroundPaint.setColor(focusedItem.isDisabled()?
				this.mMarkerFocusedDisabledBackgroundColor:
				this.mMarkerFocusedBackgroundColor);
		
		c.drawRoundRect(new RectF(descBoxLeft, descBoxTop, descBoxRight, descBoxBottom),
				DESCRIPTION_BOX_CORNERWIDTH, DESCRIPTION_BOX_CORNERWIDTH,
				this.mMarkerBackgroundPaint);

		
		final int descLeft;
		if (this.textAlign == Align.LEFT)
			descLeft = descBoxLeft + DESCRIPTION_BOX_PADDING;
		else
    	    descLeft = descBoxRight - DESCRIPTION_BOX_PADDING;
		
		int descTextLineBottom = descBoxBottom - DESCRIPTION_BOX_PADDING;


		/* Draw all the lines of the description. */
		for (int j = lines.length - 1; j >= 0; j--) {
			c.drawText(lines[j].trim(), descLeft, descTextLineBottom, this.mDescriptionPaint);
			descTextLineBottom -= DESCRIPTION_LINE_HEIGHT;
		}
		/* Draw the title. */
		c.drawText(itemTitle, descLeft, descTextLineBottom - DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT,
				this.mTitlePaint);
		c.drawLine(descBoxLeft, descTextLineBottom, descBoxRight, descTextLineBottom,
				mDescriptionPaint);

		/*
		 * Finally draw the marker base. This is done in the end to make it look better.
		 */
		Overlay.drawAt(c, markerFocusedBase, mFocusedScreenCoords.x, mFocusedScreenCoords.y, false);
	}

	@Override
	protected void onDrawItem(Canvas canvas, T item, Point curScreenCoords) {
		final int state = 
			(mDrawFocusedItem && (this.getFocusedItem() == item) ? OverlayItem.ITEM_STATE_FOCUSED_MASK
			: 0);
		final Drawable marker;
    	if (item.isDisabled()){
    		marker =(item.getMarker(state) == null) ? this.markerDisabled : item.getMarker(state);
    	}
    	else {
    		OverlayItem.setState(mDefaultMarker, state);
    		marker = (item.getMarker(state) == null) ? mDefaultMarker : item.getMarker(state);
    	}
		final HotspotPlace hotspot = item.getMarkerHotspot();
		boundToHotspot(marker, hotspot);
		// draw it
		drawAtWithScale(canvas, marker, curScreenCoords.x, curScreenCoords.y, false,10);
		
		
	}
	
	
	private static final Rect sRect = new Rect();

	/**
	 * Convenience method to draw a Drawable at an offset. x and y are pixel coordinates. You can
	 * find appropriate coordinates from latitude/longitude using the MapView.getProjection() method
	 * on the MapView passed to you in draw(Canvas, MapView, boolean).
	 * 
	 * @param shadow
	 *            If true, draw only the drawable's shadow. Otherwise, draw the drawable itself.
	 * @param scale pixels to scale the size of the drawble. postive is shrink (0 is original size)           
	 */
	protected synchronized static void drawAtWithScale(android.graphics.Canvas canvas,
			android.graphics.drawable.Drawable drawable, int x, int y, boolean shadow,int scale) {
		drawable.copyBounds(sRect);
		drawable.setBounds(sRect.left + x + scale, sRect.top + y + scale, sRect.right + x- scale, sRect.bottom + y- scale);
		drawable.draw(canvas); 
		drawable.setBounds(sRect);
	}
	
	/**
	 * Callback for dealing with user clicks on items. will enabled / disable the pop up box.
	 * @author pd
	 *
	 * @param <T2>
	 */
	public class OnItemGestureListenerImpl<T2 extends NewOSMOverlayItem> implements ItemizedIconOverlay.OnItemGestureListener<T2>{
			public boolean onItemSingleTapUp(int index, NewOSMOverlayItem item) {
				if (mFocusedItemIndex == index && mBoxOpen){
					mFocusedItemIndex = NOT_SET;
					mBoxOpen = false;
				}
				else
					mBoxOpen = true;
				System.out.println("Item '" + item.mTitle + "' (index=" + index + ") got single tapped up");
				return true;
			}

			public boolean onItemLongPress(int index, NewOSMOverlayItem item) {
				return false;
			}
	}
}

