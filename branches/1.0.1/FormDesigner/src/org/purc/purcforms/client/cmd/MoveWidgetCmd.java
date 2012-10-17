package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;


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
		
		widget.getPrevPanel().add(widget, widget.getLeftInt(), widget.getTopInt());
		widget.getPrevView().selectWidget(widget, widget.getPrevPanel()/*panel*/);
	}
	
	public void redo(){
		widget.setLeftInt(widget.getLeftInt() - x);
		widget.setTopInt(widget.getTopInt() - y);
		
		view.getPanel().add(widget, widget.getLeftInt(), widget.getTopInt());
		view.selectWidget(widget, view.getPanel());
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}