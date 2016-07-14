package org.test;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.test.model.Asset;
import org.test.model.AssetService;
import org.test.model.AssetServiceImpl;
import org.test.model.LoadData;
import org.test.model.Signal;
import org.test.model.SignalService;
import org.test.model.SignalServiceImpl;
import org.test.model.State;
import org.test.model.StateService;
import org.test.model.StateServiceImpl;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
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
    	public String id = LoadData.ALL_ASSETS;
    	public String name = "ALL";
    	public String toString() { return name; }
    };

    private MyUI ui = this;
    private AssetService assetService;
	private SignalService signalService;
	private StateService stateService;
	private SignalService.GroupBy group = SignalService.GroupBy.DAY;
	private String assetName;
	private SignalChartUi signalChart;
	private SignalTableUi signalTable;
	private GroupButtonsUi groupBtns;
	private boolean showTable;
	boolean initComplete;
	
	public boolean getShowTable() {
		return showTable;
	}
	
	public List<State> getStates() {
		return stateService.findAll();
	}
	
	public List<Signal> getSignals() {
		return signalService.findAll(assetName, group);
	}
	
	public void setAsset(String assetName) {
    	LOGGER.info("New Asset selected: " + assetName);
    	this.assetName = assetName;
    	List<State> states = getStates();
    	List<Signal> signals = getSignals();
    	signalChart.setData(states, signals);
    	if (showTable) { signalTable.setData(states, signals); }
	}
	
	public void setGroup(SignalService.GroupBy group) {
		this.group = group;
    	List<State> states = getStates();
    	List<Signal> signals = getSignals();
    	signalChart.setData(states, signals);
    	if (showTable) { signalTable.setData(states, signals); }
    }
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	// If 'demo' flag is specified, use internal mock data
    	/* AB: Disabled for now, since source CSV file removed.
    		TODO: Export CSV snapshot data from database for use in unit tests.
		boolean demodata = Boolean.valueOf(vaadinRequest.getParameter("demo"));
		boolean loaddata = Boolean.valueOf(vaadinRequest.getParameter("dataload"));
    	if (demodata) {
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
        	if (loaddata) {
        		loader.writeTestDataToDatabase();
        	}
    	} else {
    	*/
    		stateService = StateServiceImpl.getInstance();
    		assetService = AssetServiceImpl.getInstance();
    		SignalServiceImpl signalServiceImpl = SignalServiceImpl.getInstance();
    		signalServiceImpl.setStateService(stateService);
    		signalService = signalServiceImpl;
    	//}

    	// URL State
    	assetName = vaadinRequest.getParameter("asset");
    	if (assetName == null) {
    		assetName = LoadData.ALL_ASSETS;
    	}
		showTable = Boolean.valueOf(vaadinRequest.getParameter("datatable"));
    	String groupStr = vaadinRequest.getParameter("groupBy");
    	if (groupStr != null) {
	    	try {
	    		group = SignalService.GroupBy.valueOf(groupStr.toUpperCase());
	    	} catch (IllegalArgumentException e) {
	    		LOGGER.warning("Invalid groupBy name provided. " + groupStr);
	    	}
    	}
    	LOGGER.info("Show asset: " + assetName + " with grouping " + group);

    	// Load data for this init process
    	List<State> states = stateService.findAll();
    	List<Signal> signals = signalService.findAll(assetName, group);
    	
    	// Start UI
    	final HorizontalLayout layout = new HorizontalLayout();
    	layout.setWidth("100%");
    	layout.setMargin(new MarginInfo(false, false, true, false));
    	if ( ! showTable) {
    		layout.setSizeFull();
    	}

		// Chart view
        signalChart = new SignalChartUi(this);
        signalChart.createChart(states, signals);
        
        // Table View
        if (showTable) {
	        signalTable = new SignalTableUi(this);
	        signalTable.createTable(states, signals);
        }
    	
        // Asset List
        ListSelect assetSelect = new ListSelect("Assets");
        assetSelect.setNullSelectionAllowed(false);
        assetSelect.setMultiSelect(false);
    	assetSelect.setHeight("100%");
    	// Fill the asset list and mark the current asset selected
    	ListItem item = new ListItem(LoadData.ALL_ASSETS, "All");
        assetSelect.addItem(item);
        if (LoadData.ALL_ASSETS.equals(assetName)) {
        	assetSelect.select(item);
        }
        for (Asset asset : assetService.findAll()) {
        	item = new ListItem(asset.getName(), asset.getName());
        	assetSelect.addItems(item);
        	if (asset.getName().equals(assetName)) {
            	assetSelect.select(item);
        	}
        }
        // Handle asset selection changes
        assetSelect.addValueChangeListener(e -> {
        	if (initComplete) {
	        	assetName = ((ListItem)e.getProperty().getValue()).id;
	        	ui.setAsset(assetName);
        	}
        });
        
        // Data Grouping Buttons
        groupBtns = new GroupButtonsUi(this);
        groupBtns.createPanel(group);
        
        // A vertical layout for buttons, chart and data table (if present)
        VerticalLayout centerPanel = new VerticalLayout();
        centerPanel.setWidth("100%");
        centerPanel.setMargin(new MarginInfo(false, true, false, true));
        if (showTable) {
        	centerPanel.addComponents(groupBtns.getPanel(), signalChart.getChart(), signalTable.getTable());
        } else {
        	centerPanel.addComponents(groupBtns.getPanel(), signalChart.getChart());
        	centerPanel.setHeight("100%");
        	centerPanel.setExpandRatio(signalChart.getChart(), 1);
        }
        
        // Final config of the main page layout
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
