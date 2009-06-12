package org.purc.purcforms.client.widget;

import java.util.List;

import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.DynamicOptionDef;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormDesignerUtil;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author daniel
 *
 */
public class ValueWidget extends Composite implements ItemSelectionListener, PopupListener{

	private static final String EMPTY_VALUE = "_____";
	private static final String BETWEEN_WIDGET_SEPARATOR = "   "+ LocaleText.get("and") + "   ";
	private static final String BETWEEN_VALUE_SEPARATOR = " " + LocaleText.get("and") + " ";
	private static final String LIST_SEPARATOR = " , ";

	private QuestionDef questionDef;
	private int operator = ModelConstants.OPERATOR_NULL;

	private HorizontalPanel horizontalPanel;
	private TextBox txtValue1 = new TextBox();
	private TextBox txtValue2 = new TextBox();
	private Label lblAnd = new Label(BETWEEN_WIDGET_SEPARATOR);
	private Hyperlink valueHyperlink;
	private PopupPanel popup;
	private KeyboardListenerAdapter keyboardListener1;
	private KeyboardListenerAdapter keyboardListener2;
	private QuestionDef prevQuestionDef;
	private CheckBox chkQuestionValue = new CheckBox(LocaleText.get("questionValue"));
	private FormDef formDef;
	private SuggestBox sgstField = new SuggestBox();
	private QuestionDef valueQtnDef;


	public ValueWidget(){
		setupWidgets();
	}

	public void setQuestionDef(QuestionDef questionDef){
		prevQuestionDef = this.questionDef;
		this.questionDef = questionDef;
	}

	public void setOperator(int operator){
		if(this.operator != operator){ 
			if(this.operator == ModelConstants.OPERATOR_IS_NULL)
				valueHyperlink.setText(EMPTY_VALUE);

			/*if((this.operator == PurcConstants.OPERATOR_IN_LIST || this.operator == PurcConstants.OPERATOR_NOT_IN_LIST) &&
			  !(operator == PurcConstants.OPERATOR_IN_LIST || operator == PurcConstants.OPERATOR_NOT_IN_LIST))
		    	valueHyperlink.setText(EMPTY_VALUE);*/
		}

		this.operator = operator;

		if(operator == ModelConstants.OPERATOR_IS_NULL)
			valueHyperlink.setText("");
		else if(operator == ModelConstants.OPERATOR_BETWEEN || operator == ModelConstants.OPERATOR_NOT_BETWEEN)
			valueHyperlink.setText(EMPTY_VALUE + BETWEEN_VALUE_SEPARATOR + EMPTY_VALUE);
	}

	private void setupWidgets(){
		horizontalPanel = new HorizontalPanel();;

		valueHyperlink = new Hyperlink(EMPTY_VALUE,null);
		horizontalPanel.add(valueHyperlink);

		valueHyperlink.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				startEdit();
			}
		});

		//Cuases wiered behaviour when editing with between operator
		/*txtValue1.addFocusListener(new FocusListenerAdapter(){
			public void onLostFocus(Widget sender){
				stopEdit();
			}
		});*/

		setupTextListeners();

		this.chkQuestionValue.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				setupFieldSelection();
			}
		});

		initWidget(horizontalPanel);
	}

	private void setupTextListeners(){

		txtValue1.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER)
					stopEdit(true);
			}
		});

		txtValue2.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER)
					stopEdit(true);
			}
		});

		if(!(operator == ModelConstants.OPERATOR_BETWEEN || operator == ModelConstants.OPERATOR_NOT_BETWEEN)){
			txtValue1.addFocusListener(new FocusListenerAdapter(){
				public void onLostFocus(Widget sender){

					//stopEdit(true);
				}
			});
			txtValue2.addFocusListener(new FocusListenerAdapter(){
				public void onLostFocus(Widget sender){
					stopEdit(true);
				}
			});

			//if(txtValue1 instanceof DatePicker){
			txtValue1.addChangeListener(new ChangeListener(){
				public void onChange(Widget sender){
					stopEdit(true);
				}
			});
			txtValue2.addChangeListener(new ChangeListener(){
				public void onChange(Widget sender){
					stopEdit(true);
				}
			});
			//}
		}
	}

	private void setupFieldSelection(){
		if(chkQuestionValue.isChecked()){
			if(horizontalPanel.getWidgetIndex(txtValue1) > -1){
				horizontalPanel.remove(txtValue1);
				horizontalPanel.remove(chkQuestionValue);
				setupPopup();
				horizontalPanel.add(sgstField);
				horizontalPanel.add(chkQuestionValue);
				sgstField.setFocus(true);
				sgstField.setFocus(true);
				txtValue1.selectAll();
			}
		}
		else{
			if(horizontalPanel.getWidgetIndex(sgstField) > -1){
				horizontalPanel.remove(sgstField);
				horizontalPanel.remove(chkQuestionValue);
				if(txtValue1.getParent() != null && txtValue1.getParent() instanceof SuggestBox){
					txtValue1.removeKeyboardListener(keyboardListener1);
					txtValue2.removeKeyboardListener(keyboardListener2);
					txtValue1 = new TextBox();
					txtValue2 = new TextBox();
					setupTextListeners();
				}

				horizontalPanel.add(txtValue1);
				horizontalPanel.add(chkQuestionValue);
				txtValue1.setFocus(true);
				txtValue1.setFocus(true);
				txtValue1.selectAll();
			}
		}
	}

	private void startEdit(){
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
			MenuBar menuBar = new MenuBar(true);
			menuBar.addItem(QuestionDef.TRUE_DISPLAY_VALUE,true, new SelectItemCommand(QuestionDef.TRUE_DISPLAY_VALUE,this));
			menuBar.addItem(QuestionDef.FALSE_DISPLAY_VALUE,true, new SelectItemCommand(QuestionDef.FALSE_DISPLAY_VALUE,this));

			popup = new PopupPanel(true,false);
			popup.setWidget(menuBar);
			popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop());
			popup.show();
		}
		else if( (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE
				|| questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC) &&
				(operator == ModelConstants.OPERATOR_EQUAL || operator == ModelConstants.OPERATOR_NOT_EQUAL) ){

			MenuBar menuBar = new MenuBar(true);

			int size = 0, maxSize = 0; String text;
			List options = questionDef.getOptions();

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
				DynamicOptionDef dynamicOptionDef = formDef.getChildDynamicOptions(questionDef.getId());
				if(dynamicOptionDef == null)
					return;
				options = dynamicOptionDef.getOptions();
			}

			if(options == null)
				return;

			for(int i=0; i<options.size(); i++){
				OptionDef optionDef = (OptionDef)options.get(i);
				text = optionDef.getText();
				size = text.length();
				if(maxSize < size)
					maxSize = size;
				menuBar.addItem(text,true, new SelectItemCommand(optionDef,this));
			}

			/*ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setWidget(menuBar);
			scrollPanel.setHeight("200px");
			scrollPanel.setWidth((maxSize*11)+"px");*/

			int height = options.size()*40;
			if(height > 200)
				height = 200;

			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setWidget(menuBar);
			scrollPanel.setHeight(height+"px"); //"200px"
			scrollPanel.setWidth((maxSize*12)+"px");

			popup = new PopupPanel(true,false);
			popup.setWidget(scrollPanel);
			popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop());
			popup.show();
		}
		else if( (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE
				|| questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC) &&
				(operator == ModelConstants.OPERATOR_IN_LIST || operator == ModelConstants.OPERATOR_NOT_IN_LIST) ){

			String values = valueHyperlink.getText();
			String[] vals = null;
			if(!values.equals(EMPTY_VALUE))
				vals = values.split(LIST_SEPARATOR);

			int size = 0, maxSize = 0; String text;
			VerticalPanel panel = new VerticalPanel();
			List options = questionDef.getOptions();

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
				DynamicOptionDef dynamicOptionDef = formDef.getChildDynamicOptions(questionDef.getId());
				if(dynamicOptionDef == null)
					return;
				options = dynamicOptionDef.getOptions();
			}

			if(options == null)
				return;

			for(int i=0; i<options.size(); i++){
				OptionDef optionDef = (OptionDef)options.get(i);

				text = optionDef.getText();
				size = text.length();
				if(maxSize < size)
					maxSize = size;

				CheckBox checkbox = new CheckBox(text);
				if(InArray(vals,text))
					checkbox.setChecked(true);
				panel.add(checkbox);
			}

			int height = options.size()*40;
			if(height > 200)
				height = 200;

			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setWidget(panel);
			scrollPanel.setHeight(height+"px"); //"200px"
			scrollPanel.setWidth((maxSize*12)+"px");

			popup = new PopupPanel(true,false);
			popup.addPopupListener(this);
			popup.setWidget(scrollPanel);
			popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop());
			popup.show();
		}
		else{
			//horizontalPanel.remove(valueHyperlink);
			//horizontalPanel.add(txtValue1);

			//if(!valueHyperlink.getText().equals(EMPTY_VALUE))
			//	txtValue1.setText(valueHyperlink.getText());

			txtValue1.removeKeyboardListener(keyboardListener1);
			txtValue2.removeKeyboardListener(keyboardListener2);

			/*if(questionDef.getDataType() ==  QuestionDef.QTN_TYPE_DATE){
				txtValue1 = new DatePickerWidget();
				txtValue2 = new DatePickerWidget();
			}
			else*/{
				txtValue1 = new TextBox();
				txtValue2 = new TextBox();
			}

			if(chkQuestionValue.isChecked())
				setupPopup();

			setupTextListeners();

			horizontalPanel.remove(valueHyperlink);

			if(chkQuestionValue.isChecked())
				horizontalPanel.add(sgstField);
			else
				horizontalPanel.add(txtValue1);

			horizontalPanel.add(chkQuestionValue);

			if(!valueHyperlink.getText().equals(EMPTY_VALUE) && (prevQuestionDef == questionDef || prevQuestionDef == null))
				txtValue1.setText(valueHyperlink.getText());

			if(!chkQuestionValue.isChecked())
				addNumericKeyboardListener();

			if(chkQuestionValue.isChecked()){
				sgstField.setFocus(true);
				sgstField.setFocus(true);
			}
			else{
				txtValue1.setFocus(true);
				txtValue1.setFocus(true);
			}
			txtValue1.selectAll();

			if(operator ==  ModelConstants.OPERATOR_BETWEEN ||
					operator ==  ModelConstants.OPERATOR_NOT_BETWEEN){
				horizontalPanel.add(lblAnd);
				horizontalPanel.add(txtValue2);

				String val = txtValue1.getText();
				if(val.contains(BETWEEN_VALUE_SEPARATOR)){
					int pos = val.indexOf(BETWEEN_VALUE_SEPARATOR);
					String s = val.substring(0, pos);
					if(s.equals(EMPTY_VALUE))
						s = "";
					txtValue1.setText(s);
					if(pos+BETWEEN_VALUE_SEPARATOR.length() != val.length()){
						pos = pos + BETWEEN_VALUE_SEPARATOR.length();
						s = val.substring(pos,val.length());
						if(s.equals(EMPTY_VALUE)){
							s = "";
							if(txtValue1.getText().trim().length() > 0)
								txtValue2.setFocus(true);
						}
						txtValue2.setText(s);
					}
					else
						txtValue2.setText("");
				}
			}
		}
	}

	private void addNumericKeyboardListener(){
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC || questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL){
			keyboardListener1 = FormDesignerUtil.getAllowNumericOnlyKeyboardListener(txtValue1, questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ? false : true);
			keyboardListener2 = FormDesignerUtil.getAllowNumericOnlyKeyboardListener(txtValue2, questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ? false : true);

			txtValue1.addKeyboardListener(keyboardListener1);
			txtValue2.addKeyboardListener(keyboardListener2);
		}
	}

	private boolean InArray(String[] array, String item){
		if(array == null)
			return false;

		for(int i=0; i<array.length; i++){
			if(array[i].equals(item))
				return true;
		}
		return false;
	}

	public void stopEdit(boolean updateValue){
		String val1 = txtValue1.getText();		

		if(val1.trim().length() == 0){
			val1 = EMPTY_VALUE;
			//if(txtValue1 instanceof DatePicker)
			//	return;
		}

		String val2 = txtValue2.getText();
		if(val2.trim().length() == 0){
			val2 = EMPTY_VALUE;
			//if(txtValue2 instanceof DatePicker)
			//	return;
		}

		String val = val1 + 
		((operator == ModelConstants.OPERATOR_BETWEEN || 
				operator == ModelConstants.OPERATOR_NOT_BETWEEN) ? (BETWEEN_VALUE_SEPARATOR + val2 ): "");

		if(updateValue)
			valueHyperlink.setText(val);

		horizontalPanel.remove(txtValue1);
		horizontalPanel.remove(txtValue2);
		horizontalPanel.remove(lblAnd);
		horizontalPanel.remove(chkQuestionValue);
		horizontalPanel.remove(sgstField);
		horizontalPanel.add(valueHyperlink);
	}

	public void onItemSelected(Object sender, Object item) {
		if(sender instanceof SelectItemCommand){
			popup.hide();
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
					questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE ||
					questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
				valueHyperlink.setText(((OptionDef)item).getText());
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
				valueHyperlink.setText((String)item);
		}
	}

	public void onStartItemSelection(Object sender){

	}

	public void onPopupClosed(PopupPanel sender, boolean autoClosed){
		String value = "";
		VerticalPanel panel = (VerticalPanel)popup.getWidget();
		int count = panel.getWidgetCount();
		for(int i=0; i<count; i++){
			CheckBox checkbox = (CheckBox)panel.getWidget(i);
			if(checkbox.isChecked()){
				if(value.length() > 0)
					value += LIST_SEPARATOR;
				value += checkbox.getText();
			}
		}
		if(value.length() == 0)
			value = EMPTY_VALUE;
		valueHyperlink.setText(value);
	}

	public String getValue(){
		valueQtnDef = null;

		String val = valueHyperlink.getText();
		if(val.equals(EMPTY_VALUE))
			return null;

		if(chkQuestionValue.isChecked()){
			valueQtnDef = formDef.getQuestionWithText(val);
			if(valueQtnDef != null)
				val = valueQtnDef.getVariableName();
			else
				val = EMPTY_VALUE;
		}

		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE){
			OptionDef optionDef = questionDef.getOptionWithText(val);
			if(optionDef != null)
				val = optionDef.getVariableName();
			else
				val = null;
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
			DynamicOptionDef dynamicOptionDef = formDef.getChildDynamicOptions(questionDef.getId());
			if(dynamicOptionDef != null){
				OptionDef optionDef = dynamicOptionDef.getOptionWithText(val);
				if(optionDef != null)
					val = optionDef.getVariableName();
			}
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
			String[] options = val.split(LIST_SEPARATOR);
			if(options == null || options.length == 0)
				val = null;
			else{
				val = "";
				for(int i=0; i<options.length; i++){
					OptionDef optionDef = questionDef.getOptionWithText(options[i]);
					if(optionDef != null){
						if(val.length() > 0)
							val += LIST_SEPARATOR;
						val += optionDef.getVariableName();
					}
				}
			}
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
			if(val.equals(QuestionDef.TRUE_DISPLAY_VALUE))
				val = QuestionDef.TRUE_VALUE;
			else if(val.equals(QuestionDef.FALSE_DISPLAY_VALUE))
				val = QuestionDef.FALSE_VALUE;
		}

		if(val != null && this.chkQuestionValue.isChecked())
			val = formDef.getVariableName() + "/" + val;

		return val;
	}

	public void setValue(String value){
		String sValue = value;

		if(sValue != null){
			if(sValue.startsWith(formDef.getVariableName() + "/")){
				sValue = sValue.substring(sValue.indexOf('/')+1);
				QuestionDef qtn = formDef.getQuestion(sValue);
				if(qtn != null)
					sValue = qtn.getText();
				else{ //possibly varname changed.
					if(valueQtnDef != null){
						qtn = formDef.getQuestion(valueQtnDef.getVariableName());
						if(qtn != null)
							sValue = qtn.getText();
						else
							sValue = EMPTY_VALUE;
					}
					else
						sValue = EMPTY_VALUE;
				}
				chkQuestionValue.setChecked(true);
			}

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE){
				OptionDef optionDef = ((OptionDef)questionDef.getOptionWithValue(value));
				if(optionDef != null)
					sValue = optionDef.getText();
			}
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
				DynamicOptionDef dynamicOptionDef = formDef.getChildDynamicOptions(questionDef.getId());
				if(dynamicOptionDef != null){
					OptionDef optionDef = dynamicOptionDef.getOptionWithValue(value);
					if(optionDef != null)
						sValue = optionDef.getText();
				}
			}
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
				String[] options = sValue.split(LIST_SEPARATOR);
				if(options == null || options.length == 0)
					sValue = null;
				else{
					sValue = "";
					for(int i=0; i<options.length; i++){
						OptionDef optionDef = questionDef.getOptionWithValue(options[i]);
						if(optionDef != null){
							if(sValue.length() > 0)
								sValue += LIST_SEPARATOR;
							sValue += optionDef.getText();
						}
					}
				}
			}
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
				if(sValue.equals(QuestionDef.TRUE_VALUE))
					sValue = QuestionDef.TRUE_DISPLAY_VALUE;
				else if(sValue.equals(QuestionDef.FALSE_VALUE))
					sValue = QuestionDef.FALSE_DISPLAY_VALUE;
			}
		}
		else
			sValue = EMPTY_VALUE;

		valueHyperlink.setText(sValue);
	}

	public void setFormDef(FormDef formDef){
		this.formDef = formDef;
		//setupPopup();
	}

	private void setupPopup(){
		txtValue1.removeKeyboardListener(keyboardListener1);

		txtValue1 = new TextBox();

		txtValue1.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if(keyCode == KeyboardListener.KEY_ENTER)
					stopEdit(true);
			}
		});

		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

		for(int i=0; i<formDef.getPageCount(); i++)
			FormDesignerUtil.loadQuestions(formDef.getPageAt(i).getQuestions(),questionDef,oracle,false,questionDef.getDataType() != QuestionDef.QTN_TYPE_REPEAT);

		sgstField = new SuggestBox(oracle,txtValue1);
		//selectFirstQuestion();

		sgstField.addEventHandler(new SuggestionHandler(){
			public void onSuggestionSelected(SuggestionEvent event){
				stopEdit(true);
			}
		});

		/*sgstField.addFocusListener(new FocusListenerAdapter(){
			public void onLostFocus(Widget sender){
				stopSelection();
			}
		});*/
	}

	public QuestionDef getValueQtnDef(){
		return valueQtnDef;
	}

	public void setValueQtnDef(QuestionDef valueQtnDef){
		this.valueQtnDef = valueQtnDef;
	}
}