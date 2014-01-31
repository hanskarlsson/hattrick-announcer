<#-- @ftlvariable name="messageCounterView" type="com.jayway.messagecounter.infrastructure.resources.MessageCounterView" -->
<html>
    <head>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <h1>Welcome to Message Counter service</h1>
        <h3>Available resources</h3>
        <table class="table" title="Available resources">
            <tr>
                <td><a href="/statistics">Message statistics</a></td>
            </tr>
            <tr>
                <td><a href="${healthUrl}">Health Check</a></td>
            </tr>
            <tr>
                <td><a href="${metricsUrl}">Metrics</a></td>
            </tr>
        </table>
    </body>
</html>