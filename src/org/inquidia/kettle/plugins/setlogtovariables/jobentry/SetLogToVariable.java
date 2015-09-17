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
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Log Parameters type of Job Entry.
 *
 * @author Chris
 * @since 2015-09-17
 */
@JobEntry( id = "LogToVarPlugin", image = "LTV.png", name = "SetLogToVariable.Name",
        description = "SetLogToVariable.Description",
        categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.Utility",
        i18nPackageName = "org.inquidia.kettle.plugins.setlogtovariables.jobentry",
        documentationUrl = "https://github.com/inquidia/LogHelper",
        casesUrl="https://github.com/inquidia/LogHelper/issues"  )
public class SetLogToVariable extends JobEntryBase implements Cloneable, JobEntryInterface {
    private static Class<?> PKG = SetLogToVariable.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private static final String[] variableTypeCode = { "JVM", "CURRENT_JOB", "PARENT_JOB", "ROOT_JOB" };

    public static final int VARIABLE_TYPE_JVM = 0;
    public static final int VARIABLE_TYPE_CURRENT_JOB = 1;
    public static final int VARIABLE_TYPE_PARENT_JOB = 2;
    public static final int VARIABLE_TYPE_ROOT_JOB = 3;

    public static final int JOB_LEVEL_ROOT = 0;
    public static final int JOB_LEVEL_PARENT = 1;
    public static final int JOB_LEVEL_CURRENT=2;

    private String loggingLevel;

    private int jobLevel = 0;

    private String variableName;

    private int variableType = 0;

    private String limit;

    public SetLogToVariable(String name) {
        super( name, "");
        limit="0";
        jobLevel=-1;
        variableType=-1;
        variableName="";
        setID(-1L);
        loggingLevel="";
    }

    public SetLogToVariable() {
        this("");
    }

    public Object clone() {
        SetLogToVariable ltv = (SetLogToVariable) super.clone();
        return ltv;
    }

    public String getXML() {
        StringBuffer retval = new StringBuffer( 300 );

        retval.append(super.getXML());
        retval.append( "      " ).append(XMLHandler.addTagValue("jobLevel", getJobLevel())); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append( "      " ).append(XMLHandler.addTagValue("variableName", getVariableName()));
        retval.append( "      " ).append(XMLHandler.addTagValue("variableType", getVariableType()));
        retval.append( "      " ).append(XMLHandler.addTagValue("limit", getLimit())); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append( "      " ).append(XMLHandler.addTagValue("loggingLevel", getLoggingLevel())); //$NON-NLS-1$ //$NON-NLS-2$

        return retval.toString();
    }

    public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep ) throws KettleXMLException {
        try {
            System.out.println("------------------------Started XML Load");
            super.loadXML(entrynode, databases, slaveServers);
            System.out.println("--------------------------Super XML Load");
            setJobLevel(Const.toInt(XMLHandler.getTagValue(entrynode, "jobLevel"), -1)); //$NON-NLS-1$ //$NON-NLS-2$
            setVariableName(XMLHandler.getTagValue(entrynode, "variableName")); //$NON-NLS-1$ //$NON-NLS-2$
            setVariableType(Const.toInt(XMLHandler.getTagValue(entrynode, "variableType"), -1)); //$NON-NLS-1$ //$NON-NLS-2$
            setLimit(XMLHandler.getTagValue(entrynode, "limit")); //$NON-NLS-1$ //$NON-NLS-2$
            setLoggingLevel(XMLHandler.getTagValue(entrynode, "loggingLevel")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        catch ( KettleXMLException dbe ) {
            throw new KettleXMLException( BaseMessages.getString(PKG, "SetLogToVariable.Error.Exception.UnableLoadXML"), dbe );
        }
    }

    // Load the jobentry from repository
    public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
                         List<SlaveServer> slaveServers ) throws KettleException {
        try {
            setJobLevel(Const.toInt(rep.getJobEntryAttributeString(id_jobentry, "jobLevel"), -1));
            setVariableName(rep.getJobEntryAttributeString(id_jobentry, "variableName"));
            setVariableType(Const.toInt(rep.getJobEntryAttributeString(id_jobentry, "variableType"), -1));
            setLimit(rep.getJobEntryAttributeString(id_jobentry, "limit"));
            setLoggingLevel(rep.getJobEntryAttributeString(id_jobentry, "loggingLevel"));
        } catch ( KettleException dbe ) {

            throw new KettleException( BaseMessages.getString( PKG, "SetLogToVariable.Error.Exception.UnableLoadRep" )
                    + id_jobentry, dbe );
        }
    }

    // Save the attributes of this job entry
    //
    public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
        try {
            rep.saveJobEntryAttribute(id_job, getObjectId(), "jobLevel", getJobLevel()); //$NON-NLS-1$
            rep.saveJobEntryAttribute(id_job, getObjectId(), "variableName", getVariableName()); //$NON-NLS-1$
            rep.saveJobEntryAttribute(id_job, getObjectId(), "variableType", getVariableType()); //$NON-NLS-1$
            rep.saveJobEntryAttribute(id_job, getObjectId(), "limit", getLimit()); //$NON-NLS-1$
            rep.saveJobEntryAttribute(id_job, getObjectId(), "loggingLevel", getLoggingLevel()); //$NON-NLS-1$
        } catch ( KettleDatabaseException dbe ) {
            throw new KettleDatabaseException( BaseMessages.getString( PKG, "SetLogToVariable.Error.Exception.UnableSaveRep" )
                    + getObjectId(), dbe );
        }
    }

    public void clear() {
        super.clear();

        setJobLevel(0);
        setVariableName("");
        setVariableType(0);
        setLimit("0");
        loggingLevel="";
    }

    public void setLoggingLevel( String n ) {
        loggingLevel = n;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public int getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(int jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public int getVariableType() {
        return variableType;
    }

    public void setVariableType(int variableType) {
        this.variableType = variableType;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
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

        LogLevel logLevel;

        if( Const.isEmpty( getLoggingLevel() ) )
        {
            logLevel = LogLevel.ROWLEVEL;
        } else {
            logLevel = LogLevel.getLogLevelForCode( getLoggingLevel() );
        }


        Job parent = getParentJob();

        if( getJobLevel() == JOB_LEVEL_PARENT && parent.getParentJob() != null )
        {
            parent = parent.getParentJob();
        } else if ( getJobLevel() == JOB_LEVEL_ROOT )
        {
            while ( parent.getParentJob() != null )
            {
                parent = parent.getParentJob();
            }
        }

        String outputLogChannelId = parent.getLogChannelId();

        List<KettleLoggingEvent> events = KettleLogStore.getAppender().getLogBufferFromTo(outputLogChannelId, true, 0,
                KettleLogStore.getAppender().getMaxNrLines());

        if( getLogLevel().isRowlevel() ) {
            logRowlevel("Found " + events.size() + " logging events.");
        }

        ListIterator<KettleLoggingEvent> itEvents = events.listIterator(events.size());

        ArrayList<String> messages = new ArrayList<String>();

        int limit = Const.toInt(environmentSubstitute( getLimit() ),0);
        while( itEvents.hasPrevious() ) {
            KettleLoggingEvent event = itEvents.previous();
            if(event.getLevel().isVisible(logLevel))
            {
                if( getLogLevel().isRowlevel() )
                {
                    logRowlevel("Adding logging event to variable.");
                }
                messages.add( event.getMessage().toString() );
            } else if( getLogLevel().isRowlevel() )
            {
                logRowlevel("Logging event is not visible. Ignoring it.");
            }
            if( limit > 0 && messages.size() >= limit )
            {
                break;
            }
        }

        if( getLogLevel().isRowlevel() )
        {
            logRowlevel("Found " + messages.size() + " events to include in the variable.");
        }

        StringBuilder logText = new StringBuilder();
        ListIterator<String> itMessages = messages.listIterator(messages.size());

        while( itMessages.hasPrevious() )
        {
            logText.append( itMessages.previous() ).append("\n");
        }

        logRowlevel( logText.toString() );

        String varname = environmentSubstitute( getVariableName() );
        logRowlevel( "Setting variable " + varname );

        switch ( getVariableType() ) {
            case VARIABLE_TYPE_JVM:
                System.setProperty( varname, logText.toString() );
                setVariable( varname, logText.toString() );
                Job parentJobTraverse = parentJob;
                while ( parentJobTraverse != null ) {
                    parentJobTraverse.setVariable( varname, logText.toString() );
                    parentJobTraverse = parentJobTraverse.getParentJob();
                }
                break;

            case VARIABLE_TYPE_ROOT_JOB:
                // set variable in this job entry
                setVariable( varname, logText.toString() );
                Job rootJob = parentJob;
                while ( rootJob != null ) {
                    rootJob.setVariable( varname, logText.toString() );
                    rootJob = rootJob.getParentJob();
                }
                break;

            case VARIABLE_TYPE_CURRENT_JOB:
                setVariable( varname, logText.toString() );
                if ( parentJob != null ) {
                    parentJob.setVariable( varname, logText.toString() );
                } else {
                    throw new KettleJobException( BaseMessages.getString(
                            PKG, "JobEntrySetVariables.Error.UnableSetVariableCurrentJob", varname ) );
                }
                break;

            case VARIABLE_TYPE_PARENT_JOB:
                setVariable( varname, logText.toString() );

                if ( parentJob != null ) {
                    parentJob.setVariable( varname, logText.toString() );
                    Job gpJob = parentJob.getParentJob();
                    if ( gpJob != null ) {
                        gpJob.setVariable( varname, logText.toString() );
                    } else {
                        throw new KettleJobException( BaseMessages.getString(
                                PKG, "JobEntrySetVariables.Error.UnableSetVariableParentJob", varname ) );
                    }
                } else {
                    throw new KettleJobException( BaseMessages.getString(
                            PKG, "JobEntrySetVariables.Error.UnableSetVariableCurrentJob", varname ) );
                }
                break;

            default:
                break;
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
