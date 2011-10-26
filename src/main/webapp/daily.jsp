<%@ page import="com.google.appengine.tools.pipeline.JobInfo" %>
<%--<%@ page import="com.google.appengine.tools.pipeline.demo.*" %>--%>
<%@ page import="com.google.appengine.tools.pipeline.PipelineService" %>
<%@ page import="com.google.appengine.tools.pipeline.PipelineServiceFactory" %>
<%@ page import="com.mns.mojoinvest.server.engine.model.Quote" %>
<%@ page import="com.mns.mojoinvest.server.pipeline.RealtimePipeline.DailyPipeline" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mns.mojoinvest.server.engine.model.QuoteDao" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="com.mns.mojoinvest.server.engine.model.FundDao" %>

<%!
    private static final String TEXT_PARAM_NAME = "text";
    private static final String PIPELINE_ID_PARAM_NAME = "pipelineId";
    private static final String CLEANUP_PIPELINE_ID_PARAM_NAME = "cleanupId";

%>
<HTML>
<HEAD>
    <link rel="stylesheet" type="text/css" href="someStyle.css">
    <style type="text/css">
        .period {
            font-style: italic;
            margin-bottom: 1em;
            font-size: 0.8em;
        }

        h4.withperiod {
            margin-bottom: 0em;
        }
    </style>
</HEAD>
<BODY>

<H2>Compute letter counts by spanwing a sub-job for each word</H2>

<%
    String text = request.getParameter(TEXT_PARAM_NAME);
    String pipelineId = request.getParameter(PIPELINE_ID_PARAM_NAME);
    String cleanupId = request.getParameter(CLEANUP_PIPELINE_ID_PARAM_NAME);
    PipelineService service = PipelineServiceFactory.newPipelineService();
    if (null != cleanupId) {
        service.deletePipelineRecords(cleanupId);
    }
    if (null != text) {
%>
<H4>Computing letter counts...</H4>
<em><%=text%>
</em>

<p>

        <%
  if(null == pipelineId){

    pipelineId = service.startNewPipeline(new DailyPipeline(), text);
  }
  JobInfo jobInfo = service.getJobInfo(pipelineId);
  switch(jobInfo.getJobState()){
        case COMPLETED_SUCCESSFULLY:
%>
    Pipeline completed.

<p>
        <%
    List<Quote> quotes = (List<Quote>) jobInfo.getOutput();
    for (Quote quote : quotes) {
      out.print(quote.toString());
    }

%>

<form method="post">
    <input name="<%=TEXT_PARAM_NAME%>" value="" type="hidden">
    <input name="<%=PIPELINE_ID_PARAM_NAME%>" value="" type="hidden">
    <input name="<%=CLEANUP_PIPELINE_ID_PARAM_NAME%>" value="<%=pipelineId%>" type="hidden">
    <input type="submit" value="Do it again">
</form>
<%
        break;
    case RUNNING:
%>
Pipeline not yet completed.
<p>

<form method="post">
    <input name="<%=TEXT_PARAM_NAME%>" value="<%=text%>" type="hidden">
    <input name="<%=PIPELINE_ID_PARAM_NAME%>" value="<%=pipelineId%>" type="hidden">
    <input type="submit" value="Check Again">
</form>
<%
        break;
    case STOPPED_BY_ERROR:
%>
Pipeline stopped. An error occurred.
<p>

<form method="post">
    <input name="<%=TEXT_PARAM_NAME%>" value="" type="hidden">
    <input name="<%=PIPELINE_ID_PARAM_NAME%>" value="" type="hidden">
    <input type="submit" value="Do it again">
</form>
<p>
    error info:

<p>
        <%=jobInfo.getError()%>
        <%
          break;
        case STOPPED_BY_REQUEST:
%>
    Pipeline stopped by request;

<p>

<form method="post">
    <input name="<%=TEXT_PARAM_NAME%>" value="" type="hidden">
    <input name="<%=PIPELINE_ID_PARAM_NAME%>" value="" type="hidden">
    <input type="submit" value="Do it again">
</form>
<%
            break;
    }// end switch
}// end: if
else {
%>
Enter some text:
<form method="post">
    <textarea name="<%=TEXT_PARAM_NAME%>" cols=40 rows=6></textarea>
    <br>
    <input type="submit" value="Compute Letter Count">
</form>
<%
    }

    if (null != pipelineId) {
%>
<p>
    <a href="/_ah/pipeline/status.html?root=<%=pipelineId%>" target="Pipeline Status">view status
        page</a>
        <%
}
%>


</BODY>
</HTML>