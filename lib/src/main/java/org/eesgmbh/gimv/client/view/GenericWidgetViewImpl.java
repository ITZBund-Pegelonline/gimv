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

package org.eesgmbh.gimv.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import org.eesgmbh.gimv.shared.util.Bounds;

/**
 * An implementation of {@link GenericWidgetView}.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class GenericWidgetViewImpl implements GenericWidgetView {

	private final Widget widget;

	public GenericWidgetViewImpl(Widget widget) {
		this.widget = widget;
	}

	public int getAbsX() {
		return widget.getAbsoluteLeft();
	}

	public void setRelX(int x) {
		DOM.setStyleAttribute(widget.getElement(), "left", x + "px");
	}

	public int getAbsY() {
		return widget.getAbsoluteTop();
	}

	public void setRelY(int y) {
		DOM.setStyleAttribute(widget.getElement(), "top", y + "px");
	}

	public int getWidth() {
		return widget.getOffsetWidth();
	}

	public void setWidth(int width) {
		widget.setWidth(width + "px");
	}

	public int getHeight() {
		return widget.getOffsetHeight();
	}

	public void setHeight(int height) {
		widget.setHeight(height + "px");
	}

	public Bounds getAbsBounds() {
		return new Bounds(getAbsX(), getAbsX() + getWidth(), getAbsY(), getAbsY() + getHeight());
	}

	/**
	 * Can only be invoked if the internal widget implements
	 * the {@link HasHTML} interface.
	 *
	 * @param html
	 */
	public void setHtml(String html) {
		((HasHTML) widget).setHTML(html);
	}

	/**
	 * Can only be invoked if the internal widget implements
	 * the {@link HasHTML} interface.
	 */
	public String getHtml() {
		return ((HasHTML) widget).getHTML();
	}

	public void setZIndex(int zIndex) {
		DOM.setStyleAttribute(widget.getElement(), "zIndex", String.valueOf(zIndex));
	}

	public void hide() {
		//cannot use widget.setVisible() as it uses css directive display, which does not work here
		DOM.setStyleAttribute(widget.getElement(), "visibility", "hidden");
	}

	public void show() {
		//cannot use widget.setVisible() as it uses css directive display, which does not work here
		DOM.setStyleAttribute(widget.getElement(), "visibility", "visible");
	}
}
