package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;
import org.purc.purcforms.client.widget.grid.GridLine;

import com.google.gwt.user.client.ui.AbsolutePanel;


/**
 * 
 * @author danielkayiwa
 *
 */
public class MoveWidgetCmd implements ICommand {

	private DesignGroupView view;
	private DesignWidgetWrapper widget;
	private int x = 0;
	private int y = 0;
	
	
	public MoveWidgetCmd(DesignWidgetWrapper widget, int x, int y, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.x = x;
		this.y = y;
	}
	
	public String getName(){
		return "Move Widget";
	}
	
	public void undo(){
		view = widget.getView();
		
		widget.setLeftInt(widget.getLeftInt() + x);
		widget.setTopInt(widget.getTopInt() + y);
		
		PanelHistory panelHistory = widget.getPanelHistory();
		if(panelHistory.getSamePanelCount() == 0) {
			panelHistory = panelHistory.getPanelHistory();
			widget.setPanelHistory(panelHistory);
		}
		
		panelHistory.decrementSamePanelCount();
		
		AbsolutePanel prevPanel = panelHistory.getPanel();
		prevPanel.add(widget, widget.getLeftInt(), widget.getTopInt());
		
		if(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof GridLine) {
			DesignGroupView view = ((DesignWidgetWrapper)widget).getView();
			if(view instanceof GridDesignGroupWidget)
				((GridDesignGroupWidget)view).moveLine(-x, -y, widget.getLeftInt(), widget.getTopInt());
		}

		widget.getPrevView().selectWidget(widget, prevPanel/*panel*/);
	}
	
	public void redo(){
		
		widget.storePrevPanel();
		
		widget.setLeftInt(widget.getLeftInt() - x);
		widget.setTopInt(widget.getTopInt() - y);
		
		view.getPanel().add(widget, widget.getLeftInt(), widget.getTopInt());
		
		if(((DesignWidgetWrapper)widget).getWrappedWidget() instanceof GridLine) {
			DesignGroupView view = ((DesignWidgetWrapper)widget).getView();
			if(view instanceof GridDesignGroupWidget)
				((GridDesignGroupWidget)view).moveLine(x, y, widget.getLeftInt(), widget.getTopInt());
		}

		view.selectWidget(widget, view.getPanel());
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}