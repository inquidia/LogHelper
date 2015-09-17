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
package org.inquidia.kettle.plugins.setlogtovariables.jobentry;

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
public class SetLogToVariableDialog extends JobEntryDialog implements JobEntryDialogInterface {
    private static Class<?> PKG = org.inquidia.kettle.plugins.setlogtovariables.jobentry.SetLogToVariable.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private static final String[]  JOB_LEVELS = {BaseMessages.getString( PKG, "SetLogToVariableDialog.JobLevels.Top" ),
      BaseMessages.getString( PKG, "SetLogToVariableDialog.JobLevels.Parent" ),
      BaseMessages.getString( PKG, "SetLogToVariableDialog.JobLevels.Current" ) };


    private static final String[] variableTypeDesc = {
            BaseMessages.getString( PKG, "SetLogToVariableDialog.VariableType.JVM" ),
            BaseMessages.getString( PKG, "SetLogToVariableDialog.VariableType.CurrentJob" ),
            BaseMessages.getString( PKG, "SetLogToVariableDialog.VariableType.ParentJob" ),
            BaseMessages.getString( PKG, "SetLogToVariableDialog.VariableType.RootJob" ), };

    private Shell shell;

    private SetLogToVariable jobEntry;

    private Label wlName;

    private Text wName;

    private FormData fdlName, fdName;

    private Label wlJobLevel;

    private CCombo wJobLevel;

    private FormData fdlJobLevel, fdJobLevel;

    private Label wlVariableName;

    private TextVar wVariableName;

    private FormData fdlVariableName, fdVariableName;

    private Label wlVariableType;

    private CCombo wVariableType;

    private FormData fdlVariableType, fdVariableType;

    private Label wlLimit;

    private TextVar wLimit;

    private FormData fdlLimit, fdLimit;

    private Label wlLogLevel;

    private CCombo wLogLevel;

    private FormData fdlLogLevel, fdLogLevel;

    private Button wOK, wCancel;

    private Listener lsOK, lsCancel;

    private SelectionAdapter lsDef;

    private boolean backupChanged;

    private Display display;

    private Link wDevelopedBy;
    private FormData fdDevelopedBy;

    public SetLogToVariableDialog(Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta) {
        super(parent, jobEntryInt, rep, jobMeta);
        jobEntry = (SetLogToVariable) jobEntryInt;
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
        shell.setText( BaseMessages.getString( PKG, "SetLogToVariableDialog.Title" ) );

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Name line
        wlName = new Label( shell, SWT.RIGHT );
        wlName.setText( BaseMessages.getString( PKG, "SetLogToVariableDialog.Name.Label" ) );
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
        // Job Level line
        // /////////////////////
        wlJobLevel = new Label( shell, SWT.RIGHT );
        wlJobLevel.setText(BaseMessages.getString(PKG, "SetLogToVariableDialog.JobLevel.Label"));
        props.setLook(wlJobLevel);
        fdlJobLevel = new FormData();
        fdlJobLevel.left = new FormAttachment( 0, 0 );
        fdlJobLevel.top = new FormAttachment( wName, margin );
        fdlJobLevel.right = new FormAttachment( middle, 0 );
        wlJobLevel.setLayoutData( fdlJobLevel );

        wJobLevel = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wJobLevel.setItems(JOB_LEVELS);
        props.setLook(wJobLevel);
        fdJobLevel = new FormData();
        fdJobLevel.left = new FormAttachment( middle, margin );
        fdJobLevel.top = new FormAttachment( wName, margin );
        fdJobLevel.right = new FormAttachment( 100, 0 );
        wJobLevel.setLayoutData( fdJobLevel );

        // ////////////////////
        // Variable Name Line
        // ////////////////////
        wlVariableName = new Label( shell, SWT.RIGHT );
        wlVariableName.setText(BaseMessages.getString( PKG, "SetLogToVariableDialog.VariableName.Label" ) );
        props.setLook(wlVariableName);
        fdlVariableName = new FormData();
        fdlVariableName.left = new FormAttachment( 0, 0 );
        fdlVariableName.top = new FormAttachment( wJobLevel, margin );
        fdlVariableName.right = new FormAttachment( middle, 0 );
        wlVariableName.setLayoutData( fdlVariableName );

        wVariableName = new TextVar( jobMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook( wVariableName );
        wVariableName.addModifyListener(lsMod);
        fdVariableName = new FormData();
        fdVariableName.left = new FormAttachment( middle, margin );
        fdVariableName.right = new FormAttachment( 100, -margin );
        fdVariableName.top = new FormAttachment( wJobLevel, margin );
        wVariableName.setLayoutData( fdVariableName );

        // /////////////////////
        // Variable Type line
        // /////////////////////
        wlVariableType = new Label( shell, SWT.RIGHT );
        wlVariableType.setText(BaseMessages.getString(PKG, "SetLogToVariableDialog.VariableType.Label"));
        props.setLook(wlVariableType);
        fdlVariableType = new FormData();
        fdlVariableType.left = new FormAttachment( 0, 0 );
        fdlVariableType.top = new FormAttachment( wVariableName, margin );
        fdlVariableType.right = new FormAttachment( middle, 0 );
        wlVariableType.setLayoutData( fdlVariableType );

        wVariableType = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wVariableType.setItems(variableTypeDesc);
        props.setLook(wVariableType);
        fdVariableType = new FormData();
        fdVariableType.left = new FormAttachment( middle, margin );
        fdVariableType.top = new FormAttachment( wVariableName, margin );
        fdVariableType.right = new FormAttachment( 100, 0 );
        wVariableType.setLayoutData( fdVariableType );

       // /////////////////////
        // Limit line
        // /////////////////////
        wlLimit = new Label( shell, SWT.RIGHT );
        wlLimit.setText(BaseMessages.getString(PKG, "SetLogToVariableDialog.Limit.Label"));
        props.setLook( wlLimit );
        fdlLimit = new FormData();
        fdlLimit.left = new FormAttachment( 0, 0 );
        fdlLimit.top = new FormAttachment( wVariableType, margin );
        fdlLimit.right = new FormAttachment( middle, 0 );
        wlLimit.setLayoutData(fdlLimit);

        wLimit = new TextVar( jobMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wLimit);
        wLimit.addModifyListener( lsMod );
        fdLimit = new FormData();
        fdLimit.left = new FormAttachment( middle, margin );
        fdLimit.right = new FormAttachment( 100, -margin );
        fdLimit.top = new FormAttachment( wVariableType, margin );
        wLimit.setLayoutData(fdLimit);

        // /////////////////////
        // Log Level line
        // /////////////////////
        wlLogLevel = new Label( shell, SWT.RIGHT );
        wlLogLevel.setText( BaseMessages.getString( PKG, "SetLogToVariableDialog.LogLevel.Label" ) );
        props.setLook( wlLogLevel );
        fdlLogLevel = new FormData();
        fdlLogLevel.left = new FormAttachment( 0, 0 );
        fdlLogLevel.top = new FormAttachment( wLimit, margin );
        fdlLogLevel.right = new FormAttachment( middle, 0 );
        wlLogLevel.setLayoutData( fdlLogLevel );

        wLogLevel = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wLogLevel.setItems(LogLevel.getLogLevelDescriptions());
        props.setLook( wLogLevel );
        fdLogLevel = new FormData();
        fdLogLevel.left = new FormAttachment( middle, margin );
        fdLogLevel.top = new FormAttachment( wLimit, margin );
        fdLogLevel.right = new FormAttachment( 100, 0 );
        wLogLevel.setLayoutData( fdLogLevel );

        // Some buttons
        wOK = new Button( shell, SWT.PUSH );
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button( shell, SWT.PUSH );
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel}, margin, wLogLevel);

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
        wCancel.addListener(SWT.Selection, lsCancel);

        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected( SelectionEvent e ) {
                ok();
            }
        };
        wName.addSelectionListener( lsDef );

        // Detect [X] or ALT-F4 or something that kills this window...
        shell.addShellListener( new ShellAdapter() {
            public void shellClosed( ShellEvent e ) {
                cancel();
            }
        } );

        BaseStepDialog.setSize(shell);

        getData();

        shell.open();
        props.setDialogSize( shell, "LogResultRowsDialogSize" );
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
        wName.setText(Const.NVL(jobEntry.getName(), "")) ;

        wJobLevel.select(jobEntry.getJobLevel());
        wVariableName.setText(Const.NVL(jobEntry.getVariableName(), ""));
        wVariableType.select( jobEntry.getVariableType() );
        wLimit.setText( Const.NVL( jobEntry.getLimit(), "" ) );

        if( jobEntry.getLoggingLevel() != null ) {
            wLogLevel.select(LogLevel.getLogLevelForCode(jobEntry.getLoggingLevel()).getLevel());
        }

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
        jobEntry.setName( wName.getText() );
        jobEntry.setJobLevel(wJobLevel.getSelectionIndex());
        jobEntry.setVariableName(wVariableName.getText());
        jobEntry.setVariableType( wVariableType.getSelectionIndex() );
        jobEntry.setLimit(wLimit.getText());
        if( wLogLevel.getSelectionIndex() >= 0 ) {
            jobEntry.setLoggingLevel(LogLevel.logLogLevelCodes()[wLogLevel.getSelectionIndex()]);
        } else {
            jobEntry.setLoggingLevel( null );
        }

        dispose();
    }
}
