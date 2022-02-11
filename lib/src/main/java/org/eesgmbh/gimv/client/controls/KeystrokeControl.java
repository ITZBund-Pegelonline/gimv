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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Widget;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.util.EventAccumulator;
import org.eesgmbh.gimv.client.util.EventAccumulator.Callback;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Direction;
import org.eesgmbh.gimv.shared.util.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>This control can be used to move the image with keystrokes, eg. with the arrow keys.
 *
 * <p>It is implemented as a {@link NativePreviewHandler}, which will do something if the target {@link Element}
 * of the keystroke (the receiver of the key event) is one of the elements registered here. To register an {@link Element} invoke {@link #addTargetElement(Element)}.
 * The target element of the browser's key events is the one which is currently focused, e.g. because the user clicked on the element.
 * Not all elements are focusable (e.g. an image is not, wheras a drop down menu is). If the user clicked on a non-focusable element,
 * the target element is the document's HTML section (in FF) or the BODY section (in Chrome), IE seems to be able to focus on any element but
 * its behavior is generally quirky.
 *
 * <p>If you register no element at all, the control will take action on the registered key strokes ignoring the target element. You may encounter
 * problems with that approach. E.g. if the user scrolls through a drop down menu using the arrow keys the GIMV-image will react as well.
 *
 * <p>For optimal user experience register focusable elements like {@link Viewport} and also invoke {@link #addDocumentAndBodyAsTarget()}. Test your setup
 * by clicking on your user interface and verify that the keystroke control is still working (or not working where it should not). There might also be differences
 * across browsers.
 *
 * <p>After target elements are specified, the respective keys including modifier key state can be registered in {@link #registerKey(int, Direction, int)} or
 * {@link #registerKey(int, boolean, boolean, boolean, boolean, Direction, int)} with the respective move operations that should be invoked
 *
 * <p>A delay for firing {@link LoadImageDataEvent} can be specified. The default is 500ms. This means that (possibly expensive) image rendering is
 * only triggered after the user did not hit a key for 500ms. The value can be change with {@link #setLoadImageDataEventFiringDelay(int)}.
 * Thus, when the user presses a key, only the last of the resulting successive key events will lead to a {@link LoadImageDataEvent}.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * 	<li> {@link SetViewportPixelBoundsEvent} (mandatory, won't work otherwise)
 * 	<li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be no restriction)
 *  <li> {@link SetDataAreaPixelBoundsEvent} (optional, if the vieport contains an image which contains a data area related to {@link SetDomainBoundsEvent}, e.g. without axis areas)
 * </ul>
 *
 * <p>Fires the following events
 * <ul>
 * 	<li> {@link SetDomainBoundsEvent} recomputed after recieving a key stroke event
 * 	<li> {@link ChangeImagePixelBoundsEvent} fired after firing {@link SetDomainBoundsEvent}
 * 	<li> {@link LoadImageDataEvent} fired after firing {@link ChangeImagePixelBoundsEvent}
 * </ul>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class KeystrokeControl {

	private boolean fireLoadImageDataEvent;
	private boolean fireChangeImagePixelBoundsEvent;
	private boolean cancelEvent;

	private final List<com.google.gwt.dom.client.Element> registeredTargetElements = new ArrayList<com.google.gwt.dom.client.Element>();
	private final Map<KeyCodeMapKey, KeyCodeMapValue> registeredKeycodes = new HashMap<KeyCodeMapKey, KeyCodeMapValue>();

	private SetDomainBoundsEvent currentSetDomainBoundsEvent;
	private SetMaxDomainBoundsEvent currentMaxDomainBoundsEvent;
	private SetDataAreaPixelBoundsEvent currentDataAreaBoundsEvent;
	private SetViewportPixelBoundsEvent currentViewportBoundsEvent;

	private EventAccumulator eventAccumulator;
	private final HandlerManager handlerManager;

	/**
	 * Instantiates the control with default configuration.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 */
	public KeystrokeControl(HandlerManager handlerManager) {
		this.handlerManager = Validate.notNull(handlerManager);

		KeystrokeControlEventHandler eventHandler = new KeystrokeControlEventHandler();
		this.handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
		this.handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
		this.handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);
		this.handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);

		Event.addNativePreviewHandler(new KeystrokeNativePreviewHandler());

		setFireLoadImageDataEvent(true);
		setFireChangeImagePixelBoundsEvent(true);
		setCancelEvent(false);
		setLoadImageDataEventFiringDelay(500);
	}

	/**
	 * Adds a target element.
	 *
	 * @param element Target element. Can be obtained from a widget by calling {@link Widget#getElement()}
	 */
	public void addTargetElement(com.google.gwt.dom.client.Element element) {
		this.registeredTargetElements.add(element);
	}

	/**
	 * React to keystroke events, even if the browser does not have an element under focus.
	 *
	 * Has an effect in FF and Chrome.
	 */
	public void addDocumentAndBodyAsTarget() {
		addTargetElement(Document.get().getDocumentElement());
		addTargetElement(Document.get().getBody());
	}

	/**
	 * Registers a keystroke with a move operations.
	 *
	 * By default no modifier key like CTRL or ALT must be down.
	 *
	 * @param keycode One of the constants in {@link KeyCodes}
	 * @param direction The direction to move the image to
	 * @param offsetInPixel How much to move the image in pixels
	 */
	public void registerKey(int keycode, Direction direction, int offsetInPixel) {
		registerKey(keycode, false, false, false, false, direction, offsetInPixel);
	}

	/**
	 * Registers a keystroke with a move operations.
	 *
	 * The state of the modifier keys can be specified.
	 *
	 * @param keycode One of the constants in {@link KeyCodes}
	 * @param ctrl Should the modifier be down
	 * @param alt Should the modifier be down
	 * @param shift Should the modifier be down
	 * @param meta Should the modifier be down
	 * @param direction The direction to move the image to
	 * @param offsetInPixel How much to move the image in pixels
	 */
	public void registerKey(int keycode, boolean ctrl, boolean alt, boolean shift, boolean meta, Direction direction, int offsetInPixel) {
		this.registeredKeycodes.put(new KeyCodeMapKey(keycode, ctrl, alt, shift, meta), new KeyCodeMapValue(direction, offsetInPixel));
	}

	/**
	 * Specify whether keystroke events will be canceled.
	 *
	 * <p>This means that if you register the arrow keys and set cancelEvent to true, scrolling the browser's window with the arrow keys becomes
	 * impossible (as the browser is not informed about the keystroke).
	 *
	 * <p>The default is false.
	 *
	 * @param cancelEvent
	 */
	public void setCancelEvent(boolean cancelEvent) {
		this.cancelEvent = cancelEvent;
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
	 * Sets the time in milliseconds for how long to defer firing a {@link LoadImageDataEvent}.
	 *
	 * <p>The default value is 500 ms. This means that 500 ms have to pass after the last keystroke until a
	 * {@link LoadImageDataEvent} is fired causing the image to be newly rendered. If multiple keystrokes were received during
	 * that period, the new image bounds will be the 'sum' of all these keystrokes.<br>
	 * The value of 500ms was chosen to match the auto-repeat of most keyboards.
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

	private void onPreviewNativeEvent(NativePreviewEvent preview) {
		NativeEvent event = preview.getNativeEvent();

		if (isKeystroke(event)) { //quickly return, if there is no key stroke
			if (currentSetDomainBoundsEvent != null && currentViewportBoundsEvent != null && isKeyDown(event) && isTargetElementMatch(event) && isKeycodeMatch(event)) {
				KeyCodeMapValue keyCodeMapValue = registeredKeycodes.get(createKey(event));

				int horShiftInPixels;
				int verShiftInPixels;

				switch (keyCodeMapValue.direction) {
				case WEST:
					horShiftInPixels = -keyCodeMapValue.offsetInPixel;
					verShiftInPixels = 0;
					break;
				case EAST:
					horShiftInPixels = keyCodeMapValue.offsetInPixel;
					verShiftInPixels = 0;
					break;
				case NORTH:
					horShiftInPixels = 0;
					verShiftInPixels = -keyCodeMapValue.offsetInPixel;
					break;
				case SOUTH:
					horShiftInPixels = 0;
					verShiftInPixels = keyCodeMapValue.offsetInPixel;
					break;
				default:
					throw new IllegalArgumentException();
				}

				//the exact opposite, if the image moved to the right (+pixel), the new image must be rendered starting at -10 pixels
				Bounds shiftedTo = getViewportDataAreaBounds().shiftAbsolute(-horShiftInPixels, -verShiftInPixels);
				Bounds newBounds = shiftedTo.transform(getViewportDataAreaBounds(), currentSetDomainBoundsEvent.getBounds());

				if (currentMaxDomainBoundsEvent == null ||
						(currentMaxDomainBoundsEvent.containsHorizontally(newBounds.getLeft(), newBounds.getRight()) &&
								currentMaxDomainBoundsEvent.containsVertically(newBounds.getTop(), newBounds.getBottom()))) {

					if (fireChangeImagePixelBoundsEvent) {
						handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(horShiftInPixels, verShiftInPixels));
					}

					handlerManager.fireEvent(new SetDomainBoundsEvent(newBounds));

					eventAccumulator.addEvent(new LoadImageDataEvent());
				}

				if (cancelEvent) {
					preview.cancel();
				}
			}
		}
	}

	private boolean isKeystroke(NativeEvent event) {
		return event.getKeyCode() > 1;
	}

	private boolean isKeyDown(NativeEvent event) {
		return event.getType().equalsIgnoreCase("keydown");
	}

	private boolean isTargetElementMatch(NativeEvent event) {
		return registeredTargetElements.isEmpty() || registeredTargetElements.contains(event.getEventTarget().cast());
	}

	private boolean isKeycodeMatch(NativeEvent event) {
		return registeredKeycodes.containsKey(createKey(event));
	}

	private KeyCodeMapKey createKey(NativeEvent event) {
		return new KeyCodeMapKey(event.getKeyCode(), event.getCtrlKey(), event.getAltKey(), event.getShiftKey(), event.getMetaKey());
	}

	private Bounds getViewportDataAreaBounds() {
		if (currentDataAreaBoundsEvent != null) {
			return currentDataAreaBoundsEvent.getBounds();
		} else {
			return currentViewportBoundsEvent.getBounds();
		}
	}

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		currentSetDomainBoundsEvent = event;
	}

	private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
		currentMaxDomainBoundsEvent = event;
	}

	private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
		currentDataAreaBoundsEvent = event;
	}

	private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
		currentViewportBoundsEvent = event;
	}

	private class KeystrokeNativePreviewHandler implements NativePreviewHandler {
		public void onPreviewNativeEvent(NativePreviewEvent preview) {
			KeystrokeControl.this.onPreviewNativeEvent(preview);
		}
	}

	/*
	 * Gets invoked after a configurable delay and causes the
	 * image to be actually rendered.
	 */
	private class EventAccumulatorCallback implements Callback {
		public void excute(List<GwtEvent<? extends EventHandler>> gwtEvents) {
			if (fireLoadImageDataEvent) {
				handlerManager.fireEvent(new LoadImageDataEvent());
			}
		}
	}

	private class KeystrokeControlEventHandler implements SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler, SetDataAreaPixelBoundsEventHandler, SetViewportPixelBoundsEventHandler {
		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			KeystrokeControl.this.onSetDomainBounds(event);
		}

		public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
			KeystrokeControl.this.onSetMaxDomainBounds(event);
		}

		public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
			KeystrokeControl.this.onSetDataAreaBounds(event);
		}

		public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
			KeystrokeControl.this.onSetViewportBounds(event);
		}
	}

	private class KeyCodeMapKey {
		private int keycode;
		private boolean ctrl;
		private boolean alt;
		private boolean shift;
		private boolean meta;

		private KeyCodeMapKey(int keycode, boolean ctrl, boolean alt, boolean shift, boolean meta) {
			this.keycode = keycode;
			this.ctrl = ctrl;
			this.alt = alt;
			this.shift = shift;
			this.meta = meta;
		}

		private KeyCodeMapKey(KeyCodeEvent gwtKeyCodeEvent) {
			this(gwtKeyCodeEvent.getNativeKeyCode(), gwtKeyCodeEvent.isControlKeyDown(), gwtKeyCodeEvent.isAltKeyDown(),
					gwtKeyCodeEvent.isShiftKeyDown(), gwtKeyCodeEvent.isMetaKeyDown());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (this.alt ? 1231 : 1237);
			result = prime * result + (this.ctrl ? 1231 : 1237);
			result = prime * result + this.keycode;
			result = prime * result + (this.meta ? 1231 : 1237);
			result = prime * result + (this.shift ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyCodeMapKey other = (KeyCodeMapKey) obj;
			if (this.alt != other.alt)
				return false;
			if (this.ctrl != other.ctrl)
				return false;
			if (this.keycode != other.keycode)
				return false;
			if (this.meta != other.meta)
				return false;
			if (this.shift != other.shift)
				return false;
			return true;
		}
	}

	private class KeyCodeMapValue {
		private Direction direction;
		private int offsetInPixel;

		private KeyCodeMapValue(Direction direction, int offsetInPixel) {
			this.direction = direction;
			this.offsetInPixel = offsetInPixel;
		}
	}
}
