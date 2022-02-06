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

import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;


public class ImageMoveOrZoomToggleButtonPresenterTest extends AbstractGimvUnitTest {

	private ImageMoveOrZoomToggleButtonPresenter presenter;
	private MockView mockView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockView();
			this.presenter = new ImageMoveOrZoomToggleButtonPresenter(testHM, mockView);
		}
	}

	@Test
	public void testSetViewToMoveOrZoomState() throws Exception {
		mockView.clear();
		testHM.fireEvent(StateChangeEvent.createMove());
		assertTrue(mockView.toggleMoveInvoked);
		assertFalse(mockView.toggleZoomInvoked);

		mockView.clear();
		testHM.fireEvent(StateChangeEvent.createZoom());
		assertFalse(mockView.toggleMoveInvoked);
		assertTrue(mockView.toggleZoomInvoked);
	}

	@Test
	public void testStateChangeEventOnViewClick() throws Exception {
		mockView.clear();
		mockView.clickMove();

		assertTrue(testEH.stateChangeEvent.isMove());
		assertTrue(mockView.toggleMoveInvoked);
		assertFalse(mockView.toggleZoomInvoked);

		mockView.clear();
		mockView.clickZoom();

		assertTrue(testEH.stateChangeEvent.isZoom());
		assertFalse(mockView.toggleMoveInvoked);
		assertTrue(mockView.toggleZoomInvoked);
	}

	private class MockView implements ImageMoveOrZoomToggleButtonPresenter.View {
		private boolean toggleMoveInvoked;
		private boolean toggleZoomInvoked;
		private ClickHandler moveClickHandler;
		private ClickHandler zoomClickHandler;

		private void clear() {
			toggleMoveInvoked = false;
			toggleZoomInvoked = false;
		}

		private void clickMove() {
			moveClickHandler.onClick(null);
		}

		private void clickZoom() {
			zoomClickHandler.onClick(null);
		}

		public void toggleMove() {
			toggleMoveInvoked = true;
		}

		public void toggleZoom() {
			toggleZoomInvoked = true;
		}

		public void addMoveClickHandler(ClickHandler clickHandler) {
			moveClickHandler = clickHandler;
		}

		public void addZoomClickHandler(ClickHandler clickHandler) {
			zoomClickHandler = clickHandler;
		}
	}
}
