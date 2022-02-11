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

import static org.junit.Assert.*;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;


public class BoundsShiftPresenterTest extends AbstractGimvUnitTest {

	private BoundsShiftPresenter presenter;
	private MockView mockView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockView();
			this.presenter = new BoundsShiftPresenter(testHM, mockView);
		}

		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));
		testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 1000, 0, 1000)));
	}

	@Test
	public void testSetDomainBoundsEventWithAbsoluteShift() throws Exception {
		presenter.configureAbsoluteShift(10, 0);
		mockView.click();
		assertEquals(new Bounds(10, 110, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureAbsoluteShift(-10, 0);
		mockView.click();
		assertEquals(new Bounds(0, 100, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureAbsoluteShift(0, 10);
		mockView.click();
		assertEquals(new Bounds(0, 100, 10, 110), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureAbsoluteShift(0, -10);
		mockView.click();
		assertEquals(new Bounds(0, 100, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureAbsoluteShift(10, 10);
		mockView.click();
		assertEquals(new Bounds(10, 110, 10, 110), testEH.setDomainBoundsEvent.getBounds());
	}

	@Test
	public void testSetDomainBoundsEventWithProportionalShift() throws Exception {
		presenter.configureProportionalShift(0.1, 0);
		mockView.click();
		assertEquals(new Bounds(10, 110, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureProportionalShift(-0.1, 0);
		mockView.click();
		assertEquals(new Bounds(0, 100, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureProportionalShift(0, 0.1);
		mockView.click();
		assertEquals(new Bounds(0, 100, 10, 110), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureProportionalShift(0, -0.1);
		mockView.click();
		assertEquals(new Bounds(0, 100, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		presenter.configureProportionalShift(0.1, 0.1);
		mockView.click();
		assertEquals(new Bounds(10, 110, 10, 110), testEH.setDomainBoundsEvent.getBounds());
	}


	@Test
	public void testSetImagePositionWithAbsoluteShift() throws Exception {
		presenter.configureAbsoluteShift(10, 0);
		mockView.click();
		assertSetImagePositionEvent(-100d, 0d);

		presenter.configureAbsoluteShift(-10, 0);
		mockView.click();
		assertSetImagePositionEvent(100d, 0d);

		presenter.configureAbsoluteShift(0, 10);
		mockView.click();
		assertSetImagePositionEvent(0d, -100d);

		presenter.configureAbsoluteShift(0, -10);
		mockView.click();
		assertSetImagePositionEvent(0d, 100d);

		presenter.configureAbsoluteShift(10, 10);
		mockView.click();
		assertSetImagePositionEvent(-100d, -100d);
	}

	@Test
	public void testSetImagePositionWithProportionalShift() throws Exception {
		presenter.configureProportionalShift(0.1, 0);
		mockView.click();
		assertSetImagePositionEvent(-100d, 0d);

		presenter.configureProportionalShift(-0.1, 0);
		mockView.click();
		assertSetImagePositionEvent(100d, 0d);

		presenter.configureProportionalShift(0, 0.1);
		mockView.click();
		assertSetImagePositionEvent(0d, -100d);

		presenter.configureProportionalShift(0, -0.1);
		mockView.click();
		assertSetImagePositionEvent(0d, 100d);

		presenter.configureProportionalShift(0.1, 0.1);
		mockView.click();
		assertSetImagePositionEvent(-100d, -100d);
	}

	private void assertSetImagePositionEvent(double xOffset, double yOffset) {
		assertNotNull(testEH.changeImagePixelBoundsEvent);

		assertEquals(xOffset, testEH.changeImagePixelBoundsEvent.getOffsetX(), 0.001);
		assertEquals(yOffset, testEH.changeImagePixelBoundsEvent.getOffsetY(), 0.001);
		assertEquals(0d, testEH.changeImagePixelBoundsEvent.getOffsetHeight(), 0.001);
		assertEquals(0d, testEH.changeImagePixelBoundsEvent.getOffsetWidth(), 0.001);
	}

	@Test
	public void testLoadImageDataEvent() throws Exception {
		assertNull(testEH.loadImageDataEvent);

		mockView.click();
		assertNull(testEH.loadImageDataEvent); //not yet configured

		presenter.configureAbsoluteShift(10, 0);

		presenter.setFireLoadImageDataEvent(false);
		mockView.click();
		assertNull(testEH.loadImageDataEvent);

		presenter.setFireLoadImageDataEvent(true);
		mockView.click();
		assertNotNull(testEH.loadImageDataEvent);
	}

	private class MockView implements BoundsShiftPresenter.View {
		private ClickHandler clickHandler;

		public void addClickHandler(ClickHandler clickHandler) {
			this.clickHandler = clickHandler;
		}

		private void click() {
			this.clickHandler.onClick(null);
		}
	}
}
