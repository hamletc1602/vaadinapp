package org.test;

import java.util.logging.Logger;

import org.test.model.SignalService;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class GroupButtonsUi {
    private static final Logger LOGGER = Logger.getLogger(GroupButtonsUi.class.getName());

    private MyUI ui;
    private HorizontalLayout groupBtns;
    private Button dayGroup;
    private Button monthGroup;
    private Button yearGroup;
	
    public GroupButtonsUi(MyUI ui) {
        this.ui = ui;
    }
    
    /** Return the existing chart. */
    public Layout getPanel() {
    	return groupBtns;
    }

    // Create and configure the grouping buttons in a separate horizontal layout
    // (This is a very cheezy impl. of a button bar - TODO: Replace with OOB component)
    public Layout createPanel(SignalService.GroupBy group) {
    	//
    	dayGroup = new Button("Day");
    	dayGroup.setHeight("25px");
        dayGroup.addClickListener( e -> {
        	LOGGER.info("Day grouping selected");
        	ui.setGroup(SignalService.GroupBy.DAY);
        	dayGroup.setEnabled(false);
        	monthGroup.setEnabled(true);
        	yearGroup.setEnabled(true);
        });
        
        //
        monthGroup = new Button("Month");
        monthGroup.setHeight("25px");
        monthGroup.addClickListener( e -> {
        	LOGGER.info("Month grouping selected");
        	ui.setGroup(SignalService.GroupBy.MONTH);
        	dayGroup.setEnabled(true);
        	monthGroup.setEnabled(false);
        	yearGroup.setEnabled(true);
        });
        
        //
        yearGroup = new Button("Year");
        yearGroup.setHeight("25px");
        yearGroup.addClickListener( e -> {
        	LOGGER.info("Year grouping selected");
        	ui.setGroup(SignalService.GroupBy.YEAR);
        	dayGroup.setEnabled(true);
        	monthGroup.setEnabled(true);
        	yearGroup.setEnabled(false);
        });
        
        // Disable the selected button
        switch (group) {
	        case DAY: dayGroup.setEnabled(false); break;
	        case MONTH: monthGroup.setEnabled(false); break;
	        case YEAR: yearGroup.setEnabled(false); break;
        }
        
        // Create panel
        groupBtns = new HorizontalLayout();
        groupBtns.setSpacing(true);
        groupBtns.addComponents(new Label("Group by: "), dayGroup, monthGroup, yearGroup);

        return groupBtns;
    }
}
