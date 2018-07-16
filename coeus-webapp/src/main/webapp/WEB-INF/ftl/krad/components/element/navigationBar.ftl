<#--
   - Copyright © 2005-2018 Kuali, Inc.
   - All Rights Reserved
   - You may use and modify this code under the terms of the Kuali, Inc.
   - Pre-Release License Agreement. You may not distribute it.
   -
   - You should have received a copy of the Kuali, Inc. Pre-Release License
   - Agreement with this file. If not, please write to license@kuali.co.
-->

<#--
    Bootstrap Navigation Bar

 -->
<#macro uif_navigationBar element>

    <div class="${view.contentContainerClassesAsString}">
		<#if element.environmentText?? && element.environmentText != "">
			<div class="testingEnvironment">${element.environmentText}</div>
		</#if>
        <nav id="${element.id!}" ${krad.attrBuild(element)} ${element.simpleDataAttributes} role="navigation">

            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-reorder"></span>
                </button>

				<div class="logoBrand">
	            	<h1>
						<#if element.brandImageLink?? && element.brandImageLink.render>
							<@krad.template component=element.brandImageLink/>
						<#else>
			                <a class="navbar-brand" href="#">
			                    <#if element.brandImage?? && element.brandImage.render>
	                                <@krad.template component=element.brandImage/>
	                            <#else>
	                                ${element.brandText!}
	                            </#if>
			                </a>
	    		        </#if>
					</h1>
				</div>        
            </div>

            <@krad.template component=element.navigationBarGroup/>

        </nav>

    </div>

</#macro>
