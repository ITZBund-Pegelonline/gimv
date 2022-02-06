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

import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This presenter changes the bounds according to some sort of calendar.
 *
 * <p>After instantiating the presenter, the domain bound (left, right, bottom, top) must be assigned
 * with {@link #configureBound(Bound)}. The corresponding axis (horizontal or vertical) must represent
 * a date range in milliseconds.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * 	<li> {@link SetMaxDomainBoundsEvent} (optional, if received the selectable dates will be restricted accordingly,
 * 		 see {@link View#addShowRangeHandler(ShowRangeHandler)})
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent}
 * 	<li> {@link LoadImageDataEvent}
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class CalendarPresenter {

	/**
	 * The View interface of this presenter.
	 *
	 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
	 */
	public interface View {
		void setDate(Date date);
		void disableDate(Date date);

		/**
		 * This is where the presenter gets notified of a user date change.
		 *
		 * @param valueChangeHandler
		 */
		void addValueChangeHandler(ValueChangeHandler<Date> valueChangeHandler);

		/**
		 * This is where the presenter gets notified of all shown
		 * dates.
		 *
		 * @param showRangeHandler
		 */
		void addShowRangeHandler(ShowRangeHandler<Date> showRangeHandler);
	}

	private final HandlerManager handlerManager;
	private final View view;

	private boolean fireLoadImageDataEvent;
	private Bound boundToChange;

	private SetMaxDomainBoundsEvent currentMaxDomainBoundsEvent;
	private Bounds currentDomainBounds;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view A {@link View} implementation
	 */
	public CalendarPresenter(HandlerManager handlerManager, View view) {
		this.handlerManager = handlerManager;
		this.view = view;

		CalendarPresenterEventHandler eventHandler = new CalendarPresenterEventHandler();

		this.view.addShowRangeHandler(eventHandler);
		this.view.addValueChangeHandler(eventHandler);

		handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);

		setFireLoadImageDataEvent(true);
	}

	/**
	 * This method will assign the domain bound (left, right, bottom, top) to the presenter.
	 *
	 * <p>If for example {@link Bound#LEFT} is specified, the left bound of the domain bounds
	 * will be changed by the presenter.
	 *
	 * <p>Note that the corresponding axis (vertical or horizontal) must represent date ranges
	 * in milliseconds. If e.g. bound is {@link Bound#LEFT} the horizonal axis must in milliseconds.
	 *
	 * @param bound An enum value of {@link Bound}
	 */
	public void configureBound(Bound bound) {
		this.boundToChange = bound;
	}

	/**
	 * <p>Defines, whether a {@link LoadImageDataEvent} is fired.
	 *
	 * <p>Default is true.
	 *
	 * @param fireLoadImageDataEvent fire it, or not
	 */
	public void setFireLoadImageDataEvent(boolean fireLoadImageDataEvent) {
		this.fireLoadImageDataEvent = fireLoadImageDataEvent;
	}

	private void onDateSelected(ValueChangeEvent<Date> event) {
		if (currentDomainBounds != null && boundToChange != null) {
			Date date = event.getValue();

			/*
			 * DatePicker returns the dates at 12:00 o'clock which
			 * is changed here.
			 *
			 * Additionally it is assumed here that time increases from
			 * left to right and from bottom to top.
			 */
			if (boundToChange == Bound.LEFT || boundToChange == Bound.BOTTOM) {
				date = DateUtils.truncateTime(date);
			} else if (boundToChange == Bound.RIGHT || boundToChange == Bound.TOP) {
				date = DateUtils.ceilTime(date);
			}

			//setting the new bounds
			Bounds newBounds = currentDomainBounds;
			if (boundToChange == Bound.LEFT) {
				newBounds = newBounds.setLeft(date.getTime());
			} else if (boundToChange == Bound.RIGHT) {
				newBounds = newBounds.setRight(date.getTime());
			} else if (boundToChange == Bound.TOP) {
				newBounds = newBounds.setTop(date.getTime());
			} else if (boundToChange == Bound.BOTTOM) {
				newBounds = newBounds.setBottom(date.getTime());
			}

			//firing events if legal and anything has changed
			if (legalBounds(boundToChange, newBounds) && !newBounds.equals(currentDomainBounds)) {
				handlerManager.fireEvent(new SetDomainBoundsEvent(newBounds));

				if (fireLoadImageDataEvent) {
					handlerManager.fireEvent(new LoadImageDataEvent());
				}
			}

			//resetting the dates in the view, if illegal
			if (!legalBounds(boundToChange, newBounds)) {
				if (boundToChange == Bound.LEFT) {
					view.setDate(new Date(currentDomainBounds.getLeft().longValue()));
				} else if (boundToChange == Bound.RIGHT) {
					view.setDate(new Date(currentDomainBounds.getRight().longValue()));
				} else if (boundToChange == Bound.TOP) {
					view.setDate(new Date(currentDomainBounds.getTop().longValue()));
				} else if (boundToChange == Bound.BOTTOM) {
					view.setDate(new Date(currentDomainBounds.getBottom().longValue()));
				}
			}
		}
	}

	private boolean legalBounds(Bound boundToChange, Bounds bounds) {
		if (boundToChange == Bound.LEFT || boundToChange == Bound.RIGHT) {
			return bounds.getLeft() <= bounds.getRight();
		} else if (boundToChange == Bound.TOP || boundToChange == Bound.BOTTOM) {
			return bounds.getBottom() <= bounds.getTop();
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Whenever a new calendar is displayed ( a new month ) this method will be invoked
	 * and all dates not contained in {@link CalendarPresenter#currentMaxDomainBoundsEvent} will be
	 * disabled.
	 */
	private void onMonthChange(ShowRangeEvent<Date> event) {
		if (currentMaxDomainBoundsEvent != null) {
			for (Iterator<Date> it = dayIterator(event.getStart(), event.getEnd()); it.hasNext();) {
				Date date = it.next();

				if (!currentMaxDomainBoundsEvent.containsHorizontally(date.getTime())) {
					view.disableDate(date);
				}
			}
		}
	}

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		currentDomainBounds = event.getBounds();

		if (boundToChange == Bound.LEFT) {
			view.setDate(new Date(event.getBounds().getLeft().longValue()));
		} else if (boundToChange == Bound.RIGHT) {
			view.setDate(new Date(event.getBounds().getRight().longValue()));
		} else if (boundToChange == Bound.TOP) {
			view.setDate(new Date(event.getBounds().getTop().longValue()));
		} else if (boundToChange == Bound.BOTTOM) {
			view.setDate(new Date(event.getBounds().getBottom().longValue()));
		}
	}

	private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
		currentMaxDomainBoundsEvent = event;
	}

	@SuppressWarnings("deprecation")
	private Iterator<Date> dayIterator(Date start, Date end) {
		List<Date> dates = new ArrayList<Date>();

		Date currentDate = new Date(start.getTime());
		while (end.getTime() >= currentDate.getTime()) {
			dates.add(new Date(currentDate.getTime()));

			currentDate.setDate(currentDate.getDate() + 1);
		}

		return dates.iterator();
	}

	private class CalendarPresenterEventHandler implements SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler, ShowRangeHandler<Date>, ValueChangeHandler<Date> {
		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			CalendarPresenter.this.onSetDomainBounds(event);
		}

		public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
			CalendarPresenter.this.onSetMaxDomainBounds(event);
		}

		public void onShowRange(ShowRangeEvent<Date> event) {
			CalendarPresenter.this.onMonthChange(event);
		}

		public void onValueChange(ValueChangeEvent<Date> event) {
			CalendarPresenter.this.onDateSelected(event);
		}
	}
}
