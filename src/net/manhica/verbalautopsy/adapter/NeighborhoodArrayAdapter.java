package net.manhica.verbalautopsy.adapter;

import java.util.ArrayList;
import java.util.List;

import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.activity.LoginActivity;
import net.manhica.verbalautopsy.model.Neighborhood;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NeighborhoodArrayAdapter  extends ArrayAdapter<Neighborhood> {

	private List<Neighborhood> neighs;
	private Context mContext;
		
	public NeighborhoodArrayAdapter(Context context, List<Neighborhood> objects) {
		super(context, R.layout.neighborhood_item, objects);
		neighs = new ArrayList<Neighborhood>();
		neighs.addAll(objects);
		
		this.mContext = context;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.neighborhood_item, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.txtHousenumber);		
		
		textView.setText(neighs.get(position).toString());	 
		
		return rowView;
	}	
	
	@Override
	public Neighborhood getItem(int position) {
		// TODO Auto-generated method stub
		return neighs.get(position);
	}
	
}
