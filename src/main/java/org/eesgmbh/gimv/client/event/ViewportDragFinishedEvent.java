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

package org.eesgmbh.gimv.client.event;

import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;


/**
 * Fired after a drag is finished (i.e. the user performs a mouse up).
 *
 * <p>Contains information how much was dragged both in absolute pixels and as proportional
 * values relative to the viewport dimensions. The pixel bounds are also relative
 * to the parent {@link Viewport}.
 *
 * <p>The two bounds use left and top for the starting position of the drag and right and
 * bottom for the end position.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class ViewportDragFinishedEvent extends FilteredDispatchGwtEvent<ViewportDragFinishedEventHandler> {

	public static Type<ViewportDragFinishedEventHandler> TYPE = new Type<ViewportDragFinishedEventHandler>();

	private final Bounds proportionalBounds;
	private final Bounds relativePixelBounds;

	/**
	 * Instantiates the event.
	 *
	 * @param proportionalBounds {@link Bounds} in relation to the size of the {@link Viewport}
	 * @param relativePixelBounds {@link Bounds} as pixels
	 * @param blockedHandlers optional handlers that must not be informed (see {@link FilteredDispatchGwtEvent} )
	 */
	public ViewportDragFinishedEvent(Bounds proportionalBounds, Bounds relativePixelBounds, ViewportDragFinishedEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.proportionalBounds = proportionalBounds;
		this.relativePixelBounds = relativePixelBounds;
	}

	/**
	 * Returns the bounds of the complete drag as proportional values (0 to 1 based) related to
	 * the viewport dimensions.
	 *
	 * @return proportional {@link Bounds}
	 */
	public Bounds getProportionalBounds() {
		return this.proportionalBounds;
	}

	/**
	 * Returns the bounds of the complete drag as pixel values related to the viewport as the
	 * parent element.
	 *
	 * @return pixel {@link Bounds}
	 */
	public Bounds getRelativePixelBounds() {
		return this.relativePixelBounds;
	}

	@Override
	public Type<ViewportDragFinishedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(ViewportDragFinishedEventHandler handler) {
		handler.onDragFinished(this);
	}
}
