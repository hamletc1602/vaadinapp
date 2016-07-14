package org.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.test.model.Signal;
import org.test.model.State;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.RangeSelector;
import com.vaadin.addon.charts.model.RangeSelectorButton;
import com.vaadin.addon.charts.model.RangeSelectorTimespan;
import com.vaadin.addon.charts.model.Series;

public class SignalChartUi {
    private static final Logger LOGGER = Logger.getLogger(SignalChartUi.class.getName());

    private MyUI myUI;
    private Chart chart;

    public SignalChartUi(MyUI myUI) {
        this.myUI = myUI;
    }
    
    /** Return the existing chart. */
    public Chart getChart() {
    	return chart;
    }
    
    /** Add (or refresh) all data series to the chart. */
    public void setData(List<State> states, List<Signal> signals) {
    	if (chart == null) {
    		LOGGER.warning("Signal chart setData invoked before chart created");
    		return;
    	}
        ArrayList<Series> series = new ArrayList<>();
        for (State state : states) {
	        DataSeries dataSeries = new DataSeries(state.getName());
	        for(Signal signal : signals) {
	            DataSeriesItem item = new DataSeriesItem();
	            LOGGER.finer("Add Chart point: " + signal.getDate().toString() + ", " + signal.getCount(state.getName()));
	            item.setX(signal.getDate());
	            item.setY(signal.getCount(state.getName()));
	            dataSeries.add(item);
	        }
	        series.add(dataSeries);
        }
        chart.getConfiguration().setSeries(series);
        chart.drawChart(chart.getConfiguration());
    }

    public Chart createChart(List<State> states, List<Signal> signals) {
        chart = new Chart(ChartType.COLUMN);
        
        if (myUI.getShowTable()) {
	        chart.setHeight("650px");
        } else {
	        chart.setHeight("100%");
	        chart.setSizeFull();
        }
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Asset States");
        
        configuration.getLegend();

        RangeSelector rangeSelector = new RangeSelector();
        RangeSelectorButton[] buttons = new RangeSelectorButton[4];
        buttons[0] = new RangeSelectorButton(RangeSelectorTimespan.DAY, 1, "1d");
        buttons[1] = new RangeSelectorButton(RangeSelectorTimespan.MONTH, 1, "1m");
        buttons[2] = new RangeSelectorButton(RangeSelectorTimespan.YEAR, 1, "1y");
        buttons[3] = new RangeSelectorButton(RangeSelectorTimespan.ALL, "All");
        rangeSelector.setButtons(buttons);
        rangeSelector.setSelected(3);
        configuration.setRangeSelector(rangeSelector);

        setData(states, signals);

        return chart;
    }
}
