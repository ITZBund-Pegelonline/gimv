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
 * If the actual image can be retrieved with a url, this event must fired
 * after the rendering process has finished.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetImageUrlEvent extends FilteredDispatchGwtEvent<SetImageUrlEventHandler> {

	public static Type<SetImageUrlEventHandler> TYPE = new Type<SetImageUrlEventHandler>();

	private final String url;

	public SetImageUrlEvent(String url, SetImageUrlEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public Type<SetImageUrlEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetImageUrlEventHandler handler) {
		handler.onSetImageUrl(this);
	}
}
