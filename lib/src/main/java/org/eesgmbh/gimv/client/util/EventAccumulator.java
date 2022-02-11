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

package org.eesgmbh.gimv.client.util;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eesgmbh.gimv.shared.util.Validate.isPositiveOrZero;
import static org.eesgmbh.gimv.shared.util.Validate.notNull;

/**
 * <p>A utility class which cumulates {@link GwtEvent} instances until for a specified time no
 * further events were added to the accumulator. If e.g. 100 ms is specified as the accumulation
 * period, the callback's execute method is invoked 100ms after the last event was added.
 *
 * <p>A {@link List} of {@link GwtEvent} instances is passed into the {@link Callback}
 * that was specified in the constructor after the accumulation time elapsed.
 *
 * <p>This helps to reduce server load e.g. if mouse wheel events are 'summed up' during the accumulation period.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class EventAccumulator {

	private int callbackExecutionDelay;
	private final EventAccumulatorTimer timer;
	private final Callback callback;

	private final List<GwtEvent<? extends EventHandler>> accumulatedGwtEvents = new ArrayList<GwtEvent<? extends EventHandler>>();

	/**
	 * Constructor.
	 *
	 * @param callbackExecutionDelay
	 * 	How long to wait in milliseconds before invoking the callback's execute method. If 0, GwtEvents are immediately passed to the Callback,
	 * 	thus there is no delay (and no internal use of {@link Timer}).
	 * <p>
	 * @param callback
	 * 	A {@link Callback} implementation whose execute-method will be invoked after the specified delay
	 */
	public EventAccumulator(int callbackExecutionDelay, Callback callback) {
		this.callbackExecutionDelay = (int) isPositiveOrZero(callbackExecutionDelay, "callbackExecutionDelay must be positive or zero.");
		this.callback = notNull(callback, "callback must not be null");

		this.timer = new EventAccumulatorTimer();
	}

	/**
	 * Adds an event that will be passed into the {@link Callback} execute-method after the accumulation period elapsed.
	 *
	 * @param gwtEvent The {@link GwtEvent}
	 */
	public void addEvent(GwtEvent<? extends EventHandler> gwtEvent) {
		//immediatly pass on event
		if (callbackExecutionDelay == 0) {
			callback.excute(new ArrayList<GwtEvent<? extends EventHandler>>(Collections.singletonList(gwtEvent)));

		//peform accumulation
		} else {
			if (timer.elapsed) { //no events for a while
				timer.elapsed = false;
			} else {
				timer.cancel(); //stop the current timer
			}

			timer.schedule(callbackExecutionDelay);

			this.accumulatedGwtEvents.add(gwtEvent);
		}
	}

	/**
	 * How long to wait in milliseconds before invoking the callback's execute method. If 0, GwtEvents are immediately passed to the Callback,
	 * thus there is no delay (and no internal use of {@link Timer}).
	 *
	 * @param callbackExecutionDelay Delay in milliseconds
	 */
	public void setCallbackExecutionDelay(int callbackExecutionDelay) {
		this.callbackExecutionDelay = callbackExecutionDelay;
	}

	private final class EventAccumulatorTimer extends Timer {
		private boolean elapsed = true;

		public void run() {
			elapsed = true;

			callback.excute(new ArrayList<GwtEvent<? extends EventHandler>>(accumulatedGwtEvents));

			accumulatedGwtEvents.clear();
		}
	}

	public static interface Callback {
		void excute(List<GwtEvent<? extends EventHandler>> gwtEvents);
	}
}
