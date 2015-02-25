package net.manhica.verbalautopsy.adapter;

import java.util.ArrayList;
import java.util.List;

import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.model.DeadIndividual;
import net.manhica.verbalautopsy.model.Household;
import net.manhica.verbalautopsy.model.Neighborhood;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class IndividualArrayAdapter extends ArrayAdapter<DeadIndividual> {

	private List<DeadIndividual> individuals;
	private Context mContext;
	
	public IndividualArrayAdapter(Context context, List<DeadIndividual> objects) {
		super(context, R.layout.household_item, objects);
		individuals = new ArrayList<DeadIndividual>();
		individuals.addAll(objects);
		
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.individual_item, parent, false);
		
		TextView txtIndvName = (TextView) rowView.findViewById(R.id.txtIndividualName);
		TextView txtIndvPermId = (TextView) rowView.findViewById(R.id.txtIndividualPermId);
		CheckBox chkVBprocessed = (CheckBox) rowView.findViewById(R.id.chkVaProcessed);
		
		DeadIndividual indv = individuals.get(position);
		
		String processed = indv.getVerbalAutopsyProcessed();
		
		txtIndvName.setText(indv.getName());
		txtIndvPermId.setText(indv.getPermId());
				
		chkVBprocessed.setChecked(processed.equalsIgnoreCase("1"));		
		
		return rowView;
	}	
	
	@Override
	public DeadIndividual getItem(int position) {
		// TODO Auto-generated method stub
		return individuals.get(position);
	}
	
}
