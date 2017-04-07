<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="the_note"   class="mybeans.NotesBean" scope="page">
</jsp:useBean>
<c:set var="thenote" >${param.javaval}</c:set>
<jsp:setProperty name="the_note" property="all"
                   value="${thenote}"/>

<html>
<head>
  <title>Test NoteBean</title>
</head>
<body>
<h1><center>   Test NoteBean.java</center></h1>
<hr />
 
 
<h3>The Code:</h3>
<font size="+2" color="BLUE" >
<pre><b>
${the_note.thisversion}
</b></pre>
</font>
<h3>The Notes:</h3>
<font size="+2" color="BLUE" >
<pre><b>
${the_note.notes}
</b></pre>
</font>
</body>
</html>



