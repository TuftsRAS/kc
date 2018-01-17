/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.krms;

import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.repository.function.FunctionDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import java.util.*;

/**
 * This class is for resolving terms for StoredFuncions. It extract values from prerequisites, execute Stored Function 
 * and resolves the result.
 */
public abstract class FunctionTermResolver implements TermResolver<Object> {

    private List<String> orderedInputParams;
    private String output;
    private Set<String> parameterNames;
    private FunctionDefinition functionTerm;
    public FunctionTermResolver(List<String> orderedInputParams,Set<String> parameterNames,String output){
        this.orderedInputParams = orderedInputParams;
        this.parameterNames = parameterNames;
        this.output = output;
    }
    
    public List<String> getOrderedInputParams() {
        return orderedInputParams;
    }
    @Override
    public Set<String> getPrerequisites() {
        Set<String> prereqs = new HashSet<String>();
        for (String param : orderedInputParams) {
            if(!parameterNames.contains(param)){
                prereqs.add(param);
            }
        }
        return new HashSet<String>(prereqs);
    }
    /**
     * Returns the name of the function needs to be executed as part of the Term
     * @see org.kuali.rice.krms.api.engine.TermResolver#getOutput()
     */
    @Override
    public String getOutput() {
        return this.output;
    }

    @Override
    public Set<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public int getCost() {
        return 1;
    }

    /**
     * This method executes the stored function and returns the result. Parameters to Stored Function are 
     * getting passed through resolvedPrereqs.
     * @see org.kuali.rice.krms.api.engine.TermResolver#resolve(java.util.Map, java.util.Map)
     */
    @Override
    public Object resolve(Map<String, Object> resolvedPrereqs, Map<String, String> parameters) {
        String krmsTypeId = getFunctionTerm().getTypeId();
        String serviceName = null;
        if(krmsTypeId!=null){
            KrmsTypeDefinition typeDefinition = KrmsApiServiceLocator.getKrmsTypeRepositoryService().getTypeById(krmsTypeId);
            serviceName = typeDefinition.getServiceName();
        }
        String methodName = getFunctionTerm().getName();
        Object result = executeFunction(serviceName,methodName,resolvedPrereqs,parameters);
        return result;
    }
    /**
     * 
     * This method execute the function/method and return 'true' or 'false'
     * @param resolvedPrereqs
     * @return
     */
    protected abstract Object executeFunction(String serviceName,String methodName,Map<String, Object> resolvedPrereqs,Map<String,String> resolvedParameters);
    
    protected List<Object> extractParamValues(Map<String, Object> resolvedPrereqs,Map<String,String> resolvedParameters) {
        List<String> parameters = getOrderedInputParams();
        List<Object> extractedParams = new ArrayList<Object>();
        for (String parameter : parameters) {
            Object paramValue = resolvedPrereqs.get(parameter);
            if(paramValue==null)
                paramValue = resolvedParameters.get(parameter);
            extractedParams.add(paramValue);
        }
        return extractedParams;
    }

    /**
     * Gets the functionTerm attribute. 
     * @return Returns the functionTerm.
     */
    public FunctionDefinition getFunctionTerm() {
        return functionTerm;
    }

    /**
     * Sets the functionTerm attribute value.
     * @param functionTerm The functionTerm to set.
     */
    public void setFunctionTerm(FunctionDefinition functionTerm) {
        this.functionTerm = functionTerm;
    }
}
