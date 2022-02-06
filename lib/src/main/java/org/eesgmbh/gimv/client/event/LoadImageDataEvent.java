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


/**
 * An event that indicates that new image data should be loaded.
 *
 * <p>Typically received in an application specific controller or rendering
 * component.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class LoadImageDataEvent extends FilteredDispatchGwtEvent<LoadImageDataEventHandler> {

	public static Type<LoadImageDataEventHandler> TYPE = new Type<LoadImageDataEventHandler>();

	public LoadImageDataEvent(LoadImageDataEventHandler... blockedHandlers) {
		super(blockedHandlers);
	}

	@Override
	public Type<LoadImageDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(LoadImageDataEventHandler handler) {
		handler.onLoadImageData(this);
	}

}
