/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.lib.explorer.model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.api.LibraryHandle;
import org.eclipse.birt.report.model.api.core.IResourceChangeListener;

/**
 * @deprecated use ResourceEntity to represent library in resource folder.
 */

public class LibDirectoryNodeModel
{

	FileFilter filter = new FileFilter( ) {

		public boolean accept( File pathname )
		{
			return pathname.isDirectory( )
					|| pathname.isFile( )
					&& (pathname.getPath( )
							.toLowerCase( )
							.endsWith( ".rptlibrary" )
					|| pathname.getPath( )
					.toLowerCase( )
					.endsWith( ".css" )); //$NON-NLS-1$
		}

	};

	List childrenList = new ArrayList( );

	private String directoryPath;
	private String text;

	private IResourceChangeListener resourceListener;

	public LibDirectoryNodeModel( String path )
	{
		directoryPath = path;
		text = new File( path ).getName( );
	}

	public Object[] getChildren( )
	{
		if ( childrenList.size( ) > 0 )
		{
			return childrenList.toArray( );
		}

		File file = new File( directoryPath );

		if ( !file.exists( ) )
		{
			return new Object[]{
				Messages.getString( "LibraryExplorerProvider.FolderNotExist" ) //$NON-NLS-1$
			};
		}
		if ( file.isDirectory( ) )
		{
			File[] children = file.listFiles( filter );

			for ( int i = 0; i < children.length; i++ )
			{
				if ( children[i].isDirectory( ) )
				{
					childrenList.add( new LibDirectoryNodeModel( ( (File) children[i] ).getAbsolutePath( ) ) );
				}
				else
				{
					LibraryHandle library;
					try
					{
						library = SessionHandleAdapter.getInstance( )
								.getSessionHandle( )
								.openLibrary( ( (File) children[i] ).getAbsolutePath( ) );

						if ( resourceListener != null )
						{
							library.addResourceChangeListener( resourceListener );
						}

						childrenList.add( library );
					}
					catch ( DesignFileException e )
					{

					}
				}
			}

			return childrenList.toArray( );
		}

		return null;
	}

	public void dispose( )
	{
		for ( Iterator it = childrenList.iterator( ); it.hasNext( ); )
		{
			Object obj = it.next( );

			if ( obj instanceof LibDirectoryNodeModel )
			{
				( (LibDirectoryNodeModel) obj ).dispose( );
			}

			if ( obj instanceof LibraryHandle )
			{
				if ( resourceListener != null )
				{
					( (LibraryHandle) obj ).removeResourceChangeListener( resourceListener );
				}
				( (LibraryHandle) obj ).close( );
			}
		}
	}

	public void setResourceListener( IResourceChangeListener listener )
	{
		resourceListener = listener;
	}

	public String getText( )
	{
		return text;
	}
}
