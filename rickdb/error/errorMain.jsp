<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<tiles:insert definition="maindef" flush="true" >
<tiles:put name="body"   value="/error/errorBody.jsp" />
</tiles:insert>