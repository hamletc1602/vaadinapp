package org.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.test.model.MockSignalService;
import org.test.model.Signal;
import org.test.model.SignalService;
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
import com.vaadin.ui.Component;

public class DataBrowserChartUi {
    private static final Logger LOGGER = Logger.getLogger(DataBrowserChartUi.class.getName());

    SignalService signalService = MockSignalService.getInstance();
    private MyUI myUI;
    private List<Signal> signals;
    private List<State> states;

    public DataBrowserChartUi(MyUI myUI) {
        this.myUI = myUI;
    }
    
    public void setData(List<State> states, List<Signal> signals) {
    	this.states = states;
    	this.signals = signals;
    }

    protected Component getChart() {
        final Chart chart = new Chart(ChartType.COLUMN);
        chart.setHeight("450px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Asset States");

        ArrayList<Series> series = new ArrayList<>();
        for (State state : states) {
	        DataSeries dataSeries = new DataSeries();
	        for(Signal signal : signals) {
	            DataSeriesItem item = new DataSeriesItem();
	            LOGGER.finer("Add Chart point: " + signal.getDate().toString() + ", " + signal.getCount(state.getName()));
	            item.setX(signal.getDate());
	            item.setY(signal.getCount(state.getName()));
	            dataSeries.add(item);
	        }
	        series.add(dataSeries);
        }
	    configuration.setSeries(series);

        RangeSelector rangeSelector = new RangeSelector();
        
        RangeSelectorButton[] buttons = new RangeSelectorButton[4];
        buttons[0] = new RangeSelectorButton(RangeSelectorTimespan.DAY, 1, "1d");
        buttons[1] = new RangeSelectorButton(RangeSelectorTimespan.MONTH, 1, "1m");
        buttons[2] = new RangeSelectorButton(RangeSelectorTimespan.YEAR, 1, "1y");
        buttons[3] = new RangeSelectorButton(RangeSelectorTimespan.ALL, "All");
        rangeSelector.setButtons(buttons);
        rangeSelector.setSelected(1);
        configuration.setRangeSelector(rangeSelector);

        chart.drawChart(configuration);

        return chart;
    }
}
