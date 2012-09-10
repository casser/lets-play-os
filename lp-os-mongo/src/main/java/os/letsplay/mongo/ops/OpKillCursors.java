/*
 * Copyright (c) 2010 Claudio Alberto Andreoni.
 *	
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 	
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package os.letsplay.mongo.ops;

import os.letsplay.bson.BsonByteArray;
import os.letsplay.mongo.Messages;
	
public class OpKillCursors extends BaseMsg {
	
	public static final int OP_KILL_CURSORS = 2007;
	
	public Long[] cursors;
	
	public OpKillCursors(Long[] cursors ) {
		this.cursors = cursors;
	}
	
	public Boolean waitForResponse() {
		return false;
	}
	
	@Override
	public int getCode() {
		return Messages.OP_KILL_CURSORS.getCode();
	}
	
	@Override
	public Boolean isSafe() {
		return false;
	}
	
	@Override
	protected void writeBody(BsonByteArray bin) {
		bin.writeInt( 0 ); // ZERO
		bin.writeInt( cursors.length );
		for( Long cursor : cursors) {
			bin.writeLong(cursor);
		}
	}
	
	@Override
	protected void readBody(BsonByteArray bin) {
		bin.readInt();// ZERO
		int length = bin.readInt();
		cursors = new Long[length];
		for(int i=0;i<length;i++){
			cursors[i]=bin.readLong();
		}
	}
	
}
