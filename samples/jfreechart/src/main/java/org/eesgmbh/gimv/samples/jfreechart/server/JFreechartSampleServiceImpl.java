package org.eesgmbh.gimv.samples.jfreechart.server;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataService;
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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class JFreechartSampleServiceImpl extends RemoteServiceServlet implements JFreechartSampleDataService {

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

			XYDataset dataset1 = createSampleDataset(imageDataRequest, new DatasetGroup("1"), 0);
			XYDataset dataset2 = createSampleDataset(imageDataRequest, new DatasetGroup("2"), 50);

			((XYPlot)chart.getPlot()).setDataset(0, dataset1);
			((XYPlot)chart.getPlot()).setDataset(1, dataset2);

			((XYPlot)chart.getPlot()).getDomainAxis().setRange(new Range(imageDataRequest.getBounds().getLeft(), imageDataRequest.getBounds().getRight()));
			((XYPlot)chart.getPlot()).getRangeAxis().setRange(new Range(imageDataRequest.getBounds().getBottom(), imageDataRequest.getBounds().getTop()));
			((XYPlot)chart.getPlot()).getRangeAxis().setVisible(imageDataRequest.showRangeAxis());
			if (imageDataRequest.noPlotInsets()) {
				((XYPlot)chart.getPlot()).setInsets(new RectangleInsets(0, 0, 0, 0));
			}

			ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());

			String filename = ServletUtilities.saveChartAsPNG(chart, imageDataRequest.getWidth(), imageDataRequest.getHeight(), renderingInfo, null);

			Rectangle2D plotDataArea = renderingInfo.getPlotInfo().getDataArea();

			ImageDataResponse imageDataResponse = new ImageDataResponse(
					"gimvsamples_jfreechart/jfreechart/image?filename=" + filename,
					imageDataRequest.getBounds(),
					new Bounds(new Date(110, 1, 23, 0, 15).getTime(), new Date(110, 1, 27, 23, 45).getTime(), null, null),
					new Bounds(plotDataArea.getMinX(), plotDataArea.getMaxX(), plotDataArea.getMinY(), plotDataArea.getMaxY()),
					createImageEntities(renderingInfo.getEntityCollection()));

			return imageDataResponse;

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

	}

	private SimpleTimeZone mezZone = new SimpleTimeZone(3600000, "CET");

	private XYDataset createSampleDataset(ImageDataRequest imageDataRequest, DatasetGroup datasetGroup, int valueOffset) throws IOException, ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(mezZone);

		final NumberFormat numberFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));

		TimeSeries timeSeries = new TimeSeries("A sample timeseries", Minute.class);

		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("sampletimeseries.dat")));
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				} else if (!line.startsWith("#") && line.trim().length() > 0){
					String[] items = line.split(" ");

					Date date = dateFormat.parse(items[0]);
					Number value = numberFormat.parse(items[1]);

					if (imageDataRequest.getBounds().containsHorizontally(date.getTime()) && imageDataRequest.getBounds().containsVertically(value.longValue() + valueOffset)) {
						timeSeries.add(new Minute(date, mezZone), value.doubleValue() + valueOffset);
					}
				}
			}
		} finally {
			reader.close();
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
}
