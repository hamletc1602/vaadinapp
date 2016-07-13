package org.test;

import java.util.ArrayList;
import java.util.Date;
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
import com.vaadin.ui.Table;

public class SignalTableUi {
    private static final Logger LOGGER = Logger.getLogger(SignalTableUi.class.getName());

    SignalService signalService = MockSignalService.getInstance();
    private MyUI myUI;
    private Table table;

    public SignalTableUi(MyUI myUI) {
        this.myUI = myUI;
    }
    
    /** Return the existing chart. */
    public Table getTable() {
    	return table;
    }
    
    /** Add (or refresh) all data series to the chart. */
    public void setData(List<State> states, List<Signal> signals) {
    	if (table == null) {
    		LOGGER.warning("Signal chart setData invoked before chart created");
    		return;
    	}
    	
    	table.removeAllItems();

    	int id = 0;
        for(Signal signal : signals) {
        	Object[] data = new Object[states.size() + 2];
        	data[0] = signal.getDate();
        	int i = 1;
        	for (State state : states) {
        		data[i] = signal.getCount(state.getName());
        		i++;
            }
        	data[states.size() + 1] = signal.getTotal();
        	table.addItem(data, id);
        	id++;
        }

        table.setPageLength(table.size());     
        
    }

    public Table createTable(List<State> states, List<Signal> signals) {
        table = new Table("Signal Data for Chart");
        
        table.setWidth("100%");
        
        table.addContainerProperty("Date", Date.class, null);
    	for (State state : states) {
            table.addContainerProperty(state.getName(), Integer.class, null);
        }
        table.addContainerProperty("Total", Integer.class, null);

        setData(states, signals);

        return table;
    }
}
