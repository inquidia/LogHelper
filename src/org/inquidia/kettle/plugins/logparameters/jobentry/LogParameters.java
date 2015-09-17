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

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;



/**
 * Log Parameters type of Job Entry.
 *
 * @author Chris
 * @since 2015-04-30
 */
@JobEntry( id = "LogParametersPlugin", image = "VTL.png", name = "LogParameters.Name",
        description = "LogParameters.Description",
        categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.Utility",
        i18nPackageName = "org.inquidia.kettle.plugins.logparameters.jobentry",
        documentationUrl = "https://github.com/inquidia/LogHelper",
        casesUrl="https://github.com/inquidia/LogHelper/issues" )
public class LogParameters extends JobEntryBase implements Cloneable, JobEntryInterface {
    private static Class<?> PKG = LogParameters.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private static final int VARIABLE_TYPE_PARAMETER=0;
    private static final int VARIABLE_TYPE_ALL=1;

    private int variableType=0;

    private String loggingLevel;

    private String regexFilter;

    public LogParameters(String name) {
        super( name, "");
        variableType=0;
        loggingLevel=null;
        setID(-1L);
    }

    public LogParameters() {
        this("");
        // clear();
    }

    public Object clone() {
        LogParameters lp = (LogParameters) super.clone();
        return lp;
    }

    public String getXML() {
        StringBuffer retval = new StringBuffer( 300 );

        retval.append( super.getXML() );
        retval.append( "      " ).append( XMLHandler.addTagValue( "logLevel", getLoggingLevel() ) ); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append( "      " ).append( XMLHandler.addTagValue( "variableType", getVariableType() ) ); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append( "      " ).append( XMLHandler.addTagValue( "regexFilter", getRegexFilter() ) ); //$NON-NLS-1$ //$NON-NLS-2$

        return retval.toString();
    }

    public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep ) throws KettleXMLException {
        try {
            System.out.println("------------------------Started XML Load");
            super.loadXML( entrynode, databases, slaveServers );
            System.out.println("--------------------------Super XML Load");
            setLoggingLevel( XMLHandler.getTagValue(entrynode, "logLevel") ); //$NON-NLS-1$ //$NON-NLS-2$
            setVariableType( Integer.parseInt( XMLHandler.getTagValue(entrynode, "variableType") ) ); //$NON-NLS-1$ //$NON-NLS-2$
            setRegexFilter( XMLHandler.getTagValue(entrynode, "regexFilter") ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        catch ( KettleXMLException dbe ) {
            throw new KettleXMLException( BaseMessages.getString(PKG, "LogParameters.Error.Exception.UnableLoadXML"), dbe );
        }
    }

    // Load the jobentry from repository
    public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
                         List<SlaveServer> slaveServers ) throws KettleException {
        try {
            setLoggingLevel( rep.getJobEntryAttributeString(id_jobentry, "logLevel") );
            setVariableType(Const.toInt(rep.getJobEntryAttributeString(id_jobentry, "variableType"), 0));
            setRegexFilter(rep.getJobEntryAttributeString(id_jobentry, "regexFilter"));
        } catch ( KettleException dbe ) {

            throw new KettleException( BaseMessages.getString( PKG, "LogParameters.Error.Exception.UnableLoadRep" )
                    + id_jobentry, dbe );
        }
    }

    // Save the attributes of this job entry
    //
    public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
        try {
            rep.saveJobEntryAttribute( id_job, getObjectId(), "logLevel", getLoggingLevel() ); //$NON-NLS-1$
            rep.saveJobEntryAttribute( id_job, getObjectId(), "variableType", Integer.toString( getVariableType() ) ); //$NON-NLS-1$
            rep.saveJobEntryAttribute( id_job, getObjectId(), "regexFilter", getRegexFilter() ); //$NON-NLS-1$
        } catch ( KettleDatabaseException dbe ) {
            throw new KettleDatabaseException( BaseMessages.getString( PKG, "LogParameters.Error.Exception.UnableSaveRep" )
                    + getObjectId(), dbe );
        }
    }

    public void clear() {
        super.clear();

        setLoggingLevel(null);
        setVariableType(0);
        setRegexFilter(null);
    }

    public void setLoggingLevel( String n ) {
        loggingLevel = n;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setVariableType( int n ) {
        variableType = n;
    }

    public int getVariableType() {
        return variableType;
    }

    public void setRegexFilter( String n ) {
        regexFilter = n;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public boolean validate() throws KettleException {
        boolean result=true;
        return result;
    }

    public Result execute( Result previousResult, int nr ) throws KettleException {

        Result result=previousResult;
        result.setResult(validate());
        if(!result.getResult())
        {
            return result;
        }

        String[] keyArray;

        if( getVariableType() == VARIABLE_TYPE_PARAMETER )
        {
            keyArray = getParentJob().listParameters();
        } else {
            keyArray = variables.listVariables();
        }

        Arrays.sort( keyArray );

        LogLevel logLevel;

        if( getLoggingLevel() == null )
        {
            logLevel = LogLevel.NOTHING;
        } else {
            logLevel = LogLevel.getLogLevelForCode( getLoggingLevel() );
        }

        if( logLevel == null )
        {
            throw new KettleJobException( "Unable to set log level." );
        }

        StringBuilder message = new StringBuilder("---------------------- Write Variables to Log - " + getName() + "----------------------\n");

        for( int i = 0; i < keyArray.length; i++ )
        {
            if( keyArray[i]!=null && ( getRegexFilter() == null ||
                   keyArray[i].matches( environmentSubstitute(getRegexFilter()) ) ) ) {
                String value = variables.getVariable(keyArray[i]);
               message.append( keyArray[i] ).append(" = ").append( value ).append("\n");
            }

        }

        if( keyArray.length == 0 )
        {
            message.append("No ").append( getVariableType() == VARIABLE_TYPE_PARAMETER ? "parameters" : "variables" )
                    .append(" are set.");
        }

        message.append("---------------------- Write Variables to Log - " + getName() + "----------------------\n");


        if( ! Const.isEmpty( message.toString() ) ) {
            switch (logLevel) {
                case ERROR:
                    logError(message.toString());
                    break;
                case MINIMAL:
                    logMinimal(message.toString());
                    break;
                case BASIC:
                    logBasic(message.toString());
                    break;
                case DETAILED:
                    logDetailed(message.toString());
                    break;
                case DEBUG:
                    logDebug(message.toString());
                    break;
                case ROWLEVEL:
                default:
                    logMinimal(message.toString());
                    break;
            }
        }

        return result;

    }


    public boolean evaluates() {
        return false;
    }

    public boolean isUnconditional() {
        return true;
    }

    @Override
    public void check( List<CheckResultInterface> remarks, JobMeta jobMeta ) {
/*        andValidator().validate( this, "tableauClient", remarks, putValidators( notBlankValidator(),fileExistsValidator() ) );
        andValidator().validate(this, "server", remarks, putValidators(notBlankValidator()));
        andValidator().validate(this, "serverPort", remarks, putValidators(integerValidator()));
        andValidator().validate(this, "serverUser", remarks, putValidators(notBlankValidator()));
        andValidator().validate(this, "serverPassword", remarks, putValidators(notBlankValidator()));
        andValidator().validate(this, "dataSource", remarks, putValidators(notBlankValidator()));
        if(getRefreshType()==0)
        {
            andValidator().validate(this, "extractFile", remarks, putValidators(notBlankValidator(),fileExistsValidator()));
        }

*/
    }



}
