package edu.calpoly.android.walkabout;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay {

	private final List<GeoPoint> m_arrPathPoints;

	private Point m_point;
	private Point m_point2;
	private Paint m_paint;
	private RectF m_rect;

	private static final int START_RADIUS = 10;
	private static final int PATH_WIDTH = 6;

	public PathOverlay(List<GeoPoint> pathPoints) {
		super();
		m_arrPathPoints = pathPoints;
		m_point = new Point();
		m_point2 = new Point();
		m_paint = new Paint();
		m_rect = new RectF();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (m_arrPathPoints.size() != 0) {
			// draw green circle around starting point
			m_paint.setARGB(255, 0, 255, 0);
			Projection p = mapView.getProjection();
			p.toPixels(m_arrPathPoints.get(0), m_point);
			m_rect.set(m_point.x - START_RADIUS, m_point.y - START_RADIUS,
					m_point.x + START_RADIUS, m_point.y + START_RADIUS);
			canvas.drawOval(m_rect, m_paint);

			// draw red lines between points
			m_paint.setStrokeWidth(PATH_WIDTH);
			m_paint.setARGB(255, 255, 0, 0);
			for (GeoPoint geopoint : m_arrPathPoints) {
				p.toPixels(geopoint, m_point2);
				canvas.drawLine(m_point.x, m_point.y, m_point2.x, m_point2.y,
						m_paint);
				m_point.set(m_point2.x, m_point2.y);
			}
		}
	}
}
