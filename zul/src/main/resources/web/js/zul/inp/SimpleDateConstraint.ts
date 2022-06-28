/* SimpleDateConstraint.ts

	Purpose:

	Description:

	History:
		Thu Sep 10 10:09:47     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A simple date constraint.
 * @disable(zkgwt)
 */
export class SimpleDateConstraint extends zul.inp.SimpleConstraint {
	public readonly format = 'yyyyMMdd';
	private _wgt: zul.db.Datebox;
	private _localizedSymbols?: zk.LocalizedSymbols;
	private _beg?: DateImpl;
	private _end?: DateImpl;

	/** Constructor.
	 * @param Object a
	 * It can be String or number, the number or name of flag,
	 * such as "no positive", 0x0001.
	 * @param zk.Widget the datebox
	 * @since 5.0.8
	 */
	public constructor(a: unknown, wgt: zul.db.Datebox) {
		super(a); // FIXME: super was originally called at the end of the constructor
		this._wgt = wgt;
		this._localizedSymbols = wgt._localizedSymbols;
		this.lazyInit_(); // call it after all member fields are ready.
	}

	protected override parseConstraint_(constraint: string): void {
		var len = this.format.length + 1,
			arr = this._cstArr,
			wgt = this._wgt,
			tz = wgt && wgt.getTimeZone && wgt.getTimeZone();
		if (constraint.startsWith('between')) {
			var j = constraint.indexOf('and', 7);
			if (j < 0 && zk.debugJS)
				zk.error('Unknown constraint: ' + constraint);
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(7, j), this.format, null, null, null, tz);
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(j + 3, j + 3 + len), this.format, null, null, null, tz);
			if (this._beg!.getTime() > this._end!.getTime()) {
				var d = this._beg;
				this._beg = this._end;
				this._end = d;
			}

			this._beg!.setHours(0, 0, 0, 0);
			this._end!.setHours(0, 0, 0, 0);
			arr[arr.length] = 'between';
		} else if (constraint.startsWith('before') && !constraint.startsWith('before_')) {
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(6, 6 + len), this.format, null, null, null, tz);
			this._end!.setHours(0, 0, 0, 0);
			arr[arr.length] = 'before';
		} else if (constraint.startsWith('after') && !constraint.startsWith('after_')) {
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(5, 5 + len), this.format, null, null, null, tz);
			this._beg!.setHours(0, 0, 0, 0);
			arr[arr.length] = 'after';
		}
		return super.parseConstraint_(constraint);
	}

	public override validate(wgt: zk.Widget, val: unknown): zul.inp.SimpleConstraintErrorMessages | string | undefined {
		var result = super.validate(wgt, val);
		if (val instanceof window.DateImpl) {
			var msg = this._errmsg,
				v = Dates.newInstance([val.getFullYear(), val.getMonth(), val.getDate()], val.getTimeZone());
			if (this._beg != null && this._beg.getTime() > v.getTime())
				return msg['between'] || msg['after'] || this.outOfRangeValue();
			if (this._end != null && this._end.getTime() < v.getTime())
				return msg['between'] || msg['before'] || this.outOfRangeValue();
		}
		return result;
	}

	/** Returns the message about out of range value
	 * @return String
	 */
	public outOfRangeValue(): string {
		var format = this._wgt._format,
			separator = msgzul.OUT_OF_RANGE_SEPARATOR ? ' ' + msgzul.OUT_OF_RANGE_SEPARATOR + ' ' : ' ~ ';
		return msgzul.OUT_OF_RANGE + ': ' + (this._beg != null ? this._end != null ?
				new zk.fmt.Calendar(null, this._localizedSymbols).formatDate(this._beg, format) + separator
					+ new zk.fmt.Calendar().formatDate(this._end, format) :
					'>= ' + new zk.fmt.Calendar().formatDate(this._beg, format) :
					'<= ' + new zk.fmt.Calendar().formatDate(this._end!, format));
	}
}
zul.inp.SimpleDateConstraint = zk.regClass(SimpleDateConstraint);