package org.openrosa.client.view;

import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.purc.purcforms.client.controller.IFormDesignerListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.ILocaleListChangeListener;
import org.purc.purcforms.client.model.Locale;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;

/**
 * This widget is the tool bar for the form designer.
 * 
 * @author adewinter
 *
 */
public class Toolbar extends Composite implements ILocaleListChangeListener{

	/**
	 * Tool bar images.
	 */
	public interface Images extends ClientBundle{
		ImageResource newform();
		ImageResource open();
		ImageResource save();
		ImageResource moveup();
		ImageResource movedown();
		ImageResource add();
		ImageResource addchild();
		ImageResource delete();
		ImageResource justifyleft();
		ImageResource justifyright();
		ImageResource cut();
		ImageResource copy();
		ImageResource paste();
		ImageResource alignTop();
		ImageResource alignBottom();
		ImageResource samewidth();
		ImageResource sameheight();
		ImageResource samesize();
		ImageResource undo();
		ImageResource redo();
		ImageResource refresh();
		ImageResource emptyIcon();
		ImageResource addChild();
		ImageResource addDate();
		ImageResource addNumeric();
		ImageResource addDecimal();
		ImageResource addMultSelect();
		ImageResource addSingSelect();
		ImageResource addText();
		ImageResource load();
		ImageResource menu();
		ImageResource localization();
		ImageResource showxml();
		ImageResource newformmenu();
		ImageResource validate();
		ImageResource blankbutton();
	}
	 
	/** Main widget for this tool bar. */
	private ToolBar toolBar;
	
	/** The tool bar buttons. */
	private Menu menu;
	private SplitButton menuBut;
	private Button saveBut;
	private Button saveasBut;
	private Button openBut;
	private Button xmlBut;
	private Button locBut;
	private Button addSelect;
	private Button txtBut;
	private Button intBut;
	private Button decBut;
	private Button dateBut;
	private Button multBut;
	private Button singBut,timeBut,datetimeBut,picBut,vidBut,audBut,gpsBut;
	private Button newBut;
	private SplitButton splitItem;
	private Button bcut,bcopy,bpaste;
	private Button editLocale;
	
	private IFileListener fileListener;

	private DesignTabWidget dtabWidget;
	
	/** Widget to display the list of languages or locales. */
	private ComboBox<BaseModel> cb;
	
	/** The images for the tool bar icons. */
	public final Images images;
	
	/** Listener to the tool bar button click events. */
	private IFormDesignerListener controller;
	
	//This should be localized in the same way everything else is, eventually.
	String[] buttonLabels = {"Add Question","Text Question","Integer Question","Decimal Question","Date Question",
			"MultiSelect Question","SingleSelect Question","Menu","Save","Save As...","Open File...","Localization","Export XML",
			"New Xform","Time Question","Date+Time Question","Picture Question","Video Question","Audio Question","GPS Question"};
	
	

	
	/**
	 * Creates a new instance of the tool bar.
	 * 
	 * @param images the images for tool bar icons.
	 * @param controller listener to the tool bar button click events.
	 */
	public Toolbar(Images images,IFormDesignerListener controller,IFileListener fileListener,DesignTabWidget dtab){
		this.images = images;
		this.dtabWidget = dtab;
		this.controller = controller;
		setupToolbar(fileListener);
		setupClickListeners();
//		initWidget(toolBar);
	}
	
	/**
	 * Sets up the tool bar.
	 */
	private void setupToolbar(IFileListener fileListener){
	    toolBar = new ToolBar();  
	    this.fileListener = fileListener;
	    
	    //////////////////////FIRST GROUP/////////////////////////////////
	    ButtonGroup group = new ButtonGroup(1);
	    group.setHeading("Main Menu");
	  
		menuBut = new SplitButton(buttonLabels[7]);
		menuBut.setIcon(AbstractImagePrototype.create(images.menu()));
		menuBut.setScale(ButtonScale.LARGE);
		menuBut.setIconAlign(IconAlign.TOP);
		menuBut.setArrowAlign(ButtonArrowAlign.RIGHT);
		
		menu = new Menu();
		menu.addStyleName("myMenu");
		
		newBut = new Button(buttonLabels[13]);
		newBut.setIcon(AbstractImagePrototype.create(images.newformmenu()));
		newBut.setScale(ButtonScale.LARGE);
		newBut.setIconAlign(IconAlign.LEFT);
		newBut.addStyleName("myMenuButton");
		
		saveBut = new Button(buttonLabels[8]);
		saveBut.setIcon(AbstractImagePrototype.create(images.save()));
		saveBut.setScale(ButtonScale.LARGE);
		saveBut.setIconAlign(IconAlign.LEFT);
		saveBut.addStyleName("myMenuButton");
	    
		saveasBut = new Button(buttonLabels[9]);
		saveasBut.setIcon(AbstractImagePrototype.create(images.emptyIcon()));
		saveasBut.setScale(ButtonScale.LARGE);
		saveasBut.setIconAlign(IconAlign.LEFT);
		saveasBut.addStyleName("myMenuButton");
		saveasBut.disable(); //feature not ready yet.
		
		openBut = new Button(buttonLabels[10]);
		openBut.setIcon(AbstractImagePrototype.create(images.load()));
		openBut.setScale(ButtonScale.LARGE);
		openBut.setIconAlign(IconAlign.LEFT);
		openBut.addStyleName("myMenuButton");
	    
		xmlBut = new Button(buttonLabels[12]);
		xmlBut.setIcon(AbstractImagePrototype.create(images.showxml()));
		xmlBut.setScale(ButtonScale.LARGE);
		xmlBut.setIconAlign(IconAlign.LEFT);
		xmlBut.addStyleName("myMenuButton");
	    
		locBut = new Button(buttonLabels[11]);
		locBut.setIcon(AbstractImagePrototype.create(images.localization()));
		locBut.setScale(ButtonScale.LARGE);
		locBut.setIconAlign(IconAlign.LEFT);
		locBut.addStyleName("myMenuButton");
		locBut.disable(); //feature not ready yet.
		
		menu.add(newBut);
		menu.add(openBut);
		menu.add(saveBut);
		menu.add(saveasBut);
		menu.add(xmlBut);
		menu.add(locBut);
		
		menuBut.setMenu(menu);
		group.addButton(menuBut);
		toolBar.add(group);
		///////////////////////////////////////////////////////////////////
		
		
		//////////////////////SECOND GROUP/////////////////////////////////
	    group = new ButtonGroup(2);
	    group.setHeading("Add Questions");
	    splitItem = new SplitButton(buttonLabels[0]);  
	    splitItem.setIcon(AbstractImagePrototype.create(images.add()));
	    splitItem.setScale(ButtonScale.LARGE);
	    splitItem.setIconAlign(IconAlign.TOP);
	    splitItem.setArrowAlign(ButtonArrowAlign.RIGHT);
	    menu = new Menu();
	    menu.addStyleName("myMenu");
	    txtBut = new Button(buttonLabels[1]);
	    txtBut.setIcon(AbstractImagePrototype.create(images.addText()));
	    txtBut.setScale(ButtonScale.LARGE);
	    txtBut.setIconAlign(IconAlign.LEFT);
	    txtBut.addStyleName("myMenuButton"); 
	    
	    intBut = new Button(buttonLabels[2]);
	    intBut.setIcon(AbstractImagePrototype.create(images.addNumeric()));
	    intBut.setScale(ButtonScale.LARGE);
	    intBut.setIconAlign(IconAlign.LEFT);
	    intBut.addStyleName("myMenuButton");
	    decBut = new Button(buttonLabels[3]);
	    decBut.setIcon(AbstractImagePrototype.create(images.addDecimal()));
	    decBut.setScale(ButtonScale.LARGE);
	    decBut.setIconAlign(IconAlign.LEFT);
	    decBut.addStyleName("myMenuButton");
	    dateBut = new Button(buttonLabels[4]);
	    dateBut.setIcon(AbstractImagePrototype.create(images.addDate()));
	    dateBut.setScale(ButtonScale.LARGE);
	    dateBut.setIconAlign(IconAlign.LEFT);
	    dateBut.addStyleName("myMenuButton");
	    multBut = new Button(buttonLabels[5]);
	    multBut.setIcon(AbstractImagePrototype.create(images.addMultSelect()));
	    multBut.setScale(ButtonScale.LARGE);
	    multBut.setIconAlign(IconAlign.LEFT);
	    multBut.addStyleName("myMenuButton");
	    singBut = new Button(buttonLabels[6]);
	    singBut.setIcon(AbstractImagePrototype.create(images.addSingSelect()));
	    singBut.setScale(ButtonScale.LARGE);
	    singBut.setIconAlign(IconAlign.LEFT);
	    singBut.addStyleName("myMenuButton");
	    timeBut = new Button(buttonLabels[14]);
	    timeBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    timeBut.setScale(ButtonScale.LARGE);
	    timeBut.setIconAlign(IconAlign.LEFT);
	    timeBut.addStyleName("myMenuButton");
	    datetimeBut = new Button(buttonLabels[15]);
	    datetimeBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    datetimeBut.setScale(ButtonScale.LARGE);
	    datetimeBut.setIconAlign(IconAlign.LEFT);
	    datetimeBut.addStyleName("myMenuButton");	    
	    picBut = new Button(buttonLabels[16]);
	    picBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    picBut.setScale(ButtonScale.LARGE);
	    picBut.setIconAlign(IconAlign.LEFT);
	    picBut.addStyleName("myMenuButton");	    
	    vidBut = new Button(buttonLabels[17]);
	    vidBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    vidBut.setScale(ButtonScale.LARGE);
	    vidBut.setIconAlign(IconAlign.LEFT);
	    vidBut.addStyleName("myMenuButton");	    
	    audBut = new Button(buttonLabels[18]);
	    audBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    audBut.setScale(ButtonScale.LARGE);
	    audBut.setIconAlign(IconAlign.LEFT);
	    audBut.addStyleName("myMenuButton");	    
	    gpsBut = new Button(buttonLabels[19]);
	    gpsBut.setIcon(AbstractImagePrototype.create(images.blankbutton()));
	    gpsBut.setScale(ButtonScale.LARGE);
	    gpsBut.setIconAlign(IconAlign.LEFT);
	    gpsBut.addStyleName("myMenuButton");

	    menu.add(txtBut);  
	    menu.add(intBut);  
	    menu.add(decBut);
	    menu.add(dateBut);
	    menu.add(multBut);
	    menu.add(singBut); 
	    menu.add(timeBut);
	    menu.add(datetimeBut);
	    menu.add(picBut);
	    menu.add(vidBut);
	    menu.add(audBut);
	    menu.add(gpsBut);
	    
	    splitItem.setMenu(menu);  
	    group.addButton(splitItem);
	    
	    addSelect = new Button("Add Select Option");
	    addSelect.setIcon(AbstractImagePrototype.create(images.addchild()));
	    addSelect.setIconAlign(IconAlign.TOP);
	    addSelect.setScale(ButtonScale.LARGE);
	    addSelect.disable();
	    group.addButton(addSelect);
	    group.setHeight(85);
//	    group.setAutoWidth(false);
//	    group.setWidth(500);
	    
	    toolBar.add(group);
		///////////////////////////////////////////////////////////////////
		
		
		//////////////////////THIRD GROUP/////////////////////////////////
	    group = new ButtonGroup(3);
	    group.setHeading("Clipboard");
	    
	    bcut = new Button("Cut", AbstractImagePrototype.create(images.cut()));
	    bcut.setScale(ButtonScale.LARGE);
	    bcut.setIconAlign(IconAlign.TOP);
	    bcopy = new Button("Copy", AbstractImagePrototype.create(images.copy()));
	    bcopy.setScale(ButtonScale.LARGE);
	    bcopy.setIconAlign(IconAlign.TOP);
	    bpaste = new Button("Paste", AbstractImagePrototype.create(images.paste()));
	    bpaste.setScale(ButtonScale.LARGE);
	    bpaste.setIconAlign(IconAlign.TOP);
	    bpaste.disable(); //feature not ready yet
	    group.addButton(bcut);
	    group.addButton(bcopy);
	    group.addButton(bpaste);
	    group.setHeight(95);
	    toolBar.add(group);
	    
		///////////////////////////////////////////////////////////////////
		
		
		//////////////////////FOURTH GROUP/////////////////////////////////
	    group = new ButtonGroup(2);
	    group.setHeading("Localization");
	    group.setHeight(97);
	    group.setBodyStyle("myGroupStyle");
		AbstractImagePrototype spacer = AbstractImagePrototype.create(images.emptyIcon());
		
		editLocale = new Button();
		editLocale.setText("Edit Locales");
		editLocale.setBorders(true);
		editLocale.setScale(ButtonScale.SMALL);
//		editLocale.disable(); //feature not ready yet
		group.addButton(editLocale);
		
		group.setButtonAlign(HorizontalAlignment.CENTER);
	    Text lang = new Text();
	    lang.setText("Language :");
	    group.add(lang);

		cb = new ComboBox<BaseModel>();
		cb.setDisplayField("name");
		
		populateLocales();
		cb.setValue(cb.getStore().getAt(0));
		cb.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				
				if (se.getSelection().size() > 0) {
					controller.changeLocale(new Locale((String)se.getSelectedItem().get("key"),(String)se.getSelectedItem().get("name")));
		        	Info.display("Alert","Language Selected: "+se.getSelectedItem().get("name"));
		        }
			 }});
		group.add(cb);
		group.addStyleName("localizationGroup");
		
		toolBar.add(group);    
		
		Context.addLocaleListChangeListener(this);
	}
	
	public ToolBar getToolBar(){
		return toolBar;
	}
	
	/**
	 * Setup button click event handlers.
	 */
	private void setupClickListeners(){
		
		addSelect.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.addNewChildItem();
				
			}
		});
		
		dtabWidget.addFormSelectionListener(new IFormSelectionListener() {		
				@Override
				public void onFormItemSelected(Object formItem) {
					// TODO Auto-generated method stub
					if(formItem instanceof QuestionDef &&
							((((QuestionDef)formItem).getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE) ||
							 (((QuestionDef)formItem).getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) ||
							 (((QuestionDef)formItem).getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)))
					{	
						addSelect.enable();
					}else if(formItem instanceof OptionDef){
						addSelect.enable();
					}else{
						addSelect.disable();
					}
					
				}
		});
		
		
//		addSelect.addListener(Events., listener)
		
		
		
		bcut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.cutItem();			
			}
	});
	bcopy.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.copyItem();
		}
	});
	bpaste.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.pasteItem();
		}
	});
	txtBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
			splitItem.setText(txtBut.getText());
			splitItem.setIcon(txtBut.getIcon());
			splitItem.hideMenu();
		}
		
	});
	intBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_NUMERIC);
			splitItem.setText(intBut.getText());
			splitItem.setIcon(intBut.getIcon());
			splitItem.hideMenu();

		}
	});
	decBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_DECIMAL);
			splitItem.setText(decBut.getText());
			splitItem.setIcon(decBut.getIcon());
			splitItem.hideMenu();
		}
	});
	dateBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			// TODO Auto-generated method stub
			controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE);
			splitItem.setText(dateBut.getText());
			splitItem.setIcon(dateBut.getIcon());
			splitItem.hideMenu();

		}
	});
	singBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
			splitItem.setText(singBut.getText());
			splitItem.setIcon(singBut.getIcon());
			splitItem.hideMenu();

		}
	});
	multBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_MULTIPLE);
			splitItem.setText(multBut.getText());
			splitItem.setIcon(multBut.getIcon());
			splitItem.hideMenu();
		}
	});
//    timeBut,datetimeBut,picBut,vidBut,audBut,gpsBut
	timeBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_TIME);
			splitItem.setText(timeBut.getText());
			splitItem.setIcon(timeBut.getIcon());
			splitItem.hideMenu();
		}
	});
	datetimeBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE_TIME);
			splitItem.setText(datetimeBut.getText());
			splitItem.setIcon(datetimeBut.getIcon());
			splitItem.hideMenu();
		}
	});
	picBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_IMAGE);
			splitItem.setText(picBut.getText());
			splitItem.setIcon(picBut.getIcon());
			splitItem.hideMenu();
		}
	});
	vidBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_VIDEO);
			splitItem.setText(vidBut.getText());
			splitItem.setIcon(vidBut.getIcon());
			splitItem.hideMenu();
		}
	});
	audBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_AUDIO);
			splitItem.setText(audBut.getText());
			splitItem.setIcon(audBut.getIcon());
			splitItem.hideMenu();
		}
	});
	gpsBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			controller.addNewQuestion(QuestionDef.QTN_TYPE_GPS);
			splitItem.setText(gpsBut.getText());
			splitItem.setIcon(gpsBut.getIcon());
			splitItem.hideMenu();
		}
	});
	
	splitItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			String t = splitItem.getText();
			if(t.equals(buttonLabels[0])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
			}else if(t.equals(buttonLabels[1])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
			}else if(t.equals(buttonLabels[2])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_NUMERIC);
			}else if(t.equals(buttonLabels[3])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_DECIMAL);
			}else if(t.equals(buttonLabels[4])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE);
			}else if(t.equals(buttonLabels[5])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_MULTIPLE);
			}else if(t.equals(buttonLabels[6])){
				controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
			}else{
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
			}	
		}
	});
	
	saveBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			// TODO Auto-generated method stub
			fileListener.onSave(true);
			menuBut.hideMenu();
			
		}
	});
	
	xmlBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			// TODO Auto-generated method stub
			fileListener.onSave(true);
			menuBut.hideMenu();
			
		}
	});
	
	menuBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			menuBut.showMenu();
		}
	});
	
	editLocale.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			fileListener.showItext();
		}
	});
	
	openBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			fileListener.onOpen();
		}
	});
	
	newBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			fileListener.onNew();
		}
	});
		/*
		btnNewForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.newForm();}});
		
		btnOpenForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.openForm();}});
		
		btnSaveForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.saveForm();}});
		
		btnAddNewItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewItem();}});
		
		btnAddNewChildItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewChildItem();}});
		
		btnDeleteItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.deleteSelectedItem();}});
		
		btnMoveItemUp.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.moveItemUp();}});
		
		btnMoveItemDown.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.moveItemDown();}});
		
		btnAlignLeft.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignLeft();}});
		
		btnAlignRight.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignRight();}});
		
		btnAlignTop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignTop();}});
		
		btnAlignBottom.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignBottom();}});
		
		btnCut.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.cutItem();}});
		
		btnCopy.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.copyItem();}});
		
		btnPaste.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.pasteItem();}});
		
		btnSameWidth.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameWidth();}});
		
		btnSameHeight.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameHeight();}});
		
		btnSameSize.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameSize();}});
		
		btnRefresh.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.refresh(this);}});
		
		
		btnText.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);}});
		
		btnNumeric.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_NUMERIC);}});
		
		btnDecimal.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_DECIMAL);}});
		
		btnDate.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE);}});
		
		btnSingle.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);}});
		
		btnMulti.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_MULTIPLE);}});
		*/
	}
	
	/**
	 * Populates the locale drop down with a list of locales supported by the form designer.
	 */
	public void populateLocales(){
		ListStore<BaseModel> slocales = new ListStore<BaseModel>();
		List<Locale> locales = Context.getLocales();
		
		if(locales == null)
			return;
		
		for(Locale locale : locales){
			BaseModel bm = new BaseModel();
			bm.set("key", locale.getKey());
			bm.set("name",locale.getName());
			slocales.add(bm);
		}
		
		cb.setStore(slocales);
	}
	
	
	private int getCurrentLocaleIndex(){
		Locale currentLocale = Context.getLocale();
		
		List<Locale> locales = Context.getLocales();
		assert(locales != null);
		
		for(int index = 0; index < locales.size(); index++){
			Locale locale = locales.get(index);
			if(locale.getKey().equals(currentLocale.getKey()))
				return index;
		}
		
		return 0;
	}
	
	public void onLocaleListChanged(){
		populateLocales();
	}
}
