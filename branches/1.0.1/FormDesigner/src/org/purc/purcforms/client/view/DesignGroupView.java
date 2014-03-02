package org.purc.purcforms.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.LeftPanel.Images;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.cmd.ChangeWidgetCmd;
import org.purc.purcforms.client.cmd.ChangeWidgetTypeCmd;
import org.purc.purcforms.client.cmd.CommandList;
import org.purc.purcforms.client.cmd.DeleteWidgetCmd;
import org.purc.purcforms.client.cmd.GroupWidgetsCmd;
import org.purc.purcforms.client.cmd.InsertWidgetCmd;
import org.purc.purcforms.client.cmd.MoveWidgetCmd;
import org.purc.purcforms.client.cmd.ResizeWidgetCmd;
import org.purc.purcforms.client.controller.DragDropListener;
import org.purc.purcforms.client.controller.FormDesignerDragController;
import org.purc.purcforms.client.controller.FormDesignerDropController;
import org.purc.purcforms.client.controller.IWidgetPopupMenuListener;
import org.purc.purcforms.client.controller.WidgetPropertyChangeListener;
import org.purc.purcforms.client.controller.WidgetPropertySetter;
import org.purc.purcforms.client.controller.WidgetSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DatePickerEx;
import org.purc.purcforms.client.widget.DatePickerWidget;
import org.purc.purcforms.client.widget.DateTimeWidget;
import org.purc.purcforms.client.widget.DesignGroupWidget;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.PaletteWidget;
import org.purc.purcforms.client.widget.RadioButtonWidget;
import org.purc.purcforms.client.widget.TextBoxWidget;
import org.purc.purcforms.client.widget.TimeWidget;
import org.purc.purcforms.client.widget.TreeItemWidget;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;
import org.purc.purcforms.client.widget.grid.HorizontalGridLine;
import org.purc.purcforms.client.widget.grid.VerticalGridLine;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;


/**
 * 
 * @author daniel
 *
 */
public class DesignGroupView extends Composite implements WidgetSelectionListener,IWidgetPopupMenuListener,DragDropListener,WidgetPropertyChangeListener{

	protected static final int MOVE_LEFT = 1;
	protected static final int MOVE_RIGHT = 2;
	protected static final int MOVE_UP = 3;
	protected static final int MOVE_DOWN = 4;

	/** The popup panel for the design surface context menu. */
	protected PopupPanel popup;

	/** The popup panel for the widget context menu. */
	protected PopupPanel widgetPopup;

	/** The cursor position x coordinate. */
	protected int x;

	/** The cursor position y coordinate. */
	protected int y;

	protected int clipboardLeftMostPos;
	protected int clipboardTopMostPos;

	/** The copy menu item for the design surface context menu. */
	protected MenuItem copyMenu;

	/** The cut menu item. */
	protected MenuItem cutMenu;
	protected MenuItem pasteMenu;
	protected MenuItem deleteWidgetsMenu;
	protected MenuItem groupWidgetsMenu;
	protected MenuItem lockWidgetsMenu;
	protected MenuItemSeparator cutCopySeparator;
	protected MenuItemSeparator pasteSeparator;
	protected MenuItemSeparator deleteWidgetsSeparator;
	protected MenuItemSeparator groupWidgetsSeparator;

	protected int selectionXPos;
	protected int selectionYPos;
	protected boolean mouseMoved = false;

	protected AbsolutePanel selectedPanel = new AbsolutePanel();
	protected FormDesignerDragController selectedDragController;
	protected WidgetSelectionListener widgetSelectionListener;
	protected FormDesignerDropController dropController;

	/** The rubber band widget for multiple widget selection. */
	protected Label rubberBand = new Label(""); //HTML("<DIV ID='rubberBand'></DIV>");

	protected final Images images;

	//These three do not belong here (should be only for DesignSurfaceView)
	/** Tabs for displaying pages. */
	protected DecoratedTabPanel tabs = new DecoratedTabPanel();
	protected HashMap<Integer,DesignWidgetWrapper> pageWidgets = new HashMap<Integer,DesignWidgetWrapper>();

	/** The index of the selected page tab. */
	protected int selectedTabIndex = 0;

	protected WidgetSelectionListener currentWidgetSelectionListener;

	/** The text box widget for inline label editing. */
	protected TextBox txtEdit = new TextBox();
	protected DesignWidgetWrapper editWidget;
	
	protected static DesignGroupView labelEditView;

	/** The selection rubber band height in pixels. */
	protected String rubberBandHeight;

	/** The selection rubber band width in pixels. */
	protected String rubberBandWidth;
	
	/** The selection rubber band left in pixels. */
	protected int rubberBandLeft;
	
	/** The selection rubber band top in pixels. */
	protected int rubberBandTop;
	
	/** The selection rubber band right in pixels. */
	protected int rubberBandRight;
	
	/** The selection rubber band bottom in pixels. */
	protected int rubberBandBottom;

	private boolean newlyAddedWidget = false;
	
	/** List of drag controllers. */
	protected Vector<FormDesignerDropController> tabDropControllers = new Vector<FormDesignerDropController>();

	/**
	 * Creates a new instance of the design surface.
	 * 
	 * @param images
	 */
	public DesignGroupView(Images images){
		this.images = images;
	}

	public void onDragEnd(Widget widget) {
		onWidgetSelected(getSelectedWidget((DesignWidgetWrapper)widget),true);

		((DesignWidgetWrapper)widget).refreshSize();
		if(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof DesignGroupWidget)
			DOM.setStyleAttribute(((DesignWidgetWrapper)widget).getWrappedWidget().getElement(), "cursor", "default");

		//if(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof DesignGroupWidget)
		//	((DesignGroupWidget)((DesignWidgetWrapper)widget).getWrappedWidget()).getHeaderLabel().refreshSize();
	}

	public void onDragStart(Widget widget) {
		onWidgetSelected(getSelectedWidget((DesignWidgetWrapper)widget),true);
	}

	private DesignWidgetWrapper getSelectedWidget(DesignWidgetWrapper widget){
		if(widget.getWrappedWidget() instanceof DesignGroupWidget && !widget.isRepeated()){
			String cursor = DOM.getStyleAttribute(widget.getWrappedWidget().getElement(), "cursor");
			if("move".equals(cursor) || "default".equals(cursor))
				return ((DesignGroupWidget)widget.getWrappedWidget()).getHeaderLabel();
		}
		return widget;
	}

	public void onWidgetSelected(Widget widget, AbsolutePanel panel, boolean multipleSel){
		selectPanel(panel);
		this.onWidgetSelected(widget, multipleSel);
	}

	public void onWidgetSelected(Widget widget, boolean multipleSel){

		//Some widgets like check boxes and buttons may not have sizes set yet
		//and so when in edit mode, they fire onmousedown events.
		if(widget != null && widget == editWidget)
			return;

		//Right clicking on a widget when we have more than one item selected should not turn off selection.
		if(multipleSel && selectedDragController.getSelectedWidgetCount() > 1)
			return;
		
		if(labelEditView != null) {
			labelEditView.stopLabelEdit(false);
			labelEditView = null;
		}

		if(widget == null){
			if(!(selectedDragController.getSelectedWidgetCount() > 1))
				selectedDragController.clearSelection();
		}
		else if(widget instanceof DesignWidgetWrapper && !(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof DesignGroupWidget)){
			String s = ((DesignWidgetWrapper)widget).getWidth();
			if(!"100%".equals(s)){
				if(widgetSelectionListener instanceof DesignSurfaceView){
					//((DesignSurfaceView)widgetSelectionListener).clearSelection();
					if(selectedDragController.getSelectedWidgetCount() == 1)
						//Commented out because it made widgets, in more than two nested group boxes, un movable
						;//((DesignSurfaceView)widgetSelectionListener).clearSelection(); //recursivelyClearGroupBoxSelection();

					//if(!multipleSel && selectedDragController.getSelectedWidgetCount() == 1)
					//	selectedDragController.clearSelection();
				}

				if(!multipleSel && !selectedDragController.isWidgetSelected(widget)/*selectedDragController.getSelectedWidgetCount() == 1*/)
					selectedDragController.clearSelection();

				//Deselect and stop editing of any widget in group boxes
				//TODO Doesnt this slow us a bit?
				if(widget instanceof DesignWidgetWrapper &&  ((DesignWidgetWrapper)widget).getWidgetSelectionListener() instanceof DesignSurfaceView){
					if(!(selectedDragController.isWidgetSelected(widget) && selectedDragController.getSelectedWidgetCount() > 1)){
						recursivelyClearGroupBoxSelection();
					}
				}

				//Deselect any previously selected widgets in groupbox
				selectedDragController.selectWidget(widget); //TODO Test this and make sure it does not introduce bugs
			}

			stopLabelEdit(false);
		}

		widgetSelectionListener.onWidgetSelected(widget, multipleSel);
		
		//Synchronize by selecting the form field corresponding to the selected widget.
		if (Context.getLeftPanel().isFormsStackSelected()) {
			if (!multipleSel && widget instanceof DesignWidgetWrapper) {
				DesignWidgetWrapper wrapper = (DesignWidgetWrapper)widget;
				QuestionDef questionDef = wrapper.getQuestionDef();
				if (questionDef != null) {
					if (Context.getSelectedItem() != questionDef) {
						Context.getLeftPanel().selectItem(questionDef, null);
					}
				}
				else if (wrapper.getParentBinding() != null) {
					questionDef = Context.getFormDef().getQuestion(wrapper.getParentBinding());
					String binding = wrapper.getBinding();
					if (questionDef != null && binding != null) {
						OptionDef optionDef = questionDef.getOptionWithValue(binding);
						if (optionDef != null && Context.getSelectedItem() != optionDef) {
							Context.getLeftPanel().selectItem(questionDef, binding);
						}
					}
				}
			}
		}
	}

	protected void cutWidgets(){
		copyWidgets(true, true, null);
	}

	/**
	 * Adds the selected widgets to a group box.
	 */
	protected void groupWidgets(){

		//We now allow nested group boxes
		/*for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			if(((DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i)).getWrappedWidget() instanceof DesignGroupWidget)
				return; //We do not allow nested group boxes
		}*/
		
		DesignWidgetWrapper designWidgetWrapper = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);
		if(this.selectedPanel.getWidgetIndex(designWidgetWrapper) < 0){
			designWidgetWrapper.getView().groupWidgets();
			return; //cannot group within a view which is not the parent.
		}

		CommandList commands = new CommandList(this);

		//cutWidgets();
		copyWidgets(true, false, commands);

		x = clipboardLeftMostPos + selectedPanel.getAbsoluteLeft() ;
		y = clipboardTopMostPos + selectedPanel.getAbsoluteTop();

		DesignWidgetWrapper widget = addNewGroupBox(false);
		DesignGroupView designGroupView = (DesignGroupView)widget.getWrappedWidget();
		designGroupView.updateCursorPos(x+20, y+45);
		designGroupView.pasteWidgets(true, false);
		selectedDragController.clearSelection();
		designGroupView.clearSelection();
		widget.setHeightInt(FormUtil.convertDimensionToInt(rubberBandHeight)+35);
		widget.setWidth(rubberBandWidth);

		selectedDragController.selectWidget(widget);
		widgetSelectionListener.onWidgetSelected(((DesignGroupWidget)designGroupView).getHeaderLabel(), false);

		Context.getCommandHistory().add(new GroupWidgetsCmd(widget, widget.getLayoutNode(), commands, this));
	}

	protected void copyWidgets(boolean remove){
		copyWidgets(remove, true, null);
	}

	protected void copyWidgets(boolean remove, boolean storeHistory, CommandList commandsParam){
		Context.clipBoardWidgets.clear();

		CommandList commands = new CommandList(this);

		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);
			widget.storePosition();
			if(i == 0){
				clipboardLeftMostPos = FormUtil.convertDimensionToInt(widget.getLeft());;
				clipboardTopMostPos = FormUtil.convertDimensionToInt(widget.getTop());;
			}
			else{
				int dimension = FormUtil.convertDimensionToInt(widget.getLeft());
				if(clipboardLeftMostPos > dimension)
					clipboardLeftMostPos = dimension;
				dimension = FormUtil.convertDimensionToInt(widget.getTop());
				if(clipboardTopMostPos > dimension)
					clipboardTopMostPos = dimension;
			}

			if(remove){ //cut{
				DesignGroupView view = widget.getView();
				if(storeHistory)
					commands.add(new DeleteWidgetCmd(widget, widget.getLayoutNode(), view));

				if(commandsParam != null)
					commandsParam.add(new DeleteWidgetCmd(widget, widget.getLayoutNode(), view));

				tryUnregisterDropController(widget);
				view.remove(widget);
			}
			else{ //copy
				widget = new DesignWidgetWrapper(widget,images);
				tryUnregisterDropController(widget);
			}

			Context.clipBoardWidgets.add(widget);
		}

		if(commands.size() > 0)
			Context.getCommandHistory().add(commands);
	}

	private void tryUnregisterDropController(DesignWidgetWrapper widget){
		if(widget.getWrappedWidget() instanceof DesignGroupWidget) {
			DesignGroupWidget designGroupWidget = (DesignGroupWidget)widget.getWrappedWidget();
			
			PaletteView.unRegisterDropController(designGroupWidget.getDropController());
			FormsTreeView.unRegisterDropController(designGroupWidget.getDropController());
			FormDesignerDragController.unRegisterDropController(designGroupWidget.getDropController());
			
			designGroupWidget.unregisterDropControllers();
		}
	}
	
	public void unregisterDropControllers(){
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			Widget currentWidget = selectedPanel.getWidget(index);
			if(!(currentWidget instanceof DesignWidgetWrapper))
				continue;
			if(!(((DesignWidgetWrapper)currentWidget).getWrappedWidget() instanceof DesignGroupWidget))
				continue;
			
			DesignGroupWidget designGroupWidget = ((DesignGroupWidget)((DesignWidgetWrapper)currentWidget).getWrappedWidget());
			
			PaletteView.unRegisterDropController(designGroupWidget.getDropController());
			FormsTreeView.unRegisterDropController(designGroupWidget.getDropController());
			FormDesignerDragController.unRegisterDropController(designGroupWidget.getDropController());

			designGroupWidget.unregisterDropControllers();
		}
	}

	private void updateClipboardLeftMostPos(){
		for(int i=0; i<Context.clipBoardWidgets.size(); i++){
			DesignWidgetWrapper widget = Context.clipBoardWidgets.get(i);
			widget.storePosition();
			if(i == 0){
				clipboardLeftMostPos = FormUtil.convertDimensionToInt(widget.getLeft());;
				clipboardTopMostPos = FormUtil.convertDimensionToInt(widget.getTop());;
			}
			else{
				int dimension = FormUtil.convertDimensionToInt(widget.getLeft());
				if(clipboardLeftMostPos > dimension)
					clipboardLeftMostPos = dimension;
				dimension = FormUtil.convertDimensionToInt(widget.getTop());
				if(clipboardTopMostPos > dimension)
					clipboardTopMostPos = dimension;
			}
		}
	}

	protected void updateCursorPos(int x, int y){
		this.x = x;
		this.y = y;
	}

	protected void pasteWidgets(boolean afterContextMenu){
		pasteWidgets(afterContextMenu, true);
	}

	protected void pasteWidgets(boolean afterContextMenu, boolean storeHistory){
		int xOffset = x - clipboardLeftMostPos;
		int yOffset = y - clipboardTopMostPos;

		selectedDragController.clearSelection();

		CommandList commands = new CommandList(this);

		for(int i=0; i<Context.clipBoardWidgets.size(); i++){
			DesignWidgetWrapper widget = new DesignWidgetWrapper(Context.clipBoardWidgets.get(i),images);
			widget.setWidgetSelectionListener(this);

			if(i == 0){
				if(widget.getPopupPanel() != widgetPopup)
					updateClipboardLeftMostPos();
			}

			selectedDragController.makeDraggable(widget);
			selectedPanel.add(widget);

			if(widget.getWrappedWidget() instanceof DesignGroupWidget && !widget.isRepeated()) {
				DesignGroupWidget designGroupWidget = (DesignGroupWidget)widget.getWrappedWidget();
				selectedDragController.makeDraggable(widget, designGroupWidget.getHeaderLabel());
				designGroupWidget.makeHeaderLabelsDraggable();
			}

			if(widget.getPopupPanel() != widgetPopup){
				if(afterContextMenu)
					selectedPanel.setWidgetPosition(widget,(x-getAbsoluteLeft())+(widget.getLeftInt()-clipboardLeftMostPos-10),(y-getAbsoluteTop())+(widget.getTopInt()-clipboardTopMostPos-10));
				else
					selectedPanel.setWidgetPosition(widget,x-10,y-10);
			}
			else{
				String s = widget.getLeft();
				int xPos = Integer.parseInt(s.substring(0,s.length()-2)) + xOffset;
				s = widget.getTop();
				int yPos = Integer.parseInt(s.substring(0,s.length()-2)) + yOffset;

				if(yPos-widget.getAbsoluteTop() >= 0 && xPos-widget.getAbsoluteLeft() >= 0){
					xPos = xPos-widget.getAbsoluteLeft();
					yPos = yPos-widget.getAbsoluteTop();
				}

				selectedPanel.setWidgetPosition(widget,xPos,yPos);
			}

			widget.setWidth(widget.getWidth());
			widget.setHeight(widget.getHeight());
			widget.setPopupPanel(widgetPopup);
			selectedDragController.toggleSelection(widget);
			if(widget.getWrappedWidget() instanceof DesignGroupWidget)
				((DesignGroupWidget)widget.getWrappedWidget()).setWidgetPosition();

			if(i == 0 && Context.clipBoardWidgets.size() == 1)
				widgetSelectionListener.onWidgetSelected(widget,true);

			if(storeHistory)
				commands.add(new InsertWidgetCmd(widget, widget.getLayoutNode(), this));
		}

		if(Context.clipBoardWidgets.size() > 1)
			widgetSelectionListener.onWidgetSelected(null,true);

		if(commands.size() > 0)
			Context.getCommandHistory().add(commands);
	}
	
	protected void makeHeaderLabelsDraggable(){		
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			Widget currentWidget = selectedPanel.getWidget(index);
			if(!(currentWidget instanceof DesignWidgetWrapper))
				continue;
			
			DesignWidgetWrapper designWidgetWrapper = (DesignWidgetWrapper)currentWidget;
			if(designWidgetWrapper.isRepeated())
				continue;
			
			if(!(designWidgetWrapper.getWrappedWidget() instanceof DesignGroupWidget))
				continue;
			
			DesignGroupWidget designGroupWidget = (DesignGroupWidget)designWidgetWrapper.getWrappedWidget();
			selectedDragController.makeDraggable(currentWidget, designGroupWidget.getHeaderLabel());
			designGroupWidget.makeHeaderLabelsDraggable();
		}
	}

	protected boolean deleteWidgets(){
		if(!Window.confirm(LocaleText.get("deleteWidgetPrompt")))
			return false;

		CommandList commands = new CommandList(this);

		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			DesignGroupView view = widget.getView();
			commands.add(new DeleteWidgetCmd(widget, widget.getLayoutNode(), view));

			if(widget.getLayoutNode() != null)
				widget.getLayoutNode().getParentNode().removeChild(widget.getLayoutNode());
			
			tryUnregisterDropController(widget);
			view.remove(widget);
		}

		selectedDragController.clearSelection();

		Context.getCommandHistory().add(commands);

		return true;
	}

	public void deleteWidget(DesignWidgetWrapper widget, AbsolutePanel panel){
		//Added only for support of undo redo. So should be refactored.
		widget.refreshPosition();
		selectPanel(panel);
		tryUnregisterDropController(widget);
		selectedPanel.remove(widget);
		selectedDragController.clearSelection();
	}

	public void copyItem() {
		if(selectedDragController.isAnyWidgetSelected())
			copyWidgets(false);
	}

	public void cutItem() {
		if(selectedDragController.isAnyWidgetSelected())
			cutWidgets();
	}

	public void pasteItem(){
		pasteItem(false);
	}

	public void pasteItem(boolean increment) {
		if(Context.clipBoardWidgets.size() > 0){
			if(!selectedDragController.isAnyWidgetSelected()){
				if(this instanceof DesignGroupWidget){
					//x = selectedPanel.getAbsoluteLeft() + 10;
					//y = selectedPanel.getAbsoluteTop() + 10;
				}
				else if(increment){
					x += selectedPanel.getAbsoluteLeft();
					y += selectedPanel.getAbsoluteTop();
				}
				else{
					x += 10;
					y += 10;
				}

				pasteWidgets(false);
			}
			else if(selectedDragController.getSelectedWidgetCount() == 1){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget)
					((DesignGroupWidget)widget.getWrappedWidget()).pasteItem();
			}
		}
	}

	/**
	 * Deletes the selected widgets.
	 */
	public void deleteSelectedItem() {
		if(selectedDragController.isAnyWidgetSelected())
			deleteWidgets();
	}
	
	public boolean isAnyChildWidgetSelected(){
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
			if(widget.getWrappedWidget() instanceof DesignGroupWidget){
				DesignGroupWidget designGroupWidget = (DesignGroupWidget)widget.getWrappedWidget();
				if(designGroupWidget.isAnyWidgetSelected())
					return true;
				else
					return designGroupWidget.isAnyChildWidgetSelected();
			}
		}
		
		return false;
	}

	/**
	 * Aligns all labels to the right and all non labels to their left.
	 * This is excecuted after pressing Ctrl + F
	 * 
	 * @param panel the panel holding the widgets.
	 */
	private void rightAlignLabels(AbsolutePanel panel){
		List<DesignWidgetWrapper> labels = new ArrayList<DesignWidgetWrapper>();
		List<DesignWidgetWrapper> inputs = new ArrayList<DesignWidgetWrapper>();
		int longestLabelWidth = 0, longestLabelLeft = 20;

		boolean usingSelection = false;
		int count = selectedDragController.getSelectedWidgetCount();
		if(count < 2)
			count = panel.getWidgetCount();
		else
			usingSelection = true;

		List<String> tops = getInputWidgetTops(panel,usingSelection,count);

		DesignWidgetWrapper widget = null;
		for(int index =0; index < count; index++){
			if(usingSelection)
				widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(index);
			else
				widget = (DesignWidgetWrapper)panel.getWidget(index);

			//We do not format buttons and group boxes
			if(widget.getWrappedWidget() instanceof Button || widget.getWrappedWidget() instanceof DesignGroupWidget)
				continue;

			if(widget.getWrappedWidget() instanceof Label){
				//We do not align labels which are not on the same y pos as at least one input widget.
				if(!tops.contains(widget.getTop()))
					continue;

				if(widget.getElement().getScrollWidth() > longestLabelWidth){
					longestLabelWidth = widget.getElement().getScrollWidth();
					longestLabelLeft = FormUtil.convertDimensionToInt(widget.getLeft());
				}

				labels.add(widget);
			}
			else
				inputs.add(widget);
		}

		int relativeWidth = longestLabelWidth+longestLabelLeft;
		String left = (relativeWidth+5)+PurcConstants.UNITS;
		for(int index = 0; index < inputs.size(); index++)
			inputs.get(index).setLeft(left);

		for(int index = 0; index < labels.size(); index++){
			widget = labels.get(index);
			widget.setLeft((relativeWidth - widget.getElement().getScrollWidth()+PurcConstants.UNITS));
		}
	}


	/**
	 * Gets a list of widget top values, in the selected page, which capture user data. 
	 * These are neither labels, buttons, nor group boxes.
	 * 
	 * @param panel the absolute panel for the current page.
	 * @param usingSelection set to true if you want only selected widgets.
	 * @param count the number of widgets to traverse.
	 * @return the widget top value list.
	 */
	private List<String> getInputWidgetTops(AbsolutePanel panel, boolean usingSelection, int count){
		List<String> inputWidgetTops = new ArrayList<String>();

		DesignWidgetWrapper widget = null;
		for(int index =0; index < count; index++){
			if(usingSelection)
				widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(index);
			else
				widget = (DesignWidgetWrapper)panel.getWidget(index);

			if(widget.getWrappedWidget() instanceof Button || 
					widget.getWrappedWidget() instanceof DesignGroupWidget ||
					widget.getWrappedWidget() instanceof Label) 
				continue;

			inputWidgetTops.add(widget.getTop());
		}

		return inputWidgetTops;
	}


	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignLeft()
	 */
	public boolean alignLeft() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).alignLeft())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		int left = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getLeftInt();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new MoveWidgetCmd(widget, widget.getLeftInt() - left, 0, this));
			widget.setLeftInt(left);
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignRight()
	 */
	public boolean alignRight() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).alignRight())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(widgets.size() - 1);
		int total = widget.getElement().getScrollWidth() + FormUtil.convertDimensionToInt(widget.getLeft());
		for(int index = 0; index < widgets.size(); index++){
			widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new MoveWidgetCmd(widget, widget.getLeftInt() - (total - widget.getElement().getScrollWidth()), 0, this));
			widget.setLeft((total - widget.getElement().getScrollWidth()+PurcConstants.UNITS));
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignLeft()
	 */
	public boolean alignTop() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).alignTop())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		int top = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getTopInt();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new MoveWidgetCmd(widget, 0, widget.getTopInt() - top, this));
			widget.setTopInt(top);
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignRight()
	 */
	public boolean alignBottom() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).alignBottom())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(widgets.size() - 1);
		int total = widget.getElement().getScrollHeight() + FormUtil.convertDimensionToInt(widget.getTop());
		for(int index = 0; index < widgets.size(); index++){
			widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new MoveWidgetCmd(widget, 0, widget.getTopInt() - (total - widget.getElement().getScrollHeight()), this));
			widget.setTop((total - widget.getElement().getScrollHeight()+PurcConstants.UNITS));
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameHeight()
	 */
	public boolean makeSameHeight() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).makeSameHeight())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		int height = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getHeightInt();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new ResizeWidgetCmd(widget, 0, 0, 0, widget.getHeightInt() - height, this));
			widget.setHeightInt(height);
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameWidth()
	 */
	public boolean makeSameWidth() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).makeSameWidth())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		int width = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getWidthInt();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new ResizeWidgetCmd(widget, 0, 0, widget.getWidthInt() - width, 0, this));
			widget.setWidthInt(width);
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameSize()
	 */
	public boolean makeSameSize() {
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null || widgets.size() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).makeSameSize())
						return true;
				}
			}
			return false;
		}

		CommandList commands = new CommandList(this);

		//align according to the last selected item.
		int width = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getWidthInt();
		int height = ((DesignWidgetWrapper)widgets.get(widgets.size() - 1)).getHeightInt();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			commands.add(new ResizeWidgetCmd(widget, 0, 0, widget.getWidthInt() - width, widget.getHeightInt() - height, this));
			widget.setWidthInt(width);
			widget.setHeightInt(height);
		}

		Context.getCommandHistory().add(commands);

		return true;
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#format()
	 */
	public boolean format(){
		if(selectedDragController.getSelectedWidgetCount() < 2){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					if(((DesignGroupWidget)widget.getWrappedWidget()).format())
						return true;
				}
			}
			return false;
		}

		rightAlignLabels(selectedPanel);
		return true;
	}

	/**
	 * Selects all widgets on the selected page.
	 */
	protected void selectAll(){
		if(editWidget != null){
			txtEdit.selectAll();
			return; //let label editor do select all
		}

		selectedDragController.clearSelection();
		for(int i=0; i<selectedPanel.getWidgetCount(); i++){
			if(selectedPanel.getWidget(i) instanceof DesignWidgetWrapper){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(i);
				if("100%".equalsIgnoreCase(widget.getWidth()))
					continue; //This could be a group header label and hence we are not selecting it via all
				selectedDragController.selectWidget(widget);
			}
		}
	}

	public FormDesignerDragController getDragController(){
		return this.selectedDragController;
	}

	public AbsolutePanel getPanel(){
		return this.selectedPanel;
	}

	public PopupPanel getWidgetPopup(){
		return widgetPopup;
	}

	protected boolean handleStartLabelEditing(Event event){
		String s = event.getTarget().getClassName();
		s.toString();
		if(!event.getCtrlKey() && !isTextBoxFocus(event)){
			if(selectedDragController.getSelectedWidgetCount() == 1 && selectedPanel.getWidgetIndex(selectedDragController.getSelectedWidgetAt(0)) > -1 /*||
					(selectedDragController.getSelectedWidgetCount() == 0 && this instanceof DesignGroupWidget)*/){
				stopLabelEdit(false);

				if(selectedDragController.getSelectedWidgetCount() == 0 && this instanceof DesignGroupWidget)
					editWidget = ((DesignGroupWidget)this).getHeaderLabel();
				else
					editWidget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);

				if(editWidget != null){
					if(editWidget.getWidgetSelectionListener() instanceof DesignGroupWidget & !(this instanceof DesignGroupWidget)){
						boolean ret = ((DesignGroupWidget)editWidget.getWidgetSelectionListener()).handleStartLabelEditing(event);
						editWidget = null;
						return ret;
					}
					else if(editWidget.hasLabelEdidting()){
						selectedDragController.makeNotDraggable(editWidget);

						//editWidget.removeStyleName("dragdrop-handle");
						//editWidget.removeStyleName("dragdrop-draggable");

						if(this instanceof DesignGroupWidget){
							//this.removeStyleName("dragdrop-handle");
							//this.removeStyleName("dragdrop-draggable");
							((DesignGroupWidget)this).clearSelection();
						}

						selectedDragController.clearSelection();
						editWidget.startEditMode(txtEdit);
						labelEditView = this;
						return true;
					}
					else if(editWidget.getWrappedWidget() instanceof DesignGroupWidget){
						//Handle label editing
						DesignWidgetWrapper headerLabel = ((DesignGroupWidget)editWidget.getWrappedWidget()).getHeaderLabel();
						if(headerLabel == null)
							return false;

						//Without these two lines, the edit text is not selected, not even with Ctrl + A
						selectedDragController.makeNotDraggable(editWidget);
						editWidget.removeStyleName("dragdrop-handle");

						((DesignGroupWidget)editWidget.getWrappedWidget()).clearSelection();
						selectedDragController.clearSelection();
						headerLabel.startEditMode(txtEdit);
						labelEditView = this;
						return true;
					}
					else
						editWidget = null;
				}
			}
			/*else{
				editWidget = getSelPageDesignWidget();
				editWidget.startEditMode(txtEdit);
				return true;
			}*/
		}
		return false;
	}

	protected void handleStopLabelEditing(boolean select){
		if(editWidget == null && selectedDragController.isAnyWidgetSelected()){
			editWidget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);
			if(editWidget.getWidgetSelectionListener() instanceof DesignGroupWidget & !(this instanceof DesignGroupWidget)){
				((DesignGroupWidget)editWidget.getWidgetSelectionListener()).handleStopLabelEditing(select);
				editWidget = null;
				return;
			}
			else if(editWidget.hasLabelEdidting())
				stopLabelEdit(select);
		}
		else
			stopLabelEdit(select);
	}

	protected boolean isTextBoxFocus(Event event){
		return event.getTarget().getClassName().equalsIgnoreCase("gwt-TextBox") || event.getTarget().getClassName().equalsIgnoreCase("gwt-SuggestBox");
	}

	protected boolean isTextAreaFocus(Event event){
		return event.getTarget().getClassName().equalsIgnoreCase("gwt-TextArea");
	}

	public int getRubberLeft(){
		return FormUtil.convertDimensionToInt(DOM.getStyleAttribute(rubberBand.getElement(), "left"));
	}

	public int getRubberTop(){
		return FormUtil.convertDimensionToInt(DOM.getStyleAttribute(rubberBand.getElement(), "top"));
	}

	public void startRubberBand(Event event){
		//if(this instanceof DesignGroupWidget)
		//	return;

		//Prevent the browser from selecting text.
		//DOM.eventPreventDefault(event); //Commented out to allow scroll selecting.

		selectedPanel.add(rubberBand);

		x = event.getClientX()-selectedPanel.getAbsoluteLeft();
		y = event.getClientY()-selectedPanel.getAbsoluteTop();

		DOM.setStyleAttribute(rubberBand.getElement(), "width", 0+PurcConstants.UNITS);
		DOM.setStyleAttribute(rubberBand.getElement(), "height", 0+PurcConstants.UNITS);
		DOM.setStyleAttribute(rubberBand.getElement(), "left", x+PurcConstants.UNITS);
		DOM.setStyleAttribute(rubberBand.getElement(), "top", y+PurcConstants.UNITS);
		DOM.setStyleAttribute(rubberBand.getElement(), "visibility", "visible");

		DOM.setCapture(getElement());
	}

	public void stopRubberBand(Event event){
		selectedPanel.remove(rubberBand);
	}

	public void moveRubberBand(Event event){
		try
		{
			if(DOM.getCaptureElement() != getElement())
				return;

			int width = (event.getClientX()-selectedPanel.getAbsoluteLeft())-x;
			int height = (event.getClientY()-selectedPanel.getAbsoluteTop())-y;

			if(width < 0){
				DOM.setStyleAttribute(rubberBand.getElement(), "left", event.getClientX()-selectedPanel.getAbsoluteLeft()+PurcConstants.UNITS);
				DOM.setStyleAttribute(rubberBand.getElement(), "width", width * -1 + PurcConstants.UNITS);
			}
			else
				DOM.setStyleAttribute(rubberBand.getElement(), "width", (event.getClientX()-selectedPanel.getAbsoluteLeft())-getRubberLeft()+PurcConstants.UNITS);

			if(height < 0){
				DOM.setStyleAttribute(rubberBand.getElement(), "top", event.getClientY()-selectedPanel.getAbsoluteTop()+PurcConstants.UNITS);
				DOM.setStyleAttribute(rubberBand.getElement(), "height", height * -1 + PurcConstants.UNITS);
			}
			else
				DOM.setStyleAttribute(rubberBand.getElement(), "height", (event.getClientY()-selectedPanel.getAbsoluteTop())-getRubberTop()+PurcConstants.UNITS);

		}
		catch(Exception ex){
			//This exception is intentionally ignored as a rubber band is no big deal
		}
	}

	/**
	 * Moves widgets in a given direction due to movement of the keyboard arrow keys.
	 * 
	 * @param dirrection the move dirrection.
	 * @return true if any widget was moved, else false.
	 */
	protected boolean moveWidgets(int dirrection){
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null)
			return false;

		CommandList commands = new CommandList(this);

		int pos;
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);

			if(dirrection == MOVE_LEFT){
				commands.add(new MoveWidgetCmd(widget, 1, 0, this));
				pos = FormUtil.convertDimensionToInt(widget.getLeft());
				widget.setLeft(pos-1+PurcConstants.UNITS);
			}
			else if(dirrection == MOVE_RIGHT){
				commands.add(new MoveWidgetCmd(widget, -1, 0, this));
				pos = FormUtil.convertDimensionToInt(widget.getLeft());
				widget.setLeft(pos+1+PurcConstants.UNITS);
			}
			else if(dirrection == MOVE_UP){
				commands.add(new MoveWidgetCmd(widget, 0, 1, this));
				pos = FormUtil.convertDimensionToInt(widget.getTop());
				widget.setTop(pos-1+PurcConstants.UNITS);		
			}
			else if(dirrection == MOVE_DOWN){
				commands.add(new MoveWidgetCmd(widget, 0, -1, this));
				pos = FormUtil.convertDimensionToInt(widget.getTop());
				widget.setTop(pos+1+PurcConstants.UNITS);
			}
		}

		if(commands.size() > 0)
			Context.getCommandHistory().add(commands);

		return widgets.size() > 0;
	}

	/**
	 * Resizes widgets in a given direction due to movement of the keyboard arrow keys.
	 * 
	 * @param Event the current event object.
	 * @return true if any widget was resized, else false.
	 */
	protected boolean resizeWidgets(Event event){
		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets == null)
			return false;

		CommandList commands = new CommandList(this);

		int resizedCount = 0;

		int keycode = event.getKeyCode();
		for(int index = 0; index < widgets.size(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
			if(!widget.isResizable())
				continue;

			resizedCount++;

			if(keycode == KeyCodes.KEY_LEFT){
				commands.add(new ResizeWidgetCmd(widget, 0, 0, 1, 0, this));
				widget.setWidthInt(widget.getWidthInt()-1);
			}
			else if(keycode == KeyCodes.KEY_RIGHT){
				commands.add(new ResizeWidgetCmd(widget, 0, 0, -1, 0, this));
				widget.setWidthInt(widget.getWidthInt()+1);
			}
			else if(keycode == KeyCodes.KEY_UP){
				commands.add(new ResizeWidgetCmd(widget, 0, 0, 0, 1, this));
				widget.setHeightInt(widget.getHeightInt()-1);
			}
			else if(keycode == KeyCodes.KEY_DOWN){
				commands.add(new ResizeWidgetCmd(widget, 0, 0, 0, -1, this));
				widget.setHeightInt(widget.getHeightInt()+1);
			}
			else 
				return false; //Shift press when not in combination with arrow keys is ignored.
		}

		if(commands.size() > 0)
			Context.getCommandHistory().add(commands);

		return resizedCount > 0;
	}

	/**
	 * Selects widgets wrapped by the rubber band.
	 * 
	 * @param event the event object.
	 */
	protected void selectWidgets(Event event){
		int width = (event.getClientX()-selectedPanel.getAbsoluteLeft())-x;
		int height = (event.getClientY()-selectedPanel.getAbsoluteTop())-y;
		
		int endX = rubberBandRight = event.getClientX() - selectedPanel.getAbsoluteLeft();
		int endY = rubberBandBottom = event.getClientY() - selectedPanel.getAbsoluteTop();

		rubberBandLeft = x;
		rubberBandTop = y;
		if (width < 0) {
			rubberBandLeft = event.getClientX() - selectedPanel.getAbsoluteLeft();
			rubberBandRight = x;
		}
		if (height < 0) {
			rubberBandTop = event.getClientY() - selectedPanel.getAbsoluteTop();
			rubberBandBottom = y;
		}
		
		//Store this for Group Widgets
		rubberBandHeight = DOM.getStyleAttribute(rubberBand.getElement(), "height");
		rubberBandWidth = DOM.getStyleAttribute(rubberBand.getElement(), "width");
		
		for(int i=0; i<selectedPanel.getWidgetCount(); i++){
			if(selectedPanel.getWidget(i) instanceof  DesignWidgetWrapper){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedPanel.getWidget(i);
				if(widget.isWidgetInRect(selectionXPos, selectionYPos, endX, endY))
					selectedDragController.selectWidget(widget);
			}
		}

		if((event.getCtrlKey() || event.getShiftKey() || event.getAltKey()) && selectedDragController.getSelectedWidgetCount() == 1){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);
			widget.setWidthInt(endX - widget.getLeftInt());
			widget.setHeightInt(endY - widget.getTopInt());
		}

		if(event.getKeyCode() == KeyCodes.KEY_UP || event.getKeyCode() == KeyCodes.KEY_DOWN){
			for(int index = 0; index < selectedDragController.getSelectedWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(index);
				widget.setHeightInt(endY - widget.getTopInt());
			}
		}

		if(event.getKeyCode() == KeyCodes.KEY_RIGHT || event.getKeyCode() == KeyCodes.KEY_LEFT){
			for(int index = 0; index < selectedDragController.getSelectedWidgetCount(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(index);
				widget.setWidthInt(endX - widget.getLeftInt());
			}
		}

		if(selectedDragController.getSelectedWidgetCount() > 0)
			widgetSelectionListener.onWidgetSelected(null, false);
	}

	/**
	 * Updates the design surface context menu basing on the selected widgets.
	 */
	protected void updatePopup(){
		boolean visible = false;
		if(selectedDragController.isAnyWidgetSelected())
			visible = true;
		else
			visible = isAnyChildWidgetSelected();
		
		deleteWidgetsSeparator.setVisible(visible);
		deleteWidgetsMenu.setVisible(visible);

		//For now this is only used by the DesignSurfaceView
		if(groupWidgetsSeparator != null){
			groupWidgetsSeparator.setVisible(visible);
			groupWidgetsMenu.setVisible(visible);
		}

		cutCopySeparator.setVisible(visible);
		cutMenu.setVisible(visible);
		copyMenu.setVisible(visible); 

		visible = false;
		if(Context.clipBoardWidgets.size() > 0)
			visible = true;
		pasteSeparator.setVisible(visible);
		pasteMenu.setVisible(visible); 

		//lockWidgetsMenu.setHTML(FormDesignerUtil.createHeaderHTML(images.add(),Context.getLockWidgets() ? LocaleText.get("unLockWidgets") : LocaleText.get("lockWidgets")));
	}

	/**
	 * Gets the design widget wrapper representing the selected page.
	 * 
	 * @return the design widget wrapper for the selected page.
	 */
	protected DesignWidgetWrapper getSelPageDesignWidget(){
		return pageWidgets.get(selectedTabIndex);
	}

	/**
	 * Adds a new widget with a widget selection listener to the selected page.
	 * 
	 * @param widget the widget.
	 * @param select set to true to automatically select the new widget.
	 * @param widgetSelectionListener the widget selection listener.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewWidget(Widget widget, boolean select, WidgetSelectionListener widgetSelectionListener){
		currentWidgetSelectionListener = widgetSelectionListener;
		return addNewWidget(widget,select);
	}

	/**
	 * Adds a new widget to the currently selected page.
	 * 
	 * @param widget the widget to add.
	 * @param select set to true to automatically select the widget.
	 * @return the new widget.
	 */
	protected DesignWidgetWrapper addNewWidget(Widget widget, boolean select){
		stopLabelEdit(false);

		DesignWidgetWrapper wrapper = new DesignWidgetWrapper(widget, widgetPopup, currentWidgetSelectionListener);

		if(widget instanceof Label || widget instanceof TextBox || widget instanceof ListBox ||
				widget instanceof TextArea || widget instanceof Hyperlink || 
				widget instanceof CheckBox || widget instanceof RadioButton ||
				widget instanceof DateTimeWidget || widget instanceof Button){

			wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
			wrapper.setFontSize(FormUtil.getDefaultFontSize());

			if(widget instanceof DateTimeWidget){
				((DateTimeWidget)widget).setFontFamily(FormUtil.getDefaultFontFamily());
				((DateTimeWidget)widget).setFontSize(FormUtil.getDefaultFontSize());
			}
		}

		/*if(widget instanceof ListBox)
			selectedDragController.makeDraggable(wrapper,wrapper);
		else*/
		selectedDragController.makeDraggable(wrapper);

		selectedPanel.add(wrapper);
		//selectedPanel.setWidgetPosition(wrapper, x-wrapper.getAbsoluteLeft(), y-wrapper.getAbsoluteTop());
		selectedPanel.setWidgetPosition(wrapper, x-wrapper.getParent().getAbsoluteLeft(), y-wrapper.getParent().getAbsoluteTop());
		if(select){
			selectedDragController.clearSelection();
			selectedDragController.toggleSelection(wrapper);
			//widgetSelectionListener.onWidgetSelected(wrapper);
			onWidgetSelected(wrapper, false);

			Context.getCommandHistory().add(new InsertWidgetCmd(wrapper, wrapper.getLayoutNode(), this));
		}

		return wrapper;
	}

	public void insertWidget(DesignWidgetWrapper widget, AbsolutePanel panel){	
		selectPanel(panel);
		stopLabelEdit(false);
		selectedPanel.add(widget);
		selectedPanel.setWidgetPosition(widget, widget.getLeftInt(), widget.getTopInt());
		selectedDragController.selectWidget(widget);
		
		FormDesignerDropController dropController = widget.getDropController();
		if(dropController != null) {
			selectedDragController.registerDropController(dropController);
			PaletteView.registerDropController(dropController);
			FormsTreeView.registerDropController(dropController);
		}
	}

	private void selectPanel(AbsolutePanel panel){
		if(panel != selectedPanel){
			if(this instanceof DesignSurfaceView)
				((DesignSurfaceView)this).selectPanel(panel);
		}
	}

	public void selectWidget(DesignWidgetWrapper widget, AbsolutePanel panel){
		selectPanel(panel);
		stopLabelEdit(false);
		selectedDragController.selectWidget(widget);
	}

	/**
	 * Adds a new label widget to the selected page.
	 * 
	 * @param text the label text.
	 * @param select set to true if you want to automatically select the widget.
	 * @return the new label widget.
	 */
	protected DesignWidgetWrapper addNewLabel(String text, boolean select){
		if(text == null)
			text = LocaleText.get("label");
		Label label = new Label(text);

		DesignWidgetWrapper wrapper = addNewWidget(label,select);		
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		return wrapper;
	}
	
	protected DesignWidgetWrapper addNewHorizontalLine(boolean select){
		HorizontalGridLine line = new HorizontalGridLine(800);
		DesignWidgetWrapper wrapper = addNewWidget(line, select);
		if(this instanceof GridDesignGroupWidget) {
			((GridDesignGroupWidget)this).setLineBorderProperties(wrapper);
		}
		
		return wrapper;
	}
	
	protected DesignWidgetWrapper addNewVerticalLine(boolean select){
		VerticalGridLine line = new VerticalGridLine(400);
		DesignWidgetWrapper wrapper = addNewWidget(line, select);
		if(this instanceof GridDesignGroupWidget) {
			((GridDesignGroupWidget)this).setLineBorderProperties(wrapper);
		}
		
		return wrapper;
	}

	/**
	 * Adds a new audio or video widget.
	 * 
	 * @param text the display text for the widget.
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewVideoAudio(String text, boolean select){
		if(text == null)
			text = LocaleText.get("clickToPlay");
		Hyperlink link = new Hyperlink(text,"");

		DesignWidgetWrapper wrapper = addNewWidget(link,select);
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		return wrapper;
	}

	/**
	 * Adds a new server search widget to the selected page.
	 * 
	 * @param text the display text for the widget.
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewServerSearch(String text, boolean select){
		if(text == null)
			text = LocaleText.get("noSelection");
		Label label = new Label(text);

		DesignWidgetWrapper wrapper = addNewWidget(label,select);
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		return wrapper;
	}

	/**
	 * Adds a new picture widget to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.s
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewPicture(boolean select){
		Image image = FormUtil.createImage(images.picture());
		DOM.setStyleAttribute(image.getElement(), "height","155"+PurcConstants.UNITS);
		DOM.setStyleAttribute(image.getElement(), "width","185"+PurcConstants.UNITS);
		return addNewWidget(image,select);
	}

	/**
	 * Adds a new TextBox to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.s
	 */
	protected DesignWidgetWrapper addNewTextBox(boolean select){
		TextBoxWidget tb = new TextBoxWidget();
		DOM.setStyleAttribute(tb.getElement(), "height","25"+PurcConstants.UNITS);
		DOM.setStyleAttribute(tb.getElement(), "width","200"+PurcConstants.UNITS);
		return addNewWidget(tb, select);
	}

	/**
	 * Adds a new date picker to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewDatePicker(boolean select){
		DatePickerEx tb = new DatePickerWidget();
		DOM.setStyleAttribute(tb.getElement(), "height","25"+PurcConstants.UNITS);
		DOM.setStyleAttribute(tb.getElement(), "width","200"+PurcConstants.UNITS);
		return addNewWidget(tb,select);
	}

	/**
	 * Adds a new date time widget to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewDateTimeWidget(boolean select){
		DateTimeWidget tb = new DateTimeWidget();
		DOM.setStyleAttribute(tb.getElement(), "height","25"+PurcConstants.UNITS);
		DOM.setStyleAttribute(tb.getElement(), "width","200"+PurcConstants.UNITS);
		return addNewWidget(tb,select);
	}

	/**
	 * Adds a new time widget to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewTimeWidget(boolean select){
		TimeWidget tb = new TimeWidget();
		DOM.setStyleAttribute(tb.getElement(), "height","25"+PurcConstants.UNITS);
		DOM.setStyleAttribute(tb.getElement(), "width","200"+PurcConstants.UNITS);
		return addNewWidget(tb,select);
	}

	/**
	 * Adds a new CheckBox to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewCheckBox(boolean select){
		DesignWidgetWrapper wrapper = addNewWidget(new CheckBox(LocaleText.get("checkBox")),select);		
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		return wrapper;
	}

	/**
	 * Adds a new radio button to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewRadioButton(boolean select){
		DesignWidgetWrapper wrapper = addNewWidget(new RadioButtonWidget("RadioButton",LocaleText.get("radioButton")),select);
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		return wrapper;
	}

	/**
	 * Adds a new drop downlist box to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewDropdownList(boolean select){
		ListBox lb = new ListBox(false);
		DOM.setStyleAttribute(lb.getElement(), "height","25"+PurcConstants.UNITS);
		DOM.setStyleAttribute(lb.getElement(), "width","200"+PurcConstants.UNITS);
		DesignWidgetWrapper wrapper = addNewWidget(lb,select);
		return wrapper;
	}

	/**
	 * Adds a new text area to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewTextArea(boolean select){
		TextArea ta = new TextArea();
		DOM.setStyleAttribute(ta.getElement(), "height","60"+PurcConstants.UNITS);
		DOM.setStyleAttribute(ta.getElement(), "width","200"+PurcConstants.UNITS);
		return addNewWidget(ta,select);
	}

	/**
	 * Adds a new button.
	 * 
	 * @param label the button label or text.
	 * @param binding the widget binding.
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewButton(String label, String binding, boolean select){
		DesignWidgetWrapper wrapper = addNewWidget(new Button(label),select);
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		wrapper.setWidthInt(70);
		wrapper.setHeightInt(30);
		wrapper.setBinding(binding);
		wrapper.setTitle(binding);
		return wrapper;
	}

	/**
	 * Adds a new submit button.
	 * 
	 * @param select set to true to automatically select the new button.s
	 * @return the new button widget.
	 */
	protected DesignWidgetWrapper addSubmitButton(boolean select){
		return addNewButton(LocaleText.get("submit"),"submit",select);
	}

	/**
	 * Adds a new cancel button.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the new button.
	 */
	protected DesignWidgetWrapper addCancelButton(boolean select){
		return addNewButton(LocaleText.get("close"),"cancel",select);
	}

	public void onCopy(Widget sender) {
		selectedDragController.clearSelection();
		selectedDragController.selectWidget(sender.getParent().getParent());
		copyWidgets(false);
	}

	public void onCut(Widget sender) {
		selectedDragController.clearSelection();
		selectedDragController.selectWidget(sender.getParent().getParent());
		cutWidgets();
	}

	public void onDelete(Widget sender) {
		selectedDragController.clearSelection();
		selectedDragController.selectWidget(sender.getParent().getParent());
		deleteWidgets();
	}

	public DesignWidgetWrapper onDrop(Widget widget,int x, int y){
		if(widget instanceof PaletteWidget || widget instanceof TreeItemWidget){
			this.x = x;
			this.y = y;
		}
		
		if(widget instanceof PaletteWidget)
			return onDropWidgetFromPalette(widget, x, y);
		else if(widget instanceof TreeItemWidget)
			addToDesignSurface(Context.getSelectedItem(), y, x);
		
		return null;
	}
	
	public DesignWidgetWrapper onDropWidgetFromPalette(Widget widget,int x, int y){
		if(!(widget instanceof PaletteWidget))
			return null;

		String text = ((PaletteWidget)widget).getName();

		DesignWidgetWrapper retWidget = null;
		boolean resizeParent = true;

		if(text.equals(LocaleText.get("label")))
			retWidget = addNewLabel(LocaleText.get("label"),true);
		else if(text.equals(LocaleText.get("textBox")))
			retWidget = addNewTextBox(true);
		else if(text.equals(LocaleText.get("checkBox")))
			retWidget = addNewCheckBox(true);
		else if(text.equals(LocaleText.get("radioButton")))
			retWidget = addNewRadioButton(true);
		else if(text.equals(LocaleText.get("listBox")))
			retWidget = addNewDropdownList(true);
		else if(text.equals(LocaleText.get("textArea")))
			retWidget = addNewTextArea(true);
		else if(text.equals(LocaleText.get("button")))
			retWidget = addNewButton(LocaleText.get("button"),null,true);
		else if(text.equals(LocaleText.get("datePicker")))
			retWidget = addNewDatePicker(true);
		else if(text.equals(LocaleText.get("dateTimeWidget")))
			retWidget = addNewDateTimeWidget(true);
		else if(text.equals(LocaleText.get("timeWidget")))
			retWidget = addNewTimeWidget(true);
		else if(text.equals(LocaleText.get("groupBox")))
			retWidget = addNewGroupBox(true);
		else if(text.equals(LocaleText.get("repeatSection")))
			retWidget = addNewRepeatSection(true);
		else if(text.equals(LocaleText.get("picture")))
			retWidget = addNewPictureSection(null,null,true);
		else if(text.equals(LocaleText.get("videoAudio")))
			retWidget = addNewVideoAudioSection(null,null,true);
		else if(text.equals(LocaleText.get("logo")))
			retWidget = addNewPicture(true);
		else if(text.equals(LocaleText.get("table")))
			retWidget = addNewTable(true);
		else if(text.equals(LocaleText.get("horizontalLine"))) {
			retWidget = addNewHorizontalLine(true);
			resizeParent = false;
		}
		else if(text.equals(LocaleText.get("verticalLine"))) {
			retWidget = addNewVerticalLine(true);
			resizeParent = false;
		}
		/*else if(text.equals(LocaleText.get("searchServer")))
			retWidget = addNewSearchServerWidget(null,null,true);*/

		if(retWidget != null && resizeParent){
			resizeDesignSurface(retWidget);
		}

		return retWidget;
	}
	
	protected void resizeDesignSurface(DesignWidgetWrapper widget) {
		if (widget == null)
			return;
		
		int height = FormUtil.convertDimensionToInt(getHeight());
		int h = widget.getTopInt() + widget.getHeightInt();
		if(height < h) {
			int newHeight = height + (h-height)+10;
			setHeight(newHeight + PurcConstants.UNITS);
			if(this instanceof DesignGroupView) {
				DesignSurfaceView view = getDesignSurfaceView();
				view.setHeight(FormUtil.convertDimensionToInt(view.getHeight()) + (newHeight - height) + PurcConstants.UNITS);
			}
		}

		int width = FormUtil.convertDimensionToInt(getWidth());
		int w = widget.getLeftInt() + widget.getWidthInt();
		if(width < w)
			setWidth(width + (w-width)+10+PurcConstants.UNITS);
	}

	/**
	 * Sets up the current page.
	 */
	protected void initPanel(){

		//Create a DragController for each logical area where a set of draggable
		// widgets and drop targets will be allowed to interact with one another.
		selectedDragController = FormDesignerDragController.getInstance(RootPanel.get(), false, this);

		// Positioner is always constrained to the boundary panel
		// Use 'true' to also constrain the draggable or drag proxy to the boundary panel
		//selectedDragController.setBehaviorConstrainedToBoundaryPanel(false);
		selectedDragController.setBehaviorScrollIntoView(false);

		// Allow multiple widgets to be selected at once using CTRL-click
		selectedDragController.setBehaviorMultipleSelection(true);

		//Un commenting the line below causes flickering during drag and drop
		//selectedDragController.setBehaviorDragStartSensitivity(1);

		//selectedDragController.setBehaviorCancelDocumentSelections(true);

		// create a DropController for each drop target on which draggable widgets
		// can be dropped
		dropController =  new FormDesignerDropController(selectedPanel, this);

		// Don't forget to register each DropController with a DragController
		selectedDragController.registerDropController(dropController);
		PaletteView.registerDropController(dropController);
		FormsTreeView.registerDropController(dropController);

		initEditWidget();
	}

	/**
	 * Sets up the inline label editing widget.
	 */
	private void initEditWidget(){
		DOM.setStyleAttribute(txtEdit.getElement(), "borderStyle", "none");
		DOM.setStyleAttribute(txtEdit.getElement(), "fontFamily", FormUtil.getDefaultFontFamily());
		DOM.setStyleAttribute(txtEdit.getElement(), "fontSize", FormUtil.getDefaultFontSize());
		//DOM.setStyleAttribute(txtEdit.getElement(), "opacity", "1");
		//txtEdit.setWidth("400"+PurcConstants.UNITS);
		//txtEdit.addStyleName("purcforms-label-editor");
	}

	/**
	 * Stops inplace editing of widget text.
	 * 
	 * @param select a flag to determine whether to select the stoped edit widget.
	 */
	protected void stopLabelEdit(boolean select){
		if(editWidget != null){
			if(selectedPanel.getWidgetIndex(editWidget) < 0){
				editWidget = null;
				return;
			}

			DesignWidgetWrapper designGroupWidgetWrapper = null;
			if(editWidget.getWrappedWidget() instanceof DesignGroupWidget){
				//Stop header label editing
				DesignWidgetWrapper headerLabel = ((DesignGroupWidget)editWidget.getWrappedWidget()).getHeaderLabel();
				if(headerLabel != null){
					designGroupWidgetWrapper = editWidget;
					editWidget = headerLabel;
				}
			}

			editWidget.stopEditMode();

			String beforeChangeText = editWidget.getText();

			String text = txtEdit.getText();
			if((text.trim().length() > 0 && editWidget.getWrappedWidget() instanceof Label) || !(editWidget.getWrappedWidget() instanceof Label))
				editWidget.setText(text);

			if (newlyAddedWidget) {
				Context.getCommandHistory().pop();
				Context.getCommandHistory().add(new InsertWidgetCmd(editWidget, editWidget.getLayoutNode(), this));
				newlyAddedWidget = false;
			}
			else {
				Context.getCommandHistory().add(new ChangeWidgetCmd(editWidget, ChangeWidgetCmd.PROPERTY_TEXT, beforeChangeText, this));
			}

			if(designGroupWidgetWrapper == null){
				selectedPanel.setWidgetPosition(editWidget, editWidget.getLeftInt(), editWidget.getTopInt());
				selectedDragController.makeDraggable(editWidget);
			}
			else
				selectedDragController.makeDraggable(designGroupWidgetWrapper,editWidget);

			if(designGroupWidgetWrapper != null)
				editWidget = designGroupWidgetWrapper;

			if(select){
				selectedDragController.selectWidget(editWidget);
				widgetSelectionListener.onWidgetSelected(editWidget,false);
			}

			/*}
			else{
				DesignSurfaceView surface = (DesignSurfaceView)getParent().getParent().getParent().getParent().getParent().getParent().getParent();
				surface.stopHeaderLabelEdit(editWidget);
			}*/
			editWidget = null;
		}
		else if(labelEditView != null) {
			labelEditView.stopLabelEdit(select);
		}
		
		labelEditView = null;
	}

	@Override
	public void onBrowserEvent(Event event) {

		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:  

			FormDesignerUtil.enableContextMenu(getElement());

			mouseMoved = false;
			x = event.getClientX();
			y = event.getClientY();

			if(editWidget != null){
				boolean targetIsEditWidget = false;
				if(editWidget.getWrappedWidgetEx().getElement() == event.getTarget()) {
					targetIsEditWidget = true;
					x = x - editWidget.getParent().getAbsoluteLeft();
					y = y - editWidget.getParent().getAbsoluteTop();
				}
				
				handleStopLabelEditing(false);
				
				if (targetIsEditWidget) {
					return;
				}
			}

			if((event.getButton() & Event.BUTTON_RIGHT) != 0){
				updatePopup();

				int ypos = event.getClientY();
				if(Window.getClientHeight() - ypos < 220)
					ypos = event.getClientY() - 220;

				int xpos = event.getClientX();
				if(Window.getClientWidth() - xpos < 170)
					xpos = event.getClientX() - 170;

				FormDesignerUtil.disableContextMenu(popup.getElement());
				FormDesignerUtil.disableContextMenu(getElement());
				popup.setPopupPosition(xpos, ypos);
				popup.show();
			}
			else{
				selectionXPos = selectionYPos = -1;
				selectionXPos = x - selectedPanel.getAbsoluteLeft();
				selectionYPos = y - selectedPanel.getAbsoluteTop();

				if(!(event.getShiftKey() || event.getCtrlKey())){
					selectedDragController.clearSelection();
					if(event.getTarget() != this.selectedPanel.getElement()){
						try{
							if(event.getTarget().getInnerText().equals(DesignWidgetWrapper.getTabDisplayText(tabs.getTabBar().getTabHTML(tabs.getTabBar().getSelectedTab())))){
								widgetSelectionListener.onWidgetSelected(getSelPageDesignWidget(),event.getCtrlKey());
								return;
							}
						}catch(Exception ex){}
					}
				}

				if(widgetSelectionListener != null){
					if(this instanceof DesignGroupWidget)
						widgetSelectionListener.onWidgetSelected((DesignWidgetWrapper)this.getParent().getParent(),event.getCtrlKey());

					//if(!(this instanceof DesignGroupWidget) || (this instanceof DesignGroupWidget && !((DesignWidgetWrapper)this.getParent().getParent()).isRepeated()))
					//	widgetSelectionListener.onWidgetSelected(null);

					widgetSelectionListener.onWidgetSelected(this,event.getCtrlKey());
				}

				recursivelyClearGroupBoxSelection();

				if(!(this instanceof DesignGroupWidget && !"default".equals(DOM.getStyleAttribute(getElement(), "cursor"))))
					startRubberBand(event);
			}

			DesignSurfaceView.setSelectedView(this);
			
			break;
		case Event.ONMOUSEMOVE:
			mouseMoved = true;
			if(event.getButton() == Event.BUTTON_LEFT)
				moveRubberBand(event);
			break;
		case Event.ONMOUSEUP:

			//if(selectedPanel.getWidgetCount() > 0)
			stopRubberBand(event);
			if(selectionXPos > 0 && mouseMoved)
				selectWidgets(event);
			mouseMoved = false;
			DOM.releaseCapture(getElement()); //Mouse could have been captured in startRubberBand and so release it.
			break;
			/*case Event.ONKEYDOWN: This when not commented out makes me have to press enter twice to edit checkbox labels
			handleKeyDownEvent(event);
			break;*/
		}
	}

	/**
	 * Un selects all selected widgets, if any, in all group boxes on the current page.
	 */
	public void recursivelyClearGroupBoxSelection(){
		clearSelection();
		
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			Widget wid = selectedPanel.getWidget(index);
			if(!(wid instanceof DesignWidgetWrapper))
				continue;
			if(!(((DesignWidgetWrapper)wid).getWrappedWidget() instanceof DesignGroupWidget))
				continue;
			((DesignGroupWidget)((DesignWidgetWrapper)wid).getWrappedWidget()).clearGroupBoxSelection();

			if(selectedDragController.isWidgetSelected(wid))
				selectedDragController.toggleSelection(wid);
			else
				((DesignGroupWidget)((DesignWidgetWrapper)wid).getWrappedWidget()).recursivelyClearGroupBoxSelection();
		}
	}

	/**
	 * Un selects all selected widgets, if any, on the current page.
	 */
	public void clearSelection(){
		selectedDragController.clearSelection();
	}

	/**
	 * Processes key down events for the selected page.
	 * 
	 * @param event the event object.
	 * @return true if the event has been handled, else false.
	 */
	protected boolean handleKeyDownEvent(Event event){
		/*if(isTextBoxFocus(event)){
			if("none".equalsIgnoreCase(event.getTarget().getStyle().getProperty("borderStyle")));
				return true;
		}*/

		boolean ret = false;

		if(isTextBoxFocus(event) && editWidget == null)
			return false;  //could be on widget properties pane.

		if(this.isVisible()){
			int keyCode = event.getKeyCode();
			if(event.getShiftKey() || event.getCtrlKey())
				ret = resizeWidgets(event);
			else if(keyCode == KeyCodes.KEY_LEFT)
				ret = moveWidgets(MOVE_LEFT);
			else if(keyCode == KeyCodes.KEY_RIGHT)
				ret = moveWidgets(MOVE_RIGHT);
			else if(keyCode == KeyCodes.KEY_UP)
				ret = moveWidgets(MOVE_UP);
			else if(keyCode == KeyCodes.KEY_DOWN)
				ret = moveWidgets(MOVE_DOWN);  

			if(event.getCtrlKey() && (keyCode == 'A' || keyCode == 'a')){
				if(!isTextAreaFocus(event)){ //TODO This works only when the textarea is clicked to get focus. Need to make it work even before clicking the text area (as long as it is visible)
					//As for now, Ctrl+A selects all widgets on the design surface's current tab
					//If one wants to select all widgets within a DesignGroupWidget, they should
					//right click and select all
					if(isTextBoxFocus(event)){
						if(this instanceof DesignGroupWidget && ((DesignGroupWidget)this).editWidget != null)
							((DesignGroupWidget)this).txtEdit.selectAll();

						DOM.eventPreventDefault(event);
					}
					else{
						if(this instanceof DesignSurfaceView)
							((DesignSurfaceView)this).selectAll();
						else if(widgetSelectionListener instanceof DesignSurfaceView)
							((DesignSurfaceView)widgetSelectionListener).selectAll();

						DOM.eventPreventDefault(event);
					}
				}
				ret = true;
			}
			else if(event.getCtrlKey() && (keyCode == 'C' || keyCode == 'c')){
				if(selectedDragController.isAnyWidgetSelected()){
					copyWidgets(false);
					ret = true;
				}
			}
			else if(event.getCtrlKey() && (keyCode == 'X' || keyCode == 'x')){
				if(selectedDragController.isAnyWidgetSelected()){
					cutWidgets();
					ret = true;
				}
			}
			else if(event.getCtrlKey() && (keyCode == 'V' || keyCode == 'v')){
				if(Context.clipBoardWidgets.size() > 0 && x >= 0){
					if(event.getTarget() == selectedPanel.getElement()){
						//x += selectedPanel.getAbsoluteLeft();
						//y += selectedPanel.getAbsoluteTop();
						//pasteWidgets(true);
						pasteItem(false);
						x = -1; //TODO preven;t pasting twice as this is fired twice. Needs smarter solution
					}
					ret = true;
				}
			}
			else if(keyCode == KeyCodes.KEY_DELETE && !isTextBoxFocus(event)){
				if(selectedDragController.isAnyWidgetSelected()){
					deleteWidgets();
					ret = true;
				}
			}
			else if(event.getCtrlKey() && (keyCode == 'F' || keyCode == 'f')){
				format();
				DOM.eventPreventDefault(event);
				ret = false; //For now this is reserved for only designsurfaceview
			}

			if(!ret){
				if(!isTextBoxFocus(event) || (editWidget != null /*&& event.getCurrentTarget() == editWidget.getElement()*/)){
					boolean ret1 = false;
					if(keyCode != KeyCodes.KEY_DELETE && editWidget == null) {
						if (!((event.getCtrlKey() || event.getShiftKey() || event.getMetaKey()) && selectedDragController.isAnyWidgetSelected()))
							ret1 = handleStartLabelEditing(event);
						else
							ret1 = true;
					}
					else if(keyCode == KeyCodes.KEY_ENTER && editWidget != null)
						handleStopLabelEditing(true);
					else if(keyCode == KeyCodes.KEY_ESCAPE && editWidget != null){
						txtEdit.setText(editWidget.getText());
						handleStopLabelEditing(true);
					}

					if(ret1) //If handle start label edit is handled, need to signal such that others are not called for the same.
						ret = true;
				}
			}
		}

		return ret;
	}
	
	public void addLabelAndStartEditing(Event event) {
		int keyCode = event.getKeyCode();
		if(editWidget != null || keyCode == KeyCodes.KEY_ENTER ||
				event.getAltKey() || event.getCtrlKey() || event.getMetaKey() ||
				keyCode == KeyCodes.KEY_TAB || keyCode == KeyCodes.KEY_BACKSPACE ||
				keyCode == KeyCodes.KEY_DELETE || keyCode == KeyCodes.KEY_UP ||
				keyCode == KeyCodes.KEY_DOWN || keyCode == KeyCodes.KEY_ESCAPE ||
				keyCode == KeyCodes.KEY_HOME || keyCode == KeyCodes.KEY_END ||
				keyCode == KeyCodes.KEY_PAGEDOWN || keyCode == KeyCodes.KEY_PAGEUP ||
				keyCode == KeyCodes.KEY_LEFT || keyCode == KeyCodes.KEY_RIGHT)
			return;
		
		x += getAbsoluteLeft();
		y += getAbsoluteTop();
		
		if(this instanceof DesignSurfaceView)
			y += 30;
		else
			y -= 10;
		
		addNewLabel(null, true);
		newlyAddedWidget = true;
		handleStartLabelEditing(event);
	}

	/**
	 * Gets the background color for the selected page.
	 * 
	 * @return the html color value.
	 */
	public String getBackgroundColor(){
		return DOM.getStyleAttribute(selectedPanel.getElement(), "backgroundColor");
	}

	/**
	 * Gets the widgth for the selected page.
	 * 
	 * @return the widget in pixels.
	 */
	public String getWidth(){
		return DOM.getStyleAttribute(selectedPanel.getElement(), "width");
	}

	/**
	 * Gets the height for the selected page.
	 * 
	 * @return the height in pixels.
	 */
	public String getHeight(){
		return DOM.getStyleAttribute(selectedPanel.getElement(), "height");
	}

	/**
	 * Sets the background color of the selected page.
	 * 
	 * @param backgroundColor the background color. This can be any valid html color value.
	 */
	public void setBackgroundColor(String backgroundColor){
		try{
			DOM.setStyleAttribute(selectedPanel.getElement(), "backgroundColor", backgroundColor);
		}catch(Exception ex){}
	}

	/**
	 * Sets the width in pixels of the selected page.
	 */
	public void setWidth(String width){
		try{
			DOM.setStyleAttribute(selectedPanel.getElement(), "width", width);
			txtEdit.setWidth(width);
		}catch(Exception ex){}
	}

	/**
	 * Sets the height in pixels of the selected page.
	 */
	public void setHeight(String height){
		try{
			DOM.setStyleAttribute(selectedPanel.getElement(), "height", height);
		}catch(Exception ex){}
	}

	/**
	 * Adds a new set of radio buttons.
	 * 
	 * @param questionDef the question that we are to add the radio buttons for.
	 * @param vertically set to true to add the radio buttons slopping vertically downwards instead of horizontally.
	 * @return list of radion button widgets that have been created.
	 */
	protected List<DesignWidgetWrapper> addNewRadioButtonSet(QuestionDef questionDef, boolean vertically){
		List<DesignWidgetWrapper> widgets = new ArrayList<DesignWidgetWrapper>();

		List<OptionDef> options = questionDef.getOptions();

		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
			options = new ArrayList<OptionDef>();
			options.add(new OptionDef(1,QuestionDef.TRUE_DISPLAY_VALUE,QuestionDef.TRUE_VALUE,questionDef));
			options.add(new OptionDef(1,QuestionDef.FALSE_DISPLAY_VALUE,QuestionDef.FALSE_VALUE,questionDef));
		}

		if(options != null){
			for(int i=0; i<options.size(); i++){
				/*if(i != 0){
				if(vertically)
					y += 40;
				else
					x += 40;
			}*/

				OptionDef optionDef = (OptionDef)options.get(i);
				DesignWidgetWrapper wrapper = addNewWidget(new RadioButtonWidget(optionDef.getText()),false);
				wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
				wrapper.setFontSize(FormUtil.getDefaultFontSize());
				wrapper.setBinding(optionDef.getBinding());
				wrapper.setParentBinding(questionDef.getBinding());
				wrapper.setText(optionDef.getText());
				wrapper.setTitle(optionDef.getText());

				if(vertically)
					y += 40;
				else
					x += (optionDef.getText().length() * 14);

				selectedDragController.selectWidget(wrapper);

				widgets.add(wrapper);
			}
		}

		/*OptionDef optionDef = new OptionDef(0,LocaleText.get("noSelection"),null,questionDef);
		DesignWidgetWrapper wrapper = addNewWidget(new RadioButtonWidget(optionDef.getText()),false);
		wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
		wrapper.setFontSize(FormUtil.getDefaultFontSize());
		wrapper.setParentBinding(questionDef.getVariableName());
		wrapper.setText(optionDef.getText());
		wrapper.setTitle(optionDef.getText());*/

		return widgets;
	}

	/**
	 * Changes a widget to a different type. For instance single select question types
	 * can have their drop down widget changed to radio buttons.
	 * 
	 * @param vertically set to true to have the widgets vertically, else false to have them horizontally.
	 */
	protected void changeWidget(boolean vertically){
		if(selectedDragController.getSelectedWidgetCount() != 1)
			return;

		DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);

		QuestionDef questionDef = widget.getQuestionDef();
		if(questionDef == null)
			return;

		int type = questionDef.getDataType();

		if(!(widget.getWrappedWidget() instanceof ListBox || 
				(widget.getWrappedWidget() instanceof TextBox && (type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)) ))
			return;


		x = widget.getLeftInt() + selectedPanel.getAbsoluteLeft();
		y = widget.getTopInt() + selectedPanel.getAbsoluteTop();

		Element layoutNode = widget.getLayoutNode();
		if(layoutNode != null){
			layoutNode.getParentNode().removeChild(layoutNode);
			widget.setLayoutNode(null);
		}

		selectedDragController.clearSelection();

		if(widget.getWrappedWidget() instanceof ListBox){
			selectedPanel.remove(widget);
			List<DesignWidgetWrapper> widgets = addNewRadioButtonSet(questionDef, vertically);
			Context.getCommandHistory().add(new ChangeWidgetTypeCmd(widget, layoutNode, widgets, this));
		}
		else{
			//addNewSearchServerWidget(questionDef.getVariableName(),questionDef.getText(), true);
			widget.onDataTypeChanged(questionDef, questionDef.getDataType());
		}

		//increase height if the last widget is beyond our current y coordinate.
		int height = FormUtil.convertDimensionToInt(getHeight());
		if((height + getAbsoluteTop()) < y)
			setHeight(y+PurcConstants.UNITS);
	}
	
	protected void changeToTextBoxWidget(){
		if(selectedDragController.getSelectedWidgetCount() != 1)
			return;

		DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(0);

		Element layoutNode = widget.getLayoutNode();
		if(layoutNode != null){
			layoutNode.getParentNode().removeChild(layoutNode);
			widget.setLayoutNode(null);
		}
		
		Context.getCommandHistory().add(new ChangeWidgetTypeCmd(widget, layoutNode, widget.getWrappedWidget(), this));
		
		widget.copyWidgetProperties(new DesignWidgetWrapper(widget, null), new TextBox());
	}

	/**
	 * Adds a new serach server widget.
	 * 
	 * @param parentBinding the binding of the question for this widget.
	 * @param text the widget display text.
	 * @param select set to true to automatically select the added widget.,
	 * @return the added widget.
	 */
	/*protected DesignWidgetWrapper addNewSearchServerWidget(String parentBinding, String text, boolean select){
		DesignGroupWidget repeat = new DesignGroupWidget(images,this);
		repeat.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(repeat.getElement(), "height","70"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "width","285"+PurcConstants.UNITS);
		repeat.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ????????????????

		DesignWidgetWrapper widget = addNewWidget(repeat,select);
		widget.setRepeated(false);

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wdpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = repeat.getWidgetPopup();
		currentWidgetSelectionListener = repeat;

		int oldY = y;

		y = 28; //20 + 25;
		x = 5; //45;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();
		addNewServerSearch(null,false).setBinding(parentBinding);

		y = 28; //60 + 25;
		x = 10;

		//new
		x = LocaleText.get("noSelection").length() * 10;

		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();

		addNewButton(LocaleText.get("search"),"search",false).setParentBinding(parentBinding);
		//x = 120;

		x = 10;
		x = (LocaleText.get("noSelection").length() * 12) + (LocaleText.get("search").length() * 10);

		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		addNewButton(LocaleText.get("clear"),"clear",false).setParentBinding(parentBinding);

		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wdpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;

		//Header label stuff
		widget.setBorderStyle("dashed");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		y = selectedPanel.getAbsoluteTop();
		x = selectedPanel.getAbsoluteLeft();
		DesignWidgetWrapper headerLabel = addNewLabel(text != null ? text : LocaleText.get("searchServer"), false);
		headerLabel.setBackgroundColor(StyleUtil.COLOR_GROUP_HEADER);
		DOM.setStyleAttribute(headerLabel.getElement(), "width","100%");
		headerLabel.setTextAlign("center");
		selectedDragController.makeNotDraggable(headerLabel);
		headerLabel.setWidth("100%");
		headerLabel.setHeightInt(20);
		headerLabel.setForeColor("white");
		headerLabel.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;

		selectedDragController.makeDraggable(widget,headerLabel);
		repeat.setHeaderLabel(headerLabel);
		//End header label stuff

		y = oldY;

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");

		return widget;
	}*/

	/**
	 * Adds a new group box widget.
	 * 
	 * @param select set to true to automatically selecte the newly added widget.
	 * @return the new widget.
	 */
	protected DesignWidgetWrapper addNewGroupBox(boolean select){

		getDesignSurfaceView().recursivelyClearGroupBoxSelection();
		
		DesignGroupWidget group = new DesignGroupWidget(images,this);
		group.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(group.getElement(), "height","200" + PurcConstants.UNITS);
		DOM.setStyleAttribute(group.getElement(), "width","500" + PurcConstants.UNITS);
		DOM.setStyleAttribute(group.getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		group.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ??????????????

		DesignWidgetWrapper widget = addNewWidget(group,select);
		//selectedDragController.makeNotDraggable(widget);


		//Header label stuff
		widget.setBorderStyle("dashed");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		DesignWidgetWrapper headerLabel = addNewLabel("Header Label", false);
		headerLabel.setBackgroundColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		widget.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		DOM.setStyleAttribute(headerLabel.getElement(), "width","100%");
		headerLabel.setTextAlign("center");
		selectedDragController.makeNotDraggable(headerLabel);
		headerLabel.setWidth("100%");
		headerLabel.setHeightInt(20);
		headerLabel.setForeColor("white");
		headerLabel.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;

		selectedDragController.makeDraggable(widget,headerLabel);
		group.setHeaderLabel(headerLabel);
		//End header label stuff

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");
		
		//registerDropControllers(selectedDragController, widget.getDragController());
		//registerDropControllers(widget.getDragController(), selectedDragController);

		return widget;
	}

	/**
	 * Adds a new repeat section widget.
	 * 
	 * @param select set to true to automatically select the newly added widget.
	 * @return the new widget.
	 */
	protected DesignWidgetWrapper addNewRepeatSection(boolean select){
		DesignGroupWidget repeat = new DesignGroupWidget(images,this);
		repeat.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(repeat.getElement(), "height","100"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "width","500"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		repeat.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ????????????????

		DesignWidgetWrapper widget = addNewWidget(repeat,select);
		widget.setRepeated(true);
		widget.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wdpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = repeat.getWidgetPopup();
		currentWidgetSelectionListener = repeat;

		int oldY = y;
		y = 55 + 0; //50;
		x = 10;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();

		addNewButton(LocaleText.get("addNew"),"addnew",false);
		/*x = 150;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		addNewButton(LocaleText.get("remove"),"remove",false);*/

		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wdpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;


		//Group label headers are turned off from repeats because we use tables
		//instead of absolute panels.
		//Header label stuff
		/*widget.setBorderStyle("dashed");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		oldY = y;
		x = selectedPanel.getAbsoluteLeft();
		y = selectedPanel.getAbsoluteTop();
		DesignWidgetWrapper w = addNewLabel("Header Label", false);
		w.setBackgroundColor(StyleUtil.COLOR_GROUP_HEADER);
		DOM.setStyleAttribute(w.getElement(), "width","100%");
		w.setTextAlign("center");
		//selectedDragController.makeNotDraggable(w);
		w.setWidth("100%");
		w.setForeColor("white");
		w.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;
		y = oldY;*/
		//End header label stuff

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");

		return widget;
	}

	/**
	 * Adds a new picture section widget.
	 * 
	 * @param parentBinding the binding of the question for this widget.
	 * @param text the display text for the widget.
	 * @param select set to true if you want to automatically select the new widget.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewPictureSection(String parentBinding, String text, boolean select){
		DesignGroupWidget repeat = new DesignGroupWidget(images,this);
		repeat.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(repeat.getElement(), "height","245"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "width","200"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		repeat.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ????????????????

		DesignWidgetWrapper widget = addNewWidget(repeat,select);
		widget.setRepeated(false);

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wdpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = repeat.getWidgetPopup();
		currentWidgetSelectionListener = repeat;

		int oldY = y;

		y = 35;
		x = 10;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();
		addNewPicture(false).setBinding(parentBinding);

		y = 55 + 120 + 25;
		x = 10;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();

		addNewButton(LocaleText.get("browse"),"browse",false).setParentBinding(parentBinding);
		x = 120;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		addNewButton(LocaleText.get("clear"),"clear",false).setParentBinding(parentBinding);

		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wdpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;

		//Header label stuff
		widget.setBorderStyle("dashed");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		y = selectedPanel.getAbsoluteTop();
		x = selectedPanel.getAbsoluteLeft();
		DesignWidgetWrapper headerLabel = addNewLabel(text != null ? text : "Picture", false);
		headerLabel.setBackgroundColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		widget.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		DOM.setStyleAttribute(headerLabel.getElement(), "width","100%");
		headerLabel.setTextAlign("center");
		selectedDragController.makeNotDraggable(headerLabel);
		headerLabel.setWidth("100%");
		headerLabel.setHeightInt(20);
		headerLabel.setForeColor("white");
		headerLabel.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;

		selectedDragController.makeDraggable(widget,headerLabel);
		repeat.setHeaderLabel(headerLabel);
		//End header label stuff

		y = oldY;

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");

		return widget;
	}

	/**
	 * Adds a new Audio or Video section widget.
	 * 
	 * @param parentBinding the binding of the question for this widget.
	 * @param text the widget text.
	 * @param select set to true if you want the widget to be automatically selected.
	 * @return the newly added widget.
	 */
	protected DesignWidgetWrapper addNewVideoAudioSection(String parentBinding, String text, boolean select){
		DesignGroupWidget repeat = new DesignGroupWidget(images,this);
		repeat.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(repeat.getElement(), "height","125"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "width","200"+PurcConstants.UNITS);
		DOM.setStyleAttribute(repeat.getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		repeat.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ????????????????

		DesignWidgetWrapper widget = addNewWidget(repeat,select);
		widget.setRepeated(false);

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wdpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = repeat.getWidgetPopup();
		currentWidgetSelectionListener = repeat;

		int oldY = y;

		y = 20 + 25;
		x = 45;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();
		addNewVideoAudio(null,false).setBinding(parentBinding);

		y = 60 + 25;
		x = 10;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		if(selectedPanel.getAbsoluteTop() > 0)
			y += selectedPanel.getAbsoluteTop();

		addNewButton(LocaleText.get("browse"),"browse",false).setParentBinding(parentBinding);
		x = 120;
		if(selectedPanel.getAbsoluteLeft() > 0)
			x += selectedPanel.getAbsoluteLeft();
		addNewButton(LocaleText.get("clear"),"clear",false).setParentBinding(parentBinding);

		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wdpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;

		//Header label stuff
		widget.setBorderStyle("dashed");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		y = selectedPanel.getAbsoluteTop();
		x = selectedPanel.getAbsoluteLeft();
		DesignWidgetWrapper headerLabel = addNewLabel(text != null ? text : LocaleText.get("recording"), false);
		headerLabel.setBackgroundColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		widget.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		DOM.setStyleAttribute(headerLabel.getElement(), "width","100%");
		headerLabel.setTextAlign("center");
		selectedDragController.makeNotDraggable(headerLabel);
		headerLabel.setWidth("100%");
		headerLabel.setHeightInt(20);
		headerLabel.setForeColor("white");
		headerLabel.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;

		selectedDragController.makeDraggable(widget,headerLabel);
		repeat.setHeaderLabel(headerLabel);
		//End header label stuff

		y = oldY;

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");

		return widget;
	}


	public void lockWidgets(){
		//Context.setLockWidgets(!Context.getLockWidgets());
		lockUnlockWidgets(true);
	}
	
	public void unLockWidgets(){
		lockUnlockWidgets(false);
	}

	public void lockUnlockWidgets(boolean lock) {
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);
			widget.setLocked(lock);
			if(widget.getWrappedWidget() instanceof DesignGroupWidget) {
				((DesignGroupWidget)widget.getWrappedWidget()).lockUnlockChildren(lock);
			}
		}
	}
	
	public boolean onWidgetPropertyChanged(byte property, String value){
		if(WidgetPropertySetter.setProperty(property, selectedDragController, value))
			return true;

		int count  = selectedPanel.getWidgetCount();
		for(int index = 0; index < count; index++){
			Widget widget = selectedPanel.getWidget(index);
			if(!(widget instanceof DesignWidgetWrapper))
				continue;

			DesignWidgetWrapper wrapper = (DesignWidgetWrapper)widget;
			if(!(wrapper.getWrappedWidget() instanceof DesignGroupWidget))
				continue;

			if(((DesignGroupWidget)wrapper.getWrappedWidget()).onWidgetPropertyChanged(property, value))
				return true;
		}

		return false;
	}
	
	/**
	 * Adds a new widget, for the selected form field, to the selected page.
	 * 
	 * @param select set to true to automatically select the new widget.
	 * @return the newly added widget.s
	 */
	protected DesignWidgetWrapper addSelectedFormField(boolean select){
		return addToDesignSurface(Context.getSelectedItem(), y, x);
	}
	
	/**
	 * Adds a new group table widget.
	 * 
	 * @param select set to true to automatically select the newly added widget.
	 * @return the new widget.
	 */
	protected DesignWidgetWrapper addNewTable(boolean select){
		String rows = Window.prompt(LocaleText.get("numberOfRowsPrompt"), "4");
		if(rows == null || rows.trim().isEmpty()) 
			return null; //possibly user selected cancel
		
		String cols = Window.prompt(LocaleText.get("numberOfColumnsPrompt"), "4");
		if(cols == null || cols.trim().isEmpty()) 
			return null; //possibly user selected cancel
		
		GridDesignGroupWidget group = new GridDesignGroupWidget(images,this);
		group.addStyleName("getting-started-label2");
		DOM.setStyleAttribute(group.getElement(), "height","200"/*"400"*/+PurcConstants.UNITS);
		DOM.setStyleAttribute(group.getElement(), "width","200"/*"800"*/+PurcConstants.UNITS);
		DOM.setStyleAttribute(group.getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		group.setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK ??????????????

		DesignWidgetWrapper widget = addNewWidget(group,select);
		//selectedDragController.makeNotDraggable(widget);


		//Header label stuff
		widget.setBorderStyle("solid");
		AbsolutePanel panel = selectedPanel;
		FormDesignerDragController dragController = selectedDragController;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();

		DesignWidgetWrapper headerLabel = addNewLabel("Header Label", false);
		headerLabel.setBackgroundColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		widget.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
		DOM.setStyleAttribute(headerLabel.getElement(), "width","100%");
		headerLabel.setTextAlign("center");
		selectedDragController.makeNotDraggable(headerLabel);
		headerLabel.setWidth("100%");
		headerLabel.setHeightInt(20);
		headerLabel.setForeColor("white");
		headerLabel.setFontWeight("bold");

		selectedPanel = panel;
		selectedDragController = dragController;

		selectedDragController.makeDraggable(widget,headerLabel);
		group.setHeaderLabel(headerLabel);
		//End header label stuff

		//Without this, widgets in this box cant use Ctrl + A in edit mode and also
		//edited text is not automatically selected.
		widget.removeStyleName("dragdrop-handle");
		
		group.addTableLines(widget, FormDesignerUtil.convertToInt(rows), FormDesignerUtil.convertToInt(cols));

		return widget;
	}
	
	/**
	 * Gets the y coordinate of the lowest widget on the currently selected page.
	 * 
	 * @return the y coordinate in pixels.
	 */
	public int getLowestWidgetYPos(){

		int lowestYPos = 0;

		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			Widget widget = selectedPanel.getWidget(index);
			int y = widget.getAbsoluteTop() + widget.getOffsetHeight();
			if(y > lowestYPos)
				lowestYPos = y;
		}

		if(lowestYPos > 0)
			lowestYPos -= selectedPanel.getAbsoluteTop();

		return lowestYPos;
	}
	
	protected void ensureVisible(Widget widget) {
		
	}
	
	protected void ensureTabVisible(DesignWidgetWrapper widgetWrapper) {
		Widget widget = widgetWrapper.getParent();
		while (!(widget instanceof DesignSurfaceView)){
			widget = widget.getParent();
			if(widget instanceof DesignWidgetWrapper)
				widgetWrapper = (DesignWidgetWrapper)widget;
		}
		
		FormDesignerDropController dropController = ((DesignSurfaceView)widget).getWidgetDropController(widgetWrapper);
		((DesignSurfaceView)widget).selectTab(dropController);
	}
	
	/**
	 * Fills bindings for loaded widgets in a given panel.
	 * 
	 * @param panel the panel.
	 * @param bindings the map of bindings. Made a map instead of list for only easy of search with key.
	 */
	public void fillWidgetBindings(AbsolutePanel panel, HashMap<String, DesignWidgetWrapper> bindings, HashMap<String, DesignWidgetWrapper> labels){
		if(panel.getWidgetIndex(rubberBand) > -1)
			panel.remove(rubberBand);

		for(int index = 0; index < panel.getWidgetCount(); index++){
			Widget wid = panel.getWidget(index);
			if(!(wid instanceof DesignWidgetWrapper)) {
				panel.remove(wid);
				continue;
			}
			
			DesignWidgetWrapper widget = (DesignWidgetWrapper)wid;

			String binding = widget.getBinding();
			if (widget.getParentBinding() != null) {
				binding = widget.getParentBinding() + "-purcforms-" + binding;
			}
			
			bindings.put(binding, widget); //Could possibly put widget as value.
			
			//When a widget is deleted, it is reloaded on refresh even if its label still exists.
			if(widget.getWrappedWidget() instanceof Label) {
				labels.put(binding, widget); 
				continue;
			}

			if(widget.getWrappedWidget() instanceof DesignGroupWidget)
				fillWidgetBindings(((DesignGroupWidget)widget.getWrappedWidget()).getPanel(), bindings, labels);
		}
	}
	
	/**
	 * Adds a new set of check boxes.
	 * 
	 * @param questionDef the multiple select question whose check boxes we are adding.
	 * @param vertically set to true if you want to add the check boxes vertically.
	 * @param tabIndex the current tab index.
	 * @return this is always null.
	 */
	protected DesignWidgetWrapper addNewCheckBoxSet(QuestionDef questionDef, boolean vertically, int tabIndex){
		List options = questionDef.getOptions();
		if(options == null)
			return null;
		
		for(int i=0; i < options.size(); i++){
			/*if(i != 0){
				y += 40;

				if((y+40) > max){
					y += 10;
					//addNewButton(false);
					addNewTab(pageName);
					y = 20;
				}
			}*/

			OptionDef optionDef = (OptionDef)options.get(i);
			DesignWidgetWrapper wrapper = addNewWidget(new CheckBox(optionDef.getText()),false);
			wrapper.setFontFamily(FormUtil.getDefaultFontFamily());
			wrapper.setFontSize(FormUtil.getDefaultFontSize());
			wrapper.setBinding(optionDef.getBinding());
			wrapper.setParentBinding(questionDef.getBinding());
			wrapper.setText(optionDef.getText());
			wrapper.setTitle(optionDef.getText());
			wrapper.setTabIndex(++tabIndex);

			if(i < (options.size() - 1)){
				if(vertically)
					y += 40;
				else
					x += (optionDef.getText().length() * 12);
			}
		}

		return null;
	}
	
	/**
	 * Adds a new repeat set of widgets.
	 * 
	 * @param questionDef the repeat question whose widgets we are adding.
	 * @param select set to true to select the repeat widget after adding it.
	 * @return the added repeat widget.
	 */
	protected DesignWidgetWrapper addNewRepeatSet(QuestionDef questionDef, boolean select, CommandList commands, boolean useExistingPos){
		if(!useExistingPos)
			x = 35 + selectedPanel.getAbsoluteLeft();
		
		y += 25;

		int oldX = x;
		Vector questions = questionDef.getGroupQtnsDef().getQuestions();
		if(questions == null)
			return addNewTextBox(select); //TODO Bug here
		for(int index = 0; index < questions.size(); index++){
			QuestionDef qtn = (QuestionDef)questions.get(index);
			if(index > 0)
				x += 210;
			DesignWidgetWrapper label = addNewLabel(qtn.getText(),select);
			label.setBinding(qtn.getBinding());
			label.setTitle(qtn.getText());
			label.setTextDecoration("underline");
			
			if(commands != null)
				commands.add(new InsertWidgetCmd(label, label.getLayoutNode(), this));
		}

		if(!useExistingPos)
			x = 20 + selectedPanel.getAbsoluteLeft();
		else
			x = oldX;
		
		y += 25;
		DesignWidgetWrapper widget = addNewRepeatSection(select);

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wgpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;
		currentWidgetSelectionListener = (DesignGroupWidget)widget.getWrappedWidget();

		int oldY = y;
		y = x = 10;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = widget.getWidgetPopup();

		/*y = 30;
		Vector questions = questionDef.getRepeatQtnsDef().getQuestions();
		if(questions == null)
			return addNewTextBox(select); //TODO Bug here
		for(int index = 0; index < questions.size(); index++){
			QuestionDef qtn = (QuestionDef)questions.get(index);
			if(index > 0)
				x += 210;
			DesignWidgetWrapper label = addNewLabel(qtn.getText(),select);
			label.setBinding(qtn.getVariableName());
			label.setTextDecoration("underline");
		}*/

		//y = x = 10;
		x += selectedPanel.getAbsoluteLeft();
		y += selectedPanel.getAbsoluteTop() + 0; //50;

		DesignWidgetWrapper widgetWrapper = null;
		for(int index = 0; index < questions.size(); index++){
			QuestionDef qtn = (QuestionDef)questions.get(index);
			if(index > 0)
				x += 205;

			if(qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || 
					qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
				widgetWrapper = addNewDropdownList(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_DATE)
				widgetWrapper = addNewDatePicker(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_DATE_TIME)
				widgetWrapper = addNewDateTimeWidget(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_TIME)
				widgetWrapper = addNewTimeWidget(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
				widgetWrapper = addNewCheckBoxSet(qtn,false,index);
				index += qtn.getOptions().size();
			}
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
				widgetWrapper = addNewDropdownList(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_IMAGE)
				widgetWrapper = addNewPicture(select);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_VIDEO ||
					qtn.getDataType() == QuestionDef.QTN_TYPE_AUDIO)
				widgetWrapper = addNewVideoAudioSection(null,qtn.getText(),select);
			else
				widgetWrapper = addNewTextBox(select);

			if(widgetWrapper != null){//addNewCheckBoxSet returns null
				widgetWrapper.setBinding(qtn.getBinding());
				widgetWrapper.setQuestionDef(qtn);
				widgetWrapper.setTitle(qtn.getText());
				widgetWrapper.setTabIndex(index + 1);
			}
		}

		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wgpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;
		y += 90; //130; //25;

		if(questions.size() == 1)
			widget.setWidthInt(265);
		else
			widget.setWidthInt((questions.size() * 205)+15);
		return widget;
	}
	
	/**
	 * Adds a new group set of widgets.
	 * 
	 * @param questionDef the group question whose widgets we are adding.
	 * @param select set to true to select the group widget after adding it.
	 * @return the added group widget.
	 */
	protected DesignWidgetWrapper addNewGroupSet(QuestionDef questionDef, boolean select, CommandList commands, boolean useExistingPos){
		Vector questions = questionDef.getGroupQtnsDef().getQuestions();
		if(questions == null)
			return addNewTextBox(select); //TODO Bug here

		if(!useExistingPos)
			x = 20 + selectedPanel.getAbsoluteLeft();
		
		DesignWidgetWrapper widget = addNewGroupBox(select);
		DesignWidgetWrapper headerLabel = ((DesignGroupWidget)widget.getWrappedWidget()).getHeaderLabel();
		headerLabel.setText(questionDef.getText());

		FormDesignerDragController selDragController = selectedDragController;
		AbsolutePanel absPanel = selectedPanel;
		PopupPanel wgpopup = widgetPopup;
		WidgetSelectionListener wgSelectionListener = currentWidgetSelectionListener;
		currentWidgetSelectionListener = (DesignGroupWidget)widget.getWrappedWidget();

		int oldY = y;
		y = x = 10;

		selectedDragController = widget.getDragController();
		selectedPanel = widget.getPanel();
		widgetPopup = widget.getWidgetPopup();

		x += selectedPanel.getAbsoluteLeft();
		y += selectedPanel.getAbsoluteTop() + headerLabel.getHeightInt();

		int oldX = x, oldHeight = 0, widestValue = 0;
		y -= 10;
		DesignWidgetWrapper widgetWrapper = null;
		for(int index = 0; index < questions.size(); index++){
			QuestionDef qtn = (QuestionDef)questions.get(index);
			if(oldY > 0)
				y += oldHeight + 10;
			
			int type = qtn.getDataType();
			if(!(type == QuestionDef.QTN_TYPE_VIDEO || type == QuestionDef.QTN_TYPE_AUDIO || type == QuestionDef.QTN_TYPE_IMAGE
					|| type == QuestionDef.QTN_TYPE_GROUP)){
				/*labelWidgetWrapper = */widgetWrapper = addNewLabel(qtn.getText(),false);
				widgetWrapper.setBinding(qtn.getBinding());
				widgetWrapper.setTitle(qtn.getText());

				if(select)
					selectedDragController.selectWidget(widgetWrapper);

				if(commands != null)
					commands.add(new InsertWidgetCmd(widgetWrapper, widgetWrapper.getLayoutNode(), this));
			}

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT){
				widgetWrapper.setFontWeight("bold");
				widgetWrapper.setFontStyle("italic");
			}

			widgetWrapper = null;

			if(!(type == QuestionDef.QTN_TYPE_VIDEO || type == QuestionDef.QTN_TYPE_AUDIO || type == QuestionDef.QTN_TYPE_IMAGE))
				x += (qtn.getText().length() * 10);
			
			if(qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || 
					qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
				widgetWrapper = addNewDropdownList(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_DATE)
				widgetWrapper = addNewDatePicker(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_DATE_TIME)
				widgetWrapper = addNewDateTimeWidget(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_TIME)
				widgetWrapper = addNewTimeWidget(false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
				widgetWrapper = addNewCheckBoxSet(qtn, true, index);
				index += qtn.getOptions().size();
			}
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
				widgetWrapper = addNewDropdownList(false);
			else if(type == QuestionDef.QTN_TYPE_REPEAT)
				widgetWrapper = addNewRepeatSet(qtn, false, commands, useExistingPos);
			else if(type == QuestionDef.QTN_TYPE_GROUP)
				widgetWrapper = addNewGroupSet(qtn, false, commands, useExistingPos);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_IMAGE) 
				widgetWrapper = addNewPictureSection(qtn.getBinding(), qtn.getText(), false);
			else if(qtn.getDataType() == QuestionDef.QTN_TYPE_VIDEO ||
					qtn.getDataType() == QuestionDef.QTN_TYPE_AUDIO)
				widgetWrapper = addNewVideoAudioSection(null,qtn.getText(),select);
			else
				widgetWrapper = addNewTextBox(select);

			if(widgetWrapper != null){//addNewCheckBoxSet returns null
				widgetWrapper.setBinding(qtn.getBinding());
				widgetWrapper.setQuestionDef(qtn);
				widgetWrapper.setTitle(qtn.getText());
				widgetWrapper.setTabIndex(index + 1);
			}
			else {
				//Must have called addNewCheckBoxSet
				//and so this should be the last added checkbox
				y += 20; //Add some space below a set of checkboxes
				widgetWrapper = (DesignWidgetWrapper)selectedPanel.getWidget(selectedPanel.getWidgetCount() - 1);
			}
			
			x = oldX;
			oldHeight = widgetWrapper.getHeightInt();
			
			int right = widgetWrapper.getAbsoluteLeft() + widgetWrapper.getWidthInt();
			if(right > widestValue)
				widestValue = right;
		}

		widget.setHeightInt((y - widget.getAbsoluteTop()) + widgetWrapper.getHeightInt() + 10);
		
		selectedDragController.clearSelection();

		selectedDragController = selDragController;
		selectedPanel = absPanel;
		widgetPopup = wgpopup;
		currentWidgetSelectionListener = wgSelectionListener;

		y = oldY;
		
		if(widget.getWidgetSelectionListener() == this)
			y += widget.getHeightInt() - 20;

		((DesignGroupWidget)widget.getWrappedWidget()).selectAll();
		((DesignGroupWidget)widget.getWrappedWidget()).format();
		
		int right = widget.getAbsoluteLeft() + widget.getWidthInt();
		if(widestValue > right) 
			widget.setWidthInt(widget.getWidthInt() + (widestValue - right) + 20);
		
		return widget;
	}
	
	public DesignWidgetWrapper addNewTab(String name, boolean forcename){
		return addNewTab(name, selectedTabIndex, forcename);
	}

	/**
	 * Adds a new tab with a given name and selects it.
	 * 
	 * @param name the tab name.
	 */
	public DesignWidgetWrapper addNewTab(String name, int index, boolean forcename){
		initPanel();
		
		//if(!forcename)
		if(name == null && (Context.getFormDef() == null || Context.getFormDef().getPages() == null)) {
			name = LocaleText.get("page")+(tabs.getWidgetCount());
		}

		//tabs.insert(selectedPanel, name, index + 1);
		tabs.add(selectedPanel, name);
		selectedTabIndex = tabs.getWidgetCount() - 1;
		tabs.selectTab(selectedTabIndex);

		DesignWidgetWrapper widget = new DesignWidgetWrapper(tabs.getTabBar(),widgetPopup,this);
		widget.setBinding(name);
		widget.setFontFamily(FormUtil.getDefaultFontFamily());
		widget.setFontSize(FormUtil.getDefaultFontSize());
		widget.setFontWeight("normal");
		pageWidgets.put(tabs.getTabBar().getTabCount()-1, widget);

		//widgetSelectionListener.onWidgetSelected(widget);

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				//onWindowResized(Window.getClientWidth(), Window.getClientHeight());
				setHeight(getHeight());
			}
		});
		
		return widget;
	}
	
	/**
	 * Does automatic loading of question widgets onto the design surface for a given page
	 * and starting at a given y coordinate.
	 * 
	 * @param questions the list of questions.
	 * @param pageName the name of the page.
	 * @param startY the y coordinate to start at.
	 * @param startX the x coordinate to start at.
	 * @param tabIndex the tabIndex to start from.
	 * @param submitCancelBtns set to true to add the submit and cancel buttons
	 * @param select set to true to select all the created widgets.
	 */
	protected DesignWidgetWrapper loadQuestions(List<QuestionDef> questions, int startY, int startX, int tabIndex, boolean submitCancelBtns, boolean select, CommandList commands, boolean useExistingPos){
		if(questions == null)
			return null;
		
		boolean adjustSize = !(x == startX && y == startY);
		if(!adjustSize){
			startY = startY - selectedPanel.getAbsoluteTop();
			startX = startX - selectedPanel.getAbsoluteLeft();
		}
			
		int maxX = 0, max = 999999; //FormUtil.convertDimensionToInt(sHeight) - 0 + 150; //40; No longer adding submit button on every page
		x = startX;
		y = startY;

		x += selectedPanel.getAbsoluteLeft();
		y += selectedPanel.getAbsoluteTop();

		DesignWidgetWrapper widgetWrapper = null, labelWidgetWrapper = null;
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.get(i);

			//TODO Why should we not show a widget, atleast on the design surface?
			if(!questionDef.isVisible() || (questionDef.isRequired() && (questionDef.isLocked() || !questionDef.isEnabled())) )
				; //continue;

			int type = questionDef.getDataType();
			if(questionDef.isGroupQtnsDef() && questionDef.getGroupQtnsDef().getQuestions() == null)
				continue;
			
			//Provide a way of the user turning off some widgets not to be displayed.
			if(!questionDef.isVisible() && (questionDef.getDefaultValue() == null || questionDef.getDefaultValue().trim().length() == 0))
				continue;

			if(!(type == QuestionDef.QTN_TYPE_VIDEO || type == QuestionDef.QTN_TYPE_AUDIO || type == QuestionDef.QTN_TYPE_IMAGE
					|| type == QuestionDef.QTN_TYPE_GROUP)){
				labelWidgetWrapper = widgetWrapper = addNewLabel(questionDef.getText(),false);
				widgetWrapper.setBinding(questionDef.getBinding());
				widgetWrapper.setTitle(questionDef.getText());

				if(select)
					selectedDragController.selectWidget(widgetWrapper);

				if(commands != null)
					commands.add(new InsertWidgetCmd(widgetWrapper, widgetWrapper.getLayoutNode(), this));
			}

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT){
				widgetWrapper.setFontWeight("bold");
				widgetWrapper.setFontStyle("italic");
			}

			widgetWrapper = null;

			if(!(type == QuestionDef.QTN_TYPE_VIDEO || type == QuestionDef.QTN_TYPE_AUDIO || type == QuestionDef.QTN_TYPE_IMAGE)){
				if(!useExistingPos)
					x += (questionDef.getText().length() * 10);
			}

			if(type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
					type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
				widgetWrapper = addNewDropdownList(false);
			else if(type == QuestionDef.QTN_TYPE_DATE)
				widgetWrapper = addNewDatePicker(false);
			else if(type == QuestionDef.QTN_TYPE_DATE_TIME)
				widgetWrapper = addNewDateTimeWidget(false);
			else if(type == QuestionDef.QTN_TYPE_TIME)
				widgetWrapper = addNewTimeWidget(false);
			else if(type == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
				widgetWrapper = addNewCheckBoxSet(questionDef,true,tabIndex);
				tabIndex += questionDef.getOptions().size();
			}
			else if(type == QuestionDef.QTN_TYPE_BOOLEAN)
				widgetWrapper = addNewDropdownList(false);
			else if(type == QuestionDef.QTN_TYPE_REPEAT)
				widgetWrapper = addNewRepeatSet(questionDef, false, commands, useExistingPos);
			else if(type == QuestionDef.QTN_TYPE_GROUP)
				widgetWrapper = addNewGroupSet(questionDef, false, commands, useExistingPos);
			else if(type == QuestionDef.QTN_TYPE_IMAGE)
				widgetWrapper = addNewPictureSection(questionDef.getBinding(),questionDef.getText(),false);
			else if(type == QuestionDef.QTN_TYPE_VIDEO || type == QuestionDef.QTN_TYPE_AUDIO)
				widgetWrapper = addNewVideoAudioSection(questionDef.getBinding(),questionDef.getText(),false);
			else
				widgetWrapper = addNewTextBox(false);

			if(widgetWrapper != null){
				if(!(type == QuestionDef.QTN_TYPE_IMAGE|| type == QuestionDef.QTN_TYPE_VIDEO|| type == QuestionDef.QTN_TYPE_AUDIO))
					widgetWrapper.setBinding(questionDef.getBinding());

				widgetWrapper.setQuestionDef(questionDef);

				String helpText = questionDef.getHelpText();
				if(helpText != null && helpText.trim().length() > 0)
					helpText = questionDef.getHelpText();
				else
					helpText = questionDef.getText();

				widgetWrapper.setTitle(helpText);
				widgetWrapper.setTabIndex(++tabIndex);

				if(select)
					selectedDragController.selectWidget(widgetWrapper);

				if(commands != null)
					commands.add(new InsertWidgetCmd(widgetWrapper, widgetWrapper.getLayoutNode(), this));
			}

			if(x > maxX)
				maxX = x;

			x = 20 + selectedPanel.getAbsoluteLeft();
			y += 40;

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_IMAGE)
				y += 195 + 30;
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_VIDEO || questionDef.getDataType() == QuestionDef.QTN_TYPE_AUDIO)
				y += 75 + 30;

			int rptIncr = 0;
			if(i < questions.size()-1){
				int dataType = ((QuestionDef)questions.get(i+1)).getDataType();
				if(dataType == QuestionDef.QTN_TYPE_REPEAT)
					rptIncr = 90 + 50;
				else if(dataType == QuestionDef.QTN_TYPE_IMAGE)
					rptIncr = 195 + 30;
				else if(dataType == QuestionDef.QTN_TYPE_VIDEO || dataType == QuestionDef.QTN_TYPE_AUDIO)
					rptIncr = 75 + 30;
			}

			//TODO Looks like this is not longer necessary as we can have a page as long as the user wants
			if((y+40+rptIncr) > max){
				y += 10;
				//addNewButton(false);
				addNewTab(LocaleText.get("page"), true);
				y = 20 + selectedPanel.getAbsoluteTop();
			}
		}

		y += 10;

		//The submit button is added only to the first tab such that we don't keep
		//adding multiple submit buttons every time one refreshes the design surface
		if(submitCancelBtns){
			addSubmitButton(false).setTabIndex(++tabIndex);

			x += 200;
			addCancelButton(false).setTabIndex(++tabIndex);
		}

		if (getParent() instanceof ScrollPanel)
			y += ((ScrollPanel)getParent()).getScrollPosition();

		if(adjustSize)
			setHeight(y+40+PurcConstants.UNITS);

		if(maxX < 900)
			maxX = 900;
		if(adjustSize && FormUtil.convertDimensionToInt(getWidth()) < maxX)
			setWidth(maxX + PurcConstants.UNITS);
		
		return widgetWrapper != null ? widgetWrapper : labelWidgetWrapper;
	}
	
	public DesignWidgetWrapper addToDesignSurface(Object item) {
		return addToDesignSurface(item, getLowestWidgetYPos() + 20, 20);
	}
	
	public DesignSurfaceView getDesignSurfaceView() {
		Widget widget = this;
		while (!(widget instanceof DesignSurfaceView)){
			widget = widget.getParent();
		}
		
		return (DesignSurfaceView)widget;
	}
	
	public void fillWidgetBindings(HashMap<String, DesignWidgetWrapper> bindings, HashMap<String, DesignWidgetWrapper> labels) {
		List<DropController> dropControllers = FormDesignerDragController.getInstance().getDropControllers();
		for(int i=0; i<dropControllers.size(); i++){
			Widget dropTarget = dropControllers.get(i).getDropTarget();
			if(!(dropTarget instanceof AbsolutePanel))
				continue;
			fillWidgetBindings((AbsolutePanel)dropTarget, bindings, labels);
		}
	}
	
	public FormDesignerDropController getWidgetDropController(DesignWidgetWrapper widget){
		for(int i=0; i<tabDropControllers.size(); i++){
			FormDesignerDropController dropController = tabDropControllers.get(i);
			Widget dropTarget = dropController.getDropTarget();
			if(!(dropTarget instanceof AbsolutePanel))
				continue;
			if(((AbsolutePanel)dropTarget).getWidgetIndex(widget) > -1)
				return dropController;
		}
		
		assert(false); //how can a widget have no drop controller????
		return null;
	}
	
	public DesignWidgetWrapper selectItem(Object item) {
		if(item == null)
			return null;
		
		QuestionDef questionDef =  null;
		String binding = null;
		if(item instanceof QuestionDef) {
			questionDef  = (QuestionDef)item;
			binding = questionDef.getBinding();
		}
		else if(item instanceof OptionDef) {
			questionDef  = ((OptionDef)item).getParent();
			binding = questionDef.getBinding() + "-purcforms-" + ((OptionDef)item).getBinding();
		}
		else
			return null;
		
		getDesignSurfaceView().recursivelyClearGroupBoxSelection();
		
		//Create list of bindings for widgets that are already loaded on the design surface.
		HashMap<String, DesignWidgetWrapper> bindings = new HashMap<String, DesignWidgetWrapper>();
		HashMap<String, DesignWidgetWrapper> labels = new HashMap<String, DesignWidgetWrapper>();
		getDesignSurfaceView().fillWidgetBindings(bindings, labels);
		 
		if(bindings.containsKey(binding)){
			DesignWidgetWrapper widget = bindings.get(binding);
			FormDesignerDragController dragController = FormDesignerDragController.getInstance();//getDesignSurfaceView().getWidgetDragController(widget);
			if(dragController != null){
				dragController.selectWidget(widget);
				
				//select the label too
				if(labels.containsKey(binding))
					dragController.selectWidget(labels.get(binding));
				
				ensureTabVisible(widget);
				ensureVisible(widget);
				
				return widget;
			}
			else {
				//Can this really happen???
				assert(false);
			}
		}
		
		return null;
	}
	
	public DesignWidgetWrapper addToDesignSurface(Object item, int y, int x) {
		
		if(item == null)
			return null;
		
		QuestionDef questionDef =  null;
		if(item instanceof QuestionDef)
			questionDef  = (QuestionDef)item;
		else if(item instanceof OptionDef)
			questionDef  = ((OptionDef)item).getParent();
		else
			return null;
		
		DesignWidgetWrapper widget = selectItem(item);
		if (widget != null) {
			return widget;
		}
		
		if (item instanceof OptionDef) {
			OptionDef optionDef = (OptionDef)item;
			if (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
				widget = addNewWidget(new CheckBox(optionDef.getText()), true);
			else
				widget = addNewWidget(new RadioButtonWidget(optionDef.getText()), true);
			
			widget.setFontFamily(FormUtil.getDefaultFontFamily());
			widget.setFontSize(FormUtil.getDefaultFontSize());
			widget.setBinding(optionDef.getBinding());
			widget.setParentBinding(questionDef.getBinding());
			widget.setText(optionDef.getText());
			widget.setTitle(optionDef.getText());
		}
		else {
			CommandList commands = new CommandList(this);
	
			List<QuestionDef> newQuestions = new ArrayList<QuestionDef>();
			newQuestions.add(questionDef);
			
			//Load the new questions onto the design surface for the current page.
			if(newQuestions.size() > 0){
				boolean visible = questionDef.isVisible();
				questionDef.setVisible(true);
				widget = loadQuestions(newQuestions,  y, x, selectedPanel.getWidgetCount(),false, true, commands, false);
				questionDef.setVisible(visible);
				
				format();
				ensureVisible(widget);
			}
	
			if(commands.size() > 0)
				Context.getCommandHistory().add(commands);
		}
		
		return widget;
	}
	
	/**
	 * Gives a chance to child widgets to process keyboard events.
	 * 
	 * @param event the event object.
	 * @return true if any child has handled the event, else false.
	 */
	protected boolean childHandleKeyDownEvent(Event event){
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			Widget widget = selectedPanel.getWidget(index);
			if(!(widget instanceof DesignWidgetWrapper))
				continue;
			if(!(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof DesignGroupWidget))
				continue;

			if(((DesignGroupWidget)((DesignWidgetWrapper)widget).getWrappedWidget()).handleKeyDownEvent(event))
				return true;
			else if(((DesignGroupWidget)((DesignWidgetWrapper)widget).getWrappedWidget()).childHandleKeyDownEvent(event))
				return true;
		}

		return false;
	}
	
	public boolean remove(DesignWidgetWrapper widget){
		return selectedPanel.remove(widget);
	}
	
	public void addTableLines(DesignWidgetWrapper widgetWrapper, int horizontalLines, int verticalLines) {
		horizontalLines -= 1;
		verticalLines -= 1;
		
		x = widgetWrapper.getAbsoluteLeft();
		y = 20 + widgetWrapper.getAbsoluteTop();
		
		int width = widgetWrapper.getWidthInt();
		int length = width / (verticalLines + 1);
	
		for (int i = 0; i < verticalLines; i++) {
			x += length;
			addNewVerticalLine(false);
		}
		
		x = widgetWrapper.getAbsoluteLeft();
		y = 20 + widgetWrapper.getAbsoluteTop();
		
		int height = widgetWrapper.getHeightInt();
		length = (height - 20) / (horizontalLines + 1);
		for (int i = 0; i < horizontalLines; i++) {
			y += length;
			addNewHorizontalLine(false);
		}
	}
	
	public boolean findLabel(String text) {
		AbsolutePanel lastFoundWidgetPanel = null;
		if (Context.getLastFoundWidget() != null) {
			lastFoundWidgetPanel = (AbsolutePanel)Context.getLastFoundWidget().getParent();
		}
		boolean lastWidgetPanelHasBeenFound = false;
		
		List<DropController> dropControllers = FormDesignerDragController.getInstance().getDropControllers();
		for(int i=0; i<dropControllers.size(); i++){
			Widget dropTarget = dropControllers.get(i).getDropTarget();
			if(!(dropTarget instanceof AbsolutePanel))
				continue;
			
			AbsolutePanel panel = (AbsolutePanel)dropTarget;
			if (panel == lastFoundWidgetPanel) {
				lastWidgetPanelHasBeenFound  = true;
			}
		
			if (lastFoundWidgetPanel != null && !lastWidgetPanelHasBeenFound) {
				continue;
			}
			
			if (findLabel(panel, text))
				return true;
		}
		
		return false;
	}
	
	public boolean findLabel(AbsolutePanel panel, String text){
		if(panel.getWidgetIndex(rubberBand) > -1)
			panel.remove(rubberBand);

		DesignWidgetWrapper lastFoundWidget = Context.getLastFoundWidget();
		boolean lastWidgetHasBeenFound = false;
		
		for(int index = 0; index < panel.getWidgetCount(); index++){
			Widget wid = panel.getWidget(index);
			if(!(wid instanceof DesignWidgetWrapper)) {
				panel.remove(wid);
				continue;
			}
			
			DesignWidgetWrapper widget = (DesignWidgetWrapper)wid;
			
			if (widget == lastFoundWidget) {
				lastWidgetHasBeenFound  = true;
				continue;
			}
		
			if (lastFoundWidget != null && !lastWidgetHasBeenFound && lastFoundWidget.getParent() == panel) {
				continue;
			}

			String labelText = widget.getText();
			if (labelText != null && labelText.toLowerCase().contains(text)) {
				selectWidget(widget, panel);
				ensureTabVisible(widget);
				ensureVisible(widget);
				Context.setLastFoundWidget(widget);
				return true;
			}

			if(widget.getWrappedWidget() instanceof DesignGroupWidget) {
				if (findLabel(((DesignGroupWidget)widget.getWrappedWidget()).getPanel(), text))
					return true;
			}
		}
		
		return false;
	}
	
	public void find(){
		String text = Window.prompt(LocaleText.get("find"), Context.getSearchText());
		if (text == null || text.trim().length() == 0)
			return;
		
		selectedDragController.clearSelection();
		
		Context.setSearchText(text);
		
		text = text.toLowerCase();
		
		if (!findLabel(text)) {
			Context.setLastFoundWidget(null);
			Window.alert(LocaleText.get("noDataFound"));
			return;
		}
	}
	
	public void bold() {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getFontWeight();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_FONT_WEIGHT, prevValue, view));
			
			widget.setFontWeight("bold".equals(prevValue) ? "normal" : "bold");
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void italic() {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getFontStyle();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_FONT_STYLE, prevValue, view));
			
			widget.setFontStyle("italic".equals(prevValue) ? "normal" : "italic");
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void underline() {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getTextDecoration();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_TEXT_DECORATION, prevValue, view));
			
			widget.setTextDecoration("underline".equals(prevValue) ? "none" : "underline");
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void foreColor(String color) {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getForeColor();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_FORE_COLOR, prevValue, view));
			
			widget.setForeColor(color);
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void fontFamily(String fontFamily) {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getFontFamily();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_FONT_FAMILY, prevValue, view));
			
			widget.setFontFamily(fontFamily);
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void fontSize(String fontSize) {
		if (!selectedDragController.isAnyWidgetSelected())
			return;
		
		CommandList commands = new CommandList(this);
		
		for(int i=0; i<selectedDragController.getSelectedWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)selectedDragController.getSelectedWidgetAt(i);

			String prevValue = widget.getFontSize();
			DesignGroupView view = widget.getView();
			commands.add(new ChangeWidgetCmd(widget, ChangeWidgetCmd.PROPERTY_FONT_SIZE, prevValue, view));
			
			widget.setFontSize(fontSize);
		}

		Context.getCommandHistory().add(commands);
	}
	
	public void onRowsAdded(DesignWidgetWrapper tableWidget, int increment){

		//Get the current bottom y position of the table widget.
		int bottomYpos = getBottomYPos(tableWidget) - increment;

		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			DesignWidgetWrapper currentWidget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
			if(currentWidget == tableWidget)
				continue;

			int top = currentWidget.getTopInt();
			if(top >= bottomYpos)
				currentWidget.setTopInt(top + increment);
		}

		DOM.setStyleAttribute(selectedPanel.getElement(), "height", getHeightInt()+increment+PurcConstants.UNITS);	

		setParentHeight(true, tableWidget, increment);
	}
	
	public void onColumnsAdded(DesignWidgetWrapper tableWidget, int increment){

		//Get the current right x position of the table widget.
		int rightXpos = getRightXPos(tableWidget) - increment;

		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			DesignWidgetWrapper currentWidget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
			if(currentWidget == tableWidget)
				continue;

			int left = currentWidget.getLeftInt();
			if(left >= rightXpos)
				currentWidget.setLeftInt(left + increment);
		}

		DOM.setStyleAttribute(selectedPanel.getElement(), "width", getWidthInt()+increment+PurcConstants.UNITS);	

		setParentHeight(true, tableWidget, increment);
	}
	
	public void onRowsRemoved(DesignWidgetWrapper tableWidget, int decrement){

		//Get the current bottom y position of the table widget.
		int bottomYpos = getBottomYPos(tableWidget) + decrement;

		//Move widgets which are below the bottom of the table widget.
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			DesignWidgetWrapper currentWidget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
			if(currentWidget == tableWidget)
				continue;

			int top = currentWidget.getTopInt();
			if(top >= bottomYpos)
				currentWidget.setTopInt(top - decrement);
		}

		DOM.setStyleAttribute(selectedPanel.getElement(), "height", getHeightInt()-decrement+PurcConstants.UNITS);

		setParentHeight(false, tableWidget, decrement);
	}
	
	public void onColumnsRemoved(DesignWidgetWrapper tableWidget, int decrement){

		//Get the current right x position of the table widget.
		int rightXpos = getRightXPos(tableWidget) + decrement;

		//Move widgets which are on the right of the table widget.
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			DesignWidgetWrapper currentWidget = (DesignWidgetWrapper)selectedPanel.getWidget(index);
			if(currentWidget == tableWidget)
				continue;

			int left = currentWidget.getLeftInt();
			if(left >= rightXpos)
				currentWidget.setLeftInt(left - decrement);
		}

		DOM.setStyleAttribute(selectedPanel.getElement(), "width", getWidthInt()-decrement+PurcConstants.UNITS);

		setParentWidth(false, tableWidget, decrement);
	}

	private int getBottomYPos(DesignWidgetWrapper tableWidget){
		int bottomYpos = tableWidget.getTopInt() + tableWidget.getHeightInt();

		Widget parent = tableWidget.getParent();
		while(parent != null){
			if(parent instanceof DesignGroupWidget){
				DesignWidgetWrapper wrapper = (DesignWidgetWrapper)((DesignGroupWidget)parent).getParent().getParent();
				if(selectedPanel.getWidgetIndex(wrapper) != -1){
					bottomYpos = wrapper.getTopInt() + wrapper.getHeightInt();
					break;
				}
			}
			else if(parent instanceof DesignSurfaceView)
				break;

			parent = parent.getParent();
		}

		return bottomYpos;
	}
	
	private int getRightXPos(DesignWidgetWrapper tableWidget){
		int rightXpos = tableWidget.getLeftInt() + tableWidget.getWidthInt();

		Widget parent = tableWidget.getParent();
		while(parent != null){
			if(parent instanceof DesignGroupWidget){
				DesignWidgetWrapper wrapper = (DesignWidgetWrapper)((DesignGroupWidget)parent).getParent().getParent();
				if(selectedPanel.getWidgetIndex(wrapper) != -1){
					rightXpos = wrapper.getLeftInt() + wrapper.getWidthInt();
					break;
				}
			}
			else if(parent instanceof DesignSurfaceView)
				break;

			parent = parent.getParent();
		}

		return rightXpos;
	}
	
	private void setParentHeight(boolean increase, DesignWidgetWrapper tableWidget, int change){
		Widget parent = tableWidget.getParent();
		while(parent != null){
			if(parent instanceof DesignGroupWidget){
				DesignWidgetWrapper wrapper = (DesignWidgetWrapper)((DesignGroupWidget)parent).getParent().getParent();
				int height = wrapper.getHeightInt();
				wrapper.setHeight((increase ? height+change : height-change)+PurcConstants.UNITS);
			}
			else if(parent instanceof DesignSurfaceView)
				return;

			parent = parent.getParent();
		}
	}
	
	private void setParentWidth(boolean increase, DesignWidgetWrapper tableWidget, int change){
		Widget parent = tableWidget.getParent();
		while(parent != null){
			if(parent instanceof DesignGroupWidget){
				DesignWidgetWrapper wrapper = (DesignWidgetWrapper)((DesignGroupWidget)parent).getParent().getParent();
				int width = wrapper.getWidthInt();
				wrapper.setWidth((increase ? width+change : width-change)+PurcConstants.UNITS);
			}
			else if(parent instanceof DesignSurfaceView)
				return;

			parent = parent.getParent();
		}
	}
	
	private int getHeightInt(){
		return FormUtil.convertDimensionToInt(DOM.getStyleAttribute(selectedPanel.getElement(), "height"));
	}
	
	private int getWidthInt(){
		return FormUtil.convertDimensionToInt(DOM.getStyleAttribute(selectedPanel.getElement(), "width"));
	}
}
