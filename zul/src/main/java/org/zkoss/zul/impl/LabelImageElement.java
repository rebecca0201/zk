/* LabelImageElement.java

	Purpose:

	Description:

	History:
		Tue Jul 12 12:09:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.awt.image.RenderedImage;
import java.util.HashMap;

import org.zkoss.image.Image;
import org.zkoss.image.Images;
import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.sys.ObjectPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;

/**
 * A XUL element with a label ({@link #getLabel})
 * and an image ({@link #getImage}).
 * <p>
 * [Since 6.0.0]
 * <p>To turn on the preload image function for this component, you have to specify the component's
 * attribute map with key "org.zkoss.zul.image.preload" to true. That is, for
 * example, if in a zul file, you shall specify &lt;custom-attributes> of the
 * component like this:</p>
 *
 * <pre><code> &lt;button ...&gt;
 *     &lt;custom-attributes org.zkoss.zul.image.preload=&quot;true&quot;/&gt;
 * &lt;/button&gt;
 * </code></pre>
 *
 * Or specify it onto the root component.
 * For example,
 * <pre><code> &lt;window ...&gt;
 *     &lt;custom-attributes org.zkoss.zul.image.preload=&quot;true&quot;/&gt;
 *     &lt;button .../&gt;
 * &lt;/window&gt;
 * </code></pre>
 * [Since 6.5.2]
 * <p>Preload image function is also configurable from zk.xml by setting library properties.
 * For example,
 * <pre><code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.image.preload&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code></pre>
 *
 * @author tomyeh
 */
public abstract class LabelImageElement extends LabelElement {
	private AuxInfo _auxinf;

	protected LabelImageElement() {
	}

	/** @since 5.0.0
	 */
	protected LabelImageElement(String label, String image) {
		setLabel(label);
		setImage(image);
	}

	/** @since 5.0.0
	 */
	protected LabelImageElement(String label) {
		setLabel(label);
	}

	/**
	 * Sets the icon font, if iconSclasses is set, iconSclass will be ignored, iconSclasses will take precedence over iconSclass
	 * @param iconSclass a CSS class name for the icon font
	 * @since 7.0.0
	 */
	public void setIconSclass(String iconSclass) {
		if (iconSclass != null && iconSclass.isEmpty())
			iconSclass = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.iconSclass : null, iconSclass)) {
			initAuxInfo().iconSclass = iconSclass;
			smartUpdate("iconSclass", iconSclass);
		}
	}

	/**
	 * Sets multiple icons font, if iconSclasses is set, iconSclass will be ignored, iconSclasses will take precedence over iconSclass
	 * @param iconSclasses a CSS class name String array for the icon font
	 * @since 10.0.0
	 */
	public void setIconSclasses(String[] iconSclasses) {
		if (!Objects.equals(_auxinf != null ? _auxinf.iconSclasses : null, iconSclasses)) {
			initAuxInfo().iconSclasses = iconSclasses;
			smartUpdate("iconSclasses", iconSclasses);
		}
	}

	/**
	 * Returns the icon font
	 * @since 7.0.0
	 */
	public String getIconSclass() {
		return _auxinf != null && _auxinf.iconSclass instanceof String ? _auxinf.iconSclass : null;
	}

	/**
	 * Returns the icon font String array
	 * @since 10.0.0
	 */
	public String[] getIconSclasses() {
		return _auxinf != null && _auxinf.iconSclasses instanceof String[] ? _auxinf.iconSclasses : null;
	}

	/**
	 * Sets the iconTooltip, if iconTooltips is set, iconTooltip will be ignored, iconTooltips will take precedence over iconTooltip
	 * @param iconTooltip a content String for iconTooltip
	 * @since 10.0.0
	 */
	public void setIconTooltip(String iconTooltip) {
		if (iconTooltip != null && iconTooltip.isEmpty())
			iconTooltip = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.iconTooltips : null, iconTooltip)) {
			initAuxInfo().iconTooltip = iconTooltip;
			smartUpdate("iconTooltip", iconTooltip);
		}
	}

	/**
	 * Sets multiple iconTooltips, if iconTooltips is set, iconTooltip will be ignored, iconTooltips will take precedence over iconTooltip
	 * @param iconTooltips a content String array for iconTooltip
	 * @since 10.0.0
	 */
	public void setIconTooltips(String[] iconTooltips) {
		if (!Objects.equals(_auxinf != null ? _auxinf.iconTooltips : null, iconTooltips)) {
			initAuxInfo().iconTooltips = iconTooltips;
			smartUpdate("iconTooltips", iconTooltips);
		}
	}

	/**
	 * Returns the iconTooltip content
	 * @since 10.0.0
	 */
	public String getIconTooltip() {
		return _auxinf != null && _auxinf.iconTooltip instanceof String ? _auxinf.iconTooltip : null;
	}

	/**
	 * Returns the iconTooltip content String array
	 * @since 10.0.0
	 */
	public String[] getIconTooltips() {
		return _auxinf != null && _auxinf.iconTooltips instanceof String[] ? _auxinf.iconTooltips : null;
	}

	/** Returns the image URI.
	 * <p>Default: null.
	 */
	public String getImage() {
		return _auxinf != null && _auxinf.image instanceof String ? (String) _auxinf.image : null;
	}

	/** Sets the image URI.
	 * <p>Calling this method implies setImageContent(null).
	 * In other words, the last invocation of {@link #setImage} overrides
	 * the previous {@link #setImageContent}, if any.
	 * The image would hide if src == null </p>
	 * @see #setImageContent(Image)
	 * @see #setImageContent(RenderedImage)
	 */
	public void setImage(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.image : null, src)) {
			initAuxInfo().image = src;
			smartUpdate("image", new EncodedImageURL());
		}
	}

	/** Sets the content directly.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setImage(null).
	 * In other words, the last invocation of {@link #setImageContent} overrides
	 * the previous {@link #setImage}, if any.
	 * @param image the image to display.
	 * @see #setImage
	 */
	public void setImageContent(Image image) {
		if ((_auxinf != null ? _auxinf.image : null) != image) {
			initAuxInfo().image = image;
			if (image != null)
				_auxinf.imgver++; //enforce browser to reload image
			smartUpdate("image", new EncodedImageURL());
		}
	}

	/** Sets the content directly with the rendered image.
	 * It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setImageContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 *
	 * @since 3.0.7
	 */
	public void setImageContent(RenderedImage image) {
		try {
			setImageContent(Images.encode("a.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	/** Returns the image content
	 * set by {@link #setImageContent(Image)}
	 * or {@link #setImageContent(RenderedImage)}.
	 *
	 * <p>Note: it won't load the content specified by {@link #setImage}.
	 * Actually, it returns null if {@link #setImage} was called.
	 */
	public Image getImageContent() {
		return _auxinf != null && _auxinf.image instanceof Image ? (Image) _auxinf.image : null;
	}

	/** Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 * @since 3.5.0
	 */
	public String getHoverImage() {
		return _auxinf != null && _auxinf.hoverimg instanceof String ? (String) _auxinf.hoverimg : null;
	}

	/** Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Calling this method implies setHoverImageContent(null).
	 * In other words, the last invocation of {@link #setHoverImage} overrides
	 * the previous {@link #setHoverImageContent}, if any.
	 * @since 3.5.0
	 */
	public void setHoverImage(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.hoverimg : null, src)) {
			initAuxInfo().hoverimg = src;
			smartUpdate("hoverImage", new EncodedHoverURL());
		}
	}

	/** Returns the content of the hover image
	 * set by {@link #setHoverImageContent(Image)}
	 * or {@link #setHoverImageContent(RenderedImage)}.
	 *
	 * <p>Note: it won't load the content specified by {@link #setImage}.
	 * Actually, it returns null if {@link #setImage} was called.
	 * @since 5.0.8
	 */
	public Image getHoverImageContent() {
		return _auxinf != null && _auxinf.hoverimg instanceof Image ? (Image) _auxinf.hoverimg : null;
	}

	/** Sets the content of the hover image directly.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setHoverImage(null).
	 * In other words, the last invocation of {@link #setHoverImageContent} overrides
	 * the previous {@link #setHoverImage}, if any.
	 * @param image the image to display.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(Image image) {
		if ((_auxinf != null ? _auxinf.hoverimg : null) != image) {
			initAuxInfo().hoverimg = image;
			if (image != null)
				_auxinf.hoverimgver++; //enforce browser to reload image
			smartUpdate("hoverImage", new EncodedHoverURL());
		}
	}

	/** Sets the content of the hover image directly with the rendered image.
	 * The hover image is used when the mouse is moving over this component.
	 *
	 * <p>It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setHoverImageContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(RenderedImage image) {
		try {
			setHoverImageContent(Images.encode("hover.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	/** Returns whether the image is available.
	 * In other words, it return true if {@link #setImage} or
	 * {@link #setImageContent(org.zkoss.image.Image)} is called with non-null.
	 */
	public boolean isImageAssigned() {
		return _auxinf != null && _auxinf.image != null;
	}

	/** Returns the encoded URL for the image ({@link #getImage}
	 * or {@link #getImageContent}), or null if no image.
	 * <p>Used only for component development; not by application developers.
	 * <p>Note: this method can be invoked only if execution is not null.
	 */
	private String getEncodedImageURL() {
		if (_auxinf != null && _auxinf.image instanceof Image) {
			final Image image = (Image) _auxinf.image;
			return Utils.getDynamicMediaURI(this, //already encoded
					_auxinf.imgver, "c/" + image.getName(), image.getFormat());
		}

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null && _auxinf != null && _auxinf.image != null
				? dt.getExecution().encodeURL((String) _auxinf.image) : null;
	}

	/** Returns the encoded URL for the hover image or null if not
	 * available.
	 */
	private String getEncodedHoverURL() {
		if (_auxinf != null && _auxinf.hoverimg instanceof Image) {
			final Image image = (Image) _auxinf.hoverimg;
			return Utils.getDynamicMediaURI(this, _auxinf.hoverimgver, "h/" + image.getName(), image.getFormat());
		}

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null && _auxinf != null && _auxinf.hoverimg != null
				? dt.getExecution().encodeURL((String) _auxinf.hoverimg) : null;
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		//ZK-1638: preload image can also be defined in zk.xml by library property
		render(renderer, "_preloadImage", Utils.testAttribute(this, "org.zkoss.zul.image.preload", false, true));
		render(renderer, "image", getEncodedImageURL());
		render(renderer, "hoverImage", getEncodedHoverURL());
		render(renderer, "iconSclass", getIconSclass());
		render(renderer, "iconTooltip", getIconTooltip());
		render(renderer, "iconSclasses", getIconSclasses());
		render(renderer, "iconTooltips", getIconTooltips());
	}

	//-- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends LabelElement.ExtraCtrl implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			if (pathInfo != null) {
				int j = pathInfo.indexOf('/', 1);
				if (j >= 0) {
					int k = pathInfo.indexOf('/', ++j);
					if (k == j + 1 && pathInfo.charAt(j) == 'h')
						return getHoverImageContent();
				}
			}
			return getImageContent();
		}
	}

	private class EncodedImageURL implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedImageURL();
		}
	}

	private class EncodedHoverURL implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedHoverURL();
		}
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(5);

	static {
		_properties.put("iconSclass", new StringPropertyAccess() {
			public void setValue(Component cmp, String iconSclass) {
				((LabelImageElement) cmp).setIconSclass(iconSclass);
			}

			public String getValue(Component cmp) {
				return ((LabelImageElement) cmp).getIconSclass();
			}
		});
		_properties.put("iconSclasses", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object iconSclasses) {
				((LabelImageElement) cmp).setIconSclasses((String[]) iconSclasses);
			}

			public Object getValue(Component cmp) {
				return ((LabelImageElement) cmp).getIconSclasses();
			}
		});
		_properties.put("iconTooltip", new StringPropertyAccess() {
			public void setValue(Component cmp, String iconTooltip) {
				((LabelImageElement) cmp).setIconTooltip(iconTooltip);
			}

			public String getValue(Component cmp) {
				return ((LabelImageElement) cmp).getIconTooltip();
			}
		});
		_properties.put("iconTooltips", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object iconTooltip) {
				((LabelImageElement) cmp).setIconTooltips((String[]) iconTooltip);
			}

			public Object getValue(Component cmp) {
				return ((LabelImageElement) cmp).getIconTooltips();
			}
		});
		_properties.put("image", new StringPropertyAccess() {
			public void setValue(Component cmp, String image) {
				((LabelImageElement) cmp).setImage(image);
			}

			public String getValue(Component cmp) {
				return ((LabelImageElement) cmp).getImage();
			}
		});
		_properties.put("imageContent", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object image) {
				if (image instanceof Image)
					((LabelImageElement) cmp).setImageContent((Image) image);
				else if (image instanceof RenderedImage)
					((LabelImageElement) cmp).setImageContent((RenderedImage) image);
			}

			public Class getType() {
				return Image.class;
			}

			public Image getValue(Component cmp) {
				return ((LabelImageElement) cmp).getImageContent();
			}
		});
		_properties.put("hoverImage", new StringPropertyAccess() {
			public void setValue(Component cmp, String hoverImage) {
				((LabelImageElement) cmp).setHoverImage(hoverImage);
			}

			public String getValue(Component cmp) {
				return ((LabelImageElement) cmp).getHoverImage();
			}
		});
		_properties.put("hoverImageContent", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object image) {
				if (image instanceof Image)
					((LabelImageElement) cmp).setHoverImageContent((Image) image);
				else if (image instanceof RenderedImage)
					((LabelImageElement) cmp).setHoverImageContent((RenderedImage) image);
			}

			public Class getType() {
				return Image.class;
			}

			public Image getValue(Component cmp) {
				return ((LabelImageElement) cmp).getHoverImageContent();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	//Cloneable//
	public Object clone() {
		final LabelImageElement clone = (LabelImageElement) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	/** Merge multiple members to minimize the memory use.
	 * @since 5.0.8
	 */
	private static class AuxInfo implements java.io.Serializable, Cloneable {
		/** The image; either String or Image. */
		private Object image;
		/** The hover image; either String or Image. */
		private Object hoverimg;
		/** Count the version of {@link #image}. */
		private byte imgver;
		/** Count the version of {@link #hoverimg}. */
		private byte hoverimgver;

		private String iconSclass;

		private String[] iconSclasses;

		private String iconTooltip;

		private String[] iconTooltips;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}