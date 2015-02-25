package net.manhica.verbalautopsy.adapter;

import java.util.ArrayList;
import java.util.List;

import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.model.Household;
import net.manhica.verbalautopsy.model.Neighborhood;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HouseholdArrayAdapter extends ArrayAdapter<Household> {

	private List<Household> households;
	private Context mContext;
	
	public HouseholdArrayAdapter(Context context, List<Household> objects) {
		super(context, R.layout.household_item, objects);
		households = new ArrayList<Household>();
		households.addAll(objects);
		
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.household_item, parent, false);
		
		TextView txtHouseNro = (TextView) rowView.findViewById(R.id.txtHousenumber);
		TextView txtHouseExtId = (TextView) rowView.findViewById(R.id.txtHouseExtID);
		
		Household hh = households.get(position);
		
		txtHouseNro.setText(hh.getNumber());
		txtHouseExtId.setText(hh.getExtId());
		
		return rowView;
	}	
	
	@Override
	public Household getItem(int position) {
		// TODO Auto-generated method stub
		return households.get(position);
	}
	
}
