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

package org.inquidia.kettle.plugins.logresultfiles.jobentry;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
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

import java.util.Iterator;
import java.util.List;



/**
 * Log Parameters type of Job Entry.
 *
 * @author Chris
 * @since 2015-04-30
 */
@JobEntry( id = "LogResultFilesPlugin", image = "FTL.png", name = "LogResultFiles.Name",
        description = "LogResultFiles.Description",
        categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.Utility",
        i18nPackageName = "org.inquidia.kettle.plugins.logpresultfiles.jobentry",
        documentationUrl = "https://github.com/inquidia/LogHelper",
        casesUrl="https://github.com/inquidia/LogHelper/issues"  )
public class LogResultFiles extends JobEntryBase implements Cloneable, JobEntryInterface {
    private static Class<?> PKG = LogResultFiles.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$


    private String loggingLevel;

    private String limit;

    public LogResultFiles(String name) {
        super( name, "");
        limit="0";
        loggingLevel=null;
        setID(-1L);
    }

    public LogResultFiles() {
        this("");
        // clear();
    }

    public Object clone() {
        LogResultFiles lrr = (LogResultFiles) super.clone();
        return lrr;
    }

    public String getXML() {
        StringBuffer retval = new StringBuffer( 300 );

        retval.append( super.getXML() );
        retval.append( "      " ).append( XMLHandler.addTagValue( "logLevel", getLoggingLevel() ) ); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append( "      " ).append( XMLHandler.addTagValue( "limit", getLimit() ) ); //$NON-NLS-1$ //$NON-NLS-2$

        return retval.toString();
    }

    public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep ) throws KettleXMLException {
        try {
            System.out.println("------------------------Started XML Load");
            super.loadXML( entrynode, databases, slaveServers );
            System.out.println("--------------------------Super XML Load");
            setLoggingLevel(XMLHandler.getTagValue(entrynode, "logLevel")); //$NON-NLS-1$ //$NON-NLS-2$
            setLimit(XMLHandler.getTagValue(entrynode, "limit")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        catch ( KettleXMLException dbe ) {
            throw new KettleXMLException( BaseMessages.getString(PKG, "LogResultFiles.Error.Exception.UnableLoadXML"), dbe );
        }
    }

    // Load the jobentry from repository
    public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
                         List<SlaveServer> slaveServers ) throws KettleException {
        try {
            setLoggingLevel(rep.getJobEntryAttributeString(id_jobentry, "logLevel"));
            setLimit(rep.getJobEntryAttributeString(id_jobentry, "limit"));
        } catch ( KettleException dbe ) {

            throw new KettleException( BaseMessages.getString( PKG, "LogResultFiles.Error.Exception.UnableLoadRep" )
                    + id_jobentry, dbe );
        }
    }

    // Save the attributes of this job entry
    //
    public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
        try {
            rep.saveJobEntryAttribute(id_job, getObjectId(), "logLevel", getLoggingLevel()); //$NON-NLS-1$
            rep.saveJobEntryAttribute(id_job, getObjectId(), "limit", getLimit() ); //$NON-NLS-1$
        } catch ( KettleDatabaseException dbe ) {
            throw new KettleDatabaseException( BaseMessages.getString( PKG, "LogResultFiles.Error.Exception.UnableSaveRep" )
                    + getObjectId(), dbe );
        }
    }

    public void clear() {
        super.clear();

        setLoggingLevel(null);
        setLimit("0");
    }

    public void setLoggingLevel( String n ) {
        loggingLevel = n;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLimit( String n ) {
        limit = n;
    }

    public String getLimit() {
        return limit;
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

        List<ResultFile> resultFiles = result.getResultFilesList();

        LogLevel logLevel;

        if( getLoggingLevel() == null )
        {
            logLevel = LogLevel.MINIMAL;
        } else {
            logLevel = LogLevel.getLogLevelForCode( getLoggingLevel() );
        }

        if( logLevel == null )
        {
            throw new KettleJobException( "Unable to set log level." );
        }

        writeToLog( logLevel, "=========================== Write Result Files to Log - " + getName() + " =====================");

        Iterator<ResultFile> itResultFiles = resultFiles.iterator();
        int counter=0;

        while( itResultFiles.hasNext() )
        {
            if( Const.toInt( environmentSubstitute(getLimit()), 0) != 0 && counter >= Const.toInt( getLimit(), 0 ) )
            {
                break;
            }
            ResultFile rf = itResultFiles.next();
            String filename = rf.getFile().getName().getFriendlyURI();
            writeToLog( logLevel, "Filename " + counter +" = "+filename );
            counter++;
        }

        writeToLog( logLevel, "=========================== Write Result Files to Log - " + getName() + " =====================");


        return result;

    }

    private void writeToLog( LogLevel logLevel, String message )
    {
        if( ! Const.isEmpty( message ) ) {
            switch (logLevel) {
                case ERROR:
                    logError(message);
                    break;
                case MINIMAL:
                    logMinimal(message);
                    break;
                case BASIC:
                    logBasic(message);
                    break;
                case DETAILED:
                    logDetailed(message);
                    break;
                case DEBUG:
                    logDebug(message);
                    break;
                case ROWLEVEL:
                default:
                    logMinimal(message);
                    break;
            }
        }
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
