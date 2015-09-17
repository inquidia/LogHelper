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

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;

/*
 * Created on 04-may-2015
 *
 */
@Step( id="LogParametersStep", image="VTL.png", name="LogParameters.Name", description="LogParameters.Description",
        categoryDescription = "Category.Description", i18nPackageName = "org.inquidia.kettle.plugins.logparameters.step",
        isSeparateClassLoaderNeeded = false,
        documentationUrl = "https://github.com/inquidia/LogHelper",
        casesUrl="https://github.com/inquidia/LogHelper/issues"  )
public class LogParametersMeta extends BaseStepMeta implements StepMetaInterface {
    private static Class<?> PKG = LogParametersMeta.class; // for i18n purposes, needed by Translator2!!

    public static final int VARIABLE_TYPE_PARAMETER = 0;
    public static final int VARIABLE_TYPE_ALL = 1;

    private int variableType=0;

    private String loggingLevel;

    private String regexFilter;

    private boolean onlyLogFirstRow;

    public LogParametersMeta() {
        super(); // allocate BaseStepMeta
    }

    public int getVariableType() {
        return variableType;
    }

    public void setVariableType(int variableType) {
        this.variableType = variableType;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;
    }

    public boolean isOnlyLogFirstRow() {
        return onlyLogFirstRow;
    }

    public void setOnlyLogFirstRow(boolean onlyLogFirstRow) {
        this.onlyLogFirstRow = onlyLogFirstRow;
    }

    public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore )
            throws KettleXMLException {
        readData( stepnode );
    }

    public Object clone() {
        LogParametersMeta retval = (LogParametersMeta) super.clone();
        return retval;
    }

    public void readData( Node stepnode ) throws KettleXMLException {
        try {

            variableType = Const.toInt( XMLHandler.getTagValue(stepnode, "variableType"), 0 );
            loggingLevel = XMLHandler.getTagValue(stepnode, "logLevel");
            regexFilter = XMLHandler.getTagValue(stepnode, "regexFilter");
            onlyLogFirstRow = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "onlyLogFirstRow"));
        } catch ( Exception e ) {
            throw new KettleXMLException( "Unable to load step info from XML", e );
        }
    }

    public String getXML() {
        StringBuffer retval = new StringBuffer( 800 );

        retval.append( "    " + XMLHandler.addTagValue( "variableType", variableType ) );
        retval.append( "    " + XMLHandler.addTagValue( "logLevel", loggingLevel ) );
        retval.append( "    " + XMLHandler.addTagValue( "regexFilter", regexFilter ) );
        retval.append( "    " + XMLHandler.addTagValue( "onlyLogFirstRow", onlyLogFirstRow ) );

        return retval.toString();
    }

    public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
            throws KettleException {
        try {
            variableType = Const.toInt( rep.getStepAttributeString( id_step, "variableType" ), 0 );
            loggingLevel = rep.getStepAttributeString( id_step, "logLevel" );
            regexFilter = rep.getStepAttributeString( id_step, "regexFilter" );
            onlyLogFirstRow = rep.getStepAttributeBoolean( id_step, "onlyLogFirstRow" );
        } catch ( Exception e ) {
            throw new KettleException( "Unexpected error reading step information from the repository", e );
        }
    }

    public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
            throws KettleException {
        try {
            rep.saveStepAttribute( id_transformation, id_step, "variableType", variableType );
            rep.saveStepAttribute( id_transformation, id_step, "logLevel", loggingLevel );
            rep.saveStepAttribute( id_transformation, id_step, "regexFilter", regexFilter );
            rep.saveStepAttribute( id_transformation, id_step, "onlyLogFirstRow", onlyLogFirstRow );
        } catch ( Exception e ) {
            throw new KettleException( "Unable to save step information to the repository for id_step=" + id_step, e );
        }
    }

    public void setDefault() {
        variableType = 0;
        loggingLevel = null;
        regexFilter = null;
        onlyLogFirstRow = true;
    }

    public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
                       RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space,
                       Repository repository, IMetaStore metaStore ) {
        CheckResult cr;

    }

    public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
                                  TransMeta transMeta, Trans trans ) {
        return new LogParameters( stepMeta, stepDataInterface, cnr, transMeta, trans );
    }

    @Override
    public StepDataInterface getStepData() {
        return new LogParametersData();
    }


}
