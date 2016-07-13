package org.test;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.charts.Chart;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
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
        
		// Grouped chart view
        signalChart = new SignalChartUi(this);
        signalChart.createChart(
        	stateService.findAll(), 
        	signalService.findAll(assetName, SignalService.GroupBy.DAY), 
        	assetName.equals("_TOTAL_"));

        // Asset List combo Box
        ListSelect assetSelect = new ListSelect();
        assetSelect.setRows(5);
        assetSelect.setNullSelectionAllowed(false);
        assetSelect.setMultiSelect(false);
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
	        	signalChart.setData(states, 
	            	signalService.findAll(assetName, group), showTotal);
        	}
        });
        
        //
        Button dayGroup = new Button("Day");
        dayGroup.addClickListener( e -> {
        	LOGGER.info("Day grouping selected");
    		group = SignalService.GroupBy.DAY;
        	List<State> states = stateService.findAll();
        	signalChart.setData(states, signalService.findAll(assetName, group), showTotal);
        });
        Button monthGroup = new Button("Month");
        monthGroup.addClickListener( e -> {
        	LOGGER.info("Month grouping selected");
    		group = SignalService.GroupBy.MONTH;
        	List<State> states = stateService.findAll();
        	signalChart.setData(states, signalService.findAll(assetName, group), showTotal);
        });
        Button yearGroup = new Button("Year");
        yearGroup.addClickListener( e -> {
        	LOGGER.info("Year grouping selected");
    		group = SignalService.GroupBy.YEAR;
        	List<State> states = stateService.findAll();
        	signalChart.setData(states, signalService.findAll(assetName, group), showTotal);
        });
        HorizontalLayout groupBtns = new HorizontalLayout();
        groupBtns.addComponents(dayGroup, monthGroup, yearGroup);
        
        VerticalLayout centerPanel = new VerticalLayout();
        centerPanel.addComponents(groupBtns, signalChart.getChart());
        
        layout.addComponents(assetSelect, centerPanel);
        layout.setMargin(true);
        layout.setSpacing(true);
        
        setContent(layout);
        initComplete = true;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
