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

import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.datepicker.client.DatePicker;
import org.eesgmbh.gimv.client.presenter.CalendarPresenter.View;

import java.util.Date;

/**
 * An implementation of {@link View} with an internal {@link DatePicker}.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class CalendarViewImpl implements View {

	private final DatePicker datePicker;

	public CalendarViewImpl(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	public void setDate(Date date) {
		this.datePicker.setValue(date, false);
		this.datePicker.setCurrentMonth(date);
	}

	public void disableDate(Date date) {
		this.datePicker.setTransientEnabledOnDates(false, date);
	}

	public void addShowRangeHandler(ShowRangeHandler<Date> showRangeHandler) {
		this.datePicker.addShowRangeHandler(showRangeHandler);
	}

	public void addValueChangeHandler(ValueChangeHandler<Date> valueChangeHandler) {
		this.datePicker.addValueChangeHandler(valueChangeHandler);
	}
}
