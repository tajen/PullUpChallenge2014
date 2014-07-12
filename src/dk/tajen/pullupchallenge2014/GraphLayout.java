/**
 * 
 */
package dk.tajen.pullupchallenge2014;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author Tajen
 * 
 */
public class GraphLayout extends LinearLayout {

	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	/** Serien som vises */
	private TimeSeries series;

	/**
	 * @param context
	 * @param attrs
	 */
	public GraphLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGraph();
	}

	/**
	 * @param context
	 */
	public GraphLayout(Context context) {
		super(context);
		initGraph();
	}

	private void initGraph() {
		// set some properties on the main renderer
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(25);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(2);

		mChartView = ChartFactory.getTimeChartView(this.getContext(), mDataset,
				mRenderer, "MM/dd");
		this.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	public void drawGraphSeries() {

		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

		// Guideline
		String idealSeriesTitle = "Ideal line";
		// create a new series of data
		TimeSeries idealSeries = new TimeSeries(idealSeriesTitle);
		mDataset.addSeries(idealSeries);
		// mCurrentSeries = series;
		// create a new renderer for the new series
		XYSeriesRenderer idealRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(idealRenderer);
		// set some renderer properties
		idealRenderer.setPointStyle(PointStyle.CIRCLE);
		idealRenderer.setFillPoints(false);
		idealRenderer.setColor(Color.BLUE);
		idealRenderer.setDisplayChartValues(false);
		idealRenderer.setDisplayChartValuesDistance(25);

		try {
			Date start = ft.parse("2014-01-01");
			idealSeries.add((double) start.getTime(), 0.0);
			long dayAfterTomorrow = Calendar.getInstance().getTime().getTime() + 86400000;
			idealSeries.add((double) dayAfterTomorrow, MainActivity.DaysGone
					* (10001 / 365));
		} catch (ParseException e) {
			System.out.println("Unparseable using " + ft);
		}

		// Pull ups graph

		String seriesTitle = "Pull-ups done";
		// create a new series of data
		series = new TimeSeries(seriesTitle);
		mDataset.addSeries(series);
		// mCurrentSeries = series;
		// create a new renderer for the new series
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		// set some renderer properties
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setColor(Color.GREEN);
		renderer.setDisplayChartValues(true);
		renderer.setDisplayChartValuesDistance(25);
		renderer.setLineWidth(5);

		mRenderer.setXTitle("Date");
		mRenderer.setYTitle("Count");
		mRenderer.setXAxisMax((Calendar.getInstance().getTime().getTime()));
		mRenderer.setYAxisMin(0);
		mRenderer.setAxesColor(Color.WHITE);
		mRenderer.setLabelsColor(Color.WHITE);

		// Add zero point.
		try {
			Date t = ft.parse("2014-01-01");
			long zeroTime = t.getTime();
			series.add((double) zeroTime, 0.0);
		} catch (ParseException e) {
			System.out.println("Unparseable using " + ft);
		}

		// add a new data point to the current series
		long _runningSum = 0;
		for (RegistrationElement element : MainActivity.values) {
			_runningSum += element.Count;
			series.add(element.Registrated, _runningSum);
		}

		// repaint the chart such as the newly added point to be visible
		mChartView.repaint();
	}

	public void clearGraph() {
		mRenderer.removeAllRenderers();
		mDataset.clear();
	}
	
	public void redrawGraph() {
		this.clearGraph();
		this.drawGraphSeries();
	}
	
	public void addPoint(RegistrationElement element) {
		double newValue = series.getY(series.getItemCount()-1);
		series.add(element.Registrated, element.Count + newValue);
	}
}
