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

import static junit.framework.Assert.*;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.client.testsupport.MockGenericWidgetViewImpl;
import org.eesgmbh.gimv.client.testsupport.TestEventHandler;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.HandlerManager;


public class OverviewPresenterTest extends AbstractGimvUnitTest {

	private OverviewPresenter presenter;

	private HandlerManager dependantTestHM;
	private TestEventHandler dependantTestEH;

	private MockGenericWidgetViewImpl mockOverviewHandleView;
	private MockGenericWidgetViewImpl mockLeftHandleView;
	private MockGenericWidgetViewImpl mockLeftAndRightHandleView;
	private MockGenericWidgetViewImpl mockRightHandleView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			dependantTestHM = new HandlerManager(null);
			dependantTestEH = new TestEventHandler(dependantTestHM);

			mockOverviewHandleView = new MockGenericWidgetViewImpl();
			mockLeftHandleView = new MockGenericWidgetViewImpl();
			mockLeftAndRightHandleView = new MockGenericWidgetViewImpl();
			mockRightHandleView = new MockGenericWidgetViewImpl();

			//test only one combination of its numerous configurations
			presenter = new OverviewPresenter(mockOverviewHandleView, testHM, dependantTestHM);
			presenter.addHandle(mockLeftHandleView, Bound.LEFT);
			presenter.addHandle(mockLeftAndRightHandleView, Bound.LEFT, Bound.RIGHT);
			presenter.addHandle(mockRightHandleView, Bound.RIGHT);

			presenter.setVerticallyLocked(true);
		}
	}

	/**
	 * Asserts the correct placement of the overview based on events from the outside
	 */
	@Test
	public void testOverviewPlacement() throws Exception {
		mockOverviewHandleView.clear();

		testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 100, 0, 50))); //the pixel bounds
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 1000, 0, 500))); //the total domain bounds
		testHM.fireEvent(new SetOverviewDomainBoundsEvent(new Bounds(300, 700, 100, 400))); //the overviewed domain bounds

		assertMockOverviewHandleView(30, 0, 40, 30);

		//change the viewport size
		mockOverviewHandleView.clear();
		testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 1000, 0, 500)));
		assertMockOverviewHandleView(300, 0, 400, 300);

		//change the total domain bounds
		mockOverviewHandleView.clear();
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(100, 1100, 0, 500)));
		assertMockOverviewHandleView(200, 0, 400, 300);

		//change the overview
		testHM.fireEvent(new SetOverviewDomainBoundsEvent(new Bounds(400, 600, 100, 400)));
		assertMockOverviewHandleView(300, 0, 200, 300);
	}

	/**
	 * Asserts the correct placement of the overview based on movements of the handle
	 * and also asserts proper firefing of {@link SetDomainBoundsEvent} and {@link LoadImageDataEvent}
	 */
	@Test
	public void testSetBoundsEvent() throws Exception {
		mockOverviewHandleView.clear();
		mockLeftHandleView.clear();
		mockLeftAndRightHandleView.clear();
		mockRightHandleView.clear();

		testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 100, 0, 50))); //the pixel bounds
		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 1000, 0, 500))); //the total domain bounds
		testHM.fireEvent(new SetOverviewDomainBoundsEvent(new Bounds(300, 700, 100, 400))); //the overviewed domain bounds

		mockLeftHandleView.getBounds = new Bounds(30, 40, 0, 50);
		mockLeftAndRightHandleView.getBounds = new Bounds(41, 59, 0, 50);
		mockRightHandleView.getBounds = new Bounds(60, 70, 0, 50);

		//dragging the left handle
		dependantTestEH.loadImageDataEvent = null;
		testHM.fireEvent(createDragInProgressEvent(31, 1, -3, 2));
		assertMockOverviewHandleView(27, 0, 43, 30);
		testHM.fireEvent(createDragFinishedEvent());
		assertEquals(new Bounds(270, 700, 100, 400), dependantTestEH.setDomainBoundsEvent.getBounds());
		assertNotNull(dependantTestEH.loadImageDataEvent);

		//dragging the left/right handle
		dependantTestEH.loadImageDataEvent = null;
		testHM.fireEvent(createDragInProgressEvent(50, 10, 6, 4));
		assertMockOverviewHandleView(33, 0, 43, 30);
		testHM.fireEvent(createDragFinishedEvent());
		assertEquals(new Bounds(330, 760, 100, 400), dependantTestEH.setDomainBoundsEvent.getBounds());
		assertNotNull(dependantTestEH.loadImageDataEvent);

		//dragging the right handle
		dependantTestEH.loadImageDataEvent = null;
		testHM.fireEvent(createDragInProgressEvent(62, 20, -1, -7));
		assertMockOverviewHandleView(33, 0, 42, 30);
		testHM.fireEvent(createDragFinishedEvent());
		assertEquals(new Bounds(330, 750, 100, 400), dependantTestEH.setDomainBoundsEvent.getBounds());
		assertNotNull(dependantTestEH.loadImageDataEvent);
	}

	private void assertMockOverviewHandleView(int x, int y, int width, int height) {
		assertEquals(x, mockOverviewHandleView.setX);
		assertEquals(y, mockOverviewHandleView.setY);
		assertEquals(width, mockOverviewHandleView.setWidth);
		assertEquals(height, mockOverviewHandleView.setHeight);
	}

	private ViewportDragFinishedEvent createDragFinishedEvent() {
		return new ViewportDragFinishedEvent(null, null);
	}

	private ViewportDragInProgressEvent createDragInProgressEvent(int originAbsX, int originAbsY, int horizontalDragOffset, int verticalDragOffset) {
		return new ViewportDragInProgressEvent(horizontalDragOffset, verticalDragOffset, new Bounds(), new Bounds(originAbsX, 0, originAbsY, 0));
	}
}
