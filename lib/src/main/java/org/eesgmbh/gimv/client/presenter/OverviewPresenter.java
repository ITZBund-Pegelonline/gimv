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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This presenter can be used to display and manipulate the currently shown bounds of the main image
 * within larger bounds.
 *
 * <p>E.g. it can show the currently displayed map section within a larger map as a rectangle. The
 * rectangle can be moved as a whole or only a single bound can be changed.
 *
 * <p>The overview presenter must be instantiated with a different handler manager than the
 * one used to process events for the main image. This handler manager will connect the
 * presenter with its own {@link Viewport} to receive notifications of drag operations.
 *
 * <p>If the user changes the overview with this presenter, the resulting change will be
 * propagated to all registered dependant handler managers with a {@link SetDomainBoundsEvent}. If you
 * got a single main image with one overview, the only dependant handler manager is the one of the
 * main image.
 *
 * <p>After instantiating the presenter, it must be configured with handles. These handles represent
 * the visual representation of the overview and are configured to alter a certain set of bounds
 * (see {@link #addHandle(GenericWidgetView, Bound...)}.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link ViewportDragInProgressEvent} (mandatory, received from the overview's viewport)
 * 	<li> {@link ViewportDragFinishedEvent} (mandatory, received from the overview's viewport, will fire a {@link SetDomainBoundsEvent}
 * 		 and {@link LoadImageDataEvent} on all  dependant handler managers afterwards)
 * 	<li> {@link SetDomainBoundsEvent} (mandatory, the total domain bounds of the overview)
 * 	<li> {@link SetOverviewDomainBoundsEvent} (mandatory, the actual portion of the total bounds highlighted by the presenter)
 * 	<li> {@link SetViewportPixelBoundsEvent} (mandatory, the dimensions of the overview's viewport)
 * 	<li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be no restriction)
 * </ul>
 *
 * <p>Fires the following events on all dependant handler managers
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} (the new domain bounds after a drag finished)
 * 	<li> {@link LoadImageDataEvent} (after a drag finished)
 * </ul>
 *
 * @since 0.1.3
 * @author Sascha Hagedorn - EES GmbH - s.hagedorn@ees-gmbh.de
 */
public class OverviewPresenter {

	/**
	 * Contains a list of {@link HandlerManager} instances who will receive a {@link SetDomainBoundsEvent}
	 * and (optionally) a {@link LoadImageDataEvent} after a drag on the overviewWidget finished.
	 *
	 * The bounds are the current domain bounds of the overview (the portion selected).
	 */
	private final List<HandlerManager> dependantHandlerManagers = new ArrayList<HandlerManager>();

	/**
	 * The widget of the overview
	 */
	private GenericWidgetView overviewWidgetView;

	/**
	 * The handles defining the dragable areas of the overview widget
	 */
	private final List<Handle> handleWidgets = new ArrayList<Handle>();

	/**
	 * Handle which was used to initate the drag
	 */
	private Handle initialDragHandle;

	/**
	 * Pixel bounds of the overview relative to the {@link #viewport}
	 */
	private Bounds currentPixelBounds;

	/**
	 * Overall domain bounds
	 */
	private Bounds currentDomainBounds;

	/**
	 * Domain bounds of the overview. These domain bounds
	 * are represented by the overview. This is a subset of the
	 * {@link currentDomainBounds} and defines where the overview will be placed.
	 */
	private Bounds overviewBounds;

	/**
	 * The overall maxima domain bounds
	 */
	private SetMaxDomainBoundsEvent currentMaxDomainBounds;

	/**
	 * The bounds of the viewport
	 */
	private Bounds currentViewportBounds;

	/**
	 * The minimum width the overview widget can be resized to.<br />
	 * <br />
	 * <b>Default</b>: 0 pixels
	 */
	private int minClippingWidth = 0;

	/**
	 * The minimum height the overview widget can be resized to<br />
	 * <br />
	 * <b>Default</b>: 0 pixels
	 */
	private int minClippingHeight = 0;

	/**
	 * Configures the behaviour of the overview presenter regarding the aspect
	 * ratio of the {@link #overviewWidgetView}. If set to <code>true</code> the
	 * overview presenter keeps the overview at it's original aspect ratio.<br />
	 * <br />
	 * <b>Default</b>: false
	 */
	private boolean preserveAspectRatio = false;

	/**
	 * If set to true the overview widget's left and right bounds are locked. So the left and
	 * right handles (if any) can't be moved. An overview widget which is horizontally
	 * locked can only move vertically.
	 */
	private boolean horizontallyLocked = false;

	/**
	 * If set to true the overview widget's top and bottom bounds are locked. So the top and
	 * bottom handles (if any) can't be moved. An overview widget which is vertically locked
	 * can only move horizontally.
	 */
	private boolean verticallyLocked = false;

	/**
	 * Specifies the top offset of the overview widget<br />
	 * <br />
	 * <b>Default:</b> 0 pixels
	 */
	private int overviewTopOffset = 0;

	/**
	 * Specifies the left offset of the overview widget<br />
	 * <br />
	 * <b>Default:</b> 0 pixels
	 */
	private int overviewLeftOffset = 0;

	private boolean fireLoadImageDataEvent;

	/*
	 * public API
	 */

	/**
	 * Instantiates the overview presenter.
	 *
	 * @param overviewWidgetView The UI for displaying the selected portion
	 * @param handlerManager A {@link HandlerManager}
	 * @param dependantHandlerManager A {@link HandlerManager} who will receive a {@link SetDomainBoundsEvent} and
	 * 		(optionally) a {@link LoadImageDataEvent} after a drag on the overviewWidget finished.
	 * 		The bounds are the current domain bounds of the overview (the portion selected).
	 */
	public OverviewPresenter(GenericWidgetView overviewWidgetView, HandlerManager handlerManager, HandlerManager dependantHandlerManager) {
		Validate.notNull(handlerManager);
		this.overviewWidgetView = Validate.notNull(overviewWidgetView);

		addDependantHandlerManager(dependantHandlerManager);

		OverviewPresenterEventHandler eventHandler = new OverviewPresenterEventHandler();
		handlerManager.addHandler(ViewportDragInProgressEvent.TYPE,	eventHandler);
		handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetOverviewDomainBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);

		setFireLoadImageDataEvent(true);
	}

	/**
	 * Adds a view as a dragging handle. The {@link Bound} enums specify which bound of the
	 * overview will change as a result of dragging the handle. This will both be reflected in the
	 * placement and dimensions of the overview on the UI and also on the domain bounds change of the components
	 * registerd as dependant handler managers.
	 *
	 * <p>A handle with the <code>LEFT</code> bound changes the left bound. Dragging this handle will change
	 * the left bound of the overview bound and leave all other bounds unchanged. The width of the overview changes accordingly
	 *
	 * <p> When two bounds are specified, e. g. <code>LEFT</code> and <code>RIGHT</code>, dragging the handle will result
	 * in the view moving horizontally, preserving it's width, but changing both left and right by the same amount.
	 *
	 * @param handleView the view to be added as a dragging handle
	 * @param bounds bounds which the handle will change
	 */
	public void addHandle(GenericWidgetView handleView, Bound... bounds) {
		handleWidgets.add(new Handle(handleView, bounds));
	}

	/**
	 * Specifies whether the overview should keep its aspect ratio when it is being resized or not.<br />
	 * <br />
	 * <b>Default:</b> <code>false</code>
	 *
	 * @param preserveAspectRatio <code>true</code> to preserve the aspect ratio, <code>false</code> not to
	 */
	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}

	/**
	 * The minimum width the overview can be resized to.
	 *
	 * <b>Default</b>: 0 pixels
	 *
	 * @param minWidth minimum width, in pixels
	 */
	public void setMinClippingWidth(int minWidth) {
		this.minClippingWidth = minWidth;
	}

	/**
	 * The minimum height the overview can be resized to.
	 *
	 * <p><b>Default</b>: 0 pixels
	 *
	 * @param minHeight minimum height, in pixels
	 */
	public void setMinClippingHeight(int minHeight) {
		this.minClippingHeight= minHeight;
	}

	/**
	 * If set to true the overview widget's left and right pixel bounds are locked.
	 * So the left and right handles (if any) can't be moved. An overview widget
	 * which is horizontally locked can only move vertically.<br />
	 * <br>
	 * <b>Disables:</b> {@link #preserveAspectRatio}<br />
	 *
	 * <p><b>Default:</b> false
	 *
	 * @param horizontallyLocked
	 */
	public void setHorizontallyLocked(boolean horizontallyLocked) {
		this.horizontallyLocked = horizontallyLocked;

		if (horizontallyLocked) {
			this.preserveAspectRatio = false;
		}
	}

	/**
	 * If set to true the overview widget's top and bottom pixel bounds are locked.
	 * So the top and bottom handles (if any) can't be moved. An overview widget
	 * which is vertically locked can only move horizontally.<br />
	 * <br>
	 * <b>Disables:</b> {@link #preserveAspectRatio}<br />
	 *
	 * <p><b>Default:</b> false
	 *
	 * @param verticallyLocked
	 */
	public void setVerticallyLocked(boolean verticallyLocked) {
		this.verticallyLocked = verticallyLocked;

		if (verticallyLocked) {
			this.preserveAspectRatio = false;
		}
	}

	/**
	 * Sets the amount of pixels for the vertical offset of the overview widget.
	 *
	 * <p><b>Default:</b> 0 pixels
	 *
	 * @param overviewTopOffsetInPixels offset in pixels
	 */
	public void setOverviewTopOffset(int overviewTopOffsetInPixels) {
		this.overviewTopOffset = overviewTopOffsetInPixels;
	}

	/**
	 * Sets the amount of pixels for the vertical offset of the overview widget.
	 *
	 * <p><b>Default:</b> 0 pixels
	 *
	 * @param overviewLeftOffsetInPixels offset in pixels
	 */
	public void setOverviewLeftOffset(int overviewLeftOffsetInPixels) {
		this.overviewLeftOffset = overviewLeftOffsetInPixels;
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

	/**
	 * Adds {@link HandlerManager} who will receive a {@link SetDomainBoundsEvent} and (optionally) a {@link LoadImageDataEvent}
	 * after a drag on the overviewWidget finished.
	 *
	 * @param handlerManager A {@link HandlerManager} instance
	 */
	public void addDependantHandlerManager(HandlerManager handlerManager) {
		this.dependantHandlerManagers.add(Validate.notNull(handlerManager));
	}

	/*
	 * methods that receive events
	 */

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		currentDomainBounds = event.getBounds();

		if (isOverviewPlacementAvailable()) {
			currentPixelBounds = overviewBounds.transform(currentDomainBounds, currentViewportBounds);
			placeOverviewWidget(currentPixelBounds);
		}
	}

	private void onSetOverviewBounds(SetOverviewDomainBoundsEvent event) {
		overviewBounds = event.getBounds();

		if (isOverviewPlacementAvailable()){
			currentPixelBounds = overviewBounds.transform(currentDomainBounds, currentViewportBounds);
			// If the incoming converted pixel data is smaller than the minimum widget size: recalculate to mimimum pixel size.
			// When dragging, this will be the starting point instead of too minimalistic data.
			if (currentPixelBounds.getAbsWidth() < minClippingWidth) {
				currentPixelBounds = currentPixelBounds.setLeft(currentPixelBounds.getHorizontalCenter() - minClippingWidth/2)
				.setRight(currentPixelBounds.getHorizontalCenter() + minClippingWidth/2);

			}

			if (currentPixelBounds.getAbsHeight() < minClippingHeight) {
				currentPixelBounds = currentPixelBounds.setTop(currentPixelBounds.getVerticalCenter() - minClippingHeight/2)
				.setBottom(currentPixelBounds.getVerticalCenter() + minClippingHeight/2);

			}

			placeOverviewWidget(currentPixelBounds);
		}

	}

	private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
		currentMaxDomainBounds = event;
	}

	private void onDragFinished(ViewportDragFinishedEvent event) {

		if (currentPixelBounds != null && currentViewportBounds != null) {
			Bounds newBounds = currentPixelBounds.transform(currentViewportBounds, currentDomainBounds);

			if (isValidDataBounds(newBounds) && isValidPixelBounds(currentPixelBounds)) {

				fireEventOnAllDependantHandlerManagers(new SetDomainBoundsEvent(newBounds));

				if (fireLoadImageDataEvent) {
					fireEventOnAllDependantHandlerManagers(new LoadImageDataEvent());
				}
			}
		}

		initialDragHandle = null;
	}

	private void onDragInProgress(ViewportDragInProgressEvent event) {
		Handle handle = getHandleBeingHovered(event.getAbsolutePixelBounds().getLeft().intValue(), event.getAbsolutePixelBounds().getTop().intValue());

		if (initialDragHandle == null) {
			initialDragHandle = handle;
		}

		if (initialDragHandle != null && currentPixelBounds != null && currentViewportBounds != null) {

			Bounds newPixelBounds = currentPixelBounds;

			double aspect = currentViewportBounds.getAbsWidth() / currentViewportBounds.getAbsHeight();

			for (Bound bound : initialDragHandle.getBound()) {
				switch (bound) {
				case LEFT:
					if (horizontallyLocked) {
						// if horizontally locked don't move this handle

						break;

					} else if (preserveAspectRatio && !initialDragHandle.hasAllBounds()) {
						// to preserve the aspect ratio resize all handles accordingly, except for the center handle.
						// the center handle has all bounds attached to is and moves the whole overview
						newPixelBounds = newPixelBounds.shiftLeft(     event.getHorizontalDragOffset()          )
						.shiftRight(  - event.getHorizontalDragOffset()          )
						.shiftTop(      event.getHorizontalDragOffset() / aspect )
						.shiftBottom( -(event.getHorizontalDragOffset() / aspect ));
					} else {
						// if this handle is not locked and doesn't need to preserve the aspect ratio move freely
						newPixelBounds = newPixelBounds.shiftLeft(event.getHorizontalDragOffset());

					}
					break;
				case RIGHT:
					if (horizontallyLocked) {

						break;

					} else if (preserveAspectRatio && !initialDragHandle.hasAllBounds()) {

						newPixelBounds = newPixelBounds.shiftLeft(   - event.getHorizontalDragOffset()          )
						.shiftRight(    event.getHorizontalDragOffset()			)
						.shiftTop(    -(event.getHorizontalDragOffset() / aspect ))
						.shiftBottom(   event.getHorizontalDragOffset() / aspect );
					} else {

						newPixelBounds = newPixelBounds.shiftRight(event.getHorizontalDragOffset());

					}
					break;
				case TOP:
					if (verticallyLocked) {

						break;

					} else if (preserveAspectRatio && !initialDragHandle.hasAllBounds()) {
						newPixelBounds = newPixelBounds.shiftLeft(     event.getVerticalDragOffset() * aspect )
						.shiftRight(  -(event.getVerticalDragOffset() * aspect ))
						.shiftTop(	   event.getVerticalDragOffset()		  )
						.shiftBottom( - event.getVerticalDragOffset()          );
					} else {

						newPixelBounds = newPixelBounds.shiftTop(event.getVerticalDragOffset());

					}
					break;
				case BOTTOM:
					if (verticallyLocked) {

						break;

					} else if (preserveAspectRatio && !initialDragHandle.hasAllBounds()) {

						newPixelBounds = newPixelBounds.shiftLeft(   -(event.getVerticalDragOffset() * aspect ))
						.shiftRight(    event.getVerticalDragOffset() * aspect )
						.shiftTop(    - event.getVerticalDragOffset()          )
						.shiftBottom(   event.getVerticalDragOffset()		  );

					} else {

						newPixelBounds = newPixelBounds.shiftBottom(event.getVerticalDragOffset());

					}
					break;
				}
			}

			Bounds dataBounds = newPixelBounds.transform(currentViewportBounds, currentDomainBounds);

			if (isValidDataBounds(dataBounds) && isValidPixelBounds(newPixelBounds)) {

				currentPixelBounds = newPixelBounds;
				placeOverviewWidget(currentPixelBounds);

			}
		}
	}

	private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
		currentViewportBounds = event.getBounds();

		if (isOverviewPlacementAvailable()) {
			currentPixelBounds = overviewBounds.transform(currentDomainBounds, currentViewportBounds);
			placeOverviewWidget(currentPixelBounds);
		}
	}

	/*
	 * helper methods
	 */

	private void fireEventOnAllDependantHandlerManagers(GwtEvent<? extends EventHandler> gwtEvent) {
		for (HandlerManager hm : dependantHandlerManagers) {
			hm.fireEvent(gwtEvent);
		}
	}

	/**
	 * Get the Handle, which area contains the the relative postition <code>x</code>, <code>y</code>.
	 * @param x the relative X position, in pixels
	 * @param y the relative Y position, in pixels
	 * @return the {@link Handle} at position x, y
	 */
	private Handle getHandleBeingHovered(int x, int y) {
		for (Handle handleWidget : handleWidgets) {
			if (handleWidget.getBounds().contains(x, y)) {
				return handleWidget;
			}
		}
		return null;
	}


	/**
	 * Place the overview widget respectively to the offset.
	 */
	private void placeOverviewWidget(Bounds bounds) {
		this.overviewWidgetView.setWidth(this.currentPixelBounds.getAbsWidth().intValue());
		this.overviewWidgetView.setHeight(this.currentPixelBounds.getAbsHeight().intValue());

		if (horizontallyLocked) {
			this.overviewWidgetView.setRelX(overviewLeftOffset);
		} else {
			this.overviewWidgetView.setRelX(this.currentPixelBounds.getLeft().intValue() + overviewLeftOffset);
		}

		if (verticallyLocked) {
			this.overviewWidgetView.setRelY(overviewTopOffset);
		} else {
			this.overviewWidgetView.setRelY(this.currentPixelBounds.getTop().intValue() + overviewTopOffset);
		}

		this.overviewWidgetView.show();
	}

	private boolean isOverviewPlacementAvailable() {
		return overviewBounds != null && currentDomainBounds != null && currentViewportBounds != null;
	}

	private boolean isValidDataBounds(Bounds dataBounds) {
		return currentMaxDomainBounds == null ||
		currentMaxDomainBounds.containsHorizontally(dataBounds.getLeft(), dataBounds.getRight()) &&
		currentMaxDomainBounds.containsVertically(dataBounds.getTop(), dataBounds.getBottom());
	}

	private boolean isValidPixelBounds(Bounds pixelBounds) {
		return pixelBounds.getAbsWidth() >= minClippingWidth &&
		pixelBounds.getAbsHeight() >= minClippingHeight;
	}

	/*
	 * inner class that delegates received events to internal methods
	 */

	private class OverviewPresenterEventHandler implements ViewportDragInProgressEventHandler, ViewportDragFinishedEventHandler, SetDomainBoundsEventHandler, SetOverviewDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler, SetViewportPixelBoundsEventHandler {

		public void onDragInProgress(ViewportDragInProgressEvent event) {
			OverviewPresenter.this.onDragInProgress(event);
		}

		public void onDragFinished(ViewportDragFinishedEvent event) {
			OverviewPresenter.this.onDragFinished(event);
		}

		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			OverviewPresenter.this.onSetDomainBounds(event);
		}

		public void onSetOverviewDomainBounds(SetOverviewDomainBoundsEvent event) {
			OverviewPresenter.this.onSetOverviewBounds(event);
		}

		public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
			OverviewPresenter.this.onSetMaxDomainBounds(event);
		}

		public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
			OverviewPresenter.this.onSetViewportBounds(event);
		}

	}

	private class Handle {
		private GenericWidgetView widget;
		private List<Bound> bounds;

		public Handle(GenericWidgetView widget, Bound[] bounds) {
			super();
			this.widget = widget;
			this.bounds = Arrays.asList(bounds);
		}

		public List<Bound> getBound() {
			return bounds;
		}

		public boolean hasAllBounds() {
			return bounds.contains(Bound.LEFT) &&
				   bounds.contains(Bound.RIGHT) &&
				   bounds.contains(Bound.TOP) &&
				   bounds.contains(Bound.BOTTOM);
		}

		public Bounds getBounds() {
			return this.widget.getAbsBounds();
		}
	}
}
