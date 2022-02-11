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

package org.eesgmbh.gimv.client.event;

import org.eesgmbh.gimv.shared.util.Bounds;


/**
 * <p>Indicates that a drag (i.e. the user presses the left mouse button and moves over the viewport)
 * is in progress.
 *
 * <p>The offset in vertical and horizontal direction is contained in the event object.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class ViewportDragInProgressEvent extends FilteredDispatchGwtEvent<ViewportDragInProgressEventHandler> {

	public static Type<ViewportDragInProgressEventHandler> TYPE = new Type<ViewportDragInProgressEventHandler>();

	private final int horizontalDragOffset, verticalDragOffset;
	private final Bounds pixelBounds;
	private final Bounds absolutePixelBounds;

	public ViewportDragInProgressEvent(int horizontalDragOffset, int verticalDragOffset, Bounds pixelBounds, Bounds clientPixelBounds, ViewportDragInProgressEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.horizontalDragOffset = horizontalDragOffset;
		this.verticalDragOffset = verticalDragOffset;

		this.pixelBounds = pixelBounds;
		this.absolutePixelBounds = clientPixelBounds;
	}

	public int getHorizontalDragOffset() {
		return this.horizontalDragOffset;
	}

	public int getVerticalDragOffset() {
		return this.verticalDragOffset;
	}

	public Bounds getPixelBounds() {
		return this.pixelBounds;
	}

	public Bounds getAbsolutePixelBounds() {
		return this.absolutePixelBounds;
	}

	@Override
	public Type<ViewportDragInProgressEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(ViewportDragInProgressEventHandler handler) {
		handler.onDragInProgress(this);
	}
}
