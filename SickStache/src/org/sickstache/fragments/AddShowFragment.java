/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickstache.fragments;

import java.util.EnumSet;

import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.LanguageEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.HomeActivity;
import org.sickstache.R;
import org.sickstache.dialogs.QualityDialog;
import org.sickstache.dialogs.StatusDialog;
import org.sickstache.helper.Preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class AddShowFragment extends SherlockFragment {
	
	private class SearchParams {
		public String tvdbid;
		public LanguageEnum langauge;
		public Boolean seasonFolder;
		public StatusEnum status;
		public EnumSet<QualityEnum> initial;
		public EnumSet<QualityEnum> archive;
		
		public SearchParams( String tvdbid, LanguageEnum lang, Boolean season, StatusEnum status,
				EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive) {
			this.tvdbid = tvdbid;
			this.langauge = lang;
			this.seasonFolder = season;
			this.status = status;
			this.initial = initial;
			this.archive = archive;
		}
	}
	protected Dialog working;
	
	public TextView showTextView;
//	public TextView directoryTextView;
	public TextView languageTextView;
	public TextView seasonFolderTextView;
	public TextView statusTextView;
	public TextView qualityTextView;
//	public TextView initialQualityTextView;
//	public TextView archiveQualityTextView;
	
	public LanguageEnum language = null;
	public Boolean seasonFolder = null;
	public StatusEnum status = null;
	public EnumSet<QualityEnum> initialQuality = EnumSet.noneOf(QualityEnum.class);
	public EnumSet<QualityEnum> archiveQuality = EnumSet.noneOf(QualityEnum.class);
	
	public Button addButton;
	
	protected String tvdbid;
	protected String showName;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent intent = this.getActivity().getIntent();
		tvdbid = intent.getExtras().getString("tvdbid");
		showName = intent.getExtras().getString("show");
		try {
			language = LanguageEnum.valueOf(intent.getExtras().getString("lang"));
		} catch (Exception e) { language = null; }
		showTextView.setText(showName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.add_show_fragment, container, false);
		showTextView = (TextView)root.findViewById(R.id.showTextView);
//		directoryTextView = (TextView)root.findViewById(R.id.directoryTextView);
//		directoryTextView.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				final CharSequence[] items = { "Default" };
//				
//				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
//				builder.setTitle("Select Directory");
//				builder.setItems(items, new DialogInterface.OnClickListener() {
//				    public void onClick(DialogInterface dialog, int item) {
//				    	
//				    }
//				});
//			}
//		});
//		languageTextView = (TextView)root.findViewById(R.id.languageTextView);
//		languageTextView.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO probably should just make this once and save it
//				String[] items = LanguageEnum.valuesToString();
////				String[] itemsNDefault = new String[items.length+1];
////				itemsNDefault[0] = "Default";
////				for ( int i=0; i < items.length; i++ ) {
////					itemsNDefault[i+1] = items[i];
////				}
//				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
//				builder.setTitle("Select Language");
//				builder.setItems(items, new DialogInterface.OnClickListener() {
//				    public void onClick(DialogInterface dialog, int item) {
//				    	language = LanguageEnum.fromOrdinal(item);
//				    }
//				});
//				builder.show();
//			}
//		});
		statusTextView = (TextView)root.findViewById(R.id.statusTextView);
		statusTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO probably should just make this once and save it
				final StatusDialog sDialog = new StatusDialog();
				sDialog.setTitle("Set Initial Status");
				sDialog.setOnListClick( new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	status = sDialog.getStatus(item);
				    }
				});
				sDialog.show(getFragmentManager(), "status");
			}
		});
		seasonFolderTextView = (TextView)root.findViewById(R.id.seasonFolderTextView);
		seasonFolderTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( seasonFolder == null )
					seasonFolder = true;
				else
					seasonFolder = !seasonFolder;
				if ( seasonFolder != null && seasonFolder)
					seasonFolderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_on, 0);
				else
					seasonFolderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_off, 0);
			}
		});
		qualityTextView = (TextView)root.findViewById(R.id.qualityTextView);
		qualityTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final QualityDialog qDialog = new QualityDialog();
				qDialog.setTitle("Select Initial Quality");
				qDialog.setOnListClick( new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which ) {
						initialQuality = qDialog.getInitialQuality();
						archiveQuality = qDialog.getArchiveQuality();
					}
				});
				qDialog.show(getFragmentManager(), "quality");
			}
		});
//		initialQualityTextView = (TextView)root.findViewById(R.id.initialQualityTextView);
//		initialQualityTextView.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				String[] items = QualityEnum.valuesToString();
//				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
//				builder.setTitle("Select Initial Quality");
//				builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//						if ( isChecked ) {
//							initialQuality.add(QualityEnum.fromOrdinal(which));
//						} else {
//							initialQuality.remove(QualityEnum.fromOrdinal(which));
//						}
//					}
//				});
//				builder.show();
//			}
//		});
//		archiveQualityTextView = (TextView)root.findViewById(R.id.archiveQualityTextView);
//		archiveQualityTextView.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				String[] rawItems = QualityEnum.valuesToString();
//				String[] items = new String[6];
//				for ( int i=0; i < items.length; i++ )
//					items[i] = rawItems[i+1];
//				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
//				builder.setTitle("Select Archive Quality");
//				builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//						if ( which == 0 )
//							return;
//						if ( isChecked ) {
//							archiveQuality.add(QualityEnum.fromOrdinal(which+1));
//						} else {
//							archiveQuality.remove(QualityEnum.fromOrdinal(which+1));
//						}
//					}
//				});
//				builder.show();
//			}
//		});
		addButton = (Button)root.findViewById(R.id.addButton);
		addButton.setOnClickListener( new OnClickListener() {
			  public void onClick(View v) {
			    onAddShowClick(v);
			  }
			});
		return root;
	}

	@Override
	public void onDestroyView() {
		if ( working != null ) {
			working.dismiss();
		}
		super.onDestroyView();
	}

	public void onAddShowClick(View v)
	{
		// put up a loading dialog
		working = ProgressDialog.show(this.getActivity(), "Adding Show", "Adding Show to SickBeard ...", true);
		// when this downloader returns display the show
		Downloader downloader = new Downloader();
		downloader.execute(new SearchParams(this.tvdbid,
				this.language,
				this.seasonFolder,
				this.status,
				this.initialQuality,
				this.archiveQuality));
	}
	
	private class Downloader extends AsyncTask<SearchParams, Void, Boolean> {

    	public Exception error = null;
    	
    	@Override
    	protected Boolean doInBackground(SearchParams... arg0) {
    		try {
    			SearchParams params = arg0[0];
    			return Preferences.singleton.getSickBeard().showAddNew(params.tvdbid,
    					params.langauge,
    					params.seasonFolder,
    					params.status,
    					params.initial,
    					params.archive);
    		} catch (Exception e) {
    			error = e;
    		}
    		return null;
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		if ( AddShowFragment.this != null ) {
    			// finished loading
				working.dismiss();
    			// if we have a error
    			if ( result != null && result == true ) {
    				// we want the activity to be recreated so no SINGLE_TOP
    				Intent intent = new Intent( AddShowFragment.this.getActivity(), HomeActivity.class );
    	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    	            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	            // this works to fast for sickbeard to refresh so just let the user scroll to it
//    				intent.putExtra("activity", "ShowActivity");
//    				intent.putExtra("tvdbid", tvdbid);
    				startActivity(intent);
    			} else {
    				AlertDialog.Builder builder = new AlertDialog.Builder( AddShowFragment.this.getSherlockActivity() );
    				builder.setTitle("Error Adding Show");
    				builder.setIcon(android.R.drawable.ic_dialog_alert);
    				builder.setMessage("Unable to add show to sickbeard. Check settings and try again.");
    				builder.setPositiveButton(R.string.ok, null);
    				builder.show();
    			}
    		}
    	}
    }
	
}
