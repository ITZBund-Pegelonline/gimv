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

import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadHandler;


public class ImagePresenterTest extends AbstractGimvUnitTest {

	private ImagePresenter presenter;
	private MockView mockView;

	@Before
	public void setUp() {
		if (this.presenter == null) {
			mockView = new MockView();
			this.presenter = new ImagePresenter(testHM, mockView);
		}
	}

	@Test
	public void testSetUrlInView() throws Exception {
		mockView.clear();

		testHM.fireEvent(new SetImageUrlEvent("myUrl"));
		assertEquals("myUrl", mockView.url);
	}

	@Test
	public void testSetImagePositionAndDimensionsInView() throws Exception {
		mockView.clear();
		testHM.fireEvent(new ChangeImagePixelBoundsEvent(10, 20));
		assertEquals(10, mockView.offsetX);
		assertEquals(20, mockView.offsetY);
		assertEquals(0, mockView.offsetWidth);
		assertEquals(0, mockView.offsetHeight);

		mockView.clear();
		testHM.fireEvent(new ChangeImagePixelBoundsEvent(0, 4, -5, -12));
		assertEquals(0, mockView.offsetX);
		assertEquals(4, mockView.offsetY);
		assertEquals(-5, mockView.offsetWidth);
		assertEquals(-12, mockView.offsetHeight);
	}

	@Test
	public void testSetImagePositionInViewAfterImageLoad() throws Exception {
		mockView.clear();
		mockView.loadHandler.onLoad(null);

		assertEquals(0, mockView.x);
		assertEquals(0, mockView.y);
		assertEquals(Integer.MAX_VALUE, mockView.width); //not invoked
		assertEquals(Integer.MAX_VALUE, mockView.height); //not invoked

		testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 100, 0, 200)));
		mockView.loadHandler.onLoad(null);
		assertEquals(0, mockView.x);
		assertEquals(0, mockView.y);
		assertEquals(100, mockView.width);
		assertEquals(200, mockView.height);
	}

	private class MockView implements ImagePresenter.View {
		private String url;

		private int x;
		private int y;
		private int width;
		private int height;

		private int offsetX;
		private int offsetY;
		private int offsetWidth;
		private int offsetHeight;

		private LoadHandler loadHandler;

		private void clear() {
			url = null;

			x = Integer.MAX_VALUE;
			y = Integer.MAX_VALUE;
			width = Integer.MAX_VALUE;
			height = Integer.MAX_VALUE;

			offsetX = Integer.MAX_VALUE;
			offsetY = Integer.MAX_VALUE;
			offsetWidth = Integer.MAX_VALUE;
			offsetHeight = Integer.MAX_VALUE;
		}

		public void addLoadHandler(LoadHandler loadHandler) {
			this.loadHandler = loadHandler;
		}

		public void changeDimensions(int offsetWidth, int offsetHeight) {
			this.offsetWidth = offsetWidth;
			this.offsetHeight = offsetHeight;
		}

		public void changePosition(int offsetX, int offsetY) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}

		public void setDimensions(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void addErrorHandler(ErrorHandler errorHandler) {
		}
	}
}
