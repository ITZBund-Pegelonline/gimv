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

package org.eesgmbh.gimv.client.presenter;

import java.util.ArrayList;
import java.util.List;

import org.eesgmbh.gimv.client.event.SetImageEntitiesEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseMoveEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvGwtTest;
import org.eesgmbh.gimv.client.testsupport.MockMouseMoveEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.junit.Test;


public class TooltipPresenterTest extends AbstractGimvGwtTest {

	private TooltipPresenter presenter;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		if (this.presenter == null) {
			this.presenter = new TooltipPresenter(testHM);

			List<ImageEntity> imageEntities = new ArrayList<ImageEntity>();
			ImageEntity ie = new ImageEntity(new Bounds(50, 52, 40, 42), "1");
			ie.putHoverHtmlFragment("test html");
			imageEntities.add(ie);

			testHM.fireEvent(new SetImageEntitiesEvent(imageEntities));
		}
	}

	@Test
	public void testShowTooltip() throws Exception {
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(39, 39, 150, 140)));
		assertEquals(0, presenter.getTooltipViewFactory().getViewsPool().size());

		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(50, 40, 150, 140)));
		assertEquals(1, presenter.getTooltipViewFactory().getViewsPool().size());
		assertEquals("test html", presenter.getTooltipViewFactory().getViewsPool().get(0).getHtml());
	}

}
