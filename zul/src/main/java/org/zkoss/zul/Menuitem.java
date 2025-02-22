/* Menuitem.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:23     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.ext.Uploads;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 * 
 * <p>Default {@link #getZclass}: z-menuitem. (since 3.5.0)
 * @author tomyeh
 */
public class Menuitem extends LabelImageElement implements org.zkoss.zk.ui.ext.Disable,
		org.zkoss.zk.ui.ext.Uploadable {
	private AuxInfo _auxinf;

	static {
		addClientEvent(Menuitem.class, Events.ON_CHECK, CE_IMPORTANT);
		addClientEvent(Menuitem.class, Events.ON_UPLOAD, 0);
	}

	public Menuitem() {
	}

	public Menuitem(String label) {
		super(label);
	}

	public Menuitem(String label, String src) {
		super(label, src);
	}

	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isCheckmark() {
		return _auxinf != null && _auxinf.checkmark;
	}

	/** Sets whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Note the checkbox can be checked only if {@link #isAutocheck()} is true
	 * @since 3.5.0
	 */
	public void setCheckmark(boolean checkmark) {
		if ((_auxinf != null && _auxinf.checkmark) != checkmark) {
			initAuxInfo().checkmark = checkmark;
			smartUpdate("checkmark", isCheckmark());
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-menuitem" : _zclass;
	}

	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if ((_auxinf != null && _auxinf.disabled) != disabled) {
			initAuxInfo().disabled = disabled;
			smartUpdate("disabled", isDisabled());
		}
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _auxinf != null && _auxinf.disabled;
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 * @since 5.0.7
	 */
	public String getAutodisable() {
		return _auxinf != null ? _auxinf.autodisable : null;
	}

	/** Sets a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 *
	 * <p>To represent the menuitem itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;menuitem id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;menuitem id="ok" autodisable="ok,cancel"/></code>
	 * that will disable
	 * both the ok and cancel menuitem when an user clicks it.
	 *
	 * <p>The menuitem being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a menuitem is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>&lt;menuitem id="ok" autodisable="+self,+cancel"/></code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 *</code></pre>
	 *
	 * <p>Default: null.
	 * @since 5.0.7
	 */
	public void setAutodisable(String autodisable) {
		if (!Objects.equals(_auxinf != null ? _auxinf.autodisable : null, autodisable)) {
			initAuxInfo().autodisable = autodisable;
			smartUpdate("autodisable", getAutodisable());
		}
	}

	/** Returns the value.
	 * <p>Default: "".
	 */
	public String getValue() {
		return _auxinf != null ? _auxinf.value : "";
	}

	/** Sets the value.
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!Objects.equals(_auxinf != null ? _auxinf.value : "", value)) {
			initAuxInfo().value = value;
			smartUpdate("value", getValue());
		}
	}

	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	public boolean isChecked() {
		return _auxinf != null && _auxinf.checked;
	}

	/** Sets whether it is checked.
	 * <p> This only applies when {@link #isCheckmark()} = true. (since 3.5.0)
	 */
	public void setChecked(boolean checked) {
		if ((_auxinf != null && _auxinf.checked) != checked) {
			initAuxInfo().checked = checked;
			if (_auxinf.checked)
				_auxinf.checkmark = true;
			smartUpdate("checked", isChecked());
		}
	}

	/** Returns whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p>Default: false.
	 */
	public boolean isAutocheck() {
		return _auxinf != null && _auxinf.autocheck;
	}

	/** Sets whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p> This only applies when {@link #isCheckmark()} = true. (since 3.5.0)
	 */
	public void setAutocheck(boolean autocheck) {
		if ((_auxinf != null && _auxinf.autocheck) != autocheck) {
			initAuxInfo().autocheck = autocheck;
			smartUpdate("autocheck", isAutocheck());
		}
	}

	/** Returns the href.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick handler.
	 */
	public String getHref() {
		return _auxinf != null ? _auxinf.href : null;
	}

	/** Sets the href.
	 */
	public void setHref(String href) throws WrongValueException {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.href : null, href)) {
			initAuxInfo().href = href;
			smartUpdate("href", new EncodedHref()); //Bug 1850895
		}
	}

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 */
	public String getTarget() {
		return _auxinf != null ? _auxinf.target : null;
	}

	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.target : null, target)) {
			initAuxInfo().target = target;
			smartUpdate("target", getTarget());
		}
	}

	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 */
	public boolean isTopmost() {
		return !(getParent() instanceof Menupopup);
	}

	public String getUpload() {
		return _auxinf != null ? _auxinf.upload : null;
	}

	public void setUpload(String upload) {
		if (upload != null && (upload.length() == 0 || "false".equals(upload)))
			upload = null;
		if (!Objects.equals(upload, _auxinf != null ? _auxinf.upload : null)) {
			initAuxInfo().upload = upload;
			Uploads.parseUpload(this, upload);
			smartUpdate("upload", Uploads.getRealUpload(this, getUpload()));
		}
	}

	//Bug #2871082
	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _auxinf != null && _auxinf.href != null && dt != null ? dt.getExecution().encodeURL(_auxinf.href) : null;
		//if desktop is null, it doesn't belong to any execution
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Menupopup) && !(parent instanceof Menubar))
			throw new UiException("Unsupported parent for menuitem: " + parent);
		super.beforeParentChanged(parent);
	}

	//Cloneable//
	public Object clone() {
		final Menuitem clone = (Menuitem) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "checkmark", isCheckmark());
		render(renderer, "disabled", isDisabled());
		render(renderer, "checked", isChecked());
		render(renderer, "autocheck", isAutocheck());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = getEncodedHref()); //Bug #2871082
		render(renderer, "target", getTarget());
		render(renderer, "upload", Uploads.getRealUpload(this, getUpload()));
		render(renderer, "value", getValue());

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	protected void renderCrawlable(String label) throws java.io.IOException {
		//does nothing since generated in renderProperties
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onCheck.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			initAuxInfo().checked = evt.isChecked();
			if (_auxinf.checked)
				_auxinf.checkmark = true;
			Events.postEvent(evt);
		} else if (Events.ON_UPLOAD.equals(cmd)) {
			Events.postEvent(UploadEvent.getUploadEvent(cmd, this, request));
		} else
			super.service(request, everError);
	}

	//Bug #2871082
	private class EncodedHref implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}

	protected void updateByClient(String name, Object value) {
		if ("disabled".equals(name))
			setDisabled(value instanceof Boolean ? ((Boolean) value).booleanValue()
					: "true".equals(Objects.toString(value)));
		else
			super.updateByClient(name, value);
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private String value = "";
		private String href, target;
		private String autodisable;
		protected String upload;
		private boolean disabled;
		private boolean autocheck, checked;
		private boolean checkmark;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
