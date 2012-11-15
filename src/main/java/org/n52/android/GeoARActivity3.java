/**
 * Copyright 2012 52�North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.android;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.n52.android.geoar.R;
import org.n52.android.newdata.CheckList;
import org.n52.android.newdata.CheckList.OnCheckedChangedListener;
import org.n52.android.newdata.DataSourceFragment;
import org.n52.android.newdata.DataSourceHolder;
import org.n52.android.newdata.DataSourceLoader;
import org.n52.android.newdata.DataSourceLoader.OnDataSourcesChangeListener;
import org.n52.android.newdata.Visualization;
import org.n52.android.tracking.camera.RealityCamera;
import org.n52.android.view.GeoARViewPager;
import org.n52.android.view.geoar.ARFragment2;
import org.n52.android.view.map.GeoMapFragment2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

/**
 * Core and only {@link Activity} in this application. Coordinates all its child
 * views, manager classes and inter-view communication. Derived from
 * {@link MapActivity} to utilize a {@link MapView} as child.
 * 
 * Uses an icon from www.androidicons.com
 * 
 * 
 */
public class GeoARActivity3 extends SherlockFragmentActivity {

	private static final int ITEM_REMOVE_DATASOURCE = 0;
	private static final int GROUP_DATASOURCES = 1;

	private class DataSourceChangeListener implements
			OnDataSourcesChangeListener,
			OnCheckedChangedListener<DataSourceHolder> {

		@Override
		public void onCheckedChanged(DataSourceHolder item, boolean newState) {
			invalidateOptionsMenu();
		}

		@Override
		public void onDataSourcesChange() {
			invalidateOptionsMenu();
		}

	}

	private GeoMapFragment2 mapFragment = new GeoMapFragment2();
	private ARFragment2 arFragment = new ARFragment2();
	private DataSourceFragment cbFragment = new DataSourceFragment();

	// private List<GeoARView2> noiseARViews = new ArrayList<GeoARView2>();
	private DataSourceChangeListener dataSourceListener = new DataSourceChangeListener();
	private GeoARViewPager mPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// // Get MeasurementManager from previous instance or create new one
		// Object lastMeasureManager = getLastCustomNonConfigurationInstance();
		// if (lastMeasureManager != null) {
		// measurementManager = (MeasurementManager) lastMeasureManager;
		// } else {
		// measurementManager = DataSourceAdapter.createMeasurementManager();
		// }

		// First time init, create the UI.
		if (savedInstanceState == null) {
			// Fragment newFragment = ViewFragment.newInstance();
			// getSupportFragmentManager().beginTransaction()
			// .add(android.R.id.content, newFragment).commit();

			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.info_use);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.ok, null);
			builder.setTitle(R.string.advice);
			builder.show();
		}

		mPager = (GeoARViewPager) findViewById(R.id.pager);
		mPager.setFragmentManager(getSupportFragmentManager());
		mPager.addFragment(mapFragment);
		mPager.addFragment(arFragment);
		mPager.addFragment(cbFragment);
		mPager.showFragment(mapFragment);

		// Reset camera height if set
		SharedPreferences prefs = getSharedPreferences("NoiseAR", MODE_PRIVATE);
		RealityCamera.setHeight(prefs.getFloat("cameraHeight", 1.6f));

		// if (savedInstanceState != null) {
		//
		// // restore manual positioning
		// // locationHandler.onRestoreInstanceState(savedInstanceState);
		// } else {
		// Builder builder = new AlertDialog.Builder(this);
		// builder.setMessage(R.string.info_use);
		// builder.setCancelable(true);
		// builder.setPositiveButton(R.string.ok, null);
		// builder.setTitle(R.string.advice);
		// builder.show();
		// }

		DataSourceLoader
				.addOnSelectedDataSourcesUpdateListener(dataSourceListener);
		DataSourceLoader.getSelectedDataSources().addOnCheckedChangeListener(
				dataSourceListener);

	}

	// @Override
	// public Object onRetainCustomNonConfigurationInstance() {
	// // Lets measurementManager survive a screen orientation change, so that
	// // no measurements need to get recached
	// return measurementManager;
	// }

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// save manual positioning
		// locationHandler.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences prefs = getSharedPreferences("NoiseAR", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat("cameraHeight", RealityCamera.height);
		editor.commit();

		DataSourceLoader.saveDataSourceSelection();
	}

	@Override
	protected void onDestroy() {
		DataSourceLoader
				.removeOnSelectedDataSourcesUpdateListener(dataSourceListener);
		DataSourceLoader.getSelectedDataSources()
				.removeOnCheckedChangeListener(dataSourceListener);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate common general menu definition
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_general, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		// TODO
		// case R.id.item_filter:
		// // Get current measurement filter
		// MeasurementFilter filter = measurementManager
		// .getMeasurementFilter();
		// if (filter == null) {
		// filter = new MeasurementFilter();
		// }
		// new FilterDialog(this, filter, measurementManager).show();
		// break;
		// case R.id.item_source:
		// // show data sources dialog
		// // TODO
		// // new DataSourceDialog(this, dataSources, measurementManager)
		// new DataSourceDialog(this, null, measurementManager).show();
		// break;
		case R.id.item_ar:
			mPager.showFragment(arFragment);
			return true;

		case R.id.item_map:
			mPager.showFragment(mapFragment);
			return true;

		case R.id.item_filter:
			DataSourceLoader.getSelectedDataSources().iterator().next()
					.createFilterDialog(this);
			return true;

		case R.id.item_selectsources:
			mPager.showFragment(cbFragment);
			return true;

		case R.id.item_about:
			AboutDialog aboutDialog = new AboutDialog(this);
			aboutDialog.setTitle(R.string.about_titel);
			aboutDialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// Create data source selection menu
		MenuItem dataSouceItem = menu.findItem(R.id.item_datasource);

		SubMenu subMenu = dataSouceItem.getSubMenu();
		subMenu.removeGroup(GROUP_DATASOURCES);

		final CheckList<DataSourceHolder> dataSources = DataSourceLoader
				.getSelectedDataSources();
		for (final DataSourceHolder dataSource : dataSources) {
			SubMenu sourceMenu = subMenu.addSubMenu(GROUP_DATASOURCES,
					Menu.NONE, Menu.NONE, dataSource.getName());

			MenuItem menuItem = sourceMenu.getItem();
			menuItem.setCheckable(true);
			menuItem.setChecked(dataSources.isChecked(dataSource));
			menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (!item.isChecked()) {
						// source not enabled -> enable it and consume event
						item.setCheckable(true);
						item.setChecked(true);
						dataSources.checkItem(dataSource, true);
						return true; // TODO ...?
					} else {
						// source already enabled -> default behavior, show
						// submenu
						return false;
					}
				}
			});

			for (Visualization visualization : dataSource.getVisualizations()) {
				sourceMenu.add(visualization.getClass().getSimpleName()); // TODO
			}
			sourceMenu.add(Menu.NONE, ITEM_REMOVE_DATASOURCE, Menu.NONE,
					"Disable Data Source").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							dataSources.checkItem(dataSource, false);
							return true;
						}
					});

		}
		// subMenu.setGroupCheckable(GROUP_DATASOURCES, true, false);

		return super.onPrepareOptionsMenu(menu);
	}

	// private static class ViewFragment extends Fragment {
	// GeoMapFragment2 mapFragment;
	// GeoARFragment2 arFragment;
	//
	// private static ViewFragment instance;
	//
	// private LocationHandler locationHandler;
	// private InfoView infoView;
	//
	// private ImageButton mapARSwitcherButton;
	//
	// private boolean showMap = false;
	//
	// static ViewFragment newInstance() {
	// instance = new ViewFragment();
	// instance.setRetainInstance(true);
	// // instance.measureManager = measurementManager;
	// return instance;
	// }
	//
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View v = inflater.inflate(R.layout.main, container, false);
	//
	// // AR / Map switcher Button
	// mapARSwitcherButton = (ImageButton) v
	// .findViewById(R.id.imageButtonMapARSwitcher);
	// mapARSwitcherButton.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View v) {
	// // showMap = (showMap == true) ? false : true;
	// updateFragmentView();
	// }
	// });
	// return v;
	// }
	//
	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	//
	// FragmentManager fm = getFragmentManager();
	//
	// // find fragments
	// mapFragment = (GeoMapFragment2) fm
	// .findFragmentById(R.id.fragment_view);
	// arFragment = (GeoARFragment2) fm
	// .findFragmentById(R.id.fragment_view2);
	//
	// infoView = (InfoView) getView().findViewById(R.id.infoView);
	// locationHandler = new LocationHandler(getActivity(), infoView);
	// FragmentTransaction f = fm.beginTransaction();
	//
	// if (arFragment == null) {
	// // AugmentedReality Fragment
	// arFragment = new ARFragment2(locationHandler, infoView);
	//
	// arFragment.setLocationHandler(locationHandler);
	// arFragment.setInfoHandler(infoView);
	//
	// f.add(R.id.fragment_view2, arFragment);
	// } else {
	// arFragment.setInfoHandler(infoView);
	// }
	//
	// if (mapFragment == null) {
	// // Map Fragment
	// mapFragment = new GeoMapFragment2();
	//
	// // mapFragment.setLocationHandler(locationHandler);
	// // mapFragment.setInfoHandler(infoView);
	//
	// f.add(R.id.fragment_view, mapFragment);
	// } else {
	// // mapFragment.setInfoHandler(infoView);
	// }
	//
	// f.commit();
	// updateFragmentView();
	// }
	//
	// /**
	// * Sets correct drawable for map/AR switching button
	// */
	// @TargetApi(11)
	// private void updateFragmentView() {
	// showMap = (showMap == true) ? false : true;
	// FragmentTransaction fragmentTransaction = getFragmentManager()
	// .beginTransaction();
	// if (showMap) {
	// getActivity().getActionBar().show();
	// mapARSwitcherButton.setImageResource(R.drawable.ic_menu_phone);
	// fragmentTransaction.hide(arFragment);
	// fragmentTransaction.show(mapFragment);
	// } else {
	// getActivity().getActionBar().hide();
	// mapARSwitcherButton
	// .setImageResource(R.drawable.ic_menu_mapmode);
	// fragmentTransaction.hide(mapFragment);
	// fragmentTransaction.show(arFragment);
	// }
	// fragmentTransaction.commit();
	// }
	// }

}