/*
 * Copyright 2010 EES GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eesgmbh.gimv.client.view;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ToggleButton;
import org.eesgmbh.gimv.client.presenter.ImageMoveOrZoomToggleButtonPresenter.View;

/**
 * An implementation of {@link View} that uses two {@link ToggleButton}.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ImageMoveOrZoomToggleButtonViewImpl implements View {

	private final ToggleButton moveToggleButton;
	private final ToggleButton zoomToggleButton;

	/**
	 * Instantiates the view impl.
	 *
	 * @param moveToggleButton
	 * @param zoomToggleButton
	 */
	public ImageMoveOrZoomToggleButtonViewImpl(ToggleButton moveToggleButton, ToggleButton zoomToggleButton) {
		this.moveToggleButton = moveToggleButton;
		this.zoomToggleButton = zoomToggleButton;
	}

	public void addMoveClickHandler(ClickHandler clickHandler) {
		this.moveToggleButton.addClickHandler(clickHandler);
	}

	public void addZoomClickHandler(ClickHandler clickHandler) {
		this.zoomToggleButton.addClickHandler(clickHandler);
	}

	public void toggleMove() {
		this.zoomToggleButton.setDown(false);
		this.moveToggleButton.setDown(true); //force down, otherwise the button will be up after a second click on the 'downed' button
	}

	public void toggleZoom() {
		this.moveToggleButton.setDown(false);
		this.zoomToggleButton.setDown(true); //force down, otherwise the button will be up after a second click on the 'downed' button
	}

}
