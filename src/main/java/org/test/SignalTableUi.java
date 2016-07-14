package org.test;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.test.model.Signal;
import org.test.model.State;

import com.vaadin.ui.Table;

public class SignalTableUi {
    private static final Logger LOGGER = Logger.getLogger(SignalTableUi.class.getName());

    private MyUI ui;
    private Table table;

    public SignalTableUi(MyUI myUI) {
        this.ui = myUI;
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
