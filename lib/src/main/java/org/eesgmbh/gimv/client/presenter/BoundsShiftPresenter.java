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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.BoundsShiftViewImpl;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * This presenter changes the bounds of the currently displayed image. The
 * passed in View must have a method to register a {@link ClickHandler}.<br>
 * The underlying widget could just be an {@link Image} that shows an arrow
 * and will shift the image towards a configured direction.<br>
 * The only {@link View} implementation shipped with the framework is {@link BoundsShiftViewImpl}.
 *
 * <p>After instantiating the presenter, the direction and amount of shift is
 * specified with either {@link #configureAbsoluteShift(double, double)} or
 * {@link #configureProportionalShift(double, double)}. The presenter won't do anything if the shift
 * is not configured.
 *
 * <p>By default the image gets immediately repositioned on the client side, prior to requesting a new image
 * to be rendered (see {@link #setFireChangeImagePixelBoundsEvent(boolean)}).
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * 	<li> {@link SetDataAreaPixelBoundsEvent} (optional, client side repositioning won't be accurate
 * 		 if the domain bounds of the image do not cover the complete image)
 * 	<li> {@link SetViewportPixelBoundsEvent} (optional, either {@link SetDataAreaPixelBoundsEvent} or
 * 		 {@link SetViewportPixelBoundsEvent} must be recieced in order for client side repositioning to work)
 * 	<li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be no restriction)
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link ChangeImagePixelBoundsEvent}
 * 	<li> {@link SetDomainBoundsEvent}
 * 	<li> {@link LoadImageDataEvent}
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class BoundsShiftPresenter {

	/**
	 * The View interface for {@link BoundsShiftPresenter}
	 */
	public interface View {
		void addClickHandler(ClickHandler clickHandler);
	}

	private enum ShiftMode {PROPORTIONAL, ABSOLUTE};

	private final HandlerManager handlerManager;

	private ShiftMode shiftMode;
	private double horizontalShift = Double.NaN;
	private double verticalShift = Double.NaN;

	private boolean fireLoadImageDataEvent;
	private boolean fireChangeImagePixelBoundsEvent;

	private SetMaxDomainBoundsEvent currentMaxDomainBounds;
	private Bounds currentDomainBounds;
	private Bounds currentViewportBounds;
	private Bounds currentDataAreaBounds;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view A {@link View} implementation
	 */
	public BoundsShiftPresenter(HandlerManager handlerManager, View view) {
		this.handlerManager = Validate.notNull(handlerManager, "handlerManager must not be null");
		Validate.notNull(view, "view must not be null");

		BoundsShiftPresenterEventHandler eventHandler = new BoundsShiftPresenterEventHandler();
		handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);

		view.addClickHandler(eventHandler);

		setFireChangeImagePixelBoundsEvent(true);
		setFireLoadImageDataEvent(true);
	}

	/**
	 * Configures the presenter to shift the domain bounds by an absolute value.
	 *
	 * <p>For example horizontalShift=100, verticalShift=0 will add 100 to both left and
	 * right and kepp top and bottom unchanged, thus shifting the domain bounds to the right.
	 *
	 * @param horizontalShift An absolute value in the units of the domain bounds (e.g. time or coordinates). Positive, negative or 0.
	 * @param verticalShift An absolute value in the units of the domain bounds (e.g. time or coordinates). Positive, negative or 0.
	 */
	public void configureAbsoluteShift(double horizontalShift, double verticalShift) {
		configureShift(horizontalShift, verticalShift, ShiftMode.ABSOLUTE);
	}

	/**
	 * Configures the presenter to shift the domain bounds by an absolute value.
	 *
	 * <p>For example horizontalShift=0, verticalShift=-0.2 will keep left and right unchanged and
	 * subtract 20% of the total domain bounds from top and bottom, thus shifting the domain bounds to the top.
	 *
	 * @param horizontalShift A proportional value (0 to 1 based). Positive, negative or 0.
	 * @param verticalShift A proportional value (0 to 1 based). Positive, negative or 0.
	 */
	public void configureProportionalShift(double horizontalShift, double verticalShift) {
		configureShift(horizontalShift, verticalShift, ShiftMode.PROPORTIONAL);
	}

	private void configureShift(double horizontalShift, double verticalShift, ShiftMode shiftMode) {
		this.shiftMode = shiftMode;
		this.horizontalShift = horizontalShift;
		this.verticalShift = verticalShift;
	}

	/**
	 * <p>Specify whether the image gets immediately repositioned prior to loading the newly
	 * rendered one.
	 *
	 * <p>Default is true.
	 *
	 * @param fireChangeImagePixelBoundsEvent
	 */
	public void setFireChangeImagePixelBoundsEvent(boolean fireChangeImagePixelBoundsEvent) {
		this.fireChangeImagePixelBoundsEvent = fireChangeImagePixelBoundsEvent;
	}

	/**
	 * <p>Specify whether a {@link LoadImageDataEvent} is fired.
	 *
	 * <p>Default is true.
	 *
	 * @param fireLoadImageDataEvent fire it, or not
	 */
	public void setFireLoadImageDataEvent(boolean fireLoadImageDataEvent) {
		this.fireLoadImageDataEvent = fireLoadImageDataEvent;
	}

	private void shiftBounds() {
		if (currentDomainBounds != null && !Double.isNaN(horizontalShift) && !Double.isNaN(verticalShift)) {
			Bounds newDomainBounds = currentDomainBounds;

			if (currentMaxDomainBounds == null || currentMaxDomainBounds.containsHorizontally(
					shift(newDomainBounds, horizontalShift, 0).getLeft(),
					shift(newDomainBounds, horizontalShift, 0).getRight())) {
				newDomainBounds = shift(newDomainBounds, horizontalShift, 0);
			}

			if (currentMaxDomainBounds == null || currentMaxDomainBounds.containsVertically(
					shift(newDomainBounds, 0, verticalShift).getTop(),
					shift(newDomainBounds, 0, verticalShift).getBottom())) {
				newDomainBounds = shift(newDomainBounds, 0, verticalShift);
			}

			if (!newDomainBounds.equals(currentDomainBounds)) {
				if (fireChangeImagePixelBoundsEvent) {
					if (currentDataAreaBounds != null) {
						Bounds newImageBounds = newDomainBounds.transform(currentDomainBounds, currentDataAreaBounds);

						//the inverse of the change of the domain bounds
						double offsetX = currentDataAreaBounds.getLeft() - newImageBounds.getLeft();
						double offsetY = currentDataAreaBounds.getTop() - newImageBounds.getTop();

						handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(offsetX,offsetY));
					} else if (currentViewportBounds != null) {
						Bounds newImageBounds = newDomainBounds.transform(currentDomainBounds, currentViewportBounds);

						//the inverse of the change of the domain bounds
						double offsetX = currentViewportBounds.getLeft() - newImageBounds.getLeft();
						double offsetY = currentViewportBounds.getTop() - newImageBounds.getTop();

						handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(offsetX,offsetY));
					}
				}

				handlerManager.fireEvent(new SetDomainBoundsEvent(newDomainBounds));

				if (fireLoadImageDataEvent) {
					handlerManager.fireEvent(new LoadImageDataEvent());
				}
			}
		}
	}

	private Bounds shift(Bounds bounds, double horizontal, double vertical) {
		if (shiftMode == ShiftMode.ABSOLUTE) {
			return bounds.shiftAbsolute(horizontal, vertical);
		} else if (shiftMode ==  ShiftMode.PROPORTIONAL) {
			return bounds.shiftProportional(horizontal, vertical);
		} else {
			throw new IllegalStateException();
		}
	}

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		currentDomainBounds = event.getBounds();
	}

	private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
		currentMaxDomainBounds = event;
	}

	private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
		currentViewportBounds = event.getBounds();
	}

	private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
		currentDataAreaBounds = event.getBounds();
	}

	private class BoundsShiftPresenterEventHandler implements SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler, SetViewportPixelBoundsEventHandler, SetDataAreaPixelBoundsEventHandler, ClickHandler {
		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			BoundsShiftPresenter.this.onSetDomainBounds(event);
		}

		public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
			BoundsShiftPresenter.this.onSetMaxDomainBounds(event);
		}

		public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
			BoundsShiftPresenter.this.onSetViewportBounds(event);
		}

		public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
			BoundsShiftPresenter.this.onSetDataAreaBounds(event);
		}

		public void onClick(ClickEvent event) {
			shiftBounds();
		}
	}

}
