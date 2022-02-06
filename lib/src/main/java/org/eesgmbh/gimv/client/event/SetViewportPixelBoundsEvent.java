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
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * <p>Sets the viewport bounds in pixels.
 *
 * <p>The viewport width and height are normally of main interest and many presenters and controls
 * will not work without recieving it at least once.
 *
 * <p>Therefore this event must be fired during initialization or when using a fluid (resizable)
 * design whenever a change in the viewport widget takes place.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetViewportPixelBoundsEvent extends FilteredDispatchGwtEvent<SetViewportPixelBoundsEventHandler> {

	public static Type<SetViewportPixelBoundsEventHandler> TYPE = new Type<SetViewportPixelBoundsEventHandler>();

	private Bounds bounds;

	public SetViewportPixelBoundsEvent(Bounds bounds, SetViewportPixelBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.bounds = Validate.notNull(bounds, "bounds must not be null");
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	@Override
	public Type<SetViewportPixelBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetViewportPixelBoundsEventHandler handler) {
		handler.onSetViewportBounds(this);
	}
}
