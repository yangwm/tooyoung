/**
 * MemCached Java client
 * Copyright (c) 2008 Greg Whalin
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the BSD license
 *
 * This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * You should have received a copy of the BSD License along with this
 * library.
 *
 * Adds the ability for the MemCached client to be initialized
 * with a custom class loader.  This will allow for the
 * deserialization of classes that are not visible to the system
 * class loader.
 * 
 * @author Vin Chawla <vin@tivo.com> 
 * @version 2.0
 */
package cc.tooyoung.memcache.vika;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ContextObjectInputStream extends ObjectInputStream { 

	ClassLoader mLoader;
    
	public ContextObjectInputStream( InputStream in, ClassLoader loader ) throws IOException, SecurityException {
		super( in );
		mLoader = loader;
	}
	
	@SuppressWarnings("unchecked")
	protected Class resolveClass( ObjectStreamClass v ) throws IOException, ClassNotFoundException {
		if ( mLoader == null )
			return super.resolveClass( v );
		else
			return Class.forName( v.getName(), true, mLoader );
	}
}
