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

import org.eesgmbh.gimv.client.event.SetDataAreaPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseMoveEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseOutEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.client.testsupport.MockGenericWidgetViewImpl;
import org.eesgmbh.gimv.client.testsupport.MockMouseMoveEvent;
import org.eesgmbh.gimv.client.testsupport.MockMouseOutEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;


public class MousePointerPresenterTest extends AbstractGimvUnitTest {

	private MousePointerPresenter presenter;
	private MockGenericWidgetViewImpl mockView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockGenericWidgetViewImpl();
			this.presenter = new MousePointerPresenter(testHM, mockView);
		}
	}

	@Test
	public void testShowAndHideInView() throws Exception {
		mockView.clear();
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));

		assertTrue(mockView.showInvoked);
		assertFalse(mockView.hideInvoked);

		mockView.clear();
		testHM.fireEvent(new ViewportMouseOutEvent(new MockMouseOutEvent(10, 20)));

		assertFalse(mockView.showInvoked);
		assertTrue(mockView.hideInvoked);
	}

	@Test
	public void testSetXandYInView() throws Exception {
		mockView.clear();
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));

		//not properly configured yet
		assertEquals(Integer.MAX_VALUE, mockView.setX);
		assertEquals(Integer.MAX_VALUE, mockView.setY);

		mockView.clear();
		presenter.configure(true, false);
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));
		assertEquals(10, mockView.setX);
		assertEquals(Integer.MAX_VALUE, mockView.setY);

		mockView.clear();
		presenter.configure(false, true);
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));
		assertEquals(Integer.MAX_VALUE, mockView.setX);
		assertEquals(20, mockView.setY);

		mockView.clear();
		presenter.configure(true, true);
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));
		assertEquals(10, mockView.setX);
		assertEquals(20, mockView.setY);
	}

	@Test
	public void testDragHandling() throws Exception {
		mockView.clear();
		testHM.fireEvent(new ViewportDragInProgressEvent(0, 0, new Bounds(), new Bounds()));
		assertTrue(mockView.hideInvoked);

		mockView.clear();
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));
		assertFalse(mockView.showInvoked);
		assertFalse(mockView.hideInvoked);

		mockView.clear();
		testHM.fireEvent(new ViewportDragFinishedEvent(new Bounds(), new Bounds()));
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(10, 20, 110, 220)));
		assertTrue(mockView.showInvoked);
		assertFalse(mockView.hideInvoked);
	}

	@Test
	public void testDataAreaBoundsHandling() throws Exception {
		testHM.fireEvent(new SetDataAreaPixelBoundsEvent(new Bounds(10, 90, 20, 180)));

		mockView.clear();
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(12, 22, 110, 220)));
		assertTrue(mockView.showInvoked);
		assertFalse(mockView.hideInvoked);

		mockView.clear();
		testHM.fireEvent(new ViewportMouseMoveEvent(new MockMouseMoveEvent(8, 22, 110, 220)));
		assertFalse(mockView.showInvoked);
		assertTrue(mockView.hideInvoked);
	}


}
