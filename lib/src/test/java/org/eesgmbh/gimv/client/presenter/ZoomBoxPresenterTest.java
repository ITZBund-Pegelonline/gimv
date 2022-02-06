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

package org.eesgmbh.gimv.client.presenter;

import static junit.framework.Assert.*;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.client.testsupport.MockGenericWidgetViewImpl;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;


public class ZoomBoxPresenterTest extends AbstractGimvUnitTest {

	private ZoomBoxPresenter presenter;
	private MockGenericWidgetViewImpl mockView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockGenericWidgetViewImpl();
			this.presenter = new ZoomBoxPresenter(testHM, mockView);
		}

		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));
		testHM.fireEvent(StateChangeEvent.createZoom());
	}

	@Test
	public void testViewPositionAndDimensions() throws Exception {
		mockView.clear();
		testHM.fireEvent(createDragInProgressEvent(10, 10, 12, 12));
		assertView(10, 10, 2, 2);

		mockView.clear();
		testHM.fireEvent(createDragInProgressEvent(12, 12, 10, 10));
		assertView(10, 10, 2, 2);
	}

	@Test
	public void testSetDomainBoundsEvent() throws Exception {
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 20, 0, 20)));
		assertEquals(new Bounds(20, 80, 30, 70), testEH.setDomainBoundsEvent.getBounds());

		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));

		//negative proportional bounds
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.8, 0.2, 0.7, 0.3), new Bounds(0, 20, 0, 20)));
		assertEquals(new Bounds(20, 80, 30, 70), testEH.setDomainBoundsEvent.getBounds());

		//inverted domain bounds
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(100, 0, 100, 0)));
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 20, 0, 20)));
		assertEquals(new Bounds(80, 20, 70, 30), testEH.setDomainBoundsEvent.getBounds());

		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));

		//zoombox outside viewport (negative drag finished bounds)
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(-0.2, 0.8, 0.3, 1.2), new Bounds(0, 20, 0, 20)));
		assertEquals(new Bounds(-20, 80, 30, 120), testEH.setDomainBoundsEvent.getBounds());
	}

	@Test
	public void testLoadImageDataEvent() throws Exception {
		testEH.loadImageDataEvent = null;

		//deactivated, nothing happens
		testHM.fireEvent(StateChangeEvent.createMove());
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 20, 0, 20)));
		assertNull(testEH.loadImageDataEvent);

		testHM.fireEvent(StateChangeEvent.createZoom());

		//deactivated, because the presenter is configured so
		presenter.setFireLoadImageDataEvent(false);
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 20, 0, 20)));
		assertNull(testEH.loadImageDataEvent);

		presenter.setFireLoadImageDataEvent(true);

		//now it must work
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 20, 0, 20)));
		assertNotNull(testEH.loadImageDataEvent);
	}

	@Test
	public void testMinimalDragOffsets() throws Exception {
		testEH.loadImageDataEvent = null;
		testEH.setDomainBoundsEvent = null;

		presenter.setMinimalDragOffsetInPixel(20);

		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 10, 0, 10)));
		assertNull(testEH.loadImageDataEvent);
		assertNull(testEH.setDomainBoundsEvent);

		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(30, 0, 0, 20)));
		assertNull(testEH.loadImageDataEvent);
		assertNull(testEH.setDomainBoundsEvent);

		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(10, 0, 10, 40)));
		assertNull(testEH.loadImageDataEvent);
		assertNull(testEH.setDomainBoundsEvent);

		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(0.2, 0.8, 0.3, 0.7), new Bounds(0, 30, 10, 40)));
		assertNotNull(testEH.loadImageDataEvent);
		assertNotNull(testEH.setDomainBoundsEvent);
	}

	private void assertView(int x, int y, int width, int height) {
		assertEquals(x, mockView.setX);
		assertEquals(y, mockView.setY);
		assertEquals(width, mockView.setWidth);
		assertEquals(height, mockView.setHeight);
	}

	private ViewportDragInProgressEvent createDragInProgressEvent(int originX, int originY, int currentX, int currentY) {
		return new ViewportDragInProgressEvent(0, 0, new Bounds(originX, currentX, originY, currentY), new Bounds());
	}
}
