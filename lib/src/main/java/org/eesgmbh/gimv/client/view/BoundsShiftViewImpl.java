/*
 * Copyright 2022 EES GmbH
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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Widget;
import org.eesgmbh.gimv.client.presenter.BoundsShiftPresenter.View;

/**
 * A trivial implementation of the {@link View} interface
 * that contains a {@link Widget} that implements the {@link HasClickHandlers}
 * interface.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class BoundsShiftViewImpl implements View {

	private final HasClickHandlers widget;

	public BoundsShiftViewImpl(HasClickHandlers widget) {
		this.widget = widget;
	}

	public void addClickHandler(ClickHandler clickHandler) {
		this.widget.addClickHandler(clickHandler);
	}
}
