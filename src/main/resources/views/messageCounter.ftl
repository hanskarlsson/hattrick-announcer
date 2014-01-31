<#-- @ftlvariable name="messageCounterView" type="com.jayway.messagecounter.infrastructure.resources.MessageCounterView" -->
<html>
    <head>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <h1>Current number of messages: ${count?html}!</h1>
    </body>
</html>