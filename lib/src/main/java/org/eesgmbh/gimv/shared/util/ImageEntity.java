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

package org.eesgmbh.gimv.shared.util;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashMap;


/**
 * <p>An entitity is some data of interest that is displayed on an image.
 *
 * <p>When displaying a time series chart an entitity is a single data point.
 * In maps an entity is a feature e.g. a point of interest
 *
 * <p>An entity has a bounding box that describes its position within the image
 * in pixel coordinates.
 *
 * <p>An entity can contain any number of HTML fragments that provide information about the
 * entity. The respective HTML fragment can then be displayed under different circumstances, e.g.
 * there is one html snippet that will be activated when user hovers over the entity and another
 * one that will be displayed when the user clicks on the entity.<br>
 * The HTML snippets are saved in a Map and three default keys are provided within this class: {@link #STATIC_HTML_FRAGMENT_KEY},
 * {@link #HOVER_HTML_FRAGMENT_KEY}, {@link #CLICK_HTML_FRAGMENT_KEY}<br>
 * The presenters and views that are reponsible for displaying tooltips should respect the different types
 * of HTML fragments. If no HTML is defined, e.g. for #CLICK_HTML_SNIPPET_KEY no tooltip should be displayed when the user clicks
 * on the entity.<br>
 * Any number of application specific Tooltips can be defined here, who in turn will require application specific tooltip presenters.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 * TODO: consider using enums instead of Strings as keys
 */
public class ImageEntity implements IsSerializable {

	/**
	 * The html fragment that is always being displayed
	 */
	public final static String STATIC_HTML_FRAGMENT_KEY = "static html fragment key";

	/**
	 * The html fragment that is displayed when the user hovers over the area of the image entity
	 */
	public final static String HOVER_HTML_FRAGMENT_KEY = "hover html fragment key";

	/**
	 * The html fragment that is displayed when the user clicks on the area of the image entity
	 */
	public final static String CLICK_HTML_FRAGMENT_KEY = "click html fragment key";

	/**
	 * bounds in pixel coordinates
	 */
	private Bounds bounds;

	/**
	 * <p>Groups related image entities together. Must be set during construction. If you need to
	 * display several tooltips for different timeseries within one image, the group id must
	 * be different.
	 *
	 * <p>Is a string to ease GWT serialisation
	 */
	private String groupId;

	private HashMap<Object, String> htmlFragments = new HashMap<Object, String>();

	@SuppressWarnings("unused")
	private ImageEntity() {
	}

	public ImageEntity(Bounds bounds, String groupId) {
		this.bounds = Validate.notNull(bounds, "bounds must not be null");
		this.groupId = Validate.notNull(groupId, "groudId must not be null");
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public HashMap<Object, String> getHtmlFragments() {
		return this.htmlFragments;
	}

	/**
	 * convenience access.
	 *
	 * @return the html fragment to be displayed at all times
	 */
	public String getStaticHtmlFragment() {
		return this.htmlFragments.get(STATIC_HTML_FRAGMENT_KEY);
	}

	/**
	 * convenience access.
	 *
	 * @param html the html fragment to be displayed at all times
	 */
	public void putStaticHtmlFragment(String html) {
		this.htmlFragments.put(STATIC_HTML_FRAGMENT_KEY, html);
	}

	/**
	 * convenience access.
	 *
	 * @return the html fragment to be displayed with on hover
	 */
	public String getHoverHtmlFragment() {
		return this.htmlFragments.get(HOVER_HTML_FRAGMENT_KEY);
	}

	/**
	 * convenience access.
	 *
	 * @param html the html fragment to be displayed with on hover
	 */
	public void putHoverHtmlFragment(String html) {
		this.htmlFragments.put(HOVER_HTML_FRAGMENT_KEY, html);
	}

	/**
	 * convenience access.
	 *
	 * @return the html fragment to be displayed with on click
	 */
	public String getClickHtmlFragment() {
		return this.htmlFragments.get(CLICK_HTML_FRAGMENT_KEY);
	}

	/**
	 * convenience access.
	 *
	 * @param html the html fragment to be displayed with on click
	 */
	public void putClickHtmlFragment(String html) {
		this.htmlFragments.put(CLICK_HTML_FRAGMENT_KEY, html);
	}
}
