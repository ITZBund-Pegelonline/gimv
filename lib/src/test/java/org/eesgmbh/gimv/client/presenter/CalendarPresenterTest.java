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

package org.eesgmbh.gimv.client.presenter;

import static junit.framework.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;


public class CalendarPresenterTest extends AbstractGimvUnitTest {

	private CalendarPresenter presenter;
	private MockView mockView;

	private Calendar left;
	private Calendar right;
	private Calendar top;
	private Calendar bottom;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockView();
			this.presenter = new CalendarPresenter(testHM, mockView);
		}

		left = new GregorianCalendar(2010, 1, 1);
		right = new GregorianCalendar(2010, 2, 1);
		top = new GregorianCalendar(2010, 4, 1);
		bottom = new GregorianCalendar(2010, 3, 1);
	}

	@Test
	public void testSetDateInView() throws Exception {
		assertSetDateInView(null, null);
		assertSetDateInView(left, Bound.LEFT);
		assertSetDateInView(right, Bound.RIGHT);
		assertSetDateInView(top, Bound.TOP);
		assertSetDateInView(bottom, Bound.BOTTOM);
	}

	@Test
	public void testSetDomainBoundsEvent() throws Exception {
		testEH.setDomainBoundsEvent = null;

		mockView.changeDate(new GregorianCalendar(2010, 1, 1));
		assertNull(testEH.setDomainBoundsEvent);

		assertSetDomainBoundsEvent(new GregorianCalendar(2010, 0, 15), new GregorianCalendar(2010, 0, 15, 12, 0), Bound.LEFT);
		assertSetDomainBoundsEvent(ceil(new GregorianCalendar(2010, 3, 15)), new GregorianCalendar(2010, 3, 15, 12, 0), Bound.RIGHT);
		assertSetDomainBoundsEvent(ceil(new GregorianCalendar(2010, 5, 15)), new GregorianCalendar(2010, 5, 15, 12, 0), Bound.TOP);
		assertSetDomainBoundsEvent(new GregorianCalendar(2010, 2, 15), new GregorianCalendar(2010, 2, 15, 12, 0), Bound.BOTTOM);
	}

	@Test
	public void testSetIllegalDomainBounds() throws Exception {
		//illegal
		assertSetDomainBoundsEvent(left, addDay(right), Bound.LEFT);
		assertSetDomainBoundsEvent(right, subtractDay(left), Bound.RIGHT);
		assertSetDomainBoundsEvent(top, subtractDay(bottom), Bound.TOP);
		assertSetDomainBoundsEvent(bottom, addDay(top), Bound.BOTTOM);

		//still legal
		assertSetDomainBoundsEvent(right, right, Bound.LEFT);
		assertSetDomainBoundsEvent(ceil(left), left, Bound.RIGHT);
		assertSetDomainBoundsEvent(ceil(bottom), bottom, Bound.TOP);
		assertSetDomainBoundsEvent(top, top, Bound.BOTTOM);
	}

	@Test
	public void testLoadImageDataEvent() throws Exception {
		testEH.loadImageDataEvent = null;

		//no bounds yet
		mockView.changeDate(new GregorianCalendar(2010, 1, 1));
		assertNull(testEH.loadImageDataEvent);

		//bounds, but should not fire
		presenter.configureBound(Bound.LEFT);
		presenter.setFireLoadImageDataEvent(false);
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(left.getTimeInMillis(), right.getTimeInMillis(), top.getTimeInMillis(), bottom.getTimeInMillis())));
		mockView.changeDate(new GregorianCalendar(2010, 1, 1));
		assertNull(testEH.loadImageDataEvent);

		//now it should fire
		presenter.setFireLoadImageDataEvent(true);
		mockView.changeDate(subtractDay(left));
		assertNotNull(testEH.loadImageDataEvent);
	}

	private void assertSetDateInView(Calendar expected, Bound bound) {
		presenter.configureBound(bound);
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(left.getTimeInMillis(), right.getTimeInMillis(), top.getTimeInMillis(), bottom.getTimeInMillis())));
		assertEquals(expected != null ? expected.getTime() : null, mockView.date);
	}

	private void assertSetDomainBoundsEvent(Calendar expected, Calendar changeTo, Bound bound) {
		presenter.configureBound(bound);

		Bounds initialDomainBounds = new Bounds(left.getTimeInMillis(), right.getTimeInMillis(), top.getTimeInMillis(), bottom.getTimeInMillis());
		testHM.fireEvent(new SetDomainBoundsEvent(initialDomainBounds));
		mockView.changeDate(changeTo);

		Bounds expectedDomainBounds = initialDomainBounds;
		if (bound == Bound.LEFT) {
			expectedDomainBounds = expectedDomainBounds.setLeft(expected.getTimeInMillis());
		} else if (bound == Bound.RIGHT) {
			expectedDomainBounds = expectedDomainBounds.setRight(expected.getTimeInMillis());
		} else if (bound == Bound.TOP) {
			expectedDomainBounds = expectedDomainBounds.setTop(expected.getTimeInMillis());
		} else if (bound == Bound.BOTTOM) {
			expectedDomainBounds = expectedDomainBounds.setBottom(expected.getTimeInMillis());
		}

		assertEquals(expectedDomainBounds, testEH.setDomainBoundsEvent.getBounds());
	}

	private Calendar ceil(Calendar cal) {
		Calendar newCal = (Calendar) cal.clone();

		newCal.set(Calendar.HOUR_OF_DAY, 23);
		newCal.set(Calendar.MINUTE, 59);
		newCal.set(Calendar.SECOND, 59);
		newCal.setTimeInMillis(newCal.getTimeInMillis() + 999 - newCal.getTimeInMillis() % 1000); //Millisekunden auf 999

		return newCal;
	}

	private Calendar addDay(Calendar cal) {
		Calendar newCal = (Calendar) cal.clone();
		newCal.add(Calendar.DAY_OF_MONTH, 1);
		return newCal;
	}

	private Calendar subtractDay(Calendar cal) {
		Calendar newCal = (Calendar) cal.clone();
		newCal.add(Calendar.DAY_OF_MONTH, -1);
		return newCal;
	}

	private class MockView implements CalendarPresenter.View {
		private Date date;
		private ValueChangeHandler<Date> valueChangeHandler;

		private void changeDate(Calendar date) {
			valueChangeHandler.onValueChange(new TestDateChangeEvent(date.getTime()));
		}

		public void disableDate(Date date) {
		}

		public void setDate(Date date) {
			this.date = date;
		}
		public void addShowRangeHandler(ShowRangeHandler<Date> showRangeHandler) {
		}
		public void addValueChangeHandler(ValueChangeHandler<Date> valueChangeHandler) {
			this.valueChangeHandler = valueChangeHandler;
		}
	}

	private class TestDateChangeEvent extends ValueChangeEvent<Date> {
		protected TestDateChangeEvent(Date value) {
			super(value);
		}
	}
}
