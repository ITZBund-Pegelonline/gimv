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

import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.view.GenericWidgetViewImpl;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * The presenter is used to display some form of visual clue at the current
 * mouse cursor position inside the {@link Viewport}, e.g. a vertical line in
 * the samples.
 *
 * <p>The underlying view is a {@link GenericWidgetView}. The shipped implementation
 * is {@link GenericWidgetViewImpl}.
 *
 * <p>After instantiating the presenter, it has to be configured how the view position will
 * relate to the current mouse position. The view's position can either correspond to the
 * mouse' x-coordinate, the y-coordinate or both.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link ViewportMouseMoveEvent} (mandatory, won't do anything otherwise)
 * 	<li> {@link ViewportMouseOutEvent} (when received the view will be hidden)
 * 	<li> {@link SetDataAreaPixelBoundsEvent} (optional, when received the view will be hidden
 * 		 if it is not within these bounds )
 * 	<li> {@link ViewportDragInProgressEvent} (optional, when received the view will remain hidden
 * 		 until a ViewportDragFinishedEvent is received)
 * 	<li> {@link ViewportDragFinishedEvent} (optional)
 * </ul>
 *
 * <p>Fires no events.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class MousePointerPresenter {

	private final GenericWidgetView view;
	private boolean enableHorizontalPositioning;
	private boolean enableVerticalPositioning;

	private Bounds currentDataAreaBounds;
	private boolean dragInProgress = false;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view A {@link GenericWidgetView} implementation
	 */
	public MousePointerPresenter(HandlerManager handlerManager, GenericWidgetView view) {
		Validate.notNull(handlerManager);
		this.view = Validate.notNull(view);

		MousePointerPresenterEventHandler eventHandler = new MousePointerPresenterEventHandler();

		handlerManager.addHandler(ViewportMouseMoveEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportMouseOutEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
	}

	/**
	 * Configures the view to either correspond to the
	 * mouse' x-coordinate, the y-coordinate or both.
	 *
	 * <p>If e.g. true, false is passed in, the view will move from right to left,
	 * but not up or down.
	 *
	 * @param enableHorizontalPositioning If the x-position of the mouse should be set in the view.
	 * @param enableVerticalPositioning If the y-position of the mouse should be set in the view.
	 */
	public void configure(boolean enableHorizontalPositioning, boolean enableVerticalPositioning) {
		this.enableHorizontalPositioning = enableHorizontalPositioning;
		this.enableVerticalPositioning = enableVerticalPositioning;
	}

	private void processMouseMove(ViewportMouseMoveEvent event) {
		int x = event.getGwtEvent().getX();
		int y = event.getGwtEvent().getY();

		if (!dragInProgress) {
			if (currentDataAreaBounds == null || (currentDataAreaBounds != null && currentDataAreaBounds.contains(x, y))) {
				view.show();

				if (enableHorizontalPositioning) {
					view.setRelX(x);
				}

				if (enableVerticalPositioning) {
					view.setRelY(y);
				}
			} else {
				view.hide();
			}
		}
	}

	private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
		currentDataAreaBounds = event.getBounds();
	}

	private void onMouseOut(ViewportMouseOutEvent event) {
		view.hide();
	}

	private void onDragInProgress(ViewportDragInProgressEvent event) {
		dragInProgress = true;
		view.hide();
	}

	private void onDragFinished(ViewportDragFinishedEvent event) {
		dragInProgress = false;
		//the widget will become visible at the next mouse move
	}

	private class MousePointerPresenterEventHandler implements ViewportMouseMoveEventHandler, SetDataAreaPixelBoundsEventHandler, ViewportMouseOutEventHandler, ViewportDragInProgressEventHandler, ViewportDragFinishedEventHandler {
		public void onMouseMove(ViewportMouseMoveEvent event) {
			MousePointerPresenter.this.processMouseMove(event);
		}

		public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
			MousePointerPresenter.this.onSetDataAreaBounds(event);
		}

		public void onMouseOut(ViewportMouseOutEvent event) {
			MousePointerPresenter.this.onMouseOut(event);
		}

		public void onDragInProgress(ViewportDragInProgressEvent event) {
			MousePointerPresenter.this.onDragInProgress(event);
		}

		public void onDragFinished(ViewportDragFinishedEvent event) {
			MousePointerPresenter.this.onDragFinished(event);
		}
	}

}
