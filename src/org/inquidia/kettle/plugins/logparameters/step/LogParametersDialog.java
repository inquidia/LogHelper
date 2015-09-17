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

package org.inquidia.kettle.plugins.logparameters.step;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class LogParametersDialog extends BaseStepDialog implements StepDialogInterface {
    private static Class<?> PKG = LogParametersMeta.class; // for i18n purposes, needed by Translator2!!

    private static final String[] VARIABLETYPES = new String[] {
            BaseMessages.getString(PKG, "LogParametersDialog.VariableType.Parameter"),
            BaseMessages.getString( PKG, "LogParametersDialog.VariableType.All" )
    };


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

    private Label wlOnlyLogFirstRow;

    private Button wOnlyLogFirstRow;

    private FormData fdlOnlyLogFirstRow, fdOnlyLogFirstRow;

    private Link wDevelopedBy;
    private FormData fdDevelopedBy;

    private LogParametersMeta input;

    public LogParametersDialog( Shell parent, Object in, TransMeta transMeta, String sname ) {
        super( parent, (BaseStepMeta) in, transMeta, sname );
        input = (LogParametersMeta) in;
    }

    public String open() {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
        props.setLook( shell );
        setShellImage( shell, input );

        ModifyListener lsMod = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                input.setChanged();
            }
        };
        changed = input.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout( formLayout );
        shell.setText( BaseMessages.getString( PKG, "LogParametersDialog.DialogTitle" ) );

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Stepname line
        wlStepname = new Label( shell, SWT.RIGHT );
        wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
        props.setLook( wlStepname );
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment( 0, 0 );
        fdlStepname.top = new FormAttachment( 0, margin );
        fdlStepname.right = new FormAttachment( middle, -margin );
        wlStepname.setLayoutData( fdlStepname );
        wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        wStepname.setText( stepname );
        props.setLook( wStepname );
        wStepname.addModifyListener( lsMod );
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment( middle, 0 );
        fdStepname.top = new FormAttachment( 0, margin );
        fdStepname.right = new FormAttachment( 100, 0 );
        wStepname.setLayoutData( fdStepname );

        // /////////////////////
        // Log Level line
        // /////////////////////
        wlLogLevel = new Label( shell, SWT.RIGHT );
        wlLogLevel.setText( BaseMessages.getString( PKG, "LogParametersDialog.LogLevel.Label" ) );
        props.setLook( wlLogLevel );
        fdlLogLevel = new FormData();
        fdlLogLevel.left = new FormAttachment( 0, 0 );
        fdlLogLevel.top = new FormAttachment( wStepname, margin );
        fdlLogLevel.right = new FormAttachment( middle, 0 );
        wlLogLevel.setLayoutData( fdlLogLevel );

        wLogLevel = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        wLogLevel.setItems( LogLevel.getLogLevelDescriptions() );
        props.setLook( wLogLevel );
        fdLogLevel = new FormData();
        fdLogLevel.left = new FormAttachment( middle, margin );
        fdLogLevel.top = new FormAttachment( wStepname, margin );
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

        wRegexFilter = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wRegexFilter);
        wRegexFilter.addModifyListener( lsMod );
        fdRegexFilter = new FormData();
        fdRegexFilter.left = new FormAttachment( middle, margin );
        fdRegexFilter.right = new FormAttachment( 100, -margin );
        fdRegexFilter.top = new FormAttachment( wVariableType, margin );
        wRegexFilter.setLayoutData( fdRegexFilter );


        // Only Log First Row Line
        //
        wlOnlyLogFirstRow = new Label( shell, SWT.RIGHT );
        wlOnlyLogFirstRow.setText( BaseMessages.getString( PKG, "LogParametersDialog.OnlyFirstRow.Label" ) );
        props.setLook( wlOnlyLogFirstRow );
        fdlOnlyLogFirstRow = new FormData();
        fdlOnlyLogFirstRow.left = new FormAttachment( 0, 0 );
        fdlOnlyLogFirstRow.top = new FormAttachment( wRegexFilter, margin );
        fdlOnlyLogFirstRow.right = new FormAttachment( middle, -margin );
        wlOnlyLogFirstRow.setLayoutData( fdlOnlyLogFirstRow );

        wOnlyLogFirstRow = new Button( shell, SWT.CHECK );
        props.setLook( wOnlyLogFirstRow );
        fdOnlyLogFirstRow = new FormData();
        fdOnlyLogFirstRow.left = new FormAttachment( middle, 0 );
        fdOnlyLogFirstRow.top = new FormAttachment( wRegexFilter, margin );
        fdOnlyLogFirstRow.right = new FormAttachment( 100, 0 );
        wOnlyLogFirstRow.setLayoutData( fdOnlyLogFirstRow );
        wOnlyLogFirstRow.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e ) {
                input.setChanged();
            }
        } );


        wOK = new Button( shell, SWT.PUSH );
        wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );

        wCancel = new Button( shell, SWT.PUSH );
        wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

        setButtonPositions( new Button[] { wOK, wCancel }, margin, wOnlyLogFirstRow );

        wDevelopedBy = new Link( shell, SWT.PUSH );
        wDevelopedBy.setText("Developed by Inquidia Consulting (<a href=\"http://www.inquidia.com\">www.inquidia.com</a>)");
        fdDevelopedBy = new FormData();
        fdDevelopedBy.right = new FormAttachment( 100, margin );
        fdDevelopedBy.bottom = new FormAttachment( 100, margin );
        wDevelopedBy.setLayoutData( fdDevelopedBy );
        wDevelopedBy.addSelectionListener( new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent selectionEvent ) {
                Program.launch("http://www.inquidia.com");
            }
        } );

        // Add listeners
        lsOK = new Listener() {
            public void handleEvent( Event e ) {
                ok();
            }
        };
        lsCancel = new Listener() {
            public void handleEvent( Event e ) {
                cancel();
            }
        };

        wOK.addListener( SWT.Selection, lsOK );
        wCancel.addListener( SWT.Selection, lsCancel );

        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected( SelectionEvent e ) {
                ok();
            }
        };

        wStepname.addSelectionListener( lsDef );
        wRegexFilter.addSelectionListener( lsDef );
        wLogLevel.addSelectionListener( lsDef );
        wVariableType.addSelectionListener( lsDef );

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener( new ShellAdapter() {
            public void shellClosed( ShellEvent e ) {
                cancel();
            }
        } );

        // Set the shell size, based upon previous time...
        setSize();

        getData();
        input.setChanged( changed );

        shell.open();
        while ( !shell.isDisposed() ) {
            if ( !display.readAndDispatch() ) {
                display.sleep();
            }
        }
        return stepname;
    }

    /**
     * Copy information from the meta-data input to the dialog fields.
     */
    public void getData() {
        wStepname.setText(Const.NVL(stepname, "")) ;
        if( input.getLoggingLevel() != null ) {
            wLogLevel.select(LogLevel.getLogLevelForCode(input.getLoggingLevel()).getLevel());
        }
        wVariableType.select( input.getVariableType() );
        wRegexFilter.setText(Const.NVL(input.getRegexFilter(), ""));
        wOnlyLogFirstRow.setSelection( input.isOnlyLogFirstRow() );

        wStepname.selectAll();
        wStepname.setFocus();
    }

    private void cancel() {
        stepname = null;

        input.setChanged(backupChanged);

        dispose();
    }

    private void getInfo( LogParametersMeta lpm ) {
        if( wLogLevel.getSelectionIndex() >= 0 ) {
            lpm.setLoggingLevel(LogLevel.logLogLevelCodes()[wLogLevel.getSelectionIndex()]);
        } else {
            lpm.setLoggingLevel( null );
        }
        lpm.setVariableType( wVariableType.getSelectionIndex() );
        lpm.setRegexFilter( wRegexFilter.getText() );
        lpm.setOnlyLogFirstRow( wOnlyLogFirstRow.getSelection() );
    }

    private void ok() {
        if ( Const.isEmpty( wStepname.getText() ) ) {
            return;
        }

        stepname = wStepname.getText(); // return value

        getInfo( input );

        dispose();
    }

}
