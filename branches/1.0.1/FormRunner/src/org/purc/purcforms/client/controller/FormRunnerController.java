package org.purc.purcforms.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.FormRunnerContext;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.FormRunnerWidget;
import org.purc.purcforms.client.widget.RuntimeWidgetWrapper;
import org.purc.purcforms.client.xforms.XformParser;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;


/**
 * Controls the interactions between the views for the form runner
 * and also handles server side communication on behalf of this view.
 * 
 * @author daniel
 *
 */
public class FormRunnerController implements SubmitListener, LoadListener {

	private FormRunnerWidget formRunner;
	private String xformXml;
	private String layoutXml;
	private String javaScriptSrc;
	private String css;
	private int formId;
	private int entityId;

	public FormRunnerController(FormRunnerWidget formRunner){
		this.formRunner = formRunner;
	}

	public void loadForm(int frmId, int entyId){
		this.formId = frmId;
		this.entityId = entyId;

		FormUtil.dlg.setText(LocaleText.get("openingForm"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				//"http://127.0.0.1:8080/openmrs/moduleServlet/xforms/xformDownload?target=xformentry&formId="+formId+"&patientId="+patientId+"&contentType=xml&uname=Guyzb&pw=daniel123"
				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getEntityFormDefDownloadUrlSuffix();
				url += FormUtil.getFormIdName()+"="+formId;
				url += "&" + FormUtil.getEntityIdName() + "="+entityId;
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,URL.encode(url));

				try{
					builder.sendRequest(null, new RequestCallback(){
						public void onResponseReceived(Request request, Response response){
							
							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}
								
							String xml = response.getText();
							if(xml == null || xml.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}

							xformXml = null; layoutXml = null; javaScriptSrc = null; css = null;

							int pos = xml.indexOf(PurcConstants.PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR);
							int pos2 = xml.indexOf(PurcConstants.PURCFORMS_FORMDEF_JAVASCRIPT_SRC_SEPARATOR);
							int pos3 = xml.indexOf(PurcConstants.PURCFORMS_FORMDEF_CSS_SEPARATOR);
							if(pos > 0){
								xformXml = xml.substring(0,pos);
								
								int endIndex = pos2;
								if(endIndex == -1) endIndex = pos3;
								if(endIndex == -1) endIndex = xml.length();
								
								layoutXml = xml.substring(pos+PurcConstants.PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR.length(), endIndex);

								if(pos2 > 0)
									javaScriptSrc = xml.substring(pos2+PurcConstants.PURCFORMS_FORMDEF_JAVASCRIPT_SRC_SEPARATOR.length(), pos3 > 0 ? pos3 : xml.length());

								if(pos3 > 0)
									css = xml.substring(pos3+PurcConstants.PURCFORMS_FORMDEF_CSS_SEPARATOR.length(), xml.length());
								
								openForm();
								//FormUtil.dlg.hide(); //open form above will close it
							}
							else{
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noFormLayout"));
							}
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}

	public void openForm() {
		FormUtil.dlg.setText(LocaleText.get("openingForm"));
		FormUtil.dlg.center();

		final LoadListener listener  = this;
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					List<RuntimeWidgetWrapper> externalSourceWidgets = new ArrayList<RuntimeWidgetWrapper>();
					FormDef formDef = XformParser.fromXform2FormDef(xformXml);
					formRunner.loadForm(formDef, layoutXml, javaScriptSrc, css, externalSourceWidgets, listener);

					FormUtil.dlg.hide();	
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	public void onCancel(){
		String url = FormUtil.getHostPageBaseURL();
		url += FormUtil.getAfterCancelUrlSuffix();

		if(FormUtil.appendEntityIdAfterCancel()){
			url += FormUtil.getEntityIdName();
			url += "=" + entityId;
		}

		//Prevent close confirmation dialog box.
		FormRunnerContext.setWarnOnClose(false);
		
		Window.Location.replace(url);
	}

	public void onSubmit(String xml){

		FormUtil.dlg.setText(LocaleText.get("submitting"));
		FormUtil.dlg.center();

		final String submitXml = xml;

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				//"http://127.0.0.1:8080/openmrs/module/xforms/xformDataUpload.form"
				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getFormDataUploadUrlSuffix();
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,URL.encode(url));

				try{
					builder.sendRequest(submitXml, new RequestCallback(){
						public void onResponseReceived(Request request, Response response){
							FormUtil.dlg.hide();
							
							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							if(response.getStatusCode() == Response.SC_OK){
								if(FormUtil.showSubmitSuccessMsg())
									Window.alert(LocaleText.get("formSubmitSuccess"));

								String url = FormUtil.getHostPageBaseURL();
								String suffix = FormUtil.getAfterSubmitUrlSuffix();
								if (suffix != null) {
									url += suffix;
	
									if(FormUtil.appendEntityIdAfterSubmit()){
										url += FormUtil.getEntityIdName();
										if(entityId > 0)
											url += "=" + entityId;
										else if(entityId == 0 && response.getText().trim().length() > 0)
											url += "=" + response.getText();
									}
	
									//Prevent close confirmation dialog box.
									FormRunnerContext.setWarnOnClose(false);
									
									if (FormUtil.getRecordPosition() == null) {
										Window.Location.replace(url); //"http://127.0.0.1:8080/openmrs/patientDashboard.form?patientId=13"
									}
								}
							}
							else
								FormUtil.displayReponseError(response);
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	public void onDelete(){

		FormUtil.dlg.setText(LocaleText.get("submitting"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getFormDataDeleteUrlSuffix();
				url += entityId;
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,URL.encode(url));

				try{
					builder.sendRequest(entityId + "", new RequestCallback(){
						public void onResponseReceived(Request request, Response response){
							FormUtil.dlg.hide();
							
							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							if(response.getStatusCode() == Response.SC_OK){
								if(FormUtil.showSubmitSuccessMsg())
									Window.alert(LocaleText.get("formDeleteSuccess"));

								String url = FormUtil.getHostPageBaseURL();
								url += FormUtil.getAfterSubmitUrlSuffix();

								//Prevent close confirmation dialog box.
								FormRunnerContext.setWarnOnClose(false);
								
								if (FormUtil.getRecordPosition() == null) {
									Window.Location.replace(url);
								}
							}
							else
								FormUtil.displayReponseError(response);
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
     * @see org.purc.purcforms.client.controller.LoadListener#onLoad(java.lang.String)
     */
    @Override
    public void onLoad(String entyId) {
		this.entityId = Integer.parseInt(entyId);

		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				//"http://127.0.0.1:8080/openmrs/moduleServlet/xforms/xformDownload?target=xformentry&formId="+formId+"&patientId="+patientId+"&contentType=xml&uname=Guyzb&pw=daniel123"
				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getEntityFormDefDownloadUrlSuffix();
				url += FormUtil.getFormIdName()+"="+formId;
				url += "&" + FormUtil.getEntityIdName() + "="+entityId;
				url += "&dataOnly=true";
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,URL.encode(url));

				try{
					builder.sendRequest(null, new RequestCallback(){
						public void onResponseReceived(Request request, Response response){
							
							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}
								
							xformXml = response.getText();
							if(xformXml == null || xformXml.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}
								
							loadForm();
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
    }
    
    public void loadForm() {
		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					FormDef formDef = XformParser.fromXform2FormDef(xformXml);
					formRunner.loadForm(formDef);
					FormUtil.dlg.hide();	
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
}
