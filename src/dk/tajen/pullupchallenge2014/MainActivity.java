package dk.tajen.pullupchallenge2014;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.achartengine.*;
import org.achartengine.chart.*;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.*;
import org.achartengine.renderer.*;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.google.android.gms.ads.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	// private AdView adView;
	private PullUpDataBase DataSource;

	static final int Goal = 10001;
	static final int DaysTotal = 365;

	List<RegistrationElement> values;

	// Views
	TextView txtTotalPullups;
	TextView txtAverage;
	TextView txtAverageNeeded;
	TextView txtDaysLeft;
	TextView txtDaysGone;
	LinearLayout graphLayout;

	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private int TotalPullups = 0;
	private int DaysGone = 0;
	private int DaysLeft = 0;
	private float AverageSoFar = 0.0f;
	private float NeededAverage = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataSource = new PullUpDataBase(this);

		txtTotalPullups = (TextView) findViewById(R.id.totalPullups);
		txtAverage = (TextView) findViewById(R.id.txtAverage);
		txtAverageNeeded = (TextView) findViewById(R.id.txtNeededAverage);
		txtDaysGone = (TextView) findViewById(R.id.txtDaysGone);
		txtDaysLeft = (TextView) findViewById(R.id.txtDaysLeft);
		graphLayout = (LinearLayout) findViewById(R.id.chart);

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
		
		mChartView = ChartFactory.getTimeChartView(this, mDataset, mRenderer,"MM/dd");
		graphLayout.addView(mChartView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		values = DataSource.GetAllPullUpSets();

		for (RegistrationElement element : values) {
			TotalPullups += element.Count;
		}

		// Reklame
		AdView adView = (AdView) this.findViewById(R.id.adView);
		// Initiate a generic request.
		AdRequest adRequest = new AdRequest.Builder().build();
		// Load the adView with the ad request.
		adView.loadAd(adRequest);

		Recalculate();
		drawGraphSeries();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.open_database:
			Intent databaseIntent = new Intent(this, DatabaseActivity.class);
			startActivity(databaseIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void drawGraphSeries() {
		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd"); 
		
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
			idealSeries.add((double)start.getTime(), 0.0);
			long dayAfterTomorrow = Calendar.getInstance().getTime().getTime() + 86400000;
			idealSeries.add((double)dayAfterTomorrow, DaysGone * (10001/365));
		} catch (ParseException e) { 
			System.out.println("Unparseable using " + ft); 
		}
		
		// Pull ups graph 
		
		String seriesTitle = "Pull-ups done";
		// create a new series of data
		TimeSeries series = new TimeSeries(seriesTitle);
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
		//mRenderer.setXAxisMin(mRenderer.getXAxisMin() - 1000000);
		mRenderer.setXAxisMax((Calendar.getInstance().getTime().getTime()));
		mRenderer.setYAxisMin(0);
		mRenderer.setAxesColor(Color.WHITE);
		mRenderer.setLabelsColor(Color.WHITE);
	    
		// Add zero point.		
		try { 
			Date t = ft.parse("2014-01-01"); 
			long zeroTime = t.getTime();
			series.add((double)zeroTime, 0.0);
		} catch (ParseException e) { 
			System.out.println("Unparseable using " + ft); 
		}
		
        // add a new data point to the current series
		long _runningSum = 0;
		for (RegistrationElement element : values) {
			_runningSum += element.Count;
			series.add(element.Registrated, _runningSum);
		}
		
        // repaint the chart such as the newly added point to be visible
        mChartView.repaint();
	}

	public void add_onClick(View view) {
		// save the new comment to the database
		EditText _count = (EditText) findViewById(R.id.txtPullupCount);
		int Count = Integer.parseInt(_count.getText().toString());
		TotalPullups += Count;

		Date nu = Calendar.getInstance().getTime();
		RegistrationElement element = new RegistrationElement(Count, nu);
		_count.setText("");
		DataSource.AddPullUpSet(element);

		Recalculate();
	}

	public void Recalculate() {
		txtTotalPullups.setText("Status " + String.valueOf(TotalPullups) + "/" + String.valueOf(Goal));

		DaysLeft = DaysTotal - (Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		DaysGone = DaysTotal - DaysLeft;
		txtDaysGone.setText("Days Gone: " + String.valueOf(DaysGone));
		txtDaysLeft.setText("Days Left: " + String.valueOf(DaysLeft));

		AverageSoFar = (float) TotalPullups / (float) DaysGone;
		NeededAverage = (float) (Goal - TotalPullups) / (float) DaysLeft;
		txtAverage.setText("Avg.: "	+ String.format(Locale.getDefault(), "%.2f", AverageSoFar));
		txtAverageNeeded.setText("Needed Avg.: " + String.format(Locale.getDefault(), "%.2f", NeededAverage));
	}
}
