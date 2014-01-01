package org.purc.purcforms.client.querybuilder.view;

import java.util.HashMap;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.QueryBuilder;
import org.purc.purcforms.client.querybuilder.controller.ConditionController;
import org.purc.purcforms.client.querybuilder.controller.FilterRowActionListener;
import org.purc.purcforms.client.querybuilder.model.FilterCondition;
import org.purc.purcforms.client.querybuilder.model.FilterConditionGroup;
import org.purc.purcforms.client.querybuilder.sql.XmlBuilder;
import org.purc.purcforms.client.querybuilder.widget.AddConditionHyperlink;
import org.purc.purcforms.client.querybuilder.widget.ConditionActionHyperlink;
import org.purc.purcforms.client.querybuilder.widget.ConditionWidget;
import org.purc.purcforms.client.querybuilder.widget.GroupHyperlink;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * 
 * @author daniel
 *
 */
public class FilterConditionsView  extends Composite implements ConditionController, FilterRowActionListener{

	private static final int HORIZONTAL_SPACING = 5;
	private static final int VERTICAL_SPACING = 5;


	private VerticalPanel verticalPanel = new VerticalPanel();
	private AddConditionHyperlink addConditionLink = new AddConditionHyperlink(LocaleText.get("clickToAddNewCondition"),"",1);
	private GroupHyperlink groupHyperlink = new GroupHyperlink(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL,"",1, null);
	private ConditionActionHyperlink actionHyperlink;

	private FormDef formDef;
	private QuestionDef questionDef;
	private boolean enabled = true;


	public FilterConditionsView(){
		setupWidgets();
	}

	private void setupWidgets(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);
		horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);

		actionHyperlink = new ConditionActionHyperlink("<>","",false,1,addConditionLink,this);

		horizontalPanel.add(actionHyperlink);
		horizontalPanel.add(new Label("Choose records where")); //LocaleText.get("when")
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));
		verticalPanel.add(horizontalPanel);

		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition((Widget)event.getSource(), true);
			}
		});

		verticalPanel.setSpacing(VERTICAL_SPACING);
		verticalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
		
		initWidget(verticalPanel);
	}

	public ConditionWidget addCondition(Widget sender, boolean select){
		ConditionWidget conditionWidget = null;
		
		if(formDef != null && enabled){
			Widget widget = conditionWidget = new ConditionWidget(formDef,this,true,questionDef,1,addConditionLink, true);
			int index = verticalPanel.getWidgetIndex(sender);
			if(index == -1){
				AddConditionHyperlink addConditionHyperlink = (AddConditionHyperlink)sender;
				if(sender instanceof ConditionActionHyperlink)
					addConditionHyperlink = ((ConditionActionHyperlink)sender).getAddConditionHyperlink();

				index = verticalPanel.getWidgetIndex(addConditionHyperlink);
				if(index == -1)
					index = verticalPanel.getWidgetIndex(addConditionHyperlink.getParent());

				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
				int depth = addConditionHyperlink.getDepth();
				horizontalPanel.add(getSpace(depth));
				conditionWidget = new ConditionWidget(formDef,this,true,questionDef,depth,addConditionHyperlink, select);
				horizontalPanel.add(conditionWidget);
				widget = horizontalPanel;
			}

			verticalPanel.insert(widget, index);
		}
		
		return conditionWidget;
	}

	public ConditionActionHyperlink addBracket(Widget sender, String operator, boolean addCondition, boolean select){
		int depth = ((ConditionActionHyperlink)sender).getDepth() + 1;

		int index = verticalPanel.getWidgetIndex(((ConditionActionHyperlink)sender).getAddConditionHyperlink());
		if(index == -1)
			index = verticalPanel.getWidgetIndex(((ConditionActionHyperlink)sender).getAddConditionHyperlink().getParent());

		AddConditionHyperlink addConditionLink = new AddConditionHyperlink(LocaleText.get("clickToAddNewCondition"),"",depth);
		ConditionActionHyperlink actionHyperlink = new ConditionActionHyperlink("<>","",true,depth,addConditionLink,this);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);
		horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
		if(depth > 2)
			horizontalPanel.add(getSpace3(depth-1));
		
		CheckBox chk = new CheckBox();
		chk.setValue(select);
		horizontalPanel.add(chk);
		horizontalPanel.add(actionHyperlink);

		GroupHyperlink groupHyperlink = new GroupHyperlink(operator != null ? operator : GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL,"",depth, chk);
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));

		verticalPanel.insert(horizontalPanel, index);

		if(addCondition){
			horizontalPanel = new HorizontalPanel();
			horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
			horizontalPanel.add(getSpace(depth));
			horizontalPanel.add(new ConditionWidget(formDef,this,true,questionDef,depth,addConditionLink, true));
			verticalPanel.insert(horizontalPanel, ++index);
		}

		horizontalPanel = new HorizontalPanel();
		horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
		horizontalPanel.add(getSpace2(depth));
		horizontalPanel.add(addConditionLink);
		verticalPanel.insert(horizontalPanel, ++index);

		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition((Widget)event.getSource(), true);
			}
		});

		return actionHyperlink;
	}

	public void deleteCurrentRow(Widget sender){
		int startIndex = verticalPanel.getWidgetIndex(sender.getParent());

		ConditionActionHyperlink actionHyperlink = (ConditionActionHyperlink)sender;
		int sendIndex = verticalPanel.getWidgetIndex(actionHyperlink.getAddConditionHyperlink().getParent());

		int count = sendIndex - startIndex;
		for(int index = 0; index <= count; index++)
			verticalPanel.remove(startIndex);
	}

	public void deleteCondition(Widget sender,ConditionWidget conditionWidget){
		verticalPanel.remove(conditionWidget.getParent());
	}

	public void setFormDef(FormDef formDef){
		this.formDef = formDef;
		this.questionDef = null;
		clearConditions();
		addAddConditionLink();
	}

	public void addAddConditionLink(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);
		horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
		horizontalPanel.add(addConditionLink);
		verticalPanel.add(horizontalPanel);
	}

	public void clearConditions(){
		questionDef = null;
		while(verticalPanel.getWidgetCount() > 1)
			verticalPanel.remove(verticalPanel.getWidget(1));
	}

	public FilterConditionGroup getFilterConditionRows(){

		HashMap<String,FilterConditionGroup> groupDepth = new HashMap<String,FilterConditionGroup>();

		FilterConditionGroup retGroup = new FilterConditionGroup();
		retGroup.setConditionsOperator(groupHyperlink.getConditionsOperator());
		groupDepth.put(""+groupHyperlink.getDepth()+"", retGroup);

		int count = verticalPanel.getWidgetCount();
		for(int i=1; i<count; i++)
			getFilterConditionRow((HorizontalPanel)verticalPanel.getWidget(i),groupDepth);

		return retGroup;
	}

	private void getFilterConditionRow(HorizontalPanel horizontalPanel,HashMap<String,FilterConditionGroup> groupDepth){
		for(int index = 0; index < horizontalPanel.getWidgetCount(); index++){
			Widget widget = horizontalPanel.getWidget(index);
			if(widget instanceof ConditionWidget){
				ConditionWidget conditionWidget = (ConditionWidget)widget;
				Condition condition = conditionWidget.getCondition();
				if(condition == null)
					return;

				QuestionDef questionDef = formDef.getQuestion(condition.getQuestionId());
				if(questionDef == null)
					return;

				FilterCondition row = new FilterCondition();
				row.setFieldName(getFieldName(questionDef));
				row.setFirstValue(condition.getValue());
				row.setSecondValue(condition.getSecondValue());
				row.setOperator(condition.getOperator());
				row.setSelected(condition.isSelected());
				row.setDataType(questionDef.getDataType());
				groupDepth.get(((ConditionWidget)widget).getDepth()+"").addCondition(row);
				return;
			}
			else if(widget instanceof GroupHyperlink){
				GroupHyperlink groupHyperlink = (GroupHyperlink)widget;
				FilterConditionGroup row = new FilterConditionGroup();
				row.setConditionsOperator(groupHyperlink.getConditionsOperator());
				row.setSelected(groupHyperlink.isSelected());
				groupDepth.put(""+groupHyperlink.getDepth()+"", row);
				groupDepth.get((groupHyperlink.getDepth()-1)+"").addCondition(row);
				return;
			}
		}
	}

	private static String getFieldName(QuestionDef questionDef){
		int index = questionDef.getBinding().lastIndexOf('/');
		if(index > -1)
			return questionDef.getBinding().substring(index+1);
		return questionDef.getBinding();
	}

	public FormDef getFormDef(){
		return formDef;
	}

	private HTML getSpace(int depth){
		String s = "";
		for(int i = 1; i < depth; i++)
			s += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		return new HTML(s);
	}

	private HTML getSpace2(int depth){
		String s = "";
		for(int i = 1; i < depth; i++)
			s += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		return new HTML(s);
	}

	private HTML getSpace3(int depth){
		String s = "";
		for(int i = 1; i < depth; i++)
			s += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		return new HTML(s);
	}

	public void loadQueryDef(String xml){
		Document doc = XMLParser.parse(xml);
		Element rootNode = doc.getDocumentElement();
		if(!rootNode.getNodeName().equalsIgnoreCase(XmlBuilder.NODE_NAME_QUERYDEF))
			return;
		
		NodeList nodes = rootNode.getElementsByTagName(XmlBuilder.NODE_NAME_FILTER_CONDITIONS);
		if(nodes == null || nodes.getLength() == 0)
			return;
		
		rootNode = (Element)nodes.item(0);
		nodes = rootNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node node = nodes.item(index);
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase(XmlBuilder.NODE_NAME_GROUP)){
				clearConditions(); //we always have one top level group. so no need of even this for loop. :)
				groupHyperlink.setText(((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_OPERATOR));
				addAddConditionLink();
				loadConditions((Element)node,actionHyperlink);
				break;
			}
		}
	}

	private void loadConditions(Element element,ConditionActionHyperlink actionHyperlink){
		NodeList nodes = element.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node node = nodes.item(index);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				boolean selected = "true".equals(((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_SELECTED));
				if(node.getNodeName().equalsIgnoreCase(XmlBuilder.NODE_NAME_GROUP))
					loadConditions((Element)node, addBracket(actionHyperlink,((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_OPERATOR),false, selected));
				else if(node.getNodeName().equalsIgnoreCase(XmlBuilder.NODE_NAME_CONDITION)){
					ConditionWidget conditionWidget = addCondition(actionHyperlink, selected);
					conditionWidget.setQuestionDef(formDef.getQuestion(((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_FIELD)));
					conditionWidget.setOparator(Integer.parseInt(((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_OPERATOR)));
					conditionWidget.setValue(((Element)node).getAttribute(XmlBuilder.ATTRIBUTE_NAME_VALUE));
				}
			}
		}
	}
}
