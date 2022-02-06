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

import org.eesgmbh.gimv.shared.util.Bounds;

/**
 * Defines a rectangle within which the actual data of an image is being displayed.
 *
 * <p>In other words, it defines the area (or plot area) to which a {@link SetDomainBoundsEvent}
 * applies to.
 *
 * <p>Must always be fired if the data within the image does not cover the whole space
 * of the image (eg. chart images that contain axes). Otherwise zooming and moving the image
 * will be not be calculated accurately.
 *
 * <p>If the data area covers the whole image there is no need to fire this event.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetDataAreaPixelBoundsEvent extends FilteredDispatchGwtEvent<SetDataAreaPixelBoundsEventHandler> {

	public static Type<SetDataAreaPixelBoundsEventHandler> TYPE = new Type<SetDataAreaPixelBoundsEventHandler>();

	private final Bounds bounds;

	/**
	 * @param bounds if null, no constraining bounds will be specified
	 * @param blockedHandlers
	 */
	public SetDataAreaPixelBoundsEvent(Bounds bounds, SetDataAreaPixelBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.bounds = bounds;
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	@Override
	public Type<SetDataAreaPixelBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetDataAreaPixelBoundsEventHandler handler) {
		handler.onSetDataAreaPixelBounds(this);
	}
}
