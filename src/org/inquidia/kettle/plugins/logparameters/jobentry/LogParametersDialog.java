/*! ******************************************************************************
*
* Log Helper plugin for Pentaho Data Integration
*
* Author: Inquidia Consulting
* https://github.com/inquidia/LogHelper
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

package org.inquidia.kettle.plugins.logparameters.jobentry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


/**
 * Dialog that allows you to enter the settings for a Log Parameters job entry.
 *
 * @author Chris
 * @since 2015-04-30
 *
 */
public class LogParametersDialog extends JobEntryDialog implements JobEntryDialogInterface {
    private static Class<?> PKG = LogParameters.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private static final String[] VARIABLETYPES = new String[] {
            BaseMessages.getString(PKG, "LogParametersDialog.VariableType.Parameter"),
            BaseMessages.getString( PKG, "LogParametersDialog.VariableType.All" )
    };

    private Shell shell;

    private LogParameters jobEntry;

    private Label wlName;

    private Text wName;

    private FormData fdlName, fdName;

    private Label wlLogLevel;

    private CCombo wLogLevel;

    private FormData fdlLogLevel, fdLogLevel;

    private Label wlVariableType;

    private CCombo wVariableType;

    private FormData fdlVariableType, fdVariableType;

    private Label wlRegexFilter;

    private TextVar wRegexFilter;

    private FormData fdlRegexFilter, fdRegexFilter;

    private Button wOK, wCancel;

    private Listener lsOK, lsCancel;

    private SelectionAdapter lsDef;

    private boolean backupChanged;

    private Display display;

    private Link wDevelopedBy;
    private FormData fdDevelopedBy;

    public LogParametersDialog( Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta ) {
        super(parent, jobEntryInt, rep, jobMeta);
        jobEntry = (LogParameters) jobEntryInt;
    }

    public JobEntryInterface open() {
        Shell parent = getParent();
        display = parent.getDisplay();

        shell = new Shell( parent, props.getJobsDialogStyle() );
        props.setLook(shell);
        JobDialog.setShellImage(shell, jobEntry);

        ModifyListener lsMod = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                jobEntry.setChanged();
            }
        };
        backupChanged = jobEntry.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout( formLayout );
        shell.setText( BaseMessages.getString( PKG, "LogParametersDialog.Title" ) );

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Name line
        wlName = new Label( shell, SWT.RIGHT );
        wlName.setText( BaseMessages.getString( PKG, "LogParameters.Name.Label" ) );
        props.setLook( wlName );
        fdlName = new FormData();
        fdlName.left = new FormAttachment( 0, 0 );
        fdlName.top = new FormAttachment( 0, 0 );
        fdlName.right = new FormAttachment( middle, 0 );
        wlName.setLayoutData( fdlName );

        wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook( wName );
        wName.addModifyListener( lsMod );
        fdName = new FormData();
        fdName.top = new FormAttachment( 0, 0 );
        fdName.left = new FormAttachment( middle, margin );
        fdName.right = new FormAttachment( 100, 0 );
        wName.setLayoutData( fdName );

        // /////////////////////
        // Log Level line
        // /////////////////////
        wlLogLevel = new Label( shell, SWT.RIGHT );
        wlLogLevel.setText( BaseMessages.getString( PKG, "LogParametersDialog.LogLevel.Label" ) );
        props.setLook( wlLogLevel );
        fdlLogLevel = new FormData();
        fdlLogLevel.left = new FormAttachment( 0, 0 );
        fdlLogLevel.top = new FormAttachment( wName, margin );
        fdlLogLevel.right = new FormAttachment( middle, 0 );
        wlLogLevel.setLayoutData( fdlLogLevel );

        wLogLevel = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wLogLevel.setItems( LogLevel.getLogLevelDescriptions() );
        props.setLook( wLogLevel );
        fdLogLevel = new FormData();
        fdLogLevel.left = new FormAttachment( middle, margin );
        fdLogLevel.top = new FormAttachment( wName, margin );
        fdLogLevel.right = new FormAttachment( 100, 0 );
        wLogLevel.setLayoutData( fdLogLevel );

        // /////////////////////
        // Variable Type Line
        // /////////////////////
        wlVariableType = new Label( shell, SWT.RIGHT );
        wlVariableType.setText( BaseMessages.getString( PKG, "LogParametersDialog.VariableType.Label" ) );
        props.setLook( wlVariableType );
        fdlVariableType = new FormData();
        fdlVariableType.left = new FormAttachment( 0, 0 );
        fdlVariableType.top = new FormAttachment( wLogLevel, margin );
        fdlVariableType.right = new FormAttachment( middle, 0 );
        wlVariableType.setLayoutData( fdlVariableType );

        wVariableType = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wVariableType.setItems( VARIABLETYPES );
        props.setLook( wVariableType );
        fdVariableType = new FormData();
        fdVariableType.left = new FormAttachment( middle, margin );
        fdVariableType.top = new FormAttachment( wLogLevel, margin );
        fdVariableType.right = new FormAttachment( 100, 0 );
        wVariableType.setLayoutData( fdVariableType );

        // /////////////////////
        // Regex Filter line
        // /////////////////////
        wlRegexFilter = new Label( shell, SWT.RIGHT );
        wlRegexFilter.setText(BaseMessages.getString(PKG, "LogParametersDialog.RegexFilter.Label"));
        props.setLook( wlRegexFilter );
        fdlRegexFilter = new FormData();
        fdlRegexFilter.left = new FormAttachment( 0, 0 );
        fdlRegexFilter.top = new FormAttachment( wVariableType, margin );
        fdlRegexFilter.right = new FormAttachment( middle, 0 );
        wlRegexFilter.setLayoutData( fdlRegexFilter );

        wRegexFilter = new TextVar( jobMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wRegexFilter);
        wRegexFilter.addModifyListener( lsMod );
        fdRegexFilter = new FormData();
        fdRegexFilter.left = new FormAttachment( middle, margin );
        fdRegexFilter.right = new FormAttachment( 100, -margin );
        fdRegexFilter.top = new FormAttachment( wVariableType, margin );
        wRegexFilter.setLayoutData( fdRegexFilter );

        // Some buttons
        wOK = new Button( shell, SWT.PUSH );
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button( shell, SWT.PUSH );
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel}, margin, wRegexFilter);

        wDevelopedBy = new Link( shell, SWT.PUSH );
        wDevelopedBy.setText("Developed by Inquidia Consulting (<a href=\"http://www.inquidia.com\">www.inquidia.com</a>)");
        fdDevelopedBy = new FormData();
        fdDevelopedBy.right = new FormAttachment( 100, margin );
        fdDevelopedBy.bottom = new FormAttachment( 100, margin );
        wDevelopedBy.setLayoutData(fdDevelopedBy);
        wDevelopedBy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                Program.launch("http://www.inquidia.com");
            }
        });

        // Add listeners
        lsCancel = new Listener() {
            public void handleEvent( Event e ) {
                cancel();
            }
        };
        lsOK = new Listener() {
            public void handleEvent( Event e ) {
                ok();
            }
        };

        wOK.addListener( SWT.Selection, lsOK );
        wCancel.addListener( SWT.Selection, lsCancel );

        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected( SelectionEvent e ) {
                ok();
            }
        };
        wName.addSelectionListener( lsDef );
        wLogLevel.addSelectionListener( lsDef );
        wVariableType.addSelectionListener( lsDef );
        wRegexFilter.addSelectionListener(lsDef);

        // Detect [X] or ALT-F4 or something that kills this window...
        shell.addShellListener( new ShellAdapter() {
            public void shellClosed( ShellEvent e ) {
                cancel();
            }
        } );

        BaseStepDialog.setSize(shell);

        getData();

        shell.open();
        props.setDialogSize( shell, "LogParametersDialogSize" );
        while ( !shell.isDisposed() ) {
            if ( !display.readAndDispatch() ) {
                display.sleep();
            }
        }

        return jobEntry;
    }

    public void dispose() {
        WindowProperty winprop = new WindowProperty( shell );
        props.setScreen( winprop );
        shell.dispose();
    }


    public void getData() {
        wName.setText( Const.NVL(jobEntry.getName(), "" ) ) ;
        if( jobEntry.getLoggingLevel() != null ) {
            wLogLevel.select(LogLevel.getLogLevelForCode(jobEntry.getLoggingLevel()).getLevel());
        }
        wVariableType.select( jobEntry.getVariableType() );
        wRegexFilter.setText( Const.NVL( jobEntry.getRegexFilter(), "" ) );

        wName.selectAll();
        wName.setFocus();
    }

    private void cancel() {
        jobEntry.setChanged( backupChanged );

        jobEntry = null;
        dispose();
    }

    private void ok() {
        if ( Const.isEmpty( wName.getText() ) ) {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setText( BaseMessages.getString( PKG, "System.StepJobEntryNameMissing.Title" ) );
            mb.setMessage( BaseMessages.getString( PKG, "System.JobEntryNameMissing.Msg" ) );
            mb.open();
            return;
        }
        jobEntry.setName(wName.getText());
        jobEntry.setRegexFilter(wRegexFilter.getText());
        jobEntry.setVariableType(wVariableType.getSelectionIndex());
        if( wLogLevel.getSelectionIndex() >= 0 ) {
            jobEntry.setLoggingLevel(LogLevel.logLogLevelCodes()[wLogLevel.getSelectionIndex()]);
        } else {
            jobEntry.setLoggingLevel( null );
        }

        dispose();
    }
}
