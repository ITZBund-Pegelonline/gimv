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

package org.eesgmbh.gimv.client.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * <p>This widget captures mouse events.
 *
 * <p>Mouse dragging, meaning the left mouse button is pressed and the mouse is moved over the viewport,
 * is translated into semantic {@link ViewportDragInProgressEvent} and {@link ViewportDragFinishedEventHandler}
 * events.
 *
 * <p>For most Gimv components, it is required to define a viewport widget and
 * add the actual image to this viewport. There must not be any margin, padding or other
 * kinds of offsets between the viewport and the contained image.
 *
 * <p>Proper configuration requires to set the width and height explicitly (in any unit
 * including percentages), adding the image component and setting the {@link HandlerManager}.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link StateChangeEvent} (when switching between move and zoom)
 *  <li> {@link SetDataAreaPixelBoundsEvent} (optional, if the image contains a data area related to {@link SetDomainBoundsEvent}, e.g. without axis areas)
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link ViewportMouseMoveEvent} - simply wraps the GWT {@link MouseMoveEvent} in a Gimv event
 * 	<li> {@link ViewportMouseOutEvent} - simply wraps the GWT {@link MouseOutEvent} in a Gimv event
 * 	<li> {@link ViewportMouseWheelEvent} - simply wraps the GWT {@link MouseOutEvent} in a Gimv event
 * 	<li> {@link StateChangeEvent} - fired when the shift key is pressed, during left mouse down and move (drag)
 * 	<li> {@link ViewportDragInProgressEvent} - fired, while the user moves the mouse over the image and presses down the left mouse button
 * 	<li> {@link ViewportDragFinishedEvent} - fired, when the user finished dragging (releases the mouse button)
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class Viewport extends AbsolutePanel {

	private final ViewportEventHandler eventHandler;
	private HandlerManager handlerManager;

	private Bounds currentDataAreaBounds = null;

	private boolean enableZoomWhenShiftkeyPressed;

	private boolean mouseDragging = false;

	private int currentDragX, currentDragY;
	private int startDragX, startDragY;
	private int startDragAbsX, startDragAbsY;

	private boolean zoomEnabledWhenDragWasInitiated = false;

	private boolean zoom = false;

	/**
	 * No arg constructor. Should only be used if the viewport is instantiated within a
	 * ui binder context.
	 *
	 * <p>The methods {@link #setWidth(String)}, {@link #setHeight(String)}, {@link #add(Widget)} must be invoked afterwards
	 * (all three probably implicitly by ui binder) and very importantly {@link #setHandlerManager(HandlerManager)}
	 *
	 */
	public Viewport() {
		eventHandler = new ViewportEventHandler();
		addDomHandler(eventHandler, MouseDownEvent.getType());
		addDomHandler(eventHandler, MouseUpEvent.getType());
		addDomHandler(eventHandler, MouseMoveEvent.getType());
		addDomHandler(eventHandler, MouseOutEvent.getType());
		addDomHandler(eventHandler, MouseWheelEvent.getType());

		setEnableZoomWhenShiftkeyPressed(false);

		makeFocusable();
	}

	/**
	 * Instantiates the viewport with width and height.
	 *
	 * @param width a CSS attribute (e.g. "100px", "100%")
	 * @param height a CSS attribute (e.g. "100px", "100%")
	 */
	public Viewport(String width, String height) {
		this();

		setWidth(width);
		setHeight(height);
	}

	/**
	 * Registers necessary handlers. Must be invoked before any
	 * event capturing takes place.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 */
	public void setHandlerManager(HandlerManager handlerManager) {
		this.handlerManager = Validate.notNull(handlerManager, "handlerManager must not be null");

		this.handlerManager.addHandler(StateChangeEvent.TYPE, eventHandler);
		this.handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);
	}

	/**
	 * If set to true, a {@link StateChangeEvent#createZoom(StateChangeEventHandler...)} event will be fired
	 * whenever the user presses the shift key when initiating a drag.
	 *
	 * This means that the image will be zoomed if the user presses the shift
	 * key and otherwise moved.
	 *
	 * Default is false.
	 *
	 * @param enableZoomWhenShiftkeyPressed
	 */
	public void setEnableZoomWhenShiftkeyPressed(boolean enableZoomWhenShiftkeyPressed) {
		this.enableZoomWhenShiftkeyPressed = enableZoomWhenShiftkeyPressed;
	}

	/**
	 * Delegates to {@link AbsolutePanel#add(Widget, int, int)} with height and with set to zero.
	 *
	 * <p>The UIBinder uses {@link AbsolutePanel#add(Widget)} to add a child component to
	 * an {@link AbsolutePanel}. In order to be able to change the position of the underlying
	 * widget, the widget has to be given absolute coordinates (resulting in position:absolute, top and
	 * left to be set in css).
	 *
	 * <p>This workaround can be removed, once GWT allows positioning of child elements in UI Binder templates
	 *
	 * <p>implementation alternatives:<br>
	 *  - set position:absolute, top and left directly in ui binder xml<br>
	 *  - do not use UIBinder for the inner image<br>
	 *  - set position of the image afterwards<br>
	 *  - code custom AbsolutePanelParser<br>
	 *
	 * @see com.google.gwt.user.client.ui.AbsolutePanel#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		super.add(w, 0, 0);
	}

	/*
	 * UI event handling
	 */

	private void onMouseDown(MouseDownEvent event) {
		mouseDragging = true;

		startDragX = event.getX();
		startDragY = event.getY();
		startDragAbsX = event.getClientX();
		startDragAbsY = event.getClientY();

		currentDragX = event.getX();
		currentDragY = event.getY();

		//so the mouse cursor can be moved outside the viewport without loosing mouse event capturing
		DOM.setCapture(getElement());

		/*
		 * Carry out the zoom state change if the viewport is configured to to do so, if the shift
		 * key is pressed and only if zoom is not the current default already
		 */
		if (enableZoomWhenShiftkeyPressed && event.getNativeEvent().getShiftKey() && !zoom) {
			handlerManager.fireEvent(StateChangeEvent.createZoom());

			zoomEnabledWhenDragWasInitiated  = true;
		}

		//otherwise the browser will drag the image natively, this would interfere
		event.preventDefault();

		//as all defaults were prevented, the element also did not get the focus in order to receive keyboard events, correct this here
		focus();
	}

	private void onMouseUp(MouseUpEvent event) {
		mouseDragging = false;

		DOM.releaseCapture(getElement());

		Bounds dataBounds = getDataAreaBounds();

		Bounds proportionalBounds = new Bounds(
				(startDragX - dataBounds.getLeft()) / dataBounds.getAbsWidth(), (event.getX() - dataBounds.getLeft()) / dataBounds.getAbsWidth(),
				(startDragY - dataBounds.getTop()) / dataBounds.getAbsHeight(), (event.getY() - dataBounds.getTop()) / dataBounds.getAbsHeight()
		);

		Bounds absoluteBounds = new Bounds(
				startDragX, event.getX(), startDragY, event.getY()
		);

		handlerManager.fireEvent(new ViewportDragFinishedEvent(proportionalBounds,  absoluteBounds));

		if (zoomEnabledWhenDragWasInitiated) {
			handlerManager.fireEvent(StateChangeEvent.createMove());
			zoomEnabledWhenDragWasInitiated = false;
		}
	}

	private void onMouseMove(MouseMoveEvent event) {
		if (mouseDragging) {
			handlerManager.fireEvent(new ViewportDragInProgressEvent(event.getX() - currentDragX, event.getY() - currentDragY, new Bounds(startDragX, currentDragX, startDragY, currentDragY), new Bounds(startDragAbsX, event.getClientX(), startDragAbsY, event.getClientY())));

			currentDragX = event.getX();
			currentDragY = event.getY();
		} else {
			handlerManager.fireEvent(new ViewportMouseMoveEvent(event));
		}
	}

	private void onMouseOut(MouseOutEvent event) {
		handlerManager.fireEvent(new ViewportMouseOutEvent(event));
	}

	private void onMouseWheel(MouseWheelEvent event) {
		handlerManager.fireEvent(new ViewportMouseWheelEvent(event));
	}

	/*
	 * application event handling
	 */

	private void onStateChange(StateChangeEvent event) {
		if (event.isMove()) {
			zoom = false;
		} else if (event.isZoom()) {
			zoom = true;
		}
	}

	private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
		currentDataAreaBounds = event.getBounds();
	}

	private class ViewportEventHandler implements MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler, MouseWheelHandler, StateChangeEventHandler, SetDataAreaPixelBoundsEventHandler {
		public void onMouseDown(MouseDownEvent event) {
			Viewport.this.onMouseDown(event);
		}

		public void onMouseUp(MouseUpEvent event) {
			Viewport.this.onMouseUp(event);
		}

		public void onMouseMove(MouseMoveEvent event) {
			Viewport.this.onMouseMove(event);
		}

		public void onMouseOut(MouseOutEvent event) {
			Viewport.this.onMouseOut(event);
		}

		public void onMouseWheel(MouseWheelEvent event) {
			Viewport.this.onMouseWheel(event);
		}

		public void onStateChange(StateChangeEvent event) {
			Viewport.this.onStateChange(event);
		}

		public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
			Viewport.this.onSetDataAreaBounds(event);
		}
	}

	/*
	 * helper methods
	 */

	private Bounds getDataAreaBounds() {
		if (currentDataAreaBounds != null) {
			return currentDataAreaBounds;
		} else {
			return new Bounds(0, getOffsetWidth(), 0, getOffsetHeight());
		}
	}

	/**
	 * The Viewport class is an {@link AbsolutePanel} which is DIV, that cannot receive keyboard events
	 * natively.
	 *
	 * The common workaround is to assign a tab index to make it focusable and subsequently keyboard events
	 * can be received.
	 *
	 * @see FocusImpl
	 */
	private void makeFocusable() {
		getElement().setTabIndex(-1);
	}

	private void focus() {
		getElement().focus();
	}
}
