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

package org.eclipse.birt.report.designer.ui.lib.explorer.action;

import java.util.Iterator;

import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.internal.ui.util.ExceptionHandler;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.ui.ReportPlugin;
import org.eclipse.birt.report.designer.ui.lib.explorer.LibraryExplorerTreeViewPage;
import org.eclipse.birt.report.designer.ui.lib.wizards.UseCssInThemeDialog;
import org.eclipse.birt.report.model.api.IncludedCssStyleSheetHandle;
import org.eclipse.birt.report.model.api.LibraryHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.ThemeHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.css.CssStyleSheetHandle;
import org.eclipse.birt.report.model.api.util.URIUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;


/**
 * 
 */

public class UseCssInThemeAction extends Action
{
	private LibraryExplorerTreeViewPage viewer;

	private static final String ACTION_TEXT = Messages
			.getString( "UseCssInThemeAction.Text" ); //$NON-NLS-1$

	public UseCssInThemeAction( LibraryExplorerTreeViewPage page )
	{
		super( ACTION_TEXT );
		this.viewer = page;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	public boolean isEnabled( )
	{
		Object obj = SessionHandleAdapter.getInstance( ).getReportDesignHandle( );
		LibraryHandle moduleHandle;
		if((obj == null) || (! (obj instanceof LibraryHandle)))
		{
			return false;
		}
		moduleHandle = (LibraryHandle)obj;
		CssStyleSheetHandle cssHandle = getSelectedCssStyleHandle();
		if(cssHandle == null)
		{			
			return false;
		}
		SlotHandle slotHandle = moduleHandle.getThemes( );
		for(Iterator iter = slotHandle.iterator( ); iter.hasNext( );)
		{
			ThemeHandle theme = (ThemeHandle)iter.next( );
			if(theme.canAddCssStyleSheet( cssHandle ))
			{
				return true;
			}
		}
		return false;
		
	}
	
	private CssStyleSheetHandle getSelectedCssStyleHandle( )
	{
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection( );
		if ( selection != null )
		{
			if ( selection.getFirstElement( ) instanceof CssStyleSheetHandle )
			{
				return (CssStyleSheetHandle) selection.getFirstElement( );
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run( )
	{
		CssStyleSheetHandle cssHandle = getSelectedCssStyleHandle();
		UseCssInThemeDialog dialog = new UseCssInThemeDialog();
//		String relativeFileName = URIUtil.getRelativePath( ReportPlugin.getDefault( )
//				.getResourceFolder( ),
//				cssHandle.getFileName( ) );
		String relativeFileName = cssHandle.getFileName( );
		dialog.setFileName( relativeFileName );
	
		if(dialog.open( ) == Dialog.OK)
		{
			ThemeHandle themeHandle = dialog.getTheme();
//			issue here
			try
			{
				themeHandle.addCss( cssHandle.getFileName( ) );
				
				themeHandle.getAllCssStyleSheets( ).get( 0 );

			}
			catch ( SemanticException e )
			{
				ExceptionHandler.handle( e );
			}
		}
	}
	
}
