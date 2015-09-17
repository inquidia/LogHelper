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

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.util.Arrays;

/**
 * @author Chris
 * @since 5-may-2015
 */
public class LogParameters extends BaseStep implements StepInterface {
    private static Class<?> PKG = LogParametersMeta.class; // for i18n purposes, needed by Translator2!!

    public LogParametersMeta meta;

    public LogParametersData data;

    public LogParameters( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
                             Trans trans ) {
        super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
    }

    public void doLogParameters() throws KettleException
    {
        String[] keyArray;

        if (meta.getVariableType() == LogParametersMeta.VARIABLE_TYPE_PARAMETER) {
            keyArray = getTransMeta().listParameters();
        } else {
            keyArray = listVariables();
        }

        Arrays.sort(keyArray);

        LogLevel logLevel;

        if (meta.getLoggingLevel() == null) {
            logLevel = LogLevel.NOTHING;
        } else {
            logLevel = LogLevel.getLogLevelForCode(meta.getLoggingLevel());
        }

        if (logLevel == null) {
            throw new KettleException("Unable to set log level.");
        }

        StringBuilder message = new StringBuilder("---------------------- Write Variables to Log - " + getStepname() + "----------------------\n");


        for (int i = 0; i < keyArray.length; i++) {
            if (keyArray[i] != null && (meta.getRegexFilter() == null
                    || keyArray[i].matches(environmentSubstitute(meta.getRegexFilter())))) {
                String value = getVariable(keyArray[i]);
                message.append(keyArray[i]).append(" = ").append(value).append("\n");
            }
        }

        message.append("---------------------- Write Variables to Log - " + getStepname() + "----------------------\n");

        if( !Const.isEmpty(message.toString()) ) {
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


    }


    public synchronized boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
        meta = (LogParametersMeta) smi;
        data = (LogParametersData) sdi;

        boolean result = true;
        Object[] r = getRow(); // This also waits for a row to be finished.

        if ( r == null ) {
            // no more input to be expected...
            setOutputDone();
            return false;
        }

        if ( first ) {
            data.inputRowMeta = getInputRowMeta();
            data.outputRowMeta = getInputRowMeta().clone();
            first = false;
        }

        if( !meta.isOnlyLogFirstRow() ) {
            doLogParameters();
        }

        putRow( data.outputRowMeta, r ); // in case we want it to go further...
        if ( checkFeedback( getLinesOutput() ) ) {
            logBasic( "linenr " + getLinesOutput() );
        }

        return result;
    }

    public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
        meta = (LogParametersMeta) smi;
        data = (LogParametersData) sdi;

        if( meta.isOnlyLogFirstRow() ) {
            try {
                doLogParameters();
            } catch ( KettleException ex ) {
                logError( "Error initializing step "+ meta.getName(), ex );
                return false;
            }
        }

        if ( super.init( smi, sdi ) ) {
            return true;
        }

        return false;
    }


    public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
        meta = (LogParametersMeta) smi;
        data = (LogParametersData) sdi;

        super.dispose( smi, sdi );
    }


}
