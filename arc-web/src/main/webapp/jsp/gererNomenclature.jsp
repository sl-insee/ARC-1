<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"
%>
<%@ taglib
	prefix="s"
	uri="/struts-tags"
%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${current_locale}"/>
<!DOCTYPE html>
<html>
<s:if test="scope==null">
	<head>
<title><spring:message code="header.externalFile"/></title>
<link
	rel="stylesheet"
	href="<s:url value='/css/bootstrap.min.css'/>"
/>
<link
	rel="stylesheet"
	type="text/css"
	href="<s:url value='/css/style.css' />"
/>
<link
	href="<s:url value='/css/font-awesome.min.css'/>"
	rel="stylesheet"
/>
<script
	type="text/javascript"
	src="<s:url value='/js/jquery-2.1.3.min.js'/>"
></script>

<script	src="<s:url value='/js/lib/popper.min.js'/>" ></script>
<script	src="<s:url value='/js/lib/bootstrap.min.js'/>"></script>

<script
	type="text/javascript"
	src="<s:url value='/js/arc.js'/>"
></script>
<script
	type="text/javascript"
	src="<s:url value='/js/gererNomenclature.js'/>"
></script>
<script
	type="text/javascript"
	src="<s:url value='/js/component.js'/>"
></script>
	</head>
</s:if>
<body class='bg-light'>

<s:form
	spellcheck="false"
	namespace="/"
	method="POST"
	theme="simple"
	enctype="multipart/form-data"
>
	<%@include file="tiles/header.jsp"%>


	<div class="container-fluid">
		<div class="row">
				<!-- left column -->
				<div class="col-md-3 border-right" style="margin-top: 2.25rem;"
				>
					<div class="row">
						<div class="col-md">
							<!-- norm list -->
							<s:include value="tiles/templateVObject.jsp">
								<s:set
									var="view"
									value="%{viewListNomenclatures}"
									scope="request"
								></s:set>
								<s:param name="btnSelect">true</s:param>
								<s:param name="btnSee">true</s:param>
								<s:param name="btnSort">true</s:param>
								<s:param name="btnAdd">true</s:param>
								<s:param name="btnUpdate">true</s:param>
								<s:param name="btnDelete">true</s:param>
								<s:param name="ligneAdd">true</s:param>
								<s:param name="ligneFilter">true</s:param>
								<s:param name="checkbox">true</s:param>
								<s:param name="checkboxVisible">true</s:param>
								<s:param name="extraScopeAdd">viewListNomenclatures;viewSchemaNmcl;viewNomenclature;</s:param>
								<s:param name="extraScopeDelete">viewListNomenclatures;viewSchemaNmcl;viewNomenclature;</s:param>
								<s:param name="extraScopeUpdate">viewListNomenclatures;viewSchemaNmcl;viewNomenclature;</s:param>
								<s:param name="extraScopeSee">viewListNomenclatures;viewSchemaNmcl;viewNomenclature;</s:param>

							</s:include>
						</div>
					</div>
					<div class="input-group my-3">
						<div class="custom-file">
							<input
								name="fileUpload"
								type="file"
								class="custom-file-input"
								id="externalFileLoad"
								size="40"
							/> <label
								class="custom-file-label"
								for="externalFile"
							><spring:message code="general.chooseFile"/></label>
						</div>
						<div class="input-group-append">
							<button
								class="btn btn-primary btn-sm"
								id="btnFileUpload"
								type="submit"
								doAction="importListNomenclatures"
								scope=""
								multipart="true"
								ajax="false"
								onclick="submitForm()"
							><span class="fa fa-upload">&nbsp;</span> <spring:message code="managementSandbox.load"/></button>
						</div>
					</div>
			</div>

				<div class="col-md-2 border-right" style="margin-top: 2.25rem;"
				>
					<div class="row">
						<div class="col-md">
							<!-- norm list -->
							<s:include value="tiles/templateVObject.jsp">
								<s:set
									var="view"
									value="%{viewSchemaNmcl}"
									scope="request"
								></s:set>
								<s:param name="btnSelect">true</s:param>
								<s:param name="btnSee">true</s:param>
								<s:param name="btnSort">true</s:param>
								<s:param name="btnAdd">true</s:param>
								<s:param name="btnUpdate">true</s:param>
								<s:param name="btnDelete">true</s:param>
								<s:param name="ligneAdd">true</s:param>
								<s:param name="ligneFilter">true</s:param>
								<s:param name="checkbox">true</s:param>
								<s:param name="checkboxVisible">true</s:param>
								<s:param name="extraScopeAdd">viewSchemaNmcl;</s:param>
								<s:param name="extraScopeDelete">viewSchemaNmcl;</s:param>
								<s:param name="extraScopeUpdate">viewSchemaNmcl;</s:param>
								<s:param name="extraScopeSee">viewSchemaNmcl;</s:param>

							</s:include>
						</div>
					</div>
			</div>
			
						<div class="col-md-7 border-right" style="margin-top: 2.25rem;"
				>
					<div class="row">
						<div class="col-md">
							<!-- norm list -->
							<s:include value="tiles/templateVObject.jsp">
								<s:set
									var="view"
									value="%{viewNomenclature}"
									scope="request"
								></s:set>
								<s:param name="btnSelect">true</s:param>
								<s:param name="btnSee">true</s:param>
								<s:param name="btnSort">true</s:param>
								<s:param name="btnAdd">false</s:param>
								<s:param name="btnUpdate">false</s:param>
								<s:param name="btnDelete">false</s:param>
								<s:param name="ligneAdd">false</s:param>
								<s:param name="ligneFilter">true</s:param>
								<s:param name="checkbox">false</s:param>
								<s:param name="checkboxVisible">false</s:param>
								<s:param name="extraScopeSee">viewNomenclature;</s:param>

							</s:include>
						</div>
					</div>
			</div>
			
			
			</div>
			
	</div>	
</s:form>
	

</body>
</html>