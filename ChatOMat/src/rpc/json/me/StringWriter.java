/*
 * Copyright 2006 Sun Microsystems, Inc.
 */

package rpc.json.me;

import java.io.Writer;

/**
 * A simple StringBuffer-based implementation of StringWriter
 */
public class StringWriter extends Writer {

    final private StringBuffer buf;

    public StringWriter() {
        super();
		this.buf = new StringBuffer( );
    }

    public StringWriter(final int initialSize) {
        super();
		this.buf = new StringBuffer( initialSize );
    }

	public void write( final char[ ] cbuf, final int off, final int len )
	{
		this.buf.append( cbuf, off, len );
    }

	public void write( final String str )
	{
		this.buf.append( str );
    }

	public void write( final String str, final int off, final int len )
	{
		this.buf.append( str.substring( off, len ) );
    }

	public void flush( )
	{
    }

	public void close( )
	{
    }
}
