package org.eesgmbh.gimv.samples.jfreechart.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataService;
import org.eesgmbh.gimv.samples.jfreechart.shared.CommonSettings;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataRequest;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataResponse;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class JFreechartSampleServiceImpl extends RemoteServiceServlet implements JFreechartSampleDataService {

	private final HttpClient pegelonlineHttpClient = HttpClient.newHttpClient();
	private final ObjectMapper pegelonlineMeasurementsObjectMapper = new ObjectMapper().registerModules(new JavaTimeModule());

	@SuppressWarnings("deprecation")
	public ImageDataResponse getImageData(ImageDataRequest imageDataRequest) {
		try {
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
					imageDataRequest.getTitle(), // title
					imageDataRequest.getxAxisLabel(), // x-axis label
					imageDataRequest.getyAxisLabel(), // y-axis label
					null, //no dataset yet
					imageDataRequest.generateLegend(), // create legend?
					imageDataRequest.generateTooltips(), // generate tooltips?
					false // generate URLs?
			);

			XYDataset dataset1 = createPegelonlineSampleDataset(imageDataRequest, new DatasetGroup("1"));

			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setDataset(0, dataset1);

			plot.getDomainAxis().setRange(new Range(imageDataRequest.getBounds().getLeft(), imageDataRequest.getBounds().getRight()));
			plot.getRangeAxis().setRange(new Range(imageDataRequest.getBounds().getBottom(), imageDataRequest.getBounds().getTop()));
			plot.getRangeAxis().setVisible(imageDataRequest.showRangeAxis());
			plot.getRenderer().setPaint(new Color(68, 89, 139, 255));
			plot.getRenderer().setStroke(new BasicStroke(1.5f));
			if (imageDataRequest.noPlotInsets()) {
				plot.setInsets(new RectangleInsets(0, 0, 0, 0));
			}

			ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());

			String filename = ServletUtilities.saveChartAsPNG(chart, imageDataRequest.getWidth(), imageDataRequest.getHeight(), renderingInfo, null);

			Rectangle2D plotDataArea = renderingInfo.getPlotInfo().getDataArea();

			ImageDataResponse imageDataResponse = new ImageDataResponse(
					"gimvsamples_jfreechart/jfreechart/image?filename=" + filename,
					imageDataRequest.getBounds(),
					CommonSettings.MAX_BOUNDS,
					new Bounds(plotDataArea.getMinX(), plotDataArea.getMaxX(), plotDataArea.getMinY(), plotDataArea.getMaxY()),
					createImageEntities(renderingInfo.getEntityCollection()));

			return imageDataResponse;

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

	}

	private XYDataset createPegelonlineSampleDataset(ImageDataRequest imageDataRequest, DatasetGroup datasetGroup) throws IOException, InterruptedException, ParseException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://pegelonline.wsv.de/webservices/rest-api/v2/stations/MAXAU/W/measurements.json?start=P" + CommonSettings.MAX_RANGE_IN_DAYS + "D"))
				.GET()
				.build();

		String response = pegelonlineHttpClient.send(request, BodyHandlers.ofString()).body();

		List<PegelonlineMeasurement> measurements = pegelonlineMeasurementsObjectMapper.readValue(response, new TypeReference<>() {});

		TimeSeries timeSeries = new TimeSeries("MAXAU", Minute.class);
		for (PegelonlineMeasurement measurement : measurements) {
			if (imageDataRequest.getBounds().containsHorizontally(measurement.timestamp.toEpochSecond()*1000) && imageDataRequest.getBounds().containsVertically(measurement.value)) {
				timeSeries.add(new Minute(Date.from(measurement.timestamp.toInstant())), measurement.value);
			}
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(timeSeries);
		dataset.setGroup(datasetGroup);

		return dataset;
	}

	private ArrayList<ImageEntity> createImageEntities(EntityCollection entities) {
		ArrayList<ImageEntity> imageEntities = new ArrayList<ImageEntity>();

		for (Iterator iter = entities.iterator(); iter.hasNext();) {
			Object o = iter.next();

			if (o instanceof XYItemEntity) {
				XYItemEntity e = (XYItemEntity) o;

				ImageEntity imageEntity = new ImageEntity(
						new Bounds(
								e.getArea().getBounds2D().getMinX(),
								e.getArea().getBounds2D().getMaxX(),
								e.getArea().getBounds2D().getMinY(),
								e.getArea().getBounds2D().getMaxY()),
						e.getDataset().getGroup().getID());

				double time = e.getDataset().getXValue(e.getSeriesIndex(), e.getItem());
				double value = e.getDataset().getYValue(e.getSeriesIndex(), e.getItem());

				imageEntity.putHoverHtmlFragment("<span style=\"background-color:yellow; \">" + value + " on " + new Date((long)time) + "</span>");

				imageEntities.add(imageEntity);
			}
		}

		return imageEntities;
	}

	public static class PegelonlineMeasurement {
		private OffsetDateTime timestamp;
		private Double value;

		public OffsetDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(OffsetDateTime timestamp) {
			this.timestamp = timestamp;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}
	}
}
