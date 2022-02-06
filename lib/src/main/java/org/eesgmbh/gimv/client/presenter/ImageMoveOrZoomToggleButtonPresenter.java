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
import org.eesgmbh.gimv.client.controls.DragImageControl;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.StateChangeEventHandler;
import org.eesgmbh.gimv.client.view.ImageMoveOrZoomToggleButtonViewImpl;
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * Allows the user to switch between moving the image or
 * zooming it with a mouse drag.
 *
 * <p>A view is required that presents the respective state to the user, e.g. with clickable buttons.
 * When the user changes from zoom to move or vice versa, the presenters is informed via
 * {@link ClickHandler} that must be added to whatever widget in the view implementation.<br>
 * An implementation of the view is {@link ImageMoveOrZoomToggleButtonViewImpl}.
 *
 * <p>Whenever a change between zoom or move is initiated by the user, both the {@link DragImageControl}
 * and {@link ZoomBoxPresenter} will be activated or deactivated respectively.
 *
 * <p>The presenter should be initialized with a {@link StateChangeEvent} fired during application initialization.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link StateChangeEvent} ( if for some reason someone else changes or initializes the respective state, this will be passed on to
 * 		 the two view methods {@link View#toggleMove()} or {@link View#toggleZoom()} )
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link StateChangeEvent} ( informs everyone, that the user either selected zoom or move )
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ImageMoveOrZoomToggleButtonPresenter {

	/**
	 * The view interface belonging to the {@link ImageMoveOrZoomToggleButtonPresenter}.
	 *
	 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
	 *
	 */
	public interface View {
		void toggleMove();
		void toggleZoom();

		void addMoveClickHandler(ClickHandler clickHandler);
		void addZoomClickHandler(ClickHandler clickHandler);
	}

	private final HandlerManager handlerManager;
	private final View view;
	private StateChangeEventHandlerImpl stateChangeEventHandler;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view An implementation of {@link View}
	 */
	public ImageMoveOrZoomToggleButtonPresenter(HandlerManager handlerManager, View view) {
		this.handlerManager = Validate.notNull(handlerManager);
		this.view = Validate.notNull(view);

		this.stateChangeEventHandler = new StateChangeEventHandlerImpl();
		this.handlerManager.addHandler(StateChangeEvent.TYPE, stateChangeEventHandler);

		this.view.addMoveClickHandler(new MoveClickHandler());
		this.view.addZoomClickHandler(new ZoomClickHandler());
	}

	private class StateChangeEventHandlerImpl implements StateChangeEventHandler {
		public void onStateChange(StateChangeEvent event) {
			if (event.isMove()) {
				view.toggleMove();
			} else if (event.isZoom()) {
				view.toggleZoom();
			}
		}
	}

	private class MoveClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handlerManager.fireEvent(StateChangeEvent.createMove(stateChangeEventHandler));
			view.toggleMove();
		}
	}

	private class ZoomClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handlerManager.fireEvent(StateChangeEvent.createZoom(stateChangeEventHandler));
			view.toggleZoom();
		}
	}
}
