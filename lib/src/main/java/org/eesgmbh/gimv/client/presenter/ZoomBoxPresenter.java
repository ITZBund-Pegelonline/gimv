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

package org.eesgmbh.gimv.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.controls.DragImageControl;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.shared.util.Bounds;

/**
 * This presenter enables to zoom the image by mouse dragging
 * on the viewport, thus creating some kind of bounding box.
 *
 * <p>The image will be zoomed to the selection made with the
 * bounding box.<br>
 * Currently only zooming in is supported.
 *
 * <p>The passed in view is the actual visual representation
 * of the bounding box.
 *
 * <p>This presenter must be activated or deactivated with a {@link StateChangeEvent}. The reason behind
 * the mandatory activation is, that dragging can both mean zooming or moving the image. Only
 * this presenter or the {@link DragImageControl} can be active at the same time.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link ViewportDragInProgressEvent} (the presenter changes the position and dimension of the view accordingly)
 * 	<li> {@link ViewportDragFinishedEvent} (will trigger a {@link SetDomainBoundsEvent} and a {@link LoadImageDataEvent} )
 * 	<li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * 	<li> {@link StateChangeEvent} (must be set to move for the control to do something)
 * 	<li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be no restriction)
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} fired after recieving {@link ViewportDragFinishedEvent} with the new domain bounds
 * 	<li> {@link LoadImageDataEvent} fired after recieving {@link ViewportDragFinishedEvent}
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ZoomBoxPresenter {

	private final HandlerManager handlerManager;
	private final GenericWidgetView view;
	private boolean fireLoadImageDataEvent;
	private int minimalDragOffsetInPixel;

	private boolean active;
	private Bounds currentDomainBounds;
	private SetMaxDomainBoundsEvent currentMaxDomainBounds;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view An implementation of {@link GenericWidgetView}. This is the visual representation of the bounding box.
	 */
	public ZoomBoxPresenter(HandlerManager handlerManager, GenericWidgetView view) {
		this.handlerManager = handlerManager;
		this.view = view;

		ZoomBoxPresenterEventHandler eventHandler = new ZoomBoxPresenterEventHandler();
		handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(StateChangeEvent.TYPE, eventHandler);

		setMinimalDragOffsetInPixel(15);
		setFireLoadImageDataEvent(true);
	}

	/**
	 * Specifies the minimal width and height of the zoom area. If the dragging
	 * width or height is below that, no {@link SetDomainBoundsEvent} or {@link LoadImageDataEvent}
	 * will be fired. The presenter simply has no effect in this case.
	 *
	 * <p>This is to ensure, that the image does not get rendered with
	 * extremly small domain bounds, just because the user accidently clicked
	 * on the viewport.
	 *
	 * <p>The default value is 15 pixels.
	 *
	 * @param minimalDragOffsetInPixel
	 */
	public void setMinimalDragOffsetInPixel(int minimalDragOffsetInPixel) {
		this.minimalDragOffsetInPixel = minimalDragOffsetInPixel;
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

	private void onDragInProgress(ViewportDragInProgressEvent event) {
		if (active) {
			Bounds bounds = event.getPixelBounds().normalizeBounds();

			view.setRelX(bounds.getLeft().intValue());
			view.setRelY(bounds.getTop().intValue());
			view.setWidth(bounds.getWidth().intValue());
			view.setHeight(bounds.getHeight().intValue());

			view.show();
		}
	}

	private void onDragFinished(ViewportDragFinishedEvent event) {
		if (active) {
			view.hide();

			if (currentDomainBounds != null) {
				if (dragNotAccidental(event)) {
					Bounds proportionalBounds = event.getProportionalBounds().normalizeBounds();

					Bounds newBounds = currentDomainBounds.transformProportional(proportionalBounds);

					if (currentMaxDomainBounds != null) {
						if (!currentMaxDomainBounds.containsHorizontally(newBounds.getLeft(), newBounds.getRight())) {
							newBounds = newBounds.setLeft(currentDomainBounds.getLeft());
							newBounds = newBounds.setRight(currentDomainBounds.getRight());
						}

						if (!currentMaxDomainBounds.containsVertically(newBounds.getTop(), newBounds.getBottom())) {
							newBounds = newBounds.setTop(currentDomainBounds.getTop());
							newBounds = newBounds.setBottom(currentDomainBounds.getBottom());
						}
					}

					handlerManager.fireEvent(new SetDomainBoundsEvent(newBounds));

					if (fireLoadImageDataEvent) {
						handlerManager.fireEvent(new LoadImageDataEvent());
					}
				}
			}
		}
	}

	private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
		this.currentMaxDomainBounds = event;
	}

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		this.currentDomainBounds = event.getBounds();
	}

	private void onStateChange(StateChangeEvent event) {
		this.active = event.isZoom();
	}

	private boolean dragNotAccidental(ViewportDragFinishedEvent event) {
		return
			event.getRelativePixelBounds().getAbsWidth() > minimalDragOffsetInPixel &&
			event.getRelativePixelBounds().getAbsHeight() > minimalDragOffsetInPixel;
	}

	private class ZoomBoxPresenterEventHandler implements ViewportDragInProgressEventHandler, ViewportDragFinishedEventHandler, SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler, StateChangeEventHandler {
		public void onDragInProgress(ViewportDragInProgressEvent event) {
			ZoomBoxPresenter.this.onDragInProgress(event);
		}

		public void onDragFinished(ViewportDragFinishedEvent event) {
			ZoomBoxPresenter.this.onDragFinished(event);
		}

		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			ZoomBoxPresenter.this.onSetDomainBounds(event);
		}

		public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
			ZoomBoxPresenter.this.onSetMaxDomainBounds(event);
		}

		public void onStateChange(StateChangeEvent event) {
			ZoomBoxPresenter.this.onStateChange(event);
		}
	}
}
