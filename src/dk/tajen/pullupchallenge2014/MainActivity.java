package dk.tajen.pullupchallenge2014;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	// private AdView adView;
	private PullUpDataBase DataSource;

	static final int Goal = 10001;
	static final int DaysTotal = 365;

	public static List<RegistrationElement> values;

	// Views
	TextView txtTotalPullups;
	TextView txtAverage;
	TextView txtAverageNeeded;
	TextView txtDaysLeft;
	TextView txtDaysGone;
	TextView txtAvgCount;
	TextView txtDifference;
	GraphLayout graphLayout;

	public static int TotalPullups = 0;
	public static int DaysGone = 0;
	public static int DaysLeft = 0;
	public static float AverageSoFar = 0.0f;
	public static float NeededAverage = 0.0f;
	public static int AverageCount = 0;
	public static int DifferenceCount = 0;

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
		txtAvgCount = (TextView) findViewById(R.id.txtAvgCount);
		txtDifference = (TextView) findViewById(R.id.txtDifference);
		graphLayout = (GraphLayout) findViewById(R.id.chart);
		
		values = DataSource.GetAllPullUpSets();
		
		TotalPullups = 0;
		for (RegistrationElement element : values) {
			TotalPullups += element.Count;
		}
		
		// Reklame
		AdView adView = (AdView) this.findViewById(R.id.adView);
		// Initiate a generic request.
		AdRequest adRequest = new AdRequest.Builder().build();
		// Load the adView with the ad request.
		adView.loadAd(adRequest);

		//Recalculate();
		//graphLayout.drawGraphSeries();

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

	@Override
	protected void onResume() {
		super.onResume();
		Recalculate();
		graphLayout.redrawGraph();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	public void add_onClick(View view) {
		// save the new comment to the database
		EditText _count = (EditText) findViewById(R.id.txtPullupCount);
		int Count = 0;
		try {
			Count = Integer.parseInt(_count.getText().toString());
		} catch (Exception ex) {
			Toast.makeText(this, "Fejl i indtastning", Toast.LENGTH_SHORT).show();
		}
		TotalPullups += Count;

		Date nu = Calendar.getInstance().getTime();
		RegistrationElement element = new RegistrationElement(Count, nu);
		_count.setText("");
		DataSource.AddPullUpSet(element);

		Recalculate();
		graphLayout.addPoint(element);
	}

	public void Recalculate() {
		
		txtTotalPullups.setText("Status " + String.valueOf(TotalPullups) + "/"
				+ String.valueOf(Goal));

		DaysLeft = DaysTotal - (Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		DaysGone = DaysTotal - DaysLeft;
		txtDaysGone.setText("Days Gone: " + String.valueOf(DaysGone));
		txtDaysLeft.setText("Days Left: " + String.valueOf(DaysLeft));

		AverageSoFar = (float) TotalPullups / (float) DaysGone;
		NeededAverage = (float) (Goal - TotalPullups) / (float) DaysLeft;
		txtAverage.setText("Avg.: "
				+ String.format(Locale.getDefault(), "%.2f", AverageSoFar));
		txtAverageNeeded.setText("Needed Avg.: "
				+ String.format(Locale.getDefault(), "%.2f", NeededAverage));
		
		AverageCount = (int)(DaysGone * (Goal/365.0f));
		txtAvgCount.setText("Ideal count: " + String.valueOf(AverageCount));
		
		DifferenceCount = (int)(TotalPullups - AverageCount);
		txtDifference.setText("Diffenrence: " + String.valueOf(DifferenceCount));
	}
}
