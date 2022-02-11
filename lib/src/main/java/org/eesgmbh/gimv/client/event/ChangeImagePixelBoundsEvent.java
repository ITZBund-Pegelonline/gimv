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


/**
 * Indicates that image position and dimensions should be changed
 * by some offset.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ChangeImagePixelBoundsEvent extends FilteredDispatchGwtEvent<ChangeImagePixelBoundsEventHandler> {

	public static Type<ChangeImagePixelBoundsEventHandler> TYPE = new Type<ChangeImagePixelBoundsEventHandler>();

	private final double offsetX, offsetY;

	private final double offsetWidth;
	private final double offsetHeight;

	public ChangeImagePixelBoundsEvent(double offsetX, double offsetY, ChangeImagePixelBoundsEventHandler... blockedHandlers) {
		this(offsetX, offsetY, 0, 0, blockedHandlers);
	}

	public ChangeImagePixelBoundsEvent(double offsetX, double offsetY, double offsetWidth, double offsetHeight, ChangeImagePixelBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetWidth = offsetWidth;
		this.offsetHeight = offsetHeight;
	}

	public double getOffsetX() {
		return this.offsetX;
	}

	public double getOffsetY() {
		return this.offsetY;
	}

	public double getOffsetWidth() {
		return this.offsetWidth;
	}

	public double getOffsetHeight() {
		return this.offsetHeight;
	}

	@Override
	public Type<ChangeImagePixelBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(ChangeImagePixelBoundsEventHandler handler) {
		handler.onSetImageBounds(this);
	}
}
