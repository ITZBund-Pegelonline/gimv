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

import org.eesgmbh.gimv.shared.util.ImageEntity;

import java.util.List;

/**
 * Encapsulates 'points of interests' within the displayed image, that
 * should for example be displayed as tooltips.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class SetImageEntitiesEvent extends FilteredDispatchGwtEvent<SetImageEntitiesEventHandler> {

	public static Type<SetImageEntitiesEventHandler> TYPE = new Type<SetImageEntitiesEventHandler>();

	private final List<ImageEntity> imageEntities;

	public SetImageEntitiesEvent(List<ImageEntity> imageEntities, SetImageEntitiesEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.imageEntities = imageEntities;
	}

	public List<ImageEntity> getImageEntities() {
		return this.imageEntities;
	}

	@Override
	public Type<SetImageEntitiesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetImageEntitiesEventHandler handler) {
		handler.onSetImageEntities(this);
	}
}