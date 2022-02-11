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

import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import org.eesgmbh.gimv.client.presenter.ImagePresenter.View;

/**
 * An implementation of {@link View}.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ImageViewImpl implements View {

	private Image image;

	public ImageViewImpl(Image image) {
		this.image = image;
	}

	public void setUrl(String url) {
		this.image.setUrl(url);
	}

	public void changePosition(int offsetX, int offsetY) {
		setPosition(
				DOM.getIntStyleAttribute(image.getElement(), "left") + offsetX,
				DOM.getIntStyleAttribute(image.getElement(), "top") + offsetY);
	}

	public void setPosition(int x, int y) {
		DOM.setStyleAttribute(image.getElement(), "left", x + "px");
		DOM.setStyleAttribute(image.getElement(), "top", y + "px");
	}

	public void changeDimensions(int offsetWidth, int offsetHeight) {
		setDimensions(image.getWidth() + offsetWidth, image.getHeight() + offsetHeight);
	}

	public void setDimensions(int width, int height) {
		if (width >= 0) {
			image.setWidth(width + "px");
		}

		if (height >= 0) {
			image.setHeight(height + "px");
		}
	}

	public void addLoadHandler(LoadHandler loadHandler) {
		this.image.addLoadHandler(loadHandler);
	}

	public void addErrorHandler(ErrorHandler errorHandler) {
		this.image.addErrorHandler(errorHandler);
	}
}
