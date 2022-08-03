/* Script.ts

	Purpose:

	Description:

	History:
		Thu Dec 11 15:39:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag.
 * @import zul.wgt.Label
 */
@zk.WrapClass('zul.utl.Script')
export class Script extends zk.Widget {
	_src?: string;
    _content?: string;
	_charset?: string;
	_srcrun?: boolean;
	// eslint-disable-next-line @typescript-eslint/ban-types
	_fn?: Function;
	packages?: string;

    /** Returns the content of the script element.
     * By content we mean the JavaScript codes that will be enclosed
     * by the HTML SCRIPT element.
     *
     * <p>Default: null.
     * @return String
     */
    getContent(): string | undefined {
        return this._content;
    }

    /** Sets the content of the script element.
     * By content we mean the JavaScript codes that will be enclosed
     * by the HTML SCRIPT element.
     * @param String content
     */
    setContent(content: string, opts?: Record<string, boolean>): this {
        const o = this._content;
        this._content = content;

        if (o !== content || (opts && opts.force)) {
			if (content) {
				this._fn = typeof content == 'function' ? content : new Function(content);
				if (this.desktop) //check parent since no this.$n()
					this._exec();
			} else
				delete this._fn;
		}

        return this;
    }

    /** Returns the URI of the source that contains the script codes.
     * <p>Default: null.
     * @return String
     */
    getSrc(): string | undefined {
        return this._src;
    }

    /** Sets the URI of the source that contains the script codes.
     *
     * <p>You either add the script codes directly with the {@link Label}
     * children, or
     * set the URI to load the script codes with {@link #setSrc}.
     * But, not both.
     *
     * @param String src the URI of the source that contains the script codes
     */
    setSrc(src: string, opts?: Record<string, boolean>): this {
        const o = this._src;
        this._src = src;

        if (o !== src || (opts && opts.force)) {
			if (src) {
				this._srcrun = false;
				if (this.desktop)
					this._exec();
			}
		}

        return this;
    }

    /** Returns the character enconding of the source.
     * It is used with {@link #getSrc}.
     *
     * <p>Default: null.
     * @return String
     */
    getCharset(): string | undefined {
        return this._charset;
    }

    /** Sets the character encoding of the source.
     * It is used with {@link #setSrc}.
     * @param String charset
     */
    setCharset(charset: string): this {
        this._charset = charset;
        return this;
    }

    _exec(): void {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		delete this.packages; //only once
		zk.load(pkgs);

		if (zk.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	}

    _exec0(): void {
		var wgt = this, fn = this._fn;
		if (fn) {
			delete this._fn; //run only once
			zk.afterMount(function () {fn!.call(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			var e = document.createElement('script');
			e.id = this.uuid;
			e.type = 'text/javascript';
			e.charset = this._charset ?? 'UTF-8';
			e.src = this._src;
			var n = this.$n(),
				// eslint-disable-next-line zk/noNull
				nextSib: ChildNode | null = null;
			if (n) {
				nextSib = n.nextSibling;
				jq(n).remove();
			}

			if (nextSib) //use jq here would load this script twice in IE8/9
				nextSib.parentNode!.insertBefore(e, nextSib);
			else
				document.body.appendChild(e);
		}
		//update node
		this._node = jq(this.uuid, zk)[0];
		this._nodeSolved = true;
	}

	override ignoreFlexSize_(attr: string): boolean {
		// ZK-2248: ignore widget dimension in vflex/hflex calculation
		return true;
	}

    //super//
	override redraw(out: string[], skipper?: zk.Skipper): void {
		// empty
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._visible = false; //Bug ZK-1516: no DOM element widget should always return false.
		this._exec();
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this._node).remove(); // ZK-4043: the script DOM is appended in body, a manual remove is needed.
		super.unbind_(skipper, after, keepRod);
	}
}