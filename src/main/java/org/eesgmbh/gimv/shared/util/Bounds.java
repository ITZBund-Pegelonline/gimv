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

package org.eesgmbh.gimv.shared.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>An immutable support class for working with left, right, top and bottom bounds.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 * @author Sascha Hagedorn - EES GmbH - s.hagedorn@ees-gmbh.de
 */
public class Bounds implements IsSerializable {

	/*
	 * using wrapper types to permit null values
	 */

	private Double left = null;
	private Double right = null;
	private Double top = null;
	private Double bottom = null;

	/*
	 * results from computations, that can be cached
	 * since this class is immutable.
	 *
	 * Caching those results can speed things up significantly in Javascript
	 * especially in legacy browsers like IE 7
	 */

	private Double horizontalCenter;
	private Double verticalCenter;

	public Bounds() {
	}

	/**
	 * Constructor that takes {@link Double} values.
	 *
	 * @param left The left bound
	 * @param right The right bound
	 * @param top The top bound
	 * @param bottom The bottom bound
	 */
	public Bounds(Double left, Double right, Double top, Double bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Constructor that takes {@link Integer} values.
	 *
	 * @param left The left bound
	 * @param right The right bound
	 * @param top The top bound
	 * @param bottom The bottom bound
	 */
	public Bounds(Integer left, Integer right, Integer top, Integer bottom) {
		this(
				left != null ? left.doubleValue() : null,
				right != null ? right.doubleValue() : null,
				top != null ? top.doubleValue() : null,
				bottom != null ? bottom.doubleValue() : null
		);
	}

	/**
	 * Constructor that takes {@link Long} values.
	 *
	 * @param left The left bound
	 * @param right The right bound
	 * @param top The top bound
	 * @param bottom The bottom bound
	 */
	public Bounds(Long left, Long right, Long top, Long bottom) {
		this(
				left != null ? left.doubleValue() : null,
				right != null ? right.doubleValue() : null,
				top != null ? top.doubleValue() : null,
				bottom != null ? bottom.doubleValue() : null
		);
	}

	public boolean contains(double x, double y) {
		return containsHorizontally(x) && containsVertically(y);
	}

	/**
	 * @param values
	 * @return true if the values are within left and right or left and right is undefined, false otherwise
	 */
	public boolean containsHorizontally(double... values) {
		if (isHorizontalBoundsDefined()) {
			return contains(Math.min(left, right), Math.max(left, right), values);
		} else {
			return true;
		}
	}

	/**
	 * @param values
	 * @return true if the values are within top and bottom or top and bottom is undefined, false otherwise
	 */
	public boolean containsVertically(double... values) {
		if (isVerticalBoundsDefined()) {
			return contains(Math.min(top, bottom), Math.max(top, bottom), values);
		} else {
			return true;
		}
	}

	private boolean contains(double min, double max, double... values) {
		for (double v : values) {
			if (min > v || max < v) {
				return false;
			}
		}

		return true;
	}

	public boolean isLeftDefined() {
		return left != null;
	}

	public boolean isRightDefined() {
		return right != null;
	}

	public boolean isTopDefined() {
		return top != null;
	}

	public boolean isBottomDefined() {
		return bottom != null;
	}

	public boolean isHorizontalBoundsDefined() {
		return left != null && right != null;
	}

	public boolean isVerticalBoundsDefined() {
		return top != null && bottom != null;
	}

	public Double getHorizontalCenter() {
		if (horizontalCenter == null) {
			if (isHorizontalBoundsDefined()) {
				horizontalCenter = getLeft() + getWidth()/2;
			} else {
				horizontalCenter = null;
			}
		}

		return horizontalCenter;
	}

	public Double getVerticalCenter() {
		if (verticalCenter == null) {
			if (isVerticalBoundsDefined()) {
				verticalCenter = getTop() + getHeight()/2;
			} else {
				verticalCenter = null;
			}
		}

		return verticalCenter;
	}

	public Bounds shiftProportional(double horizontal, double vertical) {
		Bounds bounds = new Bounds(getLeft(), getRight(), getTop(), getBottom());

		if (isHorizontalBoundsDefined()) {
			bounds.left = getLeft() + (getRight() - getLeft()) * horizontal;
			bounds.right = getRight() + (getRight() - getLeft()) * horizontal;
		}

		if (isVerticalBoundsDefined()) {
			bounds.top = getTop() + (getBottom() - getTop()) * vertical;
			bounds.bottom = getBottom() + (getBottom() - getTop()) * vertical;
		}

		return bounds;
	}

	public Bounds shiftLeft(double amount) {
		return setLeft(getLeft() + amount);
	}

	public Bounds shiftRight(double amount) {
		return setRight(getRight() + amount);
	}

	public Bounds shiftTop(double amount) {
		return setTop(getTop() + amount);
	}

	public Bounds shiftBottom(double amount) {
		return setBottom(getBottom() + amount);
	}

	public Bounds shiftLeftProportionally(double proportion) {
		if (isLeftDefined()) {
			return setLeft(getLeft() + getWidth() * proportion);
		} else {
			return this;
		}
	}

	public Bounds shiftRightProportionally(double proportion) {
		if (isRightDefined()) {
			return setRight(getRight() + getWidth() * proportion);
		} else {
			return this;
		}
	}

	public Bounds shiftTopProportionally(double proportion) {
		if (isTopDefined()) {
			return setTop(getTop() + getHeight() * proportion);
		} else {
			return this;
		}
	}

	public Bounds shiftBottomProportionally(double proportion) {
		if (isBottomDefined()) {
			return setBottom(getBottom() + getHeight() * proportion);
		} else {
			return this;
		}
	}

	public Bounds shiftAbsolute(double horizontal, double vertical) {
		Bounds bounds = new Bounds(getLeft(), getRight(), getTop(), getBottom());

		if (isHorizontalBoundsDefined()) {
			bounds.left = getLeft() + horizontal;
			bounds.right = getRight() + horizontal;
		}

		if (isVerticalBoundsDefined()) {
			bounds.top = getTop() + vertical;
			bounds.bottom = getBottom() + vertical;
		}

		return bounds;
	}

	public Bounds transformProportional(Bounds transBounds) {
		Bounds newBounds = this;

		if (isHorizontalBoundsDefined() && transBounds.isHorizontalBoundsDefined()) {
			newBounds = newBounds.setLeft(getLeft() + transBounds.getLeft() * getWidth());
			newBounds = newBounds.setRight(getLeft() + transBounds.getRight() * getWidth());
		}

		if (isVerticalBoundsDefined() && transBounds.isVerticalBoundsDefined()) {
			newBounds = newBounds.setTop(getTop() + transBounds.getTop() * getHeight());
			newBounds = newBounds.setBottom(getTop() + transBounds.getBottom() * getHeight());
		}

		return newBounds;
	}

	/**
	 * Replaces left and right and top and bottom, so
	 * that left &lt;= right and top &lt;= bottom.
	 *
	 * @return A new normalized {@link Bounds} object.
	 */
	public Bounds normalizeBounds() {
		return new Bounds(
				Math.min(getLeft(), getRight()),
				Math.max(getLeft(), getRight()),
				Math.min(getTop(), getBottom()),
				Math.max(getTop(), getBottom())
		);
	}

	/**
	 * Converts the bounds proportionally from one range into another.<br/>
	 *
	 * <p>Example:
	 * <ul>
	 *   <li>this: left=10, right = 110, top=0, bottom=100
	 *   <li>reference: left=0, right = 100, top=0, bottom=100
	 *   <li>target: left=1000, right=2000, top=0, bottom=1000
	 *   <li>return: left=1100, right=2100, top=0, bottom=1000
	 * </ul>
     *
	 * @param reference The minima and maxima of the range this bound is currently related to.
	 * @param target The minima and maxima of the range this bound is going to be converted to.
	 * @return Converted bounds to <code>targetRange</code>.
	 */
	public Bounds transform(Bounds reference, Bounds target) {
		Bounds newBounds = Bounds.from(this);

		if (newBounds.isHorizontalBoundsDefined() && reference.isHorizontalBoundsDefined() && target.isHorizontalBoundsDefined()) {
			Double left   = target.getLeft()   + target.getWidth()  * ((newBounds.getLeft()   - reference.getLeft())   / reference.getWidth());
			Double right  = target.getRight()  + target.getWidth()  * ((newBounds.getRight()  - reference.getRight())  / reference.getWidth());

			newBounds = newBounds.setLeft(left);
			newBounds = newBounds.setRight(right);
		}

		if  (newBounds.isVerticalBoundsDefined() && reference.isVerticalBoundsDefined() && target.isVerticalBoundsDefined()) {
			Double top    = target.getTop()    + target.getHeight() * ((newBounds.getTop()    - reference.getTop())    / reference.getHeight());
			Double bottom = target.getBottom() + target.getHeight() * ((newBounds.getBottom() - reference.getBottom()) / reference.getHeight());

			newBounds = newBounds.setTop(top);
			newBounds = newBounds.setBottom(bottom);
		}

		return newBounds;
	}

	/**
	 * Determines the point in absolute coordinates based upon the passed in point which represents
	 * fraction values.
	 *
	 * @param relPoint
	 * @return Absolute Point or null if either horizontal or vertical bounds is undefind
	 */
	public Point findAbsolutePoint(Point relPoint) {
		if (isHorizontalBoundsDefined() && isVerticalBoundsDefined()) {
			return new Point(getLeft() + relPoint.getX() * getWidth(), getTop() + relPoint.getY() * getHeight());
		} else {
			return null;
		}
	}

	public Double getLeft() {
		return this.left;
	}

	public Double getRight() {
		return this.right;
	}

	public Double getTop() {
		return this.top;
	}

	public Double getBottom() {
		return this.bottom;
	}

	public Double getWidth() {
		if (this.left != null && this.right != null) {
			return this.right - this.left; //FIXME: +1 ?
		} else {
			return null;
		}
	}
	public Double getAbsWidth() {
		Double width = getWidth();

		if (width != null) {
			width = Math.abs(width);
		}

		return width;
	}

	public Double getHeight() {
		if (this.top != null && this.bottom != null) {
			return this.bottom - this.top; //FIXME: +1 ?
		} else {
			return null;
		}
	}

	public Double getAbsHeight() {
		Double height = getHeight();

		if (height != null) {
			height = Math.abs(height);
		}

		return height;
	}

	/**
	 * Returns a new Bounds object, whose bounds are the same except left, which is
	 * set to new value.
	 *
	 * @param left
	 * @return a new bounds object
	 */
	public Bounds setLeft(Double left) {
		return new Bounds(left, getRight(), getTop(), getBottom());
	}
	public Bounds setLeft(Long left) {
		return setLeft(left != null ? left.doubleValue() : null);
	}
	public Bounds setLeft(Integer left) {
		return setLeft(left != null ? left.doubleValue() : null);
	}

	/**
	 * Returns a new Bounds object, whose bounds are the same except right, which is
	 * set to new value.
	 *
	 * @param right
	 * @return a new bounds object
	 */
	public Bounds setRight(Double right) {
		return new Bounds(getLeft(), right, getTop(), getBottom());
	}
	public Bounds setRight(Long right) {
		return setRight(right != null ? right.doubleValue() : null);
	}
	public Bounds setRight(Integer right) {
		return setRight(right != null ? right.doubleValue() : null);
	}

	/**
	 * Returns a new Bounds object, whose bounds are the same except top, which is
	 * set to new value.
	 *
	 * @param top
	 * @return a new bounds object
	 */
	public Bounds setTop(Double top) {
		return new Bounds(getLeft(), getRight(), top, getBottom());
	}
	public Bounds setTop(Long top) {
		return setTop(top != null ? top.doubleValue() : null);
	}
	public Bounds setTop(Integer top) {
		return setTop(top != null ? top.doubleValue() : null);
	}

	/**
	 * Returns a new Bounds object, whose bounds are the same except bottom, which is
	 * set to new value.
	 *
	 * @param bottom
	 * @return a new bounds object
	 */
	public Bounds setBottom(Double bottom) {
		return new Bounds(getLeft(), getRight(), getTop(), bottom);
	}
	public Bounds setBottom(Long bottom) {
		return setBottom(bottom != null ? bottom.doubleValue() : null);
	}
	public Bounds setBottom(Integer bottom) {
		return setBottom(bottom != null ? bottom.doubleValue() : null);
	}

	/**
	 * Returns a new instance of {@link Bounds} with the same attributes as the passed <code>bounds</code> object.
	 * @param bounds Bounds to be copied
	 * @return new instance of {@link Bounds} with copied values from <code>bounds</code>
	 */
	public static Bounds from(Bounds bounds)  {
		return new Bounds(bounds.getLeft(),
				bounds.getRight(),
				bounds.getTop(),
				bounds.getBottom());
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = prime * result + ((this.bottom == null) ? 0 : this.bottom.hashCode());
		result = prime * result + ((this.left == null) ? 0 : this.left.hashCode());
		result = prime * result + ((this.right == null) ? 0 : this.right.hashCode());
		result = prime * result + ((this.top == null) ? 0 : this.top.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Bounds)) {
			return false;
		}
		Bounds other = (Bounds) obj;

		if (this.bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (!this.bottom.equals(other.bottom))
			return false;

		if (this.left == null) {
			if (other.left != null)
				return false;
		} else if (!this.left.equals(other.left))
			return false;

		if (this.right == null) {
			if (other.right != null)
				return false;
		} else if (!this.right.equals(other.right))
			return false;

		if (this.top == null) {
			if (other.top != null)
				return false;
		} else if (!this.top.equals(other.top))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Bounds [left=" + left + ", right=" + right + ", top=" + top + ", bottom=" + bottom + ", width=" + getWidth() + ", height=" + getHeight() + "]";
	}
}
