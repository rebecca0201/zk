/* HtmlBasedComponent.java

	Purpose:

	Description:

	History:
		Sat Dec 31 12:30:18     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.MoveEvent;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zk.ui.event.SwipeEvent;
import org.zkoss.zk.ui.event.ZIndexEvent;
import org.zkoss.zk.ui.ext.DragControl;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.impl.Utils;
import org.zkoss.zk.ui.sys.IntPropertyAccess;
import org.zkoss.zk.ui.sys.IntegerPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;

/**
 * A skeletal implementation for HTML based components.
 * It simplifies to implement methods common to HTML based components.
 *
 * <p>Events:<br/>
 *  onClick, onDoubleClick, onRightClick, onDrop,
 *  onMouseOver, onMouseOut, onOK, onCancel, onCtrlKey and onSwipe.<br/>
 * 
 * <p>It supports
 * <ul>
 * <li>{@link #getSclass} and {@link #getStyle}.</li>
 * <li>{@link #getWidth}, {@link #getHeight}, {@link #getLeft},
 * {@link #getTop}, {@link #getZIndex}</li>
 * <li>{@link #focus}</li>
 * </ul>
 *
 * @author tomyeh
 * @since 5.0.0 supports onOK event.
 * @since 5.0.0 supports onCancel event.
 * @since 5.0.0 supports onCtrlKey event.
 * @since 6.5.0 supports onSwipe event for tablet.
 */
public abstract class HtmlBasedComponent extends AbstractComponent {
	/** The ZK CSS class. */
	protected String _zclass;
	/** The prolog content that shall be generated before real content. */
	private String _prolog;
	/** AuxInfo: use a class (rather than multiple member) to save footprint */
	private AuxInfo _auxinf;

	static {
		addClientEvent(HtmlBasedComponent.class, Events.ON_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_DOUBLE_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_RIGHT_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_OK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_CANCEL, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_CTRL_KEY, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_DROP, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_MOUSE_OVER, 0); //not to use CE_DUPLICATE_IGNORE since there is an order issue
		addClientEvent(HtmlBasedComponent.class, Events.ON_MOUSE_OUT, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_SWIPE, CE_DUPLICATE_IGNORE);
		addClientEvent(HtmlBasedComponent.class, Events.ON_AFTER_SIZE, CE_DUPLICATE_IGNORE);
	}

	protected HtmlBasedComponent() {
	}

	/** Returns the left position.
	 */
	public String getLeft() {
		return _auxinf != null ? _auxinf.left : null;
	}

	/** Sets the left position.
	 * <p>If you want to specify <code>right</code>, use {@link #setStyle} instead.
	 * For example, <code>setStyle("right: 0px");</code>
	 * @param left the left position. Remember to specify <code>px</code>, <code>pt</code> or <code>%</code>.
	 */
	public void setLeft(String left) {
		if (!Objects.equals(_auxinf != null ? _auxinf.left : null, left)) {
			initAuxInfo().left = left;
			smartUpdate("left", left);
		}
	}

	/** Returns the top position.
	 */
	public String getTop() {
		return _auxinf != null ? _auxinf.top : null;
	}

	/** Sets the top position.
	 * <p>If you want to specify <code>bottom</code>, use {@link #setStyle} instead.
	 * For example, <code>setStyle("bottom: 0px");</code>
	 * @param top the top position. Remember to specify <code>px</code>, <code>pt</code> or <code>%</code>.
	 */
	public void setTop(String top) {
		if (!Objects.equals(_auxinf != null ? _auxinf.top : null, top)) {
			initAuxInfo().top = top;
			smartUpdate("top", top);
		}
	}

	/** Returns the Z index.
	 * <p>Default: -1 (means system default;
	 */
	public int getZIndex() {
		return _auxinf != null ? _auxinf.zIndex : -1;
	}

	/** Sets the Z index.
	 */
	public void setZIndex(int zIndex) {
		if (zIndex < -1)
			zIndex = -1;
		if ((_auxinf != null ? _auxinf.zIndex : -1) != zIndex) {
			initAuxInfo().zIndex = zIndex;
			if (zIndex < 0)
				smartUpdate("zIndex", (Object) null);
			else
				smartUpdate("zIndex", zIndex);
		}
	}

	/** Returns the Z index.
	 * It is the same as {@link #getZIndex}.
	 * @since 3.5.2
	 */
	public int getZindex() {
		return getZIndex();
	}

	/** Sets the Z index.
	 * It is the same as {@link #setZIndex}.
	 * @since 3.5.2
	 */
	public void setZindex(int zIndex) {
		setZIndex(zIndex);
	}

	/** Returns the height. If null, the best fit is used.
	 * <p>Default: null.
	 */
	public String getHeight() {
		return _auxinf != null ? _auxinf.height : null;
	}

	/** Sets the height. If null, the best fit is used.
	 */
	public void setHeight(String height) {
		if (getVflex() != null && !(height == null || getVflex().equals("min"))) {
			throw new UiException("Not allowed to set vflex and height at the same time except vflex=\"min\"");
		}
		setHeight0(height);
	}

	/**
	 * Internal Use Only.
	 */
	protected void setHeight0(String height) {
		if (height != null && height.length() == 0)
			height = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.height : null, height)) {
			initAuxInfo().height = height;
			smartUpdate("height", height);
		}
	}

	/** Returns the width. If null, the best fit is used.
	 * <p>Default: null.
	 */
	public String getWidth() {
		return _auxinf != null ? _auxinf.width : null;
	}

	/** Sets the width. If null, the best fit is used.
	 * @see #setWidthDirectly
	 * @see #disableClientUpdate
	 */
	public void setWidth(String width) {
		if (getHflex() != null && !(width == null || getHflex().equals("min"))) {
			throw new UiException("Not allowed to set hflex and width at the same time except hflex=\"min\"");
		}
		setWidth0(width);
	}

	/**
	 * Internal Use Only.
	 */
	protected void setWidth0(String width) {
		if (width != null && width.length() == 0)
			width = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.width : null, width)) {
			initAuxInfo().width = width;
			smartUpdate("width", width);
		}
	}

	/** Sets the width directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setWidthDirectly(String width) {
		initAuxInfo().width = width;
	}

	/** Sets the height directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setHeightDirectly(String height) {
		initAuxInfo().height = height;
	}

	/** Sets the left directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setLeftDirectly(String left) {
		initAuxInfo().left = left;
	}

	/** Sets the top directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setTopDirectly(String top) {
		initAuxInfo().top = top;
	}

	/** Sets the z-index directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setZIndexDirectly(int zIndex) {
		initAuxInfo().zIndex = zIndex;
	}

	/** Sets the hflex directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.6
	 */
	protected void setHflexDirectly(String hflex) {
		initAuxInfo().hflex = hflex;
	}

	/** Sets the vflex directly without sending back the result
	 * (smart update) to the client
	 * @since 8.0.1
	 */
	protected void setVflexDirectly(String vflex) {
		initAuxInfo().vflex = vflex;
	}

	/** Returns the text as the tooltip.
	 * <p>Default: null.
	 */
	public String getTooltiptext() {
		return _auxinf != null ? _auxinf.tooltiptext : null;
	}

	/** Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext) {
		if (tooltiptext != null && tooltiptext.length() == 0)
			tooltiptext = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.tooltiptext : null, tooltiptext)) {
			initAuxInfo().tooltiptext = tooltiptext;
			smartUpdate("tooltiptext", tooltiptext);
		}
	}

	/**
	  * Returns the ZK Cascading Style class for this component.
	  * It usually depends on the implementation of the mold ({@link #getMold()}).
	  *
	  * <p>Default: null (the default value depends on element).
	  *
	  * <p>{@link #setZclass}) will completely replace the default style
	  * of a component. In other words, the default style of a component
	  * is associated with the default value of {@link #getZclass}.
	  * Once it is changed, the default style won't be applied at all.
	  * If you want to perform small adjustments, use {@link #setSclass}
	  * instead.
	  *
	  * @since 3.5.1
	  * @see #getSclass
	  */
	public String getZclass() {
		return _zclass;
	}

	/**
	 * Sets the ZK Cascading Style class for this component.
	 * It usually depends on the implementation of the mold ({@link #getMold}).
	 *
	 * @param zclass the style class used to apply to the whole widget.
	 * @since 3.5.0
	 * @see #setSclass
	 * @see #getZclass
	 */
	public void setZclass(String zclass) {
		if (zclass != null && zclass.length() == 0)
			zclass = null;
		if (!Objects.equals(_zclass, zclass)) {
			_zclass = zclass;
			smartUpdate("zclass", _zclass);
		}
	}

	/** Returns the CSS class(es).
	 *
	 * <p>Default: null.
	 *
	 * <p>The default styles of ZK components doesn't depend on the value
	 * of {@link #getSclass}. Rather, {@link #setSclass} is provided to
	 * perform small adjustment, e.g., only changing the font size.
	 * In other words, the default style is still applied if you change
	 * the value of {@link #getSclass}, unless you override it.
	 * To replace the default style completely, use
	 * {@link #setZclass} instead.
	 *
	 * @see #getZclass
	 */
	public String getSclass() {
		return _auxinf != null ? _auxinf.sclass : null;
	}

	/** Sets the CSS class(es).
	 *
	 * @see #setZclass
	 */
	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0)
			sclass = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.sclass : null, sclass)) {
			initAuxInfo().sclass = sclass;
			smartUpdate("sclass", sclass);
		}
	}

	/** Sets the CSS class. This method is a bit confused with Java's class,
	 * but we provide it for XUL compatibility.
	 * The same as {@link #setSclass}.
	 */
	public void setClass(String sclass) {
		setSclass(sclass);
	}

	/** Add the CSS class(es) to a component's sclass property if the component doesn't have this cssClass.
	 *
	 * @param cssClass One or more space-separated CSS className to be added to the component's sclass property.
	 * @since 8.6.1
	 */
	public void addSclass(String cssClass) {
		if (!Strings.isEmpty(cssClass)) {
			String sclass = getSclass();
			if (sclass == null) {
				setSclass(cssClass);
			} else {
				String[] input = cssClass.split(" ");
				StringBuilder stringBuilder = new StringBuilder(sclass);
				String cur = " " + sclass + " ";
				for (String inputClass : input) {
					if (!cur.contains(" " + inputClass + " ")) {
						stringBuilder.append(" ").append(inputClass);
					}
				}
				setSclass(stringBuilder.toString());
			}
		}
	}

	/** Remove the CSS class(es) from a component's sclass property if the component has this cssClass.
	 *  If the component doesn't have the CSS class sclass, do nothing.
	 *  If no class names are specified in the parameter, all classes will be removed.
	 *
	 * @param cssClass One or more space-separated CSS className to be removed from a component's sclass property.
	 * @since 8.6.1
	 */
	public void removeSclass(String cssClass) {
		String sclass = getSclass();
		if (sclass != null) {
			String[] input = cssClass.split(" ");
			String cur = " " + sclass + " ";
			for (String inputClass : input) {
				String curInput = " " + inputClass + " ";
				if (cur.contains(curInput)) {
					cur = cur.replace(curInput, " ");
				}
			}
			setSclass(cur.trim());
		}
	}

	/** Remove all CSS classes from a component's sclass property.
	 *
	 * @since 8.6.1
	 */
	public void removeSclass() {
		setSclass("");
	}

	/** Returns the CSS style.
	 * <p>Default: null.
	 */
	public String getStyle() {
		return _auxinf != null ? _auxinf.style : null;
	}

	/** Sets the CSS style.
	 */
	public void setStyle(String style) {
		if (style != null && style.length() == 0)
			style = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.style : null, style)) {
			initAuxInfo().style = style;
			smartUpdate("style", style);
		}
	}

	/** Sets "true" or "false" to denote whether a component is draggable,
	 * or an identifier of a draggable type of objects.
	 *
	 * <p>The simplest way to make a component draggable is to set
	 * this attribute to true. To disable it, set this to false.
	 *
	 * <p>If there are several types of draggable objects, you could
	 * assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty.
	 *
	 * @param draggable "false", "" or null to denote non-draggable; "true" for draggable
	 * with anonymous identifier; others for an identifier of draggable.<br/>
	 * Notice that if the parent is {@link DragControl} and draggable is null,
	 * then it means draggable.
	 */
	public void setDraggable(String draggable) {
		if (draggable != null && draggable.length() == 0) //empty means false (but not null)
			draggable = "false";

		if (!Objects.equals(_auxinf != null ? _auxinf.draggable : null, draggable)) {
			initAuxInfo().draggable = draggable;
			smartUpdate("draggable", draggable); //getDraggable is final
		}
	}

	/** Returns the identifier of a draggable type of objects, or "false"
	 * if not draggable (never null nor empty).
	 */
	public String getDraggable() {
		return _auxinf != null && _auxinf.draggable != null ? _auxinf.draggable
				: getParent() instanceof DragControl ? "true" : "false";
	}

	/** Sets "true" or "false" to denote whether a component is droppable,
	 * or a list of identifiers of draggable types of objects that could
	 * be dropped to this component.
	 *
	 * <p>The simplest way to make a component droppable is to set
	 * this attribute to true. To disable it, set this to false.
	 *
	 * <p>If there are several types of draggable objects and this
	 * component accepts only some of them, you could assign a list of
	 * identifiers that this component accepts, separated by comma.
	 * For example, if this component accepts dg1 and dg2, then
	 * assign "dg1, dg2" to this attribute.
	 *
	 * @param droppable "false", null or "" to denote not-droppable;
	 * "true" for accepting any draggable types; a list of identifiers,
	 * separated by comma for identifiers of draggables this component
	 * accept (to be dropped in).
	 */
	public void setDroppable(String droppable) {
		if (droppable != null && (droppable.length() == 0 || "false".equals(droppable)))
			droppable = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.droppable : null, droppable)) {
			initAuxInfo().droppable = droppable;
			smartUpdate("droppable", droppable);
		}
	}

	/** Returns the identifier, or a list of identifiers of a droppable type
	 * of objects, or "false"
	 * if not droppable (never null nor empty).
	 */
	public String getDroppable() {
		return _auxinf != null && _auxinf.droppable != null ? _auxinf.droppable : "false";
	}

	/** Sets focus to this element. If an element does not accept focus,
	 * this method has no effect.
	 */
	public void focus() {
		response(new AuFocus(this));
	}

	/** Sets focus to this element.
	 * It is same as {@link #focus}, but used to allow ZUML to set focus
	 * to particular component.
	 *
	 * <pre><code>&lt;textbox focus="true"/&gt;</code></pre>
	 *
	 * @param focus whether to set focus. If false, this method has no effect.
	 * @since 3.0.5
	 */
	public void setFocus(boolean focus) {
		if (focus)
			focus();
	}

	/**
	 * Sets vertical flexibility hint of this component. 
	 * <p>Number flex indicates how 
	 * this component's container distributes remaining empty space among its 
	 * children vertically. Flexible component grow and shrink to fit their 
	 * given space. Flexible components with larger flex values will be made 
	 * larger than components with lower flex values, at the ratio determined by 
	 * all flexible components. The actual flex value is not relevant unless 
	 * there are other flexible components within the same container. Once the 
	 * default sizes of components in a container are calculated, the remaining 
	 * space in the container is divided among the flexible components, 
	 * according to their flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0,
	 * or "false" has the same effect as leaving the flex attribute out entirely. 
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p> 
	 * 
	 * @param flex the vertical flex hint.
	 * @since 5.0.0
	 * @see #setHflex
	 * @see #getVflex 
	 */
	public void setVflex(String flex) {
		if (getHeight() != null && !(flex == null || "min".equals(flex))) {
			throw new UiException("Not allowed to set vflex and height at the same time except vflex=\"min\"");
		}
		setVflex0(flex);
	}

	/**
	 * Internal Use Only.
	 */
	protected void setVflex0(String flex) {
		if (flex != null && flex.length() == 0)
			flex = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.vflex : null, flex)) {
			initAuxInfo().vflex = flex;
			smartUpdate("vflex", flex);
		}
	}

	/**
	 * Return vertical flex hint of this component.
	 * <p>Default: null
	 * @return vertical flex hint of this component.
	 * @since 5.0.0
	 * @see #setVflex 
	 */
	public String getVflex() {
		return _auxinf != null ? _auxinf.vflex : null;
	}

	/**
	 * Sets horizontal flex hint of this component.
	 * <p>Number flex indicates how 
	 * this component's container distributes remaining empty space among its 
	 * children horizontally. Flexible component grow and shrink to fit their 
	 * given space. Flexible components with larger flex values will be made 
	 * larger than components with lower flex values, at the ratio determined by 
	 * all flexible components. The actual flex value is not relevant unless 
	 * there are other flexible components within the same container. Once the 
	 * default sizes of components in a container are calculated, the remaining 
	 * space in the container is divided among the flexible components, 
	 * according to their flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0, or "false" has the same 
	 * effect as leaving the flex attribute out entirely. 
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p> 
	 * @param flex horizontal flex hint of this component.
	 * @since 5.0.0 
	 * @see #setVflex
	 * @see #getHflex 
	 */
	public void setHflex(String flex) {
		if (getWidth() != null && !(flex == null || "min".equals(flex))) {
			throw new UiException("Not allowed to set hflex and width at the same time except hflex=\"min\"");
		}
		setHflex0(flex);
	}

	/**
	 * Internal Use Only.
	 */
	protected void setHflex0(String flex) {
		if (flex != null && flex.length() == 0)
			flex = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.hflex : null, flex)) {
			initAuxInfo().hflex = flex;
			smartUpdate("hflex", flex);
		}
	}

	/**
	 * Returns horizontal flex hint of this component.
	 * <p>Default: null
	 * @return horizontal flex hint of this component.
	 * @since 5.0.0
	 * @see #setHflex 
	 */
	public String getHflex() {
		return _auxinf != null ? _auxinf.hflex : null;
	}

	/** Returns the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 * @since 5.0.2
	 */
	public int getRenderdefer() {
		return _auxinf != null ? _auxinf.renderdefer : -1;
	}

	/** Sets the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 *
	 * <p>This method is useful if you have a sophisticated page that takes
	 * long to render at a slow client. You can specify a non-negative value
	 * as the render-defer delay such that the other part of the UI can appear
	 * earlier. The styling of the render-deferred widget is controlled by
	 * a CSS class called <code>z-renderdefer</code>.
	 *
	 * <p>Notice that it has no effect if the component has been rendered
	 * at the client.
	 * @param ms time to wait in milliseconds before rendering.
	 * Notice: 0 also implies deferring the rendering (just right after
	 * all others are rendered).
	 * @since 5.0.2
	 */
	public void setRenderdefer(int ms) {
		initAuxInfo().renderdefer = ms;
	}

	/** Returns the client-side action (CSA).
	 * <p>Default: null (no CSA at all)
	 * @since 5.0.6
	 * @deprecated As of release 10.0.0, using {@link #setClientAction(String)} instead.
	 */
	public String getAction() {
		return getClientAction();
	}
	/** Returns the client-side action (CSA).
	 * <p>Default: null (no CSA at all)
	 * @since 10.0.0
	 */
	public String getClientAction() {
		return _auxinf != null ? _auxinf.action : null;
	}

	/** Sets the client-side action (CSA).
	 * <p>Default: null (no CSA at all)
	 * <p>The format: <br>
	 * <code>action1: action-effect1; action2: action-effect2</code><br/>
	 *
	 * <p>Currently, only two actions are <code>show</code> and <code>hide</code>.
	 * They are called when the widget is becoming visible (show) and invisible (hide).
	 * <p>The action effect (<code>action-effect1</code>) is the name of a method
	 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
	 * such as
	 * <code>show: slideDown; hide: slideUp</code>
	 * <p>You could specify the effects as follows:<br/>
	 * <code>show: slideDown({duration:1000})</code>
	 * <p>Security Tips: the action is not encoded and it is OK to embed JavaScript,
	 * so, if you want to allow users to specify the action, you have to encode it.
	 * <p>Note for developers upgraded from ZK 3:
	 * CSA's format is different and limited.
	 * In additions, it is part of {@link HtmlBasedComponent}.
	 * @since 5.0.6
	 * @deprecated As of release 10.0.0, using {@link #setClientAction(String)} instead.
	 */
	public void setAction(String action) {
		setClientAction(action);
	}

	/** Sets the client-side action (CSA).
	 * <p>Default: null (no CSA at all)
	 * <p>The format: <br>
	 * <code>action1: action-effect1; action2: action-effect2</code><br/>
	 *
	 * <p>Currently, only two actions are <code>show</code> and <code>hide</code>.
	 * They are called when the widget is becoming visible (show) and invisible (hide).
	 * <p>The action effect (<code>action-effect1</code>) is the name of a method
	 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
	 * such as
	 * <code>show: slideDown; hide: slideUp</code>
	 * <p>You could specify the effects as follows:<br/>
	 * <code>show: slideDown({duration:1000})</code>
	 * <p>Security Tips: the action is not encoded and it is OK to embed JavaScript,
	 * so, if you want to allow users to specify the action, you have to encode it.
	 * <p>Note for developers upgraded from ZK 3:
	 * CSA's format is different and limited.
	 * In additions, it is part of {@link HtmlBasedComponent}.
	 * @since 10.0.0
	 */
	public void setClientAction(String action) {
		if (action != null && action.length() == 0)
			action = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.action : null, action)) {
			initAuxInfo().action = action;
			smartUpdate("action", action);
		}
	}

	/** Returns the tab order of this component.
	 * <p>Default: 0
	 */
	public int getTabindex() {
		return (_auxinf != null && _auxinf.tabindex != null) ? _auxinf.tabindex : 0;
	}

	/**
	 * Returns null if not set.
	 * @return the tab order of this component
	 * @since 8.0.2
	 */
	public Integer getTabindexInteger() {
		return (_auxinf != null && _auxinf.tabindex != null) ? _auxinf.tabindex : null;
	}

	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		setTabindex((Integer) tabindex);
	}

	/**
	 * Sets the tab order of this component. Removes the tabindex attribute if it's set to null.
	 * @param tabindex
	 */
	public void setTabindex(Integer tabindex) {
		if ((_auxinf != null ? _auxinf.tabindex : null) != tabindex) {
			initAuxInfo().tabindex = tabindex;
			smartUpdate("tabindex", tabindex);
		}
	}

	/**
	 * Get whether css flex is enabled or not
	 * @return css flex is enabled
	 */
	public boolean evalCSSFlex() {
		return Utils.testAttribute(this, "org.zkoss.zul.css.flex", true, true);
	}

	//-- rendering --//
	/** Renders the content of this component, excluding the enclosing
	 * tags and children.
	 *
	 * <p>See also
	 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component Development/Server-side/Property_Rendering">ZK Client-side Reference: Property Rendering</a>
	 * @since 5.0.0
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (_auxinf != null) {
			render(renderer, "width", _auxinf.width);
			render(renderer, "height", _auxinf.height);
			render(renderer, "left", _auxinf.left);
			render(renderer, "top", _auxinf.top);
			render(renderer, "vflex", _auxinf.vflex);
			render(renderer, "hflex", _auxinf.hflex);
			render(renderer, "sclass", _auxinf.sclass);
			render(renderer, "style", _auxinf.style);
			render(renderer, "tooltiptext", _auxinf.tooltiptext);

			if (_auxinf.zIndex >= 0)
				renderer.render("zIndex", _auxinf.zIndex);
			if (_auxinf.renderdefer >= 0)
				renderer.render("renderdefer", _auxinf.renderdefer);
			if (_auxinf.tabindex != null)
				renderer.render("tabindex", _auxinf.tabindex);

			final String draggable = _auxinf.draggable;
			if (draggable != null && (getParent() instanceof DragControl || !draggable.equals("false")))
				render(renderer, "draggable", draggable);

			render(renderer, "droppable", _auxinf.droppable);
			render(renderer, "action", _auxinf.action);
		}

		render(renderer, "zclass", _zclass);
		render(renderer, "prolog", _prolog);
		if (!evalCSSFlex())
			renderer.render("cssflex", false);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(20);

	static {
		_properties.put("prolog", new StringPropertyAccess() {
			public void setValue(Component cmp, String prolog) {
				((HtmlBasedComponent) cmp)._prolog = prolog;
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp)._prolog;
			}
		});
		_properties.put("width", new StringPropertyAccess() {
			public void setValue(Component cmp, String width) {
				((HtmlBasedComponent) cmp).setWidth(width);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getWidth();
			}
		});
		_properties.put("height", new StringPropertyAccess() {
			public void setValue(Component cmp, String height) {
				((HtmlBasedComponent) cmp).setHeight(height);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getHeight();
			}
		});
		_properties.put("sclass", new StringPropertyAccess() {
			public void setValue(Component cmp, String sclass) {
				((HtmlBasedComponent) cmp).setSclass(sclass);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getSclass();
			}
		});
		_properties.put("zclass", new StringPropertyAccess() {
			public void setValue(Component cmp, String zclass) {
				((HtmlBasedComponent) cmp).setZclass(zclass);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getZclass();
			}
		});
		_properties.put("style", new StringPropertyAccess() {
			public void setValue(Component cmp, String style) {
				((HtmlBasedComponent) cmp).setStyle(style);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getStyle();
			}
		});
		_properties.put("left", new StringPropertyAccess() {
			public void setValue(Component cmp, String left) {
				((HtmlBasedComponent) cmp).setLeft(left);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getLeft();
			}
		});
		_properties.put("top", new StringPropertyAccess() {
			public void setValue(Component cmp, String top) {
				((HtmlBasedComponent) cmp).setTop(top);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getTop();
			}
		});
		_properties.put("draggable", new StringPropertyAccess() {
			public void setValue(Component cmp, String draggable) {
				((HtmlBasedComponent) cmp).setDraggable(draggable);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getDraggable();
			}
		});
		_properties.put("droppable", new StringPropertyAccess() {
			public void setValue(Component cmp, String droppable) {
				((HtmlBasedComponent) cmp).setDroppable(droppable);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getDroppable();
			}
		});
		_properties.put("tooltiptext", new StringPropertyAccess() {
			public void setValue(Component cmp, String tooltiptext) {
				((HtmlBasedComponent) cmp).setTooltiptext(tooltiptext);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getTooltiptext();
			}
		});
		_properties.put("zindex", new IntPropertyAccess() {
			public void setValue(Component cmp, Integer zindex) {
				((HtmlBasedComponent) cmp).setZindex(zindex);
			}

			public Integer getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getZIndex();
			}
		});
		_properties.put("renderdefer", new IntPropertyAccess() {
			public void setValue(Component cmp, Integer renderdefer) {
				((HtmlBasedComponent) cmp).setRenderdefer(renderdefer);
			}

			public Integer getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getRenderdefer();
			}
		});
		_properties.put("action", new StringPropertyAccess() {
			public void setValue(Component cmp, String action) {
				((HtmlBasedComponent) cmp).setAction(action);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getAction();
			}
		});
		_properties.put("hflex", new StringPropertyAccess() {
			public void setValue(Component cmp, String hflex) {
				((HtmlBasedComponent) cmp).setHflex(hflex);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getHflex();
			}
		});
		_properties.put("vflex", new StringPropertyAccess() {
			public void setValue(Component cmp, String vflex) {
				((HtmlBasedComponent) cmp).setVflex(vflex);
			}

			public String getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getVflex();
			}
		});
		_properties.put("tabindex", new IntegerPropertyAccess() {
			public void setValue(Component cmp, Integer tabindex) {
				((HtmlBasedComponent) cmp).setTabindex(tabindex);
			}

			public Integer getValue(Component cmp) {
				return ((HtmlBasedComponent) cmp).getTabindexInteger();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	/** Processes an AU request.
	 *
	 * <p>Default: it handles onClick, onDoubleClick, onRightClick
	 * onMove, onSize, onZIndex.
	 * @since 5.0.0
	 */
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CLICK) || cmd.equals(Events.ON_DOUBLE_CLICK) || cmd.equals(Events.ON_RIGHT_CLICK)
				|| cmd.equals(Events.ON_MOUSE_OVER) || cmd.equals(Events.ON_MOUSE_OUT)) {
			Events.postEvent(MouseEvent.getMouseEvent(request));
		} else if (cmd.equals(Events.ON_OK) || cmd.equals(Events.ON_CANCEL) || cmd.equals(Events.ON_CTRL_KEY)) {
			Events.postEvent(KeyEvent.getKeyEvent(request));
		} else if (cmd.equals(Events.ON_MOVE)) {
			MoveEvent evt = MoveEvent.getMoveEvent(request);
			setLeftDirectly(evt.getLeft());
			setTopDirectly(evt.getTop());
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_SIZE)) {
			SizeEvent evt = SizeEvent.getSizeEvent(request);
			setWidthDirectly(evt.getWidth());
			setHeightDirectly(evt.getHeight());
			setHflexDirectly(null);
			setVflexDirectly(null);
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_AFTER_SIZE)) {
			AfterSizeEvent evt = AfterSizeEvent.getAfterSizeEvent(request);
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_Z_INDEX)) {
			ZIndexEvent evt = ZIndexEvent.getZIndexEvent(request);
			setZIndexDirectly(evt.getZIndex());
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_DROP)) {
			DropEvent evt = DropEvent.getDropEvent(request);
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_SWIPE)) {
			SwipeEvent evt = SwipeEvent.getSwipeEvent(request);
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	/** Returns the client control for this component.
	 * It is used only by component developers.
	 *
	 * <p>Default: creates an instance of {@link HtmlBasedComponent.ExtraCtrl}.
	 */
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 *
	 * <p>If a component requires more client controls, it is suggested to
	 * override {@link #getExtraCtrl} to return an instance that extends from
	 * this class.
	 */
	protected class ExtraCtrl implements PrologAllowed {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prolog = prolog;
		}
	}

	//Cloneable//
	public Object clone() {
		final HtmlBasedComponent clone = (HtmlBasedComponent) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	private final AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	/** Merge multiple members into an single object (and create on demand)
	 * to minimize the footprint
	 * @since 5.0.4
	 */
	private static class AuxInfo implements java.io.Serializable, Cloneable {
		/** The width. */
		private String width;
		/** The height. */
		private String height;
		private String left;
		private String top;
		/** The vertical flex */
		private String vflex;
		/** The horizontal flex */
		private String hflex;
		/** The CSS class. */
		private String sclass;
		/** The CSS style. */
		private String style;
		/** The tooltip text. */
		private String tooltiptext;
		/** The draggable. */
		private String draggable;
		/** The droppable. */
		private String droppable;
		private String action;
		private int zIndex = -1;
		private int renderdefer = -1;
		private Integer tabindex;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
