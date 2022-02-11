package org.eesgmbh.gimv.samples.jfreechart.shared;

import java.util.ArrayList;

import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ImageDataResponse implements IsSerializable {

	private String imageUrl;

	/**
	 * Defines the domain bounds (values and time) that was rendered
	 */
	private Bounds domainBounds;

	/**
	 * The bounds of the plot (the part of the image, where the actual timeseries line is, excluding axis, titles and the like)
	 */
	private Bounds plotArea;

	/**
	 * The maximum date range, that can be rendered
	 */
	private Bounds maxDomainBounds;

	private ArrayList<ImageEntity> imageEntities; //less js-code produced when using concrete classes

	@SuppressWarnings("unused")
	private ImageDataResponse() {
	}

	public ImageDataResponse(String imageUrl, Bounds domainBounds, Bounds maxBounds, Bounds plotArea, ArrayList<ImageEntity> imageEntities) {
		this.imageUrl = imageUrl;
		this.domainBounds = domainBounds;
		this.maxDomainBounds = maxBounds;
		this.plotArea = plotArea;
		this.imageEntities = imageEntities;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public Bounds getDomainBounds() {
		return this.domainBounds;
	}

	public Bounds getMaxDomainBounds() {
		return this.maxDomainBounds;
	}

	public Bounds getPlotArea() {
		return this.plotArea;
	}

	public ArrayList<ImageEntity> getImageEntities() {
		return this.imageEntities;
	}
}