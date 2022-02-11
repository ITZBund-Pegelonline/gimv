package org.eesgmbh.gimv.samples.jfreechart.shared;

import org.eesgmbh.gimv.shared.util.Bounds;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ImageDataRequest implements IsSerializable {

	/**
	 * contains value range (vertical) and time range
	 * in ms (horizontal)
	 */
	private Bounds bounds;

	private int width, height;

	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private boolean generateLegend;
	private boolean generateTooltips;
	private boolean showRangeAxis;
	private boolean noPlotInsets;

	@SuppressWarnings("unused")
	private ImageDataRequest() {
	}

	public ImageDataRequest(int width, int height, String title, String xAxisLabel, String yAxisLabel, boolean generateLegend,	boolean generateTooltips, boolean showRangeAxis, boolean noPlotInsets) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.generateLegend = generateLegend;
		this.generateTooltips = generateTooltips;
		this.showRangeAxis = showRangeAxis;
		this.noPlotInsets = noPlotInsets;
	}

	public ImageDataRequest(Bounds bounds, int width, int height, String title, String xAxisLabel, String yAxisLabel, boolean generateLegend, boolean generateTooltips, boolean showRangeAxis, boolean noPlotInsets) {
		this.bounds = bounds;
		this.width = width;
		this.height = height;
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.generateLegend = generateLegend;
		this.generateTooltips = generateTooltips;
		this.showRangeAxis = showRangeAxis;
		this.noPlotInsets = noPlotInsets;
	}

	public Bounds getBounds() {
		return this.bounds;
	}
	public void setBounds(Bounds bounds) {
		setDomainBounds(bounds);
	}

	public void setDomainBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public int getWidth() {
		return this.width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public String getTitle() {
		return this.title;
	}

	public String getxAxisLabel() {
		return this.xAxisLabel;
	}

	public String getyAxisLabel() {
		return this.yAxisLabel;
	}

	public boolean generateLegend() {
		return this.generateLegend;
	}

	public boolean generateTooltips() {
		return this.generateTooltips;
	}

	public boolean showRangeAxis() {
		return this.showRangeAxis;
	}

	public boolean noPlotInsets() {
		return this.noPlotInsets;
	}
}
