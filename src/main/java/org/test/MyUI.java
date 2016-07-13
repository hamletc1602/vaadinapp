package org.test;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.test.model.Asset;
import org.test.model.AssetService;
import org.test.model.AssetServiceImpl;
import org.test.model.LoadData;
import org.test.model.MockAssetService;
import org.test.model.MockSignalService;
import org.test.model.MockStateService;
import org.test.model.Signal;
import org.test.model.SignalService;
import org.test.model.SignalServiceImpl;
import org.test.model.State;
import org.test.model.StateService;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("org.test.MyAppWidgetset")
public class MyUI extends UI {
    private static final Logger LOGGER = Logger.getLogger(MyUI.class.getName());

    //
    private static class ListItem {
    	public ListItem(String id, String name) {
    		this.id = id;
    		this.name = name;
    	}
    	public String id = "_TOTAL_";
    	public String name = "ALL";
    	public String toString() { return name; }
    };

    private AssetService assetService;
	private SignalService signalService;
	private StateService stateService;
	private SignalService.GroupBy group = SignalService.GroupBy.DAY;
	private String assetName;
	private boolean showTotal;
	private SignalChartUi signalChart;
	private SignalTableUi signalTable;
	boolean initComplete;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	// If 'demo' flag is specified, use internal mock data
    	String demoParam = vaadinRequest.getParameter("demo");
    	if (demoParam != null && demoParam.equalsIgnoreCase("true")) {
    		LoadData loader = new LoadData();
    		loader.loadTestData("query_result.csv");
    		
    		MockStateService states = MockStateService.getInstance();
    		states.setData(loader);
    		stateService = states;
    		
    		MockAssetService assets = MockAssetService.getInstance();
    		assets.setData(loader);
    		assetService = assets;
    		
    		MockSignalService signals = MockSignalService.getInstance();
    		signals.setData(loader);
    		signalService = signals;
    		
        	// Quick and dirty switch to populate database from the internal mock data
        	String dataloadParam = vaadinRequest.getParameter("dataload");
        	if (dataloadParam != null && dataloadParam.equalsIgnoreCase("true")) {
        		loader.writeTestDataToDatabase();
        	}
    	} else {
    		assetService = AssetServiceImpl.getInstance();
    		signalService = SignalServiceImpl.getInstance();
    	}

    	// URL State
    	assetName = vaadinRequest.getParameter("asset");
    	if (assetName == null) {
    		assetName = "_TOTAL_";
    	}
    	showTotal = "_TOTAL_".equals(assetName);
    	String groupStr = vaadinRequest.getParameter("groupBy");
    	if (groupStr != null) {
	    	try {
	    		group = SignalService.GroupBy.valueOf(groupStr);
	    	} catch (IllegalArgumentException e) {
	    		LOGGER.warning("Invalid groupBy name provided. " + groupStr);
	    	}
    	}
    	LOGGER.info("Show asset: " + assetName + " with grouping " + group);

    	// Start UI
    	final HorizontalLayout layout = new HorizontalLayout();
    	layout.setWidth("100%");

    	{
	    	List<State> states = stateService.findAll();
	    	List<Signal> signals = signalService.findAll(assetName, group);
	    	
			// Chart view
	        signalChart = new SignalChartUi(this);
	        signalChart.createChart(states, signals, showTotal);
	        
	        // Table View
	        signalTable = new SignalTableUi(this);
	        signalTable.createTable(states, signals);
    	}
    	
        // Asset List combo Box
        ListSelect assetSelect = new ListSelect("Assets");
        assetSelect.setNullSelectionAllowed(false);
        assetSelect.setMultiSelect(false);
        assetSelect.setHeight("100%");
        //assetSelect.setWidth("20%");
        assetSelect.addItem(new ListItem("_TOTAL_", "All"));
        for (Asset asset : assetService.findAll()) {
        	assetSelect.addItems(new ListItem(asset.getName(), asset.getName()));
        }
        assetSelect.setValue(assetName);
        assetSelect.addValueChangeListener(e -> {
        	if (initComplete) {
	        	assetName = ((ListItem)e.getProperty().getValue()).id;
	        	showTotal = "_TOTAL_".equals(assetName);
	        	LOGGER.info("New Asset selected: " + assetName);
	        	List<State> states = stateService.findAll();
	        	List<Signal> signals = signalService.findAll(assetName, group);
	        	signalChart.setData(states, signals, showTotal);
	        	signalTable.setData(states, signals);
        	}
        });
        
        //
        Button dayGroup = new Button("Day");
        dayGroup.setHeight("25px");
        dayGroup.addClickListener( e -> {
        	LOGGER.info("Day grouping selected");
    		group = SignalService.GroupBy.DAY;
        	List<State> states = stateService.findAll();
        	List<Signal> signals = signalService.findAll(assetName, group);
        	signalChart.setData(states, signals, showTotal);
        	signalTable.setData(states, signals);
        });
        Button monthGroup = new Button("Month");
        monthGroup.setHeight("25px");
        monthGroup.addClickListener( e -> {
        	LOGGER.info("Month grouping selected");
    		group = SignalService.GroupBy.MONTH;
        	List<State> states = stateService.findAll();
        	List<Signal> signals = signalService.findAll(assetName, group);
        	signalChart.setData(states, signals, showTotal);
        	signalTable.setData(states, signals);
        });
        Button yearGroup = new Button("Year");
        yearGroup.setHeight("25px");
        yearGroup.addClickListener( e -> {
        	LOGGER.info("Year grouping selected");
    		group = SignalService.GroupBy.YEAR;
        	List<State> states = stateService.findAll();
        	List<Signal> signals = signalService.findAll(assetName, group);
        	signalChart.setData(states, signals, showTotal);
        	signalTable.setData(states, signals);
        });
        HorizontalLayout groupBtns = new HorizontalLayout();
        groupBtns.setSpacing(true);
        groupBtns.addComponents(new Label("Group by: "), dayGroup, monthGroup, yearGroup);
        
        VerticalLayout centerPanel = new VerticalLayout();
        centerPanel.setWidth("100%");
        centerPanel.setMargin(new MarginInfo(false, true, false, true));
        centerPanel.addComponents(groupBtns, signalChart.getChart(), signalTable.getTable());
        
        layout.addComponents(assetSelect, centerPanel);
        layout.setExpandRatio(centerPanel, 1);
        
        setContent(layout);
        initComplete = true;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
