/**
 * 
 */
package dk.tajen.pullupchallenge2014;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.*;

/**
 * @author Tajen
 * 
 */
public class DatabaseActivity extends ListActivity {

	private PullUpDataBase DataSource;

	ArrayAdapter<RegistrationElement> adapter;
	List<RegistrationElement> values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_row_view);
		
		DataSource = new PullUpDataBase(this);
		values = DataSource.GetAllPullUpSets();

		adapter = new ArrayAdapter<RegistrationElement>(this,android.R.layout.simple_list_item_1, values){

	        @Override
	        public View getView(int position, View convertView,  ViewGroup parent) {
	            View view =super.getView(position, convertView, parent);

	            TextView textView=(TextView) view.findViewById(android.R.id.text1);

	            //YOUR CHOICE OF COLOR
	            textView.setTextColor(Color.WHITE);

	            return view;
	        }
	    };
		// Assign adapter to List
		setListAdapter(adapter);

	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		// ListView Clicked item value
		RegistrationElement element = (RegistrationElement) l.getItemAtPosition(position);
		
		final int entryID = element.Id;
		
		Builder alertDialog =  new Builder(this).setTitle(getString(R.string.database_delete_entry))
				.setMessage(String.valueOf(element.Id) + ": " + element.Registrated.toString())
				.setNegativeButton("Cancel", null)
				.setPositiveButton("OK", new OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						RegistrationElement element = DataSource.GetPullUpSet(entryID);
						DataSource.DeleteSet(element);
						values = DataSource.GetAllPullUpSets();
						adapter.notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), R.string.database_entry_deleted_toast, Toast.LENGTH_SHORT).show();
					}
				});
		
		//alertDialog.setIcon(R.drawable.ic_dialog_alert);
		alertDialog.show();
		
	}

}
