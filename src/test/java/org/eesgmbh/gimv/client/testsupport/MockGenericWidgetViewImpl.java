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

package org.eesgmbh.gimv.client.testsupport;

import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.shared.util.Bounds;

public class MockGenericWidgetViewImpl implements GenericWidgetView {

	public boolean showInvoked;
	public boolean hideInvoked;
	public int setX;
	public int setY;
	public int setWidth;
	public int setHeight;
	public Bounds getBounds;

	public void clear() {
		showInvoked = false;
		hideInvoked = false;
		setX = Integer.MAX_VALUE;
		setY = Integer.MAX_VALUE;
		setWidth = Integer.MAX_VALUE;
		setHeight = Integer.MAX_VALUE;
		getBounds = null;
	}

	public void show() {
		showInvoked = true;
	}

	public void hide() {
		hideInvoked = true;
	}

	public void setRelX(int x) {
		setX = x;
	}

	public void setRelY(int y) {
		setY = y;
	}

	public void setWidth(int width) {
		setWidth = width;
	}

	public void setHeight(int height) {
		setHeight = height;
	}

	public Bounds getAbsBounds() {
		return getBounds;
	}

	public int getHeight() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public int getAbsX() {
		return 0;
	}

	public int getAbsY() {
		return 0;
	}

	public void setHtml(String html) {
	}

	public String getHtml() {
		return null;
	}

	public void setZIndex(int zIndex) {
	}
}
