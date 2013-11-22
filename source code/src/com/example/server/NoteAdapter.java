package com.example.server;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter{

	private ArrayList <String> noteList = new ArrayList <String> ();
	private LayoutInflater inflater;
	Context context = null;
	
	public NoteAdapter(Context context, ArrayList <String> noteList) {
		this.noteList = noteList;
		inflater = LayoutInflater.from (context);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		
		return noteList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return noteList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View rowView, ViewGroup parent) {
		// TODO Auto-generated method stub

			ViewHolder holder;
			if (rowView == null)
			{
				rowView = inflater.inflate (R.layout.note_row, null);
				holder = new ViewHolder();
				holder.noteNameView = (TextView) rowView.findViewById (R.id.noteItem);
				holder.noteRemoveButtonView = (ImageButton) rowView.findViewById (R.id.pb_remove);
				rowView.setTag (holder);
				
				holder.noteRemoveButtonView.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                	noteList.remove(position);
	                    notifyDataSetChanged();
	                }               
	            });
			}
			else
				holder = (ViewHolder) rowView.getTag();
			
			
			holder.noteNameView.setText (noteList.get (position));
			
			return rowView;

	}
	static class ViewHolder
	{
		TextView noteNameView;
		ImageButton noteRemoveButtonView;
	}


}
