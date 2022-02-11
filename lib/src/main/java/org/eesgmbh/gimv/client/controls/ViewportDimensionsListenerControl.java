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

package org.eesgmbh.gimv.client.controls;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.util.EventAccumulator;
import org.eesgmbh.gimv.client.util.EventAccumulator.Callback;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;

import java.util.List;

import static org.eesgmbh.gimv.shared.util.Validate.*;

/**
 * <p>The control listens to the viewport dimensions and will take all necessary steps if the width or the height
 * of the viewport changes.
 *
 * <p>The control is only of use, if the viewport can change its size dynamically because of a user action. Two likely examples are
 * viewport size changes due to a browser window resize or because a {@link SplitLayoutPanel} is used.
 *
 * <p>The control listens to the viewport width and height via a {@link Timer}. The timer runs repeatedly. The period can be specified
 * with {@link #setListeningInterval(int)} but must not be smaller than 50ms (client performance). The default value is 100ms.
 *
 * <p>Whenever changes are detected a {@link SetViewportPixelBoundsEvent} is immediately fired.<br>
 * Optionally a {@link ChangeImagePixelBoundsEvent} is fired, which will cause the displayed image to realign itself with the viewport. This is
 * turned on by default and can be turned off with {@link #setFireSetImagePositionEvent(boolean)}.
 *
 * <p>A {@link LoadImageDataEvent} is fired after a certain delay. The default is 500ms. This means that (possibly expensive) image rendering is
 * only triggered after viewport resizing stopped for 500ms. The value can be changed with {@link #setLoadImageDataEventFiringDelay(int)}.
 *
 * <p>receives no events
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link SetViewportPixelBoundsEvent}
 * 	<li> {@link ChangeImagePixelBoundsEvent}
 * 	<li> {@link LoadImageDataEvent}
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ViewportDimensionsListenerControl {

	private final Viewport viewport;
	private final HandlerManager handlerManager;

	private final Timer viewportDimensionsListeningTimer;

	private EventAccumulator eventAccumulator;

	private boolean fireLoadImageDataEvent;
	private boolean fireSetImagePositionEvent;

	private Bounds currentViewportBounds;

	/**
	 * Constructor.
	 *
	 * @param viewport A {@link Viewport} instance
	 * @param handlerManager A {@link HandlerManager} instance
	 */
	public ViewportDimensionsListenerControl(Viewport viewport, HandlerManager handlerManager) {
		this.viewport = notNull(viewport);
		this.handlerManager = notNull(handlerManager);

		this.currentViewportBounds = new Bounds(
				0, validateDimension(viewport.getOffsetWidth(), "width"),
				0, validateDimension(viewport.getOffsetHeight(), "height"));

		this.viewportDimensionsListeningTimer = new ViewportDimensionsListeningTimer();

		setListeningInterval(100);
		setLoadImageDataEventFiringDelay(500);
		setFireLoadImageDataEvent(true);
		setFireSetImagePositionEvent(true);
	}

	/**
	 * Specifies the listening interval for viewport dimension changes.
	 *
	 * <p>Default is 100ms
	 *
	 * @param intervalInMillis listening interval, at least 50ms
	 */
	public void setListeningInterval(int intervalInMillis) {
		this.viewportDimensionsListeningTimer.cancel();
		this.viewportDimensionsListeningTimer.scheduleRepeating((int) isGreaterThan(intervalInMillis, 49, "The listening interval must be at least 50ms."));
	}

	/**
	 * Sets the time in milliseconds for how long to defer firing a {@link LoadImageDataEvent}.
	 *
	 * <p>The default value is 500 ms. This means that 500 ms have to pass after the last detetected viewport dimension change until a
	 * {@link LoadImageDataEvent} is fired causing the image to be newly rendered.
	 *
	 * <p>This helps to reduce server load where image rendering might take place.
	 *
	 * @param delayInMillis A millisecond value. Pass in 0 (zero) to deactivate delayed firing of {@link LoadImageDataEvent}
	 */
	public void setLoadImageDataEventFiringDelay(int delayInMillis) {
		if (this.eventAccumulator == null) {
			this.eventAccumulator = new EventAccumulator(delayInMillis, new EventAccumulatorCallback());
		} else {
			this.eventAccumulator.setCallbackExecutionDelay(delayInMillis);
		}
	}

	/**
	 * Specify whether the image is immediately repositioned prior to loading the newly
	 * rendered one.
	 *
	 * <p>Default is true.
	 *
	 * @param fireSetImagePositionEvent
	 */
	public void setFireSetImagePositionEvent(boolean fireSetImagePositionEvent) {
		this.fireSetImagePositionEvent = fireSetImagePositionEvent;
	}

	/**
	 * Specify whether a {@link LoadImageDataEvent} is fired.
	 *
	 * <p>Default is true.
	 *
	 * @param fireLoadImageDataEvent fire it, or not
	 */
	public void setFireLoadImageDataEvent(boolean fireLoadImageDataEvent) {
		this.fireLoadImageDataEvent = fireLoadImageDataEvent;
	}

	private int validateDimension(int dimension, String dimensionName) {
		return (int) isPositive(dimension, "The " + dimensionName + " of the viewport must be greater than zero. It is probably not yet attached to the UI.");
	}

	private class ViewportDimensionsListeningTimer extends Timer {
		public void run() {
			Bounds bounds = new Bounds(0, viewport.getOffsetWidth(), 0, viewport.getOffsetHeight());

			//there was a change
			if (!currentViewportBounds.equals(bounds)) {
				//immediatly change viewport bounds
				handlerManager.fireEvent(new SetViewportPixelBoundsEvent(bounds));

				//immediatly change image dimensions
				if (fireSetImagePositionEvent) {
					handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(
							0, 0, bounds.getWidth() - currentViewportBounds.getWidth(), bounds.getHeight() - currentViewportBounds.getHeight()));
				}

				//accumulate
				if (fireLoadImageDataEvent) {
					eventAccumulator.addEvent(new LoadImageDataEvent());
				}

				currentViewportBounds = bounds;
			}
		}
	}

	private class EventAccumulatorCallback implements Callback {
		public void excute(List<GwtEvent<? extends EventHandler>> gwtEvents) {
			handlerManager.fireEvent(new LoadImageDataEvent());
		}
	}
}
