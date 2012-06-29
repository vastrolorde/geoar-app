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
package org.n52.android.view.geoar;

import java.util.ArrayList;

import org.n52.android.GeoARView;
import org.n52.android.data.MeasurementManager;
import org.n52.android.geoar.R;
import org.n52.android.tracking.location.LocationHandler;
import org.n52.android.view.GeoARFragment;
import org.n52.android.view.InfoView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class ARFragment extends GeoARFragment {
	
	private AugmentedView augmentedView;
	
	public ARFragment(){
		geoARViews = new ArrayList<GeoARView>();
	}
	
	public ARFragment(MeasurementManager measureManager, LocationHandler locationHandler, InfoView infoView){
		this();
		this.mMeasureManager 	= measureManager;
		this.mInfoHandler 		= infoView;
		this.mLocationHandler 	= locationHandler;
		this.setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_ar, container, false);
		if(savedInstanceState == null){
			augmentedView = (AugmentedView) view.findViewById(R.id.glNoiseView);
			augmentedView.setInfoHandler(mInfoHandler);
			augmentedView.setLocationHandler(mLocationHandler);
			augmentedView.setMeasureManager(mMeasureManager);
			
			// Chart
//			NoiseChartView diagramView = (NoiseChartView) view.findViewById(R.id.noiseDiagramView);
//			diagramView.setNoiseGridValueProvider(augmentedView.getNoiseGridValueProvider());
//			geoARViews.add(diagramView);
			
			// Calibration View
			CalibrationControlView calibrationView = (CalibrationControlView) view.findViewById(R.id.calibrationView);
			geoARViews.add(calibrationView);
		}
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_ar, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
